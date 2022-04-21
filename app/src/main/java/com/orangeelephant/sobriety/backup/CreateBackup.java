package com.orangeelephant.sobriety.backup;

import android.os.Environment;
import android.util.Base64;

import com.orangeelephant.sobriety.database.helpers.DBOpenHelper;
import com.orangeelephant.sobriety.dependencies.ApplicationDependencies;
import com.orangeelephant.sobriety.logging.LogEvent;
import com.orangeelephant.sobriety.util.RandomUtil;
import com.orangeelephant.sobriety.util.SqlUtil;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.util.ArrayList;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CreateBackup {
    private static final String TAG = (CreateBackup.class.getSimpleName());

    private static final String AES_MODE = "AES/GCM/NoPadding";

    protected BackupSecret backupSecret;

    public CreateBackup() {
        super();

        backupSecret = new BackupSecret(null);
    }

    public void setPassphrase(String passphrase) throws GeneralSecurityException {
        backupSecret.setPassphrase(passphrase);
    }

    private JSONObject getEncryptedDataAsJson() throws JSONException, NoSecretExistsException, KeyManagementException {
        IvParameterSpec iv = new IvParameterSpec(RandomUtil.generateRandomBytes(12));
        String encryptedData = encryptString(getDatabaseAsJson().toString(), iv, backupSecret);

        JSONObject encryptedDataAsJson = new JSONObject();
        encryptedDataAsJson.put("Salt", backupSecret.getSalt());
        encryptedDataAsJson.put("IV", Base64.encodeToString(iv.getIV(), Base64.DEFAULT));
        encryptedDataAsJson.put("EncryptedData", encryptedData);

        return encryptedDataAsJson;
    }

    private static JSONObject getDatabaseAsJson() throws JSONException {
        SQLiteDatabase database = ApplicationDependencies.getSobrietyDatabase().getReadableDatabase();

        JSONObject databaseAsJson = new JSONObject();
        databaseAsJson.put("DatabaseVersion", DBOpenHelper.DATABASE_VERSION);

        ArrayList<String> tableNames =
                SqlUtil.getTableNames(database);

        for (String tableName: tableNames) {
            JSONArray tableAsJson = new JSONArray();
            Cursor cursor = SqlUtil.getAllRecordsFromTable(database, tableName);
            while (cursor.moveToNext()) {
                JSONObject recordAsJson = new JSONObject();
                for (int i=0; i < cursor.getColumnCount(); i++) {
                    int columnType = cursor.getType(i);

                    switch (columnType) {
                        case Cursor.FIELD_TYPE_STRING:
                            recordAsJson.put(cursor.getColumnName(i), cursor.getString(i));
                            break;
                        case Cursor.FIELD_TYPE_INTEGER:
                            //timestamps exceed int size limit in java,
                            //sqlite seems to cope with them as int
                            recordAsJson.put(cursor.getColumnName(i), cursor.getLong(i));
                            break;
                        case Cursor.FIELD_TYPE_FLOAT:
                            recordAsJson.put(cursor.getColumnName(i), cursor.getFloat(i));
                            break;
                        case Cursor.FIELD_TYPE_BLOB:
                            recordAsJson.put(cursor.getColumnName(i), cursor.getBlob(i));
                            break;
                        case Cursor.FIELD_TYPE_NULL:
                            recordAsJson.put(cursor.getColumnName(i), null);
                            break;
                    }
                }
                tableAsJson.put(recordAsJson);
            }
            databaseAsJson.put(tableName, tableAsJson);
        }

        return databaseAsJson;
    }

    public void saveToExternalStorage() throws JSONException, NoSecretExistsException, KeyManagementException {
        JSONObject encryptedDataAsJson = getEncryptedDataAsJson();

        String fileName = "sobriety.backup";
        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString();
        File backupFile = new File(root, fileName);
        if (backupFile.exists()) {
            backupFile.delete();
        }
        try {
            backupFile.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(backupFile);
            fileOutputStream.write(encryptedDataAsJson.toString().getBytes(StandardCharsets.UTF_8));
            fileOutputStream.flush();
            fileOutputStream.close();

            LogEvent.i(TAG, "Backup saved to " + root + "/" + fileName);
        } catch (IOException e) {
            LogEvent.e(TAG, "Failed to create backup file", e);
        }
    }

    private static String encryptString(String toEncrypt, IvParameterSpec iv, BackupSecret backupSecret) throws NoSecretExistsException, KeyManagementException {
        if (backupSecret == null) {
            backupSecret = new BackupSecret(null);
        }

        try {
            byte[] cipherKey = backupSecret.getBackupCipherKey();
            Cipher cipher = Cipher.getInstance(AES_MODE);
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(cipherKey, "AES"), iv);

            byte[] encrypted = cipher.doFinal(toEncrypt.getBytes(StandardCharsets.UTF_8));

            return Base64.encodeToString(encrypted, Base64.DEFAULT);
        } catch (GeneralSecurityException e) {
            LogEvent.e(TAG,"Exception trying to retrieve backup cipher", e);
            throw new KeyManagementException();
        }
    }
}
