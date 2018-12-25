
/**
	-----Decoder Java File-------
@author Karthikeyan Thorali Krishnmaurthy Ragunath
@version 1.0
@student ID 800936747

**/

import java.io.*;
import java.util.*;

public class Decoder {
	
	
	private static String FILE_INPUT = null;
	private static double MAX_DICT_SIZE; //Max Dictionary size is based on the bit length input.
	private static String LZW_FILE_NAME;
	

	
	/** Decodes the the compressed file to a decoded input file.
	 * @param inputText //The name of compressed file.
	 * @param bitLength  //Provided as user input.
	 * @throws IOException */


	private static void decodeStandardLZW(String inputText, double bitLength) throws IOException {
		
		
		MAX_DICT_SIZE = Math.pow(2, bitLength);
		
		
		List<Integer> compressedValues = new ArrayList<Integer>();
		int tableSize = 255;
		

		BufferedReader br = null;
		InputStream inputStream  = new FileInputStream(inputText);
		Reader inputStreamReader = new InputStreamReader(inputStream, "UTF-16BE"); // The Charset UTF-16BE is used to read the 16-bit compressed file.

		br = new BufferedReader(inputStreamReader);
		  
		double value=0;
		
         // reads to the end of the stream 
         while((value = br.read()) != -1)
         {
        	 compressedValues.add((int) value);
         }
         	
         br.close();
         			
		Map<Integer, String> dictionary = new HashMap<Integer, String>();
		for (int i = 0; i < 255; i++)
			dictionary.put(i, "" + (char) i);

		String encodeValues = "" + (char) (int) compressedValues.remove(0);
		
		StringBuffer decodedValues = new StringBuffer(encodeValues);
		
		String entry = null;
		for (int k : compressedValues) {
			
			if (dictionary.containsKey(k))
				entry = dictionary.get(k);
			else if (k == tableSize)
				entry = encodeValues + encodeValues.charAt(0);
			
			decodedValues.append(entry);
			
			if(tableSize < MAX_DICT_SIZE)
				dictionary.put(tableSize++, encodeValues + entry.charAt(0));

			encodeValues = entry;
		}
	
	createDecodedFile(decodedValues.toString());



	}


	private static void decodeWithDictionary(String inputText, String dictionaryFilePath) throws IOException {

		List<Integer> compressedValues = new ArrayList<>();

		BufferedReader br;
		InputStream inputStream  = new FileInputStream(inputText);
		Reader inputStreamReader = new InputStreamReader(inputStream, "UTF-16BE"); // The Charset UTF-16BE is used to read the 16-bit compressed file.

		br = new BufferedReader(inputStreamReader);

		double value=0;

		// reads to the end of the stream
		while((value = br.read()) != -1)
		{
			compressedValues.add((int) value);
		}

		br.close();

		// load dictionary from file
		Map<Integer, String> dictionary = loadDictionary(dictionaryFilePath);

		StringBuilder decodedValues = new StringBuilder();
		for (int k : compressedValues) {
			decodedValues.append(dictionary.get(k));
		}
		createDecodedFile(decodedValues.toString());
	}

/*
@param String , This hold the decoded text.
@throws IOException

*/

	private static void createDecodedFile(String decodedValues) throws IOException {
        
		
		LZW_FILE_NAME = FILE_INPUT.substring(0, FILE_INPUT.indexOf(".")) + "_decoded.txt";
		
		 FileWriter writer = new FileWriter(LZW_FILE_NAME, true);
		 BufferedWriter bufferedWriter = new BufferedWriter(writer);
		
	
		try {
			
			bufferedWriter.write(decodedValues);
		
			}
		 catch (IOException e) {
			e.printStackTrace(); 
		}
		bufferedWriter.flush();
		
		bufferedWriter.close();	
	}
	
	private static HashMap<Integer, String> loadDictionary(String dictionaryFilePath){

		HashMap<String, Integer> originalMap;
		HashMap<Integer, String> newMap = new HashMap<>();
		try
		{
			FileInputStream fis = new FileInputStream(dictionaryFilePath);
			ObjectInputStream ois = new ObjectInputStream(fis);
			originalMap = (HashMap) ois.readObject();
			ois.close();
			fis.close();
		}catch(IOException ioe)
		{
			ioe.printStackTrace();
			return null;
		}catch(ClassNotFoundException c)
		{
			System.out.println("Class not found");
			c.printStackTrace();
			return null;
		}
		System.out.println("Loaded LZW dictionary from " + dictionaryFilePath);

		// reverse dictionary for further searching in it
		for(Map.Entry<String, Integer> entry : originalMap.entrySet()){
			newMap.put(entry.getValue(), entry.getKey());
		}
		return newMap;

	}

	public static void main(String[] args) throws IOException {
		
		FILE_INPUT = args[0];
		if(args[1].equals("-d")){
			decodeWithDictionary(FILE_INPUT, args[2]); // we don't need bit length, because we don't build dictionary
		}else if(args[1].equals("-b")){
			int bitLength = Integer.parseInt(args[2]);
			decodeStandardLZW(FILE_INPUT,bitLength);
		}else{
			System.out.println("Bad switch");
		}

		
	}

}