//THIS CODE IS MY OWN WORK AND WAS DONE WITHOUT CONSULTING A TUTOR OR CODE WRITTEN BY OTHER STUDENTS
// - JAKE CRONIN 

import java.io.*;
import java.util.*;

public class bayes{


	static ArrayList<ArrayList<Character>> data;
	static ArrayList<ArrayList<Character>> testData;

	static Probabilities probabilities;
	/*
	HashMap of Classes{
		ArrayList of Attirbutes{
			HashMap of attribute values{
			}
			HashMap of attribute values{
			}
			HashMap of attribute values{
			}
		}
		ArrayList of Attirbutes{
		}
		ArrayList of Attirbutes{
		}
	}
	*/

	public static void main(String[] args){

		if (args.length != 3){
			System.out.println("Invalid Arguments. Useage: <training dataset file> <test dataset file> <outputfile>");
			return;
		}
		long start = System.currentTimeMillis();

		String trainFile = args[0];
		String testFile = args[1];
		String outputFile = args[2];

		//2) Load Data
		data = loadData(trainFile);

		//3) Train Model
		probabilities = new Probabilities(data);
		probabilities.print();

		//4) Test Input
		testData = loadData(testFile);
		int correct = 0;
		ArrayList<Character> guesses = new ArrayList<Character>();
		for(ArrayList<Character> row: testData){
			Character classChar = row.get(0);
			Character guess = probabilities.guess(row);
			guesses.add(guess);
			//System.out.println("Guess: "+guess+" actual: "+classChar);
			if (guess.equals(classChar)){
				correct++;
			}
		}
		double accuracy = (double) correct / testData.size();
		System.out.println("Accuracy: "+accuracy);

		//5) Write Output
		try{
			BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
			long duration = System.currentTimeMillis() - start;
			writer.write("Bayes Accuracy: "+accuracy+"\nTime Elapsed (ms): "+duration+"\nClass Guessse:\t\n");
			for(Character c: guesses){
				writer.write(c + " ");
			}
			writer.close();
		}catch (IOException e){
			System.out.println("Error writing to file");
		}

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
}

class Probabilities{
	int size = 0;
	HashMap<Character, DataClass> classes;

	public Probabilities(ArrayList<ArrayList<Character>> data){
		size = data.size();
		classes = new HashMap<Character, DataClass>();
		for (ArrayList<Character> row: data){
			Character classChar = row.get(0);
			DataClass d = classes.get(classChar);
			if (d==null){
				d = new DataClass(row.size(),classChar,size);
				classes.put(classChar,d);
			}
			d.addRow(row);
		}
	}
	public Character guess(ArrayList<Character> row){
		double maxProb = 0;
		Character bestGuess = '\n';
		for (DataClass d: classes.values()){
			double prob = d.getProbability(row);
			if (prob > maxProb){
				maxProb = prob;
				bestGuess = d.name;
			}
		}
		if (bestGuess != row.get(0)){
			System.out.println("Guessed "+bestGuess+" for class "+row.get(0));
		}

		return bestGuess;
	}
	public void print(){
		for (DataClass d: classes.values()){
			d.print();
		}
	}
}
class DataClass{
	Character name;
	int superSize = 0;
	ArrayList<HashMap<Character,Integer>> attributes;
	public DataClass(int numAttributes, Character name, int superSize){
		this.name = name;
		this.superSize = superSize;
		attributes = new ArrayList<HashMap<Character,Integer>>();
		for (int i=0;i<numAttributes;i++) {
			attributes.add(new HashMap<Character,Integer>());
		}
	}
	public void addRow(ArrayList<Character> row){
		for (int i = 0; i < row.size();i++) {
			HashMap<Character,Integer> attribute = attributes.get(i); //attribute. ex 'hair color'
			Character value = row.get(i);	//value of attribute. ex 'blonde'
			Integer count = attribute.get(value);	//number of people with this attribute in this class
			if (count == null){
				count = 0;
			}
			count = count + 1;
			attribute.put(value,count);
		}
	}
	public double getProbability(ArrayList<Character> row){
		double probability = (double) classSize() / (double) superSize;
		if ( probability == 0) return 0;
		for (int i=1;i<row.size();i++) {
			Character attrValue = row.get(i);
			Integer classAttrSize = attributes.get(i).get(attrValue);
			if (classAttrSize==null) classAttrSize = 1;
			else classAttrSize++;

			probability *= (double)classAttrSize / (double)classSize();
			//System.out.println("Total Size "+totalSize+" and small size "+smallSize+" p: "+p+" prob"+probability);
		}
		return probability;
	}
	public int classSize(){
		return attributes.get(0).get(name);
	}
	public void print(){
		System.out.println("Class "+name+":");
		for (int i = 0; i < attributes.size(); i++) {
			System.out.print("\tAttr "+i+":");
			HashMap<Character,Integer> attribute = attributes.get(i);
			for (Map.Entry<Character,Integer> entry: attribute.entrySet()){
				System.out.print(" ["+entry.getKey()+":"+entry.getValue()+"] ");
			}
			System.out.println();
		}
	}
}





