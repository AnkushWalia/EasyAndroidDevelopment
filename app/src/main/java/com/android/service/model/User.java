package com.android.service.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "User")
public class User {
    public String login;

    @PrimaryKey(autoGenerate = true)
    public long id;
    public int userColumn;
    public String avatar_url;
    public String gravatar_id;
    public String url;
    public String html_url;
    public String followers_url;
    public String following_url;
    public String gists_url;
    public String starred_url;
    public String subscriptions_url;
    public String organizations_url;
    public String repos_url;
    public String events_url;
    public String received_events_url;
    public String type;
    public String name;
    public String blog;
    public String location;
    public String email;
    public int public_repos;
    public int public_gists;
    public int followers;
    public int following;
    public String created_at;
    public String updated_at;
}