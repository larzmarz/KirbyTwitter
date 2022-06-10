package com.codepath.apps.restclienttemplate;

import android.content.Context;
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

import java.util.List;
import java.util.Objects;

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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvScreenName = itemView.findViewById(R.id.tvScreenName);
            ivMedia = itemView.findViewById(R.id.ivMedia);
            tvDate = itemView.findViewById(R.id.tvDate);
            ibFav = itemView.findViewById(R.id.ibFav);
            tvFavCount = itemView.findViewById(R.id.tvFavCount);
        }
        public void bind(Tweet tweet){
            tvBody.setText(tweet.body);
            //tvBody.setText(tweet.getFormattedTimeStamp());
            tvScreenName.setText(tweet.user.screenName);
            Glide.with(context).load(tweet.user.profileImageUrl).into(ivProfileImage);
            tvDate.setText(tweet.getFormattedTimeStamp());

            if(!Objects.equals(tweet.pic_url, "none")){
                ivMedia.setVisibility(View.VISIBLE);
                Glide.with(context).load(tweet.pic_url).into(ivMedia);
            }else{
                ivMedia.setVisibility(View.GONE);
            }
            ibFav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //if not already favorited
                        // tell Twitter I want to favorite this
                        //change the drawable to big on star (yellow star)
                        //change the text inside tvFavoriteCount
                        //increment the text inside tvFavCount
                    //else if already Favorited
                        //tell twitter to UNfavortie this
                        //change the drawable back to the off star
                        //decrement the text inside tvFavCount
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
