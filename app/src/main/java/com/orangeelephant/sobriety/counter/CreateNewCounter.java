package com.orangeelephant.sobriety.counter;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.orangeelephant.sobriety.database.DBhelper;
import com.orangeelephant.sobriety.database.DefineTables;

public class CreateNewCounter extends AppCompatActivity {
    public CreateNewCounter() {
    }

    private String _name;
    private String _description;
    private Long _startTime;

    public void create(Context context, String name, Long startTime) {
        this._name = name;
        this._startTime = startTime;

        writeToDb(context);
    }

    private void writeToDb(Context context) {
        SQLiteDatabase db = new DBhelper(context).getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DefineTables.Counters.COLUMN_NAME, this._name);
        values.put(DefineTables.Counters.COLUMN_START_TIME, this._startTime);

        long newRowId = db.insert(DefineTables.Counters.TABLE_NAME, null, values);
        db.close();
    }


}