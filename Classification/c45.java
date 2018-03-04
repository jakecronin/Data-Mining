//THIS CODE IS MY OWN WORK AND WAS DONE WITHOUT CONSULTING A TUTOR OR CODE WRITTEN BY OTHER STUDENTS
// - JAKE CRONIN 

import java.io.*;
import java.util.*;


public class c45{

	static ArrayList<ArrayList<Character>> data;
	static boolean[] categories;

	public static void main(String[] args){

		if (args.length != 3){
			System.out.println("Invalid Arguments. Useage: <training dataset file> <test dataset file> <outputfile>");
			return;
		}

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

		//1) Load Data
		data = loadData(trainFile);
		if (data == null){
			System.out.println("No training data. Not building tree");
			return;
		}
		int numTrainingSamples = data.size();
		categories = new boolean[data.get(0).size()];
		LinkedList<Integer> rows = new LinkedList<Integer>();
		for (int i = 0; i < numTrainingSamples; i++){
			rows.add(i);
		}

		//2) Recursively Build Tree
		Tree myTree = buildTree(rows);


		//3) Test Tree

	}

	public static Tree buildTree(LinkedList<Integer> rows){
		//For each attribute, calculate gain ratio
		//for bes
		return new Tree();
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

	public static void printData(){
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
	Tree[] children = null; //possible categories for this attribute
	HashMap<Character, Integer> predictions = null; //holds number of test data examples that make it here for each class that makes it here

	public Tree(){
		return;
	}
	public Tree(int attribute, Tree[] subtrees){
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
	}

}





