import java.util.List;

public class NFA {
	public List<State<String>> states;
	public List<Character> inputAlphabet;
	public State<String> q0;
	public List<State<String>> finalStates;
	public NFA(List<State<String>> states, List<Character> inputAlphabet, State<String> q0, List<State<String>> finalStates) {
		this.states = states;
		this.inputAlphabet = inputAlphabet;
		this.q0 = q0;
		this.finalStates = finalStates;
	}
}