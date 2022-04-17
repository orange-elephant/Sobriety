package com.orangeelephant.sobriety.backup;

import android.util.Base64;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.orangeelephant.sobriety.logging.LogEvent;
import com.orangeelephant.sobriety.util.KeyStoreUtil;
import com.orangeelephant.sobriety.util.RandomUtil;
import com.orangeelephant.sobriety.util.SobrietyPreferences;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class BackupSecret {
    private static final int DIGEST_ROUNDS = 250_000;

    private final byte[] salt;
    private String passphrase;
    private byte[] backupCipherKey;

    public BackupSecret(@Nullable byte[] salt) {
        if (salt == null) {
            this.salt = getSaltFromSharedPreferences();
        } else {
            this.salt = salt;
        }
    }

    private byte[] createNewSecretToStore() throws IllegalStateException {
        if (passphrase == null) {
            throw new IllegalStateException("No password was provided from which to derive a secret");
        }
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
        String base64encodedSalt = SobrietyPreferences.getBackupEncryptionPassphraseSalt();
        if (base64encodedSalt.equals("")) {
            LogEvent.i("No salt is stored, creating one now");
            base64encodedSalt = storeNewPassphraseSalt();
        }

        return base64encodedSalt.getBytes();
    }

    private String storeNewPassphraseSalt() {
        byte[] salt = RandomUtil.generateRandomBytes(32);
        String base64encodedBytes = Base64.encodeToString(salt, Base64.DEFAULT);

        SobrietyPreferences.setBackupEncryptionPassphraseSalt(base64encodedBytes);

        return base64encodedBytes;
    }

    public void setPassphrase(String passphrase) throws GeneralSecurityException {
        this.passphrase = passphrase;
        byte[] backupCipherKey = createNewSecretToStore();
        byte[] encryptedCipherKey = KeyStoreUtil.encryptBytes(backupCipherKey);
        SobrietyPreferences.setBackupEncryptionKey(Base64.encodeToString(encryptedCipherKey, Base64.DEFAULT));
        LogEvent.i("Backup cipher key created from password and stored");
    }

    public byte[] getBackupCipherKey() throws NoSecretExistsException, GeneralSecurityException {
        try {
            String savedSecret = SobrietyPreferences.getBackupEncryptionKey();
            if (savedSecret.equals("")) {
                throw new NoSecretExistsException("No secret exists, create one by setting a passphrase");
            }
            byte[] encryptedKey = Base64.decode(SobrietyPreferences.getBackupEncryptionKey(), Base64.DEFAULT);
            backupCipherKey = KeyStoreUtil.decryptBytes(encryptedKey);
        } catch (GeneralSecurityException e) {
            LogEvent.e("Couldn't decrypt cipherKey", e);
            throw new GeneralSecurityException();
        }
        return backupCipherKey;
    }

    public boolean verifyPassphrase(String passphrase) throws NoSecretExistsException, GeneralSecurityException {
        return Arrays.equals(getBackupKey(passphrase, salt), getBackupCipherKey());
    }

    public String getSalt() {
        return Base64.encodeToString(salt, Base64.DEFAULT);
    }
}
