package com.example.countdownapp;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.util.Log;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.time.ZoneId;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Manager manager;
    private ArrayAdapter<String> adapter;
    private List<String> displayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .penaltyLog()
                .build());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        manager = new Manager(this);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, displayList);
        ListView listView = findViewById(R.id.listView);
        listView.setAdapter(adapter);

        EditText eventNameInput = findViewById(R.id.eventName);
        EditText eventDateInput = findViewById(R.id.eventDate);
        TextView futureSwitch = findViewById(R.id.futureSwitch);
        Button addButton = findViewById(R.id.addButton);

        refreshDisplay();

        addButton.setOnClickListener(v -> {
            String name = eventNameInput.getText().toString();
            String dateString = eventDateInput.getText().toString();
            boolean isFuture = "Да".contentEquals(futureSwitch.getText());

            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate date = LocalDate.parse(dateString, formatter);
                Event event = new Event(name, date, isFuture);
                manager.addEvent(event);
                scheduleEventReminder(event);
                showTestNotification(event.getName());
                refreshDisplay();
                eventNameInput.setText("");
                eventDateInput.setText("");
            } catch (Exception e) {
            }
        });
    }
    @SuppressLint("ScheduleExactAlarm")
    private void scheduleEventReminder(Event event) {
        if (event.getDate() == null) {
            Toast.makeText(this, "Выберите дату события", Toast.LENGTH_SHORT).show();
            return;
        }


        long triggerTime = 0;
        Log.d("CountdownApp", "Будильник установлен на: " + new Date(triggerTime));
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this, EventReminderReceiver.class);
        intent.putExtra("event_name", event.getName());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                event.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        LocalDate eventDate = event.getDate();

        LocalDate reminderDate = eventDate.minusDays(1);
        triggerTime = reminderDate.atStartOfDay()
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();
        Log.d("CountdownApp", "Будильник установлен на: " + new Date(triggerTime));

        if (alarmManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerTime,
                        pendingIntent
                );
            } else {
                alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        triggerTime,
                        pendingIntent
                );
            }

            String message = "Напоминание установлено за день до события: " + event.getName();
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }
    public void showDatePicker(View view) {
        EditText dateInput = findViewById(R.id.eventDate);
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view1, selectedYear, selectedMonth, selectedDayOfMonth) -> {
                    String selectedDate = selectedYear + "-" +
                            String.format("%02d", selectedMonth + 1) + "-" +
                            String.format("%02d", selectedDayOfMonth);
                    dateInput.setText(selectedDate);
                },
                year, month, day
        );

        datePickerDialog.show();
    }
    private void showTestNotification(String eventName) {
        NotificationManager notificationManager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "event_test_channel",
                    "Добавление события",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            notificationManager.createNotificationChannel(channel);
        }
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "event_test_channel")
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Вы добавили событие")
                .setContentText(eventName + " — событие добавлено!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        notificationManager.notify(12345, builder.build());
    }
    private void refreshDisplay() {
        displayList.clear();
        for (Event event : manager.getEvents()) {
            LocalDate today = LocalDate.now();
            long days = ChronoUnit.DAYS.between(today, event.getDate());

            if (days >= 0) {
                displayList.add(event.getName() + " — Осталось " + days + " дней");
            } else {
                days = ChronoUnit.DAYS.between(event.getDate(), today);
                displayList.add(event.getName() + " — Прошло " + days + " дней");
            }
        }

        adapter.notifyDataSetChanged();
    }
}