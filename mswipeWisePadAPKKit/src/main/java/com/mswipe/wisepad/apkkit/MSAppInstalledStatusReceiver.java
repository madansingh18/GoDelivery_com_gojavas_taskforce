package com.mswipe.wisepad.apkkit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

public class MSAppInstalledStatusReceiver extends BroadcastReceiver
{
	
	@Override
	public void onReceive(Context context, Intent intent) 
	{
		// TODO Auto-generated method stub
		if(intent.getData().getSchemeSpecificPart().toString()
				.equalsIgnoreCase("com.mswipetech.wisepad.bajaj"))
		{

			Uri data = intent.getData();
			Log.d("MSAppInstalledStatusReceiver", "Action: " + intent.getAction());
			Log.d("MSAppInstalledStatusReceiver", "The DATA: " + data);
			//check if the package is mswipe apk kit packager that was installed
			
			//when the app is installed the onReceived is called thrice
			//1)ACTION_PACKAGE_REMOVED
			//2)ACTION_PACKAGE_ADDED
			//3)ACTION_PACKAGE_REPLACED 
			
			//when the app is been installed then ACTION_PACKAGE_ADDED will be called
			if(intent.getAction().equals("android.intent.action.PACKAGE_ADDED"))
			{
				WisePadController.OnRequestApplicationIntalled();	
			}
			else if(intent.getAction().equals("android.intent.action.PACKAGE_REPLACED")){
				
				
				WisePadController.OnRequestApplicationUpdated();
			}
			
		}

	}

}
