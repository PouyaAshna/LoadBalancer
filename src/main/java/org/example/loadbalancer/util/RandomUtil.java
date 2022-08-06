package org.example.loadbalancer.util;

import java.util.Objects;
import java.util.Random;

public class RandomUtil {

    private static Random random;

    private RandomUtil() {
    }

    public static Random getRandom() {
        if (Objects.isNull(random)) {
            random = new Random();
        }
        return random;
    }
}
