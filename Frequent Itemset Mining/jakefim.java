//THIS CODE IS MY OWN WORK AND WAS DONE WITHOUT CONSULTING A TUTOR OR CODE WRITTEN BY OTHER STUDENTS
// - JAKE CRONIN 

import java.io.*;
import java.util.*;

public class jakefim{

	static LinkedList<LinkedList<Item>> data;	//Transactions<Items Per Transaction>

	static ArrayList<HashMap<String, Item>> frequents;	//holds all frequent items

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
		frequents = new ArrayList<HashMap<String, Item>>();				//holds names of frequent sets
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

		HashMap<String, Item> singleItems = new HashMap<String, Item>();	//remember unique items, trashed after each iteration
		HashMap<String, Item> frequentItems = new HashMap<String, Item>();	//remember items that pass min threshold

		int c = 0;
		for (LinkedList<Item> transaction: data){	//for each transaction
		
			for (Item item: transaction){ 	//for each item in the transaction
				if (singleItems.containsKey(item.name)){
					Item original = singleItems.get(item.name);
					original.count = original.count + 1;	//increment count of repeat items
					singleItems.put(original.name, original);
					if (original.count == minCount){
						frequentItems.put(item.name, item);
					}
				}else{
					singleItems.put(item.name, item);	//add item into list
					if (minCount == 1){
						frequentItems.put(item.name, item);
					}
				}
			}
		}
		frequents.add(frequentItems);	//save frequent items

		singleTime = System.currentTimeMillis() - startTime;
		times.add(singleTime);	//record time
	}

	static void countDoubleItems(){
		long startTime = System.currentTimeMillis();

		HashMap<String, Item> doubleItems = new HashMap<String, Item>();	//save count of double items
		HashMap<String, Item> frequentDoubles = new HashMap<String, Item>();	//save names of frequent items

		HashMap<String, Item> frequentSingles = frequents.get(0);	//reference frequent itemsets of size 1

		int size = data.size();
		for (int p = 0; p < size; p++){	//for each transaction
			LinkedList<Item> transaction = data.pollFirst();	//single items in transaction
			LinkedList<Item> doubleTransaction = new LinkedList<Item>();	//condensed transaction

			//get every combo of sub-transactions and build set of 2-sized ones
			while(transaction.size() > 1){
				Item a = transaction.pollFirst();
				if (frequentSingles.containsKey(a.name) == false){	//skip if a is not a frequent item
					continue;
				}
				//go through all combos of item a with other items in this transaction
				Iterator<Item> it = transaction.iterator();
				while(it.hasNext()){
					Item b = it.next();
					if (frequentSingles.containsKey(b.name) == false){	//skip if b is not a frequent item
						it.remove();	//remove item from transaction if it isn't frequent
						continue;
					}
					Item n = a.combine(b);
					if (n == null){
						continue;
					}else{
						//Got a new valid superset. add to frequency and to doubletransactions
						doubleTransaction.add(n);			//save superset in transaction
						if (doubleItems.containsKey(n.name)){
							Item original = doubleItems.get(n.name);
							original.count = original.count + 1;
							if (original.count == minCount){
								frequentDoubles.put(original.name, original);	//save item as frequent
							}
						}else{
							doubleItems.put(n.name, n);
							if (minCount == 1){
								frequentDoubles.put(n.name, n);
							}
						}
					}
				}
			}
			if (doubleTransaction.isEmpty() == false){
				data.addLast(doubleTransaction);	//put non-empty transactions back for future combining
			}
		}

		frequents.add(frequentDoubles);

		doubleTime = System.currentTimeMillis() - startTime;
		times.add(doubleTime);
	}

	static void countNextSize(){
		long startTime = System.currentTimeMillis();

		HashMap<String, Item> superCounts = new HashMap<String, Item>();	//save count of supersets
		HashMap<String, Item> frequentSupers = new HashMap<String, Item>();	//save names of frequent supersets
		
		HashMap<String, Item> frequentSmalls = frequents.get(frequents.size()-1);	//remember which subsets are frequent

		linecount = data.size();
		for (int p = 0; p < linecount; p++){	//run through each transaction
			LinkedList<Item> transaction = data.removeFirst();	//subsets in transaction
			LinkedList<Item> superTransaction = new LinkedList<Item>();	//will replace transaction as set of supersets

			//get every combo of sub-transactions and build set of super transactions
			while(transaction.size() > 1){
				Item a = transaction.pollFirst();
				if (frequentSmalls.containsKey(a.name) == false){	//skip if a is not a frequent subset
					continue;
				}

				Iterator<Item> it = transaction.iterator();
				while(it.hasNext()){
					Item b = it.next();
					if (frequentSmalls.containsKey(b.name) == false){	//skip if b is not a frequent item
						it.remove();
						continue;
					}
					Item n = a.combine(b);
					if (n == null){
						break;	//no need to continue looking at things that begin with a
					}else{
						superTransaction.add(n);			//save superset in transaction
						if (superCounts.containsKey(n.name)){
							Item original = superCounts.get(n.name);
							original.count = original.count + 1;
							if (original.count == minCount){
								frequentSupers.put(original.name, original);
							}
						}else{
							superCounts.put(n.name, n);
							if (minCount == 1){
								frequentSupers.put(n.name, n);
							}
						}
					}
				}
			}
			if (superTransaction.isEmpty() == false){
				data.addLast(superTransaction);
			}
		}

		frequents.add(frequentSupers);

		times.add(System.currentTimeMillis() - startTime);
	}

	//Load Data
	static void loadData(String filename){
		data = new LinkedList<LinkedList<Item>>();
		try{
			Scanner scan = new Scanner(new File(filename));
			while (scan.hasNext()){
				LinkedList<Item> nextLine = new LinkedList<Item>();
				String[] line = scan.nextLine().split(" ");
				for (String word: line){
					Item item = new Item();
					item.name = word;
					item.last = word;
					item.prefix = null;
					nextLine.add(item);
				}
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
			// 	Iterator<Item> it = frequent.iterator();
	  //    		while(it.hasNext()){
	  //    			Item item = it.next();
	  //    			String s = item.name+" ("+item.count+")";
	  //    			writer.write(s+"\n");
			// 	}
			// }

			// //Sorted Output
			List<List<Integer>> output = new LinkedList<List<Integer>>();

			//build list of numberlists for each superset
			for (int i = 0; i < frequents.size();i++) {
				HashMap<String, Item> frequent = frequents.get(i);
				
				Iterator<Item> it = frequent.values().iterator();
	     		while(it.hasNext()){
	     			Item item = it.next();
	     			List<Integer> numlist = new LinkedList<Integer>();
	     			String[] nums = item.name.split(" ");
	     			for (String num: nums){
	     				numlist.add(Integer.parseInt(num));
	     			}
	     			numlist.add(item.count);
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

	int count;	//increments when data is folded

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
    	this.count = 1;
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

