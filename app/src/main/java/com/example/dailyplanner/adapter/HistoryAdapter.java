package com.example.dailyplanner.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dailyplanner.R;
import com.example.dailyplanner.TodayFragment;
import com.example.dailyplanner.data.TaskStorage;
import com.example.dailyplanner.model.Task;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.DayViewHolder> {

    private List<String> dates;

    public HistoryAdapter(List<String> dates) {
        this.dates = dates;
    }

    @NonNull
    @Override
    public DayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_day, parent, false);
        return new DayViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DayViewHolder holder, int position) {
        String date = dates.get(position);

        List<Task> tasks = TaskStorage.loadTasks(holder.itemView.getContext(), date);

        int total = tasks.size();
        int done = 0;
        for (Task task : tasks) {
            if (task.isDone()) done++;
        }

        holder.textDate.setText(date);
        holder.textProgress.setText("Выполнено " + done + " / " + total);

        if (total == 0) {
            holder.dayStatus.setBackgroundColor(Color.GRAY);
        } else if (done == 0) {
            holder.dayStatus.setBackgroundColor(Color.RED);
        } else if (done == total) {
            holder.dayStatus.setBackgroundColor(Color.GREEN);
        } else {
            holder.dayStatus.setBackgroundColor(Color.YELLOW);
        }

        holder.itemView.setOnClickListener(v -> {
            if (v.getContext() instanceof AppCompatActivity) {
                AppCompatActivity activity =
                        (AppCompatActivity) v.getContext();

                activity.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container,
                                TodayFragment.newInstance(date))
                        .addToBackStack(null)
                        .commit();
            }
        });

    }

    @Override
    public int getItemCount() {
        return dates.size();
    }

    static class DayViewHolder extends RecyclerView.ViewHolder {
        View dayStatus;
        TextView textDate, textProgress;

        DayViewHolder(@NonNull View itemView) {
            super(itemView);
            dayStatus = itemView.findViewById(R.id.dayStatus);
            textDate = itemView.findViewById(R.id.textDate);
            textProgress = itemView.findViewById(R.id.textProgress);
        }
    }


}
