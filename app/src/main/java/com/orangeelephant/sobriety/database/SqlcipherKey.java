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
    private static final String encryptedKeyName = "sqlcipherEncryptionKey";
    private static final String isEncrypted = "isEncrypted";
    private final byte[] sqlCipherKey;

    private static final String AndroidKeyStore = "AndroidKeyStore";
    private static final String AES_MODE = "AES/GCM/NoPadding";
    private static final String KeyAlias = "sobriety_database_key";
    private final byte[] FIXED_IV;
    private final KeyStore keyStore;
    private final java.security.Key keystoreKey;

    private final Context context;

    public SqlcipherKey(Context context) throws Exception {
        this.context = context;
        this.FIXED_IV = manageIV();

        this.keyStore = KeyStore.getInstance(AndroidKeyStore);
        keyStore.load(null);
        if (!keyStore.containsAlias(KeyAlias)) {
            generateKey();
        }
        this.keystoreKey = getKey(context);

        sqlCipherKey = decryptSqlcipherKey(getEncryptedKeyFromPreferences());
    }

    public byte[] getSqlCipherKey() {
        return this.sqlCipherKey;
    }

    public boolean getIsEncrypted() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(sharedPreferenceFile,
                context.MODE_PRIVATE);
        Boolean encryptedStatus = sharedPreferences.getBoolean(isEncrypted, false);

        return encryptedStatus;
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

    private String storeNewSqlcipherKey() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException,
            BadPaddingException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException {
        // generate a 32 byte random key
        Random random = new Random();
        byte[] byteArray = new byte[32];
        random.nextBytes(byteArray);

        //encrypt the key with the android keystore
        Cipher cipher = Cipher.getInstance(AES_MODE);
        cipher.init(Cipher.ENCRYPT_MODE, keystoreKey,
                new GCMParameterSpec(128, FIXED_IV));
        byte[] encodedBytes = cipher.doFinal(byteArray);

        String base64encodedBytes = Base64.encodeToString(encodedBytes, Base64.DEFAULT);
        SharedPreferences sharedPreferences = context.getSharedPreferences(sharedPreferenceFile,
                context.MODE_PRIVATE);
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

        byte[] decodedBytes = cipher.doFinal(encryptedKey);

        return decodedBytes;
    }

    private byte[] manageIV() {
        byte[] FIXED_IV = new byte[12];
        FIXED_IV[0] = 0x00;
        FIXED_IV[1] = 0x00;
        FIXED_IV[2] = 0x00;
        FIXED_IV[3] = 0x00;
        FIXED_IV[4] = 0x00;
        FIXED_IV[5] = 0x00;
        FIXED_IV[6] = 0x00;
        FIXED_IV[7] = 0x00;
        FIXED_IV[8] = 0x00;
        FIXED_IV[9] = 0x00;
        FIXED_IV[10] = 0x00;
        FIXED_IV[11] = 0x00;

        return FIXED_IV;
    }

    private byte[] getEncryptedKeyFromPreferences() throws BadPaddingException, InvalidKeyException,
            NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {
        //read the encrypted key from shared preferences
        SharedPreferences sharedPreferences = context.getSharedPreferences(sharedPreferenceFile,
                context.MODE_PRIVATE);
        String base64encodedEncryptedKey = sharedPreferences.getString(encryptedKeyName, "");
        if (base64encodedEncryptedKey == "") {
            LogEvent.i("Encrypted sqlcipher key not found, creating one now.");
            base64encodedEncryptedKey = storeNewSqlcipherKey();
        }
        byte[] encryptedSqlcipherKey = Base64.decode(base64encodedEncryptedKey, Base64.DEFAULT);

        return encryptedSqlcipherKey;
    }
}
