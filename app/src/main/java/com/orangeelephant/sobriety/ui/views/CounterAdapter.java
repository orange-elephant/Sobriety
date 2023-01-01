package com.orangeelephant.sobriety.ui.views;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.orangeelephant.sobriety.R;
import com.orangeelephant.sobriety.database.model.Counter;
import com.orangeelephant.sobriety.database.CountersDatabase;
import com.orangeelephant.sobriety.dependencies.ApplicationDependencies;

import java.util.ArrayList;

public class CounterAdapter extends RecyclerView.Adapter<CounterViewHolder> {
    private final Listener listener;

    private ArrayList<Counter> counters;

    public CounterAdapter(Listener listener) {
        this.listener = listener;
        onDataChanged();
    }

    @Override
    @NonNull
    public CounterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View counterView = inflater.inflate(R.layout.counter_layout, parent, false);

        return new CounterViewHolder(counterView);
    }

    @Override
    public void onBindViewHolder(CounterViewHolder holder, int position) {
        holder.setCounter(counters.get(position));

        holder.itemView.setOnClickListener(l -> {
            listener.onCounterClicked(holder.getCounter());
        });
    }

    @Override
    public int getItemCount() {
        return counters.size();
    }

    public void onDataChanged() {
        CountersDatabase countersDatabase = ApplicationDependencies.getSobrietyDatabase().getCountersDatabase();
        counters = countersDatabase.getAllCountersWithoutReasons();
        notifyDataSetChanged();
    }

    public void updateDurationString() {
        notifyDataSetChanged();
    }

    public interface Listener {
        void onCounterClicked(Counter counter);
    }
}