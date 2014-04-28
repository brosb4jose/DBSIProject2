import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.*;

public class SplashTable {
	static private int bucketSize;
	static private int reinsertions;
	static private int numberOfHashFunctions;
	static private int S;

	static private String inputFileName;
	static private String dumpFileName;

	static private int[] arrayOfA;
	
	static int numberOfRecords = 0;

	static private ArrayList<LinkedList<Pair>> splashTable;

	public SplashTable(int B, int R, int S, int H) {

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// Input validation
		System.err.println(Arrays.toString(args));
		if (args.length != 6) {
			System.err.println("Invalid amount of arguments");
			return;
		}
		inputFileName = args[4];
		dumpFileName = args[5];

		try {
			bucketSize = Integer.parseInt(args[0]);
			reinsertions = Integer.parseInt(args[1]);
			S = Integer.parseInt(args[2]);
			numberOfHashFunctions = Integer.parseInt(args[3]);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		arrayOfA = new double[numberOfHashFunctions];
        Random rand = new Random();
		for (int i = 0; i < numberOfHashFunctions; i++) {
			int temp = rand.nextInt();
			while (temp == 0.0 || temp%2==0) {
				temp = rand.nextInt();
			}
			arrayOfA[i] = temp;
		}
		splashTable = new ArrayList<LinkedList<Pair>>((int) Math.pow(2, S)
				/ bucketSize);
		for (int i = 0; i < (int) Math.pow(2, S) / bucketSize; i++) {
			splashTable.add(new LinkedList<Pair>());
		}
		build();
		dump();
		probe();
	}

	private static void build() {
		//
		File inputFile = new File(inputFileName);
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(inputFile));
			String line = null;
			while ((line = reader.readLine()) != null) {
				String[] keyValuePair = line.split(" ");
				if (keyValuePair.length != 2) {
					System.err.println("invalid inputfile format");
					System.exit(0);
				}
				int key = Integer.parseInt(keyValuePair[0]);
				int value = Integer.parseInt(keyValuePair[1]);
				int insertionTime = 0;
				while (insertionTime < reinsertions) {
					int indexOfSmallestBucket = -1;
					int sizeOfSmallestBucket = bucketSize + 1;

					// to find the smallest bucket
					for (double h : arrayOfA) {
						int bucketNum = multHashing(key, h);
						int sizeOfBucket = splashTable.get(bucketNum).size();
						if (sizeOfBucket < sizeOfSmallestBucket) {
							indexOfSmallestBucket = bucketNum;
							sizeOfSmallestBucket = sizeOfBucket;						}
					}

					// check if the smallest one is full

					if (sizeOfSmallestBucket == bucketSize) {
						Pair firstPair = splashTable.get(indexOfSmallestBucket)
								.removeFirst();
						Pair insertPair = new Pair(key, value);
						splashTable.get(indexOfSmallestBucket).add(insertPair);
						key = firstPair.key;
						value = firstPair.value;
					} else {
						Pair insertPair = new Pair(key, value);
						splashTable.get(indexOfSmallestBucket).add(insertPair);
						break;
					}

					// insert the key into the bucket
					// break;

					insertionTime++;
				}
				//System.out.println("reinsertion: "+insertionTime);
				// check the reinsertion time
				if (insertionTime >= reinsertions) {
					System.err.println("Build failed");
					dump();
					System.exit(0);
				}
				numberOfRecords++;
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static void dump() {
		File dumpFile = new File(dumpFileName);
		// if file doesnt exists, then create it
		if (!dumpFile.exists()) {
			try {
				dumpFile.createNewFile();
				FileWriter fw = new FileWriter(dumpFile.getAbsoluteFile());
				BufferedWriter bw = new BufferedWriter(fw);
				bw.write(""+bucketSize+" "+S+" "+numberOfHashFunctions+" "+numberOfRecords);
				bw.newLine();
				
				//write the hash function multiplier
				for(double a : arrayOfA){
					bw.write(""+a+" ");
				}
				bw.newLine();
				
				// write the records
				for(LinkedList<Pair> list : splashTable){
					int sizeOfList = list.size();
					for(Pair pair : list){
						bw.write(""+pair.key+" "+pair.value);
						bw.newLine();
					}
					for(int i= 0; i < bucketSize-sizeOfList; i++){
						bw.write("0 0");
						bw.newLine();
					}
				}
				
				bw.close();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	private static int multHashing(int key, double A) {
		double step1 = A * key;
		double step2 = step1 - Math.floor(step1);
		double step3 = step2 * ((int) Math.pow(2, S) / bucketSize);
		return (int) Math.floor(step3);
	}
	
	private static void probe(){
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(System.in));
			String line = null;
			ArrayList<Pair> found = new ArrayList<Pair>();
			//used while to read the File
			while ((line = reader.readLine()) != null) {
				//System.out.println(line);
				int payload = 0;
				int key = Integer.parseInt(line);
				for(double h :  arrayOfA){
					int indexOfBucket = multHashing(key,h);
					LinkedList<Pair> bucket = splashTable.get(indexOfBucket);
					for(Pair p : bucket){
						//int isEqual = (p.key  key)^0xFFFF;
						//int tempPayload = p.value & isEqual;
						//payload = payload | tempPayload;
						int mask = p.key == key ? 0xFFFFFFFF : 0x00000000;
						int tempPayload = mask & p.value;
						payload = payload | tempPayload;	
					}
				}
				System.out.println(""+key+" "+payload);
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
