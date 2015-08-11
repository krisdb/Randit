package com.qualcode.reddoulette.activities;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import com.qualcode.reddoulette.R;
import com.qualcode.reddoulette.adapters.CommentListRecyclerViewAdapter;
import com.qualcode.reddoulette.adapters.PostListRecyclerViewAdapter;
import com.qualcode.reddoulette.common.DividerItemDecoration;
import com.qualcode.reddoulette.common.Utilities;
import com.qualcode.reddoulette.models.RedditComment;
import com.qualcode.reddoulette.models.RedditPost;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class Details extends AppCompatActivity {

    private RedditPost mPost = new RedditPost();
    protected RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details);

        mRecyclerView = (RecyclerView)findViewById(R.id.commentlist);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this));

        String url = getIntent().getExtras().getString("permalink");

        new GetDetails(this, url).execute();
    }

    public class GetDetails extends AsyncTask<Void, Void, Void> {
        private ProgressDialog dialog;
        private String url;

        public GetDetails(final Activity activity, String url) {
            this.url = url;
            this.dialog = new ProgressDialog(activity);
            this.dialog.setTitle(R.string.app_name);
            this.dialog.setMessage("Retrieving...");

            if (!this.dialog.isShowing())
                this.dialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            url = "http://www.reddit.com".concat(url.concat(".json"));
            String json = Utilities.GetRemoteJSON(url);

            try {
                List<RedditComment> comments = new ArrayList<>();

                JSONArray children = new JSONArray(json).getJSONObject(1).getJSONObject("data").getJSONArray("children");
                final int length = children.length();
                for(int i=0; i < length; i++) {

                    if (children.getJSONObject(i).optString("kind") == null || children.getJSONObject(i).optString("kind").equals("t1") == false) continue;

                    JSONObject data = children.getJSONObject(i).getJSONObject("data");

                    String text = data.getString("body");
                    int score = Integer.valueOf(data.getString("score"));
                    Date date = Utilities.FormatDate(data.getString("created_utc"));
                    String displayDate = Utilities.GetDisplayDate(data.getString("created_utc"));

                    comments.add(new RedditComment(text, score, date, displayDate));
                }
                mPost.setComments(comments);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void ununsed) {

            ((TextView)findViewById(R.id.self_text)).setText(mPost.getText());

            final CommentListRecyclerViewAdapter adapter = new CommentListRecyclerViewAdapter(mPost.getComments(), mRecyclerView);
            mRecyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();

            if (this.dialog.isShowing())
                this.dialog.dismiss();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
