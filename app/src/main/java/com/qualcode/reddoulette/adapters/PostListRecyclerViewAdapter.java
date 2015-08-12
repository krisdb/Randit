package com.qualcode.reddoulette.adapters;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
        mRecyclerView = rv;
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    @Override
    public PostListViewHolder onCreateViewHolder(ViewGroup viewGroup, final int i) {
        final View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.postlist_item, null);

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

        return new PostListViewHolder(v);
    }

    @Override
    public void onBindViewHolder(PostListViewHolder personViewHolder, int i) {
        final RedditPost post = mPosts.get(i);
        personViewHolder.title.setText(post.getTitle());
        personViewHolder.score.setText(String.valueOf(post.getScore()));
        personViewHolder.displayDate.setText(post.getDisplayDate());
        personViewHolder.author.setText(post.getAuthor());
        personViewHolder.totalComments.setText(String.valueOf(post.getCommentTotal()));

        if (post.IsSelf() == false) {
            personViewHolder.domain.setText(post.getDomain());
        }
        else
            personViewHolder.domain.setVisibility(View.GONE);
    }


    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public class PostListViewHolder extends RecyclerView.ViewHolder {
        TextView title, score, displayDate, author, domain, totalComments;

        PostListViewHolder(View itemView) {
            super(itemView);
            title = (TextView)itemView.findViewById(R.id.title);
            score = (TextView)itemView.findViewById(R.id.score);
            displayDate = (TextView)itemView.findViewById(R.id.displayDate);
            author = (TextView)itemView.findViewById(R.id.author);
            domain = (TextView)itemView.findViewById(R.id.domain);
            totalComments = (TextView)itemView.findViewById(R.id.comments_count);
        }
    }
}


