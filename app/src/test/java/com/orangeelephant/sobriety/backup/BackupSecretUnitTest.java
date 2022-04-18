package com.orangeelephant.sobriety.backup;

import com.orangeelephant.sobriety.util.RandomUtil;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class BackupSecretUnitTest {

    /**
     * Ensure that the key derivation method always generates the same given the same salt and
     * passphrase, key length should be 32 bytes
     */
    @Test
    public void testGetBackupKey() {
        byte[] salt = RandomUtil.generateRandomBytes(32);
        byte[] backupKey = BackupSecret.getBackupKey("Bob", salt);

        assertEquals(32, backupKey.length);

        for (int i = 0; i < 10; i++) {
            assertArrayEquals(backupKey, BackupSecret.getBackupKey("Bob", salt));
        }
    }
}
