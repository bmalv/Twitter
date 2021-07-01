package com.codepath.apps.restclienttemplate;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.parceler.Parcels;

public class TwitterDetailsActivity extends AppCompatActivity {

    //the tweet to display
    Tweet tweet;

    //view objects
    ImageView ivATDProfileImage;
    ImageView ivATDEmbeddedImage;
    TextView tvATDBody;
    TextView tvATDScreenName;

    //empty constructor
    public TwitterDetailsActivity() {}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_twitter_details);

        //resolve view objects
        ivATDProfileImage = (ImageView) findViewById(R.id.ivATDProfileImage);
        ivATDEmbeddedImage = (ImageView) findViewById(R.id.ivATDEmbeddedImage);
        tvATDBody = (TextView) findViewById(R.id.tvATDBody);
        tvATDScreenName = (TextView) findViewById(R.id.tvATDScreenName);

        tweet = (Tweet) Parcels.unwrap(getIntent().getParcelableExtra(Tweet.class.getSimpleName()));
        Log.i("TwitterDetailsActivity", "Showing tweet details");

        //set the text views
        tvATDBody.setText(tweet.getTweetBody());
        tvATDScreenName.setText(tweet.getScreenName());

        //upload images
        Glide.with(this)
                .load(tweet.getProfileImageUrl())
                .into(ivATDProfileImage);

        Glide.with(this)
                .load(tweet.getMediaUrlHttps())
                .into(ivATDEmbeddedImage);
    }
}
