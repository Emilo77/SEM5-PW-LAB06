package przyklady06;

import java.util.ArrayList;
import java.util.List;

public class SwapperTest {
    private static final Swapper<Integer> swapper = new Swapper<>(0);
    private static final int CYCLES = 5;

    private static class Worker implements Runnable {
        private final int expectedValue;
        private final int newValue;
        private final Thread main;

        private Worker(int expectedValue, int newValue, Thread main) {
            this.expectedValue = expectedValue;
            this.newValue = newValue;
            this.main = main;
        }

        @Override
        public void run() {
            try {
                for (int i = 0; i < CYCLES; ++i) {
                    swapper.swapValue(expectedValue, newValue);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Worker interrupted");
                main.interrupt();
            }
        }

    }

    public static void main(String[] args) {
        Thread main = Thread.currentThread();
        List<Thread> workers = new ArrayList<>();

        // 42-->|
        //  |   |
        //  |<--0----->1
        //      |      |
        //      |<-----2

        workers.add(new Thread(new Worker(0, 1, main)));
        workers.add(new Thread(new Worker(1, 2, main)));
        workers.add(new Thread(new Worker(2, 0, main)));
        workers.add(new Thread(new Worker(0, 42, main)));
        workers.add(new Thread(new Worker(42, 0, main)));

        for (Thread t : workers) {
            t.start();
        }

        try {
            for (Thread t : workers) {
                t.join();
            }
            for (int n : swapper.getHistory()) {
                System.out.print(" " + n);
            }
            System.out.println();
        } catch (InterruptedException e) {
            main.interrupt();
            for (Thread t : workers) {
                t.interrupt();
            }
            System.err.println("Main interrupted");
        }
    }

}
