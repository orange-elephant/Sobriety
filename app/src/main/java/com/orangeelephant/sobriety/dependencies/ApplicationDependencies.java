package com.orangeelephant.sobriety.dependencies;


import com.orangeelephant.sobriety.database.SqlcipherKey;

/**
 * A singleton class to store and retrieve other singletons required
 * by the application
 */
public class ApplicationDependencies {
    private static ApplicationDependencies applicationDependencies = null;

    private volatile SqlcipherKey sqlcipherKey;

    private ApplicationDependencies() {}

    public static ApplicationDependencies getApplicationDependencies() {
        if (applicationDependencies == null) {
            applicationDependencies = new ApplicationDependencies();
        }
        return applicationDependencies;
    }

    public void setSqlcipherKey(SqlcipherKey sqlcipherKey) {
        this.sqlcipherKey = sqlcipherKey;
    }

    public SqlcipherKey getSqlCipherKey() {
        return sqlcipherKey;
    }
}
