package przyklady06;

import java.util.ArrayList;
import java.util.List;

public class Swapper<T> {

    private T value;
    private final List<T> history = new ArrayList<>();

    public Swapper(T value) {
        this.value = value;
        history.add(value);
    }

    public synchronized void swapValue(T expectedValue, T newValue) throws InterruptedException {
        while (!value.equals(expectedValue)) {
            wait();
        }
        value = newValue;
        history.add(newValue);
        notifyAll();
    }

    public synchronized List<T> getHistory() {
        return List.copyOf(history);
    }

}
