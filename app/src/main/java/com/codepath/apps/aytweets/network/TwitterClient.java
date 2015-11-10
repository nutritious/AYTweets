package com.codepath.apps.aytweets.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.codepath.apps.aytweets.models.Tweet;
import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

import java.util.ArrayList;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/fernandezpablo85/scribe-java/tree/master/src/main/java/org/scribe/builder/api
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {

	public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class; // Change this
	public static final String REST_URL = "https://api.twitter.com/1.1"; // Change this, base API URL

    // Production API Keys for @yegorich
//	public static final String REST_CONSUMER_KEY = "GI1gRzUDeRutEm0CeNqpg09n4";
//	public static final String REST_CONSUMER_SECRET = "ujgmS5NN0AjSQJ1HlFce5UTlxXnHeXDcjE0qQmcHBTPIWL6WXc";

    // Staging API keys for @grouponicus
    public static final String REST_CONSUMER_KEY = "M0J2XeC9aEwlcBRiJOJsKsjpe";
    public static final String REST_CONSUMER_SECRET = "KFl7ZFPlROku9RZ8ZY5WJmu07TLQDtvwbLuq7KprOLaX3j7wtc";

    // Q: How do I know what to put after oauth:// ?
	public static final String REST_CALLBACK_URL = "oauth://cpaytweets"; // Change this (here and in manifest)

    public static final int PAGE_SIZE = 25;

    private static final long TWEET_PARAM_UNDEFINED = -1;

    private long tweetsLastTweetCount = TWEET_PARAM_UNDEFINED;
    private long tweetsCurrentMaxId = TWEET_PARAM_UNDEFINED;
    private long tweetsCurrentStartId = TWEET_PARAM_UNDEFINED;

	public TwitterClient(Context context) {
		super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
	}

    //
    // Private Helpers
    //
    private Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    private void handleOnFailure(int statusCode, TwitterErrorHandler errorHandler, Object errorResponse) {
        String errorMessage;

        if (!isNetworkAvailable()) {
            errorMessage = "Network is NOT available. Try again later.";
        } else {
                            /*
                            {"errors":[{"message":"Sorry, that page does not exist","code":34}]}
                             88 - Rate limit exceeded
                             */
            errorMessage = errorResponse.toString();
        }

        Log.e("AYTweets.DEBUG", "error: " + errorMessage);

        errorHandler.onFailure(statusCode, errorMessage);
    }

    //
    // Interface
    //

    public void getHomeTimelineInitial(final TwitterResponseHandler handler) {
        tweetsLastTweetCount = TWEET_PARAM_UNDEFINED;
        tweetsCurrentMaxId = TWEET_PARAM_UNDEFINED;
        tweetsCurrentStartId = TWEET_PARAM_UNDEFINED;

        getHomeTimeline(TWEET_PARAM_UNDEFINED, TWEET_PARAM_UNDEFINED, new TwitterResponseHandler() {
                    @Override
                    public void onSuccess(ArrayList<Tweet> tweets) {
                        tweetsLastTweetCount = tweets.size();
                        Log.d("AYTweets.DEBUG", "tweets loaded: " + tweetsLastTweetCount);
                        if (tweets.size() > 0) {
                            tweetsCurrentStartId = tweets.get(0).getTweetId();
                            tweetsCurrentMaxId = tweets.get(tweets.size() - 1).getTweetId();
                        }

                        handler.onSuccess(tweets);
                    }
                    @Override
                    public void onFailure(int statusCode, String errorMessage) {
                        handler.onFailure(statusCode, errorMessage);
                    }
                }
        );
    }

    public boolean getHomeTimelineMore(final TwitterResponseHandler handler) {
        // Note: we could check that we loaded exactly or more of what we had asked, but
        //       in reality twitter gives us less.
        boolean thereIsMoreToCome = (tweetsLastTweetCount == TWEET_PARAM_UNDEFINED || tweetsLastTweetCount > 0);
        if (thereIsMoreToCome) {
            getHomeTimeline(TWEET_PARAM_UNDEFINED, tweetsCurrentMaxId - 1, new TwitterResponseHandler() {
                @Override
                public void onSuccess(ArrayList<Tweet> tweets) {
                    tweetsLastTweetCount = tweets.size();
                    Log.d("AYTweets.DEBUG", "tweets loaded: " + tweetsLastTweetCount);
                    if (tweets.size() > 0) {
                        tweetsCurrentMaxId = tweets.get(tweets.size() - 1).getTweetId();
                    }

                    handler.onSuccess(tweets);
                }
                @Override
                public void onFailure(int statusCode, String errorMessage) {
                    handler.onFailure(statusCode, errorMessage);
                }
            });
        }

        return thereIsMoreToCome;
    }

    public void getHomeTimelineRefresh(final TwitterResponseHandler handler) {
        getHomeTimelineRefreshAfter(TWEET_PARAM_UNDEFINED, handler);
    }

    public void getHomeTimelineRefreshAfter(long tweetId, final TwitterResponseHandler handler) {
        getHomeTimeline(tweetsCurrentStartId, tweetId, new TwitterResponseHandler() {
            @Override
            public void onSuccess(ArrayList<Tweet> tweets) {
                if (tweets.size() > 0) {
                    tweetsCurrentStartId = tweets.get(0).getTweetId();
                }

                handler.onSuccess(tweets);
            }

            @Override
            public void onFailure(int statusCode, String errorMessage) {
                handler.onFailure(statusCode, errorMessage);
            }
        });
    }

    public void getHomeTimeline(long sinceId, long maxId, final TwitterResponseHandler handler) {

		String apiUrl = getApiUrl("statuses/home_timeline.json");

        RequestParams params = new RequestParams();
        if (sinceId != TWEET_PARAM_UNDEFINED) {
            params.put("since_id", sinceId);
        }
        if (maxId != TWEET_PARAM_UNDEFINED) {
            params.put("max_id", maxId);
        }

        params.put("count", PAGE_SIZE);

            try {
                // This guy throws when there is OAuth token
                getClient().get(apiUrl, params, new JsonHttpResponseHandler() {
                    public void onSuccess(int statusCode, Header[] headers, JSONArray jsonArray) {
                        Log.d("AYTweets.DEBUG", "timeline: " + jsonArray.toString());
                        ArrayList<Tweet> tweets = Tweet.fromJson(jsonArray);
                        handler.onSuccess(tweets);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        TwitterClient.this.handleOnFailure(statusCode, handler, errorResponse);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                        TwitterClient.this.handleOnFailure(statusCode, handler, errorResponse);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
	}

    public void postTweet(String body, final TwitterPostResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/update.json");

        RequestParams params = new RequestParams();
        params.put("status", body);
        getClient().post(apiUrl, params, new JsonHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                Log.d("AYTweets.DEBUG", "POST response: " + jsonObject.toString());

                try {
                    long tweetId = jsonObject.getLong("id");
                    handler.onSuccess(tweetId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                TwitterClient.this.handleOnFailure(statusCode, handler, errorResponse);
            }
        });
    }
}