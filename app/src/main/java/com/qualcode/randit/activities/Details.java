package com.qualcode.randit.activities;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import com.qualcode.randit.R;
import com.qualcode.randit.common.Utilities;
import com.qualcode.randit.models.RedditComment;
import com.qualcode.randit.models.RedditPost;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;


public class Details extends AppCompatActivity {

    private RedditPost mPost;

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
            this.dialog.setMessage("Fetching...");

            if (!this.dialog.isShowing())
                this.dialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            url = url.concat(".json");
            String json = Utilities.GetRemoteJSON(url);

            try {
                JSONObject response = new JSONObject(json);
                JSONObject data = response.getJSONObject("data");
                JSONObject children = data.getJSONObject("children");

                JSONObject post = children.getJSONObject("data");

                mPost = Utilities.GetPost(post);
                mPost.setText(post.getString("selftext_html"));
                mPost.setComments(new ArrayList<RedditComment>());

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void ununsed) {

            ((TextView)findViewById(R.id.self_text)).setText(mPost.getText());

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
