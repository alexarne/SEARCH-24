/*  
 *   This file is part of the computer assignment for the
 *   Information Retrieval course at KTH.
 * 
 *   Johan Boye, 2017
 */  

package ir;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Iterator;
import java.nio.charset.*;
import java.io.*;


/**
 *  A class for representing a query as a list of words, each of which has
 *  an associated weight.
 */
public class Query {

    /**
     *  Help class to represent one query term, with its associated weight. 
     */
    class QueryTerm {
        String term;
        double weight;
        QueryTerm( String t, double w ) {
            term = t;
            weight = w;
        }
    }

    /** 
     *  Representation of the query as a list of terms with associated weights.
     *  In assignments 1 and 2, the weight of each term will always be 1.
     */
    public ArrayList<QueryTerm> queryterm = new ArrayList<QueryTerm>();

    /**  
     *  Relevance feedback constant alpha (= weight of original query terms). 
     *  Should be between 0 and 1.
     *  (only used in assignment 3).
     */
    double alpha = 0.2;

    /**  
     *  Relevance feedback constant beta (= weight of query terms obtained by
     *  feedback from the user). 
     *  (only used in assignment 3).
     */
    double beta = 1 - alpha;
    
    
    /**
     *  Creates a new empty Query 
     */
    public Query() {
    }
    
    
    /**
     *  Creates a new Query from a string of words
     */
    public Query( String queryString  ) {
        StringTokenizer tok = new StringTokenizer( queryString );
        while ( tok.hasMoreTokens() ) {
            queryterm.add( new QueryTerm(tok.nextToken(), 1.0) );
        }    
    }
    
    
    /**
     *  Returns the number of terms
     */
    public int size() {
        return queryterm.size();
    }
    
    
    /**
     *  Returns the Manhattan query length
     */
    public double length() {
        double len = 0;
        for ( QueryTerm t : queryterm ) {
            len += t.weight; 
        }
        return len;
    }
    
    
    /**
     *  Returns a copy of the Query
     */
    public Query copy() {
        Query queryCopy = new Query();
        for ( QueryTerm t : queryterm ) {
            queryCopy.queryterm.add( new QueryTerm(t.term, t.weight) );
        }
        return queryCopy;
    }
    
    
    /**
     *  Expands the Query using Relevance Feedback
     *
     *  @param results The results of the previous query.
     *  @param docIsRelevant A boolean array representing which query results the user deemed relevant.
     *  @param engine The search engine object
     */
    public void relevanceFeedback( PostingsList results, boolean[] docIsRelevant, Engine engine ) {
        //
        //  YOUR CODE HERE
        //
        System.out.println("Relevance Feedback...");
        // System.out.println("Current query:");
        // for (QueryTerm qt : queryterm) {
        //     System.out.println(qt.term + " - " + qt.weight);
        // }
        double gamma = 0;

        for (QueryTerm qt : queryterm) {
            qt.weight = qt.weight * alpha;
        }
        
        int numDr = 0;
        int numDocs = engine.index.docNames.size();
        HashMap<String, Double> Dr_centroid = new HashMap<>();
        for (int i = 0; i < docIsRelevant.length; ++i) {
            if (docIsRelevant[i]) {
                ++numDr;
                HashMap<String, Integer> tf = getTfVector(engine.index.docNames.get(results.get(i).docID));
                Integer len = engine.index.docLengths.get(results.get(i).docID);
                for (String term : tf.keySet()) {
                    Double df = (double) engine.index.getPostings(term).size();
                    Double idf = Math.log(numDocs / df);
                    Double tf_idf = tf.get(term) * idf / len;
                    if (Dr_centroid.get(term) == null) Dr_centroid.put(term, 0d);
                    Dr_centroid.merge(term, tf_idf, Double::sum);
                    // System.out.println("term " + term + " freq " + tf.get(term) + " idf " + idf + " = " + tf_idf);
                }
            }
        }

        for (QueryTerm qt : queryterm) {
            if (Dr_centroid.get(qt.term) == null) continue;
            qt.weight += beta * Dr_centroid.get(qt.term) / numDr;
            Dr_centroid.remove(qt.term);
        }
        for (String term : Dr_centroid.keySet()) {
            queryterm.add(new QueryTerm(term, beta * Dr_centroid.get(term) / numDr));
        }

        // System.out.println("After query:");
        // for (QueryTerm qt : queryterm) {
        //     System.out.println(qt.term + " - " + qt.weight);
        // }
    }

    private HashMap<String, Integer> getTfVector(String docname) {
        HashMap<String, Integer> tf = new HashMap<>();
        File f = new File(docname);
        String patterns_file = "patterns.txt";
        try {
            Reader reader = new InputStreamReader( new FileInputStream(f), StandardCharsets.UTF_8 );
            Tokenizer tok = new Tokenizer( reader, true, false, true, patterns_file );
            while ( tok.hasMoreTokens() ) {
                String token = tok.nextToken();
                if (tf.get(token) == null) tf.put(token, 0);
                tf.merge(token, 1, Integer::sum);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // System.out.println("Returning tf vector:");
        // for (String key : tf.keySet()) {
        //     System.out.println(key + " " + tf.get(key));
        // }
        return tf;
    }
}


