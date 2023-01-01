package com.orangeelephant.sobriety.ui;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.orangeelephant.sobriety.R;
import com.orangeelephant.sobriety.ui.views.ReasonsAdapter;
import com.orangeelephant.sobriety.database.model.Counter;
import com.orangeelephant.sobriety.dependencies.ApplicationDependencies;

import java.util.Date;

public class CounterFullViewActivity extends SobrietyActivity {

    private Counter openCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int openCounterId = getIntent().getIntExtra("openCounterId", 0);
        openCounter = ApplicationDependencies.getSobrietyDatabase()
                                                .getCountersDatabase()
                                                .getCounterById(openCounterId);

        setContentView(R.layout.activity_counter_full_view);

        refreshCurrentCounterView();
        onCreateRecycler();
        setTimeMessageUpdateHandler();
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
        int openCounterId = openCounter.getId();
        long recordTimeSober = openCounter.getRecordTimeSoberInMillis();
        ApplicationDependencies.getSobrietyDatabase().getCountersDatabase()
                                .resetCounterTimer(openCounterId, recordTimeSober);
        openCounter.setRecordTimeSoberInMillis(recordTimeSober);
        openCounter.setStartTimeInMillis(new Date().getTime());

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
        int openCounterId = this.openCounter.getId();
        String counterName = this.openCounter.getName();
        ApplicationDependencies.getSobrietyDatabase().getCountersDatabase()
                                .deleteCounterById(openCounterId);

        CharSequence message = String.format(this.getString(R.string.Toast_counter_deleted), counterName);
        Toast deletionMessage = Toast.makeText(this, message, Toast.LENGTH_LONG);
        deletionMessage.show();

        onBackPressed();
    }

    public void onClickEditCounter (View v) {
        Intent intent = new Intent(CounterFullViewActivity.this, EditCounterActivity.class);
        intent.putExtra("openCounterId", openCounter.getId());

        startActivity(intent);
    }

    public void onCreateRecycler() {
        RecyclerView reasonsView = findViewById(R.id.CounterViewActivity_reasons_recycler_view);

        ReasonsAdapter adapter = new ReasonsAdapter(openCounter.getReasons());
        reasonsView.setAdapter(adapter);
        reasonsView.setLayoutManager(new LinearLayoutManager(this));
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