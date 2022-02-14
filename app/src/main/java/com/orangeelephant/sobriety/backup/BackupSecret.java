package com.orangeelephant.sobriety.backup;

import android.content.Context;
import android.util.Base64;

import com.orangeelephant.sobriety.logging.LogEvent;
import com.orangeelephant.sobriety.util.SaveSecretToSharedPrefUtil;


public class BackupSecret extends SaveSecretToSharedPrefUtil {
    private static final String encryptedKeyName = "backupEncryptionKey";
    private static final String passphraseSaltName = "passphraseSalt";
    private static final String isEnabled = "isEnabled";

    private final String salt;
    private String passphrase;

    public BackupSecret(Context context) throws Exception {
        super(context, encryptedKeyName);
        this.salt = getSaltFromSharedPreferences();
    }


    @Override
    protected byte[] createNewSecretToStore() throws IllegalStateException {
        if (passphrase == null) {
            throw new IllegalStateException("No password was provided from which to derive a secret");
        }
        return deriveSecretFromPassphrase(passphrase);
    }

    private byte[] deriveSecretFromPassphrase(String passphrase) {
        //TODO this method should derive a 32 byte key from a passphrase
        return new byte[32];
    }

    private String getSaltFromSharedPreferences() {
        String base64encodedSalt = sharedPreferences.getString(passphraseSaltName, "");
        if (base64encodedSalt.equals("")) {
            LogEvent.i("No salt is stored, creating one now");
            base64encodedSalt = storeNewPassphraseSalt();
        }

        return base64encodedSalt;
    }

    private String storeNewPassphraseSalt() {
        byte[] salt = generateRandomBytes(32);
        String base64encodedBytes = Base64.encodeToString(salt, Base64.DEFAULT);

        saveStringToSharedPrefs(passphraseSaltName, base64encodedBytes);

        return base64encodedBytes;
    }

    public boolean verifyPassphrase(String passphrase) {
        //return deriveSecretFromPassphrase(passphrase).equals(//TODO, equals stored secret);
        return false;
    }

    public boolean isEnabled() {
        return sharedPreferences.getBoolean(isEnabled, false);
    }
}
