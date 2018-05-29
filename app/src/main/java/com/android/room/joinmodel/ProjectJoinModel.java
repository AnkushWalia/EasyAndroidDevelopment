package com.android.room.joinmodel;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Relation;

import com.android.service.model.Project;
import com.android.service.model.ProjectId;

import java.util.List;

public class ProjectJoinModel {

    @Embedded
    public Project project;

    @Relation(parentColumn = "id", entityColumn = "id", entity = ProjectId.class)
    public List<ProjectId> projectIds;
}
