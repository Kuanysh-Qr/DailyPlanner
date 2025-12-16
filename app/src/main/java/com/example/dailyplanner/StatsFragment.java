package com.example.dailyplanner;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.dailyplanner.data.TaskStorage;
import com.example.dailyplanner.model.Task;

import java.util.List;

public class StatsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_stats, container, false);

        TextView textTotalDays = view.findViewById(R.id.textTotalDays);
        TextView textGreen = view.findViewById(R.id.textGreen);
        TextView textYellow = view.findViewById(R.id.textYellow);
        TextView textRed = view.findViewById(R.id.textRed);
        TextView textPercent = view.findViewById(R.id.textPercent);
        ProgressBar progressBar = view.findViewById(R.id.progressTasks);

        List<String> dates = TaskStorage.getAllDates(requireContext());

        int greenDays = 0;
        int yellowDays = 0;
        int redDays = 0;

        int totalTasks = 0;
        int doneTasks = 0;

        for (String date : dates) {
            List<Task> tasks = TaskStorage.loadTasks(requireContext(), date);

            int total = tasks.size();
            int done = 0;

            for (Task task : tasks) {
                if (task.isDone()) done++;
            }

            totalTasks += total;
            doneTasks += done;

            if (total == 0) {
                // не считаем
            } else if (done == 0) {
                redDays++;
            } else if (done == total) {
                greenDays++;
            } else {
                yellowDays++;
            }
        }

        int totalDays = greenDays + yellowDays + redDays;

        textTotalDays.setText("Всего дней: " + totalDays);
        textGreen.setText("🟢 Продуктивных: " + greenDays);
        textYellow.setText("🟡 Частично: " + yellowDays);
        textRed.setText("🔴 Провальных: " + redDays);

        int percent = totalTasks == 0 ? 0 : (doneTasks * 100 / totalTasks);

        textPercent.setText("Выполнение: " + percent + "%");
        progressBar.setProgress(percent);

        return view;
    }
}
