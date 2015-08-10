package com.qualcode.randit.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.qualcode.randit.R;
import com.qualcode.randit.adapters.PostListRecyclerViewAdapter;
import com.qualcode.randit.common.Utilities;
import com.qualcode.randit.models.RedditPost;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class Details extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details);

        String url = getIntent().getExtras().getString("url");

        new GetDetails(this, url).execute();
    }

    public class GetDetails extends AsyncTask<Void, Void, Void> {
        private ProgressDialog dialog;
        private String url;

        public GetDetails(final Activity activity, String url) {
            this.url = url;
            this.dialog = new ProgressDialog(activity);
            this.dialog.setTitle(R.string.app_name);
            this.dialog.setMessage("Searching...");

            if (!this.dialog.isShowing())
                this.dialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            String json = Utilities.GetRemoteJSON(url.concat("/.json"));

            try {
                JSONObject response = new JSONObject(json);
                JSONObject data = response.getJSONObject("data");
                JSONArray posts = data.getJSONArray("children");
                final int length = posts.length();

                for (int i = 0; i < length; i++) {
                    JSONObject topic = posts.getJSONObject(i).getJSONObject("data");

                    String text = topic.getString("selftext_html");

                    //mPosts.add(new RedditPost(url, title, author, score, postDate, displayDate, domain));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void ununsed) {

            if (this.dialog.isShowing())
                this.dialog.dismiss();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
