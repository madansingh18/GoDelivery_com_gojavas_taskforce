package com.gojavas.taskforce.ui.activity;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.text.TextUtils;

import androidx.core.app.NotificationCompat;
import androidx.multidex.MultiDexApplication;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.Volley;
import com.gojavas.taskforce.R;
import com.gojavas.taskforce.database.DatabaseHelper;
import com.gojavas.taskforce.database.UserHelper;
import com.gojavas.taskforce.entity.UserEntity;
import com.gojavas.taskforce.manager.BackupManager;
import com.gojavas.taskforce.manager.LogoutShutdownManager;
import com.gojavas.taskforce.utils.Constants;
import com.gojavas.taskforce.utils.Utility;
import com.gojavas.taskforce.utils.UtilityScheduler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by GJS280 on 10/4/2015.
 */
public class TaskForceApplication extends MultiDexApplication {

    public static final String TAG = TaskForceApplication.class.getSimpleName();
    private RequestQueue mRequestQueue;
    private static TaskForceApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public TaskForceApplication() {
        instance = this;
    }

    public static TaskForceApplication getInstance() {
        if (instance == null)
            throw new IllegalStateException();
        return instance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        int socketTimeout = 10000; // 10 seconds
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        req.setRetryPolicy(policy);

        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    public void cancelPendingRequests() {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(TAG);
        }
    }

    /**
     * Clear all the data related to application
     */
    public void clearApplicationData() {
        // Save logout logs
        saveLogoutLogs();

        // Backup unsynced files
        BackupManager.getInstance().updateBackup();
        // Create backup
        Utility.createBackup();
        // Clear shared preferences
        SharedPreferences.Editor editor = getSharedPreferences("com.gojavas.taskforce.preferences", Context.MODE_PRIVATE).edit();
        editor.clear();
        editor.commit();

        // Delete all the tables
//        CallLogHelper.getInstance().deleteCallLogTable();
//        DeliveryHelper.getInstance().deleteDeliveryTable();
//        DrsHelper.getInstance().deleteDrsTable();
//        ItemHelper.getInstance().deleteItemTable();
//        PaymentHelper.getInstance().deletePaymentTable();
//        SlabHelper.getInstance().deleteSlabTable();
//        SwipeHelper.getInstance().deleteSwipeTable();
//        TrackHelper.getInstance().deleteTrackTable();
//        UserHelper.getInstance().deleteUserTable();

        // Clear rest of the data
        File cache = getCacheDir();
        File appDir = new File(cache.getParent());
        if (appDir.exists()) {
            String[] children = appDir.list();
            for (String s : children) {
                if (!s.equals("lib")) {
                    deleteDir(new File(appDir, s));
                }
            }
        }

        DatabaseHelper.instance = null;

        // Stop track and sync scheduler
        UtilityScheduler.stopAllScheduler(getApplicationContext());
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        return dir.delete();
    }

    /**
     * Call logout api
     */
    private void saveLogoutLogs() {
        JSONObject requestJson = new JSONObject();
        try {
            ArrayList<UserEntity> arrayList=UserHelper.getInstance().getUserDetail();
            if(arrayList.size() > 0) {
                UserEntity user = arrayList.get(0);
                requestJson.put(Constants.LOG_SHUT_EVENT_TYPE, "Logout");
                requestJson.put(Constants.LOG_SHUT_BRANCH, user.getbranch());
                requestJson.put(Constants.LOG_SHUT_EMP_CODE, user.getemp_code());
                requestJson.put(Constants.LOG_SHUT_USERNAME, user.getusername());
                requestJson.put(Constants.LOG_SHUT_IMEI_NO, user.getimei_no());
                requestJson.put(Constants.LOG_SHUT_DATE_TIME, Utility.getDeliveryTime());
            }
        } catch (JSONException je) {
            je.printStackTrace();
        }
        // Save the json in logout_shutdown_logs.txt
        LogoutShutdownManager.getInstance().updateBackup(requestJson);
    }

    /**
     * Show push notification
     * @param requestId
     */
    public void showNinetyMinutesNotification(String requestId, String message) {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.app_logo)
                .setContentTitle("90 Minutes")
                .setContentText(requestId + " - " + message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(11 /* ID of notification */, notificationBuilder.build());
    }

    /**
     * Remove notifications
     */
    public void removeNinetyMinutesNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(11);
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_login, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
}
