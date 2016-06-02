package com.uksapps.mockapp;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.squareup.picasso.Picasso;

/**
 * Created by UTSAV on 31-05-2016.
 */
public class FavoriteAdapter extends CursorAdapter {
    public FavoriteAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.favorite_list_item, parent, false);

    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        TextView tvFavTitle = (TextView) view.findViewById(R.id.tvFavTitle);
        TextView tvFavAddress = (TextView) view.findViewById(R.id.tvFavAddress);
        TextView tvFavRating = (TextView) view.findViewById(R.id.tvFavRating);
        TextView tvFavStar = (TextView) view.findViewById(R.id.tvFavStar);
        RatingBar favRatingBar = (RatingBar) view.findViewById(R.id.fvaRatingBar);
        ToggleButton favButton2 = (ToggleButton)view.findViewById(R.id.favButton2);
        favRatingBar.setIsIndicator(true);
        ImageView ivFav = (ImageView)view.findViewById(R.id.ivFav);

        int columnTitle = cursor.getColumnIndex(MockProvider.COL_NAME);
        int columnAddress = cursor.getColumnIndex(MockProvider.COL_ADDRESS);
        int columnRating = cursor.getColumnIndex(MockProvider.COL_RATINGS);
        int columnImage = cursor.getColumnIndex(MockProvider.COL_IMAGE);

        tvFavTitle.setText(cursor.getString(columnTitle));
        tvFavAddress.setText(cursor.getString(columnAddress));
        tvFavRating.setText(cursor.getString(columnRating));
        Picasso.with(context).load(cursor.getString(columnImage)).into(ivFav);

        double stars = 0.0;
        if (cursor.getDouble(columnRating) != 0.0) {
            stars = cursor.getDouble(columnRating);
            favRatingBar.setRating((float) stars);
            tvFavStar.setText((int) stars + " star hotel");
        }
        final String selection = MockProvider.COL_NAME+" = ? ";
        final String[]selectionArgs = new String[]{cursor.getString(columnTitle)};

        favButton2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!buttonView.isChecked())
                    context.getContentResolver().delete(MockProvider.CONTENT_URI_HOTELS,selection,selectionArgs);
            }
        });

    }
}