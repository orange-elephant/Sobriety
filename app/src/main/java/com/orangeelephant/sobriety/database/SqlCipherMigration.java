package com.orangeelephant.sobriety.database;

import android.content.Context;

import com.orangeelephant.sobriety.logging.LogEvent;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteException;
import net.sqlcipher.database.SQLiteStatement;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class SqlCipherMigration {
    public SqlCipherMigration(Context context) {
        try {
            SQLiteDatabase database = new DBhelper(context).getReadableDatabase("");
            File originalFile = new File(database.getPath());
            byte[] passphrase = new byte[32];
            int version = database.getVersion();
            database.close();
            if (version >= DBhelper.SQL_CIPHER_MIGRATION) {
                migrateDbToSqlcipher(context, originalFile, passphrase, version);
            } else {
                LogEvent.i("Not migrating as version number " + version + " is lower than " +
                        "sqlcipher migration at version number " + DBhelper.SQL_CIPHER_MIGRATION);
            }
        } catch (SQLiteException exception) {
            LogEvent.i("Database is encrypted already");
        } catch (IOException exception) {
            LogEvent.e("IOException attempting to migrate DB to sql cipher", exception);
        }
    }

    /**
     * Sourced from, approach to setting the database version was modified.
     * https://github.com/commonsguy/cwac-saferoom/blob/v1.2.1/saferoom/src/main/java/com/commonsware/cwac/saferoom/SQLCipherUtils.java#L175-L224
     *  *
     *  * Copyright (c) 2012-2017 CommonsWare, LLC
     *  *
     *  * Licensed under the Apache License, Version 2.0 (the "License");
     *  * you may not use this file except in compliance with the License.
     *  * You may obtain a copy of the License at
     *  *
     *  *      http://www.apache.org/licenses/LICENSE-2.0
     *  *
     *  * Unless required by applicable law or agreed to in writing, software
     *  * distributed under the License is distributed on an "AS IS" BASIS,
     *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
     *  * See the License for the specific language governing permissions and
     *  * limitations under the License.
     *
     * Replaces this database with a version encrypted with the supplied
     * passphrase, deleting the original. Do not call this while the database
     * is open, which includes during any Room migrations.
     *
     * @param ctxt a Context
     * @param originalFile a File pointing to the database
     * @param passphrase the passphrase from the user
     * @throws IOException
     */
    public static void migrateDbToSqlcipher(Context ctxt, File originalFile, byte[] passphrase, int version)
            throws IOException {
        SQLiteDatabase.loadLibs(ctxt);

        if (originalFile.exists()) {
            File newFile=File.createTempFile("sqlcipherutils", "tmp",
                    ctxt.getCacheDir());
            SQLiteDatabase db = SQLiteDatabase.openDatabase(newFile.getAbsolutePath(), passphrase,
                    null, SQLiteDatabase.OPEN_READWRITE, null, null);
            final SQLiteStatement st=db.compileStatement("ATTACH DATABASE ? AS plaintext KEY ''");
            st.bindString(1, originalFile.getAbsolutePath());
            st.execute();
            db.rawExecSQL("SELECT sqlcipher_export('main', 'plaintext')");
            db.rawExecSQL("DETACH DATABASE plaintext");
            db.setVersion(version);
            st.close();
            db.close();
            originalFile.delete();
            System.out.println(originalFile);
            newFile.renameTo(originalFile);
            System.out.println(newFile);
        }
        else {
            throw new FileNotFoundException(originalFile.getAbsolutePath()+ " not found");
        }
    }
}
