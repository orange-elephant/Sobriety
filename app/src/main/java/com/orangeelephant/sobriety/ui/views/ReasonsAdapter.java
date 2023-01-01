package com.orangeelephant.sobriety.ui.views;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import com.orangeelephant.sobriety.R;
import com.orangeelephant.sobriety.database.model.Reason;

public class ReasonsAdapter extends RecyclerView.Adapter<ReasonsAdapter.ViewHolder> {

    private final ArrayList<Reason> reasons;

    public ReasonsAdapter(ArrayList<Reason> reasons) {
        this.reasons = reasons;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View reasonsView = inflater.inflate(R.layout.reason_layout, parent, false);

        return new ViewHolder(reasonsView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String reason = reasons.get(position).getReason();

        TextView reasonTextView = holder.reasonView;
        reasonTextView.setText(reason);
    }

    @Override
    public int getItemCount() {
        return reasons.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView reasonView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            reasonView = (TextView) itemView.findViewById(R.id.CounterViewActivity_sobriety_reason);
        }
    }
}
