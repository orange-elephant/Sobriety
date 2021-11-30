package com.orangeelephant.sobriety.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.orangeelephant.sobriety.R;
import com.orangeelephant.sobriety.counter.Counter;
import com.orangeelephant.sobriety.counter.CreateNewCounter;
import com.orangeelephant.sobriety.counter.LoadCounters;
import com.orangeelephant.sobriety.database.SqlCipherMigration;
import com.orangeelephant.sobriety.managecounters.DeleteCounter;
import com.orangeelephant.sobriety.managecounters.ResetCounter;
import com.orangeelephant.sobriety.adapters.CounterAdapter;

import net.sqlcipher.database.SQLiteDatabase;

public class HomeScreenActivity extends AppCompatActivity implements CounterAdapter.OnItemClicked {
    private Counter[] counters;
    private Counter openCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        setStrings();

        //load libraries necessary for sqlcipher library to function
        SQLiteDatabase.loadLibs(this);
        SqlCipherMigration sqlCipherMigration = new SqlCipherMigration(this);
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

    @Override
    public void onItemClick(int position) {
        openCounter = this.counters[position];

        Intent intent = new Intent(HomeScreenActivity.this, CounterFullViewActivity.class);
        intent.putExtra("openCounter", openCounter);

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