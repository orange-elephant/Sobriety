package com.orangeelephant.sobriety.activities;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.orangeelephant.sobriety.database.helpers.SqlCipherHelper;
import com.orangeelephant.sobriety.dependencies.ApplicationDependencies;
import com.orangeelephant.sobriety.util.SobrietyPreferences;

import java.util.Locale;
import java.util.Objects;

public class SobrietyActivity extends AppCompatActivity {
    private static final String TAG = (SobrietyActivity.class.getSimpleName());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (!ApplicationDependencies.isInitialised()) {
            initialise();
        }
        super.onCreate(savedInstanceState);

        setLocale();
    }

    private void setLocale() {
        Locale locale;
        String lang = SobrietyPreferences.getLanguage();
        if (Objects.equals(lang, "default")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                locale = Resources.getSystem().getConfiguration().getLocales().get(0);
            } else {
                locale = Resources.getSystem().getConfiguration().locale;
            }
        } else {
            locale = new Locale(lang);
        }
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }

    private void initialise() {
        //initialise ApplicationDependencies
        ApplicationDependencies.init(getApplication());
        SqlCipherHelper.loadSqlCipherLibs(this);
    }
}
