package com.orangeelephant.sobriety.util;

import android.content.ContentValues;

import com.orangeelephant.sobriety.counter.Counter;
import com.orangeelephant.sobriety.counter.Reason;
import com.orangeelephant.sobriety.database.CountersDatabase;
import com.orangeelephant.sobriety.database.ReasonsDatabase;
import com.orangeelephant.sobriety.logging.LogEvent;

import net.sqlcipher.Cursor;

import net.sqlcipher.database.SQLiteDatabase;

import java.util.ArrayList;
import java.util.NoSuchElementException;

/**
 * A class which provides methods useful for interacting with the SQLiteDatabase of the
 * app
 */
public final class SqlUtil {

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

        contentValues.put(CountersDatabase.COLUMN_NAME, counterToSave.getName());
        contentValues.put(CountersDatabase.COLUMN_START_TIME, counterToSave.getStart_time_in_millis());
        contentValues.put(CountersDatabase.COLUMN_RECORD_CLEAN_TIME, counterToSave.getRecord_time_sober_in_millis());

        long counterRowId = db.insert(CountersDatabase.TABLE_NAME_COUNTERS, null, contentValues);

        ArrayList<Reason> reasonsDict = counterToSave.getReasons();
        System.out.println(reasonsDict.size());
        for (int i = 0; i < reasonsDict.size(); i++) {
            try {
                String reason = reasonsDict.get(i).getReason();
                System.out.println(reason);
                ContentValues reasonContentValues = new ContentValues();
                reasonContentValues.put(ReasonsDatabase.COLUMN_COUNTER_ID, counterRowId);
                reasonContentValues.put(ReasonsDatabase.COLUMN_SOBRIETY_REASON, reason);

                db.insert(ReasonsDatabase.TABLE_NAME_REASONS, null, reasonContentValues);
            } catch (NoSuchElementException e) {
                LogEvent.i("No more reasons to save for this counter");
                break;
            }
        }
    }
}
