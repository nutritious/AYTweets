package com.codepath.apps.aytweets.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.aytweets.R;
import com.codepath.apps.aytweets.fragments.TimelineFragment;
import com.codepath.apps.aytweets.models.User;
import com.codepath.apps.aytweets.network.TimelineType;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {

    ImageView userImageView;
    TextView nameTextView;
    TextView taglineTextView;
    TextView followersTextView;
    TextView followingTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Get the views
        userImageView = (ImageView)findViewById(R.id.profileUserImageView);
        nameTextView = (TextView)findViewById(R.id.profileNameTextView);
        taglineTextView = (TextView)findViewById(R.id.profileTaglineTextView);
        followersTextView = (TextView)findViewById(R.id.profileFollowersTextView);
        followingTextView = (TextView)findViewById(R.id.profileFollowingTextView);

        // Get the data
        Intent intent = getIntent();
        User user = (User) intent.getSerializableExtra("user");

        // Fill the data in
        Picasso.with(this).load(user.getOriginalProfileImageUrl()).into(userImageView, new Callback() {
            @Override
            public void onSuccess() {
                userImageView.setBackgroundColor(Color.TRANSPARENT);
            }

            @Override
            public void onError() {

            }
        });

        nameTextView.setText(user.getName());
        taglineTextView.setText(user.getTagline());
        followersTextView.setText(constructFollowersString(user.getFollowersCount()));
        followingTextView.setText(constructFriendsString(user.getFriendsCount()));

        setTitle("@" + user.getTwitterTag());

        // Fetch user's timeline
        if (savedInstanceState == null) {
            Bundle extraParams = new Bundle();
            extraParams.putString("user_id", user.getUserId());

            TimelineFragment userTimelineFragment = (TimelineFragment) TimelineFragment.newInstance(TimelineType.User, extraParams);
            View userTimelinePlaceholder = findViewById(R.id.userTimelinePlaceholderLayout);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.userTimelinePlaceholderLayout, userTimelineFragment);
            transaction.commit();
        }
    }

    private String constructFriendsString(int friendsCount) {
        if (friendsCount == 0) {
            return "No friends";
        } else {
            return friendsCount + " Following";
        }
    }

    String constructFollowersString(int followers) {
        if (followers == 0) {
            return "No followers ;-/";
        } else if (followers == 1) {
            return "1 Follower";
        } else {
            return followers + " Followers";
        }
    }
}
