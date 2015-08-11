package com.qualcode.randit.adapters;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.qualcode.randit.R;
import com.qualcode.randit.activities.Details;
import com.qualcode.randit.models.RedditPost;

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
                int position = mRecyclerView.getChildAdapterPosition(v);
                RedditPost rp = mPosts.get(position);

                if (rp.IsSelf()) {
                    final Intent details = new Intent(v.getContext(), Details.class);

                    final Bundle bun = new Bundle();
                    bun.putString("url", rp.getUrl());
                    details.putExtras(bun);

                    v.getContext().startActivity(details);
                } else {
                    v.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(rp.getUrl())));
                }
            }
        });

        return new PostListViewHolder(v);
    }

    @Override
    public void onBindViewHolder(PostListViewHolder personViewHolder, int i) {
        personViewHolder.title.setText(mPosts.get(i).getTitle());
        personViewHolder.score.setText(String.valueOf(mPosts.get(i).getGetScore()));
        personViewHolder.displayDate.setText(mPosts.get(i).getDisplayDate());
        personViewHolder.author.setText(mPosts.get(i).getAuthor());
        personViewHolder.domain.setText(mPosts.get(i).getDomain());
    }


    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public class PostListViewHolder extends RecyclerView.ViewHolder {
        TextView title, score, displayDate, author, domain;

        PostListViewHolder(View itemView) {
            super(itemView);
            title = (TextView)itemView.findViewById(R.id.title);
            score = (TextView)itemView.findViewById(R.id.score);
            displayDate = (TextView)itemView.findViewById(R.id.displayDate);
            author = (TextView)itemView.findViewById(R.id.author);
            domain = (TextView)itemView.findViewById(R.id.domain);
        }
    }
}


