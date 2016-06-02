package com.uksapps.mockapp;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

/**
 * Created by UTSAV on 29-05-2016.
 */
public class CitiesDataAccess {
    private SQLiteOpenHelper openHelper;
    private SQLiteDatabase database;
    private static CitiesDataAccess instance;

    public CitiesDataAccess(Context context) {
        this.openHelper = new DatabaseOpenHelper(context);
    }

    //Inner Class
    public static class DatabaseOpenHelper extends SQLiteAssetHelper {
        private static final String DATABASE_NAME = "major_city.db";
        private static final int DATABASE_VERSION = 1;

        public DatabaseOpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

    }

    //Back to CitiesDataAccess

    public static CitiesDataAccess getInstance(Context context) {
        if (instance == null) {
            instance = new CitiesDataAccess(context);
        }
        return instance;
    }

    public void open() {
        this.database = openHelper.getWritableDatabase();
    }

    public void close() {
        if (database != null) {
            this.database.close();
        }
    }

    public Cursor getCityInfo() {

        Cursor cursor = database.query("location",new String[]{"city","latitude","longitude"},null,null,null,null,"id"+" desc");
        return cursor;
    }

}
