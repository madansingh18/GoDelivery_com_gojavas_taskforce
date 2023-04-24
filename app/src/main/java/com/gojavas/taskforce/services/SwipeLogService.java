package com.gojavas.taskforce.services;



import android.app.IntentService;
import android.content.Intent;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.gojavas.taskforce.database.SwipeHelper;
import com.gojavas.taskforce.manager.BackupManager;
import com.gojavas.taskforce.ui.activity.TaskForceApplication;
import com.gojavas.taskforce.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by gjs331 on 5/26/2015.
 */
public class SwipeLogService extends IntentService {

    public  SwipeLogService(){
        super("SwipeLogService");
    }
    public SwipeLogService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        JSONArray jsonArray= SwipeHelper.getInstance().getSwipesJsonArray();
        JSONObject finalRequestJson = new JSONObject();
        try {
            finalRequestJson.put("swipe", jsonArray);

            System.out.println("swipe " + finalRequestJson.toString());

            JsonObjectRequest pushRequest = new JsonObjectRequest(Request.Method.POST, Constants.SWIPELOG_URL, finalRequestJson, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    System.out.println("swipe log response: " + jsonObject.toString());
                    SwipeHelper.getInstance().updateSwipeSync();

                    // Update logs file
                    BackupManager.getInstance().updateBackup();

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    volleyError.printStackTrace();

                    // Update logs file
                    BackupManager.getInstance().updateBackup();
                }
            });
            TaskForceApplication.getInstance().addToRequestQueue(pushRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
