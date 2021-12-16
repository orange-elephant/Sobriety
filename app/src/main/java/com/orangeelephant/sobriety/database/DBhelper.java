package com.orangeelephant.sobriety.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;


public class DBhelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 5;
    public static final String DATABASE_NAME = "sobriety_tracker";
    public static Context context;
    private SqlcipherKey keyManager = null;

    public static final int LOG_RECORD_TIME_VERSION = 3;
    public static final int LOG_SOBRIETY_REASON = 4;
    public static final int ADD_REASONS_TABLE = 5;
    public static final int SQL_CIPHER_MIGRATION = 6;

    public DBhelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        try {
            this.keyManager = new SqlcipherKey(context);
        } catch (Exception e) {

        }

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DefineTables.Counters.CREATE_TABLE_COUNTERS);
        sqLiteDatabase.execSQL(DefineTables.Counters.CREATE_TABLE_REASONS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        if (oldVersion < LOG_RECORD_TIME_VERSION) {
            sqLiteDatabase.execSQL("ALTER TABLE counters ADD record_time_clean INTEGER DEFAULT 0");
        }
        if (oldVersion < LOG_SOBRIETY_REASON) {
            sqLiteDatabase.execSQL("ALTER TABLE counters ADD sobriety_reason TEXT DEFAULT NULL");
        }
        if (oldVersion < ADD_REASONS_TABLE) {
            sqLiteDatabase.execSQL(DefineTables.Counters.CREATE_TABLE_REASONS);
            String sql = "SELECT _id, sobriety_reason FROM Counters\n" +
                    "WHERE sobriety_reason IS NOT NULL";
            Cursor cursor = sqLiteDatabase.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                int id = cursor.getInt(0);
                String reason = cursor.getString(1);

                ContentValues values = new ContentValues();
                values.put(DefineTables.Counters.COLUMN_COUNTER_ID, id);
                values.put(DefineTables.Counters.COLUMN_SOBRIETY_REASON, reason);
                long newRowId = sqLiteDatabase.insert(DefineTables.Counters.TABLE_NAME_REASONS, null, values);
            }
            //wipe set all old values to null since sqlite wont allow dropping column
            sqLiteDatabase.execSQL("UPDATE counters SET sobriety_reason = NULL");
        }
    }

    public SQLiteDatabase getReadableDatabase() {
        byte[] password = keyManager.getSqlCipherKey();
        if (DATABASE_VERSION >= SQL_CIPHER_MIGRATION && keyManager.getIsEncrypted()) {
            return super.getReadableDatabase(password);
        } else {
            return super.getReadableDatabase("");
        }
    }

    public SQLiteDatabase getWritableDatabase() {
        byte[] password = keyManager.getSqlCipherKey();
        if (DATABASE_VERSION >= SQL_CIPHER_MIGRATION && keyManager.getIsEncrypted()) {
            return super.getWritableDatabase(password);
        } else {
            return super.getWritableDatabase("");
        }
    }
}