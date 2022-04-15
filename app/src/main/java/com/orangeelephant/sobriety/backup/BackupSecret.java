package com.orangeelephant.sobriety.backup;

import android.content.Context;
import android.util.Base64;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.orangeelephant.sobriety.logging.LogEvent;
import com.orangeelephant.sobriety.util.RandomUtil;
import com.orangeelephant.sobriety.util.SaveSecretToSharedPref;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;


public class BackupSecret extends SaveSecretToSharedPref {
    private static final String encryptedKeyName = "backupEncryptionKey";
    private static final String passphraseSaltName = "passphraseSalt";
    private static final int DIGEST_ROUNDS = 250_000;

    private final byte[] salt;
    private String passphrase;
    private byte[] backupCipherKey;

    public BackupSecret(Context context, @Nullable byte[] salt) throws Exception {
        super(context, encryptedKeyName);
        if (salt == null) {
            this.salt = getSaltFromSharedPreferences();
        } else {
            this.salt = salt;
        }
    }


    @Override
    protected byte[] createNewSecretToStore() throws IllegalStateException {
        if (passphrase == null) {
            throw new IllegalStateException("No password was provided from which to derive a secret");
        }
        return deriveSecretFromPassphrase(passphrase);
    }

    private byte[] deriveSecretFromPassphrase(String passphrase) {
        return getBackupKey(passphrase, salt);
    }

    /*implementation for this method was adapted from
    https://github.com/signalapp/Signal-Android/blob/master/app/src/main/java/org/thoughtcrime/securesms/backup/FullBackupBase.java
    so that i had less opportunity to mess up deriving a key from the user provided passphrase
    I initially looked into using the argon2 KDF however the implementations i found didn't allow
    providing your own salt and i was unsure if this would work across multiple devices as needed
    for backups.
     */
    private static @NonNull byte[] getBackupKey(@NonNull String passphrase, @Nullable byte[] salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-512");
            byte[] input = passphrase.replace(" ", "").getBytes();
            byte[] hash = input;

            if (salt != null) digest.update(salt);

            for (int i = 0; i < DIGEST_ROUNDS; i++) {
                digest.update(hash);
                hash = digest.digest(input);
            }
            byte[] trimmed = new byte[32];
            System.arraycopy(hash, 0, trimmed, 0, 32);

            return trimmed;
        } catch (NoSuchAlgorithmException e) {
            throw new AssertionError(e);
        }
    }

    private byte[] getSaltFromSharedPreferences() {
        String base64encodedSalt = sharedPreferences.getString(passphraseSaltName, "");
        if (base64encodedSalt.equals("")) {
            LogEvent.i("No salt is stored, creating one now");
            base64encodedSalt = storeNewPassphraseSalt();
        }

        return base64encodedSalt.getBytes();
    }

    private String storeNewPassphraseSalt() {
        byte[] salt = RandomUtil.generateRandomBytes(32);
        String base64encodedBytes = Base64.encodeToString(salt, Base64.DEFAULT);

        saveStringToSharedPrefs(passphraseSaltName, base64encodedBytes);

        return base64encodedBytes;
    }

    public void setPassphrase(String passphrase) {
        this.passphrase = passphrase;
        byte[] backupCipherKey = createNewSecretToStore();
        storeCipherKey(backupCipherKey);
        LogEvent.i("Backup cipher key created from password and stored");
    }

    public byte[] getBackupCipherKey() throws NoSecretExistsException, KeyStoreException {
        try {
            backupCipherKey = decryptCipherKey(getEncryptedKeyFromPreferences());
        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException |
                BadPaddingException | NoSuchPaddingException |
                InvalidKeyException | IllegalBlockSizeException e) {
            LogEvent.e("Couldn't decrypt cipherKey", e);
            throw new KeyStoreException();
        }
        return backupCipherKey;
    }

    //fails!!
    public boolean verifyPassphrase(String passphrase) throws NoSecretExistsException, KeyStoreException {
        return Arrays.equals(deriveSecretFromPassphrase(passphrase), getBackupCipherKey());
    }

    public String getSalt() {
        return Base64.encodeToString(salt, Base64.DEFAULT);
    }
}
