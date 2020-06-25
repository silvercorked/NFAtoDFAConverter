public class Tuple<K, J> {
	private final K first;
	private final J second;
	public Tuple(K f, J s) {
		this.first = f;
		this.second = s;
	}
	public K getFirst() {
		return this.first;
	}
	public J getSecond() {
		return this.second;
	}
	@Override
	public String toString() {
		return String.format("[%s, %s]", this.getFirst().toString(), this.getSecond().toString());
	}
}