package com.renjunzheng.vendingmachine.data;

/**
 * Created by XPS on 2016/3/26.
 */
import android.provider.BaseColumns;
import android.text.format.Time;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;

/**
 * Defines table and column names for the data database.
 */
public class DataContract {

    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    public static final String CONTENT_AUTHORITY = "com.renjunzheng.vendingmachine";
    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    // Possible paths (appended to base content URI for possible URI's)
    public static final String PATH_USER = "user";
    public static final String PATH_ITEM = "item";
    public static final String PATH_PURCHASED = "purchased";

    /*
        Inner class that defines the table contents of the user table
     */
    public static final class UserEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_USER).build();

        //this line defines the return type of the cursor, which is DIR means directory, many items
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_USER;

        //this line defines another return type of the cursor, which is a single item
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_USER;

        //id will increment automatically because of implementing BaseColumns
        public static final String TABLE_NAME = "user";

        //public static final String COLUMN_REAL_NAME = "real_name";
        public static final String COLUMN_DISPLAY_NAME = "display_name";
        public static final String COLUMN_EMAIL = "email";
        public static final String COLUMN_MONEY_LEFT = "money_left";

        //at least i think this line returns a uri with provided user id
        public static Uri buildUserUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    /* Inner class that defines the table contents of the item table */
    public static final class ItemEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ITEM).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ITEM;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ITEM;

        public static final String TABLE_NAME = "item";

        public static final String COLUMN_ITEM_NAME = "item_name";
        public static final String COLUMN_REMAINING_NUM = "remaining_num";
        public static final String COLUMN_PRICE = "item_price";

        // Short description and long description of the item.
        // e.g "sweet" vs "contains sugar".
        public static final String COLUMN_SHORT_DESC = "short_desc";


        public static Uri buildItemUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildItemWithId(String id){
            return CONTENT_URI.buildUpon().appendPath(id).build();
        }

        /*
        public static Uri buildWeatherLocation(String locationSetting) {
            return null;
        }
        public static Uri buildWeatherLocationWithStartDate(
                String locationSetting, long startDate) {
            long normalizedDate = normalizeDate(startDate);
            return CONTENT_URI.buildUpon().appendPath(locationSetting)
                    .appendQueryParameter(COLUMN_DATE, Long.toString(normalizedDate)).build();
        }
        public static Uri buildWeatherLocationWithDate(String locationSetting, long date) {
            return CONTENT_URI.buildUpon().appendPath(locationSetting)
                    .appendPath(Long.toString(normalizeDate(date))).build();
        }
        public static String getLocationSettingFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
        public static long getDateFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(2));
        }
        public static long getStartDateFromUri(Uri uri) {
            String dateString = uri.getQueryParameter(COLUMN_DATE);
            if (null != dateString && dateString.length() > 0)
                return Long.parseLong(dateString);
            else
                return 0;
        }*/
    }

    /* Inner class that defines the table contents of the purchased table */
    public static final class PurchasedEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_PURCHASED).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PURCHASED;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PURCHASED;

        public static final String TABLE_NAME = "purchased";

        // Column with the foreign key into the user table.
        public static final String COLUMN_USER_KEY = "user_id";
        // Column with the foreign key into the item table.
        public static final String COLUMN_ITEM_KEY = "item_id";


        // User start the order time, stored as long in milliseconds since the epoch
        public static final String COLUMN_ORDER_TIME = "order_time";
        // User pick up item time, stored as long in milliseconds since the epoch
        public static final String COLUMN_PICK_UP_TIME = "pick_up_time";

        // Quantity of this item sold to this user
        public static final String COLUMN_QUANTITY = "quantity";

        // Quantity of this item sold to this user
        public static final String COLUMN_RECEIPT_NUM = "receipt_num";

        public static Uri buildPurchasedUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}