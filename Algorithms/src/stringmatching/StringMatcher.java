package stringmatching;

import java.util.Map;
import java.util.HashMap;

public class StringMatcher {

	public static int finiteAutomaton(String text, String pattern) {
		Map< Map<Integer,Character>, Integer> transitions = computerTransitions();
		return transitions.get(null);
	}
	
	/**
	 * preprocessing process: map { current state, next char } to next state
	 * @return the transition functions
	 */
	private static Map< Map<Integer,Character>, Integer> computerTransitions() {
		Map< Map<Integer,Character>, Integer> transitions = new HashMap<>();
		return transitions;
	}
}
