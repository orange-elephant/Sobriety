package com.orangeelephant.sobriety.util;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;

import com.orangeelephant.sobriety.logging.LogEvent;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;

/**
 * A class of methods useful for encrypting data using keys in the android
 * key store, can be used to encrypt string which should be stored to shared
 * prefs, these strings can then be retrieved and decrypted using this class.
 *
 * https://medium.com/@ericfu/securely-storing-secrets-in-an-android-application-501f030ae5a3
 * taken from this source as a way to store the key for the sqlite databases using the android
 * keystore.
 */
public class KeyStoreUtil {
    private static final String AES_MODE = "AES/GCM/NoPadding";
    private static final String ANDROID_KEY_STORE = "AndroidKeyStore";
    private static final String KEY_ALIAS = "sobriety_database_key";

    public static byte[] encryptBytes(byte[] bytesToEncrypt) throws GeneralSecurityException {
        try {
            Cipher cipher = Cipher.getInstance(AES_MODE);
            cipher.init(Cipher.ENCRYPT_MODE, getKey(),
                    new GCMParameterSpec(128, manageIV()));

            return cipher.doFinal(bytesToEncrypt);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException |
                InvalidKeyException e) {
            throw new GeneralSecurityException("Invalid parameters when initialising the Cipher instance");
        } catch (BadPaddingException| IllegalBlockSizeException e) {
            throw new GeneralSecurityException("Invalid ciphertext provided");
        }
    }

    public static byte[] decryptBytes(byte[] bytesToDecrypt) throws GeneralSecurityException {
        try {
            Cipher cipher = Cipher.getInstance(AES_MODE);
            cipher.init(Cipher.DECRYPT_MODE, getKey(),
                    new GCMParameterSpec(128, manageIV()));

            return cipher.doFinal(bytesToDecrypt);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException |
                InvalidKeyException e) {
            throw new GeneralSecurityException("Invalid parameters when initialising the Cipher instance");
        } catch (BadPaddingException | IllegalBlockSizeException e) {
            throw new GeneralSecurityException("Invalid ciphertext provided");
        }
    }

    private static Key getKey() throws KeyManagementException {
        try {
            KeyStore keyStore = KeyStore.getInstance(ANDROID_KEY_STORE);
            keyStore.load(null);
            if (!keyStore.containsAlias(KEY_ALIAS)) {
                generateKey();
            }

            return keyStore.getKey(KEY_ALIAS, null);
        } catch (GeneralSecurityException | IOException e) {
            LogEvent.e("Unable to get the key from the keystore", e);
            throw new KeyManagementException("Unable to get the key from the keystore");
        }
    }

    private static void generateKey() throws KeyManagementException {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE);
            keyGenerator.init(new KeyGenParameterSpec.Builder(
                    KEY_ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .setRandomizedEncryptionRequired(false)
                    .build());

            keyGenerator.generateKey();
            LogEvent.i("New keypair for encrypting sharedPref secrets created");
        } catch (GeneralSecurityException e) {
            LogEvent.e("Unable to generate a key", e);
        }
    }

    private static byte[] manageIV() {
        String base64encodedIV = SobrietyPreferences.getEncryptionFixedIv();

        if (base64encodedIV.equals("")) {
            if (!(SobrietyPreferences.getBackupEncryptionKey() == "") &&
                    !(SobrietyPreferences.getSqlcipherEncryptionKey() == "")) {

                LogEvent.i("key exists but was not encrypted with a random IV");
                return new byte[12];
            } else {
                LogEvent.i("No stored key, a random IV can be created");
                base64encodedIV = createNewRandomIv();
            }
        }

        return Base64.decode(base64encodedIV, Base64.DEFAULT);
    }

    private static String createNewRandomIv() {
        byte[] iv = RandomUtil.generateRandomBytes(12);
        String base64encodedBytes = Base64.encodeToString(iv, Base64.DEFAULT);

        SobrietyPreferences.setEncryptionFixedIv(base64encodedBytes);

        return base64encodedBytes;
    }

}