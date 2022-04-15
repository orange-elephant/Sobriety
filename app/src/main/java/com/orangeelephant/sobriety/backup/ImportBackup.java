package com.orangeelephant.sobriety.backup;

import android.os.Environment;
import android.util.Base64;

import com.orangeelephant.sobriety.counter.Counter;
import com.orangeelephant.sobriety.database.DefineTables;
import com.orangeelephant.sobriety.database.helpers.CountersDatabaseHelper;
import com.orangeelephant.sobriety.logging.LogEvent;
import com.orangeelephant.sobriety.util.SqlUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.security.KeyManagementException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Scanner;

public class ImportBackup extends BackupBase {
    public ImportBackup() throws JSONException, FileNotFoundException {
        super();

        JSONObject jsonObject = readBackupFile();

        byte[] salt = Base64.decode(jsonObject.getString("Salt"), Base64.DEFAULT);
        byte[] encrypted = Base64.decode(jsonObject.getString("EncryptedData"), Base64.DEFAULT);
        byte[] iv = Base64.decode(jsonObject.getString("IV"), Base64.DEFAULT);

        try {
            backupSecret = new BackupSecret(context, salt);
        } catch (Exception e) {
            e.printStackTrace();
        }
        setPassphrase("Bob");

        try {
            JSONObject decrypted = new JSONObject(decryptBytes(encrypted, iv));
            LogEvent.i(decrypted.toString());
            saveToDb(decrypted);
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setPassphrase(String passphrase) {
        backupSecret.setPassphrase(passphrase);
    }

    private void saveToDb(JSONObject decrypted) {
        CountersDatabaseHelper db = new CountersDatabaseHelper(context);
        //for testing
        db.getWritableDatabase().execSQL("DELETE FROM " + DefineTables.Counters.TABLE_NAME_COUNTERS);
        db.getWritableDatabase().execSQL("DELETE FROM " + DefineTables.Counters.TABLE_NAME_REASONS);
        try {
            if (CountersDatabaseHelper.DATABASE_VERSION >= decrypted.getInt("DatabaseVersion")) {
                JSONArray countersArray = decrypted.getJSONArray("counters");

                ArrayList<Counter> countersRetrieved = new ArrayList<>();
                int numCountersImported = 0;
                for (int i = 0; i <= countersArray.length() - 1; i++) {
                    JSONObject counter = countersArray.getJSONObject(i);

                    int id = counter.getInt("_id");
                    String name = counter.getString("name");
                    long startTime = counter.getLong("start_time_unix_millis");
                    long recordTime = counter.getLong("record_time_clean");

                    Counter toSave = new Counter(id, name, startTime, recordTime, new Hashtable(), "");

                    countersRetrieved.add(toSave);
                    numCountersImported++;
                }
                LogEvent.i("Imported " + numCountersImported + " counters");

                JSONArray reasonsArray = decrypted.getJSONArray("reasons");

                for (int i = 0; i <= reasonsArray.length() - 1; i++) {
                    JSONObject reason = reasonsArray.getJSONObject(i);
                    for (Counter counter: countersRetrieved) {
                        if (counter.get_id() == reason.getInt("counter_id")) {
                            counter.getReasons_dict().put(reason.getInt("_id"), reason.getString("sobriety_reason"));
                            LogEvent.i("Reason added");
                            break;
                        }
                    }
                }

                for (Counter counter: countersRetrieved) {
                    SqlUtil.saveCounterObjectToDb(db.getWritableDatabase(), counter);
                }
            } else {
                throw new IllegalArgumentException("Cant import backup to an older database version!");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private JSONObject readBackupFile() throws FileNotFoundException, JSONException{
        String fileName = "sobriety.backup";
        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString();
        File backupFile = new File(root, fileName);

        JSONObject backupAsJson;

        Scanner fileScanner = new Scanner(backupFile);
        backupAsJson = new JSONObject(fileScanner.next());
        return backupAsJson;
    }
}