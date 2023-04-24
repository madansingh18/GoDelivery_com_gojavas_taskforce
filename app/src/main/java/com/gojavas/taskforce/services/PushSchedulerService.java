package com.gojavas.taskforce.services;

import android.app.IntentService;
import android.content.Intent;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.gojavas.taskforce.database.DatabaseHelper;
import com.gojavas.taskforce.database.DeliveryHelper;
import com.gojavas.taskforce.ui.activity.TaskForceApplication;
import com.gojavas.taskforce.utils.Constants;
import com.gojavas.taskforce.utils.UtilityScheduler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by gjs331 on 5/25/2015.
 */
public class PushSchedulerService extends IntentService {

   public PushSchedulerService(){
        super("PushScheduler");
    }

    public PushSchedulerService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        final String drs_docket = intent.getStringExtra(DatabaseHelper.DRS_DOCKET);
        JSONArray jsonArray;

        if (drs_docket == null || drs_docket.isEmpty()) {
             jsonArray= DeliveryHelper.getInstance().getNonSyncDeliveryJson();
        } else {
            jsonArray= DeliveryHelper.getInstance().getDeliveryDocketJson(drs_docket);
        }


        JSONObject finalRequestJson = new JSONObject();
        try {
            finalRequestJson.put("PushData", jsonArray);

            System.out.println("pushData Service: " + finalRequestJson.toString());

            final JsonObjectRequest pushRequest = new JsonObjectRequest(Request.Method.POST, Constants.PUSH_URL, finalRequestJson, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    System.out.println("push response: " + jsonObject.toString());
                    try {
                        JSONArray docketArray = jsonObject.getJSONArray("docket");
                        int length = docketArray.length();
                        for(int i=0; i<length; i++) {
                            String drs_docket = docketArray.getString(i);
                            DeliveryHelper.getInstance().updateDeliverySync(drs_docket);

                            // Upload images of this docket
                            String docketNo = drs_docket.substring(drs_docket.lastIndexOf("_") + 1);
                            UtilityScheduler.uploadImages(PushSchedulerService.this, docketNo);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (drs_docket == null || drs_docket.isEmpty()) {
                        // Start pull scheduler
                        UtilityScheduler.startPullScheduler(TaskForceApplication.getInstance());
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    volleyError.printStackTrace();

                    if (drs_docket == null || drs_docket.isEmpty()) {
                        // Start pull scheduler
                        UtilityScheduler.startPullScheduler(TaskForceApplication.getInstance());
                    }
                }
            });
            TaskForceApplication.getInstance().addToRequestQueue(pushRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
