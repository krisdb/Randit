package com.qualcode.randit.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.qualcode.randit.R;

import java.util.ArrayList;
import java.util.List;


public class PostListFragment extends ListFragment {
    private Activity activity;
    private static CustomAdapter adapter = null;
    private ListView mListView;

    public static PostListFragment init(String subreddit) {
        PostListFragment plf = new PostListFragment();
        Bundle args = new Bundle();
        args.putString("subreddit", subreddit);
        plf.setArguments(args);
        return plf;
    }

    public static PostListFragment newInstance(String subreddit) {

        PostListFragment lf = new PostListFragment();
        Bundle bundle = new Bundle();
        bundle.putString("subreddit", subreddit);
        lf.setArguments(bundle);

        return lf;
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

        ((TextView)listingView.findViewById(R.id.subreddit)).setText( getArguments().getString("subreddit"));

        adapter = new CustomAdapter();
        //adapter.addItem(item);

        this.setListAdapter(adapter);

        adapter.notifyDataSetChanged();

        return listingView;
    }

    @Override
    public void onActivityCreated(final Bundle icicle) {
        super.onActivityCreated(icicle);
        activity = getActivity();


    }

    @Override
    public void onResume()
    {
        super.onResume();

        activity = getActivity();
        mListView = getListView();
}



    /* ADAPTER */
    private class CustomAdapter extends BaseAdapter
    {
        private final List<String> bookmarks = new ArrayList<>();
        private final LayoutInflater mInflater;

        public CustomAdapter() {
            mInflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public void addItem(String bookmark) {
            bookmarks.add(bookmark);
        }

        @Override
        public int getCount() {
            return bookmarks.size();
        }

        @Override
        public Object getItem(int position) {
            return bookmarks.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent)
        {
            final ViewHolder holder;
            final String bookmark = (String)getItem(position);

            if (convertView == null)
            {
                holder = new ViewHolder();

                //convertView = mInflater.inflate(R.layout.bookmark_list_item, null);
                //holder.bookmark_item_title = (TextView)convertView.findViewById(R.id.bookmark_item_title);
                //holder.bookmark_item_favicon = (ImageView)convertView.findViewById(R.id.bookmark_item_favicon);

                convertView.setTag(holder);
            }
            else
                holder = (ViewHolder)convertView.getTag();

            //holder.bookmark_item_title.setText(bookmark.getTitle());

            //holder.bookmark_item_favicon.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.icon_bookmark));

            return convertView;
        }
    }

    static class ViewHolder {
        TextView bookmark_item_title;
    }
   /* ADAPTER */
}
