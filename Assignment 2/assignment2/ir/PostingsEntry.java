/*  
 *   This file is part of the computer assignment for the
 *   Information Retrieval course at KTH.
 * 
 *   Johan Boye, 2017
 */  

package ir;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.io.Serializable;

public class PostingsEntry implements Comparable<PostingsEntry>, Serializable {

    public int docID;
    public double score = 0;
    public ArrayList<Integer> occurrences = new ArrayList<>();
    String lazy_occurrences = null;

    /**
     *  PostingsEntries are compared by their score (only relevant
     *  in ranked retrieval).
     *
     *  The comparison is defined so that entries will be put in 
     *  descending order.
     */
    @Override
    public int compareTo( PostingsEntry other ) {
       return Double.compare( other.score, score );
    }


    //
    // YOUR CODE HERE
    //
    public PostingsEntry(int docID) {
        this.docID = docID;
    }

    public void addOccurrence(int position) {
        occurrences.add(position);
    }

    public ArrayList<Integer> getOccurrences() {
        if (lazy_occurrences == null) return occurrences;
        String[] positions = lazy_occurrences.split(";");
        for (String pos : positions) {
            addOccurrence(Integer.valueOf(pos));
        }
        lazy_occurrences = null;
        return occurrences;
    }

    public void setOccurrences(ArrayList<Integer> occ) {
        this.occurrences = occ;
    }

    // term:PostingsList
    // PostingsList:    PostingsEntry1,PostingsEntry2,PostingsEntry3,...
    // PostingsEntry:   docID=pos1;pos2;pos3;...
    // Final:           docID1=pos1;pos2;pos3;...,docID2=pos1;pos2;pos3;...,...
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append(docID);
        str.append("=");
        for (Integer pos : occurrences) {
            str.append(pos);
            str.append(";");
        }
        str.deleteCharAt(str.length() - 1);
        return str.toString();
    }

    public PostingsEntry() {}

    public PostingsEntry(String data) {
        String[] fields = data.split("=");
        this.docID = Integer.parseInt(fields[0]);
        lazy_occurrences = fields[1];
        // String[] positions = lazy_occurrences.split(";");
        // for (String pos : positions) {
        //     addOccurrence(Integer.valueOf(pos));
        // }

        // String[] pos = lazy_occurrences.split(";");
        // occurrences = new ArrayList<Integer>(Collections.nCopies(pos.length, 0));
        // for (int i = 0; i < pos.length; ++i) {
        //     occurrences.set(i, Integer.valueOf(pos[i]));
        // }
    }

    public PostingsEntry(PostingsEntry entry) {
        this.docID = entry.docID;
        this.occurrences = entry.getOccurrences();
    }
}

