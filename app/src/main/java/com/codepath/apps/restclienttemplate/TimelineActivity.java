package com.codepath.apps.restclienttemplate;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

import static com.codepath.apps.restclienttemplate.R.layout.activity_timeline;

public class TimelineActivity extends AppCompatActivity {

    public static final String TAG = "TimelineActivity";

    // REQUEST_CODE can be any value we like, used to determine the result type later
    private final int REQUEST_CODE = 20;
    private SwipeRefreshLayout swipeContainer;

    TwitterClient client;
    RecyclerView rvTweets;
    List<Tweet> tweets;
    TweetsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       setContentView(activity_timeline);
       //lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        //setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //refresh the list
                //make sure to call swipeContainer.setRefreshing(false)
                //once the network request has completed successfully
                fetchTimelineAsync(0);
            }
        });

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        client = TwitterApp.getRestClient(this);
        //find the recycler view
        rvTweets = findViewById(R.id.rvTweets);
        //intialize the list of tweets and adapter
        tweets = new ArrayList<>();
        adapter = new TweetsAdapter(this, tweets);
        //Recycler view setup: layout manager and the adapter
        rvTweets.setLayoutManager(new LinearLayoutManager(this));
        rvTweets.setAdapter(adapter);
        populateHomeTimeline();
    }

    private void fetchTimelineAsync(int page) {
        //send the network request to fetch the updated data
        //client here is an instance of Android Async HTTP
        // getHomeTimeline is an example endpoint
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                //clear out old items before appending in the new ones
                adapter.clear();
                JSONArray jsonArray = json.jsonArray;
                //the data has come back, add new items to your adapter
                try {
                    //TODO: this is not giving the new tweets gives old tweets
                    adapter.addAll(Tweet.fromJsonArray(jsonArray));
                    adapter.notifyDataSetChanged();
                    Log.d(TAG, "adapter was notified of data change! ");
                } catch (JSONException e) {
                    Log.e(TAG, "Json exception", e);
                }
                //now we call setRefreshing(false) to signal refresh has finished
                swipeContainer.setRefreshing(false);
                Log.i(TAG, "onSuccess refreshed!");
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d("DEBUG", "Fetch timeline error: " + throwable.toString());
            }
        });
    }

    private void populateHomeTimeline() {
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "onSuccess! " + json.toString());
                JSONArray jsonArray = json.jsonArray;
                try {
                    tweets.addAll(Tweet.fromJsonArray(jsonArray));
                    adapter.notifyDataSetChanged();
                    Log.d(TAG, "adapter was notified of data change! ");
                } catch (JSONException e) {
                    Log.e(TAG, "Json exception", e);
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "onFailure! " + response, throwable);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //inflate the menu; this adds items to the action bar if it is present
        getMenuInflater().inflate(R.menu.menu_main, menu);
        //we must return true if we want the menu to be displayed
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.compose){
            //compose icon has been selected
            //navigate to the compose activity
            Intent intent = new Intent(this, ComposeActivity.class);
            //will launch the child activiy will give us back a tweet if the user has submitted a tweet corrrectly
            startActivityForResult(intent, REQUEST_CODE);
            //we want to consume the tap of this menu item here so return true
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        //check if the request code is the same as the request code being passed in
        //and check if the result code is result ok
        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK){
            //get data from the intent (tweet)
            Tweet tweet = Parcels.unwrap(data.getParcelableExtra("tweet"));
            //update the recycler view with this new tweet
            //modify data source of tweets
            tweets.add(0, tweet);
            //update the adapter
            adapter.notifyItemInserted(0);
            rvTweets.smoothScrollToPosition(0);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //this method logs the user out when the logout button is clicked
    public void onLogoutButton(View view){
        Log.i(TAG, "current user logged out");
        //forget the user who's logged in
        client.clearAccessToken();
        //navigate backwards to login screen
        finish();
    }
}