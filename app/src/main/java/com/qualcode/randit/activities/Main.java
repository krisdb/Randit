package com.qualcode.randit.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import com.qualcode.randit.R;
import com.qualcode.randit.adapters.PostListRecyclerViewAdapter;
import com.qualcode.randit.common.DividerItemDecoration;
import com.qualcode.randit.common.Utilities;
import com.qualcode.randit.models.RedditPost;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Main extends AppCompatActivity  {
    protected RecyclerView mRecyclerView;
    private List<RedditPost> mPosts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mRecyclerView = (RecyclerView)findViewById(R.id.postlist);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this));

        new GetPosts(this).execute();
    }

    public class GetPosts extends AsyncTask<Void, Void, String> {
        private ProgressDialog dialog;
        private Activity activity;

        public GetPosts(final Activity activity) {
            this.activity = activity;
            this.dialog = new ProgressDialog(activity);
            this.dialog.setTitle(R.string.app_name);
            this.dialog.setMessage("Searching...");

            if (!this.dialog.isShowing())
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

            if (this.dialog.isShowing())
                this.dialog.dismiss();

            setTitle("r/".concat(subreddit.toLowerCase()));

            final PostListRecyclerViewAdapter adapter = new PostListRecyclerViewAdapter(mPosts);
            mRecyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
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
                JSONObject topic = posts.getJSONObject(i).getJSONObject("data");

                String url = topic.getString("url");
                String author = topic.getString("author");
                String domain = topic.getString("domain").toLowerCase();
                Date postDate = Utilities.FormatDate("2012-08-09 12:12:12 GMT");
                String displayDate = Utilities.GetDisplayDate("2012-08-09 12:12:12 GMT");
                //Date postDate = Utilities.FormatDate(topic.getString("created_utc"));
                //String displayDate = Utilities.GetDisplayDate(topic.getString("created_utc"));
                int score = Integer.valueOf(topic.getString("score"));
                String title = topic.getString("title");

                mPosts.add(new RedditPost(url, title, author, score, postDate, displayDate, domain));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private String GetRandomSubreddit()
    {
        String json = Utilities.GetRemoteJSON("http://www.reddit.com/r/random/.json");

        JSONObject obj = null;

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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.action_refresh) {
            new GetPosts(this).execute();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
