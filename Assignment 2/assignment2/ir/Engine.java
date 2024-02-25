/*  
 *   This file is part of the computer assignment for the
 *   Information Retrieval course at KTH.
 * 
 *   Johan Boye, 2017
 */

package ir;

import java.util.ArrayList;
import java.util.HashMap;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 *  This is the main class for the search engine.
 */
public class Engine {

    /** The inverted index. */
    // Index index = new HashedIndex();
    // Assignment 1.7: Comment the line above and uncomment the next line
    Index index = new PersistentHashedIndex();
    // Assignment 1.8
    // Index index = new PersistentScalableHashedIndex();

    /** The indexer creating the search index. */
    Indexer indexer;

    /** The searcher used to search the index. */
    Searcher searcher;

    /** K-gram index */
    KGramIndex kgIndex = null;
    // Assignment 3: Comment the line above and uncomment the next line
    // KgramIndex kgIndex = new KGramIndex(2);

    /** Spell checker */
    SpellChecker speller;
    // Assignment 3: Comment the line above and uncomment the next line
    // SpellChecker = new SpellChecker( index, kgIndex );
    
    /** The engine GUI. */
    SearchGUI gui;

    /** Directories that should be indexed. */
    ArrayList<String> dirNames = new ArrayList<String>();

    /** Lock to prevent simultaneous access to the index. */
    Object indexLock = new Object();

    /** The patterns matching non-standard words (e-mail addresses, etc.) */
    String patterns_file = null;

    /** The file containing the logo. */
    String pic_file = "";

    /** The file containing the pageranks. */
    String rank_file = "";

    /** For persistent indexes, we might not need to do any indexing. */
    boolean is_indexing = true;


    /* ----------------------------------------------- */


    /**  
     *   Constructor. 
     *   Indexes all chosen directories and files
     */
    public Engine( String[] args ) {
        decodeArgs( args );
        indexer = new Indexer( index, kgIndex, patterns_file );
        searcher = new Searcher( index, kgIndex );
        gui = new SearchGUI( this );
        gui.init();
        boolean calculate_euclideans = !(new File("./index/euclideans.txt").exists());
        if (!calculate_euclideans) loadEuclideanLengths();
        /* 
         *   Calls the indexer to index the chosen directory structure.
         *   Access to the index is synchronized since we don't want to 
         *   search at the same time we're indexing new files (this might 
         *   corrupt the index).
         */
        if (is_indexing) {
            synchronized ( indexLock ) {
                gui.displayInfoText( "Indexing, please wait..." );
                long startTime = System.currentTimeMillis();
                for ( int i=0; i<dirNames.size(); i++ ) {
                    File dokDir = new File( dirNames.get( i ));
                    indexer.processFiles( dokDir, is_indexing, calculate_euclideans );
                }
                loadPageRank();
                long elapsedTime = System.currentTimeMillis() - startTime;
                index.cleanup();
                if (calculate_euclideans) {
                    calculateEuclideanLengths();
                    writeEuclideanLengths();
                }
                gui.displayInfoText( String.format( "Indexing done in %.1f seconds.", elapsedTime/1000.0 ));
            }
        } else {
            loadPageRank();
            gui.displayInfoText( "Index is loaded from disk" );
        }
    }

    public void loadPageRank() {
        HashMap<String, String> nameToPageRank = new HashMap<>();
        try {
            BufferedReader in = new BufferedReader( new FileReader("./index/pagerank.txt"));
            String line;
            while ((line = in.readLine()) != null) {
                String[] arr = line.split(" ");
                nameToPageRank.put(arr[0], arr[1]);
            }
            in.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        for (Integer id : index.docNames.keySet()) {
            String[] arr = index.docNames.get(id).split("\\\\");
            String filename = arr[arr.length-1];
            index.docPageRank.put(id, Double.parseDouble(nameToPageRank.get(filename)));
        }
    }

    public void loadEuclideanLengths() {
        try {
            BufferedReader in = new BufferedReader( new FileReader("./index/euclideans.txt"));
            String line;
            while ((line = in.readLine()) != null) {
                String[] arr = line.split(" ");
                Integer doc = Integer.valueOf(arr[0]);
                Double length = Double.parseDouble(arr[1]);
                index.docLengthsEuclidean.put(doc, length);
            }
            in.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void calculateEuclideanLengths() {
        double N = index.docNames.size();
        for (int i = 0; i < index.tf_vector.size(); ++i) {
            if (i % 1000 == 0) System.out.println(i);
            double norm2 = 0;
            for (String term : index.tf_vector.get(i).keySet()) {
                int tf = index.tf_vector.get(i).get(term);
                double df = index.df_map.get(term);
                double idf_t = Math.log(N / df);
                norm2 += tf*idf_t * tf*idf_t;
            }
            index.docLengthsEuclidean.put(i, Math.sqrt(norm2));
        }
    }

    public void writeEuclideanLengths() {
        try {
            FileWriter fw = new FileWriter("./index/euclideans.txt", true);
            BufferedWriter bw = new BufferedWriter(fw);
            for (int docID : index.docLengthsEuclidean.keySet()) {
                bw.write(docID + " " + index.docLengthsEuclidean.get(docID));
                bw.newLine();
            }
            bw.close();
            fw.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    /* ----------------------------------------------- */

    /**
     *   Decodes the command line arguments.
     */
    private void decodeArgs( String[] args ) {
        int i=0, j=0;
        while ( i < args.length ) {
            if ( "-d".equals( args[i] )) {
                i++;
                if ( i < args.length ) {
                    dirNames.add( args[i++] );
                }
            } else if ( "-p".equals( args[i] )) {
                i++;
                if ( i < args.length ) {
                    patterns_file = args[i++];
                }
            } else if ( "-l".equals( args[i] )) {
                i++;
                if ( i < args.length ) {
                    pic_file = args[i++];
                }
            } else if ( "-r".equals( args[i] )) {
                i++;
                if ( i < args.length ) {
                    rank_file = args[i++];
                }
            } else if ( "-ni".equals( args[i] )) {
                i++;
                is_indexing = false;
            } else {
                System.err.println( "Unknown option: " + args[i] );
                break;
            }
        }                   
    }


    /* ----------------------------------------------- */


    public static void main( String[] args ) {
        Engine e = new Engine( args );
    }

}

