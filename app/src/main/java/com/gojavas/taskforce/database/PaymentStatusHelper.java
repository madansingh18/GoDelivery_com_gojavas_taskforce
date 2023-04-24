package com.gojavas.taskforce.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.gojavas.taskforce.entity.PaymentStatusEntity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by GJS280 on 26/10/2015.
 */
public class PaymentStatusHelper {

    private static PaymentStatusHelper instance = null;

    public static PaymentStatusHelper getInstance() {
        if(instance == null) {
            instance = new PaymentStatusHelper();
        }
        return instance;
    }

    /**
     * Get payment detail
     * @return
     */
    public PaymentStatusEntity getPaymentStatusDetail(String drs_docket) {
        PaymentStatusEntity payment = null;
        SQLiteDatabase db = DatabaseHelper.getInstance().getReadableDatabase();
        Cursor cursor =  db.rawQuery( "select * from  " + DatabaseHelper.PAYMENT_STATUS_TABLE_NAME + " where " + DatabaseHelper.DRS_DOCKET + " = ? ", new String[] {drs_docket});
        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            while(cursor.isAfterLast() == false) {
                payment = new PaymentStatusEntity();
                payment = getPaymentEntity(cursor);
                cursor.moveToNext();
                break;
            }
        }
        cursor.close();
        return payment;
    }

    /**
     * Insert new payment
     * @param payment
     */
    public void insertOrUpdate(PaymentStatusEntity payment) {
        insertPayment(payment);
    }

    /**
     * Insert new payment
     * @param payment
     * @return
     */
    public long insertPayment(PaymentStatusEntity payment){
        SQLiteDatabase db = DatabaseHelper.getInstance().getWritableDatabase();
        ContentValues contentValues = getContentValues(payment);
        long rowid = db.insert(DatabaseHelper.PAYMENT_STATUS_TABLE_NAME, null, contentValues);

        return rowid;
    }

    /**
     * Get payment object
     * @param cursor
     * @return
     */
    private PaymentStatusEntity getPaymentEntity(Cursor cursor) {
        String drsnumber = cursor.getString(cursor.getColumnIndex(DatabaseHelper.DRSNUMBER));
        String docket_number = cursor.getString(cursor.getColumnIndex(DatabaseHelper.DOCKET_NUMBER));
        String drs_docket = cursor.getString(cursor.getColumnIndex(DatabaseHelper.DRS_DOCKET));
        String device_type = cursor.getString(cursor.getColumnIndex(DatabaseHelper.DEVICE_TYPE));
        String payment_status = cursor.getString(cursor.getColumnIndex(DatabaseHelper.PAYMENT_STATUS));

        PaymentStatusEntity payment = new PaymentStatusEntity();
        payment.setdrsnumber(drsnumber);
        payment.setdocket_number(docket_number);
        payment.setdrs_docket(drs_docket);
        payment.setdevice_type(device_type);
        payment.setpayment_status(payment_status);

        return payment;
    }

    /**
     * Update payment status
     */
    public void updatePaymentStatus(String drs_docket, String paymentStatus) {
        SQLiteDatabase db = DatabaseHelper.getInstance().getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.PAYMENT_STATUS, paymentStatus);
        db.update(DatabaseHelper.PAYMENT_STATUS_TABLE_NAME, contentValues, DatabaseHelper.DRS_DOCKET + " = ? ", new String[]{drs_docket});
    }

    private ContentValues getContentValues(PaymentStatusEntity payment) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(DatabaseHelper.DRSNUMBER, payment.getdrsnumber());
        contentValues.put(DatabaseHelper.DOCKET_NUMBER, payment.getdocket_number());
        contentValues.put(DatabaseHelper.DRS_DOCKET, payment.getdrs_docket());
        contentValues.put(DatabaseHelper.DEVICE_TYPE, payment.getdevice_type());
        contentValues.put(DatabaseHelper.PAYMENT_STATUS, payment.getpayment_status());

        return contentValues;
    }

    /**
     * Get all payments
     * @return
     */
    public JSONArray getPaymentsJsonArray() {
        JSONArray jsonArray=new JSONArray();
        SQLiteDatabase db = DatabaseHelper.getInstance().getReadableDatabase();
        Cursor cursor =  db.rawQuery( "select * from  " + DatabaseHelper.PAYMENT_STATUS_TABLE_NAME, null);
        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            while(cursor.isAfterLast() == false) {
                JSONObject track = getPaymentJsonObject(cursor);
                jsonArray.put(track);
                cursor.moveToNext();
            }
        }
        cursor.close();
        return jsonArray;
    }

    /**
     * Get payment jsonobject
     * @param cursor
     * @return
     */
    private JSONObject getPaymentJsonObject(Cursor cursor) {
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put(DatabaseHelper.DRSNUMBER, cursor.getString(cursor.getColumnIndex(DatabaseHelper.DRSNUMBER)));
            jsonObject.put(DatabaseHelper.DOCKET_NUMBER,cursor.getString(cursor.getColumnIndex(DatabaseHelper.DOCKET_NUMBER)));
            jsonObject.put(DatabaseHelper.DRS_DOCKET,cursor.getString(cursor.getColumnIndex(DatabaseHelper.DRS_DOCKET)));
            jsonObject.put(DatabaseHelper.DEVICE_TYPE,cursor.getString(cursor.getColumnIndex(DatabaseHelper.DEVICE_TYPE)));
            jsonObject.put(DatabaseHelper.PAYMENT_STATUS,cursor.getString(cursor.getColumnIndex(DatabaseHelper.PAYMENT_STATUS)));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    /**
     * Insert payment data in database
     * @param response
     */
    public void insertJsonInDatabase(JSONObject response) {
        try {
            JSONArray pushDataArray = response.getJSONArray("payment_status");
            int length = pushDataArray.length();
            for(int i=0; i<length; i++){
                JSONObject jsonObject = pushDataArray.getJSONObject(i);
                String drsnumber = jsonObject.getString(DatabaseHelper.DRSNUMBER);
                String docket_number = jsonObject.getString(DatabaseHelper.DOCKET_NUMBER);
                String drs_docket = jsonObject.getString(DatabaseHelper.DRS_DOCKET);
                String device_type = jsonObject.getString(DatabaseHelper.DEVICE_TYPE);
                String payment_status = jsonObject.getString(DatabaseHelper.PAYMENT_STATUS);

                PaymentStatusEntity payment = new PaymentStatusEntity();
                payment.setdrsnumber(drsnumber);
                payment.setdocket_number(docket_number);
                payment.setdrs_docket(drs_docket);
                payment.setdevice_type(device_type);
                payment.setpayment_status(payment_status);

                PaymentStatusHelper.getInstance().insertOrUpdate(payment);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Delete Payment table
     * @return
     */
    public boolean deletePaymentTable() {
        int doneDelete = 0;
        SQLiteDatabase db = DatabaseHelper.getInstance().getReadableDatabase();
        doneDelete = db.delete(DatabaseHelper.PAYMENT_STATUS_TABLE_NAME, null , null);
        db.close();
        return doneDelete > 0;
    }
}
