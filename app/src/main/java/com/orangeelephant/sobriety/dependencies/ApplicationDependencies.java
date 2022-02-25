package com.orangeelephant.sobriety.dependencies;


import android.app.Application;
import android.content.Context;

import com.orangeelephant.sobriety.database.DatabaseManager;
import com.orangeelephant.sobriety.database.SqlcipherKey;

/**
 * A class to store and retrieve other singletons required
 * by the application
 */
public class ApplicationDependencies {

    private static volatile Application     application;
    private static volatile SqlcipherKey    sqlcipherKey;
    private static volatile DatabaseManager databaseManager;

    private ApplicationDependencies() {}

    public static void init(Application application) {
        ApplicationDependencies.application = application;
    }

    public boolean isInitialised() {
        return application != null;
    }

    public static void setSqlcipherKey(SqlcipherKey sqlcipherKey) {
        ApplicationDependencies.sqlcipherKey = sqlcipherKey;
    }

    public static SqlcipherKey getSqlCipherKey() {
        return sqlcipherKey;
    }

    public static DatabaseManager getDatabaseManager() {
        if (databaseManager == null) {
            databaseManager = new DatabaseManager(application);
        }
        return databaseManager;
    }

    public static Context getApplicationContext() {
        return application.getApplicationContext();
    }
}