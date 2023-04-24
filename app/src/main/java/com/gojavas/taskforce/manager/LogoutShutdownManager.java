package com.gojavas.taskforce.manager;

import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
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
 * Created by GJS280 on 30/9/2015.
 */
public class LogoutShutdownManager {

    private static LogoutShutdownManager instance;
    private static String filePath = Utility.getApplicationPath("") + "logout_shutdown_logs.txt";
    private static File file;
    private String separator = ",";

    public LogoutShutdownManager() {
        try {
            LogoutShutdownManager.file = new File(filePath);
            if(!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static LogoutShutdownManager getInstance() {
        if(instance == null) {
            instance = new LogoutShutdownManager();
        }
        return instance;
    }

    /**
     * Add data to file
     */
    public void updateBackup(JSONObject newJson) {
        try {
            if(!file.exists()) {
                file.createNewFile();
            }
            FileWriter fileWriter = new FileWriter(file, true);
            BufferedWriter bufferFileWriter  = new BufferedWriter(fileWriter);

            // Logout/shutdown backup
            fileWriter.append(newJson.toString());
            fileWriter.append(separator);

            bufferFileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Send logs to the server
        if(Utility.isInternetConnected(TaskForceApplication.getInstance())) {
            sendLogoutShutdownLogs();
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
            // clear logout shutdown logs file
            LogoutShutdownManager.getInstance().clearFile();
        }
    }

    /**
     * Send all the logout and shutdown logs to the server
     */
    public static void sendLogoutShutdownLogs() {
        String logs = LogoutShutdownManager.getInstance().readFile();
        if(logs == null || TextUtils.isEmpty(logs) || logs.isEmpty()) {
            // Nothing inside logs file
        } else {
            // Remove last separator i.e. ,
            logs = logs.substring(0, logs.lastIndexOf(","));
            // Create logs JSONArray and then request request JSONObject
            JSONObject requestJson = new JSONObject();
            try {
                JSONArray logsJsonArray = new JSONArray("[" + logs + "]");
                requestJson.put(Constants.LOGOUT_SHUTDOWN, logsJsonArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            System.out.println("logout shutdown request: " + requestJson.toString());
            JsonObjectRequest logoutRequest = new JsonObjectRequest(Request.Method.POST, Constants.LOGOUT_URL, requestJson, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    System.out.println("logout/shutdown response: " + jsonObject);
                    // Logs successfully sent to server, hence clear the logs
                    clearSavedData();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {

                }
            });
            TaskForceApplication.getInstance().addToRequestQueue(logoutRequest);
        }
    }
}
