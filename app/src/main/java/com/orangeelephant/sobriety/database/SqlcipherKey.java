package com.orangeelephant.sobriety.database;

import android.content.Context;

import com.orangeelephant.sobriety.backup.NoSecretExistsException;
import com.orangeelephant.sobriety.logging.LogEvent;
import com.orangeelephant.sobriety.util.RandomUtil;
import com.orangeelephant.sobriety.util.SaveSecretToSharedPref;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;


public class SqlcipherKey extends SaveSecretToSharedPref {
    private static final String encryptedKeyName = "sqlcipherEncryptionKey";

    private byte[] sqlCipherKey;

    public SqlcipherKey(Context context) throws KeyStoreException {
        super(context, encryptedKeyName);
        try {
            sqlCipherKey = decryptCipherKey(getEncryptedKeyFromPreferences());
        } catch (NoSecretExistsException e) {
            LogEvent.i("No SqlcipherKey exists, creating one now.");
            sqlCipherKey = createNewSecretToStore();
            storeCipherKey(sqlCipherKey);
        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException |
                BadPaddingException | NoSuchPaddingException |
                InvalidKeyException | IllegalBlockSizeException e) {
            LogEvent.e("Couldn't decrypt cipherKey", e);
            throw new KeyStoreException();
        }
    }

    public byte[] getSqlCipherKey() {
        return this.sqlCipherKey;
    }

    @Override
    protected byte[] createNewSecretToStore() {
        LogEvent.i("Generating random bytes as secret");
        return RandomUtil.generateRandomBytes(32);
    }
}
