package com.qualcode.reddoulette.adapters;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.qualcode.reddoulette.R;
import com.qualcode.reddoulette.activities.Details;
import com.qualcode.reddoulette.models.RedditPost;

import java.util.List;

public class PostListRecyclerViewAdapter extends RecyclerView.Adapter<PostListRecyclerViewAdapter.PostListViewHolder> {

    List<RedditPost> mPosts;
    RecyclerView mRecyclerView;

    public PostListRecyclerViewAdapter(List<RedditPost> posts, RecyclerView rv) {
        mPosts = posts;
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    @Override
    public PostListViewHolder onCreateViewHolder(ViewGroup viewGroup, final int i) {
        final View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.postlist_item, null);

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
            }
        });

        if (post.IsSticky())
        {
            personViewHolder.postLayout.setBackgroundColor(Color.GREEN);
        }
        else
        {
            personViewHolder.postLayout.setBackgroundColor(Color.WHITE);
        }

        if (post.IsSelf()) {
            personViewHolder.postLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SelfIntent(v, i);
                }
            });
        }
        else {
            personViewHolder.postLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(mPosts.get(i).getUrl())));
                }
            });
            //personViewHolder.domain.setVisibility(View.GONE);
        }
    }

    private void SelfIntent(final View v, final int i)
    {
        final Intent details = new Intent(v.getContext(), Details.class);

        final Bundle bun = new Bundle();
        bun.putString("permalink", mPosts.get(i).getPermaLink());
        details.putExtras(bun);

        v.getContext().startActivity(details);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public class PostListViewHolder extends RecyclerView.ViewHolder {
        TextView title, score, displayDate, author, domain, totalComments;
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
        }
    }
}


