package com.qualcode.reddoulette.activities;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.qualcode.reddoulette.R;
import com.qualcode.reddoulette.adapters.DetailListRecyclerViewAdapter;
import com.qualcode.reddoulette.common.BaseGameUtils;
import com.qualcode.reddoulette.common.DividerItemDecoration;
import com.qualcode.reddoulette.common.Utilities;
import com.qualcode.reddoulette.models.RedditComment;
import com.qualcode.reddoulette.models.RedditObject;
import com.qualcode.reddoulette.models.RedditPost;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class Details extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener  {

    private  List<RedditObject> mObjects = new ArrayList<>();
    protected RecyclerView mRecyclerView;
    private String mUrl;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private GoogleApiClient mGoogleApiClient;  // initialized in onCreate
    private static int RC_SIGN_IN = 9001;

    private boolean mResolvingConnectionFailure = false;
    private boolean mAutoStartSignInflow = true;
    private boolean mSignInClicked = false;
    private boolean mExplicitSignOut = false;
    private boolean mInSignInFlow = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details);

        mRecyclerView = (RecyclerView)findViewById(R.id.commentlist);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this));
        mRecyclerView.setAdapter(new DetailListRecyclerViewAdapter());

        mUrl = getIntent().getExtras().getString("permalink");
        setTitle(R.string.app_name);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.detailsRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshContent();
            }
        });

        refreshContent();

        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("signedout", false) == false) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(Games.API)
                    .addScope(Games.SCOPE_GAMES)
                    .build();
        }
    }

    private void refreshContent() {
        mObjects = new ArrayList<>();
        new GetDetails(this).execute();
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == RC_SIGN_IN) {
            mSignInClicked = false;
            mResolvingConnectionFailure = false;
            if (resultCode == RESULT_OK) {
                mGoogleApiClient.connect();
            } else {
                BaseGameUtils.showActivityResultError(this, requestCode, resultCode, R.string.signin_failure);
            }
        }

        invalidateOptionsMenu();
    }

    @Override
    public void onConnected(Bundle bundle) {
        /*
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            Games.Achievements.increment(mGoogleApiClient, getString(R.string.achievement_participant_viewer), 1);

            final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

            final int totalViews = prefs.getInt("achievement_post_views", 0) + 1;

            final SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("achievement_post_views", totalViews);
            editor.commit();

            Games.Leaderboards.submitScore(mGoogleApiClient, getString(R.string.leaderboard_most_posts_viewed), totalViews);
        }
        */
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        if (mResolvingConnectionFailure) return;

        if (mSignInClicked || mAutoStartSignInflow) {
            mAutoStartSignInflow = false;
            mSignInClicked = false;
            mResolvingConnectionFailure = true;

            if (!BaseGameUtils.resolveConnectionFailure(this, mGoogleApiClient, connectionResult, RC_SIGN_IN, "Error")) {
                mResolvingConnectionFailure = false;
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null && !mInSignInFlow && !mExplicitSignOut) {
            // auto sign in
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected())
            mGoogleApiClient.disconnect();
    }

    public class GetDetails extends AsyncTask<Void, Void, Void> {
        private ProgressDialog dialog;
        private String url;

        public GetDetails(final Activity activity) {
            mSwipeRefreshLayout.setRefreshing(true);

        }

        @Override
        protected Void doInBackground(Void... params) {

            //url = "https://www.reddit.com/r/Android/comments/3glqqd/dev_i_just_published_an_app_aimed_for_high_school/.json"; //self with comments
            //url = "https://www.reddit.com/r/ToolBand/comments/3gozjm/sam_harris_drugs_and_the_meaning_of_life/.json"; //video no comment
            //url = "https://www.reddit.com/r/NorthKoreaPics/comments/3g066g/local_boys_walking_pass_the_kim_ii_sung_parade/.json";
            url = mUrl.concat(".json");
            String json = Utilities.GetRemoteJSON(url);

            try {
                final List<RedditComment> comments = new ArrayList<>();

                final JSONArray childrenPost = new JSONArray(json).getJSONObject(0).getJSONObject("data").getJSONArray("children");

                final JSONObject postData = childrenPost.getJSONObject(0).getJSONObject("data");

                RedditObject objPost = new RedditObject();

                RedditPost post = Utilities.GetPost(postData);
                post.setText(postData.getString("selftext"));
                post.isSelf = postData.getBoolean("is_self");
                post.setText(postData.getString("selftext"));

                objPost.setPost(post);

                mObjects.add(objPost);

                RedditObject objComment = new RedditObject();

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
                objComment.setComments(comments);

                mObjects.add(objComment);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void ununsed) {

            if (mObjects.size() > 0) {
                final DetailListRecyclerViewAdapter adapter = new DetailListRecyclerViewAdapter(mObjects);
                mRecyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            if (mSwipeRefreshLayout.isRefreshing())
                mSwipeRefreshLayout.setRefreshing(false);
            }
    }

    private void Share()
    {
        final Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");

        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, mObjects.get(0).getPost().getTitle());
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

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        if (id == R.id.action_share) {
            Share();
            return true;
        }

        if (id == R.id.action_open_reddit) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(mObjects.get(0).getPost().getPermaLink())));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
