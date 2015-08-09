package com.qualcode.randit.common;

import android.content.Context;
import android.widget.Toast;

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
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Utilities {

    public static void Toast(final Context ctx, final String text)
    {
        Toast.makeText(ctx, text, Toast.LENGTH_LONG);
    }

    public static String GetRemoteJSON(final String url)
    {
        URL obj = null;
        try {
            obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection)obj.openConnection();

            // optional default is GET
            con.setRequestMethod("GET");

            //add request header
            con.setRequestProperty("User-Agent", "Randit");

            int responseCode = con.getResponseCode();

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
