package com.uksapps.mockapp;


import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class MockProvider extends ContentProvider {

    public static final String COL_ID = "_id";

    public static final String TABLE_HOTELS = "hotels";
    public static final String COL_NAME = "name";
    public static final String COL_IMAGE = "image";
    public static final String COL_RATINGS = "ratings";
    public static final String COL_LATS = "latitude";
    public static final String COL_LNGS = "longitude";
    public static final String COL_CONTACTS = "contacts";
    public static final String COL_DURATION = "duration";
    public static final String COL_PRICE = "price";
    public static final String COL_ADDRESS = "address";
    public static final String COL_FAVOURITES = "is_favourite";


    public static final Uri CONTENT_URI_HOTELS = Uri.parse("content://com.uksapps.mockapp/hotels");
    public static final Uri CONTENT_URI_FAVOURITES = CONTENT_URI_HOTELS.buildUpon().appendQueryParameter(COL_FAVOURITES,1+"").build();

    private DbHelper dbHelper;

    //Database helper class
    // --------------------------------------------------------------------------//
    private static class DbHelper extends SQLiteOpenHelper {

        private static final String DATABASE_NAME = "mockapp.db";
        private static final int DATABASE_VERSION = 1;

        public DbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String schema = "CREATE TABLE " +TABLE_HOTELS + "("+ COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                            + COL_NAME + " TEXT NOT NULL, " + COL_IMAGE + " TEXT, "+ COL_RATINGS+" REAL, "
                            + COL_LATS + " REAL NOT NULL, "+COL_LNGS + " REAL NOT NULL, "+ COL_CONTACTS +" BIGINT, "+ COL_DURATION +" TEXT, "
                            +COL_PRICE+" INTEGER, "+COL_ADDRESS+" TEXT NOT NULL, "+COL_FAVOURITES+" BIT DEFAULT '0' );";
            System.out.println("Schema "+ schema);
            db.execSQL(schema);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("drop table if exists hotels");
            onCreate(db);
        }
    }

    //--------------------------------------------------------------------------//

    //Back to content provider
    public MockProvider() {
    }

    private static final int HOTELS_ALLROWS = 1;
    private static final int FAVOURITES_ALLROWS = 2;

    private static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI("com.uksapps.mockapp",TABLE_HOTELS,HOTELS_ALLROWS);
        uriMatcher.addURI("com.uksapps.mockapp",TABLE_HOTELS+"/1",FAVOURITES_ALLROWS);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new DbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        Cursor retCursor;
        switch (uriMatcher.match(uri))
        {case HOTELS_ALLROWS:
            qb.setTables(TABLE_HOTELS);
            break;
            case FAVOURITES_ALLROWS:
                qb.setTables(TABLE_HOTELS);
                qb.appendWhere(COL_FAVOURITES + "=" + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        retCursor = qb.query(db,projection,selection,selectionArgs,null,null,sortOrder);
        retCursor.setNotificationUri(getContext().getContentResolver(),uri);
        return retCursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        Uri returnUri; long _id;
        switch (match)
        {
            case HOTELS_ALLROWS:
                _id = db.insert(TABLE_HOTELS,null,values);
                if(_id>0)
                    returnUri = ContentUris.withAppendedId(uri,_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        int rowsDeleted;
        if(selection==null)
            selection="1";
        switch (match)
        {
            case HOTELS_ALLROWS:
                rowsDeleted = db.delete(TABLE_HOTELS,selection,selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        if(rowsDeleted>0)
        getContext().getContentResolver().notifyChange(uri,null);
        return rowsDeleted;

    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        int rowsUpdated;
        switch (match)
        {
            case HOTELS_ALLROWS:
                rowsUpdated = db.update(TABLE_HOTELS,values,selection,selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        if(rowsUpdated>0)
            getContext().getContentResolver().notifyChange(uri,null);
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        switch (match) {
            case HOTELS_ALLROWS:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(TABLE_HOTELS, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Override
    public void shutdown() {
        dbHelper.close();
        super.shutdown();
    }

}
