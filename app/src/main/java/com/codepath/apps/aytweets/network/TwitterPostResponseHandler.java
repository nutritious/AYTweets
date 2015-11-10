package com.codepath.apps.aytweets.network;

import android.util.Log;

/**
 * Created by ayegorov on 11/9/15.
 */
public class TwitterPostResponseHandler implements TwitterErrorHandler {

    public void onSuccess(long tweetId) {
        // Do nothing for now
    }

    public void onFailure (int statusCode, String errorMessage) {
        Log.d("DEBUG.AYTweets: ", errorMessage);
    }
}
