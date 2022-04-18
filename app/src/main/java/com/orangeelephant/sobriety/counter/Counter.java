package com.orangeelephant.sobriety.counter;

import com.orangeelephant.sobriety.R;
import com.orangeelephant.sobriety.dependencies.ApplicationDependencies;

import java.util.ArrayList;
import java.util.Date;
import java.util.NoSuchElementException;

public class Counter {
    private final int _id;
    private final String name;
    private final long start_time_in_millis;
    private final long record_time_sober_in_millis;
    private final ArrayList<Reason> reasons;

    public Counter(int _id, String name, Long start_time_in_millis, Long record_time_sober_in_millis,
                   ArrayList<Reason> reasons) {
        this._id = _id;
        this.name = name;
        this.start_time_in_millis = start_time_in_millis;
        this.record_time_sober_in_millis = record_time_sober_in_millis;
        this.reasons = reasons;
    }

    public String getName() {
        return this.name;
    }

    public int get_id(){
        return this._id;
    }

    public Long getRecordTimeSoberInMillis() {
        return Math.max(getCurrentTimeSoberInMillis(), record_time_sober_in_millis);
    }

    public Long getCurrentTimeSoberInMillis() {
        Date now = new Date();
        long timeNow = now.getTime();

        long timeSoberInMillis = timeNow - this.start_time_in_millis;

        return timeSoberInMillis;
    }

    public Reason getSobrietyReason() {
        try {
            return this.reasons.get(0);
        } catch (NoSuchElementException elementException) {
            return null;
        }
    }

    public ArrayList<Reason> getReasons() {
        return this.reasons ;
    }

    public long getStart_time_in_millis() {
        return start_time_in_millis;
    }

    public long getRecord_time_sober_in_millis() {
        return record_time_sober_in_millis;
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
        String timeSoberMessage = String.format(timeSoberString, elapsedDays, elapsedHours,
                elapsedMinutes, elapsedSeconds);

        return timeSoberMessage;
    }
}
