package com.orangeelephant.sobriety.managecounters;

import android.content.Context;
import android.database.Cursor;

import com.orangeelephant.sobriety.R;
import com.orangeelephant.sobriety.counter.Counter;
import com.orangeelephant.sobriety.database.DBhelper;
import com.orangeelephant.sobriety.database.DefineTables;

import net.sqlcipher.database.SQLiteDatabase;

import java.util.Date;
import java.util.Dictionary;

public class ResetCounter {
    private final Context context;
    private final int counterId;
    private String name;
    private long recordTime;
    private long timeNow;
    private Dictionary sobrietyReasons;

    public ResetCounter(Context context, int counterId, Dictionary sobrietyReasons) {
        this.context = context;
        this.counterId = counterId;
        this.sobrietyReasons = sobrietyReasons;

        checkForRecordTime();

        updateRecord();
    }

    public Counter returnResetCounter() {
        String time_sober_string = context.getString(R.string.CounterViewActivity_counter_message_long);
        return new Counter(this.counterId, this.name, this.timeNow, this.recordTime, this.sobrietyReasons, time_sober_string);
    }

    private void checkForRecordTime() {
        String sql = "SELECT " + DefineTables.Counters.COLUMN_START_TIME + ", " +
                DefineTables.Counters.COLUMN_RECORD_CLEAN_TIME + ", " +
                DefineTables.Counters.COLUMN_NAME +
                " FROM " + DefineTables.Counters.TABLE_NAME_COUNTERS +
                " WHERE _id = " + this.counterId;

        net.sqlcipher.database.SQLiteDatabase db = new DBhelper(this.context).getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();

        long startTime = cursor.getLong(0);
        long recordTime = cursor.getLong(1);
        String name = cursor.getString(2);

        cursor.close();
        db.close();

        long timeElapsed = getCurrentTimeElapsed(startTime);

        this.name = name;
        this.sobrietyReasons = sobrietyReasons;
        this.recordTime = Math.max(timeElapsed, recordTime);
    }

    private long getCurrentTimeElapsed(long startTime) {
        Date now = new Date();
        this.timeNow = now.getTime();

        return this.timeNow - startTime;
    }

    private void updateRecord() {
        String sql = "UPDATE " +  DefineTables.Counters.TABLE_NAME_COUNTERS +
                    " SET " + DefineTables.Counters.COLUMN_RECORD_CLEAN_TIME + " = " + this.recordTime +
                    ", " + DefineTables.Counters.COLUMN_START_TIME + " = " + this.timeNow +
                    " WHERE _id = " + this.counterId;

        SQLiteDatabase db = new DBhelper(this.context).getReadableDatabase();
        db.execSQL(sql);
    }
}
