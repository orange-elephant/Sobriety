package com.orangeelephant.sobriety.ui;

import android.content.res.Configuration;

import androidx.annotation.StyleRes;

import com.orangeelephant.sobriety.R;
import com.orangeelephant.sobriety.dependencies.ApplicationDependencies;

public enum Themes {
    SYSTEM,
    LIGHT_MODE,
    DARK_MODE;

    public @StyleRes int toStyle() {
        switch (this) {
            case SYSTEM:
                int currentNight = ApplicationDependencies.getApplicationContext().
                        getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

                if (currentNight == Configuration.UI_MODE_NIGHT_YES) {
                    return R.style.Theme_Sobriety_Dark;
                }
                return R.style.Theme_Sobriety_Light;
            case LIGHT_MODE:
                return R.style.Theme_Sobriety_Light;
            case DARK_MODE:
            default:
                return R.style.Theme_Sobriety_Dark;
        }
    }
}
