package com.orangeelephant.sobriety.database;

import android.content.Context;

import com.orangeelephant.sobriety.database.helpers.CountersDatabaseHelper;
import com.orangeelephant.sobriety.dependencies.ApplicationDependencies;
import com.orangeelephant.sobriety.logging.LogEvent;
import com.orangeelephant.sobriety.util.SobrietyPreferences;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteException;

public class DatabaseManager {
    private final Context context;

    private final SqlcipherKey sqlcipherKey;

    public DatabaseManager() {
        this.context = ApplicationDependencies.getApplicationContext();
        this.sqlcipherKey = ApplicationDependencies.getSqlCipherKey();

        //load libraries necessary for sqlcipher library to function
        SQLiteDatabase.loadLibs(context);

        // if shared preferences indicates db isn't encrypted, attempt migration
        if (!SobrietyPreferences.getIsDatabaseEncrypted()) {
            attemptToCreateEncryptedDatabase();
        }
    }

    private void attemptToCreateEncryptedDatabase() {
        try {
            SQLiteDatabase db = new CountersDatabaseHelper(context).getReadableDatabase(sqlcipherKey.getSqlCipherKey());
            SobrietyPreferences.setIsDatabaseEncrypted(true);
            LogEvent.i("A new encrypted database was created");
        } catch (SQLiteException exception) {
            LogEvent.i("Couldn't create a database with the provided key, an unencrypted database probably exists.");
            new SqlCipherMigration(context, new CountersDatabaseHelper(context));
        } catch (Exception exception) {
            LogEvent.e("Exception loading sqlcipher key", exception);
        }
    }
}
