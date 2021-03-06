package com.codepath.apps.restclienttemplate.models;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;
@Parcel
public class Tweet {
    private static final String TAG = "TAG";

    public String body;
    public String createdAt;
    public User user;
    public String pic_url;
    public boolean isFavorited;
    public boolean isRetweeted;
    public int favCount;
    public int rTcount;
    public String id;
    public long idL;

    public Tweet(){}
    public static Tweet fromJson(JSONObject jsonObject) throws JSONException {
        if(jsonObject.has("retweeted_status")){
            return null;
        }

        Tweet tweet = new Tweet();
        tweet.isFavorited = jsonObject.getBoolean("favorited");
        tweet.isRetweeted = jsonObject.getBoolean("retweeted");
        tweet.body = jsonObject.getString("text");
        tweet.createdAt = jsonObject.getString("created_at");
        tweet.user = User.fromJson(jsonObject.getJSONObject("user"));
        tweet.favCount = jsonObject.getInt("favorite_count");
        tweet.rTcount = jsonObject.getInt("retweet_count");
        tweet.id = jsonObject.getString("id_str");
        tweet.idL = jsonObject.getLong("id");
        //here I would keep adding extra features such as retweet, userID, favorited

        if(!jsonObject.getJSONObject("entities").has("media")){
            tweet.pic_url = "none";
        }else{
            tweet.pic_url = jsonObject.getJSONObject("entities").getJSONArray("media").getJSONObject(0).getString("media_url_https");
        }
        return tweet;
    }
    public static List<Tweet> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<Tweet> tweets = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            Tweet newTweet = fromJson(jsonArray.getJSONObject(i));
            if (newTweet != null) {
                tweets.add(newTweet);
            }
        }
        return tweets;
    }
    public String getFormattedTimeStamp() {
        Log.e(TAG, TimeFormatter.getTimeDifference(createdAt));
        return TimeFormatter.getTimeDifference(createdAt);

    }
}
