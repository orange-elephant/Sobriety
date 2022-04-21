package com.orangeelephant.sobriety.database;

import android.util.Base64;

import com.orangeelephant.sobriety.logging.LogEvent;
import com.orangeelephant.sobriety.util.KeyStoreUtil;
import com.orangeelephant.sobriety.util.RandomUtil;
import com.orangeelephant.sobriety.util.SobrietyPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.GeneralSecurityException;
import java.security.KeyStoreException;


public class SqlcipherKey {

    private static final String TAG = (SqlcipherKey.class.getSimpleName());

    private byte[] sqlCipherKey;

    public SqlcipherKey() throws KeyStoreException {

        if (SobrietyPreferences.getSqlcipherEncryptionKey().equals("") &&
                SobrietyPreferences.getSqlcipherKeyWithIv().equals("")) {
            createNewSqlCipherKey();
        }

        if (!SobrietyPreferences.getSqlcipherEncryptionKey().equals("")) {
            try {
                reencryptWithUniqueIv();
            } catch (JSONException e) {
                LogEvent.e(TAG, "FAILED TO RE-ENCRYPT existing sqlcipher key", e);
                throw new KeyStoreException();
            }
        }

        try {
            JSONObject serialisedStoredSecret = new JSONObject(SobrietyPreferences.getSqlcipherKeyWithIv());
            byte[] encryptedSecret = Base64.decode(serialisedStoredSecret.getString("encrypted"), Base64.DEFAULT);
            byte[] iv = Base64.decode(serialisedStoredSecret.getString("iv"), Base64.DEFAULT);

            sqlCipherKey = KeyStoreUtil.decryptBytes(encryptedSecret, iv);
            LogEvent.i(TAG, "SqlCipher key fetched successfully");
        } catch (GeneralSecurityException | JSONException e) {
            LogEvent.e(TAG, "Couldn't decrypt cipherKey", e);
            throw new KeyStoreException();
        }
    }

    public byte[] getSqlCipherKey() {
        return this.sqlCipherKey;
    }

    private byte[] createNewSecretToStore() {
        LogEvent.i(TAG, "Generating random bytes as secret");
        return RandomUtil.generateRandomBytes(32);
    }

    private void createNewSqlCipherKey() throws KeyStoreException {
        try {
            LogEvent.i(TAG, "No SqlcipherKey exists, creating one now.");
            byte[] newSqlcipherKey = createNewSecretToStore();

            storeKeyWithRandomIv(newSqlcipherKey);
            LogEvent.i(TAG, "New Sqlcipher key created successfully");
        } catch (GeneralSecurityException | JSONException e) {
            LogEvent.e(TAG, "Couldn't create a new sqlCipherEncryptionKey", e);
            throw new KeyStoreException();
        }
    }

    private void reencryptWithUniqueIv() throws KeyStoreException, JSONException {
        byte[] oldSavedKey = Base64.decode(SobrietyPreferences.getSqlcipherEncryptionKey(), Base64.DEFAULT);
        try {
            byte[] oldKey = KeyStoreUtil.decryptBytes(oldSavedKey, null);
            LogEvent.i(TAG, "Existing SqlCipher key fetched successfully");
            storeKeyWithRandomIv(oldKey);
            LogEvent.i(TAG, "Existing sqlcipherkey successfully re-encrypted with a random IV");
        } catch (GeneralSecurityException e) {
            LogEvent.e(TAG, "Couldn't decrypt cipherKey", e);
            throw new KeyStoreException();
        }
    }

    private void storeKeyWithRandomIv(byte[] keyToStore) throws GeneralSecurityException, JSONException {
        byte[] iv = RandomUtil.generateRandomBytes(12);
        byte[] encryptedCipherKey = KeyStoreUtil.encryptBytes(keyToStore, iv);

        String serialisedEncryptedSecret = KeyStoreUtil.serialiseSecretToStore(encryptedCipherKey, iv);
        SobrietyPreferences.setSqlcipherKeyWithIv(serialisedEncryptedSecret);
        SobrietyPreferences.setSqlcipherEncryptionKey("");
    }
}
