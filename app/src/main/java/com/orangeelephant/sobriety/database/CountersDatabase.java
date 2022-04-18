package com.orangeelephant.sobriety.database;

import android.provider.BaseColumns;

import com.orangeelephant.sobriety.counter.Counter;
import com.orangeelephant.sobriety.counter.Reason;
import com.orangeelephant.sobriety.database.helpers.DBOpenHelper;

import net.sqlcipher.Cursor;
import net.sqlcipher.CursorIndexOutOfBoundsException;
import net.sqlcipher.database.SQLiteDatabase;

import java.util.ArrayList;

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

    private final DBOpenHelper dbOpenHelper;

    public CountersDatabase(DBOpenHelper dbOpenHelper) {
        this.dbOpenHelper = dbOpenHelper;
    }

    public Counter getCounterById(int counterId) {
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();

        String sql = "SELECT * FROM Counters\n" +
                "WHERE " + _ID + " = " + counterId;
        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.getCount() == 0) {
            throw new CursorIndexOutOfBoundsException("No counter found with the provided id " + counterId);
        }

        cursor.moveToFirst();
        String name = cursor.getString(1);
        long time_in_millis = cursor.getLong(2);
        long record_time_in_millis = cursor.getLong(3);

        cursor.close();
        db.close();

        ArrayList<Reason> reasonsForCounterId = new ReasonsDatabase(dbOpenHelper).getReasonsForCounterId(counterId);

        return new Counter(counterId, name, time_in_millis, record_time_in_millis, reasonsForCounterId);
    }
}

