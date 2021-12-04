package com.orangeelephant.sobriety.logging;

import android.util.Log;

public class LogEvent {
    private static final String log_tag = "Sobriety";

    // error
    public static void e (String message, Exception exception) {
        Log.e(log_tag, message, exception);
    }

    // warning
    public static void w (String message) {
        Log.w(log_tag, message);
    }

    // information
    public static void i(String message) {
        Log.i(log_tag, message);
    }

    // debug
    public static void d (String message) {
        Log.d(log_tag, message);
    }

    // verbose
    public static void v (String message) {
        Log.v(log_tag, message);
    }

    // failure
    public static void wtf (String message) {
        Log.wtf(log_tag, message);
    }
}
