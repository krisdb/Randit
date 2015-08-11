package com.qualcode.reddoulette.models;

import java.util.Date;

public class RedditComment {
    public String text, displayDate, author;
    public int score;
    public Date postDate;

    public RedditComment()
    {

    }

    public RedditComment(String text, String author, int score, Date postDate, String displayDate)
    {
        this.text = text;
        this.author = author;
        this.score = score;
        this.postDate = postDate;
        this.displayDate = displayDate;
    }

    public String getDisplayDate()
    {
        return displayDate;
    }

    public String getAuthor()
    {
        return author;
    }

    public String getText()
    {
        return text;
    }

    public int getScore()
    {
        return score;
    }

    public Date getPostDate() { return postDate;}
}
