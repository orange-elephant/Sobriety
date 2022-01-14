package com.orangeelephant.sobriety.activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.biometrics.BiometricPrompt;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.widget.Toast;

import com.orangeelephant.sobriety.database.DBhelper;
import com.orangeelephant.sobriety.database.SqlCipherMigration;
import com.orangeelephant.sobriety.database.SqlcipherKey;
import com.orangeelephant.sobriety.logging.LogEvent;
import com.orangeelephant.sobriety.R;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteException;

import java.util.concurrent.Executor;

public class AppStartupActivity extends AppCompatActivity {
    private static final String sharedPreferenceFile = "com.orangeelephant.sobriety_preferences";
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

        /*preferenceChangeListener =
                new SharedPreferences.OnSharedPreferenceChangeListener() {
                    @Override
                    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                        recreate();
                    }
                };
        sharedPreferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener);*/

        if (sharedPreferences.getBoolean("fingerprint_lock_enabled", false)
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            fingerprintUnlock();
        } else {
            //start app
            Intent intent = new Intent(AppStartupActivity.this, HomeScreenActivity.class);
            startActivity(intent);
        }

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

    @RequiresApi(api = Build.VERSION_CODES.P)
    private void fingerprintUnlock() {
        Executor executor;
        BiometricPrompt biometricPrompt;

        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt.Builder(this)
                .setTitle(this.getString(R.string.AppStartup_unlock_title))
                .setSubtitle(this.getString(R.string.AppStartup_unlock_subtitle))
                .setNegativeButton(getString(R.string.AppStartup_unlock_negative_button), executor, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        moveTaskToBack(true);
                        AppStartupActivity.super.onBackPressed();
                    }
                })
                .build();

        BiometricPrompt.AuthenticationCallback callback = new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode,
                                              @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(getApplicationContext(),
                        getApplicationContext().getString(R.string.AppStartup_unlock_error) + errString, Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(getApplicationContext(),
                        getApplicationContext().getString(R.string.AppStartup_unlock_successful),
                        Toast.LENGTH_SHORT)
                        .show();

                //start app
                Intent intent = new Intent(AppStartupActivity.this, HomeScreenActivity.class);
                startActivity(intent);
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getApplicationContext(),
                        getApplicationContext().getString(R.string.AppStartup_unlock_failed),
                        Toast.LENGTH_SHORT)
                        .show();
            }
        };

        biometricPrompt.authenticate(new CancellationSignal(), executor, callback);
    }
}