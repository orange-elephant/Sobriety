package com.orangeelephant.sobriety.ui.views;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.orangeelephant.sobriety.R;
import com.orangeelephant.sobriety.database.model.Counter;

public class CounterViewHolder extends RecyclerView.ViewHolder {
    private Counter counter;

    private final TextView nameView;
    private final TextView timeView;

    public CounterViewHolder(View itemView) {
        super(itemView);

        nameView = (TextView) itemView.findViewById(R.id.NameView);
        timeView = (TextView) itemView.findViewById(R.id.TimeView);
    }

    public void setCounter(Counter counter) {
        this.counter = counter;
        update();
    }

    public Counter getCounter() {
        return counter;
    }

    public void update() {
        nameView.setText(counter.getName());
        timeView.setText(Counter.getTimeSoberMessage(counter.getCurrentTimeSoberInMillis()));
    }
}
