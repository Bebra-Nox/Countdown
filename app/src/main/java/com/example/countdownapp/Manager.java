package com.example.countdownapp;

import android.content.Context;
import android.content.SharedPreferences;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Manager {

    private static final String PREF_NAME = "event_prefs";
    private static final String KEY_EVENTS = "events";

    private Context context;

    public Manager(Context context) {
        this.context = context;
    }
    public void addEvent(Event event) {
        List<Event> events = getEvents();
        events.add(event);
        saveEvents(events);
    }
    public List<Event> getEvents() {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_EVENTS, null);

        if (json == null || json.isEmpty()) {
            return new ArrayList<>();
        }
        return Event.fromJsonList(json);
    }

    private void saveEvents(List<Event> events) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_EVENTS, Event.toJsonList(events));
        editor.apply();
    }
}