package com.orangeelephant.sobriety.database.helpers;


import android.content.Context;

import com.orangeelephant.sobriety.database.SqlcipherKey;
import com.orangeelephant.sobriety.dependencies.ApplicationDependencies;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;


public abstract class BaseDbHelper extends SQLiteOpenHelper {
    private final int SQL_CIPHER_MIGRATION;
    private final int DATABASE_VERSION;
    private final Context context;
    private final SqlcipherKey keyManager;

    public BaseDbHelper(Context context, String databaseName, int databaseVersion, int sqlCipherMigration) {
        super(context, databaseName, null, databaseVersion);

        this.DATABASE_VERSION = databaseVersion;
        this.SQL_CIPHER_MIGRATION = sqlCipherMigration;
        this.context = context;
        this.keyManager = ApplicationDependencies.getSqlCipherKey();
    }

    public SQLiteDatabase getReadableDatabase() {
        byte[] password = keyManager.getSqlCipherKey();
        if (DATABASE_VERSION >= SQL_CIPHER_MIGRATION && keyManager.getIsEncrypted()) {
            return super.getReadableDatabase(password);
        } else {
            return super.getReadableDatabase("");
        }
    }

    public SQLiteDatabase getWritableDatabase() {
        byte[] password = keyManager.getSqlCipherKey();
        if (DATABASE_VERSION >= SQL_CIPHER_MIGRATION && keyManager.getIsEncrypted()) {
            return super.getWritableDatabase(password);
        } else {
            return super.getWritableDatabase("");
        }
    }

    public int getSqlCipherMigrationVersion() {
        return SQL_CIPHER_MIGRATION;
    }
}