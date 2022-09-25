package com.orangeelephant.sobriety.database.helpers;

import com.orangeelephant.sobriety.database.CountersDatabase;
import com.orangeelephant.sobriety.database.model.Counter;
import com.orangeelephant.sobriety.dependencies.ApplicationDependencies;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DatabaseJsonHelper {
    private static final String DATABASE_VERSION = "DatabaseVersion";
    private static final String COUNTERS = "Counters";

    private int databaseVersion;
    private ArrayList<Counter> counters;

    private DatabaseJsonHelper(ArrayList<Counter> counters) {
        this.counters = counters;
        databaseVersion = DBOpenHelper.DATABASE_VERSION;
    }

    public static DatabaseJsonHelper fromLocalDatabase() {
        CountersDatabase database = ApplicationDependencies.getSobrietyDatabase().getCountersDatabase();
        ArrayList<Counter> localCounters = database.getAllCountersWithReasons();

        return new DatabaseJsonHelper(localCounters);
    }

    //TODO
    public static DatabaseJsonHelper fromJson(JSONObject data) {
        return null;
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
}
