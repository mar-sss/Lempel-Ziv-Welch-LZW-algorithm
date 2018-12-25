
/**
       -----Encoder Java File-------
@author Karthikeyan Thorali Krishnmaurthy Ragunath
@version 1.0
@student ID 800936747

**/
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Encoder {
	
	private static String File_Input = null;
	private static double MAX_TABLE_SIZE; //Max Table size is based on the bit length input.
	private static String LZWfilename;
	

	/** Compress a string to a list of output symbols and then pass it for compress file creation.
	 * @param Bit_Length //Provided as user input.
	 * @param input_string //Filename that is used for encoding.
	 * @throws IOException */
	
	private static void Encode_string(String input_string, double Bit_Length) throws IOException {

		MAX_TABLE_SIZE = Math.pow(2, Bit_Length);
			
		double table_Size =  255;
		
		HashMap<String, Integer> dictionary = new HashMap<>();

		for (int i = 0; i < 255 ; i++)
			dictionary.put("" + (char) i, i);

		String w = "";
		
		List<Integer> encoded_values = new ArrayList<>();
		
		for (char c : input_string.toCharArray()) {
			String wc = w + c;
			if (dictionary.containsKey(wc))
				w = wc;
			else {
				encoded_values.add(dictionary.get(w));
			
				if(table_Size < MAX_TABLE_SIZE)
					dictionary.put(wc, (int) table_Size++);
				w = "" + c;
			}
		}

		if (!w.equals(""))
			encoded_values.add(dictionary.get(w));
		
		CreateLZWfile("noDict", encoded_values);

		//my new method:
        encodeByDictionary(input_string, dictionary);
	}


    private static void encodeByDictionary(String text, HashMap<String, Integer> dictionary) throws IOException{
        // iterate through text: when there is match in dictionary, read one more char
        // When is no longer match, write code of the last match
        // when we are at the end of the string, write match and end
        // save the code to file.

        StringBuilder sb = new StringBuilder();
        sb.append(text.charAt(0));
        String previousSequence;
        List<Integer> encoded_values = new ArrayList<>();
        HashMap<String, Integer> newDictionary = new HashMap<>();
        char lastChar;
        int i = 1;
        while (i<text.length()){
            previousSequence = sb.toString();
            sb.append(text.charAt(i));
            i++;
            if (!dictionary.containsKey(sb.toString())){
                encoded_values.add(dictionary.get(previousSequence));
                newDictionary.put(previousSequence, dictionary.get(previousSequence));
                lastChar = sb.charAt(sb.length()-1);
                sb = new StringBuilder();
                sb.append(lastChar);
            }
        }

        encoded_values.add(dictionary.get(sb.toString())); //last iteration because of while
        newDictionary.put(sb.toString(), dictionary.get(sb.toString()));
        serializeHashMap(newDictionary);

        CreateLZWfile("withDict", encoded_values);
    }

/*
@param encoded_values , This hold the encoded text.
@throws IOException
*/

	private static void CreateLZWfile(String name, List<Integer> encoded_values) throws IOException {
		
		BufferedWriter out = null;
		
		LZWfilename = File_Input.substring(0,File_Input.indexOf(".")) + "_" + name + ".lzw";
		
		try {
	            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(LZWfilename),"UTF_16BE")); //The Charset UTF-16BE is used to write as 16-bit compressed file
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			Iterator<Integer> Itr = encoded_values.iterator();
			while (Itr.hasNext()) {
				out.write(Itr.next());
			}
		} catch (IOException e) {
			e.printStackTrace(); 
		}
		
		out.flush();
		out.close();	
	}

	private static void serializeHashMap(HashMap<String, Integer> hmap){
        String dictFileName = File_Input.substring(0,File_Input.indexOf(".")) + ".dict";


        try
        {
            FileOutputStream fos =
                    new FileOutputStream(dictFileName);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(hmap);
            oos.close();
            fos.close();
            System.out.printf("LZW dictionary is saved in " + dictFileName);
        }catch(IOException ioe)
        {
            ioe.printStackTrace();
        }

    }




	public static void main(String[] args) throws IOException {
				
		File_Input = args[0];
		int Bit_Length = Integer.parseInt(args[1]);
		
		StringBuffer input_string1 = new StringBuffer();
		
		try (BufferedReader br = Files.newBufferedReader(Paths.get(File_Input), StandardCharsets.UTF_8)) {
		    for (String line = null; (line = br.readLine()) != null;) {
		        
		    	input_string1 = input_string1.append(line);
		    }
		}
	
		Encode_string(input_string1.toString(),Bit_Length);
			
	}
}

// TODO serialize dictionary
// TODO make second encoding based on lookup on longest match dictionary
// TODO adaptive bit coding based on needs?