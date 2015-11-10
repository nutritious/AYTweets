package com.codepath.apps.aytweets.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.aytweets.R;
import com.codepath.apps.aytweets.models.Tweet;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by ayegorov on 11/8/15.
 */
public class TweetsTimelineAdapter extends ArrayAdapter<Tweet> {

    private class ViewHolder {
        public ImageView userImageView;
        public TextView userNameTextView;
        public TextView tweetBodyTextView;
        public TextView tweetTimestampTextView;
    };

    public TweetsTimelineAdapter(Context context, ArrayList<Tweet> objects) {
        // Q: do we have to put R.layout.timeline_item here? We'll inflate this guy in getView() anyway!
        super(context, R.layout.timeline_item, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Tweet tweet = getItem(position);
        ViewHolder viewHolder = null;

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.timeline_item, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.userImageView = (ImageView)convertView.findViewById(R.id.userImageView);
            viewHolder.userNameTextView = (TextView)convertView.findViewById(R.id.userNameTextView);
            viewHolder.tweetBodyTextView = (TextView)convertView.findViewById(R.id.tweetBodyTextView);
            viewHolder.tweetTimestampTextView = (TextView)convertView.findViewById(R.id.tweetTimestampTextView);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.userImageView.setImageResource(0);
        Picasso.with(getContext()).load(tweet.getUser().getProfileImageUrl()).into(viewHolder.userImageView);
        viewHolder.tweetBodyTextView.setText(tweet.getBody());
        viewHolder.userNameTextView.setText(tweet.getUser().getName());
        viewHolder.tweetTimestampTextView.setText(tweet.getTimestamp());

        return convertView;
    }
}
