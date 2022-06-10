package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.loader.content.AsyncTaskLoader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.AsyncTask;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nullable;

import okhttp3.Headers;

public class TimelineActivity extends AppCompatActivity {

    public static final String TAG = "TimelineActivity";
    private final int REQUEST_CODE = 20;
    private EndlessRecyclerViewScrollListener rvScrollListener;
    TwitterClient client;
    RecyclerView rvTweets;
    List<Tweet> tweets;
    TweetsAdapter adapter;
    Button logoutButton;
    SwipeRefreshLayout swipeContainer;
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        client =  TwitterApp.getRestClient(this);

        //find rv
        rvTweets = findViewById(R.id.rvTweets);
        tweets = new ArrayList<>();
        adapter = new TweetsAdapter(this, tweets);
        LinearLayoutManager lin = new LinearLayoutManager(this);
        logoutButton = findViewById(R.id.Logout);
        //initialize list of tweets and adapter
        //recycler view setup
        rvTweets.setLayoutManager(new LinearLayoutManager(this));
        rvTweets.setAdapter(adapter);
        populateHomeTimeline();
        rvScrollListener = new EndlessRecyclerViewScrollListener(lin) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list

            }
        };

        logoutButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                onLogoutButton();
                client.clearAccessToken();

            }
        });

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                fetchTimelineAsync(0);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


        // Find the toolbar view inside the activity layout
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);
    }


    public void fetchTimelineAsync(int page){
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
               try {
                   adapter.clear();
                   adapter.addAll(Tweet.fromJsonArray(json.jsonArray));
                   swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright);
                   swipeContainer.setRefreshing(false);
               } catch (JSONException e) {
                   e.printStackTrace();
               }

            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable e) {
                Log.d("DEBUG", "Fetch timeline error: " + e.toString());
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == R.id.compose){
            Toast.makeText(this, "Compose!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, ComposeActivity.class);
            startActivityForResult(intent, REQUEST_CODE);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK){
                //Get data from intent (tweet)
            Tweet tweet = Parcels.unwrap(data.getParcelableExtra("tweet"));
                //Update the RV with the tweet
            tweets.add(0, tweet);
            adapter.notifyItemInserted(0);
            rvTweets.smoothScrollToPosition(0);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void populateHomeTimeline(){
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "onSuccess!" + json.toString());
                JSONArray jsonArray = json.jsonArray;
                try {
                    //List<Tweet> tweets = Tweet.fromJsonArray(jsonArray);
                    tweets.addAll(Tweet.fromJsonArray(jsonArray));
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    Log.e(TAG, "Json exception", e);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.i(TAG, "onFailure!" + response, throwable);
            }
        });
    }
    //this should be able to fetch the timeline if I lose wifi
    public void FetchFromDb(){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {

            }
        });
    }
    public String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        try {
            long time = sf.parse(rawJsonDate).getTime();
            long now = System.currentTimeMillis();

            final long diff = now - time;
            if (diff < MINUTE_MILLIS) {
                return "just now";
            } else if (diff < 2 * MINUTE_MILLIS) {
                return "a minute ago";
            } else if (diff < 50 * MINUTE_MILLIS) {
                return diff / MINUTE_MILLIS + " m";
            } else if (diff < 90 * MINUTE_MILLIS) {
                return "an hour ago";
            } else if (diff < 24 * HOUR_MILLIS) {
                return diff / HOUR_MILLIS + " h";
            } else if (diff < 48 * HOUR_MILLIS) {
                return "yesterday";
            } else {
                return diff / DAY_MILLIS + " d";
            }
        } catch (ParseException e) {
            Log.i(TAG, "getRelativeTimeAgo failed");
            e.printStackTrace();
        }

        return "";
    }
    void onLogoutButton(){
        TwitterApp.getRestClient(this).clearAccessToken();
        Intent i = new Intent(this, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }
}