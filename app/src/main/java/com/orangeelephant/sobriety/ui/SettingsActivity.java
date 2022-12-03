package com.orangeelephant.sobriety.ui;

import android.os.Bundle;

import com.orangeelephant.sobriety.R;
import com.orangeelephant.sobriety.ui.fragments.preferences.PreferenceFragment;

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

        setSupportActionBar(findViewById(R.id.toolbar));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(R.string.settings);
        }
    }
}