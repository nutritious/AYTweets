package com.codepath.apps.aytweets.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.codepath.apps.aytweets.R;
import com.codepath.apps.aytweets.adapters.EndlessScrollListener;
import com.codepath.apps.aytweets.adapters.TweetsTimelineAdapter;
import com.codepath.apps.aytweets.application.TwitterApplication;
import com.codepath.apps.aytweets.models.Tweet;
import com.codepath.apps.aytweets.network.TimelineType;
import com.codepath.apps.aytweets.network.TwitterClient;

import java.util.ArrayList;

/**
 * Created by ayegorov on 11/14/15.
 */
public abstract class TimelineFragmentBase extends Fragment {

    // Data
    protected final TwitterClient client = TwitterApplication.getRestClient();

    // UI
    protected SwipeRefreshLayout swipeContainer;
    protected ListView tweetsListView;
    protected TweetsTimelineAdapter tweetsTimelineAdapter;
    protected ArrayList<Tweet> tweets;
    protected TimelineType timelineType = TimelineType.Undefined;

    // Tmp shortcuts for parent activity
    public TweetsTimelineAdapter getTweetsTimelineAdapter() {
        return tweetsTimelineAdapter;
    }

    public ArrayList<Tweet> getTweets() {
        return tweets;
    }

    // Data source interface to implement
    protected abstract void fetchTimelineInitial();
    protected abstract boolean fetchTimelineMore();
    protected abstract void fetchTimelineRefresh();

    public abstract void fetchTimelineRefreshAfterTweet(long tweetId);

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View parentView = inflater.inflate(R.layout.fragment_timeline, container, false);

        tweetsListView = (ListView) parentView.findViewById(R.id.tweetsListView);
        tweetsTimelineAdapter = new TweetsTimelineAdapter(getActivity(), tweets);
        tweetsListView.setAdapter(tweetsTimelineAdapter);

        tweetsListView.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                return fetchTimelineMore();
            }
        });

        fetchTimelineInitial();

        //
        // Swipe to refresh
        //
        swipeContainer = (SwipeRefreshLayout) parentView.findViewById(R.id.swipeContainer);

        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchTimelineRefresh();
            }
        });

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        return parentView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        timelineType = TimelineType.values()[getArguments().getInt("Type")];
        tweets = new ArrayList<Tweet>();
    }

}
