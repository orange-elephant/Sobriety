package com.orangeelephant.sobriety.database;

import android.provider.BaseColumns;

public final class DefineTables {
    private DefineTables() {
    }

    public static class Counters implements BaseColumns {
        public static final String TABLE_NAME_COUNTERS = "counters";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_START_TIME = "start_time_unix_millis";
        public static final String COLUMN_RECORD_CLEAN_TIME = "record_time_clean";

        public static final String TABLE_NAME_REASONS = "reasons";
        public static final String COLUMN_COUNTER_ID = "counter_id";
        public static final String COLUMN_SOBRIETY_REASON = "sobriety_reason";

        public static final String CREATE_TABLE_COUNTERS = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME_COUNTERS + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_START_TIME + " INTEGER, " +
                COLUMN_RECORD_CLEAN_TIME + " INTEGER DEFAULT 0 " + ")";

        public static final String CREATE_TABLE_REASONS = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME_REASONS + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_COUNTER_ID + " INTEGER, " +
                COLUMN_SOBRIETY_REASON + " TEXT DEFAULT NULL, " +
                "FOREIGN KEY (" + COLUMN_COUNTER_ID + ") REFERENCES " +
                TABLE_NAME_COUNTERS + "(" + _ID + ")" + ")";
    }
}