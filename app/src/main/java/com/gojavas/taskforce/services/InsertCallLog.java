package com.gojavas.taskforce.services;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.provider.CallLog;
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
 * Created by gjs331 on 6/11/2015.
 */
public class InsertCallLog extends IntentService {

    public InsertCallLog(){

        super("InsertCallLog");
    }

    public InsertCallLog(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

//        String timestamp = String.valueOf(getTodayTimestamp());

        String timestamp = Utility.getFromSharedPrefs(this,Constants.LOGGED_IN_TIME);

        Cursor managedCursor = getContentResolver().query(CallLog.Calls.CONTENT_URI, null, CallLog.Calls.DATE + ">= ? AND "+CallLog.Calls.TYPE+ "= ? AND "+CallLog.Calls.DURATION+" > ?", new String[]{timestamp,"2","0"}, null);

        if (managedCursor.getCount()>0) {

            ArrayList<String> professional_contacts = DrsHelper.getInstance().getContactNumber();

            int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
            int name = managedCursor.getColumnIndex(CallLog.Calls.CACHED_NAME);
            int duration1 = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
            int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
            int incoming_outgoing = managedCursor.getColumnIndex(CallLog.Calls.TYPE);

            String name1 = "", phNumber = "", callDuration = "", date1 = "", type = "", incoming_outgoing_type = "";

            if (managedCursor.moveToFirst() == true) {

                do {

                    date1 = managedCursor.getString(date);

                    phNumber = managedCursor.getString(number);
                    callDuration = managedCursor.getString(duration1);
                    name1 = managedCursor.getString(name);
                    incoming_outgoing_type = managedCursor.getString(incoming_outgoing);

                    String dir = null;


                    if (name1 == null || name1 == "") {
                        name1 = phNumber;
                    }

                    final String finalPhNumber = phNumber;
                    final String finalCallDuration = callDuration;
                    final String finalName = name1;
                    final String finalDate = date1;
                    /*((Activity)mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            sb.append("\nPhone Number:--- " + finalPhNumber + " \nCall duration in sec :--- " + finalCallDuration + "  name= " + finalName + "  date= " + finalDate);
                            sb.append("\n----------------------------------");
                            Log.i("*****Call Summary******", "Call Duration is:-------" + sb);
                        }
                    });*/



                    if (CallLogHelper.getInstance().isCallExit(date1)){
                   /* if (CallLog.Calls.OUTGOING_TYPE == Integer.parseInt(incoming_outgoing_type) &&
                            (!callDuration.equalsIgnoreCase("00:00:00") && !callDuration.equalsIgnoreCase("0"))) {
*/

//                        String phNumberch = (phNumber.length() > 10) ? phNumber.substring(phNumber.length() - 10, 10) : phNumber;


                        int actual_len=phNumber.length();



                        String phNumberch = (actual_len > 10) ? phNumber.substring((actual_len - 10),actual_len) : phNumber;
                        phNumberch="0"+phNumberch;






                        Log.i("phonenuber",phNumberch.length()+"");
                        String docketNo = "", drsNo = "";
                        if (professional_contacts.contains(phNumberch)) {
                            type = Constants.PROFESSIONAL;
                            ArrayList<String> detail = DrsHelper.getInstance().getDocketDrsNumber(phNumberch);
                            docketNo = detail.get(0);
                            drsNo = detail.get(1);
                        } else {
                            type = Constants.PERSONAL;
                        }

                        CallLogEntity callLogEntity = new CallLogEntity();
                        callLogEntity.setusername(Utility.getFromSharedPrefs(this, Constants.USERNAME_KEY));
                        callLogEntity.setname(name1);
                        callLogEntity.setnumber(phNumber);
                        callLogEntity.setduration(callDuration);
                        callLogEntity.setsync("0");
                        callLogEntity.settype(type);
                        callLogEntity.setdate(date1);
                        callLogEntity.setcall_sms(Constants.CALL);
                        callLogEntity.setdocket_no(docketNo);
                        callLogEntity.setdrsno(drsNo);

                        CallLogHelper.getInstance().insertCallLog(callLogEntity);

                    }
                }
                    while (managedCursor.moveToNext()) ;
                }

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
