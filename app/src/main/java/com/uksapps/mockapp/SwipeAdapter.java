package com.uksapps.mockapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;


/**
 * Created by UTSAV on 01-06-2016.
 */
public class SwipeAdapter extends RecyclerView.Adapter<SwipeAdapter.SwipeHolder> {

    private ArrayList<Hotel> foundHotels;
    private Context context;

    public SwipeAdapter(Context context, ArrayList<Hotel> foundHotels) {
        this.context = context;
        this.foundHotels = foundHotels;
    }

    public static class SwipeHolder extends RecyclerView.ViewHolder {

        public ToggleButton favButton;
        public TextView tvSwipeTitle;
        public TextView tvAddressLine;
        public TextView tvSwipeStar;
        public TextView tvSwipeRating;
        public RatingBar swipeRatingBar;
        public ImageView ivSwipe;
        public SwipeHolder(View itemView) {
            super(itemView);
            favButton = (ToggleButton)itemView.findViewById(R.id.favButton);
            tvSwipeTitle = (TextView) itemView.findViewById(R.id.tvSwipeTitle);
            tvAddressLine=(TextView)itemView.findViewById(R.id.tvAddressLine);
            tvSwipeRating=(TextView)itemView.findViewById(R.id.tvSwipeRating);
            tvSwipeStar=(TextView)itemView.findViewById(R.id.tvSwipeStar);
            swipeRatingBar=(RatingBar)itemView.findViewById(R.id.swipeRatingBar);
            ivSwipe = (ImageView)itemView.findViewById(R.id.ivSwipe);
        }
    }

    @Override
    public SwipeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View swipeView = inflater.inflate(R.layout.swipe_item,parent,false);
        SwipeHolder holder = new SwipeHolder(swipeView);
        return holder;
    }

    @Override
    public void onBindViewHolder(final SwipeHolder holder, int position) {
        final Hotel hotel = foundHotels.get(position);
        holder.tvSwipeTitle.setText(hotel.getName());
        holder.tvAddressLine.setText(hotel.getAddress());
        holder.tvSwipeRating.setText(""+hotel.getRatings());
        holder.swipeRatingBar.setRating((float)hotel.getRatings());
        holder.tvSwipeStar.setText((int)hotel.getRatings()+" star hotel");
        Picasso.with(context).load(hotel.getThumbnail()).into(holder.ivSwipe);

        holder.favButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ContentValues hotelValues = new ContentValues();
                hotelValues.put(MockProvider.COL_NAME,hotel.getName());
                hotelValues.put(MockProvider.COL_ADDRESS,hotel.getAddress());
                hotelValues.put(MockProvider.COL_RATINGS,hotel.getRatings());
                hotelValues.put(MockProvider.COL_LATS,hotel.getLatitude());
                hotelValues.put(MockProvider.COL_LNGS,hotel.getLongitude());
                hotelValues.put(MockProvider.COL_IMAGE,hotel.getThumbnail());

                final String selection = MockProvider.COL_NAME+" = ? ";
                final String[]selectionArgs = new String[]{hotel.getName()};
                if (holder.favButton.isChecked()){
                     Cursor c = context.getContentResolver().query(MockProvider.CONTENT_URI_HOTELS,null,selection,selectionArgs,null);
                    if(!c.moveToFirst())
                    context.getContentResolver().insert(MockProvider.CONTENT_URI_HOTELS,hotelValues);
                    c.close();
            }
                else
                {
                    context.getContentResolver().delete(MockProvider.CONTENT_URI_HOTELS,selection,selectionArgs);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        if(foundHotels!=null)
        return foundHotels.size();
        return 0;
    }

}
