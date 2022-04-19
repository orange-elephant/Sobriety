package com.orangeelephant.sobriety.logging;

import android.util.Log;

import com.orangeelephant.sobriety.dependencies.ApplicationDependencies;

public class LogEvent {

    // error
    public static void e (String Tag, String message, Exception exception) {
        Log.e(Tag, message, exception);
        ApplicationDependencies.getLogger().logToDb(Tag, message, exception.getStackTrace().toString());
    }

    // warning
    public static void w (String Tag, String message) {
        Log.w(Tag, message);
        ApplicationDependencies.getLogger().logToDb(Tag, message, null);
    }

    // information
    public static void i(String Tag, String message) {
        Log.i(Tag, message);
        ApplicationDependencies.getLogger().logToDb(Tag, message, null);
    }

    // debug
    public static void d (String Tag, String message) {
        Log.d(Tag, message);
        ApplicationDependencies.getLogger().logToDb(Tag, message, null);
    }

    // verbose
    public static void v (String Tag, String message) {
        Log.v(Tag, message);
        ApplicationDependencies.getLogger().logToDb(Tag, message, null);
    }

    // failure
    public static void wtf (String Tag, String message) {
        Log.wtf(Tag, message);
        ApplicationDependencies.getLogger().logToDb(Tag, message, null);
    }
}
