package com.orangeelephant.sobriety.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.biometrics.BiometricPrompt;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import com.orangeelephant.sobriety.R;
import com.orangeelephant.sobriety.database.SqlcipherKey;
import com.orangeelephant.sobriety.database.helpers.SqlCipherHelper;
import com.orangeelephant.sobriety.dependencies.ApplicationDependencies;
import com.orangeelephant.sobriety.logging.LogEvent;
import com.orangeelephant.sobriety.util.SobrietyPreferences;

import java.security.KeyStoreException;
import java.util.concurrent.Executor;

public class UnlockActivity extends SobrietyActivity {
    private static final String TAG = (UnlockActivity.class.getSimpleName());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        unlock();

        if (SobrietyPreferences.getIsFirstOpen()) {
            onFirstOpen();
        }

        if (!SobrietyPreferences.getIsDatabaseEncrypted()) {
            SqlCipherHelper.attemptToCreateEncryptedDatabase(this);
        }
    }

    private void unlock() {
        if (SobrietyPreferences.getFingerprintLockEnabled()
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            fingerprintUnlock();
        } else {
            uponUnlock();
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

                uponUnlock();
                finish();
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

    private void uponUnlock() {
        try {
            SqlcipherKey sqlcipherKey = new SqlcipherKey();
            ApplicationDependencies.setSqlcipherKey(sqlcipherKey);

            Intent intent = new Intent(this, HomeScreenActivity.class);
            startActivity(intent);
        } catch (KeyStoreException e) {
            Toast t = new Toast(this);
            t.setText("Unable to start app due to exception loading sqlCipherKey");
            t.show();

            LogEvent.e(TAG, "Unable to start app due to exception loading sqlCipherKey", e);
            finish();
        }
    }


    private void onFirstOpen() {
        LogEvent.i(TAG,"Running first open tasks");
        SqlCipherHelper.attemptToCreateEncryptedDatabase(this);

        SobrietyPreferences.setIsFirstOpen(false);
    }
}
