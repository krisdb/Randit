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

import java.util.ArrayList;
import java.util.List;

public class PostListRecyclerViewAdapter extends RecyclerView.Adapter<PostListRecyclerViewAdapter.PostListViewHolder> {

    private List<RedditPost> mPosts;
    private GoogleApiClient mGoogleApiClient;
    private Context mContext;
    private String mSubreddit;

    public PostListRecyclerViewAdapter() {
        mPosts = new ArrayList<>();
    }

    public PostListRecyclerViewAdapter(List<RedditPost> posts, GoogleApiClient api, String subreddit, Context ctx) {
        mPosts = posts;
        mGoogleApiClient = api;
        mContext = ctx;
        mSubreddit = subreddit;
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    @Override
    public PostListViewHolder onCreateViewHolder(ViewGroup viewGroup, final int i) {

        final View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.postlist_item, viewGroup, false);

        /*
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Intent details = new Intent(v.getContext(), Details.class);

                final Bundle bun = new Bundle();
                bun.putString("permalink", mPosts.get(mRecyclerView.getChildAdapterPosition(v)).getPermaLink());
                details.putExtras(bun);

                v.getContext().startActivity(details);
            }
        });
        */
        return new PostListViewHolder(v);
    }

    @Override
    public void onBindViewHolder(PostListViewHolder personViewHolder, final int i) {
        final RedditPost post = mPosts.get(i);
        personViewHolder.title.setText(post.getTitle());
        personViewHolder.score.setText(String.valueOf(post.getScore()));
        personViewHolder.displayDate.setText(post.getDisplayDate());
        personViewHolder.author.setText(post.getAuthor());
        personViewHolder.totalComments.setText(String.valueOf(post.getCommentTotal()));
        personViewHolder.domain.setText(post.getDomain());

        personViewHolder.commentsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelfIntent(v, i);
                RecordAchievement(mPosts.get(i));
            }
        });

        if (post.IsSticky())
        {
            int darkGreen = Color.parseColor("#006400");
            personViewHolder.title.setTextColor(darkGreen);
            personViewHolder.title.setTextColor(darkGreen);
            personViewHolder.score.setTextColor(darkGreen);
            personViewHolder.displayDate.setTextColor(darkGreen);
            personViewHolder.author.setTextColor(darkGreen);
            personViewHolder.domain.setTextColor(darkGreen);
            personViewHolder.pts.setTextColor(darkGreen);
            personViewHolder.by.setTextColor(darkGreen);
        }
        else
        {
            personViewHolder.postLayout.setBackgroundColor(Color.parseColor("#eeeeee"));
            personViewHolder.title.setTextColor(Color.DKGRAY);
            personViewHolder.title.setTextColor(Color.DKGRAY);
            personViewHolder.score.setTextColor(Color.DKGRAY);
            personViewHolder.displayDate.setTextColor(Color.DKGRAY);
            personViewHolder.author.setTextColor(Color.DKGRAY);
            personViewHolder.domain.setTextColor(Color.DKGRAY);
            personViewHolder.pts.setTextColor(Color.DKGRAY);
            personViewHolder.by.setTextColor(Color.DKGRAY);
        }

        if (post.IsSelf()) {
            personViewHolder.postLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SelfIntent(v, i);
                    RecordAchievement(mPosts.get(i));
                }
            });
        }
        else {
            personViewHolder.postLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(mPosts.get(i).getUrl())));
                    RecordAchievement(mPosts.get(i));
                }
            });
            //personViewHolder.domain.setVisibility(View.GONE);
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
        bun.putString("permalink", mPosts.get(i).getPermaLink());
        bun.putBoolean("sticky", mPosts.get(i).IsSticky());
        bun.putString("subreddit", mSubreddit);
        details.putExtras(bun);

        v.getContext().startActivity(details);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
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


