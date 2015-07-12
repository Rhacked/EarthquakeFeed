package com.rubenhag.quake.adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.cocoahero.android.geojson.Feature;
import com.rubenhag.quake.R;
import com.rubenhag.quake.domain.Utilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class QuakeListAdapter extends ArrayAdapter {
    private final Context context;
    private final List<Feature> features;

    public QuakeListAdapter(Context context, List<Feature> features) {
        super(context, -1, features);
        this.context = context;
        this.features = features;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        JSONObject props = features.get(position).getProperties();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.quakelist_layout, parent, false);
        TextView place = (TextView) rowView.findViewById(R.id.place);
        TextView magnitude = (TextView) rowView.findViewById(R.id.magnitude);
        try {
            place.setText("Place: "+ props.getString("place"));
            place.setBackgroundResource(getColor(props.getString("magType")));
            magnitude.setText("Magnitude: " + props.getString("mag"));
            magnitude.setBackgroundResource(getColor(props.getString("magType")));
        } catch (JSONException e) {
            Log.e("QuakeListAdapter", "JSONException: ",e);
        }
        return rowView;
    }


    /**
     * Returns a color based on the magType input
     * @param magType
     * @return color id
     */
    private static int getColor(String magType){
        switch (magType){
            case "Md":
                return R.color.red;
            case "mb_lg":
                return R.color.green;
            case "mb":
                return R.color.blue;
            case "mwb":
                return R.color.yellow;
            case "mww":
                return R.color.orange;
            case "mwp":
                return R.color.teal;
            case "ml":
                return R.color.lgreen;
        }
        return R.color.white;
    }
}
