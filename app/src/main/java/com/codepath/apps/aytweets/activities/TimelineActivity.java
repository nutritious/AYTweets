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
import com.codepath.apps.aytweets.network.TimelineType;
import com.codepath.apps.aytweets.network.TwitterClient;
import com.codepath.apps.aytweets.network.TwitterPostResponseHandler;

public class TimelineActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 66;
    private TimelinePagerAdapter timelinePagerAdapter;

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
        startActivityForResult(intent, REQUEST_CODE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            String tweetBody = data.getStringExtra("tweetBody");

            final TwitterClient client = TwitterApplication.getRestClient();

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
