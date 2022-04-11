package com.orangeelephant.sobriety.util;

import android.content.ContentValues;

import com.orangeelephant.sobriety.counter.Counter;
import com.orangeelephant.sobriety.database.DefineTables;
import com.orangeelephant.sobriety.logging.LogEvent;

import net.sqlcipher.Cursor;

import net.sqlcipher.database.SQLiteDatabase;

import java.util.ArrayList;

public class SqlUtil {

    public static ArrayList<String> getTableNames(SQLiteDatabase db) {
        String sql = "SELECT name FROM sqlite_master WHERE type='table';";
        Cursor cursor = db.rawQuery(sql, null);
        ArrayList<String> tableNames = new ArrayList<>();
        while (cursor.moveToNext()) {
            tableNames.add(cursor.getString(0));
        }

        return tableNames;
    }

    public static Cursor getAllRecordsFromTable(SQLiteDatabase db, String tableName) {
        return db.rawQuery("SELECT * FROM " + tableName, null);
    }

    /**
     * currently to be used for saving counters imported from backup
     *
     * @param db the database to write the counter to
     * @param counterToSave the counter object to be saved
     */
    public static void saveCounterObjectToDb(SQLiteDatabase db, Counter counterToSave) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DefineTables.Counters.COLUMN_NAME, counterToSave.getName());
        contentValues.put(DefineTables.Counters.COLUMN_START_TIME, counterToSave.getStart_time_in_millis());
        contentValues.put(DefineTables.Counters.COLUMN_RECORD_CLEAN_TIME, counterToSave.getRecord_time_sober_in_millis());

        db.insert(DefineTables.Counters.TABLE_NAME_COUNTERS, null, contentValues);
    }
}