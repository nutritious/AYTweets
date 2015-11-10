package com.codepath.apps.aytweets.network;

import android.util.Log;

import com.codepath.apps.aytweets.models.Tweet;

import java.util.ArrayList;

/**
 * Created by ayegorov on 11/9/15.
 */
public class TwitterResponseHandler implements TwitterErrorHandler {

    public void onSuccess(ArrayList<Tweet> tweets) {
        Log.w("AYTweets.DEBUG", "got the tweets: " + tweets);
    }

    public void onFailure(int statusCode, String errorMessage) {
        Log.w("AYTweets.DEBUG", "request failed with code '" + statusCode + "' message '" + errorMessage + "'");
    }
}
