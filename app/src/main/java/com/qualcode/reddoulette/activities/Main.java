package com.qualcode.reddoulette.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
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
import com.google.android.gms.games.Games;
import com.qualcode.reddoulette.R;
import com.qualcode.reddoulette.adapters.PostListRecyclerViewAdapter;
import com.qualcode.reddoulette.common.BaseGameUtils;
import com.qualcode.reddoulette.common.DividerItemDecoration;
import com.qualcode.reddoulette.common.Utilities;
import com.qualcode.reddoulette.models.RedditPost;
import com.qualcode.reddoulette.models.SubredditObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class Main extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener  {
    protected RecyclerView mRecyclerView;
    private List<SubredditObject> mSubreddit = new ArrayList<>();
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private static GoogleApiClient mGoogleApiClient;
    private static int RC_SIGN_IN = 9001;
    private static int REQUEST_LEADERBOARD = 42374;

    private boolean mResolvingConnectionFailure = false;
    private boolean mAutoStartSignInflow = true;
    private boolean mSignInClicked = false;
    private boolean mExplicitSignOut = false;
    private boolean mInSignInFlow = false;
    private AsyncTask<Void, Void, Void>  mTask;

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
    protected void onStart() {
        super.onStart();
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (mGoogleApiClient != null && !mInSignInFlow && !mExplicitSignOut) {

            if (prefs.getBoolean("initial_run", true)) {
                new ConfirmationDialog().show(getSupportFragmentManager(), "InitialRunDialog");

                final SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("initial_run", false);
                editor.commit();
            }
            else if (prefs.getBoolean("signedout", false) == false && prefs.getBoolean("login_enabled", false))
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
        mSubreddit = new ArrayList<>();
        mTask = new GetPosts(this);
        mTask.execute();
    }


    public class GetPosts extends AsyncTask<Void, Void, Void> {
        private Activity mActivity;

        public GetPosts(final Activity activity) {
            this.mActivity = activity;

            mSwipeRefreshLayout.setRefreshing(true);
        }

        @Override
        protected Void doInBackground(Void... params) {
            final String subreddit = GetRandomSubreddit();

            if (subreddit != null) {
                GetMetaData(subreddit);
                GetPosts();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {

            invalidateOptionsMenu();

            if (mSubreddit == null || mSubreddit.size() == 0)
            {
                Toast.makeText(getApplicationContext(), getString(R.string.error_subreddit), Toast.LENGTH_LONG).show();
                if (mSwipeRefreshLayout.isRefreshing())
                    mSwipeRefreshLayout.setRefreshing(false);
                return;
            }

            setTitle("r/".concat(mSubreddit.get(0).getName()));

            final PostListRecyclerViewAdapter adapter = new PostListRecyclerViewAdapter(mSubreddit, mGoogleApiClient, getApplicationContext());
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

        final String json = Utilities.GetRemoteJSON("http://www.reddit.com/r/".concat(mSubreddit.get(0).getName()).concat("/.json"));

        if (json == null || json.length() == 0) return;

        try {
            final JSONObject response = new JSONObject(json);
            final JSONObject data = response.getJSONObject("data");
            final JSONArray postsData = data.getJSONArray("children");
            final int length = postsData.length();

            SubredditObject so = new SubredditObject();
            final List<RedditPost> posts = new ArrayList<>();

            final Boolean showNSFW = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("show_nsfw", false);

            for (int i = 0; i < length; i++) {
                JSONObject post = postsData.getJSONObject(i).getJSONObject("data");

                if (post.getBoolean("over_18") && showNSFW == false) continue;

                RedditPost rp = Utilities.GetPost(post);
                rp.isSelf = post.getBoolean("is_self");
                rp.setCommentTotal(post.getInt("num_comments"));
                rp.setIsSticky(post.getBoolean("stickied"));

                posts.add(rp);
            }

            so.setPosts(posts);

            mSubreddit.add(so);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void GetMetaData(String subreddit)
    {
        if (mSubreddit == null) return;

        final String json = Utilities.GetRemoteJSON("http://www.reddit.com/r/".concat(subreddit).concat("/about/.json"));

        if (json == null || json.length() == 0) return;

        try {
            final JSONObject response = new JSONObject(json);
            final JSONObject data = response.getJSONObject("data");

            SubredditObject so = new SubredditObject();
            so.setName(subreddit);
            so.setTitle(data.getString("display_name"));
            so.setSubtitle(data.getString("title"));
            so.setSubscribers(data.getInt("subscribers"));

            mSubreddit.add(so);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String GetRandomSubreddit() {
        final String json = Utilities.GetRemoteJSON(getString(R.string.url_random));

        if (json == null || json.length() == 0) return null;

        JSONObject obj;

        try {
            obj = new JSONObject(json);
            final JSONObject data1 = (JSONObject) obj.get("data");

            final JSONArray children = (JSONArray) data1.get("children");

            final JSONObject jsonObject = (JSONObject) children.get(0);
            final JSONObject data2 = (JSONObject) jsonObject.get("data");

            final String subreddit = data2.getString("subreddit").toLowerCase();

            if (subreddit == null || subreddit.length() == 0) return null;

            if (data2.getBoolean("over_18") && PreferenceManager.getDefaultSharedPreferences(this).getBoolean("show_nsfw", false) == false)
            {
                mTask.cancel(true);
                refreshContent();
            }

            if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                if (data2.getBoolean("over_18")) {
                    Games.Achievements.increment(mGoogleApiClient, getString(R.string.achievement_nsfw), 1);
                }

                if (Utilities.ArrayContains(getResources().getStringArray(R.array.subreddits_android), subreddit))
                    Games.Achievements.increment(mGoogleApiClient, getString(R.string.achievement_android_fanboy), 1);

                if (Utilities.ArrayContains(getResources().getStringArray(R.array.subreddits_apple), subreddit))
                    Games.Achievements.increment(mGoogleApiClient, getString(R.string.achievement_apple_fanboy), 1);

                if (subreddit.length() > 4 && subreddit.substring(subreddit.length() - 4, subreddit.length()).equals("porn"))
                    Games.Achievements.increment(mGoogleApiClient, getString(R.string.achievement_sfw), 1);
            }

            return subreddit;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
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
    public boolean onPrepareOptionsMenu (Menu menu) {
        menu.getItem(1).setVisible(mSubreddit != null && mSubreddit.size() > 0); //view in reddit
        menu.getItem(2).setVisible(mGoogleApiClient.isConnected() == false); //sign in
        menu.getItem(3).setVisible(mGoogleApiClient.isConnected()); //leaderboard
        menu.getItem(4).setVisible(mGoogleApiClient.isConnected()); //achievements
        menu.getItem(5).setVisible(mGoogleApiClient.isConnected()); //sign out
        menu.getItem(8).setChecked(PreferenceManager.getDefaultSharedPreferences(this).getBoolean("show_nsfw", false));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_open_reddit) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.reddit.com/r/".concat(mSubreddit.get(0).getTitle()))));
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

        if (id == R.id.action_share_app) {
            ShareApp();
            return true;
        }

        if (id == R.id.action_rate) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.url_playstore))));
            return true;
        }

        if (id == R.id.action_nsfw) {
            final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            final SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("show_nsfw", item.isChecked() ? false : true);
            editor.commit();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static class ConfirmationDialog extends android.support.v4.app.DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            final SharedPreferences.Editor editor = prefs.edit();

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(getString(R.string.dialog_initialrun)).setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mGoogleApiClient.connect();
                    editor.putBoolean("login_enabled", true);
                }
            }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    editor.putBoolean("login_enabled", false);
                }
            });

            editor.commit();
            AlertDialog dialog = builder.create();

            return dialog;
        }
    }

    private void ShareApp()
    {
        final Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");

        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.share_app_subject));
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.share_app_body).replace("{{packageid}}", this.getPackageName()));

        startActivity(Intent.createChooser(sharingIntent, "Share"));
    }

    private void SignOut()
    {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("signedout", true);
        editor.putBoolean("login_enabled", false);
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
        editor.putBoolean("login_enabled", true);
        editor.commit();

        mGoogleApiClient.connect();
        mSignInClicked = true;
    }
}

