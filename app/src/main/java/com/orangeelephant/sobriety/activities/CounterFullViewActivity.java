package com.orangeelephant.sobriety.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.orangeelephant.sobriety.R;
import com.orangeelephant.sobriety.counter.Counter;
import com.orangeelephant.sobriety.managecounters.ResetCounter;
import com.orangeelephant.sobriety.managecounters.DeleteCounter;

public class CounterFullViewActivity extends AppCompatActivity {
    private Counter openCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.openCounter = (Counter) getIntent().getSerializableExtra("openCounter");

        setContentView(R.layout.activity_counter_full_view);
        setStrings();

        refreshCurrentCounterView();

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
    }

    public void refreshCurrentCounterView () {
        TextView name = (TextView) findViewById(R.id.CounterViewActivity_counter_name);
        name.setText(openCounter.getName());

        TextView timeSober = (TextView) findViewById((R.id.CounterViewActivity_counter_message_long));
        timeSober.setText(openCounter.getTimeSoberMessage(openCounter.getCurrentTimeSoberInMillis()));

        TextView recordTimeSober = (TextView) findViewById(R.id.CounterViewActivity_record_sobriety_message);
        String recordTimeText = "Your record sobriety duration is " +
                openCounter.getTimeSoberMessage(openCounter.getRecordTimeSoberInMillis());
        recordTimeSober.setText(recordTimeText);
    }

    public void onClickResetCounter (View v) {
        int openCounterId = this.openCounter.get_id();
        ResetCounter resetCounter = new ResetCounter(this, openCounterId);
        this.openCounter = resetCounter.returnResetCounter();

        refreshCurrentCounterView();
    }

    public void onClickDelete (View v) {
        int openCounterId = this.openCounter.get_id();
        String counterName = this.openCounter.getName();
        DeleteCounter deleteCounter = new DeleteCounter(this, openCounterId, counterName);

        onBackPressed();

        deleteCounter.printDeletionMessage();
    }
}