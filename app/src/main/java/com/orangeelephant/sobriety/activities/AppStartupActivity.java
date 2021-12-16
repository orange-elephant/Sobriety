package com.orangeelephant.sobriety.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import com.orangeelephant.sobriety.database.DBhelper;
import com.orangeelephant.sobriety.database.SqlCipherMigration;
import com.orangeelephant.sobriety.database.SqlcipherKey;
import com.orangeelephant.sobriety.logging.LogEvent;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteException;

public class AppStartupActivity extends AppCompatActivity {
    private static final String sharedPreferenceFile = "com.orangeelephant.sobriety.database_key";
    private static final String isEncrypted = "isEncrypted";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = this.getSharedPreferences(sharedPreferenceFile, this.MODE_PRIVATE);

        //load libraries necessary for sqlcipher library to function
        SQLiteDatabase.loadLibs(this);

        // if shared preferences indicates db isn't encrypted, attempt migration
        if (! sharedPreferences.getBoolean(isEncrypted, false)
                && DBhelper.DATABASE_VERSION >= DBhelper.SQL_CIPHER_MIGRATION) {
            attemptToCreateEncryptedDatabase();
        }

        preferenceChangeListener =
                new SharedPreferences.OnSharedPreferenceChangeListener() {
                    @Override
                    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                        recreate();
                    }
                };
        sharedPreferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener);

        Intent intent = new Intent(AppStartupActivity.this, HomeScreenActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void attemptToCreateEncryptedDatabase() {
        try {
            SqlcipherKey sqlcipherKey = new SqlcipherKey(this);
            SQLiteDatabase db = new DBhelper(this).getReadableDatabase(sqlcipherKey.getSqlCipherKey());
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(isEncrypted, true);
            editor.commit();
            LogEvent.i("A new encrypted database was created");
        } catch (SQLiteException exception) {
            LogEvent.i("Couldn't create a database with the provided key, an unencrypted database probably exists.");
            new SqlCipherMigration(this);
        } catch (Exception exception) {
            LogEvent.e("Exception loading sqlcipher key", exception);
        }
    }
}