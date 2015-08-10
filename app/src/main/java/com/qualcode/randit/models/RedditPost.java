package com.qualcode.randit.models;

import java.util.Date;

public final class RedditPost {

	public String url, title, author, domain;
	public int score;
	public Date postDate;
	public String displayDate;

	public RedditPost()
	{
	}

	public RedditPost(String url, String title, String author, int score, Date postDate, String displayDate, String domain)
	{
		this.url = url;
		this.domain = domain;
		this.title = title;
		this.author = author;
		this.score = score;
		this.postDate = postDate;
		this.displayDate = displayDate;
	}

	public String getUrl()
	{
		return url;
	}

	public String getTitle()
	{
		return title;
	}

	public String getDomain()
	{
		return domain;
	}

	public String getAuthor()
	{
		return author;
	}

	public int getGetScore()
	{
		return score;
	}

	public Date getPostDate()
	{
		return postDate;
	}

	public String getDisplayDate()
	{
		return displayDate;
	}

}
