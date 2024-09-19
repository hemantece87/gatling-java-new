package utils;

import java.util.Random;

public class DataProviderUtils {

    public static String getRandomNumber(int length) {
        long min = (long) Math.pow(10, length - 1);
        long max = (long) Math.pow(10, length) - 1;

        // Generate a random number within this range
        Random random = new Random();
        return Long.toString(min + (long) (random.nextDouble() * (max - min)));
    }
}
