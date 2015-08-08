package com.qualcode.randit.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qualcode.randit.R;

import java.util.ArrayList;
import java.util.List;


public class PostListFragment extends Fragment {
    private Activity activity;
    protected RecyclerView mRecyclerView;
    protected LinearLayoutManager mLinearLayoutManager;

    public static PostListFragment init(String subreddit) {
        PostListFragment plf = new PostListFragment();
        Bundle args = new Bundle();
        args.putString("subreddit", subreddit);
        plf.setArguments(args);
        return plf;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setRetainInstance(true);

        final View listingView = inflater.inflate(R.layout.postlist, container, false);
        ((TextView)listingView.findViewById(R.id.subreddit)).setText(getArguments().getString("subreddit"));

        mRecyclerView = (RecyclerView) listingView.findViewById(R.id.postlist);

        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(activity);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        List<String> posts = new ArrayList<>();
        posts.add("Title 1");
        posts.add("Title 2");
        posts.add("Title 3");
        posts.add("Title 4");

        CustomRecyclerViewAdapter adapter = new CustomRecyclerViewAdapter(posts);
        mRecyclerView.setAdapter(adapter);

        return listingView;
    }

    @Override
    public void onActivityCreated(final Bundle icicle) {
        super.onActivityCreated(icicle);
        activity = getActivity();
    }

    public class CustomRecyclerViewAdapter extends RecyclerView.Adapter<CustomRecyclerViewAdapter.PostListViewHolder>{

        List<String> mPosts;

        public CustomRecyclerViewAdapter(List<String> posts)
        {
            mPosts = posts;
        }

        @Override
        public int getItemCount() {
            return mPosts.size();
        }

        @Override
        public PostListViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.postlist_item, viewGroup, false);
            PostListViewHolder cvh = new PostListViewHolder(v);
            return cvh;
        }

        @Override
        public void onBindViewHolder(PostListViewHolder personViewHolder, int i) {
            personViewHolder.title.setText(mPosts.get(i));
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
        }

        public class PostListViewHolder extends RecyclerView.ViewHolder {
            TextView title;

            PostListViewHolder(View itemView) {
                super(itemView);
                title = (TextView)itemView.findViewById(R.id.title);
            }
        }

    }
}

