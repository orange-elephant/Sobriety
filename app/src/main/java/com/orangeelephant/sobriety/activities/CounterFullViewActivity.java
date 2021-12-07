package com.orangeelephant.sobriety.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.orangeelephant.sobriety.R;
import com.orangeelephant.sobriety.counter.Counter;
import com.orangeelephant.sobriety.managecounters.ResetCounter;
import com.orangeelephant.sobriety.managecounters.DeleteCounter;

import java.util.Dictionary;

public class CounterFullViewActivity extends AppCompatActivity {
    private Counter openCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.openCounter = (Counter) getIntent().getSerializableExtra("openCounter");

        setContentView(R.layout.activity_counter_full_view);
        setStrings();

        refreshCurrentCounterView();
        setTimeMessageUpdateHandler();

        SwipeRefreshLayout swipeRefreshLayoutFullCounter = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayoutFullCounter);
        swipeRefreshLayoutFullCounter.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayoutFullCounter.setRefreshing(false);
                refreshCurrentCounterView();
            }
        });
    }

    private void setStrings() {
        TextView reset = (TextView) findViewById(R.id.CounterViewActivity_reset_counter);
        reset.setText(R.string.CounterViewActivity_reset_counter);

        TextView delete = (TextView) findViewById(R.id.CounterViewActivity_delete_counter);
        delete.setText(R.string.CounterViewActivity_delete_counter);

        Button edit = findViewById(R.id.edit_counter_button);
        edit.setText(getString(R.string.CounterViewActivity_edit_counter));
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
        int openCounterId = this.openCounter.get_id();
        Dictionary reason = this.openCounter.getReasons_dict();
        ResetCounter resetCounter = new ResetCounter(this, openCounterId, reason);
        this.openCounter = resetCounter.returnResetCounter();

        refreshCurrentCounterView();
    }

    public void onClickDelete (View v) {
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