import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

public class NFAConvert {
	public static void main(String ... args) {
		if (args.length == 1) {
			String initializationFile = args[0];
			NFA nfa;
			try { // read file
				System.out.println("initializing NFA");
				nfa = initializeNFA(new Scanner(new File(initializationFile)));
				System.out.println("NFA initialized");
			} catch(FileNotFoundException e) { // file was missing
				System.out.println("the file '" + initializationFile + "'could not be opened");
				return;
			}
			System.out.println("Starting to convert from NFA to DFA");
			DFA dfa = convertNFAtoDFA(nfa);
			System.out.println("Converted NFA to DFA");
			BufferedWriter bw = null;
			try {
				String s = "output-for-" + initializationFile;
				File file = new File(s);
				if (!file.exists())
					file.createNewFile();
				bw = new BufferedWriter(new FileWriter(file));
				System.out.println("Writing DFA to file " + s);
				bw.write(dfa.toString()); // write here
				System.out.println("Done!");
			} catch(IOException ioe) {
				System.out.println("Unable to write output file!");
			} finally {
				try {
					if (bw != null)
						bw.close();
				} catch(Exception ex) {
					System.out.println("Error in closing BufferedWriter " + ex);
				}
			}
		}
		else { // no file given
			System.out.println(args.length > 1 ? "Too many arguments!" : "No input file specified!");
		}
	}
	public static DFA convertNFAtoDFA(NFA nfa) {
		List<Tuple<State<Set<String>>, Set<State<String>>>> incomplete = new LinkedList<Tuple<State<Set<String>>, Set<State<String>>>>();
		List<State<Set<String>>> complete = new ArrayList<State<Set<String>>>();
		Set<State<String>> q0States = emptyStringTransitionFunction(nfa.q0);
		State<Set<String>> firstState = new State<Set<String>>(
			q0States.stream().map(state -> state.getName()).collect(Collectors.toSet()),
			new ArrayList<Tuple<Character, State<Set<String>>>>(),
			!q0States.stream().filter(state -> state.isAFinalState()).collect(Collectors.toList()).isEmpty()
		);
		incomplete.add(new Tuple<State<Set<String>>, Set<State<String>>>(firstState, q0States)); // need to create states and create transitions
		while (!incomplete.isEmpty()) {
			State<Set<String>> currentDFAState = incomplete.get(0).getFirst();
			Set<State<String>> currentNFAStates = incomplete.get(0).getSecond();
			for (Character character : nfa.inputAlphabet) { // single DFA Node, get resultalt transition func based on single char
				Set<State<String>> nextNFAStates = new HashSet<State<String>>();
				for (State<String> nfaState : currentNFAStates) { // single DFA Node, single character
					State<String> nextNFAState = nfaState.next(character);
					if (nextNFAState != null)
						nextNFAStates.addAll(emptyStringTransitionFunction(nextNFAState));
				} // now have list of resulting nfa states. Create new DFA Node and check if complete, else throw in incomplete
				State<Set<String>> futureState = null;
				Set<String> potentialNodeName = nextNFAStates.stream().map(state -> state.getName()).collect(Collectors.toSet());
				List<State<Set<String>>> possiblyAlreadyComplete = complete.stream()
					.filter(state -> state.getName().equals(potentialNodeName))
					.collect(Collectors.toList());
				List<State<Set<String>>> possiblyAlreadyInComplete = incomplete.stream()
					.map(state -> state.getFirst())
					.filter(state -> state.getName().equals(potentialNodeName))
					.collect(Collectors.toList());
				if (possiblyAlreadyComplete.size() != 0)
					futureState = possiblyAlreadyComplete.get(0);
				else if (possiblyAlreadyInComplete.size() != 0)
					futureState = possiblyAlreadyInComplete.get(0);
				else {
					futureState = new State<Set<String>>(
						potentialNodeName,
						new ArrayList<Tuple<Character, State<Set<String>>>>(),
						nextNFAStates.stream().filter(state -> state.isAFinalState()).count() != 0
					);
					incomplete.add(new Tuple<State<Set<String>>, Set<State<String>>>(futureState, nextNFAStates));
				}
				List<Tuple<Character, State<Set<String>>>> currentTransitions = currentDFAState.getTransitions();
				currentTransitions.add(new Tuple<Character, State<Set<String>>>(character, futureState));
				currentDFAState.setTransitions(currentTransitions);
			}
			incomplete.remove(0);
			complete.add(currentDFAState);
		}
		// states are all complete
		List<State<Set<String>>> finalStates = complete.stream().filter(state -> state.isAFinalState()).collect(Collectors.toList());
		return new DFA(complete, nfa.inputAlphabet, firstState, finalStates);
	}

	public static Set<State<String>> emptyStringTransitionFunction(State<String> sState) {
		Set<State<String>> a = new HashSet<State<String>>();
		int prevSize = a.size();
		a.add(sState);
		while(prevSize != a.size()) {
			prevSize = a.size();
			for (State<String> state : a) {
				for (Tuple<Character, State<String>> transition : state.getTransitions()) {
					if (transition.getFirst() == 'E') {// empty string transition symbol: character literal E
						a.add(transition.getSecond());
					}
				}
			}
		}
		return a;
	}

	public static NFA initializeNFA(Scanner scan) {
		while (scan.hasNextLine())
			if (scan.nextLine().substring(0, 1).equals("%"))
				break; // find start of input
		ArrayList<State<String>> states = new ArrayList<State<String>>();
		while (scan.hasNextLine()) {
			String nodeName = scan.nextLine();
			if (nodeName.substring(0, 1).equals("%"))
				break; // trigger on end of input Nodes
			else {
				states.add(new State<String>(nodeName, new ArrayList<Tuple<Character, State<String>>>(), false));
			}
		}
		ArrayList<Character> inputAlphabet = new ArrayList<Character>();
		while (scan.hasNextLine()) {
			String alphabetCharacter = scan.nextLine();
			if (alphabetCharacter.substring(0, 1).equals("%"))
				break; // trigger on end of input alphabet
			else {
				inputAlphabet.add(alphabetCharacter.toCharArray()[0]);
			}
		}
		ArrayList<State<String>> finalStates = new ArrayList<State<String>>();
		while (scan.hasNextLine()) {
			String finalStateName = scan.nextLine();
			if (finalStateName.substring(0, 1).equals("%"))
				break; // trigger on end of input alphabet
			else {
				State<String> fState = findStateByName(finalStateName, states);
				if (fState == null)
					System.out.println("Final State not found!");
				else {
					fState.setIsFinal(true);
					finalStates.add(fState);
				}
					
			}
		}
		State<String> startState =  null;
		while (scan.hasNextLine()) {
			String startNodeName = scan.nextLine();
			if (startNodeName.substring(0, 1).equals("%"))
				break; // trigger on end of initial state
			else {
				startState = findStateByName(startNodeName, states);
				if (startState == null) {
					System.out.printf("Start state was not in states array! %sa\n", startNodeName);
				}
			}
		}
		while (scan.hasNextLine()) {
			String line = scan.nextLine();
			if (line.substring(0, 1).equals("%"))
				break; // trigger on end of input alphabet
			else {
				String[] lineArr = line.split(" ");
				State<String> start = findStateByName(lineArr[0], states);
				char inputAlphabetChar = lineArr[1].charAt(0);
				State<String> end = findStateByName(lineArr[2], states);
				List<Tuple<Character, State<String>>> transitions = start.getTransitions();
				transitions.add(new Tuple<Character,State<String>>(inputAlphabetChar, end));
				start.setTransitions(transitions);
			}
		}
		return new NFA(states, inputAlphabet, startState, finalStates);
	}

	public static State<String> findStateByName(String name, List<State<String>> states) {
		for (State<String> state : states) {
			if (state.getName().equals(name))
				return state;
		}
		return null;
	}
}