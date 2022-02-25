package com.orangeelephant.sobriety.database;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;

import com.orangeelephant.sobriety.database.helpers.CountersDatabaseHelper;
import com.orangeelephant.sobriety.dependencies.ApplicationDependencies;
import com.orangeelephant.sobriety.logging.LogEvent;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteException;

import java.util.ArrayList;

public class DatabaseManager {
    private static final String sharedPreferenceFile = "com.orangeelephant.sobriety_preferences";
    private static final String isEncrypted = "isEncrypted";
    private final Context context;

    private final SharedPreferences sharedPreferences;
    private final SqlcipherKey sqlcipherKey;

    public DatabaseManager(Application application) {
        this.context = application.getApplicationContext();
        this.sqlcipherKey = ApplicationDependencies.getSqlCipherKey();

        //load libraries necessary for sqlcipher library to function
        SQLiteDatabase.loadLibs(context);

        sharedPreferences = context.getSharedPreferences(sharedPreferenceFile, Context.MODE_PRIVATE);
        // if shared preferences indicates db isn't encrypted, attempt migration
        if (! sharedPreferences.getBoolean(isEncrypted, false)) {
            attemptToCreateEncryptedDatabase();
        }
    }

    private void attemptToCreateEncryptedDatabase() {
        try {
            SQLiteDatabase db = new CountersDatabaseHelper(context).getReadableDatabase(sqlcipherKey.getSqlCipherKey());
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(isEncrypted, true);
            editor.commit();
            LogEvent.i("A new encrypted database was created");
        } catch (SQLiteException exception) {
            LogEvent.i("Couldn't create a database with the provided key, an unencrypted database probably exists.");
            new SqlCipherMigration(context, new CountersDatabaseHelper(context));
        } catch (Exception exception) {
            LogEvent.e("Exception loading sqlcipher key", exception);
        }
    }

    public static ArrayList<String> getTableNames(SQLiteDatabase db) {
        String sql = "SELECT name FROM sqlite_master WHERE type='table';";
        Cursor cursor = db.rawQuery(sql, null);
        ArrayList<String> tableNames = new ArrayList<>();
        while (cursor.moveToNext()) {
            tableNames.add(cursor.getString(0));
        }

        return tableNames;
    }
}
