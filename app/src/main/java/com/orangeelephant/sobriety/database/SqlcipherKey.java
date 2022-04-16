package com.orangeelephant.sobriety.database;

import android.util.Base64;

import com.orangeelephant.sobriety.logging.LogEvent;
import com.orangeelephant.sobriety.util.KeyStoreUtil;
import com.orangeelephant.sobriety.util.RandomUtil;
import com.orangeelephant.sobriety.util.SobrietyPreferences;

import java.security.GeneralSecurityException;
import java.security.KeyStoreException;


public class SqlcipherKey {
    private byte[] sqlCipherKey;

    public SqlcipherKey() throws KeyStoreException {
        String base64CipherKey = SobrietyPreferences.getSqlcipherEncryptionKey();
        if (base64CipherKey == "") {
            try {
                LogEvent.i("No SqlcipherKey exists, creating one now.");
                sqlCipherKey = createNewSecretToStore();
                byte[] encryptedCipherKey = KeyStoreUtil.encryptBytes(sqlCipherKey);
                SobrietyPreferences.setSqlcipherEncryptionKey(Base64.encodeToString(encryptedCipherKey, Base64.DEFAULT));
            } catch (GeneralSecurityException e) {
                LogEvent.e("Couldn't create a new sqlCipherEncryptionKey", e);
                throw new KeyStoreException();
            }
        }

        byte[] encryptedCipherKey = Base64.decode(base64CipherKey, Base64.DEFAULT);
        try {
            sqlCipherKey = KeyStoreUtil.decryptBytes(encryptedCipherKey);
        } catch (GeneralSecurityException e) {
            LogEvent.e("Couldn't decrypt cipherKey", e);
            throw new KeyStoreException();
        }
    }

    public byte[] getSqlCipherKey() {
        return this.sqlCipherKey;
    }

    private byte[] createNewSecretToStore() {
        LogEvent.i("Generating random bytes as secret");
        return RandomUtil.generateRandomBytes(32);
    }
}
