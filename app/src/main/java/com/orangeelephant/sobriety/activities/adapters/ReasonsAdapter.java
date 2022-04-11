package com.orangeelephant.sobriety.activities.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Dictionary;

import com.orangeelephant.sobriety.R;

public class ReasonsAdapter extends
        RecyclerView.Adapter<ReasonsAdapter.ViewHolder> {

    private Dictionary reasonsDict;

    public ReasonsAdapter(Dictionary reasonsDict) {
        this.reasonsDict = reasonsDict;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View reasonsView = inflater.inflate(R.layout.reason_layout, parent, false);

        return new ViewHolder(reasonsView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String reason = reasonsDict.elements().nextElement().toString();

        TextView reasonTextView = holder.reasonView;
        reasonTextView.setText(reason);
    }

    @Override
    public int getItemCount() {
        return reasonsDict.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView reasonView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            reasonView = (TextView) itemView.findViewById(R.id.CounterViewActivity_sobriety_reason);
        }
    }
}
