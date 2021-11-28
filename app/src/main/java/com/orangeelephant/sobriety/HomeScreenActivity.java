package com.orangeelephant.sobriety;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.orangeelephant.sobriety.counter.Counter;
import com.orangeelephant.sobriety.counter.CreateNewCounter;
import com.orangeelephant.sobriety.counter.LoadCounters;
import com.orangeelephant.sobriety.managecounters.DeleteCounter;
import com.orangeelephant.sobriety.managecounters.ResetCounter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HomeScreenActivity extends AppCompatActivity implements CounterAdapter.OnItemClicked {
    private Counter[] counters;
    private Counter openCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        setStrings();

        onRefreshRecycler();

        setSupportActionBar(findViewById(R.id.homeScreenToolbar));

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);

        setHomePageRefreshListener();
    }

    private void setStrings() {
        TextView appName = (TextView) findViewById(R.id.appNameView);
        appName.setText(getString(R.string.HomeActivity_app_name));

        TextView addCounter = (TextView) findViewById(R.id.add_counter);
        addCounter.setText(getString(R.string.HomeActivity_add_counter));
    }

    public void onClickAddCounter (View v) {
        Intent intent = new Intent(HomeScreenActivity.this, AddCounterActivity.class);
        startActivity(intent);
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

        SwipeRefreshLayout swipeRefreshLayoutFullCounter = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayoutFullCounter);
        swipeRefreshLayoutFullCounter.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayoutFullCounter.setRefreshing(false);
                refreshCurrentCounterView();
            }
        });
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

        onBackPressed();

        deleteCounter.printDeletionMessage();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void setHomePageRefreshListener() {
        SwipeRefreshLayout swipeRefreshLayoutHome = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayoutHome.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayoutHome.setRefreshing(false);
                onRefreshRecycler();
            }
        });
    }
}