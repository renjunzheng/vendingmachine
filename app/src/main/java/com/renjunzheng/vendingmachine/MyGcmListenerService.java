
/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.renjunzheng.vendingmachine;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.renjunzheng.vendingmachine.data.DataContract;
import com.renjunzheng.vendingmachine.data.DataDbHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MyGcmListenerService extends GcmListenerService {

    private static final String TAG = "MyGcmListenerService";

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {

        String action = data.getString("action");
        Log.d(TAG, "action: " + data.getString("action"));



        if (from.startsWith("/topics/")) {
            // message received from some topic.
        } else {
            // normal downstream message.
        }

        // [START_EXCLUDE]
        /**
         * Production applications would usually process the message here.
         * Eg: - Syncing with server.
         *     - Store message in local database.
         *     - Update UI.
         */

        /**
         * In some cases it may be useful to show a notification indicating to the user
         * that a message was received.
         */


        if(action.equals("NOTIFICATION")) {
            String message = data.getString("message");
            sendNotification(message);
        }else if(action.equals("CONFIRM_REGISTER")){

        }else if(action.equals("CONFIRM_LOG_IN")){
            String login_status_code = data.getString("status_code");
            loginFeedback(login_status_code, data);
        }else if(action.equals("UPDATE_STORAGE_INFO")){
            String updated_info = data.getString("updated_info");
            updateStorageInfo(updated_info);
        }else if(action.equals("UPDATE_PURCHASE_INFO")){
            String updated_info = data.getString("updated_info");
            updatePurchaseInfo(updated_info);
        }else if(action.equals("CONFIRM_PURCHASE")){
            confirmPurchase(Integer.parseInt(data.getString("purchase_status")));
        }
        // [END_EXCLUDE]
    }
    // [END receive_message]

    private void confirmPurchase(int purchase_status){
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
        editor.putBoolean("purchase_verified", true);
        editor.putInt("purchase_status", purchase_status);
        editor.apply();
    }

    private void updatePurchaseInfo(String purchaseInfo){
        try {
            DataDbHelper dbHelper = new DataDbHelper(this);
            SQLiteDatabase database = dbHelper.getWritableDatabase();

            database.delete(DataContract.PurchasedEntry.TABLE_NAME,null,null);
            database.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + DataContract.PurchasedEntry.TABLE_NAME + "'");

            JSONArray valueArray = new JSONArray(purchaseInfo);
            Log.i(TAG, "the valueArray length: " + Integer.toString(valueArray.length()));
            for(int lc = 0; lc < valueArray.length(); ++ lc){
                JSONObject infoJson = valueArray.getJSONObject(lc);
                String item_name = infoJson.getString("item_name");
                database.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + DataContract.PurchasedEntry.TABLE_NAME + "'");

                //query based on this item_name and get item id
                //currently if queried has no such item in current database then don't insert to purchase table
                //find user id using the stored email
                String[] itemProj = new String[]{DataContract.ItemEntry._ID};
                String[] itemSelArgs = new String[]{item_name};
                Cursor itemIDCursor = database.query(DataContract.ItemEntry.TABLE_NAME,itemProj, DataContract.ItemEntry.COLUMN_ITEM_NAME + " = ?", itemSelArgs,
                        null, null, null);
                itemIDCursor.moveToNext();
                int itemID = itemIDCursor.getInt(0);
                String[] userProj = new String[]{DataContract.UserEntry._ID};
                String user_email = infoJson.getString("email");
                String[] userSelArgs = new String[]{user_email};
                Cursor userIDCursor = database.query(DataContract.UserEntry.TABLE_NAME,userProj, DataContract.UserEntry.COLUMN_EMAIL + " = ?", userSelArgs,
                        null, null, null);
                userIDCursor.moveToNext();
                Log.i(TAG, "userID: "+user_email);
                int userID = userIDCursor.getInt(0);
                Log.i(TAG, "itemID: "+itemID);
                Log.i(TAG, "userID: "+userID);

                ContentValues newValues = new ContentValues();
                newValues.put(DataContract.PurchasedEntry.COLUMN_ITEM_KEY, itemID);
                newValues.put(DataContract.PurchasedEntry.COLUMN_USER_KEY, userID);
                newValues.put(DataContract.PurchasedEntry.COLUMN_ORDER_TIME, infoJson.getString("order_time"));
                newValues.put(DataContract.PurchasedEntry.COLUMN_PICK_UP_TIME, infoJson.getString("pickup_time"));
                newValues.put(DataContract.PurchasedEntry.COLUMN_QUANTITY, infoJson.getString("quantity"));
                newValues.put(DataContract.PurchasedEntry.COLUMN_RECEIPT_NUM, infoJson.getString("receipt"));
                Uri returnedUri = getContentResolver().insert(DataContract.PurchasedEntry.CONTENT_URI,
                        newValues);
                Log.i(TAG,"inserted row num " + ContentUris.parseId(returnedUri));
            }

            database.close();
            dbHelper.close();
        }catch (JSONException e){
            Log.e(TAG, "error when parsing Json");
        }
    }

    private void loginFeedback(String login_status_code, Bundle data){
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
        editor.putBoolean("login_checked", true);
        int returnCode = Integer.parseInt(login_status_code);
        editor.putInt("login_status_code", returnCode);
        editor.apply();

        if(returnCode == 200) {
            ContentValues newValues = new ContentValues();
            newValues.put(DataContract.UserEntry.COLUMN_DISPLAY_NAME, data.getString("real_name"));
            newValues.put(DataContract.UserEntry.COLUMN_MONEY_LEFT, data.getString("balance"));

            String selection = DataContract.UserEntry.COLUMN_EMAIL + " =?";
            String[] selectionArgs = {data.getString("email")};
            int rowUpdated = getContentResolver().update(DataContract.UserEntry.CONTENT_URI,
                    newValues,
                    selection,
                    selectionArgs);
            if(rowUpdated == 0){
                newValues.put(DataContract.UserEntry.COLUMN_EMAIL, data.getString("email"));
                Uri returnedUri = getContentResolver().insert(DataContract.UserEntry.CONTENT_URI,
                        newValues);
                Log.i(TAG,"inserted row num " + ContentUris.parseId(returnedUri));
            }else{
                Log.i(TAG,"updated row num " + Integer.toString(rowUpdated));
            }

        }
    }

    private void updateStorageInfo(String updated_info) {

        //as far as I think, this should receive all the information about all four products
        //so whenever we substitute some product, the original one will not be kept in there
        //or we need some level of delete functionality? or do we need a sync adapter?
        //is this a good idea? what happens when the number of item increases?

        try {
            DataDbHelper dbHelper = new DataDbHelper(this);
            SQLiteDatabase database = dbHelper.getWritableDatabase();

            database.delete(DataContract.ItemEntry.TABLE_NAME,null,null);
            database.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + DataContract.ItemEntry.TABLE_NAME + "'");

            //get this valueArray from the string
            JSONArray valueArray = new JSONArray(updated_info);
            for(int lc = 0; lc < valueArray.length(); ++ lc){
                JSONObject infoJson = valueArray.getJSONObject(lc);
                //everything is the same as following code
                ContentValues newValues = new ContentValues();
                newValues.put(DataContract.ItemEntry.COLUMN_REMAINING_NUM, infoJson.getInt("remaining_num"));
                newValues.put(DataContract.ItemEntry.COLUMN_SHORT_DESC, infoJson.getString("short_desc"));
                newValues.put(DataContract.ItemEntry.COLUMN_PRICE, infoJson.getString("item_price"));
                newValues.put(DataContract.ItemEntry.COLUMN_ITEM_NAME, infoJson.getString("item_name"));
                Uri returnedUri = getContentResolver().insert(DataContract.ItemEntry.CONTENT_URI,
                        newValues);
                Log.i(TAG,"inserted row num " + ContentUris.parseId(returnedUri));
            }

            database.close();
            dbHelper.close();
        }catch (JSONException e){
            Log.e(TAG, "error when parsing Json");
        }
    }

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
     */
    private void sendNotification(String message) {
        Intent intent = new Intent(this, VMSelection.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.icon_w)
                .setContentTitle("Ultimate Vending Machine")
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.icon))
                .setColor(getColor(R.color.notification_color));

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}
