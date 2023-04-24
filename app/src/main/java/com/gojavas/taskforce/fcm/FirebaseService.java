package com.gojavas.taskforce.fcm;

import android.util.Log;

import com.gojavas.taskforce.ui.activity.TaskForceApplication;
import com.gojavas.taskforce.utils.Utility;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

public class FirebaseService extends FirebaseMessagingService {

    private static final String TAG = "FirebaseService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);


        String message = remoteMessage.getData().get("message");
        Log.d(TAG, "Notification ::" + remoteMessage.getFrom() + "Message  :: " + message);


        sendNotification(message);
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);

        Log.d(TAG, "Token :: " + s);
    }

    private void sendNotification(String message) {
        String requestId = "";
        try {
            JSONObject jsonObject = new JSONObject(message);
            requestId = jsonObject.getString("request_id");
            // Show notification
            TaskForceApplication.getInstance().showNinetyMinutesNotification(requestId, "Downloading...");
            // Call API to get all the details
            Utility.getNinetyMinuteDocket(this, requestId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
