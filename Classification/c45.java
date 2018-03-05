//THIS CODE IS MY OWN WORK AND WAS DONE WITHOUT CONSULTING A TUTOR OR CODE WRITTEN BY OTHER STUDENTS
// - JAKE CRONIN 

import java.io.*;
import java.util.*;


public class c45{

	static ArrayList<ArrayList<Character>> data;
	static ArrayList<ArrayList<Character>> testdata;
	static Tree tree;

	public static void main(String[] args){

		if (args.length != 3){
			System.out.println("Invalid Arguments. Useage: <training dataset file> <test dataset file> <outputfile>");
			return;
		}
		long start = System.currentTimeMillis();

		/*
			Pseudo Code
			load data

			buildtree(LinkedList<Integer> rows) -> Tree
				for each attribute in the unused attribute list [boolean]
					calculate gain ratio
				Split data on highest gain ratio into arrays (ArrayList<ArrayList<int>>)
				
				if no more categories, or if gain ratio is zero 
			***			return Tree(-1, Null, rows) //only pass rows if its a leaf
			
				Tree[] children = new Tree[attribute options count]
				for att in attribute options
					children.append(buildtree(att))
				
				return Tree(attribute, children)	
			Tree()
				int attribute = -1; -> col to be tested at this step. -1 if endnode
				Tree[] nodes = Null; -> children
				HashMap<Character, Int> predictions = Null; //holds number of test data examples that make it here for each class that makes it here

				public Tree(attribute, splitOptions){
					this.attribute = attribute (-1 if leaf node)
					nodes = splitOptions
				}
				public Tree(List<Int> rows){
					for row in rows{
						//increment prediction for this row's class
					}
				}

		*/
		String trainFile = args[0];
		String testFile = args[1];
		String outputFile = args[2];

		//1) Load Data
		data = loadData(trainFile);
		if (data == null){
			System.out.println("No training data. Not building tree");
			return;
		}
		int numTrainingSamples = data.size();
		ArrayList<Boolean> attributes = new ArrayList<Boolean>();
		for (int i = 0; i < data.get(0).size(); i++) {
			attributes.add(true);
		}
		LinkedList<Integer> rows = new LinkedList<Integer>();
		for (int i = 0; i < numTrainingSamples; i++){
			rows.add(i);
		}

		//2) Recursively Build Tree
		tree = buildTree(rows, attributes);
		//printTree(tree);


		//3) Test Tree
		testdata = loadData(testFile);

		int correct = 0;
		LinkedList<Character> correctClasses = new LinkedList<Character>();
		for (ArrayList<Character> row: testdata){
			Character actual = row.get(0);
			Character guess = guess(row, tree);
			correctClasses.add(guess);
			//System.out.println("guess: "+guess+" actual: "+actual);
			if (guess == actual){
				correct++;
			}
		}
		long duration = System.currentTimeMillis() - start;
		double performance = (double)correct / (double)testdata.size();
		System.out.println("Decision Tree Accuracy: "+performance*100+" ("+correct+"/"+testdata.size()+")\nTime Elapsed (ms): "+duration);

		//4) Write Results
		try{
			BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
			writer.write("Decision Tree Accuracy: "+performance*100+" ("+correct+"/"+testdata.size()+")\nTime Elapsed (ms): "+duration+"\nClass Guessse:\t\n");

			for(Character c: correctClasses){
				writer.write(c + " ");
			}
			writer.close();
		}catch (IOException e){
			System.out.println("Error writing to file");
		}
	}

	public static Character guess(ArrayList<Character> row, Tree tree){
		if (tree.attribute == -1){
			return tree.prediction;
		}else{
			Character attChar = row.get(tree.attribute);
			return guess(row, tree.children.get(attChar));
		}

	}
	public static Tree buildTree(LinkedList<Integer> rows, ArrayList<Boolean> attr){
		//Calculate attribute with best gain ratio
		double maxGainRatio = 0.0;
		int maxAttribute = 0;

		ArrayList<Boolean> attributes = new ArrayList<Boolean>(attr.size());
		for (int i = 0; i<attr.size(); i++) {
			attributes.add(attr.get(i));
		}
		for (int i = 1;i < attributes.size(); i++) {
			if (!attributes.get(i)){ continue; } //skip attributes that have already been used
			double gainRatio = calcGainRatio(rows, i);
			if (gainRatio > maxGainRatio){
				maxGainRatio = gainRatio;
				maxAttribute = i;
			}
		}
		if (maxGainRatio == 0){
			return new Tree(rows, data);
		}
		attributes.set(maxAttribute, false);	//mark this attribute as used

		//System.out.println("Got max attribute "+ maxAttribute);

		//sort rows by attribute
		HashMap<Character, LinkedList<Integer>> sortedRows = splitRows(rows, maxAttribute);

		//recursively build each child
		HashMap<Character, Tree> children = new HashMap<Character, Tree>();
		for (Character c: sortedRows.keySet()){
			Tree child = buildTree(sortedRows.get(c), attributes);
			children.put(c, child);
		}

		Tree thisTree = new Tree(maxAttribute, children);
		return thisTree;
	}
	public static void printTree(Tree tree){

		LinkedList<Tree> stack = new LinkedList<Tree>();
		stack.add(tree);

		while(stack.size() > 0){
			//go through stack and print
			for (Tree t: stack){
				if (t.attribute == -1){
					System.out.print("["+t.predictions.size()+"]");
				}else{
					System.out.print("["+t.attribute+","+t.children.size()+"]");
				}
			}
			System.out.println("\n");

			//add children of all trees at this level
			int num = stack.size();
			for (int i = 0; i < num; i++) {
				Tree next = stack.removeLast();
				if (next.children == null){continue;};
				for (Tree t: next.children.values()){
					stack.addFirst(t);
				}
			}
		}
	}
	static double calcGainRatio(LinkedList<Integer> rows, int attribute){
		//for each attribute, calculate 
		double startInfo = info(rows, 0);
		double endInfo = 0.0;
		HashMap<Character, LinkedList<Integer>> splitRows = splitRows(rows, attribute);
		for (LinkedList<Integer> subRows: splitRows.values()){
			endInfo = endInfo + (((double)subRows.size() / (double) rows.size()) * info(subRows, 0));
		}
		double gain = startInfo - endInfo;

		double splitInfo = info(rows, attribute);
		double ratio = ((splitInfo!=0)?(gain/splitInfo):0); 

		//System.out.println("Gain Ratio on "+attribute+": start("+startInfo+") end("+endInfo+") split ("+splitInfo+"( ratio("+gain+")");
		return ratio;
	}
	static double info(LinkedList<Integer> rows, int attribute){
		//Summation over attributes
		HashMap<Character,LinkedList<Integer>> rowsByAttr = splitRows(rows, attribute);
		double sum = 0.0;
		for (LinkedList<Integer> list: rowsByAttr.values()){	//summation over classes
			int i = list.size();
			double ratio = (double)i / (double) rows.size();
			double log = Math.log(ratio) / Math.log(2);
			sum = sum - (ratio * log);
		}
		return sum;
	}
	static HashMap<Character,LinkedList<Integer>> splitRows(LinkedList<Integer> rows, int attribute){
		HashMap<Character, LinkedList<Integer>> toReturn = new HashMap<Character, LinkedList<Integer>>();
		//map each row to a character
		for (Integer i: rows){
			Character attrChar = data.get(i).get(attribute);
			LinkedList<Integer> subRows = toReturn.get(attrChar);
			if (subRows == null){
				subRows = new LinkedList<Integer>();
				toReturn.put(attrChar, subRows);
			}
			subRows.add(i);
		}
		return toReturn;
	}

	public static ArrayList<ArrayList<Character>> loadData(String filename){
		data = new ArrayList<ArrayList<Character>>();
		try{
			Scanner scan = new Scanner(new File(filename));
			while (scan.hasNext()){
				ArrayList<Character> nextLine = new ArrayList<Character>();
				String[] line = scan.nextLine().split("\t");
				for (String word: line){
					char c = word.charAt(0);
					nextLine.add(c);
				}
				data.add(nextLine);
			}
		}catch (java.io.FileNotFoundException e){
			System.out.println("File not found");
		}
		return data;
	}

	public static void printData(ArrayList<ArrayList<Character>> data){
		if (data == null){
			System.out.println("Cannot print data. Data is null");
			return;
		}
		for (ArrayList<Character> line: data){
			for (Character c: line){
				System.out.print(c + " ");
			}
			System.out.print("\n");
		}
		System.out.println("Data is "+data.size()+" by "+data.get(0).size());
		return;
	}
}

class Tree{
	int attribute = -1; // col to be tested at this step. -1 if endnode
	HashMap<Character, Tree> children; //possible categories for this attribute
	HashMap<Character, Integer> predictions = null; //holds number of test data examples that make it here for each class that makes it here
	Character prediction;

	public Tree(){
		return;
	}
	public Tree(int attribute, HashMap<Character, Tree> subtrees){
		this.attribute = attribute; // (-1 if leaf node);
		children = subtrees;
	}
	public Tree(List<Integer> rows, ArrayList<ArrayList<Character>> data){
		predictions = new HashMap<Character, Integer>();
		for (Integer index: rows){
			Character itemClass = data.get(index).get(0);
			Integer count = predictions.get(itemClass);
			if (count == null){
				predictions.put(itemClass, 1);
			}else{
				predictions.put(itemClass, count + 1);
			}
		}

		int max = 0;
		for (Map.Entry<Character,Integer> entry: predictions.entrySet()){
			if (entry.getValue() > max){
				max = entry.getValue();
				prediction = entry.getKey();
			}
		}

	}
}





