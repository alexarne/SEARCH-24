/*  
 *   This file is part of the computer assignment for the
 *   Information Retrieval course at KTH.
 * 
 *   Johan Boye, 2017
 */  

package ir;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import ir.Query.QueryTerm;

/**
 *  Searches an index for results of a query.
 */
public class Searcher {

    /** The index to be searched by this Searcher. */
    Index index;

    /** The k-gram index to be searched by this Searcher */
    KGramIndex kgIndex;
    
    /** Constructor */
    public Searcher( Index index, KGramIndex kgIndex ) {
        this.index = index;
        this.kgIndex = kgIndex;
    }

    /**
     *  Searches the index for postings matching the query.
     *  @return A postings list representing the result of the query.
     */
    public PostingsList search( Query query, QueryType queryType, RankingType rankingType, NormalizationType normType ) { 
        //
        //  REPLACE THE STATEMENT BELOW WITH YOUR CODE
        //
        PostingsList result = new PostingsList();
        
        // Fetch all postings lists
        PostingsList[] lists = new PostingsList[query.size()];
        for (int i = 0; i < query.size(); ++i) {
            lists[i] = index.getPostings(query.queryterm.get(i).term);
            // Replace null with empty PostingsList for simplicity
            if (lists[i] == null) lists[i] = new PostingsList();
        }

        // If query is empty, return empty
        if (query.size() == 0) return result;
        
        switch (queryType) {
            case INTERSECTION_QUERY:
                System.out.println("Intersection query");
                // // Sort in order of increasing length (Seems to be marginally slower, though)
                // Arrays.sort(lists, (PostingsList a, PostingsList b) -> a.size() - b.size());
                
                // Intersect
                result = lists[0];
                for (int i = 1; i < query.size(); ++i) {
                    result = intersect(result, lists[i]);
                }

                break;
            case PHRASE_QUERY:
                System.out.println("Phrase query");

                result = lists[0];
                for (int i = 1; i < query.size(); ++i) {
                    result = positionalIntersect(result, lists[i], i);
                }

                break;
            case RANKED_QUERY:
                System.out.println("Ranked query");
                
                result = lists[0];
                for (int i = 1; i < query.size(); ++i) {
                    result = union(result, lists[i]);
                }
                switch (rankingType) {
                    case RankingType.TF_IDF:
                        result = cosineRank(result, query, 0);
                        break;
                    case RankingType.PAGERANK:
                        result = cosineRank(result, query, 1);
                        break;
                    case RankingType.COMBINATION:
                        double pageRankWeight = 0.95;
                        result = cosineRank(result, query, pageRankWeight);
                        break;
                    default:
                        break;
                }

                break;
        
            default:
                break;
        }
        
        return result;
    }

    public PostingsList union(PostingsList p1, PostingsList p2) {
        for (int i = 0; i < p2.size(); ++i) {
            p1.insert(p2.get(i));
        }
        return p1;
    }

    public PostingsList cosineRank(PostingsList p, Query query, double pageRankWeight) {
        PostingsList result = new PostingsList();
        double N = index.docNames.size();
        for (QueryTerm t : query.queryterm) {
            PostingsList pl = index.getPostings(t.term);
            // Assumes unique terms in query
            double w_tq = 1;
            double idf_t = Math.log(N / pl.size());
            for (int i = 0; i < pl.size(); ++i) {
                int d = pl.get(i).docID;
                // int len_d = index.docLengths.get(d);
                double len_d = index.docLengthsEuclidean.get(d);
                double tf_dt = pl.get(i).getOccurrences().size();
                double w_td = tf_dt * idf_t;
                if (result.getByDocID(d) == null) result.insert(new PostingsEntry(d));
                result.getByDocID(d).score += w_td * w_tq / len_d;
            }
        }
        for (int i = 0; i < result.size(); ++i) {
            double tf_idf = result.get(i).score;
            double pagerank = index.docPageRank.get(result.get(i).docID);
            result.get(i).score = (1-pageRankWeight) * tf_idf + pageRankWeight * pagerank;
        }
        result.sort();
        return result;
    }

    public PostingsList intersect(PostingsList p1, PostingsList p2) {
        PostingsList result = new PostingsList();
        int i1 = 0, i2 = 0;
        while (i1 < p1.size() && i2 < p2.size()) {
            if (p1.get(i1).docID == p2.get(i2).docID) {
                result.insert(new PostingsEntry(p1.get(i1).docID));
                ++i1;
                ++i2;
            } else {
                if (p1.get(i1).docID < p2.get(i2).docID) {
                    ++i1;
                } else {
                    ++i2;
                }
            }
        }
        return result;
    }

    public PostingsList positionalIntersect(PostingsList p1, PostingsList p2, int k) {
        PostingsList result = new PostingsList();
        int i1 = 0, i2 = 0;
        while (i1 < p1.size() && i2 < p2.size()) {
            if (p1.get(i1).docID == p2.get(i2).docID) {
                // Exist in same doc, check for positional relation
                ArrayList<Integer> positions1 = p1.get(i1).getOccurrences();
                ArrayList<Integer> positions2 = p2.get(i2).getOccurrences();
                ArrayList<Integer> aligned_positions = new ArrayList<>();
                for (Integer pos1 : positions1) {
                    for (Integer pos2 : positions2) {
                        if (pos2 == pos1 + k) aligned_positions.add(pos1);
                        if (pos2 >= pos1 + k) break;
                    }
                }
                if (aligned_positions.size() > 0) {
                    PostingsEntry entry = new PostingsEntry(p1.get(i1).docID);
                    entry.setOccurrences(aligned_positions);
                    result.insert(entry);
                }
                ++i1;
                ++i2;
            } else {
                if (p1.get(i1).docID < p2.get(i2).docID) {
                    ++i1;
                } else {
                    ++i2;
                }
            }
        }
        return result;
    }
}