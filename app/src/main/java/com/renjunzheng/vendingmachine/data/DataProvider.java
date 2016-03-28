package com.renjunzheng.vendingmachine.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * Created by XPS on 2016/3/26.
 */
public class DataProvider extends ContentProvider {

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private DataDbHelper mOpenHelper;

    static final int USER = 1;
    static final int ITEM = 2;
    static final int PURCHASED = 3;

    private static final SQLiteQueryBuilder sPurchasedJoinItemQueryBuilder;

    static{
        sPurchasedJoinItemQueryBuilder = new SQLiteQueryBuilder();

        //This is an inner join which looks like
        //weather INNER JOIN location ON weather.location_id = location._id
        sPurchasedJoinItemQueryBuilder.setTables(
                DataContract.PurchasedEntry.TABLE_NAME + " INNER JOIN " +
                        DataContract.ItemEntry.TABLE_NAME +
                        " ON " + DataContract.PurchasedEntry.TABLE_NAME +
                        "." + DataContract.PurchasedEntry.COLUMN_ITEM_KEY +
                        " = " + DataContract.ItemEntry.TABLE_NAME +
                        "." + DataContract.ItemEntry._ID);
    }
    /*
    //location.location_setting = ?
    private static final String sLocationSettingSelection =
            DataContract.LocationEntry.TABLE_NAME+
                    "." + DataContract.LocationEntry.COLUMN_LOCATION_SETTING + " = ? ";

    //location.location_setting = ? AND date >= ?
    private static final String sLocationSettingWithStartDateSelection =
            DataContract.LocationEntry.TABLE_NAME+
                    "." + DataContract.LocationEntry.COLUMN_LOCATION_SETTING + " = ? AND " +
                    DataContract.UserEntry.COLUMN_DATE + " >= ? ";

    //location.location_setting = ? AND date = ?
    private static final String sLocationSettingAndDaySelection =
            DataContract.LocationEntry.TABLE_NAME +
                    "." + DataContract.LocationEntry.COLUMN_LOCATION_SETTING + " = ? AND " +
                    DataContract.UserEntry.COLUMN_DATE + " = ? ";

    private Cursor getWeatherByLocationSetting(Uri uri, String[] projection, String sortOrder) {
        String locationSetting = DataContract.UserEntry.getLocationSettingFromUri(uri);
        long startDate = DataContract.UserEntry.getStartDateFromUri(uri);

        String[] selectionArgs;
        String selection;

        if (startDate == 0) {
            selection = sLocationSettingSelection;
            selectionArgs = new String[]{locationSetting};
        } else {
            selectionArgs = new String[]{locationSetting, Long.toString(startDate)};
            selection = sLocationSettingWithStartDateSelection;
        }

        return sWeatherByLocationSettingQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getWeatherByLocationSettingAndDate(
            Uri uri, String[] projection, String sortOrder) {
        String locationSetting = DataContract.UserEntry.getLocationSettingFromUri(uri);
        long date = DataContract.UserEntry.getDateFromUri(uri);

        return sWeatherByLocationSettingQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sLocationSettingAndDaySelection,
                new String[]{locationSetting, Long.toString(date)},
                null,
                null,
                sortOrder
        );
    }
    */
    /*
        Students: Here is where you need to create the UriMatcher. This UriMatcher will
        match each URI to the WEATHER, WEATHER_WITH_LOCATION, WEATHER_WITH_LOCATION_AND_DATE,
        and LOCATION integer constants defined above.  You can test this by uncommenting the
        testUriMatcher test within TestUriMatcher.
     */
    static UriMatcher buildUriMatcher() {
        // 1) The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case. Add the constructor below.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = DataContract.CONTENT_AUTHORITY;

        // 2) Use the addURI function to match each of the types.  Use the constants from
        // DataContract to help define the types to the UriMatcher.
        matcher.addURI(authority, DataContract.PATH_USER, USER);
        matcher.addURI(authority, DataContract.PATH_ITEM, ITEM);
        matcher.addURI(authority, DataContract.PATH_PURCHASED, PURCHASED);

        // 3) Return the new matcher!
        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new DataDbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case USER:
                return DataContract.UserEntry.CONTENT_TYPE;
            case ITEM:
                return DataContract.ItemEntry.CONTENT_TYPE;
            case PURCHASED:
                return DataContract.PurchasedEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case USER: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        DataContract.UserEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            case ITEM: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        DataContract.ItemEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            case PURCHASED: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        DataContract.PurchasedEntry.TABLE_NAME,
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

        //register observer on that uri once the data changes be notified
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    /*
        Student: Add the ability to insert Locations to the implementation of this function.
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case USER: {
                long _id = db.insert(DataContract.UserEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = DataContract.UserEntry.buildUserUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case ITEM: {
                long _id = db.insert(DataContract.ItemEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = DataContract.ItemEntry.buildItemUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case PURCHASED: {
                long _id = db.insert(DataContract.PurchasedEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = DataContract.PurchasedEntry.buildPurchasedUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        //notify change to the original uri
        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Start by getting a writable database
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        // Use the uriMatcher to match URIs we are going to
        // handle.  If it doesn't match these, throw an UnsupportedOperationException.
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";
        switch (match) {
            case USER:
                rowsDeleted = db.delete(
                        DataContract.UserEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case ITEM:
                rowsDeleted = db.delete(
                        DataContract.ItemEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PURCHASED:
                rowsDeleted = db.delete(
                        DataContract.PurchasedEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // A null value deletes all rows.  In my implementation of this, I only notified
        // the uri listeners (using the content resolver) if the rowsDeleted != 0 or the selection
        // is null.
        // notify the listeners here.
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        // return the actual rows deleted
        return rowsDeleted;
    }

    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // This is a lot like the delete function.  return the number of rows impacted
        // by the update.
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;
        if ( null == selection ) selection = "1";
        switch (match) {
            case USER:
                rowsUpdated = db.update(
                        DataContract.UserEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case ITEM:
                rowsUpdated = db.update(
                        DataContract.ItemEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case PURCHASED:
                rowsUpdated = db.update(
                        DataContract.PurchasedEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        // return the actual rows deleted
        return rowsUpdated;
    }
    /*
    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case WEATHER:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(DataContract.UserEntry.TABLE_NAME, null, value);
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
    */
    // You do not need to call this method. This is a method specifically to assist the testing
    // framework in running smoothly. You can read more at:
    // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}