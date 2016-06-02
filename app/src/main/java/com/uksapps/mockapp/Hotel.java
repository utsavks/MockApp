package com.uksapps.mockapp;

/**
 * Created by UTSAV on 31-05-2016.
 */
public class Hotel {
    String name,address;
    double latitude,longitude,ratings;
    String thumbnail;
    public Hotel(String title,String addressLine, double lats, double lngs, double stars,String thumbnail) {
        this.name=title;
        this.address=addressLine;
        this.latitude=lats;
        this.longitude=lngs;
        this.ratings=stars;
        this.thumbnail=thumbnail;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getRatings() {
        return ratings;
    }

    public void setRatings(double ratings) {
        this.ratings = ratings;
    }
}
