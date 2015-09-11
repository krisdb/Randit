package com.qualcode.reddoulette.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.qualcode.reddoulette.R;
import com.qualcode.reddoulette.activities.Details;
import com.qualcode.reddoulette.models.RedditPost;
import com.qualcode.reddoulette.models.SubredditObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PostListRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<SubredditObject> mSubreddit;
    private GoogleApiClient mGoogleApiClient;
    private Context mContext;


    public PostListRecyclerViewAdapter(List<SubredditObject> subreddit, GoogleApiClient api, Context ctx) {
        mSubreddit = subreddit;
        mGoogleApiClient = api;
        mContext = ctx;
    }

    private class VIEW_TYPES {
        public static final int Header = 0;
        public static final int Normal = 1;
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0) {
            return VIEW_TYPES.Header;
        }
        else {
            return VIEW_TYPES.Normal;
        }
    }
    @Override
    public int getItemCount() {
        return mSubreddit != null ? mSubreddit.get(1).getPosts().size() + 1 : 0;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, final int i) {

        switch (i)
        {
            case VIEW_TYPES.Header:
                return new PostListHeaderViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.postlist_header, viewGroup, false));
            case VIEW_TYPES.Normal:
                return new PostListViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.postlist_item, viewGroup, false));
            default:
                return new PostListViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.postlist_item, viewGroup, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int i) {

        if (holder instanceof PostListHeaderViewHolder) {
            ((PostListHeaderViewHolder)holder).title.setText((mSubreddit.get(0).getTitle()));
            ((PostListHeaderViewHolder)holder).subtitle.setText((mSubreddit.get(0).getSubtitle()));
            ((PostListHeaderViewHolder)holder).subscribers.setText((NumberFormat.getNumberInstance(Locale.US).format(mSubreddit.get(0).getSubscribers())));
        }
        else {
            final RedditPost post = mSubreddit.get(1).getPosts().get(i - 1);

            final PostListViewHolder postHolder = (PostListViewHolder)holder;

            postHolder.title.setText(post.getTitle());
            postHolder.score.setText(String.valueOf(post.getScore()));
            postHolder.displayDate.setText(post.getDisplayDate());
            postHolder.author.setText(post.getAuthor());
            postHolder.totalComments.setText(String.valueOf(post.getCommentTotal()));
            postHolder.domain.setText(post.getDomain());

            postHolder.commentsLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SelfIntent(v, i);
                    RecordAchievement(post);
                }
            });

            if (post.IsSticky()) {
                int darkGreen = Color.parseColor("#006400");
                postHolder.title.setTextColor(darkGreen);
                postHolder.title.setTextColor(darkGreen);
                postHolder.score.setTextColor(darkGreen);
                postHolder.displayDate.setTextColor(darkGreen);
                postHolder.author.setTextColor(darkGreen);
                postHolder.domain.setTextColor(darkGreen);
                postHolder.pts.setTextColor(darkGreen);
                postHolder.by.setTextColor(darkGreen);
            } else {
                postHolder.postLayout.setBackgroundColor(Color.parseColor("#eeeeee"));
                postHolder.title.setTextColor(Color.DKGRAY);
                postHolder.title.setTextColor(Color.DKGRAY);
                postHolder.score.setTextColor(Color.DKGRAY);
                postHolder.displayDate.setTextColor(Color.DKGRAY);
                postHolder.author.setTextColor(Color.DKGRAY);
                postHolder.domain.setTextColor(Color.DKGRAY);
                postHolder.pts.setTextColor(Color.DKGRAY);
                postHolder.by.setTextColor(Color.DKGRAY);
            }

            if (post.IsSelf()) {
                postHolder.postLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SelfIntent(v, i);
                        RecordAchievement(post);
                    }
                });
            } else {
                postHolder.postLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(post.getUrl())));
                        RecordAchievement(post );
                    }
                });
                //personViewHolder.domain.setVisibility(View.GONE);
            }
        }
    }

    private void RecordAchievement(RedditPost post)
    {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            Games.Achievements.increment(mGoogleApiClient, mContext.getString(R.string.achievement_participant_viewer), 1);
            Games.Achievements.increment(mGoogleApiClient, mContext.getString(R.string.achievement_bronze_viewer), 1);
            Games.Achievements.increment(mGoogleApiClient, mContext.getString(R.string.achievement_silver_viewer), 1);
            Games.Achievements.increment(mGoogleApiClient, mContext.getString(R.string.achievement_gold_viewer), 1);
            Games.Achievements.increment(mGoogleApiClient, mContext.getString(R.string.achievement_well_informed_viewer), 1);

            if (post.isSticky)
                Games.Achievements.increment(mGoogleApiClient, mContext.getString(R.string.achievement_sticky_viewer), 1);

            final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);

            final int totalViews = prefs.getInt("achievement_post_views", 0) + 1;

            final SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("achievement_post_views", totalViews);
            editor.commit();

            Games.Leaderboards.submitScore(mGoogleApiClient, mContext.getString(R.string.leaderboard_most_posts_viewed), totalViews);
        }
    }

    private void SelfIntent(final View v, final int i)
    {
        final Intent details = new Intent(v.getContext(), Details.class);

        final Bundle bun = new Bundle();
        bun.putString("permalink", mSubreddit.get(1).getPosts().get(i - 1).getPermaLink());
        bun.putBoolean("sticky", mSubreddit.get(1).getPosts().get(i - 1).IsSticky());
        bun.putString("subreddit", mSubreddit.get(0).getName());
        details.putExtras(bun);

        v.getContext().startActivity(details);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public class PostListHeaderViewHolder extends RecyclerView.ViewHolder {
        TextView title, subtitle, subscribers;

        PostListHeaderViewHolder(View itemView) {
            super(itemView);
            title = (TextView)itemView.findViewById(R.id.subreddit_title);
            subtitle = (TextView)itemView.findViewById(R.id.subreddit_subtitle);
            subscribers = (TextView)itemView.findViewById(R.id.subreddit_subcribers);
        }
    }

    public class PostListViewHolder extends RecyclerView.ViewHolder {
        TextView title, score, displayDate, author, domain, totalComments, pts, by;
        RelativeLayout commentsLayout, postLayout;

        PostListViewHolder(View itemView) {
            super(itemView);
            title = (TextView)itemView.findViewById(R.id.title);
            score = (TextView)itemView.findViewById(R.id.score);
            displayDate = (TextView)itemView.findViewById(R.id.displayDate);
            author = (TextView)itemView.findViewById(R.id.author);
            domain = (TextView)itemView.findViewById(R.id.domain);
            totalComments = (TextView)itemView.findViewById(R.id.comments_count);
            commentsLayout = (RelativeLayout)itemView.findViewById(R.id.commments_layout);
            postLayout = (RelativeLayout)itemView.findViewById(R.id.post_layout);
            pts = (TextView)itemView.findViewById(R.id.pts);
            by = (TextView)itemView.findViewById(R.id.by);
        }
    }
}


