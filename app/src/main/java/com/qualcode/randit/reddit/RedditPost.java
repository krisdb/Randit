package com.qualcode.randit.reddit;

import android.os.Parcelable;

public final class RedditPost {

	public String id, name;
	public String title, url, author, domain, subreddit, subreddit_id;
	public int num_comments, score, ups, downs;
	public boolean archived, over_18, hidden, saved, is_self, clicked, stickied;
	public Object edited;
	public Boolean likes;

	public long created, created_utc;

	public String selftext, permalink, link_flair_text, author_flair_text;
	public String thumbnail; // an image URL

}
