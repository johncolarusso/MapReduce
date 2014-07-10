MapReduce
=========
Normalize the enron data
1. Run "make" with "preprocess.cpp" and "topnodes.cpp" in the same directory.
2. Run "preproc > enron" with "Email-Enron.txt" in the same directory.

Run FindTriangle on normalized data

1. Run "javac -classpath ./hadoop-core-1.2.1.jar -d node_classes ~/Downloads/hadoop-1.2.1/FindTriangle.java"
2. Run "jar -cvf ./node.jar -C node_classes/ ."
3. Run "bin/hadoop fs -put enron input"
4. Run "bin/hadoop jar node.jar org.myorg.FindTriangle input temp out"
5. Run "bin/hadoop fs -get out enronout"

Find the 10 top most frequently appearing nodes
1. Run "topnodes" with "enronout" in the same directory.
2. The 10 top nodes with how many times they appear are written to standard output

Development Environment
VirtualBox running Ubuntu 12.04