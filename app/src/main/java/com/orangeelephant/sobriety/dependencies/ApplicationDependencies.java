package com.orangeelephant.sobriety.dependencies;

import android.app.Application;
import android.content.Context;

import com.orangeelephant.sobriety.database.SobrietyDatabase;
import com.orangeelephant.sobriety.database.SqlcipherKey;
import com.orangeelephant.sobriety.logging.Logger;

/**
 * A class to store and retrieve other singletons required
 * by the application
 */
public class ApplicationDependencies {

    private static volatile Application      application;
    private static volatile SqlcipherKey     sqlcipherKey;
    private static volatile Logger           logger;
    private static volatile SobrietyDatabase sobrietyDatabase;

    private ApplicationDependencies() {}

    public static void init(Application application) {
        if (ApplicationDependencies.application == null) {
            ApplicationDependencies.application = application;
        }
    }

    public static boolean isInitialised() {
        return application != null;
    }

    public static void setSqlcipherKey(SqlcipherKey sqlcipherKey) {
        ApplicationDependencies.sqlcipherKey = sqlcipherKey;
        logger.startLoggerThread();
    }

    public static SqlcipherKey getSqlCipherKey() {
        if (sqlcipherKey == null) {
            throw new IllegalStateException("SQLCipherKey has not been loaded");
        }
        return sqlcipherKey;
    }

    public static Logger getLogger() {
        if (logger == null) {
            logger = Logger.getInstance();
        }
        return logger;
    }

    public static SobrietyDatabase getSobrietyDatabase() {
        if (sobrietyDatabase == null) {
            sobrietyDatabase = new SobrietyDatabase(getApplicationContext());
        }
        return sobrietyDatabase;
    }

    public static Context getApplicationContext() {
        return application.getApplicationContext();
    }
}
