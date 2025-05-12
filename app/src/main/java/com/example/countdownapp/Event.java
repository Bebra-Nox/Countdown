package com.example.countdownapp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Event {

    private String name;
    private LocalDate date;
    private boolean isPositive;

    public Event(String name, LocalDate date, boolean isPositive) {
        this.name = name;
        this.date = date;
        this.isPositive = isPositive;
    }

    public String getName() {
        return name;
    }

    public LocalDate getDate() {
        return date;
    }

    public boolean isPositive() {
        return isPositive;
    }

    // Сериализация одного события в JSONObject
    public JSONObject toJson() throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("name", name);
        obj.put("date", date.toString());
        obj.put("isPositive", isPositive);
        return obj;
    }

    // Десериализация из JSONObject
    public static Event fromJson(JSONObject json) throws JSONException {
        String name = json.getString("name");
        LocalDate date = LocalDate.parse(json.getString("date"));
        boolean isPositive = json.getBoolean("isPositive");
        return new Event(name, date, isPositive);
    }

    // Перевод списка событий в JSONArray
    public static JSONArray toJsonArray(List<Event> events) throws JSONException {
        JSONArray array = new JSONArray();
        for (Event event : events) {
            array.put(event.toJson());
        }
        return array;
    }

    // Перевод JSONArray обратно в список событий
    public static List<Event> fromJsonArray(JSONArray array) throws JSONException {
        List<Event> list = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            list.add(fromJson(array.getJSONObject(i)));
        }
        return list;
    }

    // Возвращает JSON-строку из списка событий
    public static String toJsonList(List<Event> events) {
        try {
            return toJsonArray(events).toString();
        } catch (JSONException e) {
            return "";
        }
    }

    // Возвращает список событий из JSON-строки
    public static List<Event> fromJsonList(String json) {
        try {
            return fromJsonArray(new JSONArray(json));
        } catch (JSONException e) {
            return new ArrayList<>();
        }
    }
}