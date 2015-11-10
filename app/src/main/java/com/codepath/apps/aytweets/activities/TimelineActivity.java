package com.codepath.apps.aytweets.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.codepath.apps.aytweets.R;
import com.codepath.apps.aytweets.adapters.EndlessScrollListener;
import com.codepath.apps.aytweets.adapters.TweetsTimelineAdapter;
import com.codepath.apps.aytweets.application.TwitterApplication;
import com.codepath.apps.aytweets.models.Tweet;
import com.codepath.apps.aytweets.network.TwitterClient;
import com.codepath.apps.aytweets.network.TwitterPostResponseHandler;
import com.codepath.apps.aytweets.network.TwitterResponseHandler;

import java.util.ArrayList;

public class TimelineActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 66;

    private SwipeRefreshLayout swipeContainer;

    private ListView tweetsListView;
    private TweetsTimelineAdapter tweetsTimelineAdapter;
    private ArrayList<Tweet> tweets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        setTitle(R.string.title_activity_timeline);


        tweets = new ArrayList<Tweet>();
        tweetsListView = (ListView) findViewById(R.id.tweetsListView);
        tweetsTimelineAdapter = new TweetsTimelineAdapter(this, tweets);
        tweetsListView.setAdapter(tweetsTimelineAdapter);

        final TwitterClient client = TwitterApplication.getRestClient();

        tweetsListView.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                return client.getHomeTimelineMore(new TwitterResponseHandler() {
                    @Override
                    public void onSuccess(ArrayList<Tweet> newTweets) {
                        tweets.addAll(newTweets);
                        tweetsTimelineAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(int statusCode, String errorMessage) {
                        Toast.makeText(TimelineActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        client.getHomeTimelineInitial(new TwitterResponseHandler() {
            @Override
            public void onSuccess(ArrayList<Tweet> newTweets) {
                tweets.clear();
                tweets.addAll(newTweets);
                tweetsTimelineAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode, String errorMessage) {
                Toast.makeText(TimelineActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        //
        // Swipe to refresh
        //
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);

        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                client.getHomeTimelineRefresh(new TwitterResponseHandler() {
                    @Override
                    public void onSuccess(ArrayList<Tweet> tweets) {
                        if (tweets.size() > 0) {
                            TimelineActivity.this.tweets.addAll(0, tweets);
                            // TODO: we need to handle gap situation here
                            tweetsTimelineAdapter.notifyDataSetChanged();
                        }

                        swipeContainer.setRefreshing(false);
                    }

                    @Override
                    public void onFailure(int statusCode, String errorMessage) {
                        Toast.makeText(TimelineActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                        swipeContainer.setRefreshing(false);
                    }
                });
            }
        });

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }

    public void onComposeTweet(MenuItem item) {
        Intent intent = new Intent(this, ComposeTweetActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            String tweetBody = data.getStringExtra("tweetBody");

            final TwitterClient client = TwitterApplication.getRestClient();
            client.postTweet(tweetBody, new TwitterPostResponseHandler() {
                @Override
                public void onSuccess(long tweetId) {
                    client.getHomeTimelineRefreshAfter(tweetId, new TwitterResponseHandler() {
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
                            Toast.makeText(TimelineActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onFailure(int statusCode, String errorMessage) {
                    Toast.makeText(TimelineActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}
