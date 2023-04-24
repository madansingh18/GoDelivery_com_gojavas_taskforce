package com.gojavas.taskforce.services;

import android.app.IntentService;
import android.content.Intent;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.gojavas.taskforce.database.CallLogHelper;
import com.gojavas.taskforce.ui.activity.TaskForceApplication;
import com.gojavas.taskforce.utils.Constants;
import com.gojavas.taskforce.utils.UtilityScheduler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by gjs331 on 5/26/2015.
 */
public class CallLogService extends IntentService {

    public CallLogService(){
        super("CallLogService");
    }

    public CallLogService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        JSONArray jsonArray = CallLogHelper.getInstance().getCallLogsNotSyncJSonArray();
        JSONObject finalRequestJson = new JSONObject();
        try {
            finalRequestJson.put("call_log", jsonArray);

            System.out.println("call log: " + finalRequestJson.toString());

            JsonObjectRequest pushRequest = new JsonObjectRequest(Request.Method.POST, Constants.CALLLOG_URL, finalRequestJson, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    System.out.println("call log response: " + jsonObject.toString());
                    CallLogHelper.getInstance().updateCallLogSync();

                    // Start swipe scheduler
                    UtilityScheduler.startSwipeScheduler(TaskForceApplication.getInstance());
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    volleyError.printStackTrace();

                    // Start swipe scheduler
                    UtilityScheduler.startSwipeScheduler(TaskForceApplication.getInstance());
                }
            });
            TaskForceApplication.getInstance().addToRequestQueue(pushRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
