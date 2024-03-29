import java.util.*;
import java.io.*;

public class PageRank {

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

    /**
     *   The number of outlinks from each node.
     */
    int[] out = new int[MAX_NUMBER_OF_DOCS];

    /**
     *   The probability that the surfer will be bored, stop
     *   following links, and take a random jump somewhere.
     */
    final static double BORED = 0.15;

    /**
     *   Convergence criterion: Transition probabilities do not 
     *   change more that EPSILON from one iteration to another.
     */
    final static double EPSILON = 0.0001;

       
    /* --------------------------------------------- */


    public PageRank( String filename ) {
		int noOfDocs = readDocs( filename );
		iterate( noOfDocs, 1000 );
		monteCarlo(noOfDocs, 1000);
		svWiki();
    }


    /* --------------------------------------------- */


    /**
     *   Reads the documents and fills the data structures. 
     *
     *   @return the number of documents read.
     */
    int readDocs( String filename ) {
		int fileIndex = 0;
		try {
			System.err.print( "Reading file... " );
			BufferedReader in = new BufferedReader( new FileReader( filename ));
			String line;
			while ((line = in.readLine()) != null && fileIndex<MAX_NUMBER_OF_DOCS ) {
				int index = line.indexOf( ";" );
				String title = line.substring( 0, index );
				Integer fromdoc = docNumber.get( title );
				//  Have we seen this document before?
				if ( fromdoc == null ) {	
					// This is a previously unseen doc, so add it to the table.
					fromdoc = fileIndex++;
					docNumber.put( title, fromdoc );
					docName[fromdoc] = title;
				}
				// Check all outlinks.
				StringTokenizer tok = new StringTokenizer( line.substring(index+1), "," );
				while ( tok.hasMoreTokens() && fileIndex<MAX_NUMBER_OF_DOCS ) {
					String otherTitle = tok.nextToken();
					Integer otherDoc = docNumber.get( otherTitle );
					if ( otherDoc == null ) {
						// This is a previousy unseen doc, so add it to the table.
						otherDoc = fileIndex++;
						docNumber.put( otherTitle, otherDoc );
						docName[otherDoc] = otherTitle;
					}
					// Set the probability to 0 for now, to indicate that there is
					// a link from fromdoc to otherDoc.
					if ( link.get(fromdoc) == null ) {
						link.put(fromdoc, new HashMap<Integer,Boolean>());
					}
					if ( link.get(fromdoc).get(otherDoc) == null ) {
						link.get(fromdoc).put( otherDoc, true );
						out[fromdoc]++;
					}
				}
			}
			if ( fileIndex >= MAX_NUMBER_OF_DOCS ) {
				System.err.print( "stopped reading since documents table is full. " );
			}
			else {
				System.err.print( "done. " );
			}
		} catch ( FileNotFoundException e ) {
			System.err.println( "File " + filename + " not found!" );
		} catch ( IOException e ) {
			System.err.println( "Error reading file " + filename );
		}
		System.err.println( "Read " + fileIndex + " number of documents" );
		return fileIndex;
    }


    /* --------------------------------------------- */


    /*
     *   Chooses a probability vector a, and repeatedly computes
     *   aP, aP^2, aP^3... until aP^i = aP^(i+1).
     */
    void iterate( int numberOfDocs, int maxIterations ) {

		// YOUR CODE HERE

		long startTime = System.currentTimeMillis();
		double[] x = new double[numberOfDocs];
		x[0] = 1;
		double norm = 1;
		int iteration = 0;
		while (norm > EPSILON && iteration < maxIterations) {
			++iteration;
			double[] xp = x.clone();
			Arrays.fill(x, 0);

			// x = xG

			// The P-part (sparse)
			for (int from = 0; from < numberOfDocs; ++from) {
				if (link.get(from) == null) {
					for (int to = 0; to < numberOfDocs; ++to) {
						x[to] += (1-BORED) * (1.0d / numberOfDocs) * xp[from];
					}
					continue;
				}
				for (Integer to : link.get(from).keySet()) {
					x[to] += (1-BORED) * (1.0d / out[from]) * xp[from];
				}
			}

			// The J-part (uniform)
			double sum = 0;
			for (int i = 0; i < numberOfDocs; ++i)
				sum += xp[i];
			for (int i = 0; i < numberOfDocs; ++i)
				x[i] += BORED * (1.0d / numberOfDocs) * sum;

			// // Normalize x
			// double sum = 0;
			// for (int i = 0; i < x.length; ++i)
			// 	sum += x[i];
			// for (int i = 0; i < x.length; ++i)
			// 	x[i] /= sum;

			// Take norm of |x-xp|
			norm = 0;
			for (int i = 0; i < x.length; ++i)
				norm += Math.abs(x[i] - xp[i]);
				// norm += (x[i] - xp[i]) * (x[i] - xp[i]);
			// norm = Math.sqrt(norm);

			System.out.println("ITERATION " + iteration + ", norm: " + norm);
		}

		System.out.println("Top 30:");
		Integer[] indeces = new Integer[numberOfDocs];
		for (Integer i = 0; i < numberOfDocs; ++i)
			indeces[i] = i;
		Arrays.sort(indeces, new Comparator<Integer>() {
			@Override
			public int compare(Integer a, Integer b) {
			  return x[a] < x[b] ? 1 : x[a] == x[b] ? 0 : -1;
			}
		  }
		);
		for (int i = 0; i < 30; ++i) {
			System.out.println(docName[indeces[i]] + ": " + x[indeces[i]]);
		}
		long elapsedTime = System.currentTimeMillis() - startTime;
		System.out.println(String.format("Took %.1f seconds", (float) elapsedTime/1000));
    
		try {
			HashMap<String, String> realName = new HashMap<>();
			BufferedReader in = new BufferedReader( new FileReader("./davisTitles.txt"));
			String line;
			while ((line = in.readLine()) != null) {
				String[] arr = line.split(";");
				realName.put(arr[0], arr[1]);
			}
			in.close();
			FileWriter fw = new FileWriter("./pagerank.txt");
			for (int i = 0; i < numberOfDocs; ++i) {
				fw.write(realName.get(docName[i]) + " " + x[i] + "\n");
			}
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		exactPageRank = x;
	}

	Random rand = new Random();
	double[] exactPageRank;
	int[] visits;

	public void monteCarlo(int numberOfDocs, int t) {
		int cycles = 20;

		System.out.println("\nMonte-Carlo 1:");
		int simulations = 0;
		int totalVisits = 0;
		visits = new int[numberOfDocs];
		for (int i = 0; i < cycles; ++i) {
			totalVisits += mc1(numberOfDocs, t);
			++simulations;
			printGoodness(simulations, totalVisits);
		}

		System.out.println("\nMonte-Carlo 2:");
		simulations = 0;
		totalVisits = 0;
		visits = new int[numberOfDocs];
		for (int i = 0; i < cycles; ++i) {
			totalVisits += mc2(numberOfDocs, t);
			++simulations;
			printGoodness(simulations, totalVisits);
		}

		System.out.println("\nMonte-Carlo 4:");
		simulations = 0;
		totalVisits = 0;
		visits = new int[numberOfDocs];
		for (int i = 0; i < cycles; ++i) {
			totalVisits += mc4(numberOfDocs);
			++simulations;
			printGoodness(simulations, totalVisits);
		}
		// for (int i = 0; i < 30; ++i) {
		// 	System.out.println(i + " " + (double) visits[i] / totalVisits);
		// }

		System.out.println("Top 30:");
		double[] x = new double[numberOfDocs];
		for (int i = 0; i < x.length; ++i) {
			x[i] = (double) visits[i] / totalVisits;
		}
		Integer[] indeces = new Integer[numberOfDocs];
		for (Integer i = 0; i < numberOfDocs; ++i)
			indeces[i] = i;
		Arrays.sort(indeces, new Comparator<Integer>() {
			@Override
			public int compare(Integer a, Integer b) {
			  return x[a] < x[b] ? 1 : x[a] == x[b] ? 0 : -1;
			}
		  }
		);
		for (int i = 0; i < 30; ++i) {
			System.out.println(docName[indeces[i]] + ": " + x[indeces[i]]);
		}

		System.out.println("\nMonte-Carlo 5:");
		simulations = 0;
		totalVisits = 0;
		visits = new int[numberOfDocs];
		for (int i = 0; i < cycles; ++i) {
			totalVisits += mc5(numberOfDocs);
			++simulations;
			printGoodness(simulations, totalVisits);
		}
	}

	/**
	 * Random start point
	 * Go until surfer is bored or max jumps
	 * Mark the end point
	 */
	public int mc1(int numberOfDocs, int t) {
		int totalVisits	= 0;
		for (int i = 0; i < numberOfDocs; ++i) {
			int current = rand.nextInt(numberOfDocs);
			int counter = 0;
			while (counter < t) {
				double p = rand.nextDouble();
				if (p < BORED) break;
				if (link.get(current) == null) {
					current = rand.nextInt(numberOfDocs);
				} else {
					ArrayList<Integer> keys = new ArrayList<>(link.get(current).keySet());
					current = keys.get(rand.nextInt(keys.size()));
				}
				++counter;
			}
			visits[current]++;
			totalVisits++;
		}
		return totalVisits;
	}

	/**
	 * Start m times in every node
	 * Go until surfer is bored or max jumps
	 * Mark the end point
	 */
	public int mc2(int numberOfDocs, int t) {
		int totalVisits	= 0;
		for (int i = 0; i < numberOfDocs; ++i) {
			int current = i;
			int counter = 0;
			while (counter < t) {
				double p = rand.nextDouble();
				if (p < BORED) break;
				if (link.get(current) == null) {
					current = rand.nextInt(numberOfDocs);
				} else {
					ArrayList<Integer> keys = new ArrayList<>(link.get(current).keySet());
					current = keys.get(rand.nextInt(keys.size()));
				}
				++counter;
			}
			visits[current]++;
			totalVisits++;
		}
		return totalVisits;
	}


	/**
	 * Start m times in every node
	 * Go until surfer is bored or dangling node
	 * Mark all visits along the path
	 */
	public int mc4(int numberOfDocs) {
		int totalVisits = 0;
		for (int i = 0; i < numberOfDocs; ++i) {
			int current = i;
			while (true) {
				++totalVisits;
				visits[current]++;
				double p = rand.nextDouble();
				if (p < BORED) break;
				if (link.get(current) == null) break;
				ArrayList<Integer> keys = new ArrayList<>(link.get(current).keySet());
				current = keys.get(rand.nextInt(keys.size()));
			}
		}
		return totalVisits;
	}


	/**
	 * Random start point
	 * Go until surfer is bored or max jumps
	 * Mark all visits along the path
	 */
	public int mc5(int numberOfDocs) {
		int totalVisits = 0;
		for (int i = 0; i < numberOfDocs; ++i) {
			int current = rand.nextInt(numberOfDocs);
			while (true) {
				++totalVisits;
				visits[current]++;
				double p = rand.nextDouble();
				if (p < BORED) break;
				if (link.get(current) == null) break;
				ArrayList<Integer> keys = new ArrayList<>(link.get(current).keySet());
				current = keys.get(rand.nextInt(keys.size()));
			}
		}
		return totalVisits;
	}

	public void printGoodness(int simulations, int totalVisits) {
		double sum = 0; 
		for (int i = 0; i < visits.length; ++i) {
			double estimatedPageRank = (double) visits[i] / totalVisits;
			double delta = estimatedPageRank - exactPageRank[i];
			sum += delta*delta;
		}
		System.out.println("[" + simulations + ", " + sum + "],");
	}

	public void svWiki() {
		docNumber = new HashMap<String,Integer>();
		docName = new String[MAX_NUMBER_OF_DOCS];
		link = new HashMap<Integer,HashMap<Integer,Boolean>>();
		int numberOfDocs = readDocs("linksSvwiki.txt");
		int totalVisits = 0;
		double[] x = new double[numberOfDocs];
		double[] xp = new double[numberOfDocs];
		Integer[] indeces = new Integer[numberOfDocs];
		for (Integer i = 0; i < numberOfDocs; ++i)
			indeces[i] = i;
		Integer[] indecesp = indeces.clone();

		visits = new int[numberOfDocs];
		while (true) {
			totalVisits += mc4(numberOfDocs);

			xp = x.clone();
			for (int i = 0; i < x.length; ++i) {
				x[i] = (double) visits[i] / totalVisits;
			}
			indecesp = indeces.clone();
			Arrays.sort(indeces, new Comparator<Integer>() {
				@Override
				public int compare(Integer a, Integer b) {
				return x[a] < x[b] ? 1 : x[a] == x[b] ? 0 : -1;
				}
			}
			);
			double norm2 = 0;
			for (int i = 0; i < 30; ++i) {
				double delta = x[indeces[i]] - xp[indeces[i]];
				norm2 += delta*delta;
			}
			boolean stable = true;
			for (int i = 0; i < 30; ++i) {
				stable = stable && indeces[i] == indecesp[i];
			}
			System.out.println("norm2: " + norm2 + ", top30 stable: " + stable);

			if (norm2 < 1e-10 && stable) {
				System.out.println("Top30:");
				for (int i = 0; i < 30; ++i) {
					System.out.println(docName[indeces[i]] + ": " + x[indeces[i]]);
				}
				System.out.println();
				try {
					HashMap<String, String> realName = new HashMap<>();
					BufferedReader in = new BufferedReader( new FileReader("./svwikiTitles.txt"));
					String line;
					while ((line = in.readLine()) != null) {
						String[] arr = line.split(";");
						realName.put(arr[0], arr[1]);
					}
					in.close();
					for (int i = 0; i < 30; ++i) {
						System.out.println(realName.get(docName[indeces[i]]) + " " + x[indeces[i]]);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			}
		}
	}


    /* --------------------------------------------- */


    public static void main( String[] args ) {
		if ( args.length != 1 ) {
			System.err.println( "Please give the name of the link file" );
		}
		else {
			new PageRank( args[0] );
		}
    }
}