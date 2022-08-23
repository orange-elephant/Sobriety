package com.orangeelephant.sobriety.activities;

import android.os.Bundle;

import com.orangeelephant.sobriety.R;
import com.orangeelephant.sobriety.activities.fragments.preferences.PreferenceFragment;

public class SettingsActivity extends SobrietyActivity {

    private static final String TAG = (SettingsActivity.class.getSimpleName());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new PreferenceFragment())
                    .commit();
        }
    }
}