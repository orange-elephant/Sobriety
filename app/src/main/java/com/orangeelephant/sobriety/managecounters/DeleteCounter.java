package com.orangeelephant.sobriety.managecounters;

import android.content.Context;
import android.content.res.Resources;
import android.widget.Toast;

import com.orangeelephant.sobriety.R;
import com.orangeelephant.sobriety.database.DBhelper;
import com.orangeelephant.sobriety.database.DefineTables;

import net.sqlcipher.database.SQLiteDatabase;

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
        String sqlCounterRecord = "DELETE FROM " + DefineTables.Counters.TABLE_NAME_COUNTERS +
                " WHERE _id = " + this.counterID;

        SQLiteDatabase db = new DBhelper(this.context).getWritableDatabase();
        db.execSQL(sqlCounterRecord);

        String sqlReasonRecords = "DELETE FROM " + DefineTables.Counters.TABLE_NAME_REASONS +
                " WHERE counter_id = " + this.counterID;
        db.execSQL(sqlReasonRecords);
        db.close();
    }

    public void printDeletionMessage(String toastString) {
        CharSequence message = String.format(toastString, this.name);
        Toast deletionMessage = Toast.makeText(this.context, message, Toast.LENGTH_LONG);
        deletionMessage.show();
    }
}
