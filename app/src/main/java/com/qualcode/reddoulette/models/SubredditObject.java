package com.qualcode.reddoulette.models;

import java.util.List;

public class SubredditObject {

    private String name, title, subtitle;
    private int subscribers;
    private List<RedditPost> posts;

    public SubredditObject()
    {
    }

    public void setPosts(List<RedditPost> posts)
    {
        this.posts = posts;
    }

    public List<RedditPost> getPosts()
    {
        return posts;
    }

    public String getSubtitle()
    {
        return subtitle;
    }

    public int getSubscribers()
    {
        return subscribers;
    }

    public void setSubscribers(int count)
    {
        this.subscribers = count;
    }

    public void setSubtitle(String subtitle)
    {
        this.subtitle = subtitle;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

}
