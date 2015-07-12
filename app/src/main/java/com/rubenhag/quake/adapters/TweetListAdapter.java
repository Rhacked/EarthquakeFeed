package com.rubenhag.quake.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.rubenhag.quake.R;
import com.rubenhag.quake.domain.Tweet;

import java.util.List;

public class TweetListAdapter extends ArrayAdapter {
    private final Context context;
    private final List<Tweet> tweets;

    public TweetListAdapter(Context context, List<Tweet> tweets) {
        super(context, -1, tweets);
        this.context = context;
        this.tweets = tweets;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        Tweet tweet = tweets.get(position);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.tweetlist_layout, parent, false);
        TextView text = (TextView) rowView.findViewById(R.id.text);
        text.setText(tweet.getText());
        if(position == 0 || position%2 == 0){
            text.setBackgroundResource(R.color.blue);
        }
        return rowView;
    }
}
