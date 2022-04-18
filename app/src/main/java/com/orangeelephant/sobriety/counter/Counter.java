package com.orangeelephant.sobriety.counter;

import com.orangeelephant.sobriety.R;
import com.orangeelephant.sobriety.dependencies.ApplicationDependencies;

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
}
