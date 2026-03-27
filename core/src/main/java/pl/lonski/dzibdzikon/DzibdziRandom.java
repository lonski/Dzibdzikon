package pl.lonski.dzibdzikon;

import java.util.concurrent.ThreadLocalRandom;

public class DzibdziRandom {

    public static int nextInt(int low, int high) {
        return ThreadLocalRandom.current().nextInt(high - low) + low;
    }

    public static int nextInt(int bound) {
        return ThreadLocalRandom.current().nextInt(bound);
    }

    public static boolean nextBoolean() {
        return ThreadLocalRandom.current().nextBoolean();
    }

    public static double nextDouble() {
        return ThreadLocalRandom.current().nextDouble();
    }
}
