package com.uksapps.mockapp;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by UTSAV on 29-05-2016.
 */
public class SearchHotelTask extends AsyncTask<Void, LatLng[], List<Hotel>> {

    private Context myCtx;
    private GoogleMap mMap;
    private String mQuery;
    private LatLng asyncLatLng;
    private String TAG = getClass().getSimpleName();
    private RecyclerView recyclerView;

    public SearchHotelTask(Context ctx, GoogleMap googleMap, String query, LatLng latLng, RecyclerView rView) {
        this.myCtx = ctx;
        this.mMap = googleMap;
        this.mQuery = query;
        this.asyncLatLng = latLng;
        this.recyclerView = rView;
    }

    public List<Hotel> getPlacesDataFromJSON(String placesJsonStr) throws JSONException {
        JSONObject placesJSON = new JSONObject(placesJsonStr);
        JSONArray resultsJSON = placesJSON.getJSONArray("results");
        List<Hotel> foundHotels = new ArrayList<>();

        if (resultsJSON.length() != 0)
            for (int i = 0; i < resultsJSON.length(); i++) {
                double latude;
                double lngtude;
                String title;
                String address;
                String photoReference = "";
                String photoUrl;
                int maxWidth = 100;
                double rating = 0.0;

                JSONObject resultsItemJSON = resultsJSON.getJSONObject(i);
                JSONObject geometryJSON = resultsItemJSON.getJSONObject("geometry");
                JSONObject locationJSON = geometryJSON.getJSONObject("location");
                latude = locationJSON.getDouble("lat");
                lngtude = locationJSON.getDouble("lng");
                title = resultsItemJSON.getString("name");


                if (resultsItemJSON.has("rating"))
                    rating = resultsItemJSON.getDouble("rating");

                address = resultsItemJSON.getString("vicinity");
                JSONArray photoArray = new JSONArray();
                if(resultsItemJSON.has("photos"))
                 photoArray = resultsItemJSON.getJSONArray("photos");

                if(photoArray!=null&&photoArray.length()!=0)
                {if(photoArray.getJSONObject(0).has("photo_reference"))
                 photoReference = photoArray.getJSONObject(0).getString("photo_reference");
                if(photoArray.getJSONObject(0).has("width"))
                    maxWidth = photoArray.getJSONObject(0).getInt("width");
                }
                Uri photoUri = Uri.parse("https://maps.googleapis.com/maps/api/place/photo?").buildUpon()
                               .appendQueryParameter("maxwidth",maxWidth+"")
                               .appendQueryParameter("photoreference",photoReference)
                        .appendQueryParameter("sensor", "false")
                        .appendQueryParameter("key", "AIzaSyDJtUdv3CMLc40sKCFyoYaT2xwOpCjLIL0").build();
                photoUrl = photoUri.toString();
                Hotel hotel = new Hotel(title, address, latude, lngtude, rating, photoUrl);
                foundHotels.add(hotel);

            }
        return foundHotels;
    }

    @Override
    protected void onProgressUpdate(LatLng[]... values) {
        super.onProgressUpdate(values);

    }

    @Override
    protected List<Hotel> doInBackground(Void... params) {

        CitiesDataAccess dataAccess = new CitiesDataAccess(myCtx);
        dataAccess.open();
        Cursor cursor = dataAccess.getCityInfo();
        cursor.moveToFirst();
        double Latitude = 0.0;
        double Longitude = 0.0;

        while (!cursor.isAfterLast()) {
            String city = cursor.getString(0).toLowerCase();
            if (mQuery == null || mQuery.length() == 0) {
                return null; }
            else
            {mQuery.toLowerCase(); String[] queries = mQuery.split(" ");
                for (String query:queries
                     ) {
                    if (city.contains(query)&&query.length()>3) {
                        Latitude = cursor.getDouble(1);
                        Longitude = cursor.getDouble(2);
                    }
                }

            }
            cursor.moveToNext();
        }
        LatLng latLng = new LatLng(Latitude, Longitude);
        if (latLng.latitude == 0.0 && latLng.longitude == 0.0)
            latLng = asyncLatLng;
        cursor.close();

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String placesJsonStr;

        try {
            final Uri PLACES_SEARCH_URL = Uri.parse("https://maps.googleapis.com/maps/api/place/search/json?").
                    buildUpon()
                    .appendQueryParameter("location", latLng.latitude + "," + latLng.longitude)
                    .appendQueryParameter("radius", "5000")
                    .appendQueryParameter("type", "lodging")
                    .appendQueryParameter("sensor", "false")
                    .appendQueryParameter("key", "AIzaSyDJtUdv3CMLc40sKCFyoYaT2xwOpCjLIL0").build();
            URL url = new URL(PLACES_SEARCH_URL.toString());
            Log.v(TAG, "" + url);

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                return null;
            }
            placesJsonStr = buffer.toString();
            Log.v(TAG, "" + placesJsonStr);
            return getPlacesDataFromJSON(placesJsonStr);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
            e.printStackTrace();
        } catch (NullPointerException n){
            Log.e(TAG,n.getMessage(),n);
        }finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(TAG, "Error closing stream", e);
                }
            }
        }
        return null;
    }


private Marker currentMarker;
    @Override
    protected void onPostExecute(final List<Hotel> hotelResults) {
        if (hotelResults != null) {
            recyclerView.setAdapter(new SwipeAdapter(myCtx,(ArrayList<Hotel>) hotelResults));

            {
                for (final Hotel hotel : hotelResults)
                    if (hotel.getLatitude() != 0.0 && hotel.getLongitude() != 0.0) {
                        final LatLng latLng = new LatLng(hotel.getLatitude(), hotel.getLongitude());
                        mMap.addMarker(new MarkerOptions().position(latLng).title(hotel.getName())
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(14));

                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        if (currentMarker != null)
                            currentMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                        currentMarker = marker;
                        currentMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                       return false;
                    }
                });
//                        if(currentMarker!=null)
//                        if(currentMarker.getPosition()==latLng)
//                            recyclerView.scrollToPosition(hotelResults.indexOf(hotel));

                    }
            }

        }
            if(hotelResults != null&&hotelResults.size()==0)
            Toast.makeText(myCtx,R.string.no_internet_warn,Toast.LENGTH_SHORT).show();
    }
}