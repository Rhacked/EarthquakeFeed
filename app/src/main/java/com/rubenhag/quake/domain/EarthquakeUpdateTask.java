package com.rubenhag.quake.domain;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;


import com.cocoahero.android.geojson.Feature;
import com.cocoahero.android.geojson.FeatureCollection;
import com.cocoahero.android.geojson.GeoJSON;
import com.cocoahero.android.geojson.GeoJSONObject;

import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

public class EarthquakeUpdateTask extends AsyncTask<Void, Void, List> {

    private final Context context;

    public EarthquakeUpdateTask(Context context){
        this.context = context;
    }


    @Override
    protected List doInBackground(Void... params) {
            List<Feature> features = null;
            Log.d("AsyncTask", "**** Executing EarthquakeUpdateTask ****");
            URL url = null;
            try {
                url = new URL("http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/2.5_week.geojson");
            } catch (MalformedURLException e) {
                Log.e("EarthquakeUpdateTask", "MalformedURLException", e);
            }
            if(url !=null){
                try {
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        InputStream in = new BufferedInputStream(connection.getInputStream());
                        features = readStream(in);
                } catch (IOException e) {
                    Log.e("EarthquakeUpdateTask", "IOException", e);
                }
            }
            return Utilities.getTopTwenty(features);
        }


    /**
     * Reads an InputStream from a JSON source and returns a list of GeoJSON Features
     * @param inputStream
     * @return List of Features
     */
    private List<Feature> readStream(InputStream inputStream){
        try{
            GeoJSONObject geoJSON = GeoJSON.parse(inputStream);
            jsonToFile(geoJSON.toString());
            if(geoJSON instanceof FeatureCollection){
                FeatureCollection featureCollection = (FeatureCollection) geoJSON;
                List<Feature> features = featureCollection.getFeatures();
                Iterator iterator = features.iterator();
                Log.d("features", "Num features: "+features.size());
                while(iterator.hasNext()){
                    Log.d("features", ""+iterator.next());
                }
                return features;
            }
        } catch (JSONException e) {
            Log.e("EarthquakeUpdateTask", "JSONException: ",e);
        } catch (IOException e) {
            Log.e("EarthquakeUpdateTask", "IOException: ", e);
        }
        return null;
    }

    /**
     * Creates a file containing a JSON-string
     * @param json
     */
    private void jsonToFile(String json){
        if(json!=null && json.length()>0) {
            try {
                final File file = new File(context.getFilesDir(), "EarthquakeFeed");
                FileWriter fileWriter = new FileWriter(file);
                fileWriter.write(json);

            } catch (IOException e) {

            }
        }
    }

}
