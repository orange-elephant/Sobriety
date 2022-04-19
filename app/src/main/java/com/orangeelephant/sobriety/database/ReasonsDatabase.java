package com.orangeelephant.sobriety.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;

import com.orangeelephant.sobriety.counter.Reason;
import com.orangeelephant.sobriety.database.helpers.DBOpenHelper;
import com.orangeelephant.sobriety.logging.LogEvent;

import net.sqlcipher.database.SQLiteDatabase;

import java.util.ArrayList;

public class ReasonsDatabase implements BaseColumns {

    private static final String TAG = (ReasonsDatabase.class.getSimpleName());

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

    private final DBOpenHelper dbOpenHelper;

    public ReasonsDatabase(DBOpenHelper dbOpenHelper) {
        this.dbOpenHelper = dbOpenHelper;
    }

    public ArrayList<Reason> getReasonsForCounterId(int counterId) {
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        ArrayList<Reason> reasons = new ArrayList<>();

        String reasonSql = "SELECT _id, sobriety_reason FROM reasons WHERE counter_id = " + counterId;
        Cursor reasonsCursor = db.rawQuery(reasonSql, null);

        if (reasonsCursor.getCount() > 0) {
            while (reasonsCursor.moveToNext()) {
                int reason_id = reasonsCursor.getInt(0);
                String sobriety_reason = reasonsCursor.getString(1);
                reasons.add(new Reason(reason_id, sobriety_reason));
            }
        } else {
            LogEvent.i(TAG, "Counter id " + counterId + " has no associated sobriety reasons.");
        }
        reasonsCursor.close();
        db.close();

        return reasons;
    }

    public void deleteReasonsForCounterId(int counterId) {
        String sqlReasonRecords = "DELETE FROM " + ReasonsDatabase.TABLE_NAME_REASONS +
                " WHERE counter_id = " + counterId;
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        db.execSQL(sqlReasonRecords);
        db.close();
    }

    public void addReasonForCounter(int counterId, String reason) {
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ReasonsDatabase.COLUMN_COUNTER_ID, counterId);
        contentValues.put(ReasonsDatabase.COLUMN_SOBRIETY_REASON, reason);
        db.insert(ReasonsDatabase.TABLE_NAME_REASONS, null, contentValues);
        db.close();
    }

    public void changeReason(int reasonId, String reason) {
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        String sql = "update " + ReasonsDatabase.TABLE_NAME_REASONS +
                " set " + ReasonsDatabase.COLUMN_SOBRIETY_REASON + " = '" +
                reason + "' where _id = " + reasonId;
        db.execSQL(sql);
        db.close();
    }
}
