package com.orangeelephant.sobriety.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.orangeelephant.sobriety.R;
import com.orangeelephant.sobriety.counter.Counter;
import com.orangeelephant.sobriety.managecounters.ResetCounter;
import com.orangeelephant.sobriety.managecounters.DeleteCounter;

import java.util.Dictionary;

public class CounterFullViewActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener;

    private Counter openCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        this.openCounter = (Counter) getIntent().getSerializableExtra("openCounter");

        setContentView(R.layout.activity_counter_full_view);

        refreshCurrentCounterView();
        setTimeMessageUpdateHandler();
        preferenceChangeListener =
                new SharedPreferences.OnSharedPreferenceChangeListener() {
                    @Override
                    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                        recreate();
                    }
                };
        sharedPreferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener);
    }

    public void refreshCurrentCounterView () {
        TextView name = (TextView) findViewById(R.id.CounterViewActivity_counter_name);
        name.setText(openCounter.getName());

        TextView timeSober = (TextView) findViewById((R.id.CounterViewActivity_counter_message_long));
        String currentTimeText = String.format(getString(R.string.CounterViewActivity_current_sobriety_message),
                openCounter.getTimeSoberMessage(openCounter.getCurrentTimeSoberInMillis()));
        timeSober.setText(currentTimeText);

        TextView recordTimeSober = (TextView) findViewById(R.id.CounterViewActivity_record_sobriety_message);
        String recordTimeText = String.format(getString(R.string.CounterViewActivity_record_sobriety_message),
                openCounter.getTimeSoberMessage(openCounter.getRecordTimeSoberInMillis()));
        recordTimeSober.setText(recordTimeText);

        TextView sobrietyReason = (TextView) findViewById(R.id.CounterViewActivity_sobriety_reason);
        String sobrietyReasonText;
        if (openCounter.getSobrietyReason() != null) {
            sobrietyReasonText = String.format(getString(R.string.CounterViewActivity_sobriety_reason),
                    openCounter.getSobrietyReason());
        } else {
            sobrietyReasonText = getString(R.string.CounterViewActivity_no_sobriety_reason_provided);
        }
        sobrietyReason.setText(sobrietyReasonText);
    }

    public void onClickResetCounter (View v) {
        new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.ConfirmationFrgament_title_reset))
                    .setMessage(getString(R.string.ConfirmationFragment_reset))
                    .setPositiveButton(getString(R.string.ConfirmationFragment_confirm), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            CounterFullViewActivity.this.resetCounter();
                            Toast counterReset = new Toast(CounterFullViewActivity.this);
                            counterReset.setText(R.string.Toast_counter_reset);
                            counterReset.show();
                        }
                    })
                    .setNegativeButton(getString(R.string.ConfirmationFragment_cancel), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Toast cancelledMessage = new Toast(CounterFullViewActivity.this);
                            cancelledMessage.setText(R.string.Toast_reset_cancelled);
                            cancelledMessage.show();
                        }
                    })
                    .show();
    }

    private void resetCounter() {
        int openCounterId = this.openCounter.get_id();
        Dictionary reason = this.openCounter.getReasons_dict();
        ResetCounter resetCounter = new ResetCounter(this, openCounterId, reason);
        this.openCounter = resetCounter.returnResetCounter();

        refreshCurrentCounterView();
    }

    public void onClickDelete (View v) {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.ConfirmationFrgament_title_delete))
                .setMessage(getString(R.string.ConfirmationFragment_delete))
                .setPositiveButton(getString(R.string.ConfirmationFragment_confirm), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        CounterFullViewActivity.this.deleteCounter();
                    }
                })
                .setNegativeButton(getString(R.string.ConfirmationFragment_cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast cancelledMessage = new Toast(CounterFullViewActivity.this);
                        cancelledMessage.setText(R.string.Toast_delete_cancelled);
                        cancelledMessage.show();
                    }
                })
                .show();
    }

    private void deleteCounter() {
        int openCounterId = this.openCounter.get_id();
        String counterName = this.openCounter.getName();
        DeleteCounter deleteCounter = new DeleteCounter(this, openCounterId, counterName);

        String deletionToast = this.getApplicationContext().getResources().getString(R.string.Toast_counter_deleted);
        deleteCounter.printDeletionMessage(deletionToast);

        onBackPressed();
    }

    public void onClickEditCounter (View v) {
        Intent intent = new Intent(CounterFullViewActivity.this, EditCounterActivity.class);
        intent.putExtra("openCounter", openCounter);

        startActivity(intent);
    }

    // https://stackoverflow.com/questions/11434056/how-to-run-a-method-every-x-seconds
    private void setTimeMessageUpdateHandler() {
        final Handler handler = new Handler();
        final int delay = 1000; // 1000 milliseconds == 1 second

        handler.postDelayed(new Runnable() {
            public void run() {
                refreshCurrentCounterView();
                handler.postDelayed(this, delay);
            }
        }, delay);
    }
}