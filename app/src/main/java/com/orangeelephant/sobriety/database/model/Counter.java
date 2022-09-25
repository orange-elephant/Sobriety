package com.orangeelephant.sobriety.database.model;

import com.orangeelephant.sobriety.R;
import com.orangeelephant.sobriety.dependencies.ApplicationDependencies;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

public class Counter {
    private int id;
    private String name;
    private long startTimeInMillis;
    private long recordTimeSoberInMillis;
    private final ArrayList<Reason> reasons;

    public Counter(int id, String name, long startTimeInMillis, long recordTimeSoberInMillis,
                   ArrayList<Reason> reasons) {
        this.id = id;
        this.name = name;
        this.startTimeInMillis = startTimeInMillis;
        this.recordTimeSoberInMillis = recordTimeSoberInMillis;
        this.reasons = reasons;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Reason> getReasons() {
        return reasons;
    }

    public void addReasons(ArrayList<Reason> newReasons) {
        reasons.addAll(newReasons);
    }

    public long getStartTimeInMillis() {
        return startTimeInMillis;
    }

    public void setStartTimeInMillis(long startTimeInMillis) {
        this.startTimeInMillis = startTimeInMillis;
    }

    public long getRecordTimeSoberInMillis() {
        return Math.max(getCurrentTimeSoberInMillis(), recordTimeSoberInMillis);
    }

    public void setRecordTimeSoberInMillis(long recordTimeSoberInMillis) {
        this.recordTimeSoberInMillis = recordTimeSoberInMillis;
    }

    public long getCurrentTimeSoberInMillis() {
        Date now = new Date();
        long timeNow = now.getTime();

        return timeNow - startTimeInMillis;
    }

    public static String getTimeSoberMessage(Long timeSoberInMillis) {
        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = timeSoberInMillis / daysInMilli;
        timeSoberInMillis = timeSoberInMillis % daysInMilli;

        long elapsedHours = timeSoberInMillis / hoursInMilli;
        timeSoberInMillis = timeSoberInMillis % hoursInMilli;

        long elapsedMinutes = timeSoberInMillis / minutesInMilli;
        timeSoberInMillis = timeSoberInMillis % minutesInMilli;

        long elapsedSeconds = timeSoberInMillis / secondsInMilli;

        String timeSoberString = ApplicationDependencies.getApplicationContext()
                .getString(R.string.CounterViewActivity_counter_message_long);

        return String.format(timeSoberString, elapsedDays, elapsedHours, elapsedMinutes, elapsedSeconds);
    }

    public JSONObject toJson() throws JSONException {
        JSONObject counter = new JSONObject();

        counter.put("Id", id);
        counter.put("Name", name);
        counter.put("StartTimeMillis", startTimeInMillis);
        counter.put("RecordTimeSoberMillis", recordTimeSoberInMillis);

        JSONArray jsonReasons = new JSONArray();
        for (Reason reason: this.reasons) {
            jsonReasons.put(reason.toJson());
        }
        counter.put("Reasons", jsonReasons);

        return counter;
    }

    public static Counter fromJson(JSONObject jsonCounter) throws JSONException {
        int id = jsonCounter.getInt("Id");
        String name = jsonCounter.getString("Name");
        long startTime = jsonCounter.getLong("StartTimeMillis");
        long recordTime = jsonCounter.getLong("RecordTimeSoberMillis");

        JSONArray jsonReasons = jsonCounter.getJSONArray("Reasons");
        ArrayList<Reason> reasons = new ArrayList<>();
        for (int i = 0; i < jsonReasons.length(); i++) {
            JSONObject reason = jsonReasons.getJSONObject(i);
            reasons.add(Reason.fromJson(reason));
        }

        return new Counter(id, name, startTime, recordTime, reasons);
    }
}
