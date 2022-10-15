package przyklady06;

public class Pair<T> {

    private T first;
    private T second;

    public Pair(T first, T second) {
        this.first = first;
        this.second = second;
    }

    // These two methods are marked as synchronized
    // meaning that they take a lock on a whole instance.
    // Calls to them can be safely interleaved in any order.

    public synchronized void swap() {
        T a = first;
        first = second;
        second = a;
    }

    public synchronized boolean areBothEqual() {
        return first.equals(second);
    }

}
