package com.orangeelephant.sobriety.backup;

import android.util.Base64;

import com.orangeelephant.sobriety.database.model.Counter;
import com.orangeelephant.sobriety.database.model.Reason;
import com.orangeelephant.sobriety.database.CountersDatabase;
import com.orangeelephant.sobriety.database.ReasonsDatabase;
import com.orangeelephant.sobriety.database.helpers.DBOpenHelper;
import com.orangeelephant.sobriety.dependencies.ApplicationDependencies;
import com.orangeelephant.sobriety.logging.LogEvent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class ImportBackup {
    private static final String TAG = (ImportBackup.class.getSimpleName());
    private static final String AES_MODE = "AES/GCM/NoPadding";

    private final BackupSecret backupSecret;

    public ImportBackup(String path) throws JSONException, FileNotFoundException {

        JSONObject jsonObject = new JSONObject(path); //readBackupFile(path);


        byte[] salt = Base64.decode(jsonObject.getString("Salt"), Base64.DEFAULT);
        byte[] encrypted = Base64.decode(jsonObject.getString("EncryptedData"), Base64.DEFAULT);
        byte[] iv = Base64.decode(jsonObject.getString("IV"), Base64.DEFAULT);

        backupSecret = new BackupSecret(salt);

        try {
            setPassphrase("Bob");
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }

        try {
            JSONObject decrypted = new JSONObject(decryptBytes(encrypted, iv, backupSecret));
            saveToDb(decrypted);
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
    }

    public void setPassphrase(String passphrase) throws GeneralSecurityException {
        backupSecret.setPassphrase(passphrase);
    }

    private void saveToDb(JSONObject decrypted) {
        DBOpenHelper db = ApplicationDependencies.getSobrietyDatabase();
        //for testing
        db.getWritableDatabase().execSQL("DELETE FROM " + CountersDatabase.TABLE_NAME_COUNTERS);
        db.getWritableDatabase().execSQL("DELETE FROM " + ReasonsDatabase.TABLE_NAME_REASONS);
        try {
            if (DBOpenHelper.DATABASE_VERSION >= decrypted.getInt("DatabaseVersion")) {
                JSONArray countersArray = decrypted.getJSONArray("counters");

                ArrayList<Counter> countersRetrieved = new ArrayList<>();
                int numCountersImported = 0;
                for (int i = 0; i <= countersArray.length() - 1; i++) {
                    JSONObject counter = countersArray.getJSONObject(i);

                    int id = counter.getInt("_id");
                    String name = counter.getString("name");
                    long startTime = counter.getLong("start_time_unix_millis");
                    long recordTime = counter.getLong("record_time_clean");

                    Counter toSave = new Counter(id, name, startTime, recordTime, new ArrayList<>());

                    countersRetrieved.add(toSave);
                    numCountersImported++;
                }
                LogEvent.i(TAG, "Imported " + numCountersImported + " counters");

                JSONArray reasonsArray = decrypted.getJSONArray("reasons");

                for (int i = 0; i <= reasonsArray.length() - 1; i++) {
                    JSONObject reason = reasonsArray.getJSONObject(i);
                    for (Counter counter: countersRetrieved) {
                        if (counter.getId() == reason.getInt("counter_id")) {
                            Reason sobrietyReason = new Reason(reason.getInt("_id"), reason.getString("sobriety_reason"));
                            counter.getReasons().add(sobrietyReason);
                            break;
                        }
                    }
                }

                CountersDatabase countersDatabase = ApplicationDependencies.getSobrietyDatabase().getCountersDatabase();
                for (Counter counter: countersRetrieved) {
                    countersDatabase.saveCounterObjectToDb(counter);
                }
            } else {
                throw new IllegalArgumentException("Cant import backup to an older database version!");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private JSONObject readBackupFile(String path) throws FileNotFoundException, JSONException{
        //String fileName = "sobriety.backup";
        //String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString();
        File backupFile = new File(path);

        JSONObject backupAsJson;

        Scanner fileScanner = new Scanner(backupFile);
        backupAsJson = new JSONObject(fileScanner.next());
        return backupAsJson;
    }

    private static String decryptBytes(byte[] toDecrypt, byte[] iv, BackupSecret backupSecret) throws KeyManagementException {
        try {
            Cipher cipher = Cipher.getInstance(AES_MODE);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            byte[] cipherKey = backupSecret.getBackupCipherKey();
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(cipherKey, "AES"), ivParameterSpec);

            byte[] decrypted = cipher.doFinal(toDecrypt);

            return new String(decrypted);
        } catch (NoSecretExistsException | GeneralSecurityException e) {
            LogEvent.e(TAG,"Failed to decrypt string", e);
            throw new KeyManagementException();
        }
    }
}
