package com.orangeelephant.sobriety.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBhelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 3;
    public static final String DATABASE_NAME = "sobriety_tracker";

    public static final int LOG_RECORD_TIME_VERSION = 3;

    public DBhelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DefineTables.Counters.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        if (oldVersion < LOG_RECORD_TIME_VERSION) {
            sqLiteDatabase.execSQL("ALTER TABLE counters ADD record_time_clean INTEGER DEFAULT 0");
        }
    }

}