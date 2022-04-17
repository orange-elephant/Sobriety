package com.orangeelephant.sobriety.database;

import android.provider.BaseColumns;

public class CountersDatabase implements BaseColumns {
    public static final String TABLE_NAME_COUNTERS = "counters";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_START_TIME = "start_time_unix_millis";
    public static final String COLUMN_RECORD_CLEAN_TIME = "record_time_clean";

    public static final String CREATE_TABLE_COUNTERS = "CREATE TABLE IF NOT EXISTS " +
            TABLE_NAME_COUNTERS + " (" +
            _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_NAME + " TEXT, " +
            COLUMN_START_TIME + " INTEGER, " +
            COLUMN_RECORD_CLEAN_TIME + " INTEGER DEFAULT 0 " + ")";

}

