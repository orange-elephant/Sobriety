package com.orangeelephant.sobriety.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.orangeelephant.sobriety.R;
import com.orangeelephant.sobriety.counter.Counter;
import com.orangeelephant.sobriety.counter.Reason;
import com.orangeelephant.sobriety.database.CountersDatabase;
import com.orangeelephant.sobriety.database.ReasonsDatabase;
import com.orangeelephant.sobriety.database.helpers.DBOpenHelper;

import java.util.ArrayList;

public class EditCounterActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener;

    private Counter openCounter;
    private ArrayList<Reason> reasons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        setContentView(R.layout.activity_edit_counter);

        int openCounterId = getIntent().getIntExtra("openCounterId", 0);
        this.openCounter = new CountersDatabase(new DBOpenHelper(this)).getCounterById(openCounterId);
        this.reasons = openCounter.getReasons();
        preferenceChangeListener =
                new SharedPreferences.OnSharedPreferenceChangeListener() {
                    @Override
                    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                        recreate();
                    }
                };
        sharedPreferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener);
    }

    public void onClickSave(View v) {
        EditText reason_field = findViewById(R.id.addReasonField);
        String reason = reason_field.getText().toString();

        ReasonsDatabase db = new ReasonsDatabase(new DBOpenHelper(this));
        if (this.reasons.isEmpty()) {
            db.addReasonForCounter(openCounter.getId(), reason);
        } else {
            int reason_id = this.reasons.get(0).getReasonId();
            db.changeReason(reason_id, reason);
        }
        Toast deletionMessage = Toast.makeText(this,
                getString(R.string.Toast_counter_edited_successfully), Toast.LENGTH_LONG);
        deletionMessage.show();
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
