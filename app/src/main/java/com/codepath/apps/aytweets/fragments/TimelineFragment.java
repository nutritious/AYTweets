package com.codepath.apps.aytweets.fragments;

import android.os.Bundle;
import android.widget.Toast;

import com.codepath.apps.aytweets.models.Tweet;
import com.codepath.apps.aytweets.network.TimelineType;
import com.codepath.apps.aytweets.network.TwitterTimelineResponseHandler;

import java.util.ArrayList;

/**
 * Created by ayegorov on 11/16/15.
 */
public class TimelineFragment extends TimelineFragmentBase {

    private Bundle extraParams;

    public static TimelineFragment newInstance(TimelineType timelineType, Bundle extraParams) {
        Bundle args = new Bundle();
        args.putInt("Type", timelineType.ordinal());
        TimelineFragment fragment = new TimelineFragment();
        fragment.setArguments(args);
        fragment.extraParams = extraParams;
        return fragment;
    }

    @Override
    public void fetchTimelineInitial() {
        client.getTimelineInitial(timelineType, extraParams, new TwitterTimelineResponseHandler()
        {
            @Override
            public void onSuccess(ArrayList<Tweet> newTweets) {
                tweets.clear();
                tweets.addAll(newTweets);
                tweetsTimelineAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode, String errorMessage) {
                Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean fetchTimelineMore() {
        return client.getTimelineMore(timelineType, extraParams, new TwitterTimelineResponseHandler()
        {
            @Override
            public void onSuccess(ArrayList<Tweet> newTweets) {
                tweets.addAll(newTweets);
                tweetsTimelineAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode, String errorMessage) {
                Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void fetchTimelineRefresh() {
        // Your code to refresh the list here.
        // Make sure you call swipeContainer.setRefreshing(false)
        // once the network request has completed successfully.
        client.getTimelineRefresh(timelineType, extraParams, new TwitterTimelineResponseHandler() {
            @Override
            public void onSuccess(ArrayList<Tweet> tweets) {
                if (tweets.size() > 0) {
                    TimelineFragment.this.tweets.addAll(0, tweets);
                    // TODO: we need to handle gap situation here
                    tweetsTimelineAdapter.notifyDataSetChanged();
                }

                swipeContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, String errorMessage) {
                Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
                swipeContainer.setRefreshing(false);
            }
        });
    }

    @Override
    public void fetchTimelineRefreshAfterTweet(long tweetId) {
        client.getTimelineRefreshAfter(timelineType, tweetId, extraParams, new TwitterTimelineResponseHandler() {
            @Override
            public void onSuccess(ArrayList<Tweet> newTweets) {
                if (newTweets.size() > 0) {
                    tweets.addAll(0, newTweets);
                    // TODO: we need to handle gap situation here
                    tweetsTimelineAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(int statusCode, String errorMessage) {
                Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
