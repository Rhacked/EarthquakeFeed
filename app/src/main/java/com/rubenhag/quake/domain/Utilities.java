package com.rubenhag.quake.domain;

import android.util.Log;


import com.cocoahero.android.geojson.Feature;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class Utilities {


    /**
     * Cuts all but 20 objects from a given List
     * @param list
     * @return New list of maximum 20 objects
     */
    public static List cutList(List list){
        Iterator iterator = list.listIterator();
        while(iterator.hasNext()){
            if(list.indexOf(iterator.next())>19){
                iterator.remove();
            }
        }
        return list;
    }

    /**
     * Sorts a List of Features based on Magnitude. Returns a List of 20 Features
     * @param features
     * @return List of 20 Features
     */
    public static List getTopTwenty(List<Feature> features){
        Collections.sort(features, new MagnitudeComparator());
        if(features == null){
            Log.d("Utilities", "list is null");
            return null;
        }
        cutList(features);
        return features;
    }

    /**
     * Returns a List containing only the Features that caused a Tsunami
     * @param features
     * @return List of Features that caused a Tsunami
     */
    public static List getTsunamiList(List<Feature> features){
        if(features == null){
            Log.d("Utilities", "list is null");
            return null;
        }

        List<Feature> res = new ArrayList<>();
        Iterator itr = features.listIterator();
        Feature feature = null;
        while(itr.hasNext()){
            try {
                feature = features.get(features.indexOf(itr.next()));
                if (feature.getProperties().getInt("tsunami") > 0){
                    res.add(feature);
                }
            } catch (JSONException e){
                Log.e("Utilities", "JSONException", e);
            }

        }

        return res;
    }


    /**
     * Compares the Magnitude property of two Features and returns the result
     */
    private static class MagnitudeComparator implements Comparator<Feature> {

        @Override
        public int compare(Feature feature1, Feature feature2) {
                JSONObject props1 = feature1.getProperties();
                JSONObject props2 = feature2.getProperties();
                try {
                    double mag1 = props1.getDouble("mag");
                    double mag2 = props2.getDouble("mag");
                    return Double.compare(mag2, mag1);
                } catch (JSONException e) {
                    Log.e("Utilities", "JSONException", e);
                }
                return 0;
        }
    }

}

