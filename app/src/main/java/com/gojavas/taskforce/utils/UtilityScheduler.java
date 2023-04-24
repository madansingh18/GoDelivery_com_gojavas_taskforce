package com.gojavas.taskforce.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.text.TextUtils;

import com.gojavas.taskforce.database.DatabaseHelper;
import com.gojavas.taskforce.manager.UploadManager;
import com.gojavas.taskforce.services.CallLogService;
import com.gojavas.taskforce.services.FileDeleteService;
import com.gojavas.taskforce.services.GpsLogService;
import com.gojavas.taskforce.services.GpsScheduler;
import com.gojavas.taskforce.services.InsertCallLog;
import com.gojavas.taskforce.services.InsertSms;
import com.gojavas.taskforce.services.LogoutShutdownScheduler;
import com.gojavas.taskforce.services.PodImageScheduler;
import com.gojavas.taskforce.services.PullSchedulerService;
import com.gojavas.taskforce.services.PushSchedulerService;
import com.gojavas.taskforce.services.SyncSchedulerService;

import java.io.File;

/**
 * Created by gjs331 on 5/25/2015.
 */
public class UtilityScheduler {

    /**
     * Service to sync pull data from server
     *
     * @param context
     */
    public static void startPullScheduler(Context context) {
        Intent intent = new Intent(context, PullSchedulerService.class);
        context.startService(intent);
    }

    /**
     * Service to sync push data to server
     *
     * @param context
     */
    public static void startPushScheduler(Context context, String drs_docket) {
        Intent intent = new Intent(context, PushSchedulerService.class);
        intent.putExtra(DatabaseHelper.DRS_DOCKET, drs_docket);
        context.startService(intent);
    }

    /**
     * Service to sync data to server
     *
     * @param context
     */
    public static void startSync(Context context) {
        Intent intent = new Intent(context, SyncSchedulerService.class);
        context.startService(intent);
    }

    /**
     * Service to sync data to server
     *
     * @param context
     */
    public static void startCallSmsService(Context context) {
        Intent intentCall = new Intent(context, InsertCallLog.class);
        context.startService(intentCall);
        Intent intentSms = new Intent(context, InsertSms.class);
        context.startService(intentSms);
    }

    /**
     * Service to push tracking data to server
     *
     * @param context
     */
    public static void startGpsScheduler(Context context) {
        Intent intent = new Intent(context, GpsLogService.class);
        context.startService(intent);
    }

    /**
     * Service to push call log to server
     *
     * @param context
     */
    public static void startCallLogScheduler(Context context) {
        Intent intent = new Intent(context, CallLogService.class);
        context.startService(intent);
    }

    /**
     * Service to push swipe log to server
     *
     * @param context
     */
    public static void startSwipeScheduler(Context context) {
//        Intent intent = new Intent(context, SwipeLogService.class);
//        context.startService(intent);
    }

    /**
     * Start schedulers
     *
     * @param context
     */
    public static void startAllScheduler(Context context) {
        // Start scheduler if user is logged in
        String userLoggedIn = Utility.getFromSharedPrefs(context, Constants.USER_LOGGED_IN);
        if (userLoggedIn != null && !TextUtils.isEmpty(userLoggedIn) && userLoggedIn.equalsIgnoreCase("true")) {
            // Start track scheduler
            startTrackScheduler(context);
            // Start Sync scheduler
            startSyncScheduler(context);
            // Start POD images scheduler
            startPodImagesScheduler(context);
            // Start Logout Shutdown scheduler
            startLogoutShutdownScheduler(context);
            // Start File Delete images scheduler
            startFileDeleteScheduler(context);
        }
    }

    /**
     * Stop all schedulers
     *
     * @param context
     */
    public static void stopAllScheduler(Context context) {
        // Stop track scheduler
        Intent trackIntent = new Intent(context, GpsScheduler.class);
        PendingIntent trackPendingIntent = PendingIntent.getService(context, Constants.ALARM_TRACK_KEY, trackIntent, 0);
        AlarmManager trackAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        trackAlarmManager.cancel(trackPendingIntent);

        // Stop sync scheduler
        Intent syncIntent = new Intent(context, SyncSchedulerService.class);
        PendingIntent syncPendingIntent = PendingIntent.getService(context, Constants.ALARM_SYNC_KEY, syncIntent, 0);
        AlarmManager syncAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        syncAlarmManager.cancel(syncPendingIntent);

        // Stop POD scheduler
//        Intent podIntent = new Intent(context, PodImageScheduler.class);
//        PendingIntent podPendingIntent = PendingIntent.getService(context, Constants.ALARM_POD_KEY, podIntent, 0);
//        AlarmManager podAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//        podAlarmManager.cancel(podPendingIntent);
    }

    /**
     * Scheduler for send tracking details
     *
     * @param context
     */
    public static void startTrackScheduler(Context context) {
        Intent intent = new Intent(context, GpsScheduler.class);
        PendingIntent pintent = PendingIntent.getService(context, Constants.ALARM_TRACK_KEY, intent, 0);
        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        long repeatingTime = 1 * 60 * 1000; // 1 minute
//        long repeatingTime = 30*1000;
        alarm.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), repeatingTime, pintent);
    }

    /**
     * Scheduler for pull/push data
     *
     * @param context
     */
    public static void startSyncScheduler(Context context) {
        Intent intent = new Intent(context, SyncSchedulerService.class);
        PendingIntent pintent = PendingIntent.getService(context, Constants.ALARM_SYNC_KEY, intent, 0);
        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        long repeatingTime = 15 * 60 * 1000; // 15 minutes
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), repeatingTime, pintent);
    }


    /**
     * Scheduler for pod images
     *
     * @param context
     */
    public static void startPodImagesScheduler(Context context) {
        Intent intent = new Intent(context, PodImageScheduler.class);
        PendingIntent pintent = PendingIntent.getService(context, Constants.ALARM_POD_KEY, intent, 0);
        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        long repeatingTime = 25 * 60 * 1000; // 25 minutes
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), repeatingTime, pintent);
    }

    /**
     * Scheduler for logout and shutdown time
     *
     * @param context
     */
    public static void startLogoutShutdownScheduler(Context context) {
        Intent intent = new Intent(context, LogoutShutdownScheduler.class);
        PendingIntent pintent = PendingIntent.getService(context, Constants.ALARM_LOGOUT_SHUTDOWN_KEY, intent, 0);
        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        long repeatingTime = 3 * 60 * 60 * 1000; // 3 hours
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), repeatingTime, pintent);
    }

    /**
     * Scheduler for deleting images
     *
     * @param context
     */
    public static void startFileDeleteScheduler(Context context) {
        Intent intent = new Intent(context, FileDeleteService.class);
        PendingIntent pintent = PendingIntent.getService(context, Constants.ALARM_FILE_DELETE_KEY, intent, 0);
        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        long repeatingTime = 24 * 60 * 60 * 1000; // 24 hours
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), repeatingTime, pintent);
    }

    /**
     * Upload all the images of this docket on our server
     *
     * @param docketNo
     */
    public static void uploadImages(Context context, String docketNo) {
        // Our server
        File rootFile = new File(Utility.getApplicationPath(Constants.PATH_CAPTURED_IMAGE));
        if (rootFile.exists()) {
            File[] subFiles = rootFile.listFiles();
            int length = subFiles.length;
            for (int i = 0; i < length; i++) {
                File file = subFiles[i];
                String fileName = file.getName();
                if (fileName.contains(docketNo)) {
                    String filePath = file.getPath();
                    System.out.println("filepath: " + filePath);

                    Intent uploadIntent = new Intent(context, UploadManager.class);
                    uploadIntent.putExtra(UploadManager.UPLOAD_FILE_PATH, filePath);
                    context.startService(uploadIntent);
                }
            }
        }

        // Amazon
//        File rootFile = new File(Utility.getApplicationPath(Constants.PATH_CAPTURED_IMAGE));
//        if(rootFile.exists()) {
//            File[] subFiles = rootFile.listFiles();
//            for(File file : subFiles) {
//                String fileName = file.getName();
//                if(fileName.contains(docketNo)) {
//                    // Upload it
//                    uploadSingleImageOnAmazon(context, fileName);
//                }
//            }
//        }
    }

    /**
     * Uplaod this image on our server
     *
     * @param context
     * @param fileName
     */
    public static void uploadSingleImage(Context context, final String fileName) {
        // Our server
        String filePath = Utility.getApplicationPath(Constants.PATH_CAPTURED_IMAGE) + fileName;
        File imageFile = new File(filePath);
        if (imageFile.exists()) {
            Intent uploadIntent = new Intent(context, UploadManager.class);
            uploadIntent.putExtra(UploadManager.UPLOAD_FILE_PATH, filePath);
            context.startService(uploadIntent);
        }

        // Amazon
//        String filePath = Utility.getApplicationPath(Constants.PATH_CAPTURED_IMAGE) + fileName;
//        File file = new File(filePath);
//        if (file.exists()) {
//            // Insert it in image upload table
//            boolean shouldUpload = true;
//            ImageUploadEntity imageEntity = ImageUploadHelper.getInstance().getImageUploadDetail(fileName);
//            if (imageEntity == null) {
//                // Image is never uploaded
//                // Set its status as "Pending" and upload it
//                shouldUpload = true;
//                ImageUploadEntity image = new ImageUploadEntity();
//                image.setimage_name(fileName);
//                image.setupload_status(ImageUploadHelper.IMAGE_PENDING);
//                ImageUploadHelper.getInstance().insertOrUpdate(image);
//            } else {
//                String status = imageEntity.getupload_status();
//                if (status.equalsIgnoreCase(ImageUploadHelper.IMAGE_COMPLETED)) {
//                    // Image is completely uploaded, So do not upload again;
//                    shouldUpload = false;
//                } else {
//                    // Image was uploaded before but was not uploaded
//                    // Set its status as "Pending" and upload again
//                    shouldUpload = true;
//                    ImageUploadHelper.getInstance().updateImageUploadStatus(fileName, ImageUploadHelper.IMAGE_PENDING);
//                }
//            }
//
//            // Upload this file
//            if (shouldUpload) {
//                try {
//                    String timeStamp = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
//                    TransferUtility transferUtility = Utility.getTransferUtility(context);
//                    // Give timestamp with filename to automatically create folder on Amazon
//                    TransferObserver observer = transferUtility.upload(Constants.BUCKET_NAME, timeStamp + "/" + fileName, file);
//                    observer.setTransferListener(new TransferListener() {
//
//                        @Override
//                        public void onStateChanged(int id, TransferState state) {
//                            System.out.println("amazon state: " + fileName + " == " + state.toString());
//                            String newStatus = state.toString();
//                            if (newStatus.equalsIgnoreCase(ImageUploadHelper.IMAGE_COMPLETED)) {
//                                ImageUploadHelper.getInstance().updateImageUploadStatus(fileName, ImageUploadHelper.IMAGE_COMPLETED);
//                            }
//                        }
//
//                        @Override
//                        public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
////                        int percentage = (int) (bytesCurrent / bytesTotal * 100);
//                        }
//
//                        @Override
//                        public void onError(int id, Exception ex) {
//                            ImageUploadHelper.getInstance().updateImageUploadStatus(fileName, ImageUploadHelper.IMAGE_FAILED);
//                        }
//
//                    });
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }
    }

}
