package com.orangeelephant.sobriety.activities.fragments.preferences;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.orangeelephant.sobriety.R;

public class PreferenceFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences);
    }
}
