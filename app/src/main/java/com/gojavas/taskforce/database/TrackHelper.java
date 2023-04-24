package com.gojavas.taskforce.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.gojavas.taskforce.entity.TrackEntity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by GJS280 on 14/5/2015.
 */
public class TrackHelper {

    private static TrackHelper instance = null;

    public static TrackHelper getInstance() {
        if(instance == null) {
            instance = new TrackHelper();
        }
        return instance;
    }

    /**
     * Get all tracks of particular FE
     * @return
     */
    public ArrayList<TrackEntity> getTracks() {
        ArrayList<TrackEntity> arrayList = new ArrayList<>();
        SQLiteDatabase db = DatabaseHelper.getInstance().getReadableDatabase();
        Cursor cursor =  db.rawQuery( "select * from  " + DatabaseHelper.TRACK_TABLE_NAME, null);
        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            while(cursor.isAfterLast() == false) {
                TrackEntity track = getTrackEntity(cursor);
                arrayList.add(track);
                cursor.moveToNext();
            }
        }
        cursor.close();
        return arrayList;
    }

    /**
     * Update track if exist otherwise insert new track
     * @param track
     */
    public void insertOrUpdate(TrackEntity track) {
//        SQLiteDatabase db = DatabaseHelper.getInstance().getReadableDatabase();
//        Cursor cursor =  db.rawQuery( "select * from  " + DatabaseHelper.SWIPE_TABLE_NAME, null);
//        if(cursor.getCount() > 0) {
//            // Update drs
//            updateDrs(swipe);
//        } else {
        // Insert drs
        insertTrack(track);
//        }
    }

    /**
     * Insert new track
     * @param track
     * @return
     */
    public long insertTrack(TrackEntity track){
        SQLiteDatabase db = DatabaseHelper.getInstance().getWritableDatabase();
        ContentValues contentValues = getContentValues(track);
        long rowid = db.insert(DatabaseHelper.TRACK_TABLE_NAME, null, contentValues);

        return rowid;
    }

    /**
     * Update track
     * @param track
     */
    private void updateTrack(TrackEntity track) {
//        SQLiteDatabase db = DatabaseHelper.getInstance().getWritableDatabase();
//        ContentValues contentValues = getContentValues(swipe);
//        db.update(DatabaseHelper.ITEM_TABLE_NAME, contentValues, DatabaseHelper.SKU_ID + " = ? ", new String[] {item.getsku_id()});
    }

    /**
     * Update track sync
     */
    public void updateTrackSync() {
        SQLiteDatabase db = DatabaseHelper.getInstance().getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.SYNC, "1");
        db.update(DatabaseHelper.TRACK_TABLE_NAME, contentValues, DatabaseHelper.SYNC + " = ? ", new String[] {"0"});
    }

    /**
     * Delete Track table
     * @return
     */
    public boolean deleteTrackTable() {
        int doneDelete = 0;
        SQLiteDatabase db = DatabaseHelper.getInstance().getReadableDatabase();
        doneDelete = db.delete(DatabaseHelper.TRACK_TABLE_NAME, null , null);
        return doneDelete > 0;
    }

    /**
     * Get track object
     * @param cursor
     * @return
     */
    private TrackEntity getTrackEntity(Cursor cursor) {
        String sr = cursor.getString(cursor.getColumnIndex(DatabaseHelper.SR));
        String username = cursor.getString(cursor.getColumnIndex(DatabaseHelper.USERNAME));
        String latitude = cursor.getString(cursor.getColumnIndex(DatabaseHelper.LATITUDE));
        String longitude = cursor.getString(cursor.getColumnIndex(DatabaseHelper.LONGITUDE));
        String datetime = cursor.getString(cursor.getColumnIndex(DatabaseHelper.DATETIME));
        String networktype = cursor.getString(cursor.getColumnIndex(DatabaseHelper.NETWORK_TYPE));

        TrackEntity track = new TrackEntity();
        track.setsr(sr);
        track.setusername(username);
        track.setlatitude(latitude);
        track.setlongitude(longitude);
        track.setdatetime(datetime);
        track.setdatetime(networktype);

        return track;
    }

    private ContentValues getContentValues(TrackEntity track) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(DatabaseHelper.SR, track.getsr());
        contentValues.put(DatabaseHelper.USERNAME, track.getusername());
        contentValues.put(DatabaseHelper.LATITUDE, track.getlatitude());
        contentValues.put(DatabaseHelper.LONGITUDE, track.getlongitude());
        contentValues.put(DatabaseHelper.DATETIME, track.getdatetime());
        contentValues.put(DatabaseHelper.NETWORK_TYPE, track.getNetworkType());
        contentValues.put(DatabaseHelper.SYNC,track.getsync());

        return contentValues;
    }

    /**
     * Get all tracks of particular FE which is not sync on server
     * @return
     */
    public JSONArray getTracksJsonArray() {
        JSONArray jsonArray=new JSONArray();
        SQLiteDatabase db = DatabaseHelper.getInstance().getReadableDatabase();
        Cursor cursor =  db.rawQuery( "select * from  " + DatabaseHelper.TRACK_TABLE_NAME+ " where " + DatabaseHelper.SYNC + " = ? ", new String[] {"0"});
        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            while(cursor.isAfterLast() == false) {
                JSONObject track = getTrackJsonObject(cursor);
                jsonArray.put(track);
                cursor.moveToNext();
            }
        }
        cursor.close();
        return jsonArray;
    }

    /**
     * Get track jsonobject
     * @param cursor
     * @return
     */
    private JSONObject getTrackJsonObject(Cursor cursor) {
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put(DatabaseHelper.SR, cursor.getString(cursor.getColumnIndex(DatabaseHelper.SR)));
            jsonObject.put(DatabaseHelper.USERNAME,cursor.getString(cursor.getColumnIndex(DatabaseHelper.USERNAME)));
            jsonObject.put(DatabaseHelper.LATITUDE,cursor.getString(cursor.getColumnIndex(DatabaseHelper.LATITUDE)));
            jsonObject.put(DatabaseHelper.LONGITUDE,cursor.getString(cursor.getColumnIndex(DatabaseHelper.LONGITUDE)));
            jsonObject.put(DatabaseHelper.DATETIME,cursor.getString(cursor.getColumnIndex(DatabaseHelper.DATETIME)));
            jsonObject.put(DatabaseHelper.NETWORK_TYPE,cursor.getString(cursor.getColumnIndex(DatabaseHelper.NETWORK_TYPE)));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    /**
     * Insert track data in database
     * @param response
     */
    public void insertJsonInDatabase(JSONObject response) {
        try {
            JSONArray pushDataArray = response.getJSONArray("track");
            int length = pushDataArray.length();
            for(int i=0; i<length; i++){
                JSONObject jsonObject = pushDataArray.getJSONObject(i);
                String sr = jsonObject.getString(DatabaseHelper.SR);
                String username = jsonObject.getString(DatabaseHelper.USERNAME);
                String latitude = jsonObject.getString(DatabaseHelper.LATITUDE);
                String longitude = jsonObject.getString(DatabaseHelper.LONGITUDE);
                String datetime = jsonObject.getString(DatabaseHelper.DATETIME);
                String sync = jsonObject.getString(DatabaseHelper.SYNC);
                String networktype = jsonObject.getString(DatabaseHelper.NETWORK_TYPE);

                TrackEntity track = new TrackEntity();
                track.setsr(sr);
                track.setusername(username);
                track.setlatitude(latitude);
                track.setlongitude(longitude);
                track.setdatetime(datetime);
                track.setsync(sync);
                track.setNetWorkType(networktype);

                TrackHelper.getInstance().insertOrUpdate(track);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
