package com.android.service.model;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "Project")//, indices = {@Index(value = "name", unique = true)})
public class Project {

    @PrimaryKey(autoGenerate = true)
    public long id;
    public String name;
    public String full_name;
    @Embedded(prefix = "user")
    public User owner;
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
