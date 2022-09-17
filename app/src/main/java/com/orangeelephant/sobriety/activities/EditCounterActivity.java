package com.orangeelephant.sobriety.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.orangeelephant.sobriety.R;
import com.orangeelephant.sobriety.database.model.Counter;
import com.orangeelephant.sobriety.database.model.Reason;
import com.orangeelephant.sobriety.database.ReasonsDatabase;
import com.orangeelephant.sobriety.dependencies.ApplicationDependencies;

import java.util.ArrayList;

public class EditCounterActivity extends SobrietyActivity {

    private Counter openCounter;
    private ArrayList<Reason> reasons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_counter);

        int openCounterId = getIntent().getIntExtra("openCounterId", 0);
        this.openCounter = ApplicationDependencies.getSobrietyDatabase().getCountersDatabase()
                                .getCounterById(openCounterId);
        this.reasons = openCounter.getReasons();
    }

    public void onClickSave(View v) {
        EditText reason_field = findViewById(R.id.addReasonField);
        String reason = reason_field.getText().toString();

        ReasonsDatabase db = ApplicationDependencies.getSobrietyDatabase().getReasonsDatabase();
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
