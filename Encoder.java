
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
	
	private static String FILE_INPUT = null;
	private static double MAX_DICT_SIZE; //Max Dictionary size is based on the bit length input.
	private static String LZW_FILE_NAME;
	

	/** Compress a string text (by standard LZW and enhanced LZW) to a list of output symbols and then pass it for compress file creation.
	 * @param bitLength //Provided as user input.
	 * @param inputText //Filename that is used for encoding.
	 * @throws IOException */
	
	private static void encodeStandardLZW(String inputText, double bitLength) throws IOException {

		MAX_DICT_SIZE = Math.pow(2, bitLength);
			
		double tableSize =  255;
		
		HashMap<String, Integer> dictionary = new HashMap<>();

		for (int i = 0; i < 255 ; i++)
			dictionary.put("" + (char) i, i);

		String w = "";
		
		List<Integer> encodedValues = new ArrayList<>();
		
		for (char c : inputText.toCharArray()) {
			String wc = w + c;
			if (dictionary.containsKey(wc))
				w = wc;
			else {
				encodedValues.add(dictionary.get(w));
			
				if(tableSize < MAX_DICT_SIZE)
					dictionary.put(wc, (int) tableSize++);
				w = "" + c;
			}
		}

		if (!w.equals(""))
			encodedValues.add(dictionary.get(w));
		
		CreateLZWfile("noDict", encodedValues);

		//my new method:
        encodeWithDictionary(inputText, dictionary);
	}


    private static void encodeWithDictionary(String text, HashMap<String, Integer> dictionary) throws IOException{
        // iterate through text: when there is match in dictionary, read one more char
        // When is no longer match, write code of the last match
        // when we are at the end of the string, write match and end
        // save the code to file.

        StringBuilder sb = new StringBuilder();
        sb.append(text.charAt(0));
        String previousSequence;
        List<Integer> encodedValues = new ArrayList<>();
        HashMap<String, Integer> newDictionary = new HashMap<>();
        char lastChar;
        int i = 1;
        while (i<text.length()){
            previousSequence = sb.toString();
            sb.append(text.charAt(i));
            i++;
            if (!dictionary.containsKey(sb.toString())){
                encodedValues.add(dictionary.get(previousSequence));
                newDictionary.put(previousSequence, dictionary.get(previousSequence));
                lastChar = sb.charAt(sb.length()-1);
                sb = new StringBuilder();
                sb.append(lastChar);
            }
        }

        encodedValues.add(dictionary.get(sb.toString())); //last iteration because of while
        newDictionary.put(sb.toString(), dictionary.get(sb.toString()));
        saveDictionary(newDictionary);

        CreateLZWfile("withDict", encodedValues);
    }

    /**
    * @param encodedValues , This hold the encoded text.
    * @throws IOException */

	private static void CreateLZWfile(String name, List<Integer> encodedValues) throws IOException {
		
		BufferedWriter out = null;
		
		LZW_FILE_NAME = FILE_INPUT.substring(0, FILE_INPUT.indexOf(".")) + "_" + name + ".lzw";
		
		try {
	            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(LZW_FILE_NAME),"UTF_16BE")); //The Charset UTF-16BE is used to write as 16-bit compressed file
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			Iterator<Integer> Itr = encodedValues.iterator();
			while (Itr.hasNext()) {
				out.write(Itr.next());
			}
		} catch (IOException e) {
			e.printStackTrace(); 
		}
		
		out.flush();
		out.close();	
	}

	private static void saveDictionary(HashMap<String, Integer> hmap){
        String dictFileName = FILE_INPUT.substring(0, FILE_INPUT.indexOf(".")) + ".dict";


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
				
		FILE_INPUT = args[0];
		int bitLength = Integer.parseInt(args[1]);
		
		StringBuffer inputText = new StringBuffer();
		
		try (BufferedReader br = Files.newBufferedReader(Paths.get(FILE_INPUT), StandardCharsets.UTF_8)) {
		    for (String line = null; (line = br.readLine()) != null;) {
		        
		    	inputText = inputText.append(line);
		    }
		}
	
		encodeStandardLZW(inputText.toString(),bitLength);
			
	}
}

// TODO serialize dictionary = DONE
// TODO make second encoding based on lookup on longest match dictionary DONE
    // TODO make compress method with switch argument DONE
    // TODO save dictionary with just used entries DONE
// TODO adaptive bit coding based on needs?
// TODO download books in txt and experiment with new compress method

// TODO Decoder, save dictionary only with actually used entries

// TODO dokumentace - ovecny popis LZW, popis algoritmu, popis toho co se stane kdyz dojde misto v tabulce, popis jak se ukladaji zakodovane sekvence, moje vylepseni, samotny experiment, vysledky