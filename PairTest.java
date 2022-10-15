package przyklady06;

public class PairTest {

    private static final int SWAPS = 1000000;
    private static final int CHECKS = 1000000;

    public static void main(String[] args) {
        Pair<Integer> pair = new Pair<>(1, 2);
        new Thread(() -> {
            for (int i = 0; i < SWAPS; ++i) {
                pair.swap();
            }

        }).start();

        // Note that we don't wait for the thread to finish on purpose!
        // We want the checks to run concurrently with the swaps
        // to make sure that the pair's state is valid
        // at all times

        int equalCount = 0;
        for (int i = 0; i < CHECKS; ++i) {
            if (pair.areBothEqual()) {
                ++equalCount;
            }
        }
        System.out.println("Correct? " + (equalCount == 0));
    }

}
