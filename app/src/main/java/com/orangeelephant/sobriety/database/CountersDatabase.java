package com.orangeelephant.sobriety.database;

import android.content.ContentValues;
import android.provider.BaseColumns;

import com.orangeelephant.sobriety.database.model.Counter;
import com.orangeelephant.sobriety.database.model.Reason;
import com.orangeelephant.sobriety.database.helpers.DBOpenHelper;
import com.orangeelephant.sobriety.dependencies.ApplicationDependencies;
import com.orangeelephant.sobriety.logging.LogEvent;

import net.sqlcipher.Cursor;
import net.sqlcipher.CursorIndexOutOfBoundsException;
import net.sqlcipher.database.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Date;

/**
 * class that provides methods for interfacing with the counters table in the main app database
 *
 * access using singleton instance provided by {@link SobrietyDatabase}
 */
public class CountersDatabase implements BaseColumns {
    private static final String TAG = (CountersDatabase.class.getSimpleName());

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

        ArrayList<Reason> reasonsForCounterId = ApplicationDependencies.getSobrietyDatabase()
                                                                        .getReasonsDatabase()
                                                                        .getReasonsForCounterId(counterId);

        return new Counter(counterId, name, time_in_millis, record_time_in_millis, reasonsForCounterId);
    }

    public ArrayList<Counter> getAllCountersWithoutReasons() {
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();

        String sql = "SELECT * FROM Counters\n" +
                "ORDER by start_time_unix_millis ASC";
        Cursor cursor = db.rawQuery(sql, null);

        ArrayList<Counter> counters = new ArrayList<>(cursor.getCount());

        while(cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            long time_in_millis = cursor.getLong(2);
            long record_time_in_millis = cursor.getLong(3);

            counters.add(new Counter(id, name, time_in_millis, record_time_in_millis, new ArrayList<>()));
        }
        cursor.close();

        return counters;
    }

    public ArrayList<Counter> getAllCountersWithReasons() {
        ArrayList<Counter> counters = getAllCountersWithoutReasons();
        ReasonsDatabase database = ApplicationDependencies.getSobrietyDatabase().getReasonsDatabase();

        for (Counter counter: counters) {
            counter.addReasons(database.getReasonsForCounterId(counter.getId()));
        }

        return counters;
    }

    public void deleteCounterById(int counterID) {
        String sqlCounterRecord = "DELETE FROM " + CountersDatabase.TABLE_NAME_COUNTERS +
                " WHERE _id = " + counterID;

        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        db.execSQL(sqlCounterRecord);

        ApplicationDependencies.getSobrietyDatabase().getReasonsDatabase()
                .deleteReasonsForCounterId(counterID);
        LogEvent.i(TAG, "Counter id " + counterID + " was deleted");
    }

    public void resetCounterTimer(int counterId, long recordTime) {
        long timeNow = new Date().getTime();
        String sql = "UPDATE " +  CountersDatabase.TABLE_NAME_COUNTERS +
                " SET " + CountersDatabase.COLUMN_RECORD_CLEAN_TIME + " = " + recordTime +
                ", " + CountersDatabase.COLUMN_START_TIME + " = " + timeNow +
                " WHERE _id = " + counterId;

        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        db.execSQL(sql);
        LogEvent.i(TAG, "Counter id " + counterId + " was reset");
    }

    /**
     * used to save a counter object and its reasons to the database
     * currently used when importing backups, and creating new counters
     *
     * @param counterToSave the counter object to be saved
     */
    public void saveCounterObjectToDb(Counter counterToSave) {
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(CountersDatabase.COLUMN_NAME, counterToSave.getName());
        contentValues.put(CountersDatabase.COLUMN_START_TIME, counterToSave.getStartTimeInMillis());
        contentValues.put(CountersDatabase.COLUMN_RECORD_CLEAN_TIME, counterToSave.getRecordTimeSoberInMillis());

        int counterRowId = (int) db.insert(CountersDatabase.TABLE_NAME_COUNTERS, null, contentValues);

        ArrayList<Reason> reasons = counterToSave.getReasons();
        ReasonsDatabase reasonsDatabase = ApplicationDependencies.getSobrietyDatabase().getReasonsDatabase();
        for (Reason reason: reasons) {
            reasonsDatabase.addReasonForCounter(counterRowId, reason.getReason());
        }
    }
}

