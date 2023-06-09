///**
// * Copyright 2015 Google Inc. All Rights Reserved.
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// * http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package com.gojavas.taskforce.gcm;
//
//import android.os.Bundle;
//import android.util.Log;
//
//import com.gojavas.taskforce.utils.Utility;
//import com.gojavas.taskforce.ui.activity.TaskForceApplication;
//import com.google.android.gms.gcm.GcmListenerService;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//public class MyGcmListenerService extends GcmListenerService {
//
//    private static final String TAG = "MyGcmListenerService";
//
//    /**
//     * Called when message is received.
//     *
//     * @param from SenderID of the sender.
//     * @param data Data bundle containing message data as key/value pairs.
//     *             For Set of keys use data.keySet().
//     */
//    // [START receive_message]
//    @Override
//    public void onMessageReceived(String from, Bundle data) {
//        String message = data.getString("message");
//        Log.d(TAG, "From: " + from);
//        Log.d(TAG, "Message: " + message);
//
//        /**
//         * Production applications would usually process the message here.
//         * Eg: - Syncing with server.
//         *     - Store message in local database.
//         *     - Update UI.
//         */
//
//        /**
//         * In some cases it may be useful to show a notification indicating to the user
//         * that a message was received.
//         */
//        sendNotification(message);
//    }
//    // [END receive_message]
//
//    /**
//     * Create and show a simple notification containing the received GCM message.
//     *
//     * @param message GCM message received.
//     */
//    private void sendNotification(String message) {
//        String requestId = "";
//        try {
//            JSONObject jsonObject = new JSONObject(message);
//            requestId = jsonObject.getString("request_id");
//            // Show notification
//            TaskForceApplication.getInstance().showNinetyMinutesNotification(requestId, "Downloading...");
//            // Call API to get all the details
//            Utility.getNinetyMinuteDocket(this, requestId);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
////        Intent intent = new Intent(this, HomeActivity.class);
////        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
////        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent, PendingIntent.FLAG_ONE_SHOT);
//
////        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
////        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
////                .setSmallIcon(R.drawable.app_logo)
////                .setContentTitle("GoDelivery")
////                .setContentText("90 Min: " + requestId)
////                .setAutoCancel(true)
////                .setSound(defaultSoundUri)
////                .setContentIntent(pendingIntent);
////
////        NotificationManager notificationManager =
////                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
////
////        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
//    }
//}
