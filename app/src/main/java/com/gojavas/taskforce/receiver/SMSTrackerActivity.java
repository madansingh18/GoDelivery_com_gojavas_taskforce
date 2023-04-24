package com.gojavas.taskforce.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.gojavas.taskforce.utils.Constants;

public class SMSTrackerActivity extends BroadcastReceiver {
	
    private Context mContext;
    private Bundle mBundle;
    private Intent mIntent;
    
	private static final String TAG = "SMSTRACKER";
    private static final Uri STATUS_URI = Uri.parse("content://sms");
    
	public void onReceive(Context context, Intent intent) {
		try{
			mContext = context;
			mIntent = intent;
			mBundle = intent.getExtras();  
			Log.e(TAG, "Intent Action : " + intent.getAction());

		       if (mBundle != null){

		    	getSMSDetails(context);
		    }
		    else
		    	Log.e(TAG, "Bundle is Empty!");
		}
		catch(Exception sgh){
			Log.e(TAG, "Error in Init : " + sgh.toString());
		}
	}//fn onReceive

	private void getSMSDetails(Context mContext ){
	    SmsMessage[] msgs = null;
		try{
			Object[] pdus = (Object[]) mBundle.get("pdus");
			if(pdus != null){
				msgs = new SmsMessage[pdus.length];
				Log.e(TAG, "pdus length : " + pdus.length);
				for(int k=0; k<msgs.length; k++){
					msgs[k] = SmsMessage.createFromPdu((byte[]) pdus[k]);
//					Log.e(TAG, "getDisplayMessageBody : " + msgs[k].getDisplayMessageBody());
//					Log.e(TAG, "getDisplayOriginatingAddress : " + msgs[k].getDisplayOriginatingAddress());
//					Log.e(TAG, "getMessageBody : " + msgs[k].getMessageBody());
//					Log.e(TAG, "getOriginatingAddress : " + msgs[k].getOriginatingAddress());
//					Log.e(TAG, "getProtocolIdentifier : " + msgs[k].getProtocolIdentifier());
//					Log.e(TAG, "getStatus : " + msgs[k].getStatus());
//					Log.e(TAG, "getStatusOnIcc : " + msgs[k].getStatusOnIcc());
//					Log.e(TAG, "getStatusOnSim : " + msgs[k].getStatusOnSim());

					if (msgs[k].getDisplayMessageBody().contains("OTP"))
				            sendMessage( msgs[k].getDisplayMessageBody(),mContext);
				}
			}
		}
		catch(Exception sfgh){
			Log.e(TAG, "Error in getSMSDetails : " + sfgh.toString());
		}
	}//fn getSMSDetails


	/**
	 * Notify calling Activity
	 * @param message
	 */
	private void sendMessage(String message,Context mContext) {

		Intent intent = new Intent(Constants.OTP_ACTION);
		// You can also include some extra data.
		intent.putExtra("message", message);
		LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
	}
}//End of class SMSTrackerActivity
