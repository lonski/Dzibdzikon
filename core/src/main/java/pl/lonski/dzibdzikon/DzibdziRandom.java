package pl.lonski.dzibdzikon;

import java.security.SecureRandom;

public class DzibdziRandom {

    private static final SecureRandom RANDOM = new SecureRandom();

    public static int nextInt(int low, int high) {
        return RANDOM.nextInt(high - low) + low;
    }

    public static int nextInt(int bound) {
        return RANDOM.nextInt(bound);
    }

    public static boolean nextBoolean() {
        return RANDOM.nextBoolean();
    }

    public static double nextDouble() {
        return RANDOM.nextDouble();
    }
}
