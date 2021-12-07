package com.orangeelephant.sobriety.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.orangeelephant.sobriety.R;
import com.orangeelephant.sobriety.counter.Counter;
import com.orangeelephant.sobriety.counter.LoadCounters;
import com.orangeelephant.sobriety.adapters.CounterAdapter;

import net.sqlcipher.database.SQLiteDatabase;

public class HomeScreenActivity extends AppCompatActivity implements CounterAdapter.OnItemClicked {

    private CounterAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        setStrings();
        onCreateRecycler();

        setSupportActionBar(findViewById(R.id.homeScreenToolbar));
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);

        setHomePageRefreshListener();
        setTimeMessageUpdateHandler();
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

    @Override
    public void onItemClick(int position) {
        Counter openCounter = this.adapter.getmCounter()[position];

        Intent intent = new Intent(HomeScreenActivity.this, CounterFullViewActivity.class);
        intent.putExtra("openCounter", openCounter);

        startActivity(intent);
    }

    public void onCreateRecycler() {
        RecyclerView countersView = (RecyclerView) findViewById(R.id.counterView);

        this.adapter = new CounterAdapter(this);
        countersView.setAdapter(adapter);
        countersView.setLayoutManager(new LinearLayoutManager(this));

        adapter.setOnClick(this);
    }

    public void onUpdateRecycler() {
        this.adapter.onDataChanged();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    private void setHomePageRefreshListener() {
        SwipeRefreshLayout swipeRefreshLayoutHome = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayoutHome.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayoutHome.setRefreshing(false);
                onUpdateRecycler();
            }
        });
    }

    // https://stackoverflow.com/questions/11434056/how-to-run-a-method-every-x-seconds
    private void setTimeMessageUpdateHandler() {
        final Handler handler = new Handler();
        final int delay = 1000; // 1000 milliseconds == 1 second

        handler.postDelayed(new Runnable() {
            public void run() {
                onUpdateRecycler();
                handler.postDelayed(this, delay);
            }
        }, delay);
    }
}