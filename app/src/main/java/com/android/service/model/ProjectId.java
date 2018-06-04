package com.android.service.model;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

import static android.arch.persistence.room.ForeignKey.CASCADE;

//@Entity(tableName = "ProjectId")
@Entity(foreignKeys = @ForeignKey(entity = Project.class,
        parentColumns = "id",
        childColumns = "id",
        onDelete = CASCADE))
public class ProjectId {
    @PrimaryKey(autoGenerate = true)
    public long id;
    //    @ColumnInfo(name = "project_id")
    public String name;
    @Embedded(prefix = "user")
    public User owner;
    public String full_name;
    public String html_url;
    public String description;
    public String url;
    public String created_at;
    public String updated_at;
    public String pushed_at;
    public String git_url;
    public String ssh_url;
    public String clone_url;
    public String svn_url;
    public String homepage;
    public int stargazers_count;
    public int watchers_count;
    public String language;
    public boolean has_issues;
    public boolean has_downloads;
    public boolean has_wiki;
    public boolean has_pages;
    public int forks_count;
    public int open_issues_count;
    public int forks;
    public int open_issues;
    public int watchers;
    public String default_branch;
}

