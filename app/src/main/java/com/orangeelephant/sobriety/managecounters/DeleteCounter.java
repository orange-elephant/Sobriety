package com.orangeelephant.sobriety.managecounters;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.orangeelephant.sobriety.database.DBhelper;
import com.orangeelephant.sobriety.database.DefineTables;

public class DeleteCounter {
    private final int counterID;
    private final Context context;
    private final String name;

    public DeleteCounter(Context context, int counterID, String name) {
        this.context = context;
        this.counterID = counterID;
        this.name = name;

        deleteRecord();
    }

    private void deleteRecord() {
        String sql = "DELETE FROM " + DefineTables.Counters.TABLE_NAME +
                " WHERE _id = " + this.counterID;

        SQLiteDatabase db = new DBhelper(this.context).getReadableDatabase();
        db.execSQL(sql);
    }

    public void printDeletionMessage() {
        CharSequence message = this.name + " was deleted";
        Toast deletionMessage = Toast.makeText(this.context, message, Toast.LENGTH_LONG);
        deletionMessage.show();
    }
}
