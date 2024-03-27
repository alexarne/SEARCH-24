/*
 *   This file is part of the computer assignment for the
 *   Information Retrieval course at KTH.
 *
 *   Dmytro Kalpakchi, 2018
 */

package ir;

import java.io.*;
import java.util.*;
import java.nio.charset.StandardCharsets;


public class KGramIndex {

    /** Mapping from term ids to actual term strings */
    HashMap<Integer,String> id2term = new HashMap<Integer,String>();

    /** Mapping from term strings to term ids */
    HashMap<String,Integer> term2id = new HashMap<String,Integer>();

    /** Index from k-grams to list of term ids that contain the k-gram */
    HashMap<String,List<KGramPostingsEntry>> index = new HashMap<String,List<KGramPostingsEntry>>();
    // HashMap<String, ArrayList<String>> kgramMap = new HashMap<>();

    /** The ID of the last processed term */
    int lastTermID = -1;

    /** Number of symbols to form a K-gram */
    int K = 3;

    public KGramIndex(int k) {
        K = k;
        if (k <= 0) {
            System.err.println("The K-gram index can't be constructed for a negative K value");
            System.exit(1);
        }
    }

    /** Generate the ID for an unknown term */
    private int generateTermID() {
        return ++lastTermID;
    }

    public int getK() {
        return K;
    }


    /**
     *  Get intersection of two postings lists
     */
    private List<KGramPostingsEntry> intersect(List<KGramPostingsEntry> p1, List<KGramPostingsEntry> p2) {
        // 
        // YOUR CODE HERE
        //
        List<KGramPostingsEntry> result = new ArrayList<>();
        int i1 = 0, i2 = 0;
        while (i1 < p1.size() && i2 < p2.size()) {
            if (p1.get(i1).tokenID == p2.get(i2).tokenID) {
                result.add(new KGramPostingsEntry(p1.get(i1)));
                ++i1;
                ++i2;
            } else if (p1.get(i1).tokenID < p2.get(i2).tokenID) {
                ++i1;
            } else if (p1.get(i1).tokenID > p2.get(i2).tokenID) {
                ++i2;
            }
        }
        return result;
    }


    /** Inserts all k-grams from a token into the index. */
    public void insert( String token ) {
        //
        // YOUR CODE HERE
        //
        if (term2id.get(token) != null) return;
        int termID = generateTermID();
        term2id.put(token, termID);
        id2term.put(termID, token);

        token = "^" + token + "$";
        // kgramMap.put(token, new ArrayList<>());
        for (int i = 0; i < token.length() - K + 1; ++i) {
            String kgram = token.substring(i, i+K);
            // kgramMap.get(token).add(kgram);
            if (index.get(kgram) == null) index.put(kgram, new ArrayList<>());
            if (index.get(kgram).size() != 0 && index.get(kgram).get(index.get(kgram).size()-1).tokenID == lastTermID)
                continue;
            index.get(kgram).add(new KGramPostingsEntry(termID));
        }
    }

    /** Get postings for the given k-gram */
    public List<KGramPostingsEntry> getPostings(String kgram) {
        //
        // YOUR CODE HERE
        //
        List<KGramPostingsEntry> result = index.get(kgram);
        if (result == null) result = new ArrayList<>();
        return result;
    }

    public ArrayList<String> getWords(String[] kgrams) {
        List<KGramPostingsEntry> postings = null;
        for (String kgram : kgrams) {
            if (kgram.length() != K) {
                System.err.println("Cannot search k-gram index: " + kgram.length() + "-gram provided instead of " + K + "-gram");
            }

            if (postings == null) {
                postings = getPostings(kgram);
            } else {
                postings = intersect(postings, getPostings(kgram));
            }
        }
        ArrayList<String> words = new ArrayList<>();
        for (KGramPostingsEntry entry : postings) {
            words.add(id2term.get(entry.tokenID));
        }
        return words;
    }

    public ArrayList<String> getWildcardWords(String wildcard) {
        int asterisk = wildcard.indexOf("*");
        String prefix = "^" + wildcard.substring(0, asterisk);
        String suffix = wildcard.substring(asterisk+1) + "$";
        ArrayList<String> kgrams = new ArrayList<>();
        for (int i = 0; i < prefix.length() - K + 1; ++i) {
            String kgram = prefix.substring(i, i+K);
            kgrams.add(kgram);
        }
        for (int i = 0; i < suffix.length() - K + 1; ++i) {
            String kgram = suffix.substring(i, i+K);
            kgrams.add(kgram);
        }
        ArrayList<String> words = getWords(kgrams.toArray(new String[0]));
        ArrayList<String> cleaned = new ArrayList<>();
        for (String word : words) {
            if (!word.startsWith(prefix.substring(1))) continue;
            if (!word.endsWith(suffix.substring(0, suffix.length()-1))) continue;
            cleaned.add(word);
        }
        // System.out.println("Wildcard " + wildcard + " gave words:");
        // for (String word : cleaned) {
        //     System.out.println(word);
        // }
        return cleaned;
    }

    public ArrayList<String> getKgrams(String term) {
        // if (kgramMap.get(term) != null) return kgramMap.get(term);
        ArrayList<String> kgrams = new ArrayList<>();
        for (int i = 0; i < term.length() - K + 1; ++i) {
            String kgram = term.substring(i, i+K);
            kgrams.add(kgram);
        }
        return kgrams;
    }

    /** Get id of a term */
    public Integer getIDByTerm(String term) {
        return term2id.get(term);
    }

    /** Get a term by the given id */
    public String getTermByID(Integer id) {
        return id2term.get(id);
    }

    private static HashMap<String,String> decodeArgs( String[] args ) {
        HashMap<String,String> decodedArgs = new HashMap<String,String>();
        int i=0, j=0;
        while ( i < args.length ) {
            if ( "-p".equals( args[i] )) {
                i++;
                if ( i < args.length ) {
                    decodedArgs.put("patterns_file", args[i++]);
                }
            } else if ( "-f".equals( args[i] )) {
                i++;
                if ( i < args.length ) {
                    decodedArgs.put("file", args[i++]);
                }
            } else if ( "-k".equals( args[i] )) {
                i++;
                if ( i < args.length ) {
                    decodedArgs.put("k", args[i++]);
                }
            } else if ( "-kg".equals( args[i] )) {
                i++;
                if ( i < args.length ) {
                    decodedArgs.put("kgram", args[i++]);
                }
            } else {
                System.err.println( "Unknown option: " + args[i] );
                break;
            }
        }
        return decodedArgs;
    }

    public static void main(String[] arguments) throws FileNotFoundException, IOException {
        HashMap<String,String> args = decodeArgs(arguments);

        int k = Integer.parseInt(args.getOrDefault("k", "3"));
        KGramIndex kgIndex = new KGramIndex(k);

        File f = new File(args.get("file"));
        Reader reader = new InputStreamReader( new FileInputStream(f), StandardCharsets.UTF_8 );
        Tokenizer tok = new Tokenizer( reader, true, false, true, args.get("patterns_file") );
        while ( tok.hasMoreTokens() ) {
            String token = tok.nextToken();
            kgIndex.insert(token);
        }

        String[] kgrams = args.get("kgram").split(" ");
        List<KGramPostingsEntry> postings = null;
        for (String kgram : kgrams) {
            if (kgram.length() != k) {
                System.err.println("Cannot search k-gram index: " + kgram.length() + "-gram provided instead of " + k + "-gram");
                System.exit(1);
            }

            if (postings == null) {
                postings = kgIndex.getPostings(kgram);
            } else {
                postings = kgIndex.intersect(postings, kgIndex.getPostings(kgram));
            }
        }
        if (postings == null) {
            System.err.println("Found 0 posting(s)");
        } else {
            int resNum = postings.size();
            System.err.println("Found " + resNum + " posting(s)");
            // if (resNum > 10) {
            //     System.err.println("The first 10 of them are:");
            //     resNum = 10;
            // }
            for (int i = 0; i < resNum; i++) {
                System.err.println(kgIndex.getTermByID(postings.get(i).tokenID));
            }
        }
    }
}