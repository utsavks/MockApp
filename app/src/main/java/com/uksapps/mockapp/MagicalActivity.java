package com.uksapps.mockapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;


public class MagicalActivity extends AppCompatActivity implements OnMapReadyCallback, android.location.LocationListener, ActivityCompat.OnRequestPermissionsResultCallback {

    private GoogleMap mMap;
    private String query;
    private LatLng mLatLng;
    private android.widget.SearchView etSearchBar;
    private RecyclerView swipeRView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_magical);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        manipulateActionBar();

        etSearchBar = (SearchView) findViewById(R.id.etSearchBar);
        etSearchBar.setIconifiedByDefault(false);
        query = getIntent().getStringExtra(Intent.EXTRA_TEXT);
        etSearchBar.setQuery(query, true);

        swipeRView =(RecyclerView)findViewById(R.id.my_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        swipeRView.setLayoutManager(layoutManager);
        swipeRView.setItemAnimator(new DefaultItemAnimator());
        swipeRView.hasFixedSize();
    }

    public void manipulateActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setCustomView(R.layout.actionbar);
            actionBar.show();
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_magical, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_favorite) {
            Intent i = new Intent(MagicalActivity.this, FavoriteActivity.class);
            startActivity(i);
        } else {
            Intent i = new Intent(MagicalActivity.this, MainActivity.class);
            startActivity(i);
        }
        return true;
    }

    private static final int REQUEST_CODE_LOCATION = 2;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE_LOCATION);
        }
        mMap.setMyLocationEnabled(true);
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, true);
        Location location = locationManager.getLastKnownLocation(provider);
        if(location!=null)
            onLocationChanged(location);
        locationManager.requestLocationUpdates(provider, 20000, 0, this);
           if(mLatLng==null)
               Log.v("While setting coords","Null values obtained");
        else {
               SearchHotelTask searchTask = new SearchHotelTask(getApplicationContext(), mMap, query, mLatLng,swipeRView);
               System.out.println(mLatLng.latitude + "," + mLatLng.longitude);
               searchTask.execute();
           }
          etSearchBar.setOnQueryTextListener(new android.widget.SearchView.OnQueryTextListener() {
              @Override
              public boolean onQueryTextSubmit(String query) {
                  mMap.clear();
                  query=etSearchBar.getQuery().toString();
                  SearchHotelTask reSearchTask = new SearchHotelTask(getApplicationContext(), mMap, query, mLatLng,swipeRView);
                  reSearchTask.execute();
                  return true;
              }

              @Override
              public boolean onQueryTextChange(String newText) {
                  return false;
              }
          });

//        LatLng bangkok = new LatLng(13.758953,100.4949978);
//        mMap.addMarker(new MarkerOptions().position(bangkok).title("Marker in Bangkok"));

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    @Override
    public void onLocationChanged(Location location) {
      mLatLng = new LatLng(location.getLatitude(),location.getLongitude());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}




