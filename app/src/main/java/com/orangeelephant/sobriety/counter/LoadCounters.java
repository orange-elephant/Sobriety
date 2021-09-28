package com.orangeelephant.sobriety.counter;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.orangeelephant.sobriety.database.DBhelper;

import java.util.ArrayList;
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

        SQLiteDatabase db = new DBhelper(this.context).getReadableDatabase();

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

            outerList.add((ArrayList) innerList.clone());
        }

        cursor.close();
        db.close();

        System.out.println(outerList);
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

            Counter currentCounter = new Counter(id, name, time, recordTime);

            counterObjects.add(currentCounter);
        }

        return counterObjects;
    }

}
