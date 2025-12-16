package com.example.dailyplanner;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dailyplanner.adapter.TaskAdapter;
import com.example.dailyplanner.data.TaskStorage;
import com.example.dailyplanner.model.Task;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TodayFragment extends Fragment {

    private List<Task> tasks;
    private TaskAdapter adapter;
    private View dayIndicator;
    private String todayDate;

    public static TodayFragment newInstance(String date) {
        TodayFragment fragment = new TodayFragment();
        Bundle args = new Bundle();
        args.putString("date", date);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_today, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerTasks);
        EditText editTask = view.findViewById(R.id.editTask);
        Button buttonAdd = view.findViewById(R.id.buttonAdd);
        dayIndicator = view.findViewById(R.id.dayIndicator);


        if (getArguments() != null && getArguments().containsKey("date")) {
            todayDate = getArguments().getString("date");
        } else {
            todayDate = getTodayDate();
        }



        tasks = TaskStorage.loadTasks(requireContext(), todayDate);

        adapter = new TaskAdapter(tasks, () -> {
            updateDayIndicator();
            TaskStorage.saveTasks(requireContext(), todayDate, tasks);
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);


        ItemTouchHelper.SimpleCallback simpleCallback =
                new ItemTouchHelper.SimpleCallback(0,
                        ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

                    @Override
                    public boolean onMove(@NonNull RecyclerView recyclerView,
                                          @NonNull RecyclerView.ViewHolder viewHolder,
                                          @NonNull RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                        int position = viewHolder.getAdapterPosition();
                        adapter.removeTask(position);
                        TaskStorage.saveTasks(requireContext(), todayDate, tasks);
                        updateDayIndicator();
                    }

                    @Override
                    public void onChildDraw(@NonNull Canvas c,
                                            @NonNull RecyclerView recyclerView,
                                            @NonNull RecyclerView.ViewHolder viewHolder,
                                            float dX, float dY,
                                            int actionState,
                                            boolean isCurrentlyActive) {

                        View itemView = viewHolder.itemView;
                        Paint paint = new Paint();
                        paint.setColor(Color.RED);

                        if (dX > 0) {
                            c.drawRect(itemView.getLeft(), itemView.getTop(),
                                    dX, itemView.getBottom(), paint);
                        } else if (dX < 0) {
                            c.drawRect(itemView.getRight() + dX, itemView.getTop(),
                                    itemView.getRight(), itemView.getBottom(), paint);
                        }

                        Drawable icon = ContextCompat.getDrawable(requireContext(),
                                android.R.drawable.ic_menu_delete);

                        if (icon != null) {
                            int margin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
                            int top = itemView.getTop() + margin;
                            int bottom = top + icon.getIntrinsicHeight();

                            if (dX > 0) {
                                int left = itemView.getLeft() + margin;
                                int right = left + icon.getIntrinsicWidth();
                                icon.setBounds(left, top, right, bottom);
                            } else if (dX < 0) {
                                int right = itemView.getRight() - margin;
                                int left = right - icon.getIntrinsicWidth();
                                icon.setBounds(left, top, right, bottom);
                            }
                            icon.draw(c);
                        }

                        super.onChildDraw(c, recyclerView, viewHolder,
                                dX, dY, actionState, isCurrentlyActive);
                    }
                };

        new ItemTouchHelper(simpleCallback).attachToRecyclerView(recyclerView);


        buttonAdd.setOnClickListener(v -> {
            String text = editTask.getText().toString().trim();
            if (!text.isEmpty()) {
                tasks.add(new Task(text));
                adapter.notifyItemInserted(tasks.size() - 1);
                TaskStorage.saveTasks(requireContext(), todayDate, tasks);
                editTask.setText("");
                updateDayIndicator();
            }
        });

        updateDayIndicator();
        return view;
    }


    private void updateDayIndicator() {
        int total = tasks.size();
        int done = 0;

        for (Task task : tasks) {
            if (task.isDone()) done++;
        }

        if (total == 0) {
            dayIndicator.setBackgroundColor(Color.GRAY);
        } else if (done == 0) {
            dayIndicator.setBackgroundColor(Color.RED);
        } else if (done == total) {
            dayIndicator.setBackgroundColor(Color.GREEN);
        } else {
            dayIndicator.setBackgroundColor(Color.YELLOW);
        }
    }

    private String getTodayDate() {
        return new SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.getDefault()
        ).format(new Date());
    }
}
