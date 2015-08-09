package com.qualcode.randit.asynctasks;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import com.qualcode.randit.R;
import com.qualcode.randit.common.Utilities;
import com.qualcode.randit.interfaces.RandomSubredditResponse;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class GetRandomSubreddit extends AsyncTask<Void, Void, String> {
    private ProgressDialog dialog;
    private Activity activity;
    public RandomSubredditResponse delegate=null;

    public GetRandomSubreddit(final Activity activity) {
        this.activity = activity;
        this.dialog = new ProgressDialog(activity);
        this.dialog.setTitle(R.string.app_name);
        this.dialog.setMessage("Finding Random Subreddit...");

        if(!this.dialog.isShowing())
            this.dialog.show();
    }

    @Override
    protected String doInBackground(Void... params) {

        DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
        HttpPost httppost = new HttpPost("http://www.reddit.com/r/random/.json");

        httppost.setHeader("Content-type", "application/json");
        InputStream inputStream = null;
        String result = null;
        try {
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();

            inputStream = entity.getContent();

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
            StringBuilder sb = new StringBuilder();

            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            result = sb.toString();

            JSONObject obj = new JSONObject(result);
            JSONObject data1 = (JSONObject) obj.get("data");

            JSONArray children = (JSONArray) data1.get("children");

            JSONObject jsonObject = (JSONObject) children.get(0);
            JSONObject data2 = (JSONObject) jsonObject.get("data");

            return data2.getString("subreddit");

        } catch (Exception e) {
            Utilities.Toast(activity, "error");
        }

        return null;
    }

    @Override
    protected void onPostExecute(String subreddit) {

        if(this.dialog.isShowing())
            this.dialog.dismiss();
    }
}