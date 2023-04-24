package com.gojavas.taskforce.services;

import android.app.IntentService;
import android.content.Intent;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.gojavas.taskforce.ui.activity.TaskForceApplication;
import com.gojavas.taskforce.utils.Constants;
import com.gojavas.taskforce.utils.Utility;
import com.gojavas.taskforce.utils.UtilityScheduler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by gjs331 on 7/8/2015.
 */
public class PodImageScheduler extends IntentService {


    public PodImageScheduler(String name) {
        super(name);
    }

    public  PodImageScheduler(){
        super("podimages");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        System.out.println("image upload");
        try {
            JSONObject requestParams = new JSONObject();
//            String driverId = Utility.getFromSharedPrefs(this, Constants.EMP_CODE_KEY);
            requestParams.put("device_id", Utility.getDeviceId());
            JsonObjectRequest pushRequest = new JsonObjectRequest(Request.Method.POST, Constants.IMAGE_UPLOAD_STATUS, requestParams, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    try {
                        System.out.println("image api success");
                        String success = jsonObject.getString(Constants.SUCCESS);
                        if (success.equalsIgnoreCase("1")) {
                            JSONArray imagesArray = jsonObject.getJSONArray("images");
                            int length = imagesArray.length();
                            for(int i=0; i<length; i++) {
                                String imageName = imagesArray.getString(i);
                                System.out.println("image name: " + imageName);
                                // Upload this image
                                UtilityScheduler.uploadSingleImage(PodImageScheduler.this, imageName);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    System.out.println("image api fail");
                    volleyError.printStackTrace();
                }
            });
            TaskForceApplication.getInstance().addToRequestQueue(pushRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
