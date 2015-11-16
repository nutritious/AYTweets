package com.codepath.apps.aytweets.models;

import com.activeandroid.Model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ayegorov on 11/8/15.
 */

/*
    "user": {
      "name": "OAuth Dancer",
      "profile_sidebar_fill_color": "DDEEF6",
      "profile_background_tile": true,
      "profile_sidebar_border_color": "C0DEED",
      "profile_image_url": "http://a0.twimg.com/profile_images/730275945/oauth-dancer_normal.jpg",
      "created_at": "Wed Mar 03 19:37:35 +0000 2010",
      "location": "San Francisco, CA",
      "follow_request_sent": false,
      "id_str": "119476949",
      "is_translator": false,
      "profile_link_color": "0084B4",
      "entities": {
        "url": {
          "urls": [
            {
              "expanded_url": null,
              "url": "http://bit.ly/oauth-dancer",
              "indices": [
                0,
                26
              ],
              "display_url": null
            }
          ]
        },
        "description": null
      },
 */
public class User extends Model {

    // Q: Is it a requirement / good idea for some reason to make these private?
    private String name;
    private String userId;
    private String profileImageUrl;
    private String tagline;
    private int followersCount;
    private int friendsCount;

    public String getTagline() {
        return tagline;
    }

    public int getFollowersCount() {
        return followersCount;
    }

    public int getFriendsCount() {
        return friendsCount;
    }

    public String getName() {
        return name;
    }

    public String getUserId() {
        return userId;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public User(JSONObject jsonObject) {

        try {
            userId = jsonObject.getString("id_str");
            name = jsonObject.getString("name");
            profileImageUrl = jsonObject.getString("profile_image_url");
            tagline = jsonObject.getString("description");
            friendsCount = jsonObject.getInt("friends_count");
            followersCount = jsonObject.getInt("followers_count");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
