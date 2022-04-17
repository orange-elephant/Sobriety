package com.orangeelephant.sobriety.backup;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

import android.app.Application;

import androidx.test.core.app.ApplicationProvider;

import com.orangeelephant.sobriety.dependencies.ApplicationDependencies;

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

        assertEquals(true, backupSecret.verifyPassphrase("bob"));
        assertEquals(false, backupSecret.verifyPassphrase("jim"));
    }
}
