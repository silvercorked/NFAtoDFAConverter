import java.util.List;

public class State<V> {
	private V name;
	private List<Tuple<Character, State<V>>> transitions;
	private boolean isFinal;
	
	public State(V name, List<Tuple<Character, State<V>>> transitions, boolean isFinal) {
		this.name = name;
		this.transitions = transitions;
		this.isFinal = isFinal;
	}
	public V getName() {
		return this.name;
	}
	public List<Tuple<Character, State<V>>> getTransitions() {
		return this.transitions;
	}
	public boolean isAFinalState() {
		return this.isFinal;
	}
	public State<V> next(Character c) {
		State<V> nextState = null;
		for(Tuple<Character, State<V>> item : this.transitions)
			if (item.getFirst().equals(c))
				nextState = item.getSecond();
		return nextState;
	}
	public void setName(V name) {
		this.name = name;
	}
	public void setTransitions(List<Tuple<Character, State<V>>> transitions) {
		this.transitions = transitions;
	}
	public void setIsFinal(boolean isFinal) {
		this.isFinal = isFinal;
	}

	@Override
	public String toString() {
		return this.name.toString();
	}
	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof State) {
			State<V> a = (State<V>) obj;
			return this.getName().equals(a.getName());
		}
		return false;
	}
}

