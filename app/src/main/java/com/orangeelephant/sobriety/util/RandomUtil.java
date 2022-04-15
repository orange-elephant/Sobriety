package com.orangeelephant.sobriety.util;

import java.util.Random;

public final class RandomUtil {

    public static byte[] generateRandomBytes(int numBytes) {
        //generate random bytes of specified length
        byte[] bytes = new byte[numBytes];
        Random random = new Random();
        random.nextBytes(bytes);

        return bytes;
    }
}
