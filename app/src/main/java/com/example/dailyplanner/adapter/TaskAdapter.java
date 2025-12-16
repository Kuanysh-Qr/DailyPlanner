package com.example.dailyplanner.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dailyplanner.R;
import com.example.dailyplanner.model.Task;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<Task> tasks;
    private OnTaskChangedListener listener;
    private boolean readOnly;

    public interface OnTaskChangedListener {
        void onTaskChanged();
    }


    public TaskAdapter(List<Task> tasks,
                       OnTaskChangedListener listener,
                       boolean readOnly) {
        this.tasks = tasks;
        this.listener = listener;
        this.readOnly = readOnly;
    }


    public TaskAdapter(List<Task> tasks,
                       OnTaskChangedListener listener) {
        this(tasks, listener, false);
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                             int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder,
                                 int position) {

        Task task = tasks.get(position);

        holder.textTask.setText(task.getTitle());


        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(task.isDone());


        holder.checkBox.setEnabled(!readOnly);

        if (!readOnly) {
            holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                task.setDone(isChecked);
                if (listener != null) listener.onTaskChanged();
            });
        }
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }


    public void removeTask(int position) {
        tasks.remove(position);
        notifyItemRemoved(position);
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {

        TextView textTask;
        CheckBox checkBox;

        TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            textTask = itemView.findViewById(R.id.textTask);
            checkBox = itemView.findViewById(R.id.checkTask);
        }
    }
}
