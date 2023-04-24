package com.gojavas.taskforce.services;

import android.app.IntentService;
import android.content.Intent;

import com.gojavas.taskforce.manager.LogoutShutdownManager;

/**
 * Created by GJS280 on 30/9/2015.
 */
public class LogoutShutdownScheduler extends IntentService {


    public LogoutShutdownScheduler(String name) {
        super(name);
    }

    public  LogoutShutdownScheduler(){
        super("logoutshutdown");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Send logout and shutdown logs to the server
        LogoutShutdownManager.sendLogoutShutdownLogs();
    }
}
