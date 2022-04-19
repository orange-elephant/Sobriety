package com.orangeelephant.sobriety.logging;

import android.util.Log;

public class LogEvent {

    // error
    public static void e (String Tag, String message, Exception exception) {
        Log.e(Tag, message, exception);
    }

    // warning
    public static void w (String Tag, String message) {
        Log.w(Tag, message);
    }

    // information
    public static void i(String Tag, String message) {
        Log.i(Tag, message);
    }

    // debug
    public static void d (String Tag, String message) {
        Log.d(Tag, message);
    }

    // verbose
    public static void v (String Tag, String message) {
        Log.v(Tag, message);
    }

    // failure
    public static void wtf (String Tag, String message) {
        Log.wtf(Tag, message);
    }
}
