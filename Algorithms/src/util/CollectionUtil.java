package util;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;
import java.util.TreeMap;

public class CollectionUtil {

	public static StringBuilder lowerCaseStringBUilder(StringBuilder pText) {
        StringBuilder pTextLower = new StringBuilder(pText);
        for (int idx = 0; idx < pText.length(); idx++) {
            char c = pText.charAt(idx);
            if (c >= 65 && c <= 65 + 27) {
                pTextLower.setCharAt(idx, (char) ((int) (pText.charAt(idx)) | 32));
            }
        }
        return pTextLower;
	}
	
	public static <K,V extends Comparable<? super V>> Map<K,V> sortMapByKey(Map<K,V> map) {
		return new TreeMap<K,V>(map);
	}
	
	public static <K, V extends Comparable<? super V>> Map<K, V> sortMapByValue(
							Map<K, V> map, boolean java7) {
		List<Map.Entry<K, V>> list = new LinkedList<>(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
			@Override
			public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
				return (o1.getValue()).compareTo(o2.getValue());
			}
		});

		Map<K, V> result = new LinkedHashMap<>();
		for (Map.Entry<K, V> entry : list) {
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}

	// java 8 style
	public static <K, V extends Comparable<? super V>> Map<K, V> sortMapByValue( Map<K, V> map ) {
		Map<K, V> result = new LinkedHashMap<>();
		Stream<Entry<K, V>> stream = map.entrySet().stream();

		stream.sorted( Comparator.comparing( e -> e.getValue()))
		      .forEach( e -> result.put(e.getKey(), e.getValue()));

		return result;
	}
	
}
