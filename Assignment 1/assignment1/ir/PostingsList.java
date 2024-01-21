/*  
 *   This file is part of the computer assignment for the
 *   Information Retrieval course at KTH.
 * 
 *   Johan Boye, 2017
 */  

package ir;

import java.util.ArrayList;

public class PostingsList {
    
    /** The postings list */
    private ArrayList<PostingsEntry> list = new ArrayList<PostingsEntry>();


    /** Number of postings in this list. */
    public int size() {
    return list.size();
    }

    /** Returns the ith posting. */
    public PostingsEntry get( int i ) {
    return list.get( i );
    }

    // 
    //  YOUR CODE HERE
    //
    public void insert(PostingsEntry entry) {
        list.add(entry);
    }

    // term:PostingsList
    // PostingsList:    PostingsEntry1,PostingsEntry2,PostingsEntry3,...
    // PostingsEntry:   docID=pos1;pos2;pos3;...
    // Final:           docID1=pos1;pos2;pos3;...,docID2=pos1;pos2;pos3;...>...
    public String toString() {
        StringBuilder str = new StringBuilder();
        for (PostingsEntry entry : list) {
            str.append(entry.toString());
            str.append(",");
        }
        str.deleteCharAt(str.length() - 1);
        return str.toString();
    }

    public PostingsList() {}

    public PostingsList(String data) {
        String[] entries = data.split(",");
        for (String entry : entries) {
            insert(new PostingsEntry(entry));
        }
    }
}

