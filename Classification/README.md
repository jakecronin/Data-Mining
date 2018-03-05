
# Decision Tree and Bayes Classifier README

## How To Compile

##### In a terminal with java installed, run:

javac c45.java
javac bayes.java

## How To Run

##### After compiling the program, execute with the command:

java c45 \<training data filename\> \<test data filename\> \<output filename\>
java bayes \<training data filename\> \<test data filename\> \<output filename\>


##### Example:

java c45 mushroom.training.dat mushroom.text.dat c45Out.txt
java bayes mushroom.training.dat mushroom.test.dat bayesOut.txt

##### Note: Ensure that input file is in the same directory as the executable files