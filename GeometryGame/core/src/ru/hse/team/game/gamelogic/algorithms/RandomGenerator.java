package ru.hse.team.game.gamelogic.algorithms;

import java.util.Random;

public class RandomGenerator {
    
    private static final Random random = new Random(System.currentTimeMillis());
    private static final int ENGLISH_ALPHABET_SIZE = 26;

    /**
     * Generates random string of given length using alphabet
     * of upper case english letters.
     *
     * @param length of string to be generated
     * @throws IllegalArgumentException if length is less or equal zero
     * @return generated string
     */
    public static String generateRandomString(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("length should be positive integer, but found length = " 
                    + length);
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            stringBuilder.append((char) (random.nextInt(ENGLISH_ALPHABET_SIZE) + 'A'));
        }
        return stringBuilder.toString();
    }
}
