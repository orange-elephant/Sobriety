package com.orangeelephant.sobriety.database;

import android.content.ContentValues;
import android.content.Context;
import android.provider.BaseColumns;

import com.orangeelephant.sobriety.dependencies.ApplicationDependencies;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

/**
 * A separate database file to store logs in for a longer period, eventually a full log
 * should be exportable as a file. This database should always be encrypted so it wont be
 * constrained by SQLCipher migrations
 */
public class LogDatabase extends SQLiteOpenHelper implements BaseColumns {

    private static final String TAG = (LogDatabase.class.getSimpleName());

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "sobriety_logs";

    public static final String TABLE_NAME = "logs";
    public static final String COLUMN_TAG = "tag";
    public static final String COLUMN_MESSAGE = "message";
    public static final String COLUMN_STACK_TRACE = "stackTrace";
    public static final String COLUMN_KEEP_UNTIL = "keepUntil";

    public static final String CREATE_TABLE_LOGS = "CREATE TABLE IF NOT EXISTS " +
            TABLE_NAME + " (" +
            _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_TAG + " TEXT, " +
            COLUMN_MESSAGE + " TEXT, " +
            COLUMN_STACK_TRACE + " TEXT DEFAULT NULL, " +
            COLUMN_KEEP_UNTIL + " INTEGER" + ")";

    public LogDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE_LOGS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

    }

    public SQLiteDatabase getReadableDatabase() {
        byte[] password = ApplicationDependencies.getSqlCipherKey().getSqlCipherKey();

        return super.getReadableDatabase(password);
    }

    public SQLiteDatabase getWritableDatabase() {
        byte[] password = ApplicationDependencies.getSqlCipherKey().getSqlCipherKey();

        return super.getWritableDatabase(password);
    }
}
