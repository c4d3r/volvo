package se.hh.volvo.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import java.util.Arrays;

/**
 * Created by Maxim on 20/03/2015.
 */
public class RoadProvider extends ContentProvider
{
    private static final String TAG = RoadProvider.class.getSimpleName();

    private static final int ROADS = 100;
    private static final int ROAD_ID = 101;

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = RoadContract.CONTENT_AUTHORITY;

        // voor elk type URI dat we willen toevoegen creeeren we een code
        matcher.addURI(authority, RoadContract.PATH_ROAD, ROADS);
        matcher.addURI(authority, RoadContract.PATH_ROAD + "/#", ROAD_ID);

        return matcher;
    }

    private static final UriMatcher sUriMatcher = buildUriMatcher();


    //public static Uri CONTENT_URI = Uri.parse("content://com.c4d3r.reddit/topics");
    private EcologDbHelper mDbHelper;
    private SQLiteDatabase _db;

    @Override
    public boolean onCreate() {
        mDbHelper = new EcologDbHelper(getContext());
        return true; //contentprovider succesvol aangemaakt
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        Cursor retCursor;
        switch(sUriMatcher.match(uri))
        {
            // "topic/*"
            case ROAD_ID:
            {
                retCursor = mDbHelper.getReadableDatabase().query(
                        RoadContract.RoadEntry.TABLE_NAME,
                        projection,
                        RoadContract.RoadEntry._ID + " = '" + ContentUris.parseId(uri) + "'",
                        null,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            // "topic"
            case ROADS:
            {
                retCursor = mDbHelper.getReadableDatabase().query(
                        RoadContract.RoadEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    /**
     * geeft het MIME type terug dat geassocieerd is met de data bij een URI
     * @param uri
     * @return
     */
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ROAD_ID:
                return RoadContract.RoadEntry.CONTENT_TOPIC_TYPE;
            case ROADS:
                return RoadContract.RoadEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values)
    {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case ROADS: {
                long _id = db.insert(RoadContract.RoadEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = RoadContract.RoadEntry.buildTopicUri(_id);
                else
                    throw new SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted = 0;

        switch (match) {
            case ROADS: {
                rowsDeleted = db.delete(RoadContract.RoadEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        //Because a null deletes all rows
        if(null == selection || 0 != rowsDeleted) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case ROADS: {
                rowsUpdated = db.update(RoadContract.RoadEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if(rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values)
    {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ROADS:
                db.beginTransaction();
                int returnCount = 0;
                try
                {
                    Log.d(TAG, Arrays.deepToString(values));
                    for(ContentValues value : values) {

                        long _id = db.insert(RoadContract.RoadEntry.TABLE_NAME, null, value);
                        if(-1 != _id) {
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
}
