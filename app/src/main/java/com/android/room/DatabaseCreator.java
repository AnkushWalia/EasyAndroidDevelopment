package com.android.room;


import android.arch.persistence.room.Room;
import android.content.Context;

public class DatabaseCreator {

    private static final Object LOCK = new Object();
    private static PersonDatabase personDatabase;

    public synchronized static PersonDatabase getPersonDatabase(Context context) {
        if (personDatabase == null) {
            synchronized (LOCK) {
                if (personDatabase == null) {
                    personDatabase = Room.databaseBuilder(context,
                            PersonDatabase.class, "person db").build();
                }
            }
        }
        return personDatabase;
    }
}
