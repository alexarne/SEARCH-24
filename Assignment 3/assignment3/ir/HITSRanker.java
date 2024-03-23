/**
 *   Computes the Hubs and Authorities for an every document in a query-specific
 *   link graph, induced by the base set of pages.
 *
 *   @author Dmytro Kalpakchi
 */

package ir;

import java.util.*;
import java.io.*;


public class HITSRanker {

    /**
     *   Max number of iterations for HITS
     */
    final static int MAX_NUMBER_OF_STEPS = 1000;

    /**
     *   Convergence criterion: hub and authority scores do not 
     *   change more that EPSILON from one iteration to another.
     */
    // final static double EPSILON = 0.001;
    final static double EPSILON = 1e-17;

    /**
     *   The inverted index
     */
    Index index;

    /**
     *   Mapping from the titles to internal document ids used in the links file
     */
    HashMap<String,Integer> titleToId = new HashMap<String,Integer>();

    /**
     *   Sparse vector containing hub scores
     */
    HashMap<Integer,Double> hubs;

    /**
     *   Sparse vector containing authority scores
     */
    HashMap<Integer,Double> authorities;


    

    /**  
     *   Maximal number of documents. We're assuming here that we
     *   don't have more docs than we can keep in main memory.
     */
    final static int MAX_NUMBER_OF_DOCS = 2000000;
    /**
     *   Mapping from document names to document numbers.
     */
    HashMap<String,Integer> docNumber = new HashMap<String,Integer>();
    /**
     *   Mapping from document numbers to document names
     */
    String[] docName = new String[MAX_NUMBER_OF_DOCS];
    /**
     *   The number of outlinks from each node.
     */
    int[] out = new int[MAX_NUMBER_OF_DOCS];
    /**  
     *   A memory-efficient representation of the transition matrix.
     *   The outlinks are represented as a HashMap, whose keys are 
     *   the numbers of the documents linked from.<p>
     *
     *   The value corresponding to key i is a HashMap whose keys are 
     *   all the numbers of documents j that i links to.<p>
     *
     *   If there are no outlinks from i, then the value corresponding 
     *   key i is null.
     */
    HashMap<Integer,HashMap<Integer,Boolean>> link = new HashMap<Integer,HashMap<Integer,Boolean>>();
    HashMap<Integer,HashMap<Integer,Boolean>> linkReverse = new HashMap<Integer,HashMap<Integer,Boolean>>();
    HashMap<Integer, String> idToTitle = new HashMap<>();


    
    /* --------------------------------------------- */

    /**
     * Constructs the HITSRanker object
     * 
     * A set of linked documents can be presented as a graph.
     * Each page is a node in graph with a distinct nodeID associated with it.
     * There is an edge between two nodes if there is a link between two pages.
     * 
     * Each line in the links file has the following format:
     *  nodeID;outNodeID1,outNodeID2,...,outNodeIDK
     * This means that there are edges between nodeID and outNodeIDi, where i is between 1 and K.
     * 
     * Each line in the titles file has the following format:
     *  nodeID;pageTitle
     *  
     * NOTE: nodeIDs are consistent between these two files, but they are NOT the same
     *       as docIDs used by search engine's Indexer
     *
     * @param      linksFilename   File containing the links of the graph
     * @param      titlesFilename  File containing the mapping between nodeIDs and pages titles
     * @param      index           The inverted index
     */
    public HITSRanker( String linksFilename, String titlesFilename, Index index ) {
        this.index = index;
        readDocs( linksFilename, titlesFilename );
    }


    /* --------------------------------------------- */

    /**
     * A utility function that gets a file name given its path.
     * For example, given the path "davisWiki/hello.f",
     * the function will return "hello.f".
     *
     * @param      path  The file path
     *
     * @return     The file name.
     */
    private String getFileName( String path ) {
        String result = "";
        StringTokenizer tok = new StringTokenizer( path, "\\/" );
        while ( tok.hasMoreTokens() ) {
            result = tok.nextToken();
        }
        return result;
    }


    /**
     * Reads the files describing the graph of the given set of pages.
     *
     * @param      linksFilename   File containing the links of the graph
     * @param      titlesFilename  File containing the mapping between nodeIDs and pages titles
     */
    int readDocs( String linksFilename, String titlesFilename ) {
        //
        // YOUR CODE HERE
        //
        
		try {
			System.err.print( "Reading file... " );
            BufferedReader in = new BufferedReader( new FileReader( titlesFilename ));
            String line;
			while ((line = in.readLine()) != null) {
                String[] arr = line.split( ";" );
                titleToId.put(arr[1], Integer.valueOf(arr[0]));
                idToTitle.put(Integer.valueOf(arr[0]), arr[1]);
			}
            in.close();
            in = new BufferedReader( new FileReader( linksFilename ));
			while ((line = in.readLine()) != null) {
				int index = line.indexOf( ";" );
				String title = line.substring( 0, index );
				Integer fromdoc = Integer.valueOf(title);
                
				// Check all outlinks.
				StringTokenizer tok = new StringTokenizer( line.substring(index+1), "," );
				while ( tok.hasMoreTokens() ) {
					String otherTitle = tok.nextToken();
					Integer otherDoc = Integer.valueOf(otherTitle);
                    
					// Set the probability to 0 for now, to indicate that there is
					// a link from fromdoc to otherDoc.
					if ( link.get(fromdoc) == null ) {
						link.put(fromdoc, new HashMap<Integer,Boolean>());
					}
					if ( link.get(fromdoc).get(otherDoc) == null ) {
						link.get(fromdoc).put( otherDoc, true );
						out[fromdoc]++;
					}
                    if (linkReverse.get(otherDoc) == null) {
                        linkReverse.put(otherDoc, new HashMap<>());
                    }
                    if (linkReverse.get(otherDoc).get(fromdoc) == null) {
                        linkReverse.get(otherDoc).put(fromdoc, true);
                    }
				}
			}
            in.close();

            System.err.println( "done. " );
		} catch ( FileNotFoundException e ) {
			System.err.println( "File " + linksFilename + " not found!" );
		} catch ( IOException e ) {
			System.err.println( "Error reading file " + linksFilename );
		}
		return 0;
    }

    /**
     * Perform HITS iterations until convergence
     *
     * @param      titles  The titles of the documents in the root set
     */
    private void iterate(String[] titles) {
        //
        // YOUR CODE HERE
        //
        hubs = new HashMap<>();
        authorities = new HashMap<>();

        Integer[] baseSet = getBaseSet(titles);
        System.out.println("Root size: " + titles.length + ", Base size: " + baseSet.length);
        for (int i = 0; i < baseSet.length; ++i) {
            hubs.put(baseSet[i], 1d);
            authorities.put(baseSet[i], 1d);
        }
        HashMap<Integer, Double> hubsOld, authoritiesOld;
        while (true) {
            hubsOld = new HashMap<>(hubs);
            authoritiesOld = new HashMap<>(authorities);
            
            for (Integer doc : baseSet) {
                Double value = 0d;
                if (linkReverse.get(doc) != null) {
                    for (Integer otherdoc : linkReverse.get(doc).keySet()) {
                        if (hubsOld.get(otherdoc) != null) 
                            value += hubsOld.get(otherdoc);
                    }
                }
                authorities.put(doc, value);
            }
            
            for (Integer doc : baseSet) {
                Double value = 0d;
                if (link.get(doc) != null) {
                    for (Integer otherdoc : link.get(doc).keySet()) {
                        if (authoritiesOld.get(otherdoc) != null) 
                            value += authoritiesOld.get(otherdoc);
                    }
                }
                hubs.put(doc, value);
                // System.out.println("hub of " + doc + " set to " + value);
            }
            
            Double norm2a = 0d, norm2h = 0d;
            for (Integer doc : baseSet) {
                norm2h += hubs.get(doc)*hubs.get(doc);
                norm2a += authorities.get(doc)*authorities.get(doc);
            }
            Double norma = Math.sqrt(norm2a);
            Double normh = Math.sqrt(norm2h);
            for (Integer doc : baseSet) {
                hubs.put(doc, hubs.get(doc) / normh);
                authorities.put(doc, authorities.get(doc) / norma);
            }
            
            Double norm2ad = 0d, norm2hd = 0d;
            for (Integer doc : baseSet) {
                Double delta = hubs.get(doc) - hubsOld.get(doc);
                norm2hd += delta*delta;
                delta = authorities.get(doc) - authoritiesOld.get(doc);
                norm2ad += delta*delta;
            }
            // System.out.println("norm2ad: " + norm2ad + " norm2hd: " + norm2hd);
            if (norm2ad < EPSILON && norm2hd < EPSILON) break;
        }
    }

    private Integer[] getBaseSet(String[] titles) {
        Set<Integer> set = new HashSet<>();
        for (String title : titles) {
            String filename = getFileName(title);
            Integer id = titleToId.get(filename);
            if (!set.contains(id)) set.add(id);
            if (link.get(id) != null) {
                for (Integer node : link.get(id).keySet()) {
                    if (!set.contains(node)) set.add(node);
                }
            }
            if (linkReverse.get(id) != null) {
                for (Integer node : linkReverse.get(id).keySet()) {
                    if (!set.contains(node)) set.add(node);
                }
            }
        }
        return set.toArray(new Integer[0]);
    }


    /**
     * Rank the documents in the subgraph induced by the documents present
     * in the postings list `post`.
     *
     * @param      post  The list of postings fulfilling a certain information need
     *
     * @return     A list of postings ranked according to the hub and authority scores.
     */
    PostingsList rank(PostingsList post) {
        //
        // YOUR CODE HERE
        //

        Set<String> set = new HashSet<>();
        for (int i = 0; i < post.size(); ++i) {
            String filename = getFileName(index.docNames.get(post.get(i).docID));
            if (!set.contains(filename)) set.add(filename);
        }
        iterate(set.toArray(new String[0]));

        PostingsList result = new PostingsList();
        HashMap<Integer, Double> combined = new HashMap<>();
        String filename = index.docNames.get(post.get(0).docID);
        String prefix = filename.substring(0, filename.lastIndexOf("\\")+1);
        for (Integer localID : hubs.keySet()) {
            Integer docID = index.docIDs.get(prefix + idToTitle.get(localID));
            Double value = hubs.get(localID) + authorities.get(localID);
            combined.put(docID, value);
        }
        HashMap<Integer, Double> sortedCombined = sortHashMapByValue(combined);
        for (Map.Entry<Integer,Double> e : sortedCombined.entrySet()) {
            PostingsEntry entry = new PostingsEntry(e.getKey());
            entry.score = e.getValue();
            result.insert(entry);
        }
        return result;
    }


    /**
     * Sort a hash map by values in the descending order
     *
     * @param      map    A hash map to sorted
     *
     * @return     A hash map sorted by values
     */
    private HashMap<Integer,Double> sortHashMapByValue(HashMap<Integer,Double> map) {
        if (map == null) {
            return null;
        } else {
            List<Map.Entry<Integer,Double> > list = new ArrayList<Map.Entry<Integer,Double> >(map.entrySet());
      
            Collections.sort(list, new Comparator<Map.Entry<Integer,Double>>() {
                public int compare(Map.Entry<Integer,Double> o1, Map.Entry<Integer,Double> o2) { 
                    return (o2.getValue()).compareTo(o1.getValue()); 
                } 
            }); 
              
            HashMap<Integer,Double> res = new LinkedHashMap<Integer,Double>(); 
            for (Map.Entry<Integer,Double> el : list) { 
                res.put(el.getKey(), el.getValue()); 
            }
            return res;
        }
    } 


    /**
     * Write the first `k` entries of a hash map `map` to the file `fname`.
     *
     * @param      map        A hash map
     * @param      fname      The filename
     * @param      k          A number of entries to write
     */
    void writeToFile(HashMap<Integer,Double> map, String fname, int k) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fname));
            
            if (map != null) {
                int i = 0;
                for (Map.Entry<Integer,Double> e : map.entrySet()) {
                    i++;
                    writer.write(e.getKey() + ": " + String.format("%.5g%n", e.getValue()));
                    if (i >= k) break;
                }
            }
            writer.close();
        } catch (IOException e) {}
    }


    /**
     * Rank all the documents in the links file. Produces two files:
     *  hubs_top_30.txt with documents containing top 30 hub scores
     *  authorities_top_30.txt with documents containing top 30 authority scores
     */
    void rank() {
        iterate(titleToId.keySet().toArray(new String[0]));
        HashMap<Integer,Double> sortedHubs = sortHashMapByValue(hubs);
        HashMap<Integer,Double> sortedAuthorities = sortHashMapByValue(authorities);
        writeToFile(sortedHubs, "hubs_top_30.txt", 30);
        writeToFile(sortedAuthorities, "authorities_top_30.txt", 30);
    }


    /* --------------------------------------------- */


    public static void main( String[] args ) {
        if ( args.length != 2 ) {
            System.err.println( "Please give the names of the link and title files" );
        }
        else {
            HITSRanker hr = new HITSRanker( args[0], args[1], null );
            hr.rank();
        }
    }
} 