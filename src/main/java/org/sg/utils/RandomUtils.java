package org.sg.utils;

import java.util.Random;

public class RandomUtils {
    private static final Random random = new Random(System.currentTimeMillis());

    public static int nextInt(int bound) {
        return random.nextInt(bound);
    }
}
