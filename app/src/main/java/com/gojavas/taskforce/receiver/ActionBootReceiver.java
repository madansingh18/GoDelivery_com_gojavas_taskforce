package com.gojavas.taskforce.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.gojavas.taskforce.utils.UtilityScheduler;

/**
 * Created by gjs331 on 5/13/2015.
 */
public class ActionBootReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent1) {
        UtilityScheduler.startAllScheduler(context);
    }

}
