package com.orangeelephant.sobriety.backup;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.app.Application;
import android.util.Base64;

import androidx.test.core.app.ApplicationProvider;

import com.orangeelephant.sobriety.dependencies.ApplicationDependencies;
import com.orangeelephant.sobriety.util.RandomUtil;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;

public class BackupSecretTest {
    @BeforeClass
    public static void init() {
        Application a = (Application) ApplicationProvider.getApplicationContext();
        ApplicationDependencies.init(a);
    }

    @Test
    public void testCheckPassword() throws NoSecretExistsException, GeneralSecurityException {
        BackupSecret backupSecret = new BackupSecret(null);
        backupSecret.setPassphrase("bob");

        assertTrue(backupSecret.verifyPassphrase("bob"));
        assertFalse(backupSecret.verifyPassphrase("jim"));
    }

    @Test
    public void testSetPassword() throws GeneralSecurityException, NoSecretExistsException {
        BackupSecret backupSecret = new BackupSecret(null);

        backupSecret.setPassphrase("abcdefg");
        assertTrue(backupSecret.verifyPassphrase("abcdefg"));
        //verify that the passphrase has actually been changed
        backupSecret.setPassphrase("123");
        assertFalse(backupSecret.verifyPassphrase("abcdefg"));
        assertTrue(backupSecret.verifyPassphrase("123"));
    }

    @Test
    public void testGetBackupCipherKey() throws GeneralSecurityException, NoSecretExistsException {
        BackupSecret backupSecret = new BackupSecret(null);
        backupSecret.setPassphrase("Bob");
        byte[] sharedPrefSalt = Base64.decode(backupSecret.getSalt(), Base64.DEFAULT);
        byte[] expected = BackupSecret.getBackupKey("Bob", sharedPrefSalt);
        assertArrayEquals(expected, backupSecret.getBackupCipherKey());

        byte[] providedSalt = RandomUtil.generateRandomBytes(32);
        backupSecret = new BackupSecret(providedSalt);
        backupSecret.setPassphrase("Bob");
        expected = BackupSecret.getBackupKey("Bob", providedSalt);
        assertArrayEquals(expected, backupSecret.getBackupCipherKey());


    }
}
