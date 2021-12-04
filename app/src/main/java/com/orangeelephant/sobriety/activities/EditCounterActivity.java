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

public class EditCounterActivity extends AppCompatActivity {
    private EditCounter editCounter;
    private Counter openCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_counter);

        this.openCounter = (Counter) getIntent().getSerializableExtra("openCounter");
        this.editCounter = new EditCounter(this, openCounter.get_id());

        setStrings();
    }

    private void setStrings() {
        EditText reason_field = findViewById(R.id.addReasonField);
        reason_field.setText(openCounter.getSobrietyReason());
        reason_field.setHint(getString(R.string.EditCounterActivity_add_sobriety_reason_hint));

        Button save_change = findViewById(R.id.save_changes);
        save_change.setText(getString(R.string.EditCounterActivity_save_changes_button));
    }

    public void onClickSave(View v) {
        EditText reason_field = findViewById(R.id.addReasonField);
        String reason = reason_field.getText().toString();
        editCounter.addReason(reason);
        editCounter.printEditSuccessfulMessage(getString(R.string.Toast_counter_edited_successfully));
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
