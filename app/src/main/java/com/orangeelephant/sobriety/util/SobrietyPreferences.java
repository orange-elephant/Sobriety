package com.orangeelephant.sobriety.util;

import android.content.Context;

import android.content.SharedPreferences;


import com.orangeelephant.sobriety.dependencies.ApplicationDependencies;

/**
 * A class which provides methods to simplify the setting and accessing of preferences
 * and settings related to the app
 */
public final class SobrietyPreferences {
    private static final String sharedPreferenceFile = "com.orangeelephant.sobriety_preferences";

    private static final String ENCRYPTION_FIXED_IV = "fixedIv";
    private static final String BACKUP_ENCRYPTION_KEY = "backupEncryptionKey";
    private static final String BACKUP_ENCRYPTION_PASSPHRASE_SALT = "passphraseSalt";
    private static final String SQLCIPHER_ENCRYPTION_KEY = "sqlcipherEncryptionKey";
    private static final String IS_DATABASE_ENCRYPTED = "isEncrypted";
    private static final String IS_FIRST_OPEN = "isFirstOpen";
    private static final String SQLCIPHER_KEY_WITH_IV = "sqlCipherKeyWithIv";
    private static final String BACKUP_KEY_WITH_IV = "backupKeyWithIv";

    private static final String LANGUAGE = "language";
    private static final String THEME = "theme";
    private static final String FINGERPRINT_LOCK_ENABLED = "fingerprintLockEnabled";

    public static void setEncryptionFixedIv(String fixedIv) {
        setStringPreference(ENCRYPTION_FIXED_IV, fixedIv);
    }

    public static String getEncryptionFixedIv() {
        return getStringPreference(ENCRYPTION_FIXED_IV, "");
    }

    public static void setSqlcipherEncryptionKey(String sqlcipherEncryptionKey) {
        setStringPreference(SQLCIPHER_ENCRYPTION_KEY, sqlcipherEncryptionKey);
    }

    public static String getBackupEncryptionKey() {
        return getStringPreference(BACKUP_ENCRYPTION_KEY, "");
    }

    public static void setBackupEncryptionKey(String backupEncryptionKey) {
        setStringPreference(BACKUP_ENCRYPTION_KEY, backupEncryptionKey);
    }

    public static String getSqlcipherEncryptionKey() {
        return getStringPreference(SQLCIPHER_ENCRYPTION_KEY, "");
    }

    public static void setIsDatabaseEncrypted(boolean isDatabaseEncrypted) {
        setBooleanPreference(IS_DATABASE_ENCRYPTED, isDatabaseEncrypted);
    }

    public static boolean getIsDatabaseEncrypted() {
        return getBooleanPreference(IS_DATABASE_ENCRYPTED, false);
    }

    public static void setSqlcipherKeyWithIv(String  sqlcipherKeyWithIv) {
        setStringPreference(SQLCIPHER_KEY_WITH_IV, sqlcipherKeyWithIv);
    }

    public static String getSqlcipherKeyWithIv() {
        return getStringPreference(SQLCIPHER_KEY_WITH_IV, "");
    }

    public static void setBackupKeyWithIv(String backupKeyWithIv) {
        setStringPreference(BACKUP_KEY_WITH_IV, backupKeyWithIv);
    }

    public static String getBackupKeyWithIv() {
        return getStringPreference(BACKUP_KEY_WITH_IV, "");
    }

    public static void setLanguage(String language) {
        setStringPreference(LANGUAGE, language);
    }

    public static String getLanguage() {
        return getStringPreference(LANGUAGE, "default");
    }

    public static void setTheme(String theme) {
        setStringPreference(THEME, theme);
    }

    public static String getTheme() {
        return getStringPreference(THEME, "default");
    }

    public static void setBackupEncryptionPassphraseSalt(String salt) {
        setStringPreference(BACKUP_ENCRYPTION_PASSPHRASE_SALT, salt);
    }

    public static String getBackupEncryptionPassphraseSalt() {
        return getStringPreference(BACKUP_ENCRYPTION_PASSPHRASE_SALT, "");
    }

    public static void setIsFirstOpen(boolean isFirstOpen) {
        setBooleanPreference(IS_FIRST_OPEN, isFirstOpen);
    }

    public static boolean getIsFirstOpen() {
        return getBooleanPreference(IS_FIRST_OPEN, true);
    }

    public static void setFingerprintLockEnabled(boolean fingerprintLockEnabled) {
        setBooleanPreference(FINGERPRINT_LOCK_ENABLED, fingerprintLockEnabled);
    }

    public static boolean getFingerprintLockEnabled() {
        return getBooleanPreference(FINGERPRINT_LOCK_ENABLED, false);
    }

    private static void setBooleanPreference(String prefName, boolean toSave) {
        SharedPreferences preferences = getSharedPreferences();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(prefName, toSave);
        editor.apply();
    }

    private static boolean getBooleanPreference(String prefName, boolean defaultValue) {
        return getSharedPreferences().getBoolean(prefName, defaultValue);
    }

    private static void setStringPreference(String prefName, String toSave) {
        SharedPreferences preferences = getSharedPreferences();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(prefName, toSave);
        editor.apply();
    }

    private static String getStringPreference(String prefName, String defaultValue) {
        return getSharedPreferences().getString(prefName, defaultValue);
    }

    public static SharedPreferences getSharedPreferences() {
        return ApplicationDependencies.getApplicationContext()
                .getSharedPreferences(sharedPreferenceFile, Context.MODE_PRIVATE);
    }
}
