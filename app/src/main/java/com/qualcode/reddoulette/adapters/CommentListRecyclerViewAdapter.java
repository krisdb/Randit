package com.qualcode.reddoulette.adapters;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qualcode.reddoulette.R;
import com.qualcode.reddoulette.activities.Details;
import com.qualcode.reddoulette.models.RedditComment;
import com.qualcode.reddoulette.models.RedditPost;

import java.util.List;

public class CommentListRecyclerViewAdapter extends RecyclerView.Adapter<CommentListRecyclerViewAdapter.PostListViewHolder> {

    List<RedditComment> mComments;
    RecyclerView mRecyclerView;

    public CommentListRecyclerViewAdapter(List<RedditComment> comments, RecyclerView rv) {
        mComments = comments;
        mRecyclerView = rv;
    }

    @Override
    public int getItemCount() {
        return mComments.size();
    }

    @Override
    public PostListViewHolder onCreateViewHolder(ViewGroup viewGroup, final int i) {
        final View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.comment_item, null);

        return new PostListViewHolder(v);
    }

    @Override
    public void onBindViewHolder(PostListViewHolder personViewHolder, int i) {
        personViewHolder.comment.setText(mComments.get(i).getText());
        personViewHolder.author.setText(mComments.get(i).getAuthor());
        personViewHolder.score.setText(String.valueOf(mComments.get(i).getScore()));
        personViewHolder.displayDate.setText(mComments.get(i).getDisplayDate());
    }


    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public class PostListViewHolder extends RecyclerView.ViewHolder {
        TextView comment, author, score, displayDate;

        PostListViewHolder(View itemView) {
            super(itemView);
            comment = (TextView)itemView.findViewById(R.id.comment_text);
            author = (TextView)itemView.findViewById(R.id.comment_author);
            score = (TextView)itemView.findViewById(R.id.comment_score);
            displayDate = (TextView)itemView.findViewById(R.id.comment_displayDate);
        }
    }
}


