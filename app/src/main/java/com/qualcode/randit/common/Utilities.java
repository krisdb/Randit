package com.qualcode.randit.common;

import android.content.Context;
import android.widget.Toast;

import com.qualcode.randit.models.RedditPost;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class Utilities {

    public static Date FormatDate(final String date)
    {
        return FormatDate(date, "yyyy-MM-dd HH:mm:ss z");
    }

    public static Date FormatDate(final String date, final String pattern)
    {
        final Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        final SimpleDateFormat format = new SimpleDateFormat(pattern);
        format.setCalendar(cal);

        try {
            return format.parse(date);
        } catch(Throwable t) {
            return null;
        }
    }

    public static String GetDisplayDate(final String date)
    {
        final Date itemDateTime = new java.util.Date(Double.valueOf(date).longValue()*1000);
        //final Date itemDateTime = FormatDate(date);

        return android.text.format.DateUtils.getRelativeTimeSpanString(itemDateTime.getTime(), new Date().getTime(), android.text.format.DateUtils.SECOND_IN_MILLIS).toString();

    }

    public static void Toast(final Context ctx, final String text)
    {
        Toast.makeText(ctx, text, Toast.LENGTH_LONG);
    }

    public static RedditPost GetPost(final JSONObject topic) {
        RedditPost post = null;
        try {

            String url = topic.getString("url");
            String author = topic.getString("author");
            String domain = topic.getString("domain").toLowerCase();
            //Date postDate = Utilities.FormatDate("2012-08-09 12:12:12 GMT");
            //String displayDate = Utilities.GetDisplayDate("2012-08-09 12:12:12 GMT");
            Date postDate = Utilities.FormatDate(topic.getString("created_utc"));
            String displayDate = Utilities.GetDisplayDate(topic.getString("created_utc"));
            int score = Integer.valueOf(topic.getString("score"));
            String title = topic.getString("title");

           post = new RedditPost(title, url, author, score, domain, postDate, displayDate);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return post;
    }

        public static String GetRemoteJSON(final String url)
    {
        URL obj;
        try {
            obj = new URL(url);

            HttpURLConnection con = (HttpURLConnection)obj.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", "Randit");

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            in.close();

            return response.toString();

        } catch (Throwable e) {
            e.printStackTrace();
        }

        return  null;
    }

}
