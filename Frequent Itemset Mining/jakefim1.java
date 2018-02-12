//THIS CODE IS MY OWN WORK AND WAS DONE WITHOUT CONSULTING A TUTOR OR CODE WRITTEN BY OTHER STUDENTS
// - JAKE CRONIN 

import java.io.*;
import java.util.*;

public class jakefim{

	static LinkedList<LinkedList<Item>> data;	//Transactions<Items Per Transaction>

	static ArrayList<HashMap<Item, Integer>> frequency;	//records frequency of each item
	static ArrayList<HashSet<Item>> frequents;	//holds all frequent items

	static int minCount = 1;


//Used For Printing
	static int linecount = 0;

	static long singleTime = 0;
	static long doubleTime = 0;

	static ArrayList<Long> times;	
//Used for Printing

	public static void main(String[] args) {
		long start = System.currentTimeMillis();

		if (args.length != 3){
			System.out.println("Invalid call. Useage: <inputfile> <min support count> <outputfile>");
			return;
		}

		//Init Stuff
		frequency = new ArrayList<HashMap<Item, Integer>>();	//holds count of each visited set
		frequents = new ArrayList<HashSet<Item>>();				//holds names of frequent sets
		minCount = Integer.parseInt(args[1]);					//minimum support threshold
		times = new ArrayList<Long>();							//time breakdown of various functions

		//Read in Data
		String fromfile = args[0];
		loadData(fromfile);


		countSingleItems();
		countDoubleItems();
		while (data.isEmpty() == false){
			countNextSize();
		}
		System.out.println("going to write data. time elapsed: "+(System.currentTimeMillis()-start));

		String tofile = args[2];
		writeData(tofile);

		for (int i = 0; i < times.size(); i++) {
			System.out.println("Layer "+(i+1)+" time: "+times.get(i));
		}

		long duration = System.currentTimeMillis() - start;
		System.out.println("Total Duration: "+duration);

	}



	static void countSingleItems(){   //count occurances for size 1's into a hashmap
		long startTime = System.currentTimeMillis();

		HashMap<Item,Integer> singleItems = new HashMap<Item, Integer>();	//remember unique items
		HashSet<Item> frequent = new HashSet<Item>();	//remember items that pass min threshold

		int c = 0;
		int size = data.size();
		for (int p = 0; p < size; p++){
			LinkedList<Item> transaction = data.pollFirst();
		
			while(transaction.size() > 1){
				Item item = transaction.pollFirst();
			}
			for (Item item: transaction){
				int count = 1;
				if (singleItems.containsKey(item)){
					count = singleItems.get(item) + 1;
					singleItems.put(item, count);	//increment count
				}else{
					singleItems.put(item, 1);
				}
				if (count == minCount){
					frequent.add(item);
				}
			}
		}
		frequency.add(singleItems);	//FIXME: Only retain item counts that are frequent to save space
		frequents.add(frequent);

		singleTime = System.currentTimeMillis() - startTime;
		times.add(singleTime);
	}

	static void countDoubleItems(){
		long startTime = System.currentTimeMillis();

		HashMap<Item, Integer> doubleItems = new HashMap<Item, Integer>();	//save count of double items
		HashSet<Item> frequentDoubles = new HashSet<Item>();	//save names of frequent items

		HashSet<Item> frequentSingles = frequents.get(0);

		int size = data.size();
		for (int p = 0; p < size; p++){
			//System.out.println(times.size()+": "+p+"/"+linecount);
			LinkedList<Item> transaction = data.pollFirst();	//single items in transaction
			LinkedList<Item> doubleTransaction = new LinkedList<Item>();	//will replace transaction

			//get every combo of sub-transactions and build set of 2-sized ones
			while(transaction.size() > 1){
				Item a = transaction.pollFirst();
				if (frequentSingles.contains(a) == false){	//skip if a is not a frequent item
					continue;
				}
				//run through the rest of the items with an interator
				Iterator<Item> it = transaction.iterator();
				while(it.hasNext()){
					Item b = it.next();
					if (frequentSingles.contains(b) == false){	//skip if b is not a frequent item
						it.remove();	//remove item from transaction if it isn't frequent
						continue;
					}
					Item n = a.combine(b);
					if (n == null){
						continue;
					}else{
						//Got a new valid superset. add to frequency and to doubletransactions
						doubleTransaction.add(n);			//save superset in transaction
						int count = 1;
						if (doubleItems.containsKey(n)){
							count = doubleItems.get(n) + 1;
							doubleItems.put(n, count);	//save set count
						}else{
							doubleItems.put(n, 1);
						}
						if (count == minCount){
							frequentDoubles.add(n);		//save name of this frequent set
						}
					}
				}
			}
			if (doubleTransaction.isEmpty() == false){
				data.addLast(doubleTransaction);
			}
		}

		frequency.add(doubleItems);
		frequents.add(frequentDoubles);

		doubleTime = System.currentTimeMillis() - startTime;
		times.add(doubleTime);
	}

	static void countNextSize(){
		long startTime = System.currentTimeMillis();

		HashMap<Item, Integer> superCounts = new HashMap<Item, Integer>();	//save count of supersets
		HashSet<Item> frequentSupers = new HashSet<Item>();	//save names of frequent supersets
		
		HashSet<Item> frequentSmalls = frequents.get(frequents.size()-1);	//remember which subsets are frequent

		linecount = data.size();
		for (int p = 0; p < linecount; p++){	//run through each transaction
			LinkedList<Item> transaction = data.removeFirst();	//subsets in transaction

			LinkedList<Item> superTransaction = new LinkedList<Item>();	//will replace transaction as set of supersets

			//get every combo of sub-transactions and build set of super transactions
			while(transaction.size() > 1){
				Item a = transaction.pollFirst();
				if (frequentSmalls.contains(a) == false){	//skip if a is not a frequent subset
					//System.out.println("\ta failed");
					continue;
				}

				Iterator<Item> it = transaction.iterator();
				while(it.hasNext()){
					Item b = it.next();
					if (frequentSmalls.contains(b) == false){	//skip if b is not a frequent item
						it.remove();
						//System.out.println("\t\tb failed");
						continue;
					}
					Item n = a.combine(b);
					if (n == null){
						//System.out.println("failed merge");
						break;	//no need to continue looking at things that begin with a
					}else{
						//System.out.println("successful merge");
						superTransaction.add(n);			//save superset in transaction
						int count = 1;
						if (superCounts.containsKey(n)){
							count = superCounts.get(n) + 1;
							superCounts.put(n, count);	//save set count
						}else{
							superCounts.put(n, 1);
						}
						if (count == minCount){
							frequentSupers.add(n);		//save name of this frequent set
						}
					}
				}
			}
			if (superTransaction.isEmpty() == false){
				data.addLast(superTransaction);
			}
		}

		frequency.add(superCounts);
		frequents.add(frequentSupers);

		times.add(System.currentTimeMillis() - startTime);
	}

	//Load Data
	static void loadData(String filename){
		data = new LinkedList<LinkedList<Item>>();
		try{
			Scanner scan = new Scanner(new File(filename));
			//for (int i = 0; i < 5; i++) {
			while (scan.hasNext()){
				LinkedList<Item> nextLine = new LinkedList<Item>();
				String[] line = scan.nextLine().split(" ");
				for (String word: line){
					Item item = new Item();
					item.name = word;
					item.last = word;
					item.prefix = null;
					nextLine.add(item);
				//	System.out.print(word+" ");
				}
				//System.out.print("\n");
				data.add(nextLine);
				linecount = linecount + 1;
			}
		}catch (java.io.FileNotFoundException e){
			System.out.println("File not found");
		}
	}

	static void writeData(String filename){

		try{
			BufferedWriter writer = new BufferedWriter(new FileWriter(filename));

			// for (int i = 0; i < frequents.size();i++) {	//for each length
			// 	HashSet<Item> frequent = frequents.get(i);
			// 	HashMap<Item, Integer> counts = frequency.get(i);
				
			// 	Iterator<Item> it = frequent.iterator();
	  //    		while(it.hasNext()){
	  //    			Item item = it.next();
	  //    			item.count = counts.get(item);
	  //    			String s = item.name+" ("+item.count+")";
	  //    			writer.write(s+"\n");
			// 	}
			// }

			// //Sorted Output
			List<List<Integer>> output = new LinkedList<List<Integer>>();

			//build list of numberlists for each superset
			for (int i = 0; i < frequents.size();i++) {
				HashSet<Item> frequent = frequents.get(i);
				HashMap<Item, Integer> counts = frequency.get(i);
				
				Iterator<Item> it = frequent.iterator();
	     		while(it.hasNext()){
	     			Item item = it.next();
	     			int count = counts.get(item);
	     			List<Integer> numlist = new LinkedList<Integer>();
	     			String[] nums = item.name.split(" ");
	     			for (String num: nums){
	     				numlist.add(Integer.parseInt(num));
	     			}
	     			numlist.add(count);
	     			output.add(numlist);
				}
			}

			Collections.sort(output, new ListComparator<Integer>());

			 for (List<Integer> numlist: output){
     			for (int i = 0; i < numlist.size()-1; i++) {
     				writer.write(numlist.get(i) + " ");
     			}
		 		writer.write("("+numlist.get(numlist.size()-1)+")\n");
			 }

			writer.close();
		}catch (IOException e){
			System.out.println("Error writing to file");
		}

	
	}
}


class Item implements Comparable<Item>{
	String prefix;
	String last;
	String name;

	int[] values;

	int count = 1;	//increments when data is folded

	@Override
    public boolean equals(Object obj){
        if (this == obj) return true;
        if (!(obj instanceof Item)) return false;

        Item that = (Item)obj;
        return this.name.equals(that.name);    
    }

    @Override
    public int hashCode(){
        return name.hashCode();
    }

    @Override
    public int compareTo(Item that){
        //returns -1 if "this" object is less than "that" object
        //returns 0 if they are equal
        //returns 1 if "this" object is greater than "that" object

        return this.name.compareTo(that.name);
    }

    Item(){
    	//
    }

    Item combine(Item item){	//prefixes must be the same, and my 'last' must be before than theirs
    	if (this.prefix != item.prefix){
    		return null;
    	}else if (Integer.parseInt(this.last) >= Integer.parseInt(item.last)){	//other item must have greater last value
    		return null;
    	}else{
    		Item toReturn = new Item();
    		toReturn.prefix = this.name;
    		toReturn.last = item.last;
    		toReturn.name = this.name + " " + item.last;
    		return toReturn;
    	}
    }

}

class ListComparator<T extends Comparable<T>> implements Comparator<List<T>>{
	@Override
	public int compare(List<T> o1, List<T> o2) {
    	for (int i = 0; i < Math.min(o1.size(), o2.size()) - 1; i++) {
    		int c = o1.get(i).compareTo(o2.get(i));
    		if (c != 0) {
     			return c;
    		}
    	}
    	return Integer.compare(o1.size(), o2.size());
	}
}

