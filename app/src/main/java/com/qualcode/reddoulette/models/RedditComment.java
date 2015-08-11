package com.qualcode.reddoulette.models;

import java.util.Date;

public class RedditComment {
    public String comment, displayDate;
    public int score;
    public Date postDate;


    public RedditComment()
    {

    }

    public RedditComment(String comment, int score, Date postDate, String displayDate)
    {
        this.comment = comment;
        this.score = score;
        this.postDate = postDate;
        this.displayDate = displayDate;
    }

    public String getComment()
    {
        return comment;
    }

    public int getScore()
    {
        return score;
    }

    public Date getPostDate()
    {
        return postDate;
    }
}
