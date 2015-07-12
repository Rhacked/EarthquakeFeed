package com.rubenhag.quake.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Parcel;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Switch;

import com.cocoahero.android.geojson.Feature;
import com.cocoahero.android.geojson.FeatureCollection;
import com.cocoahero.android.geojson.GeoJSON;
import com.cocoahero.android.geojson.GeoJSONObject;
import com.rubenhag.quake.R;
import com.rubenhag.quake.adapters.QuakeListAdapter;
import com.rubenhag.quake.domain.EarthquakeUpdateTask;
import com.rubenhag.quake.domain.TwitterTask;
import com.rubenhag.quake.domain.Utilities;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class MainActivity extends Activity {

    private Context context;
    private List<Feature> mainList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        if(isNetworkAvailable()){
            EarthquakeUpdateTask earthquakeUpdateTask = new EarthquakeUpdateTask(this);
            earthquakeUpdateTask.execute();

            try {
                mainList = earthquakeUpdateTask.get();
                TwitterTask twitterTask = new TwitterTask(this);
                twitterTask.execute(earthquakeUpdateTask.get());

            } catch (InterruptedException e) {
                Log.e("exception", "InterruptedException: ", e);
            } catch (ExecutionException e) {
                Log.e("exception", "ExecutionException: ", e);
            }

        } else {

            mainList = getFeaturesFromFile();
        }

        if(mainList !=null){
            Iterator itr = mainList.iterator();
            while(itr.hasNext()){
                Object obj = itr.next();
            }
            final ListView listView = (ListView) findViewById(R.id.quakeList);
            listView.setAdapter(new QuakeListAdapter(this, mainList));
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Feature feature = (Feature) parent.getAdapter().getItem(position);
                    Parcel parcel = Parcel.obtain();
                    feature.writeToParcel(parcel, 0);
                    Intent intent = new Intent(context, MapActivity.class);
                    intent.putExtra("feature", feature);
                    startActivity(intent);

                }
            });
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean isNetworkAvailable(){
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo !=null && activeNetworkInfo.isConnected();
    }

    public void onToggleClicked(View view){
        if(mainList !=null){
            boolean on = ((Switch)view).isChecked();
            if(on){
                ListView listView = (ListView) findViewById(R.id.quakeList);
                listView.setAdapter(new QuakeListAdapter(this, Utilities.getTsunamiList(mainList)));
            } else {
                ListView listView = (ListView) findViewById(R.id.quakeList);
                listView.setAdapter(new QuakeListAdapter(this, Utilities.getTopTwenty(mainList)));
            }
        }
    }

    private List<Feature> getFeaturesFromFile(){
        File[] files = new File(context.getFilesDir().toString()).listFiles();
        File earthquakeFeed = null;
        for(File file : files){
            if(file.getName().equals("EarthquakeFeed")){
                earthquakeFeed = file;
            }
        }
        if(earthquakeFeed==null){
            Log.d("MainActivity", "No file found");
            return null;
        }
        try {
            FileReader in = new FileReader(earthquakeFeed);
            BufferedReader bufferedReader = new BufferedReader(in);
            GeoJSONObject geoJSON = GeoJSON.parse(bufferedReader.readLine());
            if(geoJSON instanceof FeatureCollection) {
                FeatureCollection featureCollection = (FeatureCollection) geoJSON;
                List<Feature> features = featureCollection.getFeatures();
                return features;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}
