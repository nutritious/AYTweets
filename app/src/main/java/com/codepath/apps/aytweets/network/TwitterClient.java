package com.codepath.apps.aytweets.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;

import com.codepath.apps.aytweets.models.Tweet;
import com.codepath.apps.aytweets.models.User;
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
import java.util.HashMap;

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

    private static final long TWEET_PARAM_UNDEFINED = -1;
    public static final int PAGE_SIZE = 25;

    private class PaginationContext {
        public long tweetsLastTweetCount;
        public long tweetsCurrentMaxId;
        public long tweetsCurrentStartId;

        public PaginationContext() {
            reset();
        }

        public void reset() {
            tweetsLastTweetCount = TWEET_PARAM_UNDEFINED;
            tweetsCurrentMaxId = TWEET_PARAM_UNDEFINED;
            tweetsCurrentStartId = TWEET_PARAM_UNDEFINED;
        }

    }

    public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class; // Change this
    public static final String REST_URL = "https://api.twitter.com/1.1"; // Change this, base API URL

    // Production API keys for @yegorich
//    public static final String REST_CONSUMER_KEY = "GI1gRzUDeRutEm0CeNqpg09n4";
//    public static final String REST_CONSUMER_SECRET = "ujgmS5NN0AjSQJ1HlFce5UTlxXnHeXDcjE0qQmcHBTPIWL6WXc";

    // Staging API keys for @grouponicus
    public static final String REST_CONSUMER_KEY = "M0J2XeC9aEwlcBRiJOJsKsjpe";
    public static final String REST_CONSUMER_SECRET = "KFl7ZFPlROku9RZ8ZY5WJmu07TLQDtvwbLuq7KprOLaX3j7wtc";
//
    // Q: How do I know what to put after oauth:// ?
    public static final String REST_CALLBACK_URL = "oauth://cpaytweets"; // Change this (here and in manifest)

    private HashMap<TimelineType, PaginationContext> paginationContexts;

    public TwitterClient(Context context) {
        super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
        paginationContexts = new HashMap<>();
        paginationContexts.put(TimelineType.Home, new PaginationContext());
        paginationContexts.put(TimelineType.Mentions, new PaginationContext());
        paginationContexts.put(TimelineType.User, new PaginationContext());
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

    private String getTimelineApiUrlForType(TimelineType timelineType) {
        switch (timelineType) {
            case Home:
                return "statuses/home_timeline.json";
            case Mentions:
                return "statuses/mentions_timeline.json";
            case User:
                return "statuses/user_timeline.json";
            default:
                assert (false); // Shouldn't come here
        }

        return "<undefined>";
    }

    //
    // Interface
    //

    // -- Timeline family of methods

    public void getTimelineInitial(TimelineType timelineType, Bundle extraParams, final TwitterTimelineResponseHandler handler) {

        final PaginationContext paginationContext = paginationContexts.get(timelineType);
        paginationContext.reset();

        getTimeline(timelineType, TWEET_PARAM_UNDEFINED, TWEET_PARAM_UNDEFINED, extraParams, new TwitterTimelineResponseHandler() {
                    @Override
                    public void onSuccess(ArrayList<Tweet> tweets) {
                        paginationContext.tweetsLastTweetCount = tweets.size();
                        Log.d("AYTweets.DEBUG", "tweets loaded: " + paginationContext.tweetsLastTweetCount);
                        if (tweets.size() > 0) {
                            paginationContext.tweetsCurrentStartId = tweets.get(0).getTweetId();
                            paginationContext.tweetsCurrentMaxId = tweets.get(tweets.size() - 1).getTweetId();
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

    public boolean getTimelineMore(TimelineType timelineType, Bundle extraParams, final TwitterTimelineResponseHandler handler) {
        // Note: we could check that we loaded exactly or more of what we had asked, but
        //       in reality twitter gives us less.
        final PaginationContext paginationContext = paginationContexts.get(timelineType);

        boolean thereIsMoreToCome = (paginationContext.tweetsLastTweetCount == TWEET_PARAM_UNDEFINED || paginationContext.tweetsLastTweetCount > 0);
        if (thereIsMoreToCome) {
            getTimeline(timelineType, TWEET_PARAM_UNDEFINED, paginationContext.tweetsCurrentMaxId - 1, extraParams, new TwitterTimelineResponseHandler() {
                @Override
                public void onSuccess(ArrayList<Tweet> tweets) {
                    paginationContext.tweetsLastTweetCount = tweets.size();
                    Log.d("AYTweets.DEBUG", "tweets loaded: " + paginationContext.tweetsLastTweetCount);
                    if (tweets.size() > 0) {
                        paginationContext.tweetsCurrentMaxId = tweets.get(tweets.size() - 1).getTweetId();
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

    public void getTimelineRefresh(TimelineType timelineType, Bundle extraParams, final TwitterTimelineResponseHandler handler) {
        // TODO: this one doesn't handle a 'DELETE' case
        getTimelineRefreshAfter(timelineType, TWEET_PARAM_UNDEFINED, extraParams, handler);
    }

    public void getTimelineRefreshAfter(TimelineType timelineType, long tweetId, Bundle extraParams, final TwitterTimelineResponseHandler handler) {

        final PaginationContext paginationContext = paginationContexts.get(timelineType);

        getTimeline(timelineType, paginationContext.tweetsCurrentStartId, tweetId, extraParams, new TwitterTimelineResponseHandler() {
            @Override
            public void onSuccess(ArrayList<Tweet> tweets) {
                if (tweets.size() > 0) {
                    paginationContext.tweetsCurrentStartId = tweets.get(0).getTweetId();
                }

                handler.onSuccess(tweets);
            }

            @Override
            public void onFailure(int statusCode, String errorMessage) {
                handler.onFailure(statusCode, errorMessage);
            }
        });
    }

    public void getTimeline(TimelineType timelineType, long sinceId, long maxId, Bundle extraParams, final TwitterTimelineResponseHandler handler) {

        String apiUrl = getApiUrl(getTimelineApiUrlForType(timelineType));

        RequestParams params = new RequestParams();
        if (sinceId != TWEET_PARAM_UNDEFINED) {
            params.put("since_id", sinceId);
        }
        if (maxId != TWEET_PARAM_UNDEFINED) {
            params.put("max_id", maxId);
        }

        params.put("count", PAGE_SIZE);

        // Put any extra parameters if needed
        if (extraParams != null) {
            for (String key : extraParams.keySet()) {
                Object value = extraParams.get(key);
                params.put(key, value);
            }
        }

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

    // -- Post a tweet
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

    // -- User profile

    public void getUserProfile(final TwitterAccountResponseHandler handler) {

        String apiUrl = getApiUrl("account/verify_credentials.json");

        RequestParams params = new RequestParams();
        getClient().get(apiUrl, params, new JsonHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                Log.d("AYTweets.DEBUG", "GET account response: " + jsonObject.toString());
                User user = new User(jsonObject);

                if (user != null) {
                    handler.onSuccess(user);
                } else {
                    handler.onFailure(-1, "couldn't create user object from JSON");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                TwitterClient.this.handleOnFailure(statusCode, handler, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                TwitterClient.this.handleOnFailure(statusCode, handler, responseString);
            }
        });
    }
}