package com.orangeelephant.sobriety.backup;

import android.content.Context;
import android.util.Base64;

import com.orangeelephant.sobriety.dependencies.ApplicationDependencies;
import com.orangeelephant.sobriety.logging.LogEvent;
import com.orangeelephant.sobriety.util.RandomUtil;

import net.sqlcipher.database.SQLiteDatabase;

import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public abstract class BackupBase {
    protected Context context;
    protected SQLiteDatabase database;
    protected BackupSecret backupSecret = null;
    protected IvParameterSpec iv;

    private static final String AES_MODE = "AES/GCM/NoPadding";

    protected BackupBase() {
        this.context = ApplicationDependencies.getApplicationContext();
    }

    public abstract void setPassphrase(String passphrase);

    protected String encryptString(String toEncrypt) throws NoSecretExistsException, KeyManagementException {
        try {
            if (backupSecret == null) {
                backupSecret = new BackupSecret(context, null);
            }
        } catch (Exception e) {
            LogEvent.e("Could create backup secret object", e);
        }

        byte[] cipherKey;
        try {
            cipherKey = backupSecret.getBackupCipherKey();
            Cipher cipher = Cipher.getInstance(AES_MODE);
            iv = new IvParameterSpec(RandomUtil.generateRandomBytes(12));
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(cipherKey, "AES"), iv);

            byte[] encrypted = cipher.doFinal(toEncrypt.getBytes(StandardCharsets.UTF_8));

            return Base64.encodeToString(encrypted, Base64.DEFAULT);
        } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException |
                KeyStoreException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException e) {
            LogEvent.e("Exception trying to retrieve backup cipher", e);
            throw new KeyManagementException();
        }
    }

    protected String decryptBytes(byte[] toDecrypt, byte[] iv) throws KeyManagementException {
        try {
            Cipher cipher = Cipher.getInstance(AES_MODE);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            byte[] cipherKey = backupSecret.getBackupCipherKey();
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(cipherKey, "AES"), ivParameterSpec);

            byte[] decrypted = cipher.doFinal(toDecrypt);

            return new String(decrypted);
        } catch (NoSecretExistsException | NoSuchPaddingException | NoSuchAlgorithmException | KeyStoreException |
                InvalidKeyException | BadPaddingException | IllegalBlockSizeException | InvalidAlgorithmParameterException e) {
            LogEvent.e("Failed to decrypt string", e);
            throw new KeyManagementException();
        }
    }

    protected String getIv() {
        return Base64.encodeToString(iv.getIV(), Base64.DEFAULT);
    }
}
