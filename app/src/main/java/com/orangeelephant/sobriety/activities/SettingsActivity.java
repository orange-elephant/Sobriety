package com.orangeelephant.sobriety.activities;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.orangeelephant.sobriety.R;
import com.orangeelephant.sobriety.activities.fragments.preferences.PreferenceFragment;
import com.orangeelephant.sobriety.logging.LogEvent;

import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = (SettingsActivity.class.getSimpleName());

    private SharedPreferences sharedPreferences;
    private SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener;

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


        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferenceChangeListener =
                new SharedPreferences.OnSharedPreferenceChangeListener() {
                    @RequiresApi(api = Build.VERSION_CODES.R)
                    @Override
                    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                        if (key.equals("language")) {
                            String language_key = sharedPreferences.getString("language", "none");
                            switch (language_key) {
                                case "en":
                                    Locale localeEN = new Locale("en");
                                    setLocale(localeEN);
                                    break;
                                case "es":
                                    Locale localeES = new Locale("es");
                                    setLocale(localeES);
                                    break;
                                case "de":
                                    Locale localeDE = new Locale("de");
                                    setLocale(localeDE);
                                    break;
                                case "default":
                                default:
                                    Locale default_locale = Resources.getSystem().getConfiguration()
                                            .getLocales().get(0);
                                    Locale localeDefault = new Locale(default_locale.getLanguage());
                                    setLocale(localeDefault);
                                    break;

                            }
                        } else if (key.equals("theme")) {
                            String theme_key = sharedPreferences.getString("theme", "none");
                            switch (theme_key) {
                                case "light":

                                case "dark":

                                default:

                            }
                        } else if (key.equals("fingerprint_lock_enabled")) {

                        }
                    }
                };
        sharedPreferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        sharedPreferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener);
    }

    public void setLocale(Locale locale) {
        Locale.setDefault(locale);
        Resources resources = getResources();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(locale);
        resources.updateConfiguration(configuration, displayMetrics);

        LogEvent.i(TAG, "Language changed to " + locale.getLanguage());
        recreate();
    }

    public void setTheme() {
        this.setTheme();
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    public boolean checkFingerprintLockCapable() {
        //only offering for api 29 and above
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            return false;
        }
        return false;
    }
}