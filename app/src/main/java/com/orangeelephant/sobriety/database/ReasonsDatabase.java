package com.orangeelephant.sobriety.database;

import android.provider.BaseColumns;

public class ReasonsDatabase implements BaseColumns {
    public static final String TABLE_NAME_REASONS = "reasons";
    public static final String COLUMN_COUNTER_ID = "counter_id";
    public static final String COLUMN_SOBRIETY_REASON = "sobriety_reason";

    public static final String CREATE_TABLE_REASONS = "CREATE TABLE IF NOT EXISTS " +
            TABLE_NAME_REASONS + " (" +
            _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_COUNTER_ID + " INTEGER, " +
            COLUMN_SOBRIETY_REASON + " TEXT DEFAULT NULL, " +
            "FOREIGN KEY (" + COLUMN_COUNTER_ID + ") REFERENCES " +
            CountersDatabase.TABLE_NAME_COUNTERS + "(" + _ID + ")" + ")";
}
