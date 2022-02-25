package com.orangeelephant.sobriety.backup;

import com.orangeelephant.sobriety.database.DatabaseManager;
import com.orangeelephant.sobriety.database.helpers.CountersDatabaseHelper;
import com.orangeelephant.sobriety.logging.LogEvent;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CreateBackup extends BackupBase {
    protected BackupSecret backupSecret = null;
    protected JSONObject databaseAsJson;

    public CreateBackup() {
        super();
        this.database = new CountersDatabaseHelper(context).getReadableDatabase();

    }

    @Override
    public void setPassphrase(String passphrase) {
        try {
            backupSecret = new BackupSecret(context, null);
        } catch (Exception e) {
            LogEvent.e("Couldn't set up the passphrase for creating backup", e);
        }
    }

    public JSONObject getDatabaseAsJson() throws JSONException {
        JSONObject databaseAsJson = new JSONObject();
        databaseAsJson.put("DatabaseVersion", CountersDatabaseHelper.DATABASE_VERSION);
        databaseAsJson.put("Salt", backupSecret.getSalt());

        ArrayList<String> tableNames =
                DatabaseManager.getTableNames(database);

        for (String tableName: tableNames) {
            JSONObject tableAsJson = new JSONObject();
            Cursor cursor = database.rawQuery("SELECT * FROM " + tableName, null);
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
                String recordId = String.valueOf(cursor.getInt(0));
                tableAsJson.put(recordId, recordAsJson);
            }
            databaseAsJson.put(tableName, tableAsJson);
        }

        return databaseAsJson;
    }
}
