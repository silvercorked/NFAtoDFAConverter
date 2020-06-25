import java.util.List;
import java.util.Set;

public class DFA {
	public List<State<Set<String>>> states;
	public List<Character> inputAlphabet;
	public State<Set<String>> q0;
	public List<State<Set<String>>> finalStates;
	public DFA(List<State<Set<String>>> states, List<Character> inputAlphabet, State<Set<String>> q0, List<State<Set<String>>> finalStates) {
		this.states = states;
		this.inputAlphabet = inputAlphabet;
		this.q0 = q0;
		this.finalStates = finalStates;
	}

	@Override
	public String toString() {
		String states = this.states.stream()
			.map(state -> state.getName().toString())
			.reduce("", (partial, nextStateString) -> partial + nextStateString + "\n");
		String inputAlphabet = this.inputAlphabet.stream()
			.map(character -> character.toString())
			.reduce("", (partial, nextCharacter) -> partial + nextCharacter + "\n");
		String finalStates = this.finalStates.stream()
			.map(state -> state.getName().toString())
			.reduce("", (partial, nextStateString) -> partial + nextStateString + "\n");
		String q0 = this.q0.toString() + "\n";
		String transitions = this.states.stream()
			.map(state -> state.getTransitions().stream()
				.map(transitionTuple -> "" + state + " " + transitionTuple.getFirst().toString() + " " + transitionTuple.getSecond().toString())
				.reduce("", (partial, nextTransitionString) -> partial + nextTransitionString + "\n")
			).reduce("", (partial, nextTransitionStrings) -> partial + nextTransitionStrings);
		return String.format("%s\n%s%s\n%s%s\n%s%s\n%s%s\n%s",
			"% States", states, "% Input Alphabet", inputAlphabet, "% Final States",
			finalStates, "% Initial State", q0, "% Transition Functions", transitions);
	}
}
