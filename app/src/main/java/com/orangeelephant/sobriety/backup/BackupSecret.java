package com.orangeelephant.sobriety.backup;

import android.util.Base64;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

import com.orangeelephant.sobriety.logging.LogEvent;
import com.orangeelephant.sobriety.util.KeyStoreUtil;
import com.orangeelephant.sobriety.util.RandomUtil;
import com.orangeelephant.sobriety.util.SobrietyPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class BackupSecret {

    private static final String TAG = (BackupSecret.class.getSimpleName());

    private static final int DIGEST_ROUNDS = 250_000;

    private final byte[] salt;
    private String passphrase;

    public BackupSecret(@Nullable byte[] salt) {
        if (salt == null) {
            this.salt = RandomUtil.generateRandomBytes(32);
        } else {
            this.salt = salt;
        }
    }

    /*implementation for this method was adapted from
    https://github.com/signalapp/Signal-Android/blob/master/app/src/main/java/org/thoughtcrime/securesms/backup/FullBackupBase.java
    so that i had less opportunity to mess up deriving a key from the user provided passphrase
    I initially looked into using the argon2 KDF however the implementations i found didn't allow
    providing your own salt and i was unsure if this would work across multiple devices as needed
    for backups.
     */
    @VisibleForTesting
    protected static @NonNull byte[] getBackupKey(@NonNull String passphrase, @Nullable byte[] salt) {
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

    public void setPassphrase(String passphrase) throws GeneralSecurityException {
        this.passphrase = passphrase;

        byte[] backupCipherKey = passphrase.getBytes(StandardCharsets.UTF_8);
        byte[] iv = RandomUtil.generateRandomBytes(12);
        byte[] encryptedCipherKey = KeyStoreUtil.encryptBytes(backupCipherKey, iv);

        try {
            String serialisedEncryptedSecret = KeyStoreUtil.serialiseSecretToStore(encryptedCipherKey, iv);
            SobrietyPreferences.setBackupKeyWithIv(serialisedEncryptedSecret);
            SobrietyPreferences.setBackupEncryptionKey("");

            LogEvent.i(TAG,"Backup cipher key created from password and stored");
        } catch (JSONException e) {
            LogEvent.e(TAG, "setting backup passphrase failed due to a JSON exception", e);
            throw new GeneralSecurityException("Couldn't set backup passphrase");
        }
    }

    public byte[] getBackupCipherKey() throws NoSecretExistsException, GeneralSecurityException {
        try {
            if (SobrietyPreferences.getBackupEncryptionKey().equals("") &&
                    SobrietyPreferences.getBackupKeyWithIv().equals("")) {
                throw new NoSecretExistsException("No secret exists, create one by setting a passphrase");
            }

            byte[] decryptedKey;
            if (!SobrietyPreferences.getBackupEncryptionKey().equals("")) {
                LogEvent.w(TAG, "Backup passphrase is still stored without a unique IV");
                byte[] encryptedKey = Base64.decode(SobrietyPreferences.getBackupEncryptionKey(), Base64.DEFAULT);
                decryptedKey = KeyStoreUtil.decryptBytes(encryptedKey, null);

                LogEvent.i(TAG, "Re-encrypting with a unique IV");
                byte[] iv = RandomUtil.generateRandomBytes(12);
                byte[] encryptedWithNewIv = KeyStoreUtil.encryptBytes(decryptedKey, iv);
                String serialisedEncryptedSecret = KeyStoreUtil.serialiseSecretToStore(encryptedWithNewIv, iv);

                SobrietyPreferences.setBackupKeyWithIv(serialisedEncryptedSecret);
                SobrietyPreferences.setBackupEncryptionKey("");
            } else {
                JSONObject serialisedStoredSecret = new JSONObject(SobrietyPreferences.getBackupKeyWithIv());

                byte[] encryptedSecret = Base64.decode(serialisedStoredSecret.getString("encrypted"), Base64.DEFAULT);
                byte[] iv = Base64.decode(serialisedStoredSecret.getString("iv"), Base64.DEFAULT);

                decryptedKey = KeyStoreUtil.decryptBytes(encryptedSecret, iv);
            }

            return getBackupKey(new String(decryptedKey), salt);
        } catch (GeneralSecurityException | JSONException e) {
            LogEvent.e(TAG,"Couldn't decrypt cipherKey", e);
            throw new GeneralSecurityException();
        }
    }

    public boolean verifyPassphrase(String passphrase) throws NoSecretExistsException, GeneralSecurityException {
        return Arrays.equals(getBackupKey(passphrase, salt), getBackupCipherKey());
    }

    public String getSalt() {
        return Base64.encodeToString(salt, Base64.DEFAULT);
    }
}
