package com.orangeelephant.sobriety.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;

import com.orangeelephant.sobriety.backup.NoSecretExistsException;
import com.orangeelephant.sobriety.logging.LogEvent;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;


/*https://medium.com/@ericfu/securely-storing-secrets-in-an-android-application-501f030ae5a3
 * taken from this source as a way to store the key for the sqlite databases using the android
 * keystore. the plan is to generate a key, and encrypt it with help from the keystore,
 * this can then be decrypted and retrieved when the app is launched.*/

/**
 * Util to help with storing a secret to the android shared preferences, encrypted using the android
 * keystore. By default 32 random bytes are stored as a key, however the method
 * protected byte[] createNewSecretToStore() can be overridden to specify a custom generated secret
 */
public abstract class SaveSecretToSharedPrefUtil {
    protected static final String sharedPreferenceFile = "com.orangeelephant.sobriety_preferences";
    private static final String isEncrypted = "isEncrypted";
    protected static SharedPreferences sharedPreferences;

    private static final String AndroidKeyStore = "AndroidKeyStore";
    private static final String AES_MODE = "AES/GCM/NoPadding";
    private static final String KeyAlias = "sobriety_database_key";
    private static final String storedIvName = "fixedIv";
    private byte[] FIXED_IV;
    private final KeyStore keyStore;
    private final java.security.Key keystoreKey;
    private final String encryptedKeyName;

    public SaveSecretToSharedPrefUtil(Context context, String encryptedKeyName) throws KeyStoreException {
        sharedPreferences = context.getSharedPreferences(sharedPreferenceFile,
                Context.MODE_PRIVATE);
        this.encryptedKeyName = encryptedKeyName;
        try {
            this.keyStore = KeyStore.getInstance(AndroidKeyStore);
            keyStore.load(null);
            if (!keyStore.containsAlias(KeyAlias)) {
                generateKey();
            }
            this.keystoreKey = getKey();
        } catch (Exception e) {
            LogEvent.e("Error with the android keystore", e);
            throw new KeyStoreException();
        }
        this.FIXED_IV = manageIV();
    }

    protected abstract byte[] createNewSecretToStore();

    private void generateKey() {
        try{
            KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, AndroidKeyStore);
            keyGenerator.init(new KeyGenParameterSpec.Builder(
                    KeyAlias,
                    KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .setRandomizedEncryptionRequired(false)
                    .build());

            keyGenerator.generateKey();
            LogEvent.i("New keypair for encrypting sharedPref secrets created");
        } catch (Exception e) {
            LogEvent.e("Couldn't generate a key to store secrets to shared preferences", e);
        }

    }

    private java.security.Key getKey() throws KeyStoreException,
            NoSuchAlgorithmException, UnrecoverableKeyException {
        return keyStore.getKey(KeyAlias, null);
    }

    protected void storeCipherKey(byte[] sqlCipherKey) {
        try {
            //encrypt the key with the android keystore
            Cipher cipher = Cipher.getInstance(AES_MODE);
            cipher.init(Cipher.ENCRYPT_MODE, keystoreKey,
                    new GCMParameterSpec(128, FIXED_IV));
            byte[] encodedBytes = cipher.doFinal(sqlCipherKey);

            String base64encodedBytes = Base64.encodeToString(encodedBytes, Base64.DEFAULT);
            saveStringToSharedPrefs(encryptedKeyName, base64encodedBytes);
        } catch (Exception e) {
            LogEvent.e("Couldn't encrypt the cipher key to store to shared preferences", e);
        }
    }

    protected byte[] decryptCipherKey(byte[] encryptedKey) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException,
            BadPaddingException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException {

        Cipher cipher = Cipher.getInstance(AES_MODE);
        cipher.init(Cipher.DECRYPT_MODE, keystoreKey,
                new GCMParameterSpec(128, FIXED_IV));

        return cipher.doFinal(encryptedKey);
    }

    private byte[] manageIV() {
        String base64encodedIV = sharedPreferences.getString(storedIvName, "");

        if (base64encodedIV.equals("")) {
            try {
                getEncryptedKeyFromPreferences();
                LogEvent.i("key exists but was not encrypted with a random IV");
                return new byte[12];
            } catch (NoSecretExistsException e) {
                LogEvent.i("No stored key, a random IV can be created");
                base64encodedIV = createNewRandomIv();
            }
        }

        return Base64.decode(base64encodedIV, Base64.DEFAULT);
    }

    protected byte[] getEncryptedKeyFromPreferences() throws NoSecretExistsException {
        //read the encrypted key from shared preferences
        LogEvent.i("Fetching " + encryptedKeyName);
        String base64encodedEncryptedKey = sharedPreferences.getString(encryptedKeyName, "");

        if (base64encodedEncryptedKey.equals("")) {
            LogEvent.i("Encrypted key " + encryptedKeyName + " not found.");
            throw new NoSecretExistsException();
        }

        return Base64.decode(base64encodedEncryptedKey, Base64.DEFAULT);
    }

    private String createNewRandomIv() {
        FIXED_IV = generateRandomBytes(12);
        String base64encodedBytes = Base64.encodeToString(FIXED_IV, Base64.DEFAULT);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(storedIvName, base64encodedBytes);
        editor.commit();

        return base64encodedBytes;
    }

    public byte[] generateRandomBytes(int numBytes) {
        //generate random bytes of specified length
        byte[] bytes = new byte[numBytes];
        Random random = new Random();
        random.nextBytes(bytes);

        return bytes;
    }

    public boolean getIsEncrypted() {
        return sharedPreferences.getBoolean(isEncrypted, false);
    }
    
    protected void saveStringToSharedPrefs(String name, String valueToStore) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(name, valueToStore);
        editor.commit();
    }
}
