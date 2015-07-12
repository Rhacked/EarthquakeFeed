package com.rubenhag.quake.domain;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import com.cocoahero.android.geojson.Feature;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TwitterTask extends AsyncTask <List<Feature>, Void, String> {
    final static String CONSUMER_KEY = "AjaESabx0se0tPewDP7wM3VMH";
    final static String CONSUMER_SECRET = "QF3uBO0jmUghwT40MSzFsOnWxFWyYSk349Yj7T6TM8eFGWLBkg";
    final static String BEARER_TOKEN_URL = "https://api.twitter.com/oauth2/token";
    final static String GEOCODE_URL = "https://api.twitter.com/1.1/search/tweets.json?geocode=";

    private String featureIdentifier = null;
    private Feature feature = null;
    private Context context;
    private ProgressDialog progressDialog;

    public TwitterTask(Context context){
        this.context = context;
        this.progressDialog = new ProgressDialog(context);
    }




    @Override
    protected String doInBackground(List<Feature>... params) {

            String accessToken = getAccessToken();
            if(accessToken!=null){
                for(Feature feature : params[0]){
                    Log.d("TwitterTask", "Processing Feature: "+feature.getIdentifier());
                    generateTwitterFiles(feature, accessToken);
                }
            }

        return null;
    }

    protected void onPreExecute(){
        this.progressDialog.setMessage("Loading...");
        this.progressDialog.setCanceledOnTouchOutside(false);
        this.progressDialog.show();
    }


    /**
     * Sets up a HttpGet object with headers containing Authorization and Content-Type and executes it and parses the response body to jsonToTwitter
     * @param feature
     * @param accessToken
     */
    private void generateTwitterFiles(Feature feature, String accessToken){
        try {
            String locationString = "";
            locationString += feature.getGeometry().toJSON().getJSONArray("coordinates").getDouble(0);
            locationString += ","+feature.getGeometry().toJSON().getJSONArray("coordinates").getDouble(1);
            StringBuilder stringBuilder = new StringBuilder(GEOCODE_URL);
            stringBuilder.append(locationString);
            stringBuilder.append(",100000km");
            stringBuilder.append("&count=20");
            HttpGet httpGet = new HttpGet(stringBuilder.toString());
            httpGet.setHeader("Authorization", "Bearer " + accessToken);
            httpGet.setHeader("Content-Type", "application/json");
            tweetsToFile(context, jsonToTwitter(getHttpResponse(httpGet)), feature);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * Retrieves a bearer token needed for authorization to use the Twitter API
     * @return the access token retrived
     */
    private String getAccessToken(){
        try {
            String urlConsumerKey = URLEncoder.encode(CONSUMER_KEY, "UTF-8");
            String urlSecretKey = URLEncoder.encode(CONSUMER_SECRET,"UTF-8");
            String combinedKey = urlConsumerKey+":"+urlSecretKey;
            String base64EncodedKey =  Base64.encodeToString(combinedKey.getBytes(), Base64.NO_WRAP);
            HttpPost httpPost = new HttpPost(BEARER_TOKEN_URL);
            httpPost.setHeader("Authorization", "Basic " + base64EncodedKey);
            httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
            httpPost.setEntity(new StringEntity("grant_type=client_credentials"));
            JSONObject jsonObject =  new JSONObject(getHttpResponse(httpPost));

            if(jsonObject!= null && jsonObject.getString("token_type").equals("bearer")){
                return jsonObject.getString("access_token");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }



    protected void onPostExecute(String result){
        if(progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }

    /**
     * Executes a httprequest, reads the response and returns it as a String
     * @param httpRequestBase
     * @return responsebody
     */
    private String getHttpResponse(HttpRequestBase httpRequestBase){
        StringBuilder responseBuilder = new StringBuilder();
        try{
            DefaultHttpClient defaultHttpClient = new DefaultHttpClient(new BasicHttpParams());
            HttpResponse httpResponse = defaultHttpClient.execute(httpRequestBase);

            int responseStatusCode = httpResponse.getStatusLine().getStatusCode();
            String responseStatusReason = httpResponse.getStatusLine().getReasonPhrase();

            if(responseStatusCode == 200){

                HttpEntity responseEntitiy = httpResponse.getEntity();
                InputStream inputStream = responseEntitiy.getContent();

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line = null;
                while((line = bufferedReader.readLine())!=null){
                    responseBuilder.append(line);
                }
            } else {
                responseBuilder.append(responseStatusReason);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("TwitterTask", "Responsebuilder.length: "+responseBuilder.toString().length());
        return responseBuilder.toString();
    }

    /**
     * Parses a JSON object, makes Tweet object from it and returns a list of all Tweet objects found
     * @param result String containing JSON
     * @return List of Tweets
     */
    private List<Tweet> jsonToTwitter(String result){
        List<Tweet> tweets = null;
        if(result !=null && result.length()>0){

            try {
                JsonParser parser = new JsonParser();
                JsonObject rootObject = parser.parse(result).getAsJsonObject();
                Log.d("rootObject", rootObject.toString());
                JsonElement statusesElement = rootObject.get("statuses");
                Gson gson = new Gson();
                tweets = new ArrayList<>();

                if(statusesElement.isJsonObject()){
                    Tweet tweet = gson.fromJson(statusesElement, Tweet.class);
                    Log.d("jsonToTwitter", "Making tweet:\n"+tweet.getUser().getScreenName()+" "+tweet.getText());

                    tweets.add(tweet);
                } else if(statusesElement.isJsonArray()){
                    Type statusesListType = new TypeToken<List<Tweet>>(){}.getType();
                    tweets = gson.fromJson(statusesElement, statusesListType);
                    Log.d("jsonToTwitter", "Tweets size: "+tweets.size());
                }

            } catch (IllegalStateException e) {
                Log.e("Exception", "IllegalStateException", e);
            }

        } else {
            Log.d("jsonToTwitter", "results == null || result.length()<=0");
        }
        return tweets;
    }

    /**
     * Creates a file containing the Tweet text from each Tweet related to the given Feature
     * @param context The context that the file will be created within
     * @param tweets The list of Tweets
     * @param feature The related Feature
     */
    private void tweetsToFile(Context context, List<Tweet> tweets, Feature feature){
        final File file = new File(context.getFilesDir(), feature.getIdentifier());
        if(tweets!=null && tweets.size()>0) {
            try {
                PrintStream printStream = new PrintStream(file);
                Log.d("Tweets", "Tweets size: " + tweets.size());
                for (Tweet aTweet : tweets) {
                    Log.d("Tweet", "Writing tweets for " + feature.getIdentifier());
                    Log.d("Tweet", aTweet.getUser().getScreenName() + "splithere123" + aTweet.getText());
                    printStream.println(aTweet.getText());

                }
                printStream.flush();
                printStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Log.d("Tweets", "Tweets == null");
            try {
                PrintStream printStream = new PrintStream(file);
                printStream.println("No tweets to display for the location of this earthquake");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }



}
