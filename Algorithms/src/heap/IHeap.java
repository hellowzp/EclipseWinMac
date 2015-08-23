package heap;

public interface IHeap<T extends Comparable<? super T>> {
	T peak();
	T extract();
	int size();
	boolean isEmpty();
	void insert(T element);
	void distroy();
}
