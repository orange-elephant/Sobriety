package com.orangeelephant.sobriety.dependencies;


import android.app.Application;
import android.content.Context;

import com.orangeelephant.sobriety.database.SqlcipherKey;

/**
 * A class to store and retrieve other singletons required
 * by the application
 */
public class ApplicationDependencies {

    private static volatile Application     application;
    private static volatile SqlcipherKey    sqlcipherKey;

    private ApplicationDependencies() {}

    public static void init(Application application) {
        if (ApplicationDependencies.application == null) {
            ApplicationDependencies.application = application;
        }
    }

    public boolean isInitialised() {
        return application != null;
    }

    public static void setSqlcipherKey(SqlcipherKey sqlcipherKey) {
        ApplicationDependencies.sqlcipherKey = sqlcipherKey;
    }

    public static SqlcipherKey getSqlCipherKey() {
        if (ApplicationDependencies.sqlcipherKey == null) {
            throw new IllegalStateException("SQLCipherKey has not been loaded");
        }
        return sqlcipherKey;
    }

    public static Context getApplicationContext() {
        return application.getApplicationContext();
    }
}
