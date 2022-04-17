package com.orangeelephant.sobriety.counter;

import android.content.ContentValues;
import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

import com.orangeelephant.sobriety.database.DefineTables;
import com.orangeelephant.sobriety.database.helpers.DBOpenHelper;

import net.sqlcipher.database.SQLiteDatabase;

public class CreateNewCounter extends AppCompatActivity {
    private final String _name;
    private final String _reason;
    private final Long _startTime;

    public CreateNewCounter(Context context, String name, String reason, Long startTime) {
        this._name = name;
        this._reason = reason;
        this._startTime = startTime;

        writeToDb(context);
    }

    private void writeToDb(Context context) {
        SQLiteDatabase db = new DBOpenHelper(context).getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DefineTables.Counters.COLUMN_NAME, this._name);
        values.put(DefineTables.Counters.COLUMN_START_TIME, this._startTime);
        long counterRowId = db.insert(DefineTables.Counters.TABLE_NAME_COUNTERS, null, values);

        if (! this._reason.isEmpty()) {
            ContentValues nextValues = new ContentValues();
            nextValues.put(DefineTables.Counters.COLUMN_COUNTER_ID, counterRowId);
            nextValues.put(DefineTables.Counters.COLUMN_SOBRIETY_REASON, this._reason);
            long reasonRowId = db.insert(DefineTables.Counters.TABLE_NAME_REASONS, null, nextValues);
        }
        db.close();
    }
}