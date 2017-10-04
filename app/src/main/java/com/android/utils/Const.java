package com.android.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class Const {
    public static final String DISPLAY_MESSAGE_ACTION = "com.android.DISPLAY_MESSAGE";
    public static final int PLAY_SERVICES_RESOLUTION_REQUEST = 1234;
    private static Gson gson = new Gson();

    public static String getStringFromArray(ArrayList<String> strings) {
        return gson.toJson(strings);
    }

    public static ArrayList<String> getArrayFromString(String time) {
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        Gson gson = new Gson();
        return gson.fromJson(time, type);
    }

}