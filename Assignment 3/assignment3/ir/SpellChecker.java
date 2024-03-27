/*
 *   This file is part of the computer assignment for the
 *   Information Retrieval course at KTH.
 *
 *   Dmytro Kalpakchi, 2018
 */

package ir;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;


public class SpellChecker {
    /** The regular inverted index to be used by the spell checker */
    Index index;

    /** K-gram index to be used by the spell checker */
    KGramIndex kgIndex;

    /** The auxiliary class for containing the value of your ranking function for a token */
    class KGramStat implements Comparable {
        double score;
        String token;

        KGramStat(String token, double score) {
            this.token = token;
            this.score = score;
        }

        public String getToken() {
            return token;
        }

        public int compareTo(Object other) {
            if (this.score == ((KGramStat)other).score) return 0;
            return this.score < ((KGramStat)other).score ? -1 : 1;
        }

        public String toString() {
            return token + ";" + score;
        }
    }

    /**
     * The threshold for Jaccard coefficient; a candidate spelling
     * correction should pass the threshold in order to be accepted
     */
    private static final double JACCARD_THRESHOLD = 0.4;


    /**
      * The threshold for edit distance for a candidate spelling
      * correction to be accepted.
      */
    private static final int MAX_EDIT_DISTANCE = 2;


    public SpellChecker(Index index, KGramIndex kgIndex) {
        this.index = index;
        this.kgIndex = kgIndex;
    }

    /**
     *  Computes the Jaccard coefficient for two sets A and B, where the size of set A is 
     *  <code>szA</code>, the size of set B is <code>szB</code> and the intersection 
     *  of the two sets contains <code>intersection</code> elements.
     */
    private double jaccard(int szA, int szB, int intersection) {
        //
        // YOUR CODE HERE
        //
        return intersection / ((double) szA + szB - intersection);
    }

    /**
     * Computing Levenshtein edit distance using dynamic programming.
     * Allowed operations are:
     *      => insert (cost 1)
     *      => delete (cost 1)
     *      => substitute (cost 2)
     */
    private int editDistance(String s1, String s2) {
        //
        // YOUR CODE HERE
        //

        int dp[][] = new int[s1.length()+1][s2.length()+1];
        for (int i = 0; i < s1.length()+1; ++i) dp[i][0] = i;
        for (int i = 0; i < s2.length()+1; ++i) dp[0][i] = i;
        for (int i = 1; i < s1.length()+1; ++i) {
            for (int j = 1; j < s2.length()+1; ++j) {
                int costAdd = dp[i-1][j] + 1;
                int costRemove = dp[i][j-1] + 1;
                int costSubstitute = dp[i-1][j-1];
                if (s1.charAt(i-1) != s2.charAt(j-1)) costSubstitute += 2;
                dp[i][j] = Math.min(costAdd, costRemove);
                dp[i][j] = Math.min(dp[i][j], costSubstitute); 
            }
        }

        return dp[s1.length()][s2.length()];
    }

    class Candidate implements Comparable<Candidate> {
        public String term;
        public double jaccard;
        public int editDistance;
        public Candidate(String term, double jaccard, int editDistance) {
            this.term = term;
            this.jaccard = jaccard;
            this.editDistance = editDistance;
        }
        @Override
        public int compareTo(Candidate other) {
            if (this.editDistance < other.editDistance) return -1;
            if (this.editDistance > other.editDistance) return 1;
            if (this.jaccard < other.jaccard) return 1;
            if (this.jaccard == other.jaccard) return 0;
            return -1;
        }
    }

    /**
     *  Checks spelling of all terms in <code>query</code> and returns up to
     *  <code>limit</code> ranked suggestions for spelling correction.
     */
    public String[] check(Query query, int limit) {
        //
        // YOUR CODE HERE
        //
        // System.out.println("Checking");
        if (query.queryterm.size() == 0) return null;
        String term = query.queryterm.get(0).term;
        if (index.getPostings(term) != null) return null;

        ArrayList<Candidate> candidates = new ArrayList<>();
        ArrayList<String> kgramsTemp = kgIndex.getKgrams("^" + term + "$");
        HashSet<String> kgrams = new HashSet<>();
        for (String kgram : kgramsTemp) kgrams.add(kgram);
        HashSet<String> candidateWords = new HashSet<>();
        for (String kgram : kgrams) {
            // System.out.println("kgram " + kgram);
            ArrayList<String> words = kgIndex.getWords(new String[]{ kgram });
            // System.out.println("words");
            for (String word : words) {
                candidateWords.add(word);
                // System.out.println(word);
            }
        }
        for (String word : candidateWords) {
            ArrayList<String> kgramsOther = kgIndex.getKgrams("^" + word + "$");
            int szA = kgrams.size();
            int szB = kgramsOther.size();
            int intersection = 0;
            for (String kgram : kgramsOther) {
                if (kgrams.contains(kgram)) ++intersection;
            }
            double jaccard = jaccard(szA, szB, intersection);
            // if (word.equals("thorn")) System.out.println("jaccard to " + word + ": " + jaccard);
            if (jaccard < JACCARD_THRESHOLD) continue;
            int editDistance = editDistance(term, word);
            // if (word.equals("thorn")) System.out.println("distacne to " + word + ": " + editDistance);
            if (editDistance > MAX_EDIT_DISTANCE) continue;
            candidates.add(new Candidate(word, jaccard, editDistance));
        }
        Collections.sort(candidates);
        System.out.println("candidates:");
        for (Candidate c : candidates) {
            System.out.println(c.term + " " + c.jaccard + " " + c.editDistance);
        }
        System.out.println("limit " + limit);
        String[] result = new String[Math.min(limit, candidates.size())];
        for (int i = 0; i < candidates.size() && i < limit; ++i) {
            result[i] = candidates.get(i).term;
        }
        // System.out.println("Returning");
        // for (String s : result) System.out.println(s);
        return result;
    }

    /**
     *  Merging ranked candidate spelling corrections for all query terms available in
     *  <code>qCorrections</code> into one final merging of query phrases. Returns up
     *  to <code>limit</code> corrected phrases.
     */
    private List<KGramStat> mergeCorrections(List<List<KGramStat>> qCorrections, int limit) {
        //
        // YOUR CODE HERE
        //
        return null;
    }
}
