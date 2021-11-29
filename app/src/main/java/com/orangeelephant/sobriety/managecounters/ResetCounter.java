package com.orangeelephant.sobriety.managecounters;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.orangeelephant.sobriety.R;
import com.orangeelephant.sobriety.counter.Counter;
import com.orangeelephant.sobriety.database.DBhelper;
import com.orangeelephant.sobriety.database.DefineTables;

import java.util.Date;

public class ResetCounter {
    private final Context context;
    private final int counterId;
    private String name;
    private long recordTime;
    private long timeNow;
    private String sobrietyReason;

    public ResetCounter(Context context, int counterId) {
        this.context = context;
        this.counterId = counterId;

        checkForRecordTime();

        updateRecord();
    }

    public Counter returnResetCounter() {
        String time_sober_string = context.getString(R.string.CounterViewActivity_counter_message_long);
        return new Counter(this.counterId, this.name, this.timeNow, this.recordTime, this.sobrietyReason, time_sober_string);
    }

    private void checkForRecordTime() {
        String sql = "SELECT " + DefineTables.Counters.COLUMN_START_TIME + ", " +
                DefineTables.Counters.COLUMN_RECORD_CLEAN_TIME + ", " +
                DefineTables.Counters.COLUMN_NAME + ", " +
                DefineTables.Counters.COLUMN_SOBRIETY_REASON +
                " FROM " + DefineTables.Counters.TABLE_NAME +
                " WHERE _id = " + this.counterId;

        SQLiteDatabase db = new DBhelper(this.context).getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();

        long startTime = cursor.getLong(0);
        long recordTime = cursor.getLong(1);
        String name = cursor.getString(2);
        String sobrietyReason = cursor.getString(3);

        cursor.close();
        db.close();

        long timeElapsed = getCurrentTimeElapsed(startTime);

        this.name = name;
        this.sobrietyReason = sobrietyReason;
        this.recordTime = Math.max(timeElapsed, recordTime);
    }

    private long getCurrentTimeElapsed(long startTime) {
        Date now = new Date();
        this.timeNow = now.getTime();

        return this.timeNow - startTime;
    }

    private void updateRecord() {
        String sql = "UPDATE " +  DefineTables.Counters.TABLE_NAME +
                    " SET " + DefineTables.Counters.COLUMN_RECORD_CLEAN_TIME + " = " + this.recordTime +
                    ", " + DefineTables.Counters.COLUMN_START_TIME + " = " + this.timeNow +
                    " WHERE _id = " + this.counterId;

        SQLiteDatabase db = new DBhelper(this.context).getReadableDatabase();
        db.execSQL(sql);
    }
}
