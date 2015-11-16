package com.codepath.apps.aytweets.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.codepath.apps.aytweets.R;
import com.codepath.apps.aytweets.adapters.TimelinePagerAdapter;
import com.codepath.apps.aytweets.application.TwitterApplication;
import com.codepath.apps.aytweets.models.User;
import com.codepath.apps.aytweets.network.TimelineType;
import com.codepath.apps.aytweets.network.TwitterAccountResponseHandler;
import com.codepath.apps.aytweets.network.TwitterClient;
import com.codepath.apps.aytweets.network.TwitterPostResponseHandler;

public class TimelineActivity extends AppCompatActivity {

    private static final int COMPOSE_TWEET_REQUEST_CODE = 66;

    private TimelinePagerAdapter timelinePagerAdapter;

    final TwitterClient client = TwitterApplication.getRestClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        ViewPager viewPager = (ViewPager)findViewById(R.id.viewpager);
        timelinePagerAdapter = new TimelinePagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(timelinePagerAdapter);

        PagerSlidingTabStrip tabStrip = (PagerSlidingTabStrip)findViewById(R.id.tabs);
        tabStrip.setViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }

    public void onComposeTweet(MenuItem item) {
        Intent intent = new Intent(this, ComposeTweetActivity.class);
        startActivityForResult(intent, COMPOSE_TWEET_REQUEST_CODE);
    }

    public void onUserProfile(MenuItem item) {

        client.getUserProfile(new TwitterAccountResponseHandler() {
            @Override
            public void onSuccess(User user) {
                Intent intent = new Intent(TimelineActivity.this, ProfileActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
            }

            @Override
            public void onFailure(int statusCode, String errorMessage) {
                Toast.makeText(TimelineActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == COMPOSE_TWEET_REQUEST_CODE && resultCode == RESULT_OK) {
            String tweetBody = data.getStringExtra("tweetBody");

            client.postTweet(tweetBody, new TwitterPostResponseHandler() {
                @Override
                public void onSuccess(long tweetId) {
                    timelinePagerAdapter.getItemByTimelineType(TimelineType.Home).fetchTimelineRefreshAfterTweet(tweetId);
                }

                @Override
                public void onFailure(int statusCode, String errorMessage) {
                    Toast.makeText(TimelineActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}
