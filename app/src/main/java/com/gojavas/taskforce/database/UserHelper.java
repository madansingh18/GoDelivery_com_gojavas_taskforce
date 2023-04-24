package com.gojavas.taskforce.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.gojavas.taskforce.entity.UserEntity;

import java.util.ArrayList;

/**
 * Created by GJS280 on 15/4/2015.
 */
public class UserHelper {

    private static UserHelper instance = null;

    public static UserHelper getInstance() {
        if(instance == null) {
            instance = new UserHelper();
        }
        return instance;
    }

    /**
     * Get user detail
     * @return
     */
    public ArrayList<UserEntity> getUserDetail() {
        ArrayList<UserEntity> array_list = new ArrayList<>();
        SQLiteDatabase db = DatabaseHelper.getInstance().getReadableDatabase();
        Cursor cursor =  db.rawQuery( "select * from  " + DatabaseHelper.USER_TABLE_NAME, null );
        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            while(cursor.isAfterLast() == false) {
                String sr = cursor.getString(cursor.getColumnIndex(DatabaseHelper.SR));
                String emp_code = cursor.getString(cursor.getColumnIndex(DatabaseHelper.EMP_CODE));
                String username = cursor.getString(cursor.getColumnIndex(DatabaseHelper.USERNAME));
                String password = cursor.getString(cursor.getColumnIndex(DatabaseHelper.PASSWORD));
                String firstname = cursor.getString(cursor.getColumnIndex(DatabaseHelper.FIRSTNAME));
                String city = cursor.getString(cursor.getColumnIndex(DatabaseHelper.CITY));
                String branch = cursor.getString(cursor.getColumnIndex(DatabaseHelper.BRANCH));
                String imei_no = cursor.getString(cursor.getColumnIndex(DatabaseHelper.IMEI_NO));
                String mobile_no = cursor.getString(cursor.getColumnIndex(DatabaseHelper.MOBILE_NO));
                String datetime = cursor.getString(cursor.getColumnIndex(DatabaseHelper.DATETIME));

                UserEntity user = new UserEntity();
                user.setsr(sr);
                user.setemp_code(emp_code);
                user.setusername(username);
                user.setpassword(password);
                user.setfirstname(firstname);
                user.setcity(city);
                user.setbranch(branch);
                user.setimei_no(imei_no);
                user.setmobile_no(mobile_no);
                user.setdatetime(datetime);

                array_list.add(user);

                cursor.moveToNext();
            }
        }
        cursor.close();
        return array_list;
    }

    /**
     * Get user detail
     * @return
     */
    public UserEntity getUserCompleteDetail() {
        UserEntity user = new UserEntity();
        SQLiteDatabase db = DatabaseHelper.getInstance().getReadableDatabase();
        Cursor cursor =  db.rawQuery( "select * from  " + DatabaseHelper.USER_TABLE_NAME, null );
        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            while(cursor.isAfterLast() == false) {
                String sr = cursor.getString(cursor.getColumnIndex(DatabaseHelper.SR));
                String emp_code = cursor.getString(cursor.getColumnIndex(DatabaseHelper.EMP_CODE));
                String username = cursor.getString(cursor.getColumnIndex(DatabaseHelper.USERNAME));
                String password = cursor.getString(cursor.getColumnIndex(DatabaseHelper.PASSWORD));
                String firstname = cursor.getString(cursor.getColumnIndex(DatabaseHelper.FIRSTNAME));
                String city = cursor.getString(cursor.getColumnIndex(DatabaseHelper.CITY));
                String branch = cursor.getString(cursor.getColumnIndex(DatabaseHelper.BRANCH));
                String imei_no = cursor.getString(cursor.getColumnIndex(DatabaseHelper.IMEI_NO));
                String mobile_no = cursor.getString(cursor.getColumnIndex(DatabaseHelper.MOBILE_NO));
                String datetime = cursor.getString(cursor.getColumnIndex(DatabaseHelper.DATETIME));

                user.setsr(sr);
                user.setemp_code(emp_code);
                user.setusername(username);
                user.setpassword(password);
                user.setfirstname(firstname);
                user.setcity(city);
                user.setbranch(branch);
                user.setimei_no(imei_no);
                user.setmobile_no(mobile_no);
                user.setdatetime(datetime);

                break;
            }
        }
        cursor.close();
        return user;
    }

    /**
     * Get user detail
     * @return
     */
    public String getUserName() {
        String firstname = "";
        SQLiteDatabase db = DatabaseHelper.getInstance().getReadableDatabase();
        Cursor cursor =  db.rawQuery( "select " + DatabaseHelper.FIRSTNAME + " from  " + DatabaseHelper.USER_TABLE_NAME, null );
        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            while(cursor.isAfterLast() == false) {
                firstname = cursor.getString(cursor.getColumnIndex(DatabaseHelper.FIRSTNAME));
                break;
            }
        }
        cursor.close();
        return firstname;
    }

//    public long insertPhotoRecordIfNotExist(String photoId, String photoUrl, String search){
//        boolean isExist = false;
//        ArrayList<PhotoBean> avatars = getAllPhotos(search);
//        for(int i=0; i<avatars.size(); i++) {
//            if(avatars.get(i).getPhotoid().equalsIgnoreCase(search)) {
//                isExist = true;
//                break;
//            }
//        }
//
//        if(!isExist) {
//            return insertPhotoRecord(photoId, photoUrl, search);
//        }
//
//        return 0;
//    }

    /**
     * Update user if exist otherwise insert new user
     * @param user
     */
    public void insertOrUpdate(UserEntity user) {
        String username = user.getusername();
        SQLiteDatabase db = DatabaseHelper.getInstance().getReadableDatabase();
        Cursor cursor =  db.rawQuery( "select * from  " + DatabaseHelper.USER_TABLE_NAME + " where " + DatabaseHelper.USERNAME + " = ? ", new String[] {username});
        if(cursor.getCount() > 0) {
            // Update user
            updateUser(user);
        } else {
            // Insert user
            insertUser(user);
        }
    }

    /**
     * Insert new user
     * @param user
     * @return
     */
    public long insertUser(UserEntity user){
        SQLiteDatabase db = DatabaseHelper.getInstance().getWritableDatabase();
        ContentValues contentValues = getContentValues(user);
        long rowid = db.insert(DatabaseHelper.USER_TABLE_NAME, null, contentValues);

        return rowid;
    }

    /**
     * Update user
     * @param user
     */
    private void updateUser(UserEntity user) {
        SQLiteDatabase db = DatabaseHelper.getInstance().getWritableDatabase();
        ContentValues contentValues = getContentValues(user);
        db.update(DatabaseHelper.USER_TABLE_NAME, contentValues, DatabaseHelper.USERNAME + " = ? ", new String[] {user.getusername()});
    }

    /**
     * Delete User Table
     * @return
     */
    public boolean deleteUserTable() {
        int doneDelete = 0;
        SQLiteDatabase db = DatabaseHelper.getInstance().getReadableDatabase();
        doneDelete = db.delete(DatabaseHelper.USER_TABLE_NAME, null , null);
        return doneDelete > 0;
    }

    private ContentValues getContentValues(UserEntity user) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(DatabaseHelper.SR, user.getsr());
        contentValues.put(DatabaseHelper.EMP_CODE, user.getemp_code());
        contentValues.put(DatabaseHelper.USERNAME, user.getusername());
        contentValues.put(DatabaseHelper.PASSWORD, user.getpassword());
        contentValues.put(DatabaseHelper.FIRSTNAME, user.getfirstname());
        contentValues.put(DatabaseHelper.CITY, user.getcity());
        contentValues.put(DatabaseHelper.BRANCH, user.getbranch());
        contentValues.put(DatabaseHelper.IMEI_NO, user.getimei_no());
        contentValues.put(DatabaseHelper.MOBILE_NO, user.getmobile_no());
        contentValues.put(DatabaseHelper.DATETIME, user.getdatetime());

        return contentValues;
    }

    /**
     * Update user's password
     * @param userName
     * @param password
     */
    public void updateUserPassword(String userName,String password) {
        SQLiteDatabase db = DatabaseHelper.getInstance().getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.PASSWORD, password);
        db.update(DatabaseHelper.USER_TABLE_NAME, contentValues, DatabaseHelper.USERNAME + " = ? ", new String[] {userName});
    }
}
