package com.orangeelephant.sobriety;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toolbar;

import com.orangeelephant.sobriety.counter.Counter;
import com.orangeelephant.sobriety.counter.CreateNewCounter;
import com.orangeelephant.sobriety.counter.LoadCounters;
import com.orangeelephant.sobriety.managecounters.DeleteCounter;
import com.orangeelephant.sobriety.managecounters.ResetCounter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements CounterAdapter.OnItemClicked {
    private Counter[] counters;
    private Counter openCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        onRefreshRecycler();

        setSupportActionBar(findViewById(R.id.homeScreenToolbar));

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);

        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                onRefreshRecycler();
            }
        });
    }

    public void onClickAddCounter (View v) {
        setContentView(R.layout.add_counter);

        DatePicker startDatePicker = (DatePicker)findViewById(R.id.startDatePicker); // initiate a date picker

        startDatePicker.setSpinnersShown(false);

        Date now = new Date();
        long time = now.getTime();
        startDatePicker.setMaxDate(time);
    }

    public void onClickSubmit (View v) throws ParseException {
        EditText name = (EditText) findViewById(R.id.name);
        String nameText = name.getText().toString();

        DatePicker startDatePicker = (DatePicker)findViewById(R.id.startDatePicker);
        int year = startDatePicker.getYear();
        int month = startDatePicker.getMonth() + 1;
        int day = startDatePicker.getDayOfMonth();

        String dateString = year + "-" + month + "-" + day;
        SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");

        Date startDate = date.parse(dateString);
        Long time = startDate.getTime();

        CreateNewCounter newCounter = new CreateNewCounter();
        newCounter.create(this, nameText, time);

        setContentView(R.layout.activity_main);
        onRefreshRecycler();
    }

    public void onClickRefreshCurrentCounterView (View v) {
        refreshCurrentCounterView();
    }

    public void onClickCancel (View v) {
        setContentView(R.layout.activity_main);
        onRefreshRecycler();
    }

    public void onRefreshRecycler() {
        LoadCounters counters = new LoadCounters(this);
        this.counters = counters.getLoadedCounters().toArray(new Counter[0]);

        RecyclerView countersView = (RecyclerView) findViewById(R.id.counterView);

        CounterAdapter adapter = new CounterAdapter(this.counters);
        countersView.setAdapter(adapter);
        countersView.setLayoutManager(new LinearLayoutManager(this));

        adapter.setOnClick(this);
    }

    @Override
    public void onItemClick(int position) {
        setContentView(R.layout.counter_full_view);

        this.openCounter = this.counters[position];

        refreshCurrentCounterView();
    }

    public void onClickResetCounter (View v) {
        int openCounterId = this.openCounter.get_id();
        ResetCounter resetCounter = new ResetCounter(this, openCounterId);
        this.openCounter = resetCounter.returnResetCounter();

        refreshCurrentCounterView();
    }

    public void refreshCurrentCounterView () {
        TextView name = (TextView) findViewById(R.id.counterName);
        name.setText(openCounter.getName());

        TextView timeSober = (TextView) findViewById((R.id.durationView));
        timeSober.setText(openCounter.getTimeSoberMessage(openCounter.getCurrentTimeSoberInMillis()));

        TextView recordTimeSober = (TextView) findViewById(R.id.RecordSobrietyStreakView);
        String recordTimeText = "Your record sobriety duration is " +
                                openCounter.getTimeSoberMessage(openCounter.getRecordTimeSoberInMillis());
        recordTimeSober.setText(recordTimeText);
    }

    public void onClickDelete (View v) {
        int openCounterId = this.openCounter.get_id();
        String counterName = this.openCounter.getName();
        DeleteCounter deleteCounter = new DeleteCounter(this, openCounterId, counterName);

        setContentView(R.layout.activity_main);
        onRefreshRecycler();

        deleteCounter.printDeletionMessage();
    }

    @Override
    public void onBackPressed() {
        setContentView(R.layout.activity_main);
        onRefreshRecycler();
        super.onBackPressed();
    }
}