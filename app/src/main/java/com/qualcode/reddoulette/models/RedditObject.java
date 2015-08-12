package com.qualcode.reddoulette.models;

import java.util.List;

public class RedditObject {

    private RedditPost post;
    private List<RedditComment> comments;

    public RedditObject()
    {
    }

    public void setComments(List<RedditComment> comments)
    {
        this.comments = comments;
    }

    public List<RedditComment> getComments()
    {
        return comments;
    }

    public RedditPost getPost()
    {
        return post;
    }

    public void setPost(RedditPost post)
    {
        this.post = post;
    }
}
