/*  
 *   This file is part of the computer assignment for the
 *   Information Retrieval course at KTH.
 * 
 *   Johan Boye, 2017
 */  

package ir;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 *  Defines some common data structures and methods that all types of
 *  index should implement.
 */
public interface Index {

    /** Mapping from document identifiers to document names. */
    public HashMap<Integer,String> docNames = new HashMap<Integer,String>();
    /** Mapping from document names to document identifiers. */
    public HashMap<String,Integer> docIDs = new HashMap<>();
    
    /** Mapping from document identifier to document length. */
    public HashMap<Integer,Integer> docLengths = new HashMap<Integer,Integer>();

    /** Inserts a token into the index. */
    public void insert( String token, int docID, int offset );

    /** Returns the postings for a given term. */
    public PostingsList getPostings( String token );

    /** This method is called on exit. */
    public void cleanup();



    /** Mapping from document identifier to its pagerank value */
    public HashMap<Integer, Double> docPageRank = new HashMap<>();
    
    /** Mapping from document identifier to document (Euclidean) length */
    public HashMap<Integer, Double> docLengthsEuclidean = new HashMap<Integer, Double>();

    /** Intermediary helper for Euclidean lengths */
    public ArrayList<HashMap<String, Integer>> tf_vector = new ArrayList<>();
    public HashMap<String, Integer> df_map = new HashMap<>();
}

