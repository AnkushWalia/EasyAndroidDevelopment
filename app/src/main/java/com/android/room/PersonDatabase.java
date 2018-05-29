package com.android.room;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.android.service.model.Project;
import com.android.service.model.ProjectId;

@Database(entities = {Project.class, ProjectId.class,}, version = 1)
public abstract class PersonDatabase extends RoomDatabase {
    public abstract PersonDAO PersonDatabase();
}
