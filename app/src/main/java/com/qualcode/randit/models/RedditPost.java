package com.qualcode.randit.models;

public final class RedditPost {

	public String title, author;
	public int score;

	public RedditPost()
	{
	}

	public RedditPost(String title, String author, int score)
	{
		this.title = title;
		this.author = author;
		this.score = score;
	}

	public String getTitle()
	{
		return title;
	}

	public String getAuthor()
	{
		return author;
	}

	public int getGetScore()
	{
		return score;
	}


}
