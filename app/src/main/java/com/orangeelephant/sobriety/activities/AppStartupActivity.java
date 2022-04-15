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

import com.orangeelephant.sobriety.database.SqlcipherKey;
import com.orangeelephant.sobriety.dependencies.ApplicationDependencies;
import com.orangeelephant.sobriety.R;
import com.orangeelephant.sobriety.util.SobrietyPreferences;

import java.util.concurrent.Executor;

public class AppStartupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initialise ApplicationDependencies
        ApplicationDependencies.init(getApplication());

        if (SobrietyPreferences.getFingerprintLockEnabled()
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            fingerprintUnlock();
        } else {
            try {
                SqlcipherKey sqlcipherKey = new SqlcipherKey(this);
                ApplicationDependencies.setSqlcipherKey(sqlcipherKey);
                ApplicationDependencies.getDatabaseManager();

                if (SobrietyPreferences.getIsFirstOpen()) {
                    onFirstOpen();
                }

            } catch (Exception exception) {
                exception.printStackTrace();
            }
            Intent intent = new Intent(AppStartupActivity.this, HomeScreenActivity.class);
            startActivity(intent);
        }

    }

    private void onFirstOpen() {
        SobrietyPreferences.setIsFirstOpen(false);
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

                try {
                    SqlcipherKey sqlcipherKey = new SqlcipherKey(AppStartupActivity.this);
                    ApplicationDependencies.setSqlcipherKey(sqlcipherKey);
                    ApplicationDependencies.getDatabaseManager();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
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