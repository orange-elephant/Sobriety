package com.orangeelephant.sobriety.util;

import java.util.Random;

/**
 * Currently only provides generateRandomBytes method
 * used for random key, IV and Salt generation
 */
public final class RandomUtil {

    public static byte[] generateRandomBytes(int numBytes) {
        //generate random bytes of specified length
        byte[] bytes = new byte[numBytes];
        Random random = new Random();
        random.nextBytes(bytes);

        return bytes;
    }
}
