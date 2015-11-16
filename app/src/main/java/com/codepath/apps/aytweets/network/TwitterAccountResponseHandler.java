package com.codepath.apps.aytweets.network;

import android.util.Log;

import com.codepath.apps.aytweets.models.User;

/**
 * Created by ayegorov on 11/16/15.
 */
public class TwitterAccountResponseHandler implements TwitterErrorHandler {

    public void onSuccess(User user) {
        // Do nothing for now
    }

    @Override
    public void onFailure(int statusCode, String errorMessage) {
        Log.w("AYTweets.DEBUG", "request failed with code '" + statusCode + "' message '" + errorMessage + "'");
    }
}
