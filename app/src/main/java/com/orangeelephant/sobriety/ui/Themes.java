package com.orangeelephant.sobriety.ui;

import androidx.annotation.StyleRes;

import com.orangeelephant.sobriety.R;

public enum Themes {
    SYSTEM,
    LIGHT_MODE,
    DARK_MODE;

    public @StyleRes int toStyle() {
        switch (this) {
            case LIGHT_MODE:
            case SYSTEM:
                return R.style.Theme_Sobriety_Light;
            case DARK_MODE:
            default:
                return R.style.Theme_Sobriety_Dark;
        }
    }
}
