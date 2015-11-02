package cloudapp;

import java.io.File;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Stream;

public class MP1 {
    Random generator;
    String userName;
    String inputFileName;
    String delimiters = "[ \t,;.?!-:@\\[\\](){}_*/]";
    String[] stopWordsArray = {"i", "me", "my", "myself", "we", "our", "ours", "ourselves", "you", "your", "yours",
            "yourself", "yourselves", "he", "him", "his", "himself", "she", "her", "hers", "herself", "it", "its",
            "itself", "they", "them", "their", "theirs", "themselves", "what", "which", "who", "whom", "this", "that",
            "these", "those", "am", "is", "are", "was", "were", "be", "been", "being", "have", "has", "had", "having",
            "do", "does", "did", "doing", "a", "an", "the", "and", "but", "if", "or", "because", "as", "until", "while",
            "of", "at", "by", "for", "with", "about", "against", "between", "into", "through", "during", "before",
            "after", "above", "below", "to", "from", "up", "down", "in", "out", "on", "off", "over", "under", "again",
            "further", "then", "once", "here", "there", "when", "where", "why", "how", "all", "any", "both", "each",
            "few", "more", "most", "other", "some", "such", "no", "nor", "not", "only", "own", "same", "so", "than",
            "too", "very", "s", "t", "can", "will", "just", "don", "should", "now"};
    Map<String,Integer> hashedStopWords = hashStopWords();
    
    void initialRandomGenerator(String seed) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA");
        messageDigest.update(seed.toLowerCase().trim().getBytes());
        byte[] seedMD5 = messageDigest.digest();

        long longSeed = 0;
        for (int i = 0; i < seedMD5.length; i++) {
            longSeed += ((long) seedMD5[i] & 0xffL) << (8 * i);
        }

        this.generator = new Random(longSeed);
    }

	Integer[] getIndexes() throws NoSuchAlgorithmException {
        Integer n = 10000;
        Integer number_of_lines = 50000;
        Integer[] ret = new Integer[n];
        this.initialRandomGenerator(this.userName);
        for (int i = 0; i < n; i++) {
            ret[i] = generator.nextInt(number_of_lines);
        }
        return ret;
    }

    public MP1(String userName, String inputFileName) {
        this.userName = userName;
        this.inputFileName = inputFileName;
    }

    public String[] process() throws Exception {
       
        URL url = MP1.class.getResource(inputFileName);
        List<String> lines = Files.readAllLines( new File( url.toURI() ).toPath() );
        
        Integer[] interestedIndice = getIndexes();
        Map<String, Integer> wordStatistics = new HashMap<>();
        
        for ( int i= 0; i<interestedIndice.length; i++ ) {
        	String line = ( (ArrayList<String>)lines ).get(i);
        	for (String word : line.split( delimiters )) {
        		word = word.trim().toLowerCase();
        		if ( word.length() > 0 && ! isStopWord(word) ) {
        			Integer freq = wordStatistics.get(word);
        			wordStatistics.put(word, freq == null ? 1 : freq + 1);
        		}
        	}
        }
        
        // Convert Map to List
 		List<Map.Entry<String, Integer>> list = new LinkedList<>( wordStatistics.entrySet());

 		// Sort list with comparator, to compare the Map values
 		Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
 			public int compare( Map.Entry<String, Integer> o1,
                                Map.Entry<String, Integer> o2 ) {
 				return (o2.getValue()).compareTo(o1.getValue()); // descending order
 			}
 		});

 		// Convert sorted list back to a Map： must be linked to ensure element order
// 		Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
// 		for ( Iterator<Map.Entry<String, Integer>> it = list.iterator(); it.hasNext();) {
// 			Map.Entry<String, Integer> entry = it.next();
// 			sortedMap.put(entry.getKey(), entry.getValue());
// 		}
 		
 		String[] top20 = new String[20];
 		int i = 0;
 		PrintWriter pw = new PrintWriter(new File("src/cloudapp/output.txt"));		
		for ( Map.Entry<String, Integer> entry : list.subList(0, 20) ) {
 			top20[i] = entry.getKey(); // + entry.getValue();
 			i++;
 			pw.println(entry.getKey());
 		}        
		pw.close();
        return top20;
    }
    
    private Map<String, Integer> hashStopWords() {
		Map<String, Integer> hash = new HashMap<>();
		for ( String s : stopWordsArray) {
			hash.put(s, 1);
		}
		return hash;
	}

    private boolean isStopWord(String word) {
		return hashedStopWords.containsKey(word);
	}

	public static StringBuilder sbToLowerCase(StringBuilder pText) {
        StringBuilder pTextLower = new StringBuilder(pText);
        for (int idx = 0; idx < pText.length(); idx++) {
            char c = pText.charAt(idx);
            if (c >= 65 && c <= 65 + 27) {
                pTextLower.setCharAt(idx, (char) ((int) (pText.charAt(idx)) | 32));
            }
        }
        return pTextLower;
	}
	
	public static <K, V extends Comparable<? super V>> Map<K, V> sortMapByValue( Map<K, V> map ) {
		Map<K, V> result = new LinkedHashMap<>();
		Stream<Entry<K, V>> st = map.entrySet().stream();

		st.sorted( Comparator.comparing( e -> e.getValue()))
		  .forEach( e -> result.put(e.getKey(), e.getValue()));

		return result;
	}

    public static void main(String[] args) throws Exception {
        if (args.length < 1){
            System.out.println("MP1 <User ID>");
        }
        else {
            String userName = args[0];
            String inputFileName = "./input.txt";
            MP1 mp = new MP1(userName, inputFileName);
            String[] topItems = mp.process();
            for (String item: topItems){
                System.out.println(item);
            }
        }
    }
}