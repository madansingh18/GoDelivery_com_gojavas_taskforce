package com.gojavas.taskforce.services;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Telephony;
import android.util.Log;

import com.gojavas.taskforce.database.CallLogHelper;
import com.gojavas.taskforce.database.DrsHelper;
import com.gojavas.taskforce.entity.CallLogEntity;
import com.gojavas.taskforce.utils.Constants;
import com.gojavas.taskforce.utils.Utility;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by gjs331 on 6/10/2015.
 */

public class InsertSms extends IntentService {


    public InsertSms(){
        super("InsertSms");
    }
    public InsertSms(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

//        String timestap = String.valueOf(getTodayTimestamp());
        String timestamp = Utility.getFromSharedPrefs(this,Constants.LOGGED_IN_TIME);
//        Cursor managedCursor = cr.query(CallLog.Calls.CONTENT_URI, CallLog.Calls.DATE + ">= ?", new String[]{timestamp}, null);
        ContentResolver contentResolver = getContentResolver();
        Cursor sms_sent_cursor = contentResolver.query(Uri.parse("content://sms/"),null, Telephony.Sms.DATE+ ">= ?",new String[]{timestamp}, null);


        int type2=sms_sent_cursor.getColumnIndex("type");

        int indexBody = sms_sent_cursor.getColumnIndex("body");
//        int indexAddress = sms_sent_cursor.getColumnIndex("address");
//        int date=sms_sent_cursor.getColumnIndex("date");
        long currenttime= System.currentTimeMillis();

        if (indexBody < 0 || !sms_sent_cursor.moveToFirst()) return;
//        arrayAdapter.clear();

        if (sms_sent_cursor.getCount()>0){

            ArrayList<String> professional_contacts= DrsHelper.getInstance().getContactNumber();

            do {
                int type = sms_sent_cursor.getInt(type2);
//            long smstime = sms_sent_cursor.getInt(date);
                long smstime=sms_sent_cursor.getLong(sms_sent_cursor.getColumnIndex("date"));

                if (CallLogHelper.getInstance().isSmsExit(smstime+""))

//                    if ( (areEqual(smstime,currenttime)) && type==2){
                    if (type==2){

                        String address=sms_sent_cursor.getString(sms_sent_cursor.getColumnIndex("address"));
                        String type_professional_personal=Constants.PERSONAL;

                        int actual_len=address.length();

                        String phonenumber = (actual_len > 10)? address.substring((actual_len - 10), actual_len):address;

                        phonenumber="0"+phonenumber;

                        Log.i("phonenumber", phonenumber);

                        String docketNo = "", drsNo = "";
                        if (professional_contacts.contains(phonenumber)){
                            type_professional_personal= Constants.PROFESSIONAL;
                            ArrayList<String> detail = DrsHelper.getInstance().getDocketDrsNumber(phonenumber);
                            docketNo = detail.get(0);
                            drsNo = detail.get(1);
                        }else{
                            type_professional_personal=Constants.PERSONAL;
                        }

                        CallLogEntity callLogEntity=new CallLogEntity();
                        callLogEntity.setusername(Utility.getFromSharedPrefs(this, Constants.USERNAME_KEY));
                        callLogEntity.setname(phonenumber);
                        callLogEntity.setnumber(address);
                        callLogEntity.setduration("0");
                        callLogEntity.setsync("0");
                        callLogEntity.settype(type_professional_personal);
                        callLogEntity.setdate(smstime+"");
                        callLogEntity.setcall_sms(Constants.SMS);
                        callLogEntity.setdocket_no(docketNo);
                        callLogEntity.setdrsno(drsNo);

                        CallLogHelper.getInstance().insertCallLog(callLogEntity);

                    }

            } while (sms_sent_cursor.moveToNext());

        }
    }


    private boolean areEqual(long smsDate, long currentTimeMillis) {
        try {
            Calendar c1 = Calendar.getInstance();
            c1.setTime(new Date(smsDate));

            Calendar c2 = Calendar.getInstance();
            c2.setTime(new Date(currentTimeMillis));

            return ((c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR))
                    &&
                    (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)));

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    //With this method you will get the timestamp of today at midnight
    public long getTodayTimestamp(){
        Calendar c1 = Calendar.getInstance();
        c1.setTime(new Date());

        Calendar c2 = Calendar.getInstance();
        c2.set(Calendar.YEAR, c1.get(Calendar.YEAR));
        c2.set(Calendar.MONTH, c1.get(Calendar.MONTH));
        c2.set(Calendar.DAY_OF_MONTH, c1.get(Calendar.DAY_OF_MONTH));
        c2.set(Calendar.HOUR_OF_DAY, 0);
        c2.set(Calendar.MINUTE, 0);
        c2.set(Calendar.SECOND, 0);

        return c2.getTimeInMillis();
    }
}
