package com.gojavas.taskforce.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.gojavas.taskforce.entity.CallLogEntity;
import com.gojavas.taskforce.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by GJS280 on 14/5/2015.
 */
public class CallLogHelper {

    private static CallLogHelper instance = null;

    public static CallLogHelper getInstance() {
        if(instance == null) {
            instance = new CallLogHelper();
        }
        return instance;
    }

    /**
     * Get all call logs of particular FE
     * @return
     */
    public ArrayList<CallLogEntity> getCallLogs() {
        ArrayList<CallLogEntity> arrayList = new ArrayList<>();
        SQLiteDatabase db = DatabaseHelper.getInstance().getReadableDatabase();
        Cursor cursor =  db.rawQuery( "select * from  " + DatabaseHelper.CALL_LOG_TABLE_NAME, null);
        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            while(cursor.isAfterLast() == false) {
                CallLogEntity log = getCallLogEntity(cursor);
                arrayList.add(log);
                cursor.moveToNext();
            }
        }
        cursor.close();
        return arrayList;
    }

    /**
     * Get all call logs of particular FE which are not synced with the server
     * @return
     */
    public ArrayList<CallLogEntity> getCallLogsNotSync() {
        ArrayList<CallLogEntity> arrayList = new ArrayList<>();
        SQLiteDatabase db = DatabaseHelper.getInstance().getReadableDatabase();
        Cursor cursor =  db.rawQuery( "select * from  " + DatabaseHelper.CALL_LOG_TABLE_NAME + " where " + DatabaseHelper.SYNC + " = ? ", new String[] {"false"});
        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            while(cursor.isAfterLast() == false) {
                CallLogEntity log = getCallLogEntity(cursor);
                arrayList.add(log);
                cursor.moveToNext();
            }
        }
        cursor.close();
        return arrayList;
    }

    /**
     * Update log if exist otherwise insert new track
     * @param log
     */
    public void insertOrUpdate(CallLogEntity log) {
//        SQLiteDatabase db = DatabaseHelper.getInstance().getReadableDatabase();
//        Cursor cursor =  db.rawQuery( "select * from  " + DatabaseHelper.SWIPE_TABLE_NAME, null);
//        if(cursor.getCount() > 0) {
//            // Update drs
//            updateDrs(swipe);
//        } else {
        // Insert drs
        insertCallLog(log);
//        }
    }

    /**
     * Insert new call log
     * @param log
     * @return
     */
    public long insertCallLog(CallLogEntity log){
        SQLiteDatabase db = DatabaseHelper.getInstance().getWritableDatabase();
        ContentValues contentValues = getContentValues(log);
        long rowid = db.insert(DatabaseHelper.CALL_LOG_TABLE_NAME, null, contentValues);

        return rowid;
    }

    /**
     * Update log
     * @param log
     */
    private void updateCallLog(CallLogEntity log) {
//        SQLiteDatabase db = DatabaseHelper.getInstance().getWritableDatabase();
//        ContentValues contentValues = getContentValues(swipe);
//        db.update(DatabaseHelper.ITEM_TABLE_NAME, contentValues, DatabaseHelper.SKU_ID + " = ? ", new String[] {item.getsku_id()});
    }

    /**
     * Update track sync
     */
    public void updateCallLogSync() {
        SQLiteDatabase db = DatabaseHelper.getInstance().getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.SYNC, "1");
        db.update(DatabaseHelper.CALL_LOG_TABLE_NAME, contentValues, DatabaseHelper.SYNC + " = ? ", new String[] {"0"});
    }

    /**
     * Delete CallLog Table
     * @return
     */
    public boolean deleteCallLogTable() {
        int doneDelete = 0;
        SQLiteDatabase db = DatabaseHelper.getInstance().getReadableDatabase();
        doneDelete = db.delete(DatabaseHelper.CALL_LOG_TABLE_NAME, null , null);
        return doneDelete > 0;
    }

    /**
     * Get call log object
     * @param cursor
     * @return
     */
    private CallLogEntity getCallLogEntity(Cursor cursor) {
        String id = cursor.getString(cursor.getColumnIndex(DatabaseHelper.ID));
        String username = cursor.getString(cursor.getColumnIndex(DatabaseHelper.USERNAME));
        String name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.NAME));
        String number = cursor.getString(cursor.getColumnIndex(DatabaseHelper.NUMBER));
        String duration = cursor.getString(cursor.getColumnIndex(DatabaseHelper.DURATION));
        String date = cursor.getString(cursor.getColumnIndex(DatabaseHelper.DATE));
        String type = cursor.getString(cursor.getColumnIndex(DatabaseHelper.TYPE));
        String call_sms = cursor.getString(cursor.getColumnIndex(DatabaseHelper.CALL_SMS));
        String docket_no = cursor.getString(cursor.getColumnIndex(DatabaseHelper.DOCKET_NO));
        String drsno = cursor.getString(cursor.getColumnIndex(DatabaseHelper.DRSNO));
        String sync = cursor.getString(cursor.getColumnIndex(DatabaseHelper.SYNC));

        CallLogEntity log = new CallLogEntity();
        log.setid(id);
        log.setusername(username);
        log.setname(name);
        log.setnumber(number);
        log.setduration(duration);
        log.setdate(date);
        log.settype(type);
        log.setcall_sms(call_sms);
        log.setdocket_no(docket_no);
        log.setdrsno(drsno);
        log.setsync(sync);

        return log;
    }

    private ContentValues getContentValues(CallLogEntity log) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(DatabaseHelper.ID, log.getid());
        contentValues.put(DatabaseHelper.USERNAME, log.getusername());
        contentValues.put(DatabaseHelper.NAME, log.getname());
        contentValues.put(DatabaseHelper.NUMBER, log.getnumber());
        contentValues.put(DatabaseHelper.DURATION, log.getduration());
        contentValues.put(DatabaseHelper.DATE, log.getdate());
        contentValues.put(DatabaseHelper.TYPE, log.gettype());
        contentValues.put(DatabaseHelper.CALL_SMS, log.getcall_sms());
        contentValues.put(DatabaseHelper.DOCKET_NO, log.getdocket_no());
        contentValues.put(DatabaseHelper.DRSNO, log.getdrsno());
        contentValues.put(DatabaseHelper.SYNC, log.getsync());

        return contentValues;
    }


    public int getNumberOfCallandSms(String type, String call_sms){

        int count=0;
        SQLiteDatabase db = DatabaseHelper.getInstance().getWritableDatabase();
//        Cursor cursor =  db.rawQuery( "SELECT COUNT(*), call_sms, type FROM call_log GROUP BY call_sms, type",null);
//        Cursor cursor =  db.rawQuery( "SELECT COUNT(*) FROM call_log GROUP BY call_sms, type",null);
        Cursor cursor =  db.rawQuery( "select COUNT(*) from  " + DatabaseHelper.CALL_LOG_TABLE_NAME + " where " + DatabaseHelper.TYPE + " = ? AND "+DatabaseHelper.CALL_SMS +" = ?", new String[] {type,call_sms});

        if(cursor!=null && cursor.getCount() > 0 )
        {
                cursor.moveToFirst();
            count=cursor.getInt(0);
        }
        return count;
    }

    public int getDurationSum(String type, String call_sms){

        int durtion=0;
        SQLiteDatabase db = DatabaseHelper.getInstance().getWritableDatabase();
//        Cursor cursor =  db.rawQuery( "SELECT COUNT(*), call_sms, type FROM call_log GROUP BY call_sms, type",null);
//        Cursor cursor =  db.rawQuery( "SELECT COUNT(*) FROM call_log GROUP BY call_sms, type",null);
        Cursor cursor =  db.rawQuery( "select SUM(duration) from  " + DatabaseHelper.CALL_LOG_TABLE_NAME + " where " + DatabaseHelper.TYPE + " = ? AND "+DatabaseHelper.CALL_SMS +" = ?", new String[] {type,call_sms});

        int  count = (cursor!=null && cursor.getCount() > 0 )? cursor.getCount():0;

        if(cursor!=null && cursor.getCount()>0)
        {
            cursor.moveToFirst();
            durtion= cursor.getInt(0);
        }
        return durtion;
    }

    /**
     * Get all call logs of particular FE which are not synced with the server
     * @return
     */
    public JSONArray getCallLogsNotSyncJSonArray() {
        JSONArray jsonArray=new JSONArray();
        SQLiteDatabase db = DatabaseHelper.getInstance().getReadableDatabase();

        Cursor cursor =  db.rawQuery( "select * from  " + DatabaseHelper.CALL_LOG_TABLE_NAME + " where " + DatabaseHelper.SYNC + " = ? ", new String[] {"0"});
        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            while(cursor.isAfterLast() == false) {
                JSONObject log = getCallLogJsonObject(cursor);
                jsonArray.put(log);
                cursor.moveToNext();
            }
        }
        cursor.close();
        return jsonArray;
    }

    /**
     * Get jsonobject of call log
     * @param cursor
     * @return
     */
    private JSONObject getCallLogJsonObject(Cursor cursor) {

        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put(DatabaseHelper.NAME, cursor.getString(cursor.getColumnIndex(DatabaseHelper.NAME)));
            jsonObject.put(DatabaseHelper.USERNAME, cursor.getString(cursor.getColumnIndex(DatabaseHelper.USERNAME)));
            jsonObject.put(DatabaseHelper.NUMBER,cursor.getString(cursor.getColumnIndex(DatabaseHelper.NUMBER)));
            jsonObject.put(DatabaseHelper.DURATION,cursor.getString(cursor.getColumnIndex(DatabaseHelper.DURATION)));
            jsonObject.put(DatabaseHelper.DATE,cursor.getString(cursor.getColumnIndex(DatabaseHelper.DATE)));
            jsonObject.put(DatabaseHelper.TYPE,cursor.getString(cursor.getColumnIndex(DatabaseHelper.TYPE)));
            jsonObject.put(DatabaseHelper.CALL_SMS, cursor.getString(cursor.getColumnIndex(DatabaseHelper.CALL_SMS)));
            jsonObject.put(DatabaseHelper.DOCKET_NO, cursor.getString(cursor.getColumnIndex(DatabaseHelper.DOCKET_NO)));
            jsonObject.put(DatabaseHelper.DRSNO, cursor.getString(cursor.getColumnIndex(DatabaseHelper.DRSNO)));
        }
        catch (JSONException e) {
            e.printStackTrace();
        }


        return jsonObject;
    }

    public boolean isSmsExit(String timestamp){
        SQLiteDatabase db = DatabaseHelper.getInstance().getReadableDatabase();
        Cursor cursor =  db.rawQuery( "select * from  " + DatabaseHelper.CALL_LOG_TABLE_NAME + " where " + DatabaseHelper.DATE + " = '"+timestamp +"' AND "+DatabaseHelper.CALL_SMS+" = '"+ Constants.SMS+"'", null);
        if(cursor.getCount() > 0) {
            return false;
        }
        return true;
    }

    public boolean isCallExit(String timestamp){
        SQLiteDatabase db = DatabaseHelper.getInstance().getReadableDatabase();
        Cursor cursor =  db.rawQuery( "select * from  " + DatabaseHelper.CALL_LOG_TABLE_NAME + " where " + DatabaseHelper.DATE + " = '"+timestamp +"' AND "+DatabaseHelper.CALL_SMS +" = '"+Constants.CALL+"'", null);
        if(cursor.getCount() > 0) {
            return false;
        }
        return true;
    }

    /**
     * Insert track data in database
     * @param response
     */
    public void insertJsonInDatabase(JSONObject response) {
        try {
            JSONArray pushDataArray = response.getJSONArray("call_log");
            int length = pushDataArray.length();
            for(int i=0; i<length; i++){
                JSONObject jsonObject = pushDataArray.getJSONObject(i);
                String id = jsonObject.getString(DatabaseHelper.ID);
                String username = jsonObject.getString(DatabaseHelper.USERNAME);
                String name = jsonObject.getString(DatabaseHelper.NAME);
                String number = jsonObject.getString(DatabaseHelper.NUMBER);
                String duration = jsonObject.getString(DatabaseHelper.DURATION);
                String date = jsonObject.getString(DatabaseHelper.DATE);
                String type = jsonObject.getString(DatabaseHelper.TYPE);
                String call_sms = jsonObject.getString(DatabaseHelper.CALL_SMS);
                String docket_no = jsonObject.getString(DatabaseHelper.DOCKET_NO);
                String drsno = jsonObject.getString(DatabaseHelper.DRSNO);
                String sync = jsonObject.getString(DatabaseHelper.SYNC);

                CallLogEntity calllog = new CallLogEntity();
                calllog.setid(id);
                calllog.setusername(username);
                calllog.setname(name);
                calllog.setnumber(number);
                calllog.setduration(duration);
                calllog.setdate(date);
                calllog.settype(type);
                calllog.setcall_sms(call_sms);
                calllog.setdocket_no(docket_no);
                calllog.setdrsno(drsno);
                calllog.setsync(sync);

                CallLogHelper.getInstance().insertOrUpdate(calllog);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
