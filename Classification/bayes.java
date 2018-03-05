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

		if (args.length != 3 && args.length != 4){
			System.out.println("Invalid Arguments. Useage: <training dataset file> <test dataset file> <outputfile> (-v)");
			return;
		}
		long start = System.currentTimeMillis();

		String trainFile = args[0];
		String testFile = args[1];
		String outputFile = args[2];
		boolean verbose = (args.length == 4 && args[3].equals("-v"));

		//2) Load Data
		data = loadData(trainFile);

		//3) Train Model
		probabilities = new Probabilities(data);
		if(verbose) probabilities.print();

		//4) Test Input
		testData = loadData(testFile);
		int correctLap = 0, correctNoLap = 0;
		ArrayList<Character> guessesLap = new ArrayList<Character>(); //guesses with laplacian correction
		ArrayList<Character> guessesNoLap = new ArrayList<Character>(); //guesses without laplacian correction

		for(ArrayList<Character> row: testData){
			Character classChar = row.get(0);
			Character guessLap = probabilities.guess(row,false,true); //guess using laplacian correction
			Character guessNoLap = probabilities.guess(row,false,false); //guess without laplacian correction
			guessesLap.add(guessLap);
			guessesNoLap.add(guessNoLap);
			if (guessLap.equals(classChar)) correctLap++;
			else if (verbose) probabilities.guess(row,true,true);
			if (guessNoLap.equals(classChar)) correctNoLap++;
			else if (verbose) probabilities.guess(row,true,false);
		}
		double accuracyLap = (double) correctLap / testData.size() * 100;
		double accuracyNoLap = (double) correctNoLap / testData.size() * 100;
		System.out.println("Accuracy with Laplacian correction: "+accuracyLap+"%\tWithout:"+accuracyNoLap+"%");
		System.out.println("Writing results "+(accuracyLap>accuracyNoLap?"without laplacian correction.":"with laplacian correction."));

		//5) Write Output. Use whichever method had better accuracy
		try{
			BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
			long duration = System.currentTimeMillis() - start;
			writer.write("Bayes Accuracy: "+Math.max(accuracyLap,accuracyNoLap)+"\nTime Elapsed (ms): "+duration+"\nClass Guessse:\t\n");
			for(Character c: (accuracyLap>accuracyNoLap?guessesLap:guessesNoLap)){
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
	ArrayList<HashMap<Character,Integer>> attributeCounts;

	public Probabilities(ArrayList<ArrayList<Character>> data){
		size = data.size();
		classes = new HashMap<Character, DataClass>();
		attributeCounts = new ArrayList<HashMap<Character,Integer>>();
		for (int i=0;i<data.get(0).size();i++) {
			attributeCounts.add(new HashMap<Character,Integer>());
		}

		for (ArrayList<Character> row: data){

			//Put row into appropriate class
			Character classChar = row.get(0);
			DataClass d = classes.get(classChar);
			if (d==null){
				d = new DataClass(row.size(),classChar,size);
				classes.put(classChar,d);
			}
			d.addRow(row);

			//Use row to populate attribute counts
			for (int i = 0; i < row.size();i++) {
				HashMap<Character,Integer> attribute = attributeCounts.get(i); //attribute. ex 'hair color'
				Character value = row.get(i);	//value of attribute. ex 'blonde'
				Integer count = attribute.get(value);	//total number of people with this attribute
				if (count == null) count = 1;
				else count = count + 1;
				attribute.put(value,count);
			}
		}
	}
	public Character guess(ArrayList<Character> row, boolean verbose, boolean laplacian){
		double maxProb = 0;
		Character bestGuess = '\n';
		for (DataClass d: classes.values()){
			if (verbose) System.out.println("Guessing "+(verbose?"with laplacian correction":"without laplacian correction")+"...");
			double prob = d.getProbability(row, attributeCounts, verbose, laplacian);
			if (prob > maxProb){
				maxProb = prob;
				bestGuess = d.name;
			}
		}
		return bestGuess;
	}
	public void print(){
		System.out.print("\n\nProbabilities\n\tSize:"+size+"\n");

		System.out.print("Attributes:\n\t");
		for (int i=0; i<attributeCounts.size(); i++) {
			HashMap<Character,Integer> attribute = attributeCounts.get(i);
			System.out.print("\n\tattribute "+i+": ");
			for (Map.Entry<Character,Integer> entry: attribute.entrySet()){
				System.out.print(" ["+entry.getKey()+":"+entry.getValue()+"] ");
			}
		}
			
		System.out.print("\nClasses:\n");	
		for (DataClass d: classes.values()){
			d.print();
		}
	}
}
class DataClass{
	Character name;
	int superSize = 0;	//number of data points in train data set with laplacean correction (#data + #classes)
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
			if (count == null) count = 1;
			else count = count + 1;
			attribute.put(value,count);
		}
	}
	public double getProbability(ArrayList<Character> row, ArrayList<HashMap<Character,Integer>> totalAttributes, boolean verbose, boolean laplacian){

		double probability = (double) classSize() / (double) superSize; //P(this class)
		if (verbose) System.out.print("\n\tP("+name+") = "+probability+"\n");

		for (int i=1;i<row.size();i++) {
			Character attrValue = row.get(i);
			Integer classAttrSize = attributes.get(i).get(attrValue);
			if (classAttrSize==null) classAttrSize = (laplacian==true?1:0);	//optional laplacian transform
			Integer totalClassSize = classSize()+ (laplacian==true?attributes.get(i).size():0); //optional laplacian transform
			probability *= (double)classAttrSize / (double)totalClassSize; // *= P(this attribute | this class)
			
			Integer attrSize = totalAttributes.get(i).get(attrValue);
			if (attrSize==null) attrSize = (laplacian==true?1:0); //optional laplacian transform
			else attrSize = attrSize + 1;
			Integer totalTrainSize = superSize + (laplacian==true?totalAttributes.get(i).size():0);	//laplacean corection for denominator. Total#Data + #attributeValues
			probability /= (double) attrSize / (double) totalTrainSize;	// /= P(this attribute)

			if (verbose){
				System.out.print("\t\tP("+i+"="+attrValue+"|"+name+")="+(double)classAttrSize / (double)totalClassSize);
				System.out.print("\tP("+i+"="+attrValue+")="+(double)attrSize / (double)totalTrainSize+"\tP("+name+"|"+i+"="+attrValue+")="+((double)classAttrSize / (double)totalClassSize) / ((double)attrSize / (double)totalTrainSize) * ((double) classSize() / (double) superSize)+"\n");
			}
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





