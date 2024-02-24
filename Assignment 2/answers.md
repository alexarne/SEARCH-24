## Task 2.2

- Added the (rudimentary) union method and cosineRank in Searcher, and added the ability to get a PostingsEntry by the docID in PostingsList.

# Task 2.3

**What is the term frequency of the words ‘davis’ and ‘leadership’?**

|     | **davis** ALILP.f | **leadership** ALILP.f | **davis** Davis_Funeral_Chapel.f | **leadership** Davis_Funeral_Chapel.f |
| --- | ----------------- | ---------------------- | -------------------------------- | ------------------------------------- |
| tf  | 0                 | 1                      | 1                                | 0                                     |

**Now use your search engine (how?) to compute the idf of all terms present in both files (round to 4 decimal places). What are the lengths of these documents in the tf-space and in the tf×idf space (rounded to 4 decimal places)?**

Need total number of documents, which is N = 17478.

We then calculate the idf for each distinct term in our document selection (by using idf = ln(N/df)):

|            | df    | idf    |
| ---------- | ----- | ------ |
| davis      | 10312 | 0.5276 |
| leadership | 376   | 3.8391 |
| redirect   | 1907  | 2.2154 |
| asian      | 378   | 3.8338 |
| pacific    | 236   | 4.3049 |
| islander   | 30    | 6.3675 |
| program    | 1377  | 2.5410 |
| wiscombes  | 7     | 7.8228 |
| funeral    | 28    | 6.4365 |
| chapel     | 26    | 6.5106 |

Which gives

|                     | **davis leadership** | ALILP.f | Davis_Funeral_Chapel.f |
| ------------------- | -------------------- | ------- | ---------------------- |
| Euclidean, tf       | 1.4142               | 2.4495  | 2.2361                 |
| Manhattan, tf       | 2                    | 6       | 5                      |
| Euclidean, tf x idf | 3.8752               | 9.9939  | 12.2556                |
| Manhattan, tf x idf | 4.3667               | 23.1017 | 23.5129                |

Calculated using:

```
Euclidean tf x idf:
sqrt((1*ln(17478/10312))^2 + (1*ln(17478/376))^2) = 3.8752
sqrt((1*ln(17478/1907))^2 + (1*ln(17478/378))^2 + (1*ln(17478/236))^2 + (1*ln(17478/30))^2 + (1*ln(17478/376))^2 + (1*ln(17478/1377))^2) = 9.9939
sqrt((1*ln(17478/1907))^2 + (1*ln(17478/7))^2 + (1*ln(17478/10312))^2 + (1*ln(17478/28))^2 + (1*ln(17478/26))^2) = 12.2556

Manhattan tf x idf:
1*ln(17478/10312) + 1*ln(17478/376) = 4.3667
1*ln(17478/1907) + 1*ln(17478/378) + 1*ln(17478/236) + 1*ln(17478/30) + 1*ln(17478/376) + 1*ln(17478/1377) = 23.1017
1*ln(17478/1907) + 1*ln(17478/7) + 1*ln(17478/10312) + 1*ln(17478/28) + 1*ln(17478/26) = 23.5129
```

(Note: the idf in the query is ln(2/1) for both terms)

**What is the cosine similarity between the query and the two documents (in the specified spaces using the specified length normalization, rounded to 4 decimal places)? Don’t forget to use idf for the query terms in the two last rows.**

```
ALILP.f:

tf:
Euclidean: (1*1) / (sqrt(2) * sqrt(6)) = 0.2887
Manhattan: (1*1) / (2 * 6) = 0.0833

tf x idf:
Euclidean: (1*ln(17478/10312) * 1*ln(17478/10312)) / (sqrt((1*ln(17478/10312))^2 + (1*ln(17478/376))^2) * sqrt((1*ln(17478/1907))^2 + (1*ln(17478/378))^2 + (1*ln(17478/236))^2 + (1*ln(17478/30))^2 + (1*ln(17478/376))^2 + (1*ln(17478/1377))^2)) = 0.0072
Manhattan: (1*ln(17478/10312) * 1*ln(17478/10312)) / ((1*ln(17478/10312) + 1*ln(17478/376)) * (1*ln(17478/1907) + 1*ln(17478/378) + 1*ln(17478/236) + 1*ln(17478/30) + 1*ln(17478/376) + 1*ln(17478/1377))) = 0.0028


Davis_Funeral_Chapel.f:

tf:
Euclidean: (1*1) / (sqrt(2) * sqrt(5)) = 0.3162
Manhattan: (1*1) / (2* 5) = 0.1000

tf x idf:
Euclidean: (1*ln(17478/10312) * 1*ln(17478/10312)) / ((sqrt((1*ln(17478/10312))^2 + (1*ln(17478/376))^2)) * (sqrt((1*ln(17478/1907))^2 + (1*ln(17478/7))^2 + (1*ln(17478/10312))^2 + (1*ln(17478/28))^2 + (1*ln(17478/26))^2))) = 0.0059
Manhattan: (1*ln(17478/10312) * 1*ln(17478/10312)) / ((1*ln(17478/10312) + 1*ln(17478/376)) * (1*ln(17478/1907) + 1*ln(17478/7) + 1*ln(17478/10312) + 1*ln(17478/28) + 1*ln(17478/26))) = 0.0027
```

|                            | ALILP.f | Davis_Funeral_Chapel.f |
| -------------------------- | ------- | ---------------------- |
| Euclidean length, tf       | 0.2887  | 0.3162                 |
| Manhattan length, tf       | 0.0833  | 0.1000                 |
| Euclidean length, tf x idf | 0.0072  | 0.0059                 |
| Manhattan length, tf x idf | 0.0028  | 0.0027                 |

**What is the cosine similarity (rounded to 4 decimal places) if the query coordinates are considered to be (1,1)?**

```
ALILP.f:

tf:
Euclidean: (1*1) / (sqrt(2) * sqrt(6)) = 0.2887
Manhattan: (1*1) / (2 * 6) = 0.0833

tf x idf:
Euclidean: (1 * 1*ln(17478/10312)) / (sqrt(2) * sqrt((1*ln(17478/1907))^2 + (1*ln(17478/378))^2 + (1*ln(17478/236))^2 + (1*ln(17478/30))^2 + (1*ln(17478/376))^2 + (1*ln(17478/1377))^2)) = 0.0373
Manhattan: (1*ln(17478/10312) * 1*ln(17478/10312)) / (2 * (1*ln(17478/1907) + 1*ln(17478/378) + 1*ln(17478/236) + 1*ln(17478/30) + 1*ln(17478/376) + 1*ln(17478/1377))) = 0.0060


Davis_Funeral_Chapel.f:

tf:
Euclidean: (1*1) / (sqrt(2) * sqrt(5)) = 0.3162
Manhattan: (1*1) / (2* 5) = 0.1000

tf x idf:
Euclidean: (1*ln(17478/10312) * 1*ln(17478/10312)) / (sqrt(2) * (sqrt((1*ln(17478/1907))^2 + (1*ln(17478/7))^2 + (1*ln(17478/10312))^2 + (1*ln(17478/28))^2 + (1*ln(17478/26))^2))) = 0.0161
Manhattan: (1*ln(17478/10312) * 1*ln(17478/10312)) / (2 * (1*ln(17478/1907) + 1*ln(17478/7) + 1*ln(17478/10312) + 1*ln(17478/28) + 1*ln(17478/26))) = 0.0059
```

|                            | ALILP.f | Davis_Funeral_Chapel.f |
| -------------------------- | ------- | ---------------------- |
| Euclidean length, tf       | 0.2887  | 0.3162                 |
| Manhattan length, tf       | 0.0833  | 0.1000                 |
| Euclidean length, tf x idf | 0.0373  | 0.0161                 |
| Manhattan length, tf x idf | 0.0060  | 0.0059                 |

## Task 2.4

The only one similar two similar to the intersection query from 1.5 are Elaine_Kasimatis.f and Evelyn_Silvia.f.

The ranked retrieval only returns very short documents.

Difficult cases (generally all redirects are tricky):

```
1 Math.f 2 - is a redirect to a relevant page considered relevant? perhaps...
1 Grad_Students.f 0 - would probably be relevant if i took the target page (Graduate Students) into consideration
1 EfremRensi.f 1 - same as assignment 1, are graduates relevant?
```

```
top10, relevant and returned: 3
top20, relevant and returned: 3
top30, relevant and returned: 7
top40, relevant and returned: 10
top50, relevant and returned: 13
```

|        | **Precision** | **Recall**    |
| ------ | ------------- | ------------- |
| **10** | 3/10 = 0.30   | 3/100 = 0.03  |
| **20** | 3/20 = 0.15   | 3/100 = 0.03  |
| **30** | 7/30 = 0.23   | 7/100 = 0.07  |
| **40** | 10/40 = 0.25  | 10/100 = 0.10 |
| **50** | 13/50 = 0.26  | 13/100 = 0.13 |

![](./precision-recall-graph.png)

**Which precision is the highest? Are there any trends?**

Highest for top10 results, then it shoots down and starts climbing back up but never reaching past the precision for top10.

**Which recall is the highest? Is there any relation between precision at 10, 20, 30, 40, 50, and recall at 10, 20, 30, 40, 50?**

Recall is obviously highest when we consider the most amount of documents in our returned result, which is for top50.

The relation is that precision perhaps starts to flatline at around 0.25 while recall increases the more documents are considered.

## Task 2.5

![](./pagerank-optimization.png)

Edited the pagerank file, added the pagerank loader to the Engine file, and used that pagerank mapping in the Searcher file for combining in search.

**Look up the titles of some documents with high rank, and some documents with low rank. Does the ranking make sense?**

```
245: 0.01253164114193445 - UC_Davis.f
121: 0.012095036645627017 - Davis.f
...
16: 0.0014464177187003574 - 2004.f
484: 0.0014457534622735054 - Winters.f
```

The rankings make sense, it is to be expected that pages which are central to Davis should be linked to a lot, and therefore have a high PageRank. Perhaps 2004.f is a yearly review or something, which is why it is less linked to but still somewhat prevalent. And the same for Winters.f.

**What is the effect of letting the tf_idf score dominate this ranking? What is the effect of letting the pagerank dominate? What would be a good strategy for selecting an ”optimal” combination? (Remember the quality measures you studied in Task 2.3.)**

Similarity (which is a bit unreliable) vs. credibility/reputation.

# Task 2.6

Added function to load the file with Euclidean lengths in Engine and added the calculator in Indexer.
