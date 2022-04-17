package com.orangeelephant.sobriety.database;

import android.content.Context;

import com.orangeelephant.sobriety.database.helpers.DBOpenHelper;
import com.orangeelephant.sobriety.dependencies.ApplicationDependencies;
import com.orangeelephant.sobriety.logging.LogEvent;
import com.orangeelephant.sobriety.util.SobrietyPreferences;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteException;

public class DatabaseManager {

    private DatabaseManager() {}

    /**
     * A method that loads the libraries required for sqlCipher to work
     * to be run at app launch
     * @param context the context needed by sqlCipher
     */
    public static void loadSqlCipherLibs(Context context) {
        SQLiteDatabase.loadLibs(context);
    }

    public static void attemptToCreateEncryptedDatabase(Context context) {
        SqlcipherKey sqlcipherKey = ApplicationDependencies.getSqlCipherKey();
        try {
            new DBOpenHelper(context).getReadableDatabase(sqlcipherKey.getSqlCipherKey());
            SobrietyPreferences.setIsDatabaseEncrypted(true);
            LogEvent.i("A new encrypted database was created");
        } catch (SQLiteException exception) {
            LogEvent.i("Couldn't create a database with the provided key, an unencrypted database probably exists.");
            new SqlCipherMigration(context, new DBOpenHelper(context));
        }
    }
}
