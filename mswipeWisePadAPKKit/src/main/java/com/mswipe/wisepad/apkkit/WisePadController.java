package com.mswipe.wisepad.apkkit;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;

import java.io.File;

public class WisePadController 
{
	public static final String MS_APKINSTALL_RECEIVER="com.mswipe.wisepad.apkkit.InstallReceiver";
	private static final String apkurl = "http://chip.mswipetech.com/mobapps/Android/mswipeapkkit/mswipegojavas.apk";
	
	public static final int MS_LOGIN_ACTIVITY_REQUEST_CODE = 1;
	public static final int MS_LOGIN_ACTIVITY_WITH_UI_REQUEST_CODE = 2;
	public static final int MS_CARDSALE_ACTIVITY_REQUEST_CODE = 3;
	public static final int MS_EMISALE_ACTIVITY_REQUEST_CODE = 4;
	public static final int MS_VOID_TRX_ACTIVITY_REQUEST_CODE = 5;
	public static final int MS_CASHSALE_ACTIVITY_REQUEST_CODE = 6;
	public static final int MS_BANKSALE_ACTIVITY_REQUEST_CODE = 7;
	public static final int MS_LAST_TRX_ACTIVITY_REQUEST_CODE = 8;
	public static final int MS_CHANGEPASSWORD_ACTIVITY_REQUEST_CODE = 9;
	
	private static final String MSWIPEWISEPAD_LOGIN_ACTION = "mswipe.wisepad.sdk.LoginAction";
	private static final String MSWIPEWISEPAD_LOGIN_WITH_UI_ACTION = "mswipe.wisepad.sdk.LoginActionUI";
	private static final String MSWIPEWISEPAD_CARD_SALE_ACTION = "mswipe.wisepad.sdk.CardSaleAction";
	private static final String MSWIPEWISEPAD_EMI_SALE_ACTION = "mswipe.wisepad.sdk.EmiAction";
	private static final String MSWIPEWISEPAD_VOID_SALE_ACTION = "mswipe.wisepad.sdk.VoidSaleAction";
	private static final String MSWIPEWISEPAD_CASH_SALE_ACTION = "mswipe.wisepad.sdk.CashSaleAction";
	private static final String MSWIPEWISEPAD_BANK_SALE_ACTION = "mswipe.wisepad.sdk.BankSaleAction";
	private static final String MSWIPEWISEPAD_LAST_TAX_ACTION = "mswipe.wisepad.sdk.LastTrx";
	private static final String MSWIPEWISEPAD_CHANGE_PASSWORD_ACTION = "mswipe.wisepad.sdk.ChangePassword";
	
	public static enum ORIENTATION{
		PORTRAIT, LANDSCAPE
	}

	private static WisePadControllerListener mWisePadControllerListener;	
	private CustomProgressDialog mProgressDialog; 
	private AutoUpdateAPKController mAutoUpdateAPKController;	
	private static Context mContext;
	private static Intent mPendingIntent;
	private long lastRequestTime;
	
	private static int MSAPP_ISTALLMODE = 0; // 0 is for 
	private static final int MSAPP_UPDATE = 2; // 0 is for 
	private static final int MSAPP_INSTALL = 1; // 0 is for 
	
	private static WisePadController wisepadController = null;
	
	public static WisePadController sharedInstance(Context context, WisePadControllerListener wisePadControllerListener)
	{
		mContext = context;
		mWisePadControllerListener = wisePadControllerListener;
		
		if(wisepadController == null)
		{			
			wisepadController = new WisePadController();
		}
		return wisepadController;
	}
	
	public void login(String userName, String password, boolean production, ORIENTATION orientation){
		
		Intent intent = new Intent();
		intent.setAction(MSWIPEWISEPAD_LOGIN_ACTION);		
		intent.putExtra("username", userName);
		intent.putExtra("password", password);
		intent.putExtra("production", production);
		
		if (orientation == ORIENTATION.PORTRAIT) 
			intent.putExtra("orientation", "portrait");
		else
			intent.putExtra("orientation", "landscape");
		
		if (System.currentTimeMillis() - lastRequestTime >= 3000) {
			if(isAppExists(intent)){
				((Activity) mContext).startActivityForResult(intent, MS_LOGIN_ACTIVITY_REQUEST_CODE);			
			}else{
				intent.putExtra("requestcode", MS_LOGIN_ACTIVITY_REQUEST_CODE);
				mPendingIntent = intent;
				processInstallApk();
			}
			lastRequestTime = System.currentTimeMillis();
		}
	}
	
	public void loginWithUI(String userName, String password, boolean production, ORIENTATION orientation){
		
		Intent intent = new Intent();
		intent.setAction(MSWIPEWISEPAD_LOGIN_WITH_UI_ACTION);		
		intent.putExtra("username", userName);
		intent.putExtra("password", password);
		intent.putExtra("production", production);
		
		if (orientation == ORIENTATION.PORTRAIT) 
			intent.putExtra("orientation", "portrait");
		else
			intent.putExtra("orientation", "landscape");
		
		if (System.currentTimeMillis() - lastRequestTime >= 3000) {
			if(isAppExists(intent)){
				((Activity) mContext).startActivityForResult(intent, MS_LOGIN_ACTIVITY_WITH_UI_REQUEST_CODE);			
			}else{
				intent.putExtra("requestcode", MS_LOGIN_ACTIVITY_WITH_UI_REQUEST_CODE);
				mPendingIntent = intent;
				processInstallApk();
			}
			lastRequestTime = System.currentTimeMillis();
		}
	}
	
	public void processCardSale(String referenceID, String sessionToken, String amount, 
			String mobileNO,String requestID, String mailID, String notes, 
			String extra1, String extra2,String extra3, 
			boolean isSignatureRequired, boolean production, ORIENTATION orientation)
	{
		Intent intent;
		intent = new Intent();
		intent.setAction(MSWIPEWISEPAD_CARD_SALE_ACTION);
		intent.putExtra("refrenceId", referenceID);
		intent.putExtra("sessionTokeniser", sessionToken);
		intent.putExtra("Amount", amount);   
		intent.putExtra("MobileNumber", mobileNO); 
		intent.putExtra("MailId", mailID);   
		intent.putExtra("Reciept", requestID);
		intent.putExtra("Notes", notes);
		intent.putExtra("isSignatureRequired", isSignatureRequired);
		intent.putExtra("production", production);
		intent.putExtra("extra1", extra1);
		intent.putExtra("extra2", extra2);
		intent.putExtra("extra3", extra3);
		
		if (orientation == ORIENTATION.PORTRAIT) 
			intent.putExtra("orientation", "portrait");
		else
			intent.putExtra("orientation", "landscape");
		
		if (System.currentTimeMillis() - lastRequestTime >= 3000) {
			if(isAppExists(intent))
			{			     			
				((Activity) mContext).startActivityForResult(intent, MS_CARDSALE_ACTIVITY_REQUEST_CODE);
			}
			else{
				intent.putExtra("requestcode", MS_CARDSALE_ACTIVITY_REQUEST_CODE);
				mPendingIntent = intent;
				processInstallApk();
			}
			lastRequestTime = System.currentTimeMillis();
		}
	}
	
	public void processEmiSale(String referenceID, String sessionToken, String cardFirstSixDigit, 
			String emiAmount, String mobileNO,String requestID,	String mailID, String notes, 
			String extra1, String extra2,String extra3, boolean isSignatureRequired, 
			boolean production, ORIENTATION orientation)
	{
		Intent intent;
		intent = new Intent();
		intent.setAction(MSWIPEWISEPAD_EMI_SALE_ACTION);
		intent.putExtra("refrenceId", referenceID);
		intent.putExtra("sessionTokeniser", sessionToken);
		intent.putExtra("CardFirstSixDigit", cardFirstSixDigit); 
		intent.putExtra("EmiAmount", emiAmount);   
		intent.putExtra("MobileNumber", mobileNO); 
		intent.putExtra("MailId", mailID);   
		intent.putExtra("Reciept", requestID);
		intent.putExtra("Notes", notes);
		intent.putExtra("isSignatureRequired", isSignatureRequired);
		intent.putExtra("production", production);
		intent.putExtra("extra1", extra1);
		intent.putExtra("extra2", extra2);
		intent.putExtra("extra3", extra3);
		
		if (orientation == ORIENTATION.PORTRAIT) 
			intent.putExtra("orientation", "portrait");
		else
			intent.putExtra("orientation", "landscape");
		
		if (System.currentTimeMillis() - lastRequestTime >= 3000) {
			if(isAppExists(intent))
			{			     			
				((Activity) mContext).startActivityForResult(intent, MS_EMISALE_ACTIVITY_REQUEST_CODE);
			}
			else{
				intent.putExtra("requestcode", MS_EMISALE_ACTIVITY_REQUEST_CODE);
				mPendingIntent = intent;
				processInstallApk();
			}
			lastRequestTime = System.currentTimeMillis();
		}
	}
	
	public void processVoidTrx(String refrenceId, String sessionTokeniser, String amount,
			String last4digits, String selectedDate, String selectedDataLbl, boolean production, ORIENTATION orientation)
	{
		Intent intent = new Intent();
		intent.setAction(MSWIPEWISEPAD_VOID_SALE_ACTION);
		intent.putExtra("refrenceId", refrenceId);
		intent.putExtra("sessionTokeniser", sessionTokeniser);
		intent.putExtra("Amount", amount);
		intent.putExtra("Last4digits", last4digits);
		intent.putExtra("SelectedDate", selectedDate);
		intent.putExtra("SelectedDataLbl", selectedDataLbl);
		intent.putExtra("production", production);
		
		if (orientation == ORIENTATION.PORTRAIT) 
			intent.putExtra("orientation", "portrait");
		else
			intent.putExtra("orientation", "landscape");
		
		if (System.currentTimeMillis() - lastRequestTime >= 3000) {
			if(intent.resolveActivity(mContext.getPackageManager()) != null)
			{					
				((Activity) mContext).startActivityForResult(intent, MS_VOID_TRX_ACTIVITY_REQUEST_CODE);
			}
			else{
				intent.putExtra("requestcode", MS_VOID_TRX_ACTIVITY_REQUEST_CODE);
				processInstallApk();
			}
			lastRequestTime = System.currentTimeMillis();
		}
	}
	
	
	public void processCashSale(String referenceID, String sessionToken, String amount, 
			String mobileNO,String requestID, String mailID, String notes, 
			boolean isSignatureRequired, boolean production, ORIENTATION orientation)
	{
		
		Intent intent;
		intent = new Intent();
		intent.setAction(MSWIPEWISEPAD_CASH_SALE_ACTION);
		intent.putExtra("refrenceId", referenceID);
		intent.putExtra("sessionTokeniser", sessionToken);
		intent.putExtra("Amount", amount);   
		intent.putExtra("MobileNumber", mobileNO); 
		intent.putExtra("MailId", mailID);   
		intent.putExtra("Reciept", requestID);
		intent.putExtra("Notes", notes);
		intent.putExtra("isSignatureRequired", isSignatureRequired);
		intent.putExtra("production", production);
		
		if (orientation == ORIENTATION.PORTRAIT) 
			intent.putExtra("orientation", "portrait");
		else
			intent.putExtra("orientation", "landscape");
		
		if (System.currentTimeMillis() - lastRequestTime >= 3000) {
			if(isAppExists(intent))
			{			     			
				((Activity) mContext).startActivityForResult(intent, MS_CASHSALE_ACTIVITY_REQUEST_CODE);
			}
			else{
				intent.putExtra("requestcode", MS_CASHSALE_ACTIVITY_REQUEST_CODE);
				mPendingIntent = intent;
				processInstallApk();
			}
			lastRequestTime = System.currentTimeMillis();
		}
	}
	
	
	public void processBankSale(String referenceID, String sessionToken, String amount,  String cheque,  String selectedData,  
			String formatedDate, String requestID, String mobileNO, String mailID, String notes, 
			boolean isSignatureRequired, boolean production, ORIENTATION orientation)
	{
		Intent intent;
		intent = new Intent();
		intent.setAction(MSWIPEWISEPAD_BANK_SALE_ACTION);
		intent.putExtra("refrenceId", referenceID);
		intent.putExtra("sessionTokeniser", sessionToken);
		intent.putExtra("Amount", amount);   
		intent.putExtra("Cheque", cheque);   
		intent.putExtra("SelectedDate", selectedData);   
		intent.putExtra("FormatedDate", formatedDate);   
		intent.putExtra("MobileNumber", mobileNO); 
		intent.putExtra("Reciept", requestID);
		intent.putExtra("Notes", notes);
		intent.putExtra("MailId", mailID);   
		intent.putExtra("isSignatureRequired", isSignatureRequired);
		intent.putExtra("production", production);
		
		if (orientation == ORIENTATION.PORTRAIT) 
			intent.putExtra("orientation", "portrait");
		else
			intent.putExtra("orientation", "landscape");
		
		if (System.currentTimeMillis() - lastRequestTime >= 3000) {
			if(isAppExists(intent))
			{			     			
				((Activity) mContext).startActivityForResult(intent, MS_BANKSALE_ACTIVITY_REQUEST_CODE);
			}
			else{
				intent.putExtra("requestcode", MS_BANKSALE_ACTIVITY_REQUEST_CODE);
				mPendingIntent = intent;
				processInstallApk();
			}
			lastRequestTime = System.currentTimeMillis();
		}
	}
	
	
	
	
	public void processLastTrx(String refrenceId, String sessionTokeniser,boolean production, ORIENTATION orientation)
	{
		Intent intent = new Intent();
		intent.setAction(MSWIPEWISEPAD_LAST_TAX_ACTION);	
		intent.putExtra("refrenceId", refrenceId);
		intent.putExtra("sessionTokeniser", sessionTokeniser);
		intent.putExtra("production", production);
		
		
		if (orientation == ORIENTATION.PORTRAIT) 
			intent.putExtra("orientation", "portrait");
		else
			intent.putExtra("orientation", "landscape");
		
		if (System.currentTimeMillis() - lastRequestTime >= 3000) {
			if(intent.resolveActivity(mContext.getPackageManager()) != null)
			{					
				((Activity) mContext).startActivityForResult(intent, MS_LAST_TRX_ACTIVITY_REQUEST_CODE);
			}
			else{
				intent.putExtra("requestcode", MS_LAST_TRX_ACTIVITY_REQUEST_CODE);
				processInstallApk();
			}
			lastRequestTime = System.currentTimeMillis();
		}
	}
	
	public void processChangePassword(String refrenceId, String sessionTokeniser, 
			String orignalPassword, String newPassword, boolean production, ORIENTATION orientation)
	{
		Intent intent = new Intent();
		intent.setAction(MSWIPEWISEPAD_CHANGE_PASSWORD_ACTION);	
		intent.putExtra("refrenceId", refrenceId);
		intent.putExtra("sessionTokeniser", sessionTokeniser);
		intent.putExtra("production", production);
		intent.putExtra("orignalPassword", orignalPassword);
		intent.putExtra("newPassword", newPassword);
		
		if (orientation == ORIENTATION.PORTRAIT) 
			intent.putExtra("orientation", "portrait");
		else
			intent.putExtra("orientation", "landscape");
		
		if (System.currentTimeMillis() - lastRequestTime >= 3000) {
			if(intent.resolveActivity(mContext.getPackageManager()) != null)
			{					
				((Activity) mContext).startActivityForResult(intent, MS_CHANGEPASSWORD_ACTIVITY_REQUEST_CODE);
			}
			else{
				intent.putExtra("requestcode", MS_CHANGEPASSWORD_ACTIVITY_REQUEST_CODE);
				processInstallApk();
			}
			lastRequestTime = System.currentTimeMillis();
		}
	}
	
	protected void processInstallApk()
	{
		AlertDialog.Builder builder = new Builder(mContext);
		builder.setTitle("Mswipe APKKit");
		builder.setMessage("Mswipe WebRocket is unavailable, press ok to install.");
		builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				
				mProgressDialog = new CustomProgressDialog(mContext,"installing, please wait...");
				mProgressDialog.show();
				InstallMswieApplication(mContext);
			}
		});
		builder.create().show();
		
	}
	
	public void processUpdateMswipeApplication()
	{
		AlertDialog.Builder builder = new Builder(mContext);
		builder.setTitle("Mswipe APKKit");
		builder.setMessage("New version of Mswipe WebRocket is available, press ok to install.");
		builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				
				mProgressDialog = new CustomProgressDialog(mContext,"installing, please wait...");
				mProgressDialog.show();
				UpdateMswipeApplication(mContext);
			}
		});
		builder.create().show();
		
	}
	
	protected void InstallMswieApplication(Context context)
	{
		MSAPP_ISTALLMODE = MSAPP_INSTALL;
		mAutoUpdateAPKController = new AutoUpdateAPKController(context, handlerMswipeApplicationInstalled);
		mAutoUpdateAPKController.processApkUpdateDetails(apkurl);
	}
	
	protected void UpdateMswipeApplication(Context context)
	{
		MSAPP_ISTALLMODE = MSAPP_UPDATE;
		mAutoUpdateAPKController = new AutoUpdateAPKController(context, handlerMswipeApplicationInstalled);
		mAutoUpdateAPKController.processApkUpdateDetails(apkurl);
	}
	
	protected static void OnRequestApplicationIntalled()
	{		
		//the below will be not called when the app is installed manually this is when its not been 
		//installed prgramatically
		
		if(MSAPP_ISTALLMODE == MSAPP_INSTALL) 
		{
			if(mContext != null && mPendingIntent != null){
				Activity activity = (Activity) mContext;
				activity.startActivityForResult(mPendingIntent, mPendingIntent.getIntExtra("requestcode", 0) );				
			}
		}
	}
	
	protected static void OnRequestApplicationUpdated()
	{		
		if(MSAPP_ISTALLMODE == MSAPP_UPDATE) // this is initiated the apk is been installed
		{
			new Handler().post(new Runnable() 
			{
				
				@Override
				public void run() 
				{
					if(wisepadController != null && wisepadController.mWisePadControllerListener != null)
						wisepadController.mWisePadControllerListener.onMswipeAppUpdated();
				}
			});
		}
	}
	
	protected Handler handlerMswipeApplicationInstalled = new Handler()
	{
		public void handleMessage(android.os.Message msg) 
		{
			mProgressDialog.dismiss();
			Bundle bundle = msg.getData();
			final String errMsg = bundle.getString("httperror");
			if(errMsg.length()==0)
			{
				//mWisePadControllerListener.onApkDownloadComplete();
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory().getAbsolutePath() +"/BALiveUpdate.apk")), "application/vnd.android.package-archive");
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // without this flag android returned a intent error!
				((Activity)mContext).startActivity(intent);
				
			}
			else{
				
				new Handler().post(new Runnable() 
				{
					
					@Override
					public void run() 
					{
						mWisePadControllerListener.onError("Unable to install the Mswipe WisepadWebRocket, " + errMsg, 111);
					}
				});
			}
		};
	};
	
	private boolean isAppExists(Intent intent){
		if(intent.resolveActivity(mContext.getPackageManager()) != null)
		{
			return true;
		}
		return false;		
	}
}