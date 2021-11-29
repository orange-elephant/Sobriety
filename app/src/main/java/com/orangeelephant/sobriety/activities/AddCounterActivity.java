package com.orangeelephant.sobriety.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.orangeelephant.sobriety.R;
import com.orangeelephant.sobriety.counter.CreateNewCounter;

public class AddCounterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_counter);
        setStrings();

        DatePicker startDatePicker = (DatePicker)findViewById(R.id.startDatePicker); // initiate a date picker

        startDatePicker.setSpinnersShown(false);

        Date now = new Date();
        long time = now.getTime();
        startDatePicker.setMaxDate(time);
    }

    private void setStrings() {
        TextView enterNamePointer = (TextView) findViewById(R.id.AddCounterActivity_enter_counter_name);
        enterNamePointer.setText(R.string.AddCounterActivity_enter_counter_name);

        TextView enterNameBox = (TextView) findViewById(R.id.AddCounterActivity_counter_name_input);
        enterNameBox.setHint(R.string.AddCounterActivity_counter_name_input);

        TextView selectStartDatePointer = (TextView) findViewById(R.id.AddCounterActivity_select_start_date);
        selectStartDatePointer.setText(R.string.AddCounterActivity_select_start_date);

        TextView enterReasonPointer = (TextView) findViewById(R.id.AddCounterActivity_add_a_reason_for_sobriety);
        enterReasonPointer.setText(getString(R.string.AddCounterActivity_add_a_reason_for_sobriety));

        TextView enterReasonHint = (TextView) findViewById(R.id.AddCounterActivity_add_a_reason_for_sobriety_hint);
        enterReasonHint.setHint(getString(R.string.AddCounterActivity_add_a_reason_for_sobriety_hint));

        TextView cancel = (TextView) findViewById(R.id.AddCounterActivity_cancel);
        cancel.setText(R.string.AddCounterActivity_cancel);

        TextView submit = (TextView) findViewById(R.id.AddCounterActivity_submit);
        submit.setText(R.string.AddCounterActivity_submit);
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

        CreateNewCounter newCounter = new CreateNewCounter();
        newCounter.create(this, nameText, reasonText, time);

        onBackPressed();
    }

    public void onClickCancel (View v) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}