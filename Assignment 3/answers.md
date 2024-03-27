## Task 3.1

## Task 3.2

IDCG assuming the rating docs are the ONLY relevant documents for the query.

```
DCG_50 =
1 / log_2(2) +
0 / log_2(3) +
0 / log_2(4) +
1 / log_2(5) +
0 / log_2(6) +
1 / log_2(7) +
0 / log_2(8) +
0 / log_2(9) +
0 / log_2(10) +
0 / log_2(11) +
0 / log_2(12) +
0 / log_2(13) +
0 / log_2(14) +
0 / log_2(15) +
0 / log_2(16) +
0 / log_2(17) +
0 / log_2(18) +
0 / log_2(19) +
0 / log_2(20) +
0 / log_2(21) +
0 / log_2(22) +
1 / log_2(23) +
0 / log_2(24) +
0 / log_2(25) +
0 / log_2(26) +
0 / log_2(27) +
1 / log_2(28) +
0 / log_2(29) +
0 / log_2(30) +
1 / log_2(31) +
0 / log_2(32) +
0 / log_2(33) +
1 / log_2(34) +
2 / log_2(35) +
0 / log_2(36) +
0 / log_2(37) +
0 / log_2(38) +
0 / log_2(39) +
0 / log_2(40) +
0 / log_2(41) +
0 / log_2(42) +
0 / log_2(43) +
0 / log_2(44) +
0 / log_2(45) +
1 / log_2(46) +
0 / log_2(47) +
1 / log_2(48) +
0 / log_2(49) +
0 / log_2(50) +
0 / log_2(51)
= 3.36438666345

IDCG_50 =
3 / log_2(2) +
3 / log_2(3) +
3 / log_2(4) +
3 / log_2(5) +
2 / log_2(6) +
2 / log_2(7) +
2 / log_2(8) +
2 / log_2(9) +
2 / log_2(10) +
2 / log_2(11) +
2 / log_2(12) +
2 / log_2(13) +
2 / log_2(14) +
2 / log_2(15) +
2 / log_2(16) +
2 / log_2(17) +
1 / log_2(18) +
1 / log_2(19) +
1 / log_2(20) +
1 / log_2(21) +
1 / log_2(22) +
1 / log_2(23) +
1 / log_2(24) +
1 / log_2(25) +
1 / log_2(26) +
1 / log_2(27) +
1 / log_2(28) +
1 / log_2(29) +
1 / log_2(30) +
1 / log_2(31) +
1 / log_2(32) +
1 / log_2(33) +
1 / log_2(34) +
1 / log_2(35) +
1 / log_2(36) +
1 / log_2(37) +
1 / log_2(38) +
1 / log_2(39) +
1 / log_2(40) +
1 / log_2(41) +
1 / log_2(42) +
1 / log_2(43) +
1 / log_2(44) +
1 / log_2(45) +
1 / log_2(46) +
1 / log_2(47) +
0 / log_2(48) +
0 / log_2(49) +
0 / log_2(50) +
0 / log_2(51)
= 20.8547064738

nDCG_50 = DCG_50 / IDCG_50
= 0.16132505473
```

Mathematics.f marked.

```
Missing docs given 0 rating

DCG_50 =
1 / log_2(2) +
0 / log_2(3) +
0 / log_2(4) +
0 / log_2(5) +
0 / log_2(6) +
0 / log_2(7) +
0 / log_2(8) +
0 / log_2(9) +

0 / log_2(11) +
0 / log_2(12) +
1 / log_2(13) +
2 / log_2(14) +
0 / log_2(15) +
3 / log_2(16) +
0 / log_2(17) +
0 / log_2(18) +
0 / log_2(19) +
0 / log_2(20) +
0 / log_2(21) +
0 / log_2(22) +
0 / log_2(23) +
0 / log_2(24) +
0 / log_2(25) +
0 / log_2(26) +
0 / log_2(27) +
1 / log_2(28) +
1 / log_2(29) +
0 / log_2(30) +
0 / log_2(31) +
0 / log_2(32) +
0 / log_2(33) +
0 / log_2(34) +
0 / log_2(35) +
0 / log_2(36) +
0 / log_2(37) +
0 / log_2(38) +
0 / log_2(39) +
0 / log_2(40) +
0 / log_2(41) +
1 / log_2(42) +
0 / log_2(43) +
0 / log_2(44) +
0 / log_2(45) +
0 / log_2(46) +
0 / log_2(47) +
0 / log_2(48) +
1 / log_2(49) +
0 / log_2(50) +
0 / log_2(51)
= 3.32295127161

IDCG_50 = same
= 20.8547064738

nDCG_50 = 0.15933819427
```

If we dont omit the selected document, we may inflate the score since it is almost guaranteed that the selected document will appear very high in the results.

We see that the nDCG worsens slightly, and that both precision and recall are reduced because there are fewer relevant documents returned. However, those results are still rated highly, but placed suboptimally in the ranking.

## Task 3.3

```
compile_all.bat
java -cp classes ir.KGramIndex -f kgram_test.txt -p patterns.txt -k 2 -kg "ve"
java -cp classes ir.KGramIndex -f kgram_test.txt -p patterns.txt -k 2 -kg "th he"
```

Number of words containing bigram "ve": 7497
Number of words containing bigrams "th he": 3194
