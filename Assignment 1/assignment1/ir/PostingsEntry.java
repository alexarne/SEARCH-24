/*  
 *   This file is part of the computer assignment for the
 *   Information Retrieval course at KTH.
 * 
 *   Johan Boye, 2017
 */  

package ir;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.io.Serializable;

public class PostingsEntry implements Comparable<PostingsEntry>, Serializable {

    public int docID;
    public double score = 0;
    public ArrayList<Integer> occurrences = new ArrayList<>();

    /**
     *  PostingsEntries are compared by their score (only relevant
     *  in ranked retrieval).
     *
     *  The comparison is defined so that entries will be put in 
     *  descending order.
     */
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
        String[] positions = fields[1].split(";");
        for (String pos : positions) {
            addOccurrence(Integer.valueOf(pos));
        }
    }
}

