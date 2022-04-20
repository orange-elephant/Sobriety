package com.orangeelephant.sobriety.util;

import net.sqlcipher.Cursor;

import net.sqlcipher.database.SQLiteDatabase;

import java.util.ArrayList;

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

    public static int getCountFromTable(SQLiteDatabase db, String tableName) {
        Cursor cursor = getAllRecordsFromTable(db, tableName);

        return cursor.getCount();
    }
}
