package com.gojavas.taskforce.manager;

import android.content.Context;
import android.text.TextUtils;

import com.gojavas.taskforce.database.CallLogHelper;
import com.gojavas.taskforce.database.DeliveryHelper;
import com.gojavas.taskforce.database.ImageUploadHelper;
import com.gojavas.taskforce.database.PaymentHelper;
import com.gojavas.taskforce.database.PaymentStatusHelper;
import com.gojavas.taskforce.database.SwipeHelper;
import com.gojavas.taskforce.database.TrackHelper;
import com.gojavas.taskforce.ui.activity.TaskForceApplication;
import com.gojavas.taskforce.utils.Constants;
import com.gojavas.taskforce.utils.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by GJS280 on 2/6/2015.
 */
public class BackupManager {

    private static BackupManager instance;
    private static String filePath = Utility.getApplicationPath("") + "logs.txt";
    private static File file;
    private String separator = "##;##";

    public BackupManager() {
        try {
            BackupManager.file = new File(filePath);
            if(!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static BackupManager getInstance() {
        if(instance == null) {
            instance = new BackupManager();
        }
        return instance;
    }

    /**
     * Add data to file
     */
    public void updateBackup() {

        // Clear Backup text file
        BackupManager.getInstance().clearFile();

        try {
            if(!file.exists()) {
                file.createNewFile();
            }
            FileWriter fileWriter = new FileWriter(file, true);
            BufferedWriter bufferFileWriter  = new BufferedWriter(fileWriter);

            Context context = TaskForceApplication.getInstance();

            // Login time backup
            JSONObject loginJson = new JSONObject();
            String loginDate = Utility.getFromSharedPrefs(context,Constants.LOGGED_IN_TIME);
            loginJson.put(Constants.LOGGED_IN_TIME, loginDate);
            fileWriter.append(loginJson.toString());
            fileWriter.append(separator);

            // Username backup
            JSONObject userJson= new JSONObject();
            String username = Utility.getFromSharedPrefs(context, Constants.USERNAME_KEY);
            userJson.put(Constants.USERNAME_KEY, username);
            fileWriter.append(userJson.toString());
            fileWriter.append(separator);

            // Delivery data backup
            JSONArray pushJsonArray= DeliveryHelper.getInstance().getNonSyncDeliveryJson();
            JSONObject pushJson = new JSONObject();
            pushJson.put("PushData", pushJsonArray);
            fileWriter.append(pushJson.toString());
            fileWriter.append(separator);

            // Track data backup
            JSONArray trackJsonArray = TrackHelper.getInstance().getTracksJsonArray();
            JSONObject trackJson = new JSONObject();
            trackJson.put("track", trackJsonArray);
            fileWriter.append(trackJson.toString());
            fileWriter.append(separator);

            // Call Log data backup
            JSONArray callJsonArray = CallLogHelper.getInstance().getCallLogsNotSyncJSonArray();
            JSONObject callJson = new JSONObject();
            callJson.put("call_log", callJsonArray);
            fileWriter.append(callJson.toString());
            fileWriter.append(separator);

            // Swipe data backup
            JSONArray swipeJsonArray= SwipeHelper.getInstance().getSwipesJsonArray();
            JSONObject swipeJson = new JSONObject();
            swipeJson.put("swipe", swipeJsonArray);
            fileWriter.append(swipeJson.toString());
            fileWriter.append(separator);

            // Payment backup
            JSONArray paymentJsonArray = PaymentHelper.getInstance().getPaymentsJsonArray();
            JSONObject paymentJson = new JSONObject();
            paymentJson.put("payment", paymentJsonArray);
            fileWriter.append(paymentJson.toString());
            fileWriter.append(separator);

            // Payment status backup
            JSONArray paymentStatusJsonArray = PaymentStatusHelper.getInstance().getPaymentsJsonArray();
            JSONObject paymentStatusJson = new JSONObject();
            paymentStatusJson.put("payment_status", paymentStatusJsonArray);
            fileWriter.append(paymentStatusJson.toString());
            fileWriter.append(separator);

            // Image upload backup
            JSONArray imageUploadJsonArray = ImageUploadHelper.getInstance().getImageUploadsJsonArray();
            JSONObject imageUploadStatus = new JSONObject();
            imageUploadStatus.put("image_upload", imageUploadJsonArray);
            fileWriter.append(imageUploadStatus.toString());
            fileWriter.append(separator);

            // COD, SOD, COR PD and SOD PR Amount backup
            JSONObject amountJson= new JSONObject();
            String amountCOD = Utility.getFromSharedPrefs(context, Constants.AMOUNT_COD);
            String amountSOD = Utility.getFromSharedPrefs(context, Constants.AMOUNT_SOD);
            String amountCODPR = Utility.getFromSharedPrefs(context, Constants.AMOUNT_COD_PR);
            String amountSODPR = Utility.getFromSharedPrefs(context, Constants.AMOUNT_SOD_PR);
            amountJson.put(Constants.AMOUNT_COD, amountCOD);
            amountJson.put(Constants.AMOUNT_SOD, amountSOD);
            amountJson.put(Constants.AMOUNT_COD_PR, amountCODPR);
            amountJson.put(Constants.AMOUNT_SOD_PR, amountSODPR);
            fileWriter.append(amountJson.toString());

            bufferFileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Clear file
     */
    public void clearFile() {
        try {
            PrintWriter writer = new PrintWriter(file);
            writer.print("");
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Read the backup logs file
     * @return
     */
    public String readFile() {
        File file = new File(filePath);
        StringBuilder text = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return text.toString();
    }

    /**
     * Clear saved images and logs file on next day
     */
    public static void clearSavedData() {
        // Delete images and signatures
        String rootPath = Utility.getApplicationPath(Constants.PATH_CAPTURED_IMAGE);
        File rootFile = new File(rootPath);
        if(rootFile.exists()) {
            // Don't clear saved images
//            File[] files = rootFile.listFiles();
//            for(File file : files) {
//                file.delete();
//            }
            // clear logs file
            BackupManager.getInstance().clearFile();
        }
    }

    /**
     * Read saved data in logs.txt file
     * Insert the data in corresponding table
     */
    public static void readSavedData() {
        String logs = BackupManager.getInstance().readFile();
        if(logs == null || TextUtils.isEmpty(logs) || logs.isEmpty()) {
            // Nothing inside logs file
        } else {
            String[] logsData = logs.split(BackupManager.getInstance().separator);
            boolean isSameUser = false, isSameLoginTime = false;
            try {
                // Check saved login time with current time
                JSONObject loginJson = new JSONObject(logsData[0]); // login json is at 0 index
                if(loginJson.has(Constants.LOGGED_IN_TIME)) {
                    // login json
                    String savedloginTime = loginJson.getString(Constants.LOGGED_IN_TIME);
                    if(Utility.areEqual(Long.parseLong(savedloginTime),System.currentTimeMillis())){
                        isSameLoginTime = true;
                    }
                }

                // Check saved user is current user or not
                JSONObject userJson = new JSONObject(logsData[1]); // user json is at 1 index
                if(userJson.has(Constants.USERNAME_KEY)) {
                    // User json
                    String savedUsername = userJson.getString(Constants.USERNAME_KEY);
                    String currentUsername = Utility.getFromSharedPrefs(TaskForceApplication.getInstance(), Constants.USERNAME_KEY);
                    if(savedUsername.equals(currentUsername)) {
                        isSameUser = true;
                    }
                }

                // If saved login date is current date and saved user is current user, get rest of the data else clear all data
                if(isSameLoginTime && isSameUser) {
                    for(String log : logsData) {
                        JSONObject logJson = new JSONObject(log);
                        if(logJson.has("PushData")) {
                            // Push data json
                            DeliveryHelper.getInstance().insertJsonInDatabase(logJson);
                        } else if(logJson.has("track")) {
                            // track json
                            TrackHelper.getInstance().insertJsonInDatabase(logJson);
                        } else if(logJson.has("call_log")) {
                            // call log json
                            CallLogHelper.getInstance().insertJsonInDatabase(logJson);
                        } else if(logJson.has("swipe")) {
                            // swipe json
                            SwipeHelper.getInstance().insertJsonInDatabase(logJson);
                        } else if(logJson.has("payment")) {
                            // Payment json
                            PaymentHelper.getInstance().insertJsonInDatabase(logJson);
                        } else if(logJson.has("payment_status")) {
                            // Payment status json
                            PaymentStatusHelper.getInstance().insertJsonInDatabase(logJson);
                        } else if(logJson.has("image_upload")) {
                            // Image upload json
                            ImageUploadHelper.getInstance().insertJsonInDatabase(logJson);
                        } else if(logJson.has(Constants.AMOUNT_COD) || logJson.has(Constants.AMOUNT_SOD) ||
                                logJson.has(Constants.AMOUNT_COD_PR) || logJson.has(Constants.AMOUNT_SOD_PR)) {
                            // Amount json
                            String amountCOD = logJson.getString(Constants.AMOUNT_COD);
                            String amountSOD = logJson.getString(Constants.AMOUNT_SOD);
                            String amountCODPR = logJson.getString(Constants.AMOUNT_COD_PR);
                            String amountSODPR = logJson.getString(Constants.AMOUNT_SOD_PR);
                            Utility.saveToSharedPrefs(TaskForceApplication.getInstance(), Constants.AMOUNT_COD, amountCOD);
                            Utility.saveToSharedPrefs(TaskForceApplication.getInstance(), Constants.AMOUNT_SOD, amountSOD);
                            Utility.saveToSharedPrefs(TaskForceApplication.getInstance(), Constants.AMOUNT_COD_PR, amountCODPR);
                            Utility.saveToSharedPrefs(TaskForceApplication.getInstance(), Constants.AMOUNT_SOD_PR, amountSODPR);
                        }
                    }
                } else {
                    // Either login date is different or user is different
                    // clear saved data
                    clearSavedData();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
