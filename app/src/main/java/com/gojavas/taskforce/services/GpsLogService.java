package com.gojavas.taskforce.services;

import android.app.IntentService;
import android.content.Intent;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.gojavas.taskforce.database.TrackHelper;
import com.gojavas.taskforce.ui.activity.TaskForceApplication;
import com.gojavas.taskforce.utils.Constants;
import com.gojavas.taskforce.utils.UtilityScheduler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by gjs331 on 5/27/2015.
 */
public class GpsLogService extends IntentService {

    public GpsLogService() {
        super("GpsLogService");
    }

    public GpsLogService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        JSONArray jsonArray = TrackHelper.getInstance().getTracksJsonArray();
        JSONObject finalRequestJson = new JSONObject();
        try {
            finalRequestJson.put("track", jsonArray);

            System.out.println("track: " + finalRequestJson.toString());

            JsonObjectRequest pushRequest = new JsonObjectRequest(Request.Method.POST, Constants.TRACK_URL, finalRequestJson, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    System.out.println("track log response: " + jsonObject.toString());
                    TrackHelper.getInstance().updateTrackSync();

                    // Start call log scheduler
                    UtilityScheduler.startCallLogScheduler(TaskForceApplication.getInstance());

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    volleyError.printStackTrace();

                    // Start call log scheduler
                    UtilityScheduler.startCallLogScheduler(TaskForceApplication.getInstance());
                }
            });
            TaskForceApplication.getInstance().addToRequestQueue(pushRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}