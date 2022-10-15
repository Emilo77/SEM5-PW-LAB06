package przyklady06;

public class BlockingQueue<T> {

    public BlockingQueue(int capacity) {

    }

    public synchronized T take() throws InterruptedException {
        return null;
    }

    public synchronized void put(T item) throws InterruptedException {

    }

    public synchronized int getSize() {
        return 0;
    }

    public int getCapacity() {
        return 0;
    }
}