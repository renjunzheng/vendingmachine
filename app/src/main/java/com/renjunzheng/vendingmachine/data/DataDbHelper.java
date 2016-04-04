package com.renjunzheng.vendingmachine.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.renjunzheng.vendingmachine.data.DataContract.UserEntry;
import com.renjunzheng.vendingmachine.data.DataContract.ItemEntry;
import com.renjunzheng.vendingmachine.data.DataContract.PurchasedEntry;

/**
 * Created by XPS on 2016/3/26.
 */
public class DataDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 3;

    static final String DATABASE_NAME = "data.db";

    public DataDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create a table to hold locations.  A location consists of the string supplied in the
        // location setting, the city name, and the latitude and longitude
        final String SQL_CREATE_USER_TABLE = "CREATE TABLE " + UserEntry.TABLE_NAME + " (" +
                UserEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                UserEntry.COLUMN_EMAIL + " TEXT UNIQUE NOT NULL, " +
                //UserEntry.COLUMN_REAL_NAME + " TEXT NOT NULL, " +
                UserEntry.COLUMN_DISPLAY_NAME + " TEXT NOT NULL, " +
                UserEntry.COLUMN_MONEY_LEFT + " INTEGER NOT NULL CHECK (" +
                UserEntry.COLUMN_MONEY_LEFT + " >0)" +
                " );";

        final String SQL_CREATE_ITEM_TABLE = "CREATE TABLE " + ItemEntry.TABLE_NAME + " (" +
                ItemEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                ItemEntry.COLUMN_ITEM_NAME + " TEXT NOT NULL, " +
                ItemEntry.COLUMN_SHORT_DESC + " TEXT NOT NULL, " +
                ItemEntry.COLUMN_PRICE + " TEXT NOT NULL, " +
                ItemEntry.COLUMN_REMAINING_NUM + " INTEGER NOT NULL CHECK (" +
                ItemEntry.COLUMN_REMAINING_NUM + " >0)" +
                " );";

        final String SQL_CREATE_PURCHASED_TABLE = "CREATE TABLE " + PurchasedEntry.TABLE_NAME + " (" +
                PurchasedEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                PurchasedEntry.COLUMN_USER_KEY + " INTEGER NOT NULL, " +
                PurchasedEntry.COLUMN_ITEM_KEY + " INTEGER NOT NULL, " +
                PurchasedEntry.COLUMN_ORDER_TIME + " INTEGER NOT NULL, " +
                PurchasedEntry.COLUMN_PICK_UP_TIME + " INTEGER," +
                PurchasedEntry.COLUMN_RECEIPT_NUM + " INTEGER NOT NULL," +
                PurchasedEntry.COLUMN_QUANTITY + " INTEGER NOT NULL CHECK (" +
                PurchasedEntry.COLUMN_QUANTITY + " >0), " +


                // Set up the user column as a foreign key to user table.
                "FOREIGN KEY (" + PurchasedEntry.COLUMN_USER_KEY + ") REFERENCES " +
                UserEntry.TABLE_NAME + " (" + UserEntry._ID + "), " +

                "FOREIGN KEY (" + PurchasedEntry.COLUMN_ITEM_KEY + ") REFERENCES " +
                ItemEntry.TABLE_NAME + " (" + ItemEntry._ID + "), " +

                // To assure the application have just one user, item, order time combination
                // it's created a UNIQUE constraint with REPLACE strategy
                " UNIQUE (" + PurchasedEntry.COLUMN_USER_KEY + ", " +
                PurchasedEntry.COLUMN_ITEM_KEY + ", " + PurchasedEntry.COLUMN_ORDER_TIME + ") ON CONFLICT REPLACE);";

        sqLiteDatabase.execSQL(SQL_CREATE_USER_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_ITEM_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_PURCHASED_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 3 lines
        // should be your top priority before modifying this method.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + UserEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ItemEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PurchasedEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}