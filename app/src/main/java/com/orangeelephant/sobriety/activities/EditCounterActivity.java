package com.orangeelephant.sobriety.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.orangeelephant.sobriety.R;
import com.orangeelephant.sobriety.counter.Counter;
import com.orangeelephant.sobriety.counter.Reason;
import com.orangeelephant.sobriety.managecounters.EditCounter;

import java.util.ArrayList;

public class EditCounterActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener;

    private EditCounter editCounter;
    private Counter openCounter;
    private ArrayList<Reason> reasons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        setContentView(R.layout.activity_edit_counter);

        this.openCounter = (Counter) getIntent().getSerializableExtra("openCounter");
        this.editCounter = new EditCounter(this, openCounter.get_id());
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

        if (this.reasons.isEmpty()) {
            editCounter.addReason(reason);
        } else {
            int reason_id = this.reasons.get(0).getReasonId();
            editCounter.changeReason(reason, reason_id);
        }
        editCounter.printEditSuccessfulMessage(getString(R.string.Toast_counter_edited_successfully));
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
