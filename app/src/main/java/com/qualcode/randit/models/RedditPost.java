package com.qualcode.randit.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public final class RedditPost {

	public String url, title, author, domain, text;
	public int score;
	public Date postDate;
	public String displayDate;
	public List<RedditComment> comments;
	public Boolean isSelf;

	public RedditPost()
	{
	}

	public RedditPost(String title, String url, String author, int score, String domain, Date postDate, String displayDate)
	{
		this.url = url;
		this.domain = domain;
		this.title = title;
		this.author = author;
		this.score = score;
		this.postDate = postDate;
		this.displayDate = displayDate;
		this.comments = comments;
	}

	public void setIsSelf(Boolean isSelf)
	{
		this.isSelf = isSelf;
	}

	public void setComments(List<RedditComment> comments)
	{
		this.comments = comments;
	}

	public void setText(String text)
	{
		this.text = text;
	}

	public List<RedditComment> getComments()
	{
		return comments;
	}

	public Boolean IsSelf()
	{
		return isSelf;
	}

	public String getText()
	{
		return text;
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
