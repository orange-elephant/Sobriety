package com.orangeelephant.sobriety.util;

import android.content.ContentValues;

import com.orangeelephant.sobriety.counter.Counter;
import com.orangeelephant.sobriety.counter.Reason;
import com.orangeelephant.sobriety.database.CountersDatabase;
import com.orangeelephant.sobriety.database.ReasonsDatabase;
import com.orangeelephant.sobriety.database.helpers.DBOpenHelper;
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
}
