package com.example.dailyplanner.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.dailyplanner.model.Task;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class TaskStorage {

    private static final String PREFS_NAME = "daily_planner_prefs";
    private static final String KEY_PREFIX = "tasks_";

    private static final Gson gson = new Gson();


    public static void saveTasks(Context context, String date, List<Task> tasks) {
        SharedPreferences prefs =
                context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = prefs.edit();
        String json = gson.toJson(tasks);
        editor.putString(KEY_PREFIX + date, json);
        editor.apply();
    }


    public static List<Task> loadTasks(Context context, String date) {
        SharedPreferences prefs =
                context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        String json = prefs.getString(KEY_PREFIX + date, null);

        if (json == null) {
            return new ArrayList<>();
        }

        Type type = new TypeToken<List<Task>>() {}.getType();
        return gson.fromJson(json, type);
    }


    public static List<String> getAllDates(Context context) {
        SharedPreferences prefs =
                context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        List<String> dates = new ArrayList<>();

        for (String key : prefs.getAll().keySet()) {
            if (key.startsWith(KEY_PREFIX)) {
                dates.add(key.replace(KEY_PREFIX, ""));
            }
        }
        return dates;
    }
}
