package com.orangeelephant.sobriety.database.helpers;

import com.orangeelephant.sobriety.database.CountersDatabase;
import com.orangeelephant.sobriety.database.model.Counter;
import com.orangeelephant.sobriety.database.model.Reason;
import com.orangeelephant.sobriety.dependencies.ApplicationDependencies;
import com.orangeelephant.sobriety.logging.LogEvent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DatabaseJsonHelper {
    private static final String TAG = DatabaseJsonHelper.class.getSimpleName();

    private static final String DATABASE_VERSION = "DatabaseVersion";
    private static final String COUNTERS = "Counters";

    private int databaseVersion;
    private ArrayList<Counter> counters;

    private DatabaseJsonHelper(ArrayList<Counter> counters, int databaseVersion) {
        this.counters = counters;
        this.databaseVersion = databaseVersion;
    }

    public static DatabaseJsonHelper fromLocalDatabase() {
        CountersDatabase database = ApplicationDependencies.getSobrietyDatabase().getCountersDatabase();
        ArrayList<Counter> localCounters = database.getAllCountersWithReasons();

        return new DatabaseJsonHelper(localCounters, DBOpenHelper.DATABASE_VERSION);
    }

    public static DatabaseJsonHelper fromJson(JSONObject data) throws JSONException {
        int databaseVersion = data.getInt(DATABASE_VERSION);

        JSONArray jsonCounters = data.getJSONArray(COUNTERS);
        ArrayList<Counter> counters = new ArrayList<>();
        for (int i = 0; i < jsonCounters.length(); i++) {
            JSONObject reason = jsonCounters.getJSONObject(i);
            counters.add(Counter.fromJson(reason));
        }

        return new DatabaseJsonHelper(counters, databaseVersion);
    }

    public JSONObject toJson() throws JSONException {
        JSONObject databaseAsJson = new JSONObject();
        databaseAsJson.put(DATABASE_VERSION, databaseVersion);

        JSONArray jsonCounters = new JSONArray();
        for (Counter counter: counters) {
            jsonCounters.put(counter.toJson());
        }
        databaseAsJson.put(COUNTERS, jsonCounters);

        return databaseAsJson;
    }

    public void saveToDatabase() {
        if (databaseVersion <= DBOpenHelper.DATABASE_VERSION) {
            CountersDatabase database = ApplicationDependencies.getSobrietyDatabase().getCountersDatabase();

            for (Counter counter : counters) {
                database.saveCounterObjectToDb(counter);
            }
        } else {
            LogEvent.i(TAG, "App is incompatible with this database version");
        }
    }
}
