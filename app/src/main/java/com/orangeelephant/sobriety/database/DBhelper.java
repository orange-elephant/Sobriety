package com.orangeelephant.sobriety.database;

import android.content.Context;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;
import net.sqlcipher.database.SQLiteStatement;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class DBhelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 4;
    public static final String DATABASE_NAME = "sobriety_tracker";
    public static Context context;

    public static final int LOG_RECORD_TIME_VERSION = 3;
    public static final int LOG_SOBRIETY_REASON = 4;
    public static final int SQL_CIPHER_MIGRATION = 5;

    public DBhelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DefineTables.Counters.CREATE_TABLE);
        System.out.println(DefineTables.Counters.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        if (oldVersion < LOG_RECORD_TIME_VERSION) {
            sqLiteDatabase.execSQL("ALTER TABLE counters ADD record_time_clean INTEGER DEFAULT 0");
        }
        if (oldVersion < LOG_SOBRIETY_REASON) {
            sqLiteDatabase.execSQL("ALTER TABLE counters ADD sobriety_reason TEXT DEFAULT NULL");
        }
        if (oldVersion < SQL_CIPHER_MIGRATION) {
            //TODO
            //migrateDbToSqlcipher(context, sqLiteDatabase.getPath(), );
        }
    }

}