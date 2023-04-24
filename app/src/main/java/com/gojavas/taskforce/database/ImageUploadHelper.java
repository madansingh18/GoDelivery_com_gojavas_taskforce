package com.gojavas.taskforce.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.gojavas.taskforce.entity.ImageUploadEntity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by GJS280 on 28/10/2015.
 */
public class ImageUploadHelper {

    public static final String IMAGE_PENDING = "Pending";
    public static final String IMAGE_COMPLETED = "Completed";
    public static final String IMAGE_FAILED = "Failed";

    private static ImageUploadHelper instance = null;

    public static ImageUploadHelper getInstance() {
        if(instance == null) {
            instance = new ImageUploadHelper();
        }
        return instance;
    }

    /**
     * Get payment detail
     * @return
     */
    public ImageUploadEntity getImageUploadDetail(String image_name) {
        ImageUploadEntity image = null;
        SQLiteDatabase db = DatabaseHelper.getInstance().getReadableDatabase();
        Cursor cursor =  db.rawQuery("select * from  " + DatabaseHelper.IMAGE_UPLOAD_TABLE_NAME + " where " + DatabaseHelper.IMAGE_NAME + " = ? ", new String[]{image_name});
        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            while(cursor.isAfterLast() == false) {
                image = new ImageUploadEntity();
                image = getImageUploadEntity(cursor);
                cursor.moveToNext();
                break;
            }
        }
        cursor.close();
        return image;
    }

    /**
     * Get all the images with status pending or failed
     * @return
     */
    public ArrayList<ImageUploadEntity> getPendingImages() {
        ArrayList<ImageUploadEntity> pendingImages = new ArrayList<>();
        SQLiteDatabase db = DatabaseHelper.getInstance().getReadableDatabase();
        Cursor cursor =  db.rawQuery("select * from  " + DatabaseHelper.IMAGE_UPLOAD_TABLE_NAME + " where " + DatabaseHelper.UPLOAD_STATUS + " = ? OR " + DatabaseHelper.UPLOAD_STATUS + " = ?", new String[]{IMAGE_PENDING, IMAGE_FAILED});
        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            while(cursor.isAfterLast() == false) {
                ImageUploadEntity imageEntity = getImageUploadEntity(cursor);
                pendingImages.add(imageEntity);
                cursor.moveToNext();
            }
        }
        cursor.close();
        return pendingImages;
    }

    /**
     * Insert new payment
     * @param image
     */
    public void insertOrUpdate(ImageUploadEntity image) {
        String image_name = image.getimage_name();
        SQLiteDatabase db = DatabaseHelper.getInstance().getReadableDatabase();
        Cursor cursor =  db.rawQuery( "select " + DatabaseHelper.IMAGE_NAME + " from  " + DatabaseHelper.IMAGE_UPLOAD_TABLE_NAME + " where " + DatabaseHelper.IMAGE_NAME + " = ? ", new String[] {image_name});
        if(cursor.getCount() > 0) {
            // Update image
            updateImage(image);
        } else {
            // Insert image
            insertImage(image);
        }
    }

    /**
     * Insert new payment
     * @param image
     * @return
     */
    public long insertImage(ImageUploadEntity image){
        SQLiteDatabase db = DatabaseHelper.getInstance().getWritableDatabase();
        ContentValues contentValues = getContentValues(image);
        long rowid = db.insert(DatabaseHelper.IMAGE_UPLOAD_TABLE_NAME, null, contentValues);

        return rowid;
    }

    /**
     * Update image
     * @param image
     */
    private void updateImage(ImageUploadEntity image) {
        SQLiteDatabase db = DatabaseHelper.getInstance().getWritableDatabase();
        ContentValues contentValues = getContentValues(image);
        db.update(DatabaseHelper.IMAGE_UPLOAD_TABLE_NAME, contentValues, DatabaseHelper.IMAGE_NAME + " = ? ", new String[] {image.getimage_name()});
    }

    /**
     * Get payment object
     * @param cursor
     * @return
     */
    private ImageUploadEntity getImageUploadEntity(Cursor cursor) {
        String image_name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.IMAGE_NAME));
        String upload_status = cursor.getString(cursor.getColumnIndex(DatabaseHelper.UPLOAD_STATUS));

        ImageUploadEntity image = new ImageUploadEntity();
        image.setimage_name(image_name);
        image.setupload_status(upload_status);

        return image;
    }

    /**
     * Update payment status
     */
    public void updateImageUploadStatus(String image_name, String uploadStatus) {
        SQLiteDatabase db = DatabaseHelper.getInstance().getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.UPLOAD_STATUS, uploadStatus);
        db.update(DatabaseHelper.IMAGE_UPLOAD_TABLE_NAME, contentValues, DatabaseHelper.IMAGE_NAME + " = ? ", new String[]{image_name});
    }

    private ContentValues getContentValues(ImageUploadEntity image) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(DatabaseHelper.IMAGE_NAME, image.getimage_name());
        contentValues.put(DatabaseHelper.UPLOAD_STATUS, image.getupload_status());

        return contentValues;
    }

    /**
     * Get all image uploads jsonarray
     * @return
     */
    public JSONArray getImageUploadsJsonArray() {
        JSONArray jsonArray=new JSONArray();
        SQLiteDatabase db = DatabaseHelper.getInstance().getReadableDatabase();
        Cursor cursor =  db.rawQuery( "select * from  " + DatabaseHelper.IMAGE_UPLOAD_TABLE_NAME, null);
        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            while(cursor.isAfterLast() == false) {
                JSONObject jsonObject = getImageUploadJsonObject(cursor);
                jsonArray.put(jsonObject);
                cursor.moveToNext();
            }
        }
        cursor.close();
        return jsonArray;
    }

    /**
     * Get image upload jsonobject
     * @param cursor
     * @return
     */
    private JSONObject getImageUploadJsonObject(Cursor cursor) {
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put(DatabaseHelper.IMAGE_NAME, cursor.getString(cursor.getColumnIndex(DatabaseHelper.IMAGE_NAME)));
            jsonObject.put(DatabaseHelper.UPLOAD_STATUS,cursor.getString(cursor.getColumnIndex(DatabaseHelper.UPLOAD_STATUS)));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    /**
     * Insert image upload data in database
     * @param response
     */
    public void insertJsonInDatabase(JSONObject response) {
        try {
            JSONArray pushDataArray = response.getJSONArray("image_upload");
            int length = pushDataArray.length();
            for(int i=0; i<length; i++){
                JSONObject jsonObject = pushDataArray.getJSONObject(i);
                String image_name = jsonObject.getString(DatabaseHelper.IMAGE_NAME);
                String upload_status = jsonObject.getString(DatabaseHelper.UPLOAD_STATUS);

                ImageUploadEntity image = new ImageUploadEntity();
                image.setimage_name(image_name);
                image.setupload_status(upload_status);

                ImageUploadHelper.getInstance().insertOrUpdate(image);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Delete image upload table
     * @return
     */
    public boolean deleteImageUploadTable() {
        int doneDelete = 0;
        SQLiteDatabase db = DatabaseHelper.getInstance().getReadableDatabase();
        doneDelete = db.delete(DatabaseHelper.IMAGE_UPLOAD_TABLE_NAME, null , null);
        db.close();
        return doneDelete > 0;
    }
}
