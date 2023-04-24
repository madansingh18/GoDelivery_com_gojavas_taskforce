package com.gojavas.taskforce.services;

import android.app.IntentService;
import android.content.Intent;

import com.gojavas.taskforce.utils.Constants;
import com.gojavas.taskforce.utils.Utility;
import com.gojavas.taskforce.utils.UtilityScheduler;

/**
 * Created by gjs331 on 5/25/2015.
 */
public class SyncSchedulerService extends IntentService {

    public SyncSchedulerService() {
        super("SyncScheduler");
    }

    public SyncSchedulerService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        // If current date is different from login date
        String saveDate = Utility.getFromSharedPrefs(this, Constants.LOGGED_IN_TIME);
        if (!Utility.areEqual(Long.parseLong(saveDate), System.currentTimeMillis())) {
            // Skip scheduler job
            System.out.println("scheduler skip");
            return;
        }

        System.out.println("scheduler continue");
        // Pull call/sms data
        UtilityScheduler.startCallSmsService(this);
        UtilityScheduler.startPushScheduler(this, "");

    }
}
