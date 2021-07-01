package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.jetbrains.annotations.NotNull;
import org.parceler.Parcels;

import java.text.ParseException;
import java.util.List;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder>{

    Context context;
    List<Tweet> tweets;

    public TweetsAdapter(Context context, List<Tweet> tweets){
        this.context = context;
        this.tweets = tweets;
    }

    //pass in the context and list of tweets

    //for each row, inflate a layout
    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tweet, parent, false);
        return new ViewHolder(view);
    }

    //bind values based on the position of the element
    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        //get the data at position
        Tweet tweet = tweets.get(position);
        //bind the tweet with the view holder
        try {
            holder.bind(tweet);
        } catch (ParseException e) {
            Log.i("TweetsAdapter", "failed to bind tweet with view holder");
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    /* Within the RecyclerView.Adapter class */

    // Clean all elements of the recycler
    public void clear() {
        tweets.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Tweet> list) {
        tweets.addAll(list);
        notifyDataSetChanged();
    }

    //define  a view holder
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView ivProfileImage;
        ImageView ivEmbeddedImage;
        TextView tvBody;
        TextView tvScreenName;
        TextView tvCreatedAt;

        public ViewHolder(@NonNull @NotNull View itemView) {
            //the item view passed in is a tweet
            super(itemView);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvScreenName = itemView.findViewById(R.id.tvScreenName);
            tvCreatedAt = itemView.findViewById(R.id.tvCreatedAt);
            ivEmbeddedImage = itemView.findViewById(R.id.ivEmbeddedView);
            itemView.setOnClickListener(this);
        }

        public void bind(Tweet tweet) throws ParseException {
            tvBody.setText(tweet.body);
            tvScreenName.setText(tweet.user.screenName);
            tvCreatedAt.setText(tweet.getRelativeTimeAgo(tweet.createdAt));
            Glide.with(context)
                    .load(tweet.user.profileImageUrl)
                    .into(ivProfileImage);
            if(tweet.getMediaUrlHttps() == null){
                //there is no image
                ivEmbeddedImage.setVisibility(View.GONE);
            }else{
                //there is an image
                Glide.with(context)
                        .load(tweet.getMediaUrlHttps())
                        .into(ivEmbeddedImage);
            }
        }

        public void onClick(View v){
            int pos = getAdapterPosition();

            if(pos != RecyclerView.NO_POSITION) {
                Tweet tweet = tweets.get(pos);
                //create intent for the new activity
                Intent intent = new Intent(context, TwitterDetailsActivity.class);
                //serialize the movie using parceler, use its short name as a key
                intent.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet));
                //show the activity
                context.startActivity(intent);
            }
        }
    }
}
