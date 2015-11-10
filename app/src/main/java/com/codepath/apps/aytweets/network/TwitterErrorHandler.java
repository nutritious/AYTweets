package com.codepath.apps.aytweets.network;

/**
 * Created by ayegorov on 11/9/15.
 */
public interface TwitterErrorHandler {
    void onFailure(int statusCode, String errorMessage);
}
