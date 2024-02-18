/*  
 *   This file is part of the computer assignment for the
 *   Information Retrieval course at KTH.
 * 
 *   Johan Boye, KTH, 2018
 */  

package ir;

import java.io.*;
import java.util.*;
import java.nio.charset.*;


/*
 *   Implements an inverted index as a hashtable on disk.
 *   
 *   Both the words (the dictionary) and the data (the postings list) are
 *   stored in RandomAccessFiles that permit fast (almost constant-time)
 *   disk seeks. 
 *
 *   When words are read and indexed, they are first put in an ordinary,
 *   main-memory HashMap. When all words are read, the index is committed
 *   to disk.
 */
public class PersistentHashedIndex implements Index {

    /** The directory where the persistent index files are stored. */
    public static final String INDEXDIR = "./index";

    /** The dictionary file name */
    public static final String DICTIONARY_FNAME = "dictionary";

    /** The data file name */
    public static final String DATA_FNAME = "data";

    /** The terms file name */
    public static final String TERMS_FNAME = "terms";

    /** The doc info file name */
    public static final String DOCINFO_FNAME = "docInfo";

    /** The dictionary hash table on disk can fit this many entries. */
    public static final long TABLESIZE = 611953L;

    /** The dictionary hash table is stored in this file. */
    RandomAccessFile dictionaryFile;

    /** The data (the PostingsLists) are stored in this file. */
    RandomAccessFile dataFile;

    /** Pointer to the first free memory cell in the data file. */
    long free = 0L;

    /** The cache as a main-memory hash map. */
    HashMap<String,PostingsList> index = new HashMap<String,PostingsList>();


    // ===================================================================

    /**
     *   A helper class representing one entry in the dictionary hashtable.
     */ 
    public class Entry {
        //
        //  YOUR CODE HERE
        //
        long ptr;
        int size; 

        public Entry(long ptr, int size) {
            this.ptr = ptr;
            this.size = size;
        }
    }

    int ENTRY_SIZE = 12;        // 12 bytes per entry (long + int)
    long total_tokens = 0;


    // ==================================================================

    
    /**
     *  Constructor. Opens the dictionary file and the data file.
     *  If these files don't exist, they will be created. 
     */
    public PersistentHashedIndex() {
        try {
            dictionaryFile = new RandomAccessFile( INDEXDIR + "/" + DICTIONARY_FNAME, "rw" );
            dataFile = new RandomAccessFile( INDEXDIR + "/" + DATA_FNAME, "rw" );
        } catch ( IOException e ) {
            e.printStackTrace();
        }

        try {
            readDocInfo();
        } catch ( FileNotFoundException e ) {
        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }

    /**
     *  Writes data to the data file at a specified place.
     *
     *  @return The number of bytes written.
     */ 
    int writeData( String dataString, long ptr ) {
        try {
            dataFile.seek( ptr ); 
            byte[] data = dataString.getBytes();
            dataFile.write( data );
            return data.length;
        } catch ( IOException e ) {
            e.printStackTrace();
            return -1;
        }
    }


    /**
     *  Reads data from the data file
     */ 
    String readData( long ptr, int size ) {
        try {
            dataFile.seek( ptr );
            byte[] data = new byte[size];
            dataFile.readFully( data );
            return new String(data);
        } catch ( IOException e ) {
            e.printStackTrace();
            return null;
        }
    }


    // ==================================================================
    //
    //  Reading and writing to the dictionary file.

    /*
     *  Writes an entry to the dictionary hash table file. 
     *
     *  @param entry The key of this entry is assumed to have a fixed length
     *  @param ptr   The place in the dictionary file to store the entry
     */
    void writeEntry( Entry entry, long ptr ) {
        //
        //  YOUR CODE HERE
        //
        try {
            dictionaryFile.seek(ptr);
            dictionaryFile.writeLong(entry.ptr);
            dictionaryFile.seek(ptr + 8);
            dictionaryFile.writeInt(entry.size);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     *  Reads an entry from the dictionary file.
     *
     *  @param ptr The place in the dictionary file where to start reading.
     */
    Entry readEntry( long ptr ) {   
        //
        //  REPLACE THE STATEMENT BELOW WITH YOUR CODE 
        //
        try {
            dictionaryFile.seek(ptr);
            long entry_ptr = dictionaryFile.readLong();
            dictionaryFile.seek(ptr + 8);
            int entry_size = dictionaryFile.readInt();
            Entry entry = new Entry(entry_ptr, entry_size);
            return entry;
        } catch (IOException e) {
        }
        return null;
    }


    // ==================================================================

    /**
     *  Writes the document names and document lengths to file.
     *
     * @throws IOException  { exception_description }
     */
    private void writeDocInfo() throws IOException {
        FileOutputStream fout = new FileOutputStream( INDEXDIR + "/docInfo" );
        for ( Map.Entry<Integer,String> entry : docNames.entrySet() ) {
            Integer key = entry.getKey();
            String docInfoEntry = key + ";" + entry.getValue() + ";" + docLengths.get(key) + "\n";
            fout.write( docInfoEntry.getBytes() );
        }
        fout.close();
    }


    /**
     *  Reads the document names and document lengths from file, and
     *  put them in the appropriate data structures.
     *
     * @throws     IOException  { exception_description }
     */
    private void readDocInfo() throws IOException {
        File file = new File( INDEXDIR + "/docInfo" );
        FileReader freader = new FileReader(file);
        try ( BufferedReader br = new BufferedReader(freader) ) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(";");
                docNames.put( new Integer(data[0]), data[1] );
                docLengths.put( new Integer(data[0]), new Integer(data[2]) );
            }
        }
        freader.close();
    }


    /**
     *  Write the index to files.
     */
    public void writeIndex() {
        int collisions = 0;
        try {
            // Write the 'docNames' and 'docLengths' hash maps to a file
            writeDocInfo();

            // Write the dictionary and the postings list

            // 
            //  YOUR CODE HERE
            //
            for (String key : index.keySet()) {
                // Find empty slot in dictionary
                long hash = hash(key);
                while (entryExists(hash)) {
                    hash = (hash + 1) % TABLESIZE;
                    ++collisions;
                }

                // Write to dataFile
                String data = key + " " + index.get(key).toString() + "\n";
                int size = writeData(data, free);

                // Write to dictionaryFile
                Entry entry = new Entry(free, size);
                writeEntry(entry, hash * ENTRY_SIZE);
                
                free += size;
            }
        } catch ( IOException e ) {
            e.printStackTrace();
        }
        System.err.println( collisions + " collisions." );
    }


    // ==================================================================


    /**
     *  Returns the postings for a specific term, or null
     *  if the term is not in the index.
     */
    public PostingsList getPostings( String token ) {
        //
        //  REPLACE THE STATEMENT BELOW WITH YOUR CODE
        //

        long hash = hash(token);
        while (entryExists(hash)) {
            Entry entry = readEntry(hash * ENTRY_SIZE);
            String[] data = readData(entry.ptr, entry.size).split(" ");
            if (data[0].equals(token)) return new PostingsList(data[1].trim());

            // Try next slot
            ++hash;
        }
        
        // No match
        return null;
    }


    /**
     *  Inserts this token in the main-memory hashtable.
     */
    public void insert( String token, int docID, int offset ) {
        //
        //  YOUR CODE HERE
        //

        // If first occurrence of this word
        if (!index.containsKey(token)) index.put(token, new PostingsList());

        // Assume in-order insertions, current doc is last doc if previously seen
        // Doc not previously inserted
        if (index.get(token).size() == 0 || index.get(token).get(index.get(token).size()-1).docID != docID) index.get(token).insert(new PostingsEntry(docID));
        // Add position of token
        index.get(token).get(index.get(token).size()-1).addOccurrence(offset);
    }

    public long hash(String token) {
        long hash = 0;
        for (char c : token.toCharArray()) {
            hash = (hash*50 + c) % TABLESIZE;
        }
        return hash;
    }

    public boolean entryExists(long hash) {
        Entry entry = readEntry(hash * ENTRY_SIZE);
        if (entry == null) return false;
        if (entry.ptr == 0 && entry.size == 0) return false;
        return true;
    }


    /**
     *  Write index to file after indexing is done.
     */
    public void cleanup() {
        System.err.println(index.keySet().size() + " unique words" );
        System.err.print( "Writing index to disk..." );
        long startTime = System.currentTimeMillis();
        writeIndex();
        System.err.println( "done!" );
        long elapsedTime = System.currentTimeMillis() - startTime;
        System.err.println(String.format( "Took %.1f seconds.", elapsedTime/1000.0 ));
    }
}
