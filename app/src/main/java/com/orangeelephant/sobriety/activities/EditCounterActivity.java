package com.orangeelephant.sobriety.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.orangeelephant.sobriety.R;
import com.orangeelephant.sobriety.counter.Counter;
import com.orangeelephant.sobriety.managecounters.EditCounter;

import net.sqlcipher.database.SQLiteDatabase;

import java.util.Dictionary;

public class EditCounterActivity extends AppCompatActivity {
    private EditCounter editCounter;
    private Counter openCounter;
    private Dictionary reasons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_counter);

        this.openCounter = (Counter) getIntent().getSerializableExtra("openCounter");
        this.editCounter = new EditCounter(this, openCounter.get_id());
        this.reasons = openCounter.getReasons_dict();
    }

    public void onClickSave(View v) {
        EditText reason_field = findViewById(R.id.addReasonField);
        String reason = reason_field.getText().toString();

        if (this.reasons.isEmpty()) {
            editCounter.addReason(reason);
        } else {
            int reason_id = (int) this.reasons.keys().nextElement();
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
