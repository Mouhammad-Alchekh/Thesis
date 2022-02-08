## Description 
The goal of this project is to design a software which takes as input SQL code and a database schema and produces a report that explains under which isolation level lower than serializability the input can be run safely without generating anomalies. The implementation of this software is based on algorithms and theoretical concepts explained in the next cited paper:

B. Ketsman, C. Koch, F. Neven, and B. Vandevoort, "Deciding robustness for lower SQL isolation levels" in Proceedings of the 39th ACM SIGMOD-SIGACT-SIGAI Symposium on Principles of Database Systems, PODS 2020, Portland, OR, USA, June 14-19, 2020, D. Suciu, Y. Tao, and Z.Wei, Eds., ACM, 2020, pp. 315-330.
