package com.example.countdownapp;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class EventAdapter extends ArrayAdapter<Event> {

    public EventAdapter(Context context, List<Event> events) {
        super(context, 0, events);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        }

        TextView textView = convertView.findViewById(android.R.id.text1);

        Event event = getItem(position);

        LocalDate today = LocalDate.now();
        long days = ChronoUnit.DAYS.between(today, event.getDate());

        String message;
        if (days >= 0) {
            message = event.getName() + " — Осталось " + days + " дней";
            textView.setTextColor(Color.parseColor("#4CAF50"));
        } else {
            message = event.getName() + " — Прошло " + (-days) + " дней";
            textView.setTextColor(Color.parseColor("#F44336"));
        }

        textView.setText(message);
        textView.setTextSize(16);
        textView.setPadding(24, 16, 24, 16);

        textView.setBackgroundResource(R.drawable.list_item_background);

        return convertView;
    }
}