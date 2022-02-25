package com.orangeelephant.sobriety.backup;

import android.content.Context;

import com.orangeelephant.sobriety.dependencies.ApplicationDependencies;

import net.sqlcipher.database.SQLiteDatabase;

public abstract class BackupBase {
    protected Context context;
    protected SQLiteDatabase database;

    protected BackupBase() {
        this.context = ApplicationDependencies.getApplicationContext();
    }

    public abstract void setPassphrase(String passphrase);
}
