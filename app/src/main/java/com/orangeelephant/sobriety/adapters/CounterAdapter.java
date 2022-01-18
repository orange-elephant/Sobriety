package com.orangeelephant.sobriety.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.orangeelephant.sobriety.R;
import com.orangeelephant.sobriety.counter.Counter;
import com.orangeelephant.sobriety.counter.LoadCounters;

// Create the basic adapter extending from RecyclerView.Adapter
// Note that we specify the custom ViewHolder which gives us access to our views
public class CounterAdapter extends
        RecyclerView.Adapter<CounterAdapter.ViewHolder> {

    private OnItemClicked onClick;

    private Counter[] mCounter;
    private final Context context;

    public interface OnItemClicked {
        void onItemClick(int position);
    }

    public CounterAdapter(Context context) {
        this.context = context;
        onDataChanged();
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView nameView;
        public TextView timeView;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            nameView = (TextView) itemView.findViewById(R.id.NameView);
            timeView = (TextView) itemView.findViewById(R.id.TimeView);
        }
    }

    @Override
    @NonNull
    public CounterAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.counter_layout, parent, false);

        // Return a new holder instance
        return new ViewHolder(contactView);
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(CounterAdapter.ViewHolder holder, int position) {
        // Get the data model based on position

        Counter counter = mCounter[position];

        // Set item views based on your views and data model
        TextView textView = holder.nameView;
        textView.setText(counter.getName());

        TextView timeView = holder.timeView;
        timeView.setText(counter.getTimeSoberMessage(counter.getCurrentTimeSoberInMillis()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClick.onItemClick(holder.getAdapterPosition());
            }
        });
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mCounter.length;
    }

    public Counter[] getmCounter() {
        return this.mCounter;
    }

    public void setOnClick(OnItemClicked onClick) {
        this.onClick=onClick;
    }

    public void onDataChanged() {
        LoadCounters counters = new LoadCounters(context);
        this.mCounter = counters.getLoadedCounters().toArray(new Counter[0]);
        this.notifyDataSetChanged();
    }

    public void updateDurationString() {
        this.notifyDataSetChanged();
    }
}