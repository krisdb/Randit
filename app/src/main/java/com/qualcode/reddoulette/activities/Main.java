package com.qualcode.reddoulette.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
    private static GoogleApiClient mGoogleApiClient;
    private static int RC_SIGN_IN = 9001;
    private static int REQUEST_LEADERBOARD = 42374;

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
        mRecyclerView.setAdapter(new PostListRecyclerViewAdapter());

        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.mainRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshContent();
            }
        });

        refreshContent();

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        if (prefs.getBoolean("initial_run", true))
        {
            new ConfirmationDialog().show(getSupportFragmentManager(), "myDialogFragment");

            final SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("initial_run", false);
            editor.commit();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null && !mInSignInFlow && !mExplicitSignOut) {
            // auto sign in
            if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("signedout", false) == false)
                mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected())
            mGoogleApiClient.disconnect();
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

            final PostListRecyclerViewAdapter adapter = new PostListRecyclerViewAdapter(mPosts, mGoogleApiClient, getApplicationContext());
            mRecyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();

            if (mSwipeRefreshLayout.isRefreshing())
                mSwipeRefreshLayout.setRefreshing(false);

            if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                Games.Achievements.increment(mGoogleApiClient, getString(R.string.achievement_participant_refresher), 1);
                Games.Achievements.increment(mGoogleApiClient, getString(R.string.achievement_bronze_refresher), 1);
                Games.Achievements.increment(mGoogleApiClient, getString(R.string.achievement_silver_refresher), 1);
                Games.Achievements.increment(mGoogleApiClient, getString(R.string.achievement_gold_refresher), 1);
                Games.Achievements.increment(mGoogleApiClient, getString(R.string.achievement_no_life_refresher), 1);
                Games.Achievements.increment(mGoogleApiClient, getString(R.string.achievement_participant_refresher), 1);
                Games.Achievements.increment(mGoogleApiClient, getString(R.string.achievement_no_life_refresher), 1);

                final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

                final int totalViews = prefs.getInt("achievement_subreddit_views", 0) + 1;

                final SharedPreferences.Editor editor = prefs.edit();
                editor.putInt("achievement_subreddit_views", totalViews);
                editor.commit();

                Games.Leaderboards.submitScore(mGoogleApiClient, getString(R.string.leaderboard_most_subreddits_viewed), totalViews);
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

    private Void GetRandomSubreddit() {
        String json = Utilities.GetRemoteJSON(getString(R.string.url_random));

        JSONObject obj;

        try {
            obj = new JSONObject(json);
            JSONObject data1 = (JSONObject) obj.get("data");

            JSONArray children = (JSONArray) data1.get("children");

            JSONObject jsonObject = (JSONObject) children.get(0);
            JSONObject data2 = (JSONObject) jsonObject.get("data");

            mSubreddit = data2.getString("subreddit");

            if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                if (data2.getBoolean("over_18")) {
                    Games.Achievements.increment(mGoogleApiClient, getString(R.string.achievement_nsfw), 1);
                }

                if (mSubreddit.length() > 4 && mSubreddit.substring(mSubreddit.length() - 4, mSubreddit.length()).toLowerCase().equals("porn"))
                    Games.Achievements.increment(mGoogleApiClient, getString(R.string.achievement_sfw), 1);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {
        menu.getItem(2).setVisible(mGoogleApiClient.isConnected() == false); //sign in
        menu.getItem(3).setVisible(mGoogleApiClient.isConnected()); //leaderboard
        menu.getItem(4).setVisible(mGoogleApiClient.isConnected()); //achievements
        menu.getItem(5).setVisible(mGoogleApiClient.isConnected()); //sign out
        return true;
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
    public void onConnectionSuspended(int i) {
        // Attempt to reconnect
        mGoogleApiClient.connect();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == RC_SIGN_IN) {
            mSignInClicked = false;
            mResolvingConnectionFailure = false;

            if (resultCode == RESULT_OK)
                mGoogleApiClient.connect();
            else
                BaseGameUtils.showActivityResultError(this, requestCode, resultCode, R.string.signin_failure);
        }

        if (mSwipeRefreshLayout.isRefreshing())
            mSwipeRefreshLayout.setRefreshing(false);
    }


    @Override
    public void onConnected(Bundle connectionHint) {
        invalidateOptionsMenu();
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
            SignIn();
            return true;
        }

        if (id == R.id.action_signout) {
            SignOut();
            return true;
        }

        if (id == R.id.action_leaderboard) {
            startActivityForResult(Games.Leaderboards.getAllLeaderboardsIntent(mGoogleApiClient), REQUEST_LEADERBOARD);
            return true;
        }

        if (id == R.id.action_achievements) {
            startActivityForResult(Games.Achievements.getAchievementsIntent(mGoogleApiClient), REQUEST_LEADERBOARD);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static class ConfirmationDialog extends android.support.v4.app.DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Do you want to login for achievements?").setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mGoogleApiClient.connect();
                }
            }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });

            AlertDialog dialog = builder.create();

            return dialog;
        }
    }

    private void SignOut()
    {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("signedout", true);
        editor.commit();

        Games.signOut(mGoogleApiClient);
        mSignInClicked = false;
        mGoogleApiClient.disconnect();

        invalidateOptionsMenu();
    }

    private void SignIn()
    {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("signedout", false);
        editor.commit();

        mGoogleApiClient.connect();
        mSignInClicked = true;
    }
}

