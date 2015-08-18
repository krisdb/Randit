package com.qualcode.reddoulette.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.games.Games;
import com.qualcode.reddoulette.R;
import com.qualcode.reddoulette.adapters.PostListRecyclerViewAdapter;
import com.qualcode.reddoulette.common.BaseGameUtils;
import com.qualcode.reddoulette.common.DividerItemDecoration;
import com.qualcode.reddoulette.common.Utilities;
import com.qualcode.reddoulette.models.RedditPost;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class Main extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener  {
    protected RecyclerView mRecyclerView;
    private List<RedditPost> mPosts = new ArrayList<>();
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private String mSubreddit;
    private GoogleApiClient mGoogleApiClient;
    private static int RC_SIGN_IN = 9001;

    private boolean mResolvingConnectionFailure = false;
    private boolean mAutoStartSignInflow = true;
    private boolean mSignInClicked = false;
    private boolean mExplicitSignOut = false;
    private boolean mInSignInFlow = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Games.API)
                .addScope(Games.SCOPE_GAMES)
                .build();

        mRecyclerView = (RecyclerView)findViewById(R.id.postlist);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this));

        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.mainRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshContent();
            }
        });

        refreshContent();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (mResolvingConnectionFailure) {
            // already resolving
            return;
        }

        // if the sign-in button was clicked or if auto sign-in is enabled,
        // launch the sign-in flow
        if (mSignInClicked || mAutoStartSignInflow) {
            mAutoStartSignInflow = false;
            mSignInClicked = false;
            mResolvingConnectionFailure = true;

            // Attempt to resolve the connection failure using BaseGameUtils.
            // The R.string.signin_other_error value should reference a generic
            // error string in your strings.xml file, such as "There was
            // an issue with sign-in, please try again later."
            if (!BaseGameUtils.resolveConnectionFailure(this, mGoogleApiClient, connectionResult, RC_SIGN_IN, "Error")) {
                mResolvingConnectionFailure = false;
            }
        }

        // Put code here to display the sign-in button
    }

    @Override
    public void onConnectionSuspended(int i) {
        // Attempt to reconnect
        mGoogleApiClient.connect();
    }

    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
        if (requestCode == RC_SIGN_IN) {
            mSignInClicked = false;
            mResolvingConnectionFailure = false;
            if (resultCode == RESULT_OK) {
                mGoogleApiClient.connect();
            } else {
                // Bring up an error dialog to alert the user that sign-in
                // failed. The R.string.signin_failure should reference an error
                // string in your strings.xml file that tells the user they
                // could not be signed in, such as "Unable to sign in."
                BaseGameUtils.showActivityResultError(this, requestCode, resultCode, R.string.signin_failure);
            }
        }
        invalidateOptionsMenu();
    }


    @Override
    protected void onStart() {
        super.onStart();
        //if (!mInSignInFlow && !mExplicitSignOut) {
            // auto sign in
            //mGoogleApiClient.connect();
        //}
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {
        menu.getItem(2).setVisible(mSignInClicked == false); //sign in
        menu.getItem(3).setVisible(mSignInClicked); //sign out
        menu.getItem(4).setVisible(mSignInClicked); //leaderboard
        menu.getItem(5).setVisible(mSignInClicked); //achievements
        return true;
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        // The player is signed in. Hide the sign-in button and allow the
        // player to proceed.
    }


    private void refreshContent() {
        mPosts = new ArrayList<>();
        new GetPosts(this).execute();
    }

    public class GetPosts extends AsyncTask<Void, Void, Void> {
        private Activity mActivity;

        public GetPosts(final Activity activity) {
            this.mActivity = activity;

            mSwipeRefreshLayout.setRefreshing(true);
            }

            @Override
            protected Void doInBackground(Void... params) {

                GetRandomSubreddit();
                GetPosts();

                return null;
            }

            @Override
            protected void onPostExecute(Void unused) {

                setTitle("r/".concat(mSubreddit.toLowerCase()));

                final PostListRecyclerViewAdapter adapter = new PostListRecyclerViewAdapter(mPosts, mRecyclerView);
                mRecyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                if (mSwipeRefreshLayout.isRefreshing())
                    mSwipeRefreshLayout.setRefreshing(false);

                final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mActivity);
                final int nsfw = prefs.getInt("achievement_nsfw", 0);

                //if (nsfw == 10)
                    //Games.Achievements.unlock(mGoogleApiClient, getString(R.string.achievement_nsfw));

                final int totalViews = prefs.getInt("achievement_views", 0);

                switch (totalViews)
                {
                    case 5:
                        //Games.Achievements.unlock(mGoogleApiClient, getString(R.string.achievement_participant_refresher));
                        break;
                    case 50:
                        //Games.Achievements.unlock(mGoogleApiClient, getString(R.string.achievement_bronze_refresher));
                        break;
                    case 120:
                        //Games.Achievements.unlock(mGoogleApiClient, getString(R.string.achievement_silver_refresher));
                        break;
                    case 300:
                        //Games.Achievements.unlock(mGoogleApiClient, getString(R.string.achievement_gold_refresher));
                        break;
                    case 1000:
                        //Games.Achievements.unlock(mGoogleApiClient, getString(R.string.achievement_no_life_refresher));
                        break;
                }
            }
    }

    private void GetPosts()
    {
        if (mSubreddit == null) return;

        final String json = Utilities.GetRemoteJSON("http://www.reddit.com/r/".concat(mSubreddit).concat("/.json"));

        try {
            JSONObject response = new JSONObject(json);
            JSONObject data = response.getJSONObject("data");
            JSONArray posts = data.getJSONArray("children");
            final int length = posts.length();

            for (int i = 0; i < length; i++) {
                JSONObject post = posts.getJSONObject(i).getJSONObject("data");

                RedditPost rp = Utilities.GetPost(post);
                rp.isSelf = post.getBoolean("is_self");
                rp.setCommentTotal(post.getInt("num_comments"));
                rp.setIsSticky(post.getBoolean("stickied"));

                mPosts.add(rp);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private Void GetRandomSubreddit()
    {
        String json = Utilities.GetRemoteJSON(getString(R.string.url_random));

        JSONObject obj;

        try {
            obj = new JSONObject(json);
            JSONObject data1 = (JSONObject) obj.get("data");

            JSONArray children = (JSONArray) data1.get("children");

            JSONObject jsonObject = (JSONObject) children.get(0);
            JSONObject data2 = (JSONObject) jsonObject.get("data");

            mSubreddit = data2.getString("subreddit");

            final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            final SharedPreferences.Editor editor = prefs.edit();

            editor.putInt("achievement_views", prefs.getInt("achievement_views", 0) + 1);

            if (data2.getBoolean("over_18"))
            {
                final int nsfw = prefs.getInt("achievement_nsfw", 0) + 1;

                if (nsfw < 11)
                    editor.putInt("achievement_nsfw", nsfw);

            }

            editor.commit();

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

        if (id == R.id.action_open_reddit) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.reddit.com/r/".concat(mSubreddit.toLowerCase()))));
            return true;
        }

        //if (id == R.id.action_settings) {
        //return true;
        //}

        if (id == R.id.action_refresh) {
            refreshContent();
            return true;
        }

        if (id == R.id.action_signin) {
            mSignInClicked = true;
            mGoogleApiClient.connect();
            return true;
        }

        if (id == R.id.action_signout) {
            Games.signOut(mGoogleApiClient);
            mSignInClicked = false;
            mGoogleApiClient.disconnect();
            return true;
        }

        if (id == R.id.action_leaderboard) {
            startActivityForResult(Games.Leaderboards.getAllLeaderboardsIntent(mGoogleApiClient), RC_SIGN_IN);
            return true;
        }

        if (id == R.id.action_achievements) {
            startActivityForResult(Games.Achievements.getAchievementsIntent(mGoogleApiClient), RC_SIGN_IN);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
