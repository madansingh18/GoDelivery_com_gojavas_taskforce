package com.gojavas.taskforce.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.gojavas.taskforce.entity.SwipeEntity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by GJS280 on 14/5/2015.
 */
public class SwipeHelper {

    private static SwipeHelper instance = null;

    public static SwipeHelper getInstance() {
        if(instance == null) {
            instance = new SwipeHelper();
        }
        return instance;
    }

    /**
     * Get all swipes of particular docket
     * @return
     */
    public ArrayList<SwipeEntity> getSwipes() {
        ArrayList<SwipeEntity> arrayList = new ArrayList<>();
        SQLiteDatabase db = DatabaseHelper.getInstance().getReadableDatabase();
        Cursor cursor =  db.rawQuery( "select * from  " + DatabaseHelper.SWIPE_TABLE_NAME, null);
        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            while(cursor.isAfterLast() == false) {
                SwipeEntity swipe = getSwipeEntity(cursor);
                arrayList.add(swipe);
                cursor.moveToNext();
            }
        }
        cursor.close();
        return arrayList;
    }

    /**
     * Update swipe if exist otherwise insert new swipe
     * @param swipe
     */
    public void insertOrUpdate(SwipeEntity swipe) {
//        SQLiteDatabase db = DatabaseHelper.getInstance().getReadableDatabase();
//        Cursor cursor =  db.rawQuery( "select * from  " + DatabaseHelper.SWIPE_TABLE_NAME, null);
//        if(cursor.getCount() > 0) {
//            // Update drs
//            updateDrs(swipe);
//        } else {
            // Insert drs
            insertSwipe(swipe);
//        }
    }

    /**
     * Insert new swipe
     * @param swipe
     * @return
     */
    public long insertSwipe(SwipeEntity swipe){
        SQLiteDatabase db = DatabaseHelper.getInstance().getWritableDatabase();
        ContentValues contentValues = getContentValues(swipe);
        long rowid = db.insert(DatabaseHelper.SWIPE_TABLE_NAME, null, contentValues);

        return rowid;
    }

    /**
     * Update swipe
     * @param swipe
     */
    private void updateDrs(SwipeEntity swipe) {
//        SQLiteDatabase db = DatabaseHelper.getInstance().getWritableDatabase();
//        ContentValues contentValues = getContentValues(swipe);
//        db.update(DatabaseHelper.ITEM_TABLE_NAME, contentValues, DatabaseHelper.SKU_ID + " = ? ", new String[] {item.getsku_id()});
    }

    /**
     * Update swipe sync
     */
    public void updateSwipeSync() {
        SQLiteDatabase db = DatabaseHelper.getInstance().getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.SYNC, "1");
        db.update(DatabaseHelper.SWIPE_TABLE_NAME, contentValues, DatabaseHelper.SYNC + " = ? ", new String[] {"0"});
    }

    /**
     * Delete Swipe table
     * @return
     */
    public boolean deleteSwipeTable() {
        int doneDelete = 0;
        SQLiteDatabase db = DatabaseHelper.getInstance().getReadableDatabase();
        doneDelete = db.delete(DatabaseHelper.SWIPE_TABLE_NAME, null , null);
        return doneDelete > 0;
    }

    /**
     * Get swipe object
     * @param cursor
     * @return
     */
    private SwipeEntity getSwipeEntity(Cursor cursor) {
        String sr = cursor.getString(cursor.getColumnIndex(DatabaseHelper.SR));
        String docket_no = cursor.getString(cursor.getColumnIndex(DatabaseHelper.DOCKET_NO));
        String drs_no = cursor.getString(cursor.getColumnIndex(DatabaseHelper.DRS_NO));
        String device_type = cursor.getString(cursor.getColumnIndex(DatabaseHelper.DEVICE_TYPE));
        String status = cursor.getString(cursor.getColumnIndex(DatabaseHelper.STATUS));
        String reason = cursor.getString(cursor.getColumnIndex(DatabaseHelper.REASON));
        String datetime = cursor.getString(cursor.getColumnIndex(DatabaseHelper.DATETIME));
        String sync = cursor.getString(cursor.getColumnIndex(DatabaseHelper.SYNC));

        SwipeEntity swipe = new SwipeEntity();
        swipe.setsr(sr);
        swipe.setdocket_no(docket_no);
        swipe.setdrs_no(drs_no);
        swipe.setdevice_type(device_type);
        swipe.setstatus(status);
        swipe.setreason(reason);
        swipe.setdatetime(datetime);
        swipe.setsync(sync);

        return swipe;
    }

    private ContentValues getContentValues(SwipeEntity swipe) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(DatabaseHelper.SR, swipe.getsr());
        contentValues.put(DatabaseHelper.DOCKET_NO, swipe.getdocket_no());
        contentValues.put(DatabaseHelper.DRS_NO, swipe.getdrs_no());
        contentValues.put(DatabaseHelper.DEVICE_TYPE, swipe.getdevice_type());
        contentValues.put(DatabaseHelper.STATUS, swipe.getstatus());
        contentValues.put(DatabaseHelper.REASON, swipe.getreason());
        contentValues.put(DatabaseHelper.DATETIME, swipe.getdatetime());

        return contentValues;
    }

    /**
     * Get all swipes of particular docket in JSonArrayForm
     * @return
     */
    public JSONArray getSwipesJsonArray() {
        JSONArray jsonArray=new JSONArray();
        SQLiteDatabase db = DatabaseHelper.getInstance().getReadableDatabase();
        Cursor cursor =  db.rawQuery( "select * from  " + DatabaseHelper.SWIPE_TABLE_NAME + " where " + DatabaseHelper.SYNC + " = ? ",  new String[] {"0"});
        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            while(cursor.isAfterLast() == false) {
                try {
                    JSONObject swipe = getSwipeJsonObject(cursor);
                    jsonArray.put(swipe);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                cursor.moveToNext();
            }
        }
        cursor.close();
        return jsonArray;
    }

    /**
     * Get swipe jsonobject
     * @param cursor
     * @return
     * @throws JSONException
     */
    private JSONObject getSwipeJsonObject(Cursor cursor) throws JSONException {
        JSONObject jsonObject=new JSONObject();
        jsonObject.put(DatabaseHelper.SR, cursor.getString(cursor.getColumnIndex(DatabaseHelper.SR)));
        jsonObject.put(DatabaseHelper.DOCKET_NO, cursor.getString(cursor.getColumnIndex(DatabaseHelper.DOCKET_NO)));
        jsonObject.put(DatabaseHelper.DRS_NO, cursor.getString(cursor.getColumnIndex(DatabaseHelper.DRS_NO)));
        jsonObject.put(DatabaseHelper.DEVICE_TYPE, cursor.getString(cursor.getColumnIndex(DatabaseHelper.DEVICE_TYPE)));
        jsonObject.put(DatabaseHelper.STATUS, cursor.getString(cursor.getColumnIndex(DatabaseHelper.STATUS)));
        jsonObject.put(DatabaseHelper.REASON, cursor.getString(cursor.getColumnIndex(DatabaseHelper.REASON)));
        jsonObject.put(DatabaseHelper.DATETIME, cursor.getString(cursor.getColumnIndex(DatabaseHelper.DATETIME)));
        return jsonObject;
    }

    /**
     * Insert track data in database
     * @param response
     */
    public void insertJsonInDatabase(JSONObject response) {
        try {
            JSONArray pushDataArray = response.getJSONArray("swipe");
            int length = pushDataArray.length();
            for(int i=0; i<length; i++){
                JSONObject jsonObject = pushDataArray.getJSONObject(i);
                String sr = jsonObject.getString(DatabaseHelper.SR);
                String docket_no = jsonObject.getString(DatabaseHelper.DOCKET_NO);
                String drs_no = jsonObject.getString(DatabaseHelper.DRS_NO);
                String device_type = jsonObject.getString(DatabaseHelper.DEVICE_TYPE);
                String status = jsonObject.getString(DatabaseHelper.STATUS);
                String reason = jsonObject.getString(DatabaseHelper.REASON);
                String datetime = jsonObject.getString(DatabaseHelper.DATETIME);
                String sync = jsonObject.getString(DatabaseHelper.SYNC);

                SwipeEntity swipe = new SwipeEntity();
                swipe.setsr(sr);
                swipe.setdocket_no(docket_no);
                swipe.setdrs_no(drs_no);
                swipe.setdevice_type(device_type);
                swipe.setstatus(status);
                swipe.setreason(reason);
                swipe.setdatetime(datetime);
                swipe.setsync(sync);

                SwipeHelper.getInstance().insertOrUpdate(swipe);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
