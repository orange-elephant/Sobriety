package com.orangeelephant.sobriety.managecounters;

import android.content.Context;
import android.database.Cursor;

import com.orangeelephant.sobriety.R;
import com.orangeelephant.sobriety.counter.Counter;
import com.orangeelephant.sobriety.counter.Reason;
import com.orangeelephant.sobriety.database.CountersDatabase;
import com.orangeelephant.sobriety.database.helpers.DBOpenHelper;

import net.sqlcipher.database.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.Dictionary;

public class ResetCounter {
    private final Context context;
    private final int counterId;
    private String name;
    private long recordTime;
    private long timeNow;
    private ArrayList<Reason> sobrietyReasons;

    public ResetCounter(Context context, int counterId, ArrayList<Reason> sobrietyReasons) {
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
        String sql = "SELECT " + CountersDatabase.COLUMN_START_TIME + ", " +
                CountersDatabase.COLUMN_RECORD_CLEAN_TIME + ", " +
                CountersDatabase.COLUMN_NAME +
                " FROM " + CountersDatabase.TABLE_NAME_COUNTERS +
                " WHERE _id = " + this.counterId;

        SQLiteDatabase db = new DBOpenHelper(this.context).getReadableDatabase();
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
        String sql = "UPDATE " +  CountersDatabase.TABLE_NAME_COUNTERS +
                    " SET " + CountersDatabase.COLUMN_RECORD_CLEAN_TIME + " = " + this.recordTime +
                    ", " + CountersDatabase.COLUMN_START_TIME + " = " + this.timeNow +
                    " WHERE _id = " + this.counterId;

        SQLiteDatabase db = new DBOpenHelper(this.context).getReadableDatabase();
        db.execSQL(sql);
    }
}
