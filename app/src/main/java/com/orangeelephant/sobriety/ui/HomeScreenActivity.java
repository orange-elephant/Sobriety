package com.orangeelephant.sobriety.ui;

import androidx.appcompat.app.ActionBar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.orangeelephant.sobriety.R;
import com.orangeelephant.sobriety.database.model.Counter;
import com.orangeelephant.sobriety.ui.views.CounterAdapter;

public class HomeScreenActivity extends SobrietyActivity implements CounterAdapter.Listener {
    private CounterAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home_screen);

        adapter = new CounterAdapter(this);
        RecyclerView countersView = (RecyclerView) findViewById(R.id.counterView);
        countersView.setAdapter(adapter);
        countersView.setLayoutManager(new LinearLayoutManager(this));

        setSupportActionBar(findViewById(R.id.homeScreenToolbar));
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);

        setTimeMessageUpdateHandler();
    }

    @Override
    public void onResume() {
        adapter.onDataChanged();
        super.onResume();
    }

    public void onClickAddCounter (View v) {
        Intent intent = new Intent(HomeScreenActivity.this, AddCounterActivity.class);
        startActivity(intent);
    }

    public void onClickSettings (View v) {
        Intent intent = new Intent(HomeScreenActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

    @Override
    public void onCounterClicked(Counter openCounter) {
        Intent intent = new Intent(HomeScreenActivity.this, CounterFullViewActivity.class);
        intent.putExtra("openCounterId", openCounter.getId());

        startActivity(intent);
    }

    private void updateCounterDurationMessage() {
        this.adapter.updateDurationString();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    // https://stackoverflow.com/questions/11434056/how-to-run-a-method-every-x-seconds
    private void setTimeMessageUpdateHandler() {
        final Handler handler = new Handler();
        final int delay = 1000; // 1000 milliseconds == 1 second

        handler.postDelayed(new Runnable() {
            public void run() {
                updateCounterDurationMessage();
                handler.postDelayed(this, delay);
            }
        }, delay);
    }
}