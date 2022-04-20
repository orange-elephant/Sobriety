package com.orangeelephant.sobriety.database;

import android.content.Context;

import com.orangeelephant.sobriety.database.helpers.DBOpenHelper;
import com.orangeelephant.sobriety.dependencies.ApplicationDependencies;
import com.orangeelephant.sobriety.logging.LogEvent;
import com.orangeelephant.sobriety.util.SobrietyPreferences;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteException;

public class DatabaseManager {

    private static final String TAG = (DatabaseManager.class.getSimpleName());

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
            if (DBOpenHelper.DATABASE_VERSION >= DBOpenHelper.SQL_CIPHER_MIGRATION) {
                new DBOpenHelper(context).getReadableDatabase(sqlcipherKey.getSqlCipherKey());
                SobrietyPreferences.setIsDatabaseEncrypted(true);
                LogEvent.i(TAG, "A new encrypted database was created");
                return;
            }
            ApplicationDependencies.getSobrietyDatabase().getWritableDatabase();
            LogEvent.w(TAG, "Not creating encrypted database as version is pre-migration");
        } catch (SQLiteException exception) {
            LogEvent.i(TAG, "Couldn't create a database with the provided key, an unencrypted database probably exists.");
            SqlCipherMigration.migrate(context, new DBOpenHelper(context));
        }
    }
}
