package com.orangeelephant.sobriety.backup;

import android.util.Base64;

import com.orangeelephant.sobriety.counter.Counter;
import com.orangeelephant.sobriety.database.DefineTables;
import com.orangeelephant.sobriety.database.helpers.CountersDatabaseHelper;
import com.orangeelephant.sobriety.logging.LogEvent;
import com.orangeelephant.sobriety.util.SqlUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.KeyManagementException;
import java.util.Hashtable;

public class ImportBackup extends BackupBase {
    public ImportBackup(JSONObject jsonObject) throws JSONException {
        super();
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
        try {
            if (CountersDatabaseHelper.DATABASE_VERSION >= decrypted.getInt("DatabaseVersion")) {
                JSONArray countersArray = decrypted.getJSONArray("counters");

                int countersImported = 0;
                for (int i = 0; i <= countersArray.length() - 1; i++) {
                    JSONObject counter = countersArray.getJSONObject(i);

                    int id = counter.getInt("_id");
                    String name = counter.getString("name");
                    long startTime = counter.getLong("start_time_unix_millis");
                    long recordTime = counter.getLong("record_time_clean");

                    Counter toSave = new Counter(id, name, startTime, recordTime, new Hashtable(), "");

                    SqlUtil.saveCounterObjectToDb(db.getWritableDatabase(), toSave);
                    countersImported++;
                }
                LogEvent.i("Imported " + countersImported + " counters");
            } else {
                throw new IllegalArgumentException("Cant import backup to an older database version!");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
