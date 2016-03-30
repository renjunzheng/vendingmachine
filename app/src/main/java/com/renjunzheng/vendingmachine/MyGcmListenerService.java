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
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.renjunzheng.vendingmachine.data.DataContract;

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
        String message = data.getString("message");
        String action = data.getString("action");
        Log.d(TAG, "action: " + data.getString("action"));
        String updated_info = data.getString("updated_info");
        Log.d(TAG, "From: " + from);
        Log.d(TAG, "Message: " + message);
        String login_status_code = data.getString("status_code");

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
            sendNotification(message);
        }else if(action.equals("CONFIRM_REGISTER")){

        }else if(action.equals("CONFIRM_LOG_IN")){
            loginFeedback(login_status_code);
        }else if(action.equals("UPDATE_STORAGE_INFO")){
            updateStorageInfo(updated_info);
        }
        // [END_EXCLUDE]
    }
    // [END receive_message]

    private void loginFeedback(String login_status_code){
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
        editor.putBoolean("login_checked", true);
        editor.putInt("login_status_code", Integer.parseInt(login_status_code));
        Log.i(TAG,"status_code returned: "+ login_status_code);
        editor.apply();
    }


    private void updateStorageInfo(String updated_info) {

        //as far as I think, this should receive all the information about all four products
        //so whenever we substitute some product, the original one will not be kept in there
        //or we need some level of delete functionality? or do we need a sync adapter?
        //is this a good idea? what happens when the number of item increases?

        try {
            JSONObject infoJson = new JSONObject(updated_info);

            ContentValues newValues = new ContentValues();
            newValues.put(DataContract.ItemEntry.COLUMN_REMAINING_NUM, infoJson.getInt("remaining_num"));
            newValues.put(DataContract.ItemEntry.COLUMN_SHORT_DESC, infoJson.getString("short_desc"));
            String selection = DataContract.ItemEntry.COLUMN_ITEM_NAME + " =?";

            String[] selectionArgs = {infoJson.getString("item_name")};
            int rowUpdated = getContentResolver().update(DataContract.ItemEntry.CONTENT_URI,
                    newValues,
                    selection,
                    selectionArgs);


            if(rowUpdated == 0){
                newValues.put(DataContract.ItemEntry.COLUMN_ITEM_NAME, infoJson.getString("item_name"));
                Uri returnedUri = getContentResolver().insert(DataContract.ItemEntry.CONTENT_URI,
                        newValues);
                Log.i(TAG,"inserted row num " + ContentUris.parseId(returnedUri));
            }else{
                Log.i(TAG,"updated row num " + Integer.toString(rowUpdated));
            }
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
