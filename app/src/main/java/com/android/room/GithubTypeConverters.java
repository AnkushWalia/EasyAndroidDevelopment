package com.android.room;

import android.arch.persistence.room.TypeConverter;

import com.android.service.model.Project;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class GithubTypeConverters {
    private static Gson gson = new Gson();

    @TypeConverter
    public static List<Project> stringToSomeObjectList(String data) {
        if (data == null) {
            return Collections.emptyList();
        }

        Type listType = new TypeToken<List<Project>>() {
        }.getType();

        return gson.fromJson(data, listType);
    }

    @TypeConverter
    public static String someObjectListToString(List<Project> someObjects) {
        return gson.toJson(someObjects);
    }
}
