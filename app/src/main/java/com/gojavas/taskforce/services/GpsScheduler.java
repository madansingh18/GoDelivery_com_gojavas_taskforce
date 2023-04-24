package com.gojavas.taskforce.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.IBinder;

import com.gojavas.taskforce.database.TrackHelper;
import com.gojavas.taskforce.entity.TrackEntity;
import com.gojavas.taskforce.ui.activity.TaskForceApplication;
import com.gojavas.taskforce.utils.Constants;
import com.gojavas.taskforce.utils.Utility;

import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by gjs331 on 5/20/2015.
 */
public class GpsScheduler extends IntentService {

   public GpsScheduler(){
        super("GpsScheduler");
    }
    public GpsScheduler(String name) {

        super(name);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        HashMap<String,String> map = Utility.getLocationScheduler(TaskForceApplication.getInstance());

        TrackEntity trackEntity= new TrackEntity();

        String longitude=map.get(Constants.LONGITUDE);

        if (map.size()!=0 && longitude!=null && !longitude.equalsIgnoreCase("0.0")) {
            Calendar cal = Calendar.getInstance();

            trackEntity.setsr("");
            trackEntity.setusername(Utility.getFromSharedPrefs(this, Constants.USERNAME_KEY));

            trackEntity.setlongitude(longitude);
            trackEntity.setlatitude(map.get(Constants.LATITUDE));
            trackEntity.setdatetime(cal.getTimeInMillis() + "");
            trackEntity.setNetWorkType(map.get(Constants.PROVIDER));
            trackEntity.setsync("0");

            TrackHelper.getInstance().insertOrUpdate(trackEntity);
        }
//        Toast.makeText(this, map.get(Constants.PROVIDER), Toast.LENGTH_LONG).show();

        System.out.println("provider schediler: " + map.get(Constants.PROVIDER) + " " + map.get(Constants.LONGITUDE));
//        Log.i("provoider",map.get(Constants.PROVIDER)+" "+map.get(Constants.LONGITUDE));

    }

}
