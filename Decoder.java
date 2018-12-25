
/**
	-----Decoder Java File-------
@author Karthikeyan Thorali Krishnmaurthy Ragunath
@version 1.0
@student ID 800936747

**/

import java.io.*;
import java.util.*;

public class Decoder {
	
	
	private static String File_Input = null;
	private static double MAX_TABLE_SIZE; //Max Table size is based on the bit length input.
	private static String LZWfilename;
	

	
	/* Decodes the the compressed file to a decoded input file. 
	 * @param file_Input2 //The name of compressed file.
	 * @param bit_Length  //Provided as user input.
	 * @throws IOException */


	private static void Decode_String(String file_Input2, double bit_Length) throws IOException {
		
		
		MAX_TABLE_SIZE = Math.pow(2, bit_Length);
		
		
		List<Integer> get_compress_values = new ArrayList<Integer>();
		int table_Size = 255;
		
		
		BufferedReader br = null;
		InputStream inputStream  = new FileInputStream(file_Input2);
		Reader inputStreamReader = new InputStreamReader(inputStream, "UTF-16BE"); // The Charset UTF-16BE is used to read the 16-bit compressed file.
	
		br = new BufferedReader(inputStreamReader);
		  
		double value=0;
		
         // reads to the end of the stream 
         while((value = br.read()) != -1)
         {
        	 get_compress_values.add((int) value);
         }
         	
         br.close();
         			
		Map<Integer, String> dictionary = new HashMap<Integer, String>();
		for (int i = 0; i < 255; i++)
			dictionary.put(i, "" + (char) i);

		String Encode_values = "" + (char) (int) get_compress_values.remove(0);
		
		StringBuffer decoded_values = new StringBuffer(Encode_values);
		
		String entry = null;
		for (int k : get_compress_values) {
			
			if (dictionary.containsKey(k))
				entry = dictionary.get(k);
			else if (k == table_Size)
				entry = Encode_values + Encode_values.charAt(0);
			
			decoded_values.append(entry);
			
			if(table_Size < MAX_TABLE_SIZE )
				dictionary.put(table_Size++, Encode_values + entry.charAt(0));

			Encode_values = entry;
		}
	
	Create_decoded_file(decoded_values.toString());



	}


	private static void Decode_String_dictionary(String file_Input2, String dictionary_file) throws IOException {

		List<Integer> get_compress_values = new ArrayList<>();

		BufferedReader br;
		InputStream inputStream  = new FileInputStream(file_Input2);
		Reader inputStreamReader = new InputStreamReader(inputStream, "UTF-16BE"); // The Charset UTF-16BE is used to read the 16-bit compressed file.

		br = new BufferedReader(inputStreamReader);

		double value=0;

		// reads to the end of the stream
		while((value = br.read()) != -1)
		{
			get_compress_values.add((int) value);
		}

		br.close();

		// load dictionary from file
		Map<Integer, String> dictionary = deserializeHashMap(dictionary_file);

		StringBuilder decoded_values = new StringBuilder();
		for (int k : get_compress_values) {
			decoded_values.append(dictionary.get(k));
		}
		Create_decoded_file(decoded_values.toString());
	}

/*
@param String , This hold the decoded text.
@throws IOException

*/

	private static void Create_decoded_file(String decoded_values) throws IOException {
        
		
		LZWfilename = File_Input.substring(0,File_Input.indexOf(".")) + "_decoded.txt";
		
		 FileWriter writer = new FileWriter(LZWfilename, true);
		 BufferedWriter bufferedWriter = new BufferedWriter(writer);
		
	
		try {
			
			bufferedWriter.write(decoded_values);
		
			}
		 catch (IOException e) {
			e.printStackTrace(); 
		}
		bufferedWriter.flush();
		
		bufferedWriter.close();	
	}
	
	private static HashMap<Integer, String> deserializeHashMap(String dictionary_file){

		HashMap<String, Integer> originalMap;
		HashMap<Integer, String> newMap = new HashMap<>();
		try
		{
			FileInputStream fis = new FileInputStream(dictionary_file);
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
		System.out.println("Loaded LZW dictionary from " + dictionary_file);

		// reverse dictionary for further searching in it
		for(Map.Entry<String, Integer> entry : originalMap.entrySet()){
			newMap.put(entry.getValue(), entry.getKey());
		}
		return newMap;

	}

	public static void main(String[] args) throws IOException {
		
		File_Input = args[0];
		if(args[1].equals("-d")){
			Decode_String_dictionary(File_Input, args[2]); // we don't need bit length, because we don't build dictionary
		}else if(args[1].equals("-b")){
			int Bit_Length = Integer.parseInt(args[2]);
			Decode_String(File_Input,Bit_Length);
		}else{
			System.out.println("Bad switch");
		}

		
	}

}