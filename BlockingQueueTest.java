package przyklady06;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class BlockingQueueTest {
    private static void assertThat(boolean predicate) {
        if (!predicate) {
            throw new AssertionError();
        }
    }

    private static void testThreads(int timeoutMs, Consumer<List<Thread>> testBody) {
        List<Thread> threads = new ArrayList<>();
        AtomicBoolean ok = new AtomicBoolean(true);

        testBody.accept(threads);

        for (Thread t : threads) {
            t.setUncaughtExceptionHandler((thread, exception) -> {
                ok.set(false);
                exception.printStackTrace();
            });
        }

        for (Thread t : threads) {
            t.start();
        }

        try {
            Thread.sleep(timeoutMs);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (Thread t : threads) {
            if (t.isAlive()) {
                ok.set(false);
            }
        }

        for (Thread t : threads) {
            try {
                t.interrupt();
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (ok.get()) {
            System.out.println("OK");
        } else {
            System.out.println("FAIL");
        }

    }

    private static void testQueue() {
        int capacity = 2;
        int specialValue = 42;
        BlockingQueue<Integer> blockingQueue = new BlockingQueue<>(capacity);

        testThreads(50, threads -> {
        threads.add(new Thread(() -> {
            try {
                assertThat(blockingQueue.getSize() == 0);
                for (int i = 0; i < capacity + 1; i++) {
                    blockingQueue.put(i);
                }
                assertThat(blockingQueue.take() + blockingQueue.take() == capacity + specialValue);
                assertThat(blockingQueue.getSize() == 0);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }));

        threads.add(new Thread(() -> {
            try {
                for (int i = 0; i < capacity; i++) {
                    blockingQueue.take();
                }
                blockingQueue.put(specialValue);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }));
        });
    }

    private static void testMultipleProducersConsumers() {
        int capacity = 2;
        int producersConsumersCount = 100;
        BlockingQueue<Integer> blockingQueue = new BlockingQueue<>(capacity);

        testThreads(50, threads -> {
            Runnable producer = () -> {
                try {
                    for (int i = 0; i < capacity + 1; i++) {
                        blockingQueue.put(i);
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            };

            Runnable consumer = () -> {
                try {
                    for (int i = 0; i < capacity + 1; i++) {
                        blockingQueue.take();
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            };

            for (int i = 0; i < producersConsumersCount; i++) {
                threads.add(new Thread(producer));
                threads.add(new Thread(consumer));
            }

        });
    }

    private static void testRendezvous() {
        BlockingQueue<Integer> blockingQueue = new BlockingQueue<>(0);
        long firstSleepTime = 1000;
        long secondSleepTime = 2000;
        int specialValue = 42;

        testThreads(5000, threads -> {
            threads.add(new Thread(() -> {
                try {
                    assertThat(blockingQueue.getSize() == 0);
                    long now = System.currentTimeMillis();
                    blockingQueue.put(specialValue);
                    // This thread will sleep until the other one receives the value
                    long msPassed = System.currentTimeMillis() - now;
                    assertThat( msPassed > firstSleepTime + secondSleepTime);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }));

            threads.add(new Thread(() -> {
                try {
                    Thread.sleep(firstSleepTime);
                    assertThat(blockingQueue.getSize() == 0);
                    Thread.sleep(secondSleepTime);
                    assertThat(blockingQueue.take() == specialValue);
                    assertThat(blockingQueue.getSize() == 0);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }));
        });

    }

    public static void main(String[] args) {
        testQueue();
        testMultipleProducersConsumers();
        testRendezvous();
    }

}
