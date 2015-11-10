package com.codepath.apps.aytweets.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.codepath.apps.aytweets.R;

public class ComposeTweetActivity extends AppCompatActivity {

    static final int TWEET_MAX_TEXT_COUNT = 140;

    EditText tweetEditText;
    Button tweetButton;
    TextView tweetCharCountTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose_tweet);

        tweetEditText = (EditText)findViewById(R.id.tweetEditText);
        tweetButton = (Button)findViewById(R.id.tweetButton);
        tweetCharCountTextView = (TextView)findViewById(R.id.tweetCharCountTextView);

        tweetCharCountTextView.setText(String.valueOf(TWEET_MAX_TEXT_COUNT));


        tweetEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                int charCountdown = TWEET_MAX_TEXT_COUNT - tweetEditText.getText().toString().length();

                if (charCountdown < 0) {
                    tweetCharCountTextView.setTextColor(Color.RED);
                } else if (charCountdown == 0) {
                    tweetCharCountTextView.setTextColor(Color.YELLOW);
                } else {
                    tweetCharCountTextView.setTextColor(Color.BLACK);
                }

                tweetCharCountTextView.setText(String.valueOf(charCountdown));

                return false;
            }
        });
    }

    public void onTweet(View view) {
        Intent data = new Intent();
        data.putExtra("tweetBody", tweetEditText.getText().toString());
        this.setResult(RESULT_OK, data);
        this.finish();
    }
}
