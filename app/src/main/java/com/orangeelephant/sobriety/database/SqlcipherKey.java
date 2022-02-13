package com.orangeelephant.sobriety.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;

import androidx.annotation.RequiresApi;

import com.orangeelephant.sobriety.logging.LogEvent;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteException;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.util.Arrays;
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

public class SqlcipherKey {
    private static final String sharedPreferenceFile = "com.orangeelephant.sobriety_preferences";
    private static SharedPreferences sharedPreferences;
    private static final String encryptedKeyName = "sqlcipherEncryptionKey";
    private static final String storedIvName = "fixedIv";
    private static final String isEncrypted = "isEncrypted";
    private final byte[] sqlCipherKey;

    private static final String AndroidKeyStore = "AndroidKeyStore";
    private static final String AES_MODE = "AES/GCM/NoPadding";
    private static final String KeyAlias = "sobriety_database_key";
    private byte[] FIXED_IV;
    private final KeyStore keyStore;
    private final java.security.Key keystoreKey;

    public SqlcipherKey(Context context) throws Exception {
        sharedPreferences = context.getSharedPreferences(sharedPreferenceFile,
                Context.MODE_PRIVATE);

        this.keyStore = KeyStore.getInstance(AndroidKeyStore);
        keyStore.load(null);
        if (!keyStore.containsAlias(KeyAlias)) {
            generateKey();
        }
        this.keystoreKey = getKey(context);

        this.FIXED_IV = manageIV();

        sqlCipherKey = decryptSqlcipherKey(getEncryptedKeyFromPreferences());
    }

    public byte[] getSqlCipherKey() {
        return this.sqlCipherKey;
    }

    public boolean getIsEncrypted() {
        return sharedPreferences.getBoolean(isEncrypted, false);
    }

    private void generateKey() throws NoSuchAlgorithmException,
            InvalidAlgorithmParameterException, NoSuchProviderException {

        KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, AndroidKeyStore);
        keyGenerator.init(new KeyGenParameterSpec.Builder(
                KeyAlias,
                KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setRandomizedEncryptionRequired(false)
                .build());

        keyGenerator.generateKey();
        LogEvent.i("New keypair for encrypting SQLCipher key created");
    }

    private java.security.Key getKey(Context context) throws KeyStoreException,
            NoSuchAlgorithmException, UnrecoverableKeyException {
        return keyStore.getKey(KeyAlias, null);
    }

    private String storeSqlcipherKey(byte[] sqlCipherKey) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException,
            BadPaddingException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException {

        //encrypt the key with the android keystore
        Cipher cipher = Cipher.getInstance(AES_MODE);
        cipher.init(Cipher.ENCRYPT_MODE, keystoreKey,
                new GCMParameterSpec(128, FIXED_IV));
        byte[] encodedBytes = cipher.doFinal(sqlCipherKey);

        String base64encodedBytes = Base64.encodeToString(encodedBytes, Base64.DEFAULT);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(encryptedKeyName, base64encodedBytes);
        editor.commit();

        return base64encodedBytes;
    }

    private byte[] decryptSqlcipherKey(byte[] encryptedKey) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException,
            BadPaddingException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException {

        Cipher cipher = Cipher.getInstance(AES_MODE);
        cipher.init(Cipher.DECRYPT_MODE, keystoreKey,
                new GCMParameterSpec(128, FIXED_IV));

        return cipher.doFinal(encryptedKey);
    }

    private byte[] manageIV() throws BadPaddingException, InvalidKeyException, NoSuchAlgorithmException,
            NoSuchPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {

        String base64encodedIV = sharedPreferences.getString(storedIvName, "");

        if (base64encodedIV.equals("") && !getIsEncrypted()) {
            base64encodedIV = createNewRandomIv();
            LogEvent.i("Stored key was not encrypted with a random IV and is not currently used, generating a new key");
            storeSqlcipherKey(generateRandomBytes(32));
        } else if (base64encodedIV.equals("") && getIsEncrypted()) {
            LogEvent.i("Stored key was not encrypted with a random IV but is in use, encrypting again with new IV");
            this.FIXED_IV = new byte[12];
            byte[] currentKey = decryptSqlcipherKey(getEncryptedKeyFromPreferences());
            base64encodedIV = createNewRandomIv();
            storeSqlcipherKey(currentKey);
        }

        return Base64.decode(base64encodedIV, Base64.DEFAULT);
    }

    private String createNewRandomIv() {
        FIXED_IV = generateRandomBytes(12);
        String base64encodedBytes = Base64.encodeToString(FIXED_IV, Base64.DEFAULT);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(storedIvName, base64encodedBytes);
        editor.commit();

        return base64encodedBytes;
    }

    private byte[] generateRandomBytes(int numBytes) {
        //generate random bytes of specified length
        byte[] bytes = new byte[numBytes];
        Random random = new Random();
        random.nextBytes(bytes);

        return bytes;
    }

    private byte[] getEncryptedKeyFromPreferences() throws BadPaddingException, InvalidKeyException,
            NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {
        //read the encrypted key from shared preferences
        String base64encodedEncryptedKey = sharedPreferences.getString(encryptedKeyName, "");
        if (base64encodedEncryptedKey.equals("")) {
            LogEvent.i("Encrypted sqlcipher key not found, creating one now.");
            base64encodedEncryptedKey = storeSqlcipherKey(generateRandomBytes(32));
        }

        return Base64.decode(base64encodedEncryptedKey, Base64.DEFAULT);
    }
}
