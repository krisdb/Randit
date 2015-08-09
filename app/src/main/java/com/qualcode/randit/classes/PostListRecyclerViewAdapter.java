package com.qualcode.randit.classes;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.qualcode.randit.R;
import com.qualcode.randit.models.RedditPost;

import java.util.List;

public class PostListRecyclerViewAdapter extends RecyclerView.Adapter<PostListRecyclerViewAdapter.PostListViewHolder> {

    List<RedditPost> mPosts;

    public PostListRecyclerViewAdapter(List<RedditPost> posts) {
        mPosts = posts;
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    @Override
    public PostListViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        final View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.postlist_item, null);

        final PostListViewHolder cvh = new PostListViewHolder(v);
        return cvh;
    }

    @Override
    public void onBindViewHolder(PostListViewHolder personViewHolder, int i) {
        personViewHolder.title.setText(mPosts.get(i).getTitle());
        personViewHolder.author.setText(mPosts.get(i).getAuthor());
        personViewHolder.score.setText(String.valueOf(mPosts.get(i).getGetScore()));
    }


    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public class PostListViewHolder extends RecyclerView.ViewHolder {
        TextView title, author, score;

        PostListViewHolder(View itemView) {
            super(itemView);
            title = (TextView)itemView.findViewById(R.id.title);
            author = (TextView)itemView.findViewById(R.id.author);
            score = (TextView)itemView.findViewById(R.id.score);
        }
    }
}


