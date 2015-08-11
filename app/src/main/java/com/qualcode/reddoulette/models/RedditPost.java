package com.qualcode.reddoulette.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public final class RedditPost {

	public String url, permalink, title, author, domain, text, displayDate;
	public int score;
	public Date postDate;
	public List<RedditComment> comments;
	public Boolean isSelf;

	public RedditPost()
	{
	}

	public RedditPost(String title, String author, int score, String domain, Date postDate, String displayDate)
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

	public void setPermaLink(String permalink)
	{
		this.permalink = permalink;
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

	public void setUrl(String url)
	{
		this.url = url;
	}

	public List<RedditComment> getComments()
	{
		return comments;
	}

	public Boolean IsSelf()
	{
		return isSelf;
	}

	public String getPermaLink()
	{
		return permalink;
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
