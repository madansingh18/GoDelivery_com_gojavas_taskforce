package com.gojavas.taskforce.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.gojavas.taskforce.database.UserHelper;
import com.gojavas.taskforce.entity.UserEntity;
import com.gojavas.taskforce.manager.LogoutShutdownManager;
import com.gojavas.taskforce.utils.Constants;
import com.gojavas.taskforce.utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by GJS280 on 30/9/2015.
 */
public class ShutdownReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        JSONObject requestJson = new JSONObject();
        try {
            ArrayList<UserEntity> arrayList= UserHelper.getInstance().getUserDetail();
            if(arrayList.size() > 0) {
                UserEntity user = arrayList.get(0);
                requestJson.put(Constants.LOG_SHUT_EVENT_TYPE, "Shutdown");
                requestJson.put(Constants.LOG_SHUT_BRANCH, user.getbranch());
                requestJson.put(Constants.LOG_SHUT_EMP_CODE, user.getemp_code());
                requestJson.put(Constants.LOG_SHUT_USERNAME, user.getusername());
                requestJson.put(Constants.LOG_SHUT_IMEI_NO, user.getimei_no());
                requestJson.put(Constants.LOG_SHUT_DATE_TIME, Utility.getDeliveryTime());
            }
        } catch (JSONException je) {
            je.printStackTrace();
        }
        // Save the json in logout_shutdown_logs.txt
        LogoutShutdownManager.getInstance().updateBackup(requestJson);
    }
}
