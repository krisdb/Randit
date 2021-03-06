package com.qualcode.reddoulette.adapters;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qualcode.reddoulette.R;
import com.qualcode.reddoulette.models.RedditObject;

import java.util.ArrayList;
import java.util.List;

public class DetailListRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private class VIEW_TYPES {
        public static final int Header = 0;
        public static final int Normal = 1;
    }

    private List<RedditObject> mObjects;

    public DetailListRecyclerViewAdapter()
    {
        mObjects = new ArrayList<>();
    }

    public DetailListRecyclerViewAdapter(List<RedditObject> objects) {
        mObjects = objects;
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
        return mObjects != null ? mObjects.get(1).getComments().size() + 1 : 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, final int i) {

        switch (i)
        {
            case VIEW_TYPES.Header:
                return new HeaderViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.comment_header, viewGroup, false));
            case VIEW_TYPES.Normal:
                return new CommentViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.comment_item, viewGroup, false));
            default:
                return new CommentViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.comment_item, viewGroup, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int i) {

        if (holder instanceof HeaderViewHolder) {
            if (mObjects.get(0).getPost().IsSelf() == false)
            {
                SpannableString title = new SpannableString(mObjects.get(0).getPost().getTitle());
                title.setSpan(new UnderlineSpan(), 0, title.length(), 0);
                ((HeaderViewHolder)holder).postTitle.setText(title);

                ((HeaderViewHolder)holder).postHeaderLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(mObjects.get(0).getPost().getUrl())));

                    }
                });
            }
            else
                ((HeaderViewHolder)holder).postTitle.setText(mObjects.get(0).getPost().getTitle());

            ((HeaderViewHolder)holder).postAuthor.setText(mObjects.get(0).getPost().getAuthor());
            ((HeaderViewHolder)holder).postScore.setText(String.valueOf(mObjects.get(0).getPost().getScore()));
            ((HeaderViewHolder)holder).postDisplayDate.setText(mObjects.get(0).getPost().getDisplayDate());
            ((HeaderViewHolder)holder).postText.setText(mObjects.get(0).getPost().getText());

        }
        else {
            ((CommentViewHolder)holder).comment.setText(mObjects.get(1).getComments().get(i - 1).getText());
            ((CommentViewHolder)holder).author.setText(mObjects.get(1).getComments().get(i-1).getAuthor());
            ((CommentViewHolder)holder).score.setText(String.valueOf(mObjects.get(1).getComments().get(i-1).getScore()));
            ((CommentViewHolder)holder).displayDate.setText(mObjects.get(1).getComments().get(i-1).getDisplayDate());
        }
    }


    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView postTitle, postAuthor, postScore, postDisplayDate, postText;
        RelativeLayout postHeaderLayout;

        HeaderViewHolder(View itemView) {
            super(itemView);
            postTitle = (TextView)itemView.findViewById(R.id.post_title);
            postAuthor = (TextView)itemView.findViewById(R.id.post_author);
            postScore = (TextView)itemView.findViewById(R.id.post_score);
            postDisplayDate = (TextView)itemView.findViewById(R.id.post_displaydate);
            postText = (TextView)itemView.findViewById(R.id.post_text);
            postHeaderLayout = (RelativeLayout)itemView.findViewById(R.id.post_header_layout);
        }
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView comment, author, score, displayDate;

        CommentViewHolder(View itemView) {
            super(itemView);
            comment = (TextView)itemView.findViewById(R.id.comment_text);
            author = (TextView)itemView.findViewById(R.id.comment_author);
            score = (TextView)itemView.findViewById(R.id.comment_score);
            displayDate = (TextView)itemView.findViewById(R.id.comment_displayDate);
        }
    }
}


