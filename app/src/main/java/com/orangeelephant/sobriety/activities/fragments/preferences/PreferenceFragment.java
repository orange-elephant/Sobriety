package com.orangeelephant.sobriety.activities.fragments.preferences;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.orangeelephant.sobriety.BuildConfig;
import com.orangeelephant.sobriety.R;
import com.orangeelephant.sobriety.logging.LogEvent;

public class PreferenceFragment extends PreferenceFragmentCompat {
    private static final String TAG = PreferenceFragment.class.getSimpleName();

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences);
        requirePreference("app_version").setSummary(BuildConfig.VERSION_NAME);

        Preference languagePref = requirePreference("language");
        languagePref.setOnPreferenceChangeListener(((preference, newValue) -> {
            requireActivity().recreate();
            LogEvent.i(TAG, String.format("Language changed: %s", newValue));
            return true;
        }));
    }

    private @NonNull Preference requirePreference(String key) {
        Preference required = findPreference(key);
        if (required == null) {
            throw new IllegalArgumentException("No such preference found");
        }

        return required;
    }
}
