package com.orangeelephant.sobriety.backup;

import com.orangeelephant.sobriety.database.helpers.CountersDatabaseHelper;
import com.orangeelephant.sobriety.logging.LogEvent;
import com.orangeelephant.sobriety.util.SqlUtil;

import net.sqlcipher.Cursor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.KeyManagementException;
import java.util.ArrayList;

public class CreateBackup extends BackupBase {
    protected BackupSecret backupSecret = null;
    protected JSONObject databaseAsJson;

    public CreateBackup() {
        super();
        this.database = new CountersDatabaseHelper(context).getReadableDatabase();

        try {
            backupSecret = new BackupSecret(context, null);
        } catch (Exception e) {
            LogEvent.e("Couldn't create a new instance of backupSecret", e);
        }
    }

    @Override
    public void setPassphrase(String passphrase) {
        backupSecret.setPassphrase(passphrase);
    }

    public JSONObject getEncryptedDataAsJson() throws JSONException, NoSecretExistsException, KeyManagementException {
        String encryptedData = encryptString(getDatabaseAsJson().toString());

        JSONObject encryptedDataAsJson = new JSONObject();
        encryptedDataAsJson.put("Salt", backupSecret.getSalt());
        encryptedDataAsJson.put("IV", getIv());
        encryptedDataAsJson.put("EncryptedData", encryptedData);

        LogEvent.i(encryptedDataAsJson.toString());

        return encryptedDataAsJson;
    }

    private JSONObject getDatabaseAsJson() throws JSONException {
        JSONObject databaseAsJson = new JSONObject();
        databaseAsJson.put("DatabaseVersion", CountersDatabaseHelper.DATABASE_VERSION);

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
                            recordAsJson.put(cursor.getColumnName(i), cursor.getInt(i));
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
}
