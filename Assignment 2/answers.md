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

## Task 2.
