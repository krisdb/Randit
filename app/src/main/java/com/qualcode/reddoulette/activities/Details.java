package com.qualcode.reddoulette.activities;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.qualcode.reddoulette.R;
import com.qualcode.reddoulette.adapters.CommentListRecyclerViewAdapter;
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
    private String mUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details);

        mRecyclerView = (RecyclerView)findViewById(R.id.commentlist);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this));

        mUrl = getIntent().getExtras().getString("permalink");

        setTitle(R.string.app_name);

        new GetDetails(this).execute();
    }

    public class GetDetails extends AsyncTask<Void, Void, Void> {
        private ProgressDialog dialog;
        private String url;

        public GetDetails(final Activity activity) {
            this.dialog = new ProgressDialog(activity);
            this.dialog.setTitle(R.string.app_name);
            this.dialog.setMessage("Retrieving...");

            if (!this.dialog.isShowing())
                this.dialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            //url = "https://www.reddit.com/r/Android/comments/3glqqd/dev_i_just_published_an_app_aimed_for_high_school/.json";
            url = "http://www.reddit.com".concat(mUrl.concat(".json"));
            String json = Utilities.GetRemoteJSON(url);

            try {
                final List<RedditComment> comments = new ArrayList<>();

                final JSONArray childrenPost = new JSONArray(json).getJSONObject(0).getJSONObject("data").getJSONArray("children");

                final JSONObject postData = childrenPost.getJSONObject(0).getJSONObject("data");

                mPost = Utilities.GetPost(postData);
                mPost.setText(postData.getString("selftext"));
                mPost.isSelf = postData.getBoolean("is_self");

                final JSONArray childrenComments = new JSONArray(json).getJSONObject(1).getJSONObject("data").getJSONArray("children");

                final int length = childrenComments.length();
                for(int i=0; i < length; i++) {

                    if (childrenComments.getJSONObject(i).optString("kind") == null || childrenComments.getJSONObject(i).optString("kind").equals("t1") == false) continue;

                    JSONObject commentData = childrenComments.getJSONObject(i).getJSONObject("data");

                    String text = commentData.getString("body");
                    String author = commentData.getString("author");
                    int score = Integer.valueOf(commentData.getString("score"));
                    Date date = Utilities.FormatDate(commentData.getString("created_utc"));
                    String displayDate = Utilities.GetDisplayDate(commentData.getString("created_utc"));

                    comments.add(new RedditComment(text, author, score, date, displayDate));
                }
                mPost.setComments(comments);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void ununsed) {
            final TextView tvTitle = (TextView)findViewById(R.id.title);
            tvTitle.setText(mPost.getTitle());
            ((TextView)findViewById(R.id.text)).setText(mPost.getText());
            ((TextView)findViewById(R.id.author)).setText(mPost.getAuthor());
            ((TextView)findViewById(R.id.score)).setText(String.valueOf(mPost.getGetScore()));
            ((TextView)findViewById(R.id.displayDate)).setText(String.valueOf(mPost.getDisplayDate()));

            if (mPost.IsSelf() == false) {
                ((TextView)findViewById(R.id.domain)).setText(mPost.getDomain());
                tvTitle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(mPost.getUrl())));
                    }
                });
            }
            else
                findViewById(R.id.domain).setVisibility(View.GONE);

            final CommentListRecyclerViewAdapter adapter = new CommentListRecyclerViewAdapter(mPost.getComments(), mRecyclerView);
            mRecyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();

            if (this.dialog.isShowing())
                this.dialog.dismiss();
        }
    }

    private void Share()
    {
        final Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");

        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, mPost.getTitle());
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "http://www.reddit.com".concat(mUrl));

        startActivity(Intent.createChooser(sharingIntent, "Share"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_share) {
            Share();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
