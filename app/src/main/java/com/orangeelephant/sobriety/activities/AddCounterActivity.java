package com.orangeelephant.sobriety.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.orangeelephant.sobriety.R;
import com.orangeelephant.sobriety.database.model.Counter;
import com.orangeelephant.sobriety.database.model.Reason;
import com.orangeelephant.sobriety.database.CountersDatabase;
import com.orangeelephant.sobriety.dependencies.ApplicationDependencies;
import com.orangeelephant.sobriety.logging.LogEvent;

public class AddCounterActivity extends SobrietyActivity {
    private static final String TAG = (AddCounterActivity.class.getSimpleName());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_counter);

        DatePicker startDatePicker = (DatePicker)findViewById(R.id.startDatePicker); // initiate a date picker

        startDatePicker.setSpinnersShown(false);

        Date now = new Date();
        long time = now.getTime();
        startDatePicker.setMaxDate(time);
    }

    public void onClickSubmit (View v) throws ParseException {
        EditText name = (EditText) findViewById(R.id.AddCounterActivity_counter_name_input);
        String nameText = name.getText().toString();

        EditText reason = (EditText) findViewById(R.id.AddCounterActivity_add_a_reason_for_sobriety_hint);
        String reasonText = reason.getText().toString();

        DatePicker startDatePicker = (DatePicker)findViewById(R.id.startDatePicker);
        int year = startDatePicker.getYear();
        int month = startDatePicker.getMonth() + 1;
        int day = startDatePicker.getDayOfMonth();

        String dateString = year + "-" + month + "-" + day;
        SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");

        Date startDate = date.parse(dateString);
        Long time = startDate.getTime();

        //write the new counter to the db
        ArrayList<Reason> reasons = new ArrayList<>();
        reasons.add(new Reason(0, reasonText));
        Counter counterToAdd = new Counter(0, nameText, time, 0L, reasons);
        CountersDatabase countersDatabase = ApplicationDependencies.getSobrietyDatabase().getCountersDatabase();
        countersDatabase.saveCounterObjectToDb(counterToAdd);
        LogEvent.i(TAG, "New counter was created");

        onBackPressed();
    }

    public void onClickCancel (View v) {
        onBackPressed();
    }

}