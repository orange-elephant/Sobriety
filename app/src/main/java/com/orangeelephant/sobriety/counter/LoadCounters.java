package com.orangeelephant.sobriety.counter;

import android.content.Context;
import android.database.Cursor;

import com.orangeelephant.sobriety.R;
import com.orangeelephant.sobriety.database.DBhelper;
import com.orangeelephant.sobriety.logging.LogEvent;

import net.sqlcipher.CursorIndexOutOfBoundsException;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteException;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

public class LoadCounters {
    private final ArrayList counters;
    private final Context context;
    private final List<Counter> counterArray;

    public LoadCounters(Context context) {
        this.context = context;
        this.counters = (ArrayList) retrieveCountersFromDb();

        this.counterArray = convertRetrievedArrayToCounterObjects();
    }

    private List retrieveCountersFromDb() {
        String sql = "SELECT * FROM Counters\n" +
                     "ORDER by start_time_unix_millis ASC";
        SQLiteDatabase db;
        db = new DBhelper(this.context).getReadableDatabase("");

        Cursor cursor = db.rawQuery(sql, null);

        ArrayList<ArrayList> outerList = new ArrayList<>(cursor.getCount());
        ArrayList<Object> innerList = new ArrayList<>();

        while(cursor.moveToNext()) {
            innerList.clear();

            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            long time_in_millis = cursor.getLong(2);
            long record_time_in_millis = cursor.getLong(3);

            innerList.add(id);
            innerList.add(name);
            innerList.add(time_in_millis);
            innerList.add(record_time_in_millis);

            try {
                String reasonSql = "SELECT _id, sobriety_reason FROM reasons WHERE counter_id = " + id;
                Cursor reasonsCursor = db.rawQuery(reasonSql, null);
                Dictionary reasons_dict = new Hashtable();

                while(reasonsCursor.moveToNext()) {
                    int reason_id = reasonsCursor.getInt(0);
                    String sobriety_reason = reasonsCursor.getString(1);
                    reasons_dict.put(reason_id, sobriety_reason);
                }
                innerList.add(reasons_dict);

            } catch (CursorIndexOutOfBoundsException exception) {
                innerList.add(null);
                LogEvent.i("Counter id " + id + " has no associated sobriety reasons.");
            }

            outerList.add((ArrayList) innerList.clone());
        }

        cursor.close();
        db.close();

        return outerList;
    }

    public List<Counter> getLoadedCounters() {
        return this.counterArray;
    }

    private List<Counter> convertRetrievedArrayToCounterObjects() {
        List<Counter> counterObjects = new ArrayList<>();

        for (int i = 0; i < this.counters.size(); i++) {
            ArrayList currentCounterDetails = (ArrayList) this.counters.get(i);

            int id = (int) currentCounterDetails.get(0);
            String name = currentCounterDetails.get(1).toString();
            Long time = (Long) currentCounterDetails.get(2);
            Long recordTime = (Long) currentCounterDetails.get(3);
            Dictionary sobriety_reasons = (Dictionary) currentCounterDetails.get(4);
            String time_sober_string = context.getString(R.string.CounterViewActivity_counter_message_long);

            Counter currentCounter = new Counter(id, name, time, recordTime, sobriety_reasons, time_sober_string);

            counterObjects.add(currentCounter);
        }

        return counterObjects;
    }

}
