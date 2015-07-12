package com.rubenhag.quake.activities;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.cocoahero.android.geojson.Feature;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.rubenhag.quake.R;
import com.rubenhag.quake.adapters.TweetListAdapter;
import com.rubenhag.quake.domain.Tweet;
import com.rubenhag.quake.domain.User;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback{

    private GoogleMap mMap;
    private Double lat = null;
    private Double lon = null;
    private String placeString = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Bundle b = getIntent().getExtras();
        Feature feature = b.getParcelable("feature");

        Log.d("Feature", feature.getIdentifier());
        ArrayList<Tweet> tweets = fileToTweets(this, feature);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        try {
            lat = feature.getGeometry().toJSON().getJSONArray("coordinates").getDouble(1);
            lon = feature.getGeometry().toJSON().getJSONArray("coordinates").getDouble(0);
            placeString = feature.getProperties().getString("place");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(tweets!=null) {
            final ListView tweetList = (ListView) findViewById(R.id.tweetList);
            tweetList.setAdapter(new TweetListAdapter(this, tweets));
        }
        mapFragment.getMapAsync(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private ArrayList<Tweet> fileToTweets(Context context, Feature feature){
        File[] filesDir = new File(context.getFilesDir().toString()).listFiles();
        File featureFile = null;
        for(File aFile : filesDir){
            if(aFile.getName().equals(feature.getIdentifier())){
                Log.d("File", "File found");
                featureFile = aFile;

            }
        }
        if(featureFile == null){
            return null;
        }

        try {
            Log.d("MapActivity", "Making tweets");
            ArrayList<Tweet> res = new ArrayList<>();
            FileReader in = new FileReader(featureFile);
            BufferedReader bufferedReader = new BufferedReader(in);
            String line = bufferedReader.readLine();
            Tweet tweet = null;
            User user = null;
            if(line==null){
                Log.d("Line", "Line == null");
            }
            while (line!=null){
                tweet = new Tweet();
                tweet.setText(line);
                res.add(tweet);
                line = bufferedReader.readLine();
            }
            Log.d("Tweets", "Number of tweets: "+res.size());
            return res;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e){
            e.printStackTrace();
        }

        return null;

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng location = new LatLng(lat, lon);
        googleMap.addMarker(new MarkerOptions().position(location).title(placeString));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(location));

    }
}
