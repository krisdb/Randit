package com.qualcode.reddoulette.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import com.qualcode.reddoulette.R;
import com.qualcode.reddoulette.adapters.PostListRecyclerViewAdapter;
import com.qualcode.reddoulette.common.DividerItemDecoration;
import com.qualcode.reddoulette.common.Utilities;
import com.qualcode.reddoulette.models.RedditPost;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class Main extends AppCompatActivity  {
    protected RecyclerView mRecyclerView;
    private List<RedditPost> mPosts = new ArrayList<>();
    SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mRecyclerView = (RecyclerView)findViewById(R.id.postlist);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this));

        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.refeshlayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshContent();
            }
        });

        refreshContent();
    }

    private void refreshContent() {
        mPosts = new ArrayList<>();
        new GetPosts(this).execute();
    }

        public class GetPosts extends AsyncTask<Void, Void, String> {
        private ProgressDialog dialog;

        public GetPosts(final Activity activity) {
            this.dialog = new ProgressDialog(activity);
            this.dialog.setTitle(R.string.app_name);
            this.dialog.setMessage("Searching...");

            if (this.dialog.isShowing() == false && mSwipeRefreshLayout.isRefreshing() == false)
                this.dialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {

            String subreddit = GetRandomSubreddit();
            GetPosts(subreddit);

            return subreddit;
        }

        @Override
        protected void onPostExecute(final String subreddit) {

            setTitle(subreddit);

            final PostListRecyclerViewAdapter adapter = new PostListRecyclerViewAdapter(mPosts, mRecyclerView);
            mRecyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();

            if (this.dialog.isShowing())
                this.dialog.dismiss();

            if (mSwipeRefreshLayout.isRefreshing())
                mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    private void GetPosts(final String subreddit)
    {
        if (subreddit == null) return;

        String json = Utilities.GetRemoteJSON("http://www.reddit.com/r/".concat(subreddit).concat("/.json"));

        try {
            JSONObject response = new JSONObject(json);
            JSONObject data = response.getJSONObject("data");
            JSONArray posts = data.getJSONArray("children");
            final int length = posts.length();

            for (int i = 0; i < length; i++) {
                JSONObject post = posts.getJSONObject(i).getJSONObject("data");

                RedditPost rp = Utilities.GetPost(post);
                rp.isSelf = post.getBoolean("is_self");

                mPosts.add(rp);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String GetRandomSubreddit()
    {
        String json = Utilities.GetRemoteJSON("http://www.reddit.com/r/random/.json");

        JSONObject obj;

        try {
            obj = new JSONObject(json);
            JSONObject data1 = (JSONObject) obj.get("data");

            JSONArray children = (JSONArray) data1.get("children");

            JSONObject jsonObject = (JSONObject) children.get(0);
            JSONObject data2 = (JSONObject) jsonObject.get("data");

            return data2.getString("subreddit");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //if (id == R.id.action_settings) {
        //return true;
        //}

        if (id == R.id.action_refresh) {
            refreshContent();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
