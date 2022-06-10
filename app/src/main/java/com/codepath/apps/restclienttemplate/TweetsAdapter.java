package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.TimeFormatter;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import java.util.List;
import java.util.Objects;

import okhttp3.Headers;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder>{
    Context context;
    List<Tweet> tweets;
    public TweetsAdapter(Context context, List<Tweet> tweets){
        this.context = context;
        this.tweets = tweets;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tweet, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    //get the data
        Tweet tweet = tweets.get(position);
        //bind the tweet
        holder.bind(tweet);
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    // Pass in the context and list
    //For each row, inflate the layout
    //Bind values based on the position of the element
    //Define a viewholder
    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView ivProfileImage;
        TextView tvBody;
        TextView tvScreenName;
        ImageView ivMedia;
        TextView tvDate;
        ImageButton ibFav;
        TextView tvFavCount;
        ImageButton ibReply;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvScreenName = itemView.findViewById(R.id.tvScreenName);
            ivMedia = itemView.findViewById(R.id.ivMedia);
            tvDate = itemView.findViewById(R.id.tvDate);
            ibFav = itemView.findViewById(R.id.ibFav);
            tvFavCount = itemView.findViewById(R.id.tvFavCount);
            ibReply = itemView.findViewById(R.id.ibReply);
        }
        public void bind(Tweet tweet){
            tvBody.setText(tweet.body);
            //tvBody.setText(tweet.getFormattedTimeStamp());
            tvScreenName.setText(tweet.user.screenName);
            Glide.with(context).load(tweet.user.profileImageUrl).into(ivProfileImage);
            tvDate.setText(tweet.getFormattedTimeStamp());
            tvFavCount.setText(String.valueOf(tweet.favCount));
            Drawable newPic = context.getDrawable(android.R.drawable.ic_menu_send);
            ibReply.setImageDrawable(newPic);
            if(!Objects.equals(tweet.pic_url, "none")){
                ivMedia.setVisibility(View.VISIBLE);
                Glide.with(context).load(tweet.pic_url).into(ivMedia);
            }else{
                ivMedia.setVisibility(View.GONE);
            }
            if(tweet.isFavorited){
                Drawable newImage = context.getDrawable(android.R.drawable.btn_star_big_on);
                ibFav.setImageDrawable(newImage);
            }else{
                Drawable newImage = context.getDrawable(android.R.drawable.btn_star_big_off);
                ibFav.setImageDrawable(newImage);
            }
            ibFav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //if not already favorited
                    if(!tweet.isFavorited) {
                        //hard: tell Twitter I want to favorite this
                        tweet.isFavorited = true;
                        TwitterApp.getRestClient(context).favoriteTweet(tweet.id, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {
                                Log.i("adapter", "This should've been favorited");
                            }

                            @Override
                            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

                            }
                        });

                        //easy: change the drawable to big on star (yellow star)

                        Drawable newImage = context.getDrawable(android.R.drawable.btn_star_big_on);
                        ibFav.setImageDrawable(newImage);
                        //med: increment the text inside tvFavCount
                        tweet.favCount++;
                        tvFavCount.setText(String.valueOf(tweet.favCount));

                    }
                    //else if already Favorited
                    else{
                        TwitterApp.getRestClient(context).unfavoriteTweet(tweet.id, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {
                                Log.i("adapter", "This should've been unfavorited");
                            }

                            @Override
                            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

                            }
                        });
                        //tell twitter to UNfavortie this
                        //change the drawable back to the off star
                        tweet.isFavorited = false;
                        Drawable newImage = context.getDrawable(android.R.drawable.btn_star_big_off);
                        ibFav.setImageDrawable(newImage);
                        //decrement the text inside tvFavCount
                        tweet.favCount--;
                        tvFavCount.setText(String.valueOf(tweet.favCount));

                    }

                }
            });
        }
    }
    public void clear(){
        tweets.clear();
        notifyDataSetChanged();
    }
    public void addAll(List<Tweet> list){
        tweets.addAll(list);
        notifyDataSetChanged();
    }
}
