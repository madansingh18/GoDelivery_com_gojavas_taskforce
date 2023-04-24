package com.gojavas.taskforce.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.gojavas.taskforce.entity.PaymentEntity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by GJS280 on 7/7/2015.
 */
public class PaymentHelper {

    private static PaymentHelper instance = null;

    public static PaymentHelper getInstance() {
        if(instance == null) {
            instance = new PaymentHelper();
        }
        return instance;
    }

    /**
     * Get all payments
     * @return
     */
    public ArrayList<PaymentEntity> getAllPayments() {
        ArrayList<PaymentEntity> arrayList = new ArrayList<>();
        SQLiteDatabase db = DatabaseHelper.getInstance().getReadableDatabase();
        Cursor cursor =  db.rawQuery( "select * from  " + DatabaseHelper.PAYMENT_TABLE_NAME, null);
        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            while(cursor.isAfterLast() == false) {
                PaymentEntity payment = getPaymentEntity(cursor);
                arrayList.add(payment);
                cursor.moveToNext();
            }
        }
        cursor.close();
        return arrayList;
    }

    /**
     * Get payment detail
     * @return
     */
    public PaymentEntity getPaymentDetail(String drs_docket) {
        PaymentEntity payment = null;
        SQLiteDatabase db = DatabaseHelper.getInstance().getReadableDatabase();
        Cursor cursor =  db.rawQuery( "select * from  " + DatabaseHelper.PAYMENT_TABLE_NAME + " where " + DatabaseHelper.DRS_DOCKET + " = ? ", new String[] {drs_docket});
        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            while(cursor.isAfterLast() == false) {
                payment = new PaymentEntity();
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
    public void insertOrUpdate(PaymentEntity payment) {
        insertPayment(payment);
    }

    /**
     * Insert new payment
     * @param payment
     * @return
     */
    public long insertPayment(PaymentEntity payment){
        SQLiteDatabase db = DatabaseHelper.getInstance().getWritableDatabase();
        ContentValues contentValues = getContentValues(payment);
        long rowid = db.insert(DatabaseHelper.PAYMENT_TABLE_NAME, null, contentValues);

        return rowid;
    }

    /**
     * Get payment object
     * @param cursor
     * @return
     */
    private PaymentEntity getPaymentEntity(Cursor cursor) {
        String drsnumber = cursor.getString(cursor.getColumnIndex(DatabaseHelper.DRSNUMBER));
        String docket_number = cursor.getString(cursor.getColumnIndex(DatabaseHelper.DOCKET_NUMBER));
        String drs_docket = cursor.getString(cursor.getColumnIndex(DatabaseHelper.DRS_DOCKET));
        String original_amount_paid = cursor.getString(cursor.getColumnIndex(DatabaseHelper.ORIGINAL_AMOUNT_PAID));
        String transaction_no = cursor.getString(cursor.getColumnIndex(DatabaseHelper.TRANSACTION_NO));
        String auth_code = cursor.getString(cursor.getColumnIndex(DatabaseHelper.AUTH_CODE));
        String card_type = cursor.getString(cursor.getColumnIndex(DatabaseHelper.CARD_TYPE));
        String bank_name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.BANK_NAME));
        String mode_of_payment = cursor.getString(cursor.getColumnIndex(DatabaseHelper.MODE_OF_PAYMENT));

        PaymentEntity payment = new PaymentEntity();
        payment.setdrsnumber(drsnumber);
        payment.setdocket_number(docket_number);
        payment.setdrs_docket(drs_docket);
        payment.setoriginal_amount_paid(original_amount_paid);
        payment.settransaction_no(transaction_no);
        payment.setauth_code(auth_code);
        payment.setcard_type(card_type);
        payment.setbank_name(bank_name);
//        payment.setcustomer_wallet_contact_number(customer_wallet_contact_number);
        payment.setmode_of_payment(mode_of_payment);

        return payment;
    }

    private ContentValues getContentValues(PaymentEntity payment) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(DatabaseHelper.DRSNUMBER, payment.getdrsnumber());
        contentValues.put(DatabaseHelper.DOCKET_NUMBER, payment.getdocket_number());
        contentValues.put(DatabaseHelper.DRS_DOCKET, payment.getdrs_docket());
        contentValues.put(DatabaseHelper.ORIGINAL_AMOUNT_PAID, payment.getoriginal_amount_paid());
        contentValues.put(DatabaseHelper.TRANSACTION_NO, payment.gettransaction_no());
        contentValues.put(DatabaseHelper.AUTH_CODE, payment.getauth_code());
        contentValues.put(DatabaseHelper.CARD_TYPE, payment.getcard_type());
        contentValues.put(DatabaseHelper.BANK_NAME, payment.getbank_name());
        contentValues.put(DatabaseHelper.MODE_OF_PAYMENT, payment.getmode_of_payment());

        return contentValues;
    }

    /**
     * Get all payments
     * @return
     */
    public JSONArray getPaymentsJsonArray() {
        JSONArray jsonArray=new JSONArray();
        SQLiteDatabase db = DatabaseHelper.getInstance().getReadableDatabase();
        Cursor cursor =  db.rawQuery( "select * from  " + DatabaseHelper.PAYMENT_TABLE_NAME, null);
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
            jsonObject.put(DatabaseHelper.ORIGINAL_AMOUNT_PAID,cursor.getString(cursor.getColumnIndex(DatabaseHelper.ORIGINAL_AMOUNT_PAID)));
            jsonObject.put(DatabaseHelper.TRANSACTION_NO,cursor.getString(cursor.getColumnIndex(DatabaseHelper.TRANSACTION_NO)));
            jsonObject.put(DatabaseHelper.AUTH_CODE,cursor.getString(cursor.getColumnIndex(DatabaseHelper.AUTH_CODE)));
            jsonObject.put(DatabaseHelper.CARD_TYPE,cursor.getString(cursor.getColumnIndex(DatabaseHelper.CARD_TYPE)));
            jsonObject.put(DatabaseHelper.BANK_NAME,cursor.getString(cursor.getColumnIndex(DatabaseHelper.BANK_NAME)));
            jsonObject.put(DatabaseHelper.MODE_OF_PAYMENT,cursor.getString(cursor.getColumnIndex(DatabaseHelper.MODE_OF_PAYMENT)));
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
            JSONArray pushDataArray = response.getJSONArray("payment");
            int length = pushDataArray.length();
            for(int i=0; i<length; i++){
                JSONObject jsonObject = pushDataArray.getJSONObject(i);
                String drsnumber = jsonObject.getString(DatabaseHelper.DRSNUMBER);
                String docket_number = jsonObject.getString(DatabaseHelper.DOCKET_NUMBER);
                String drs_docket = jsonObject.getString(DatabaseHelper.DRS_DOCKET);
                String original_amount_paid = jsonObject.getString(DatabaseHelper.ORIGINAL_AMOUNT_PAID);
                String transaction_no = jsonObject.getString(DatabaseHelper.TRANSACTION_NO);
                String auth_code = jsonObject.getString(DatabaseHelper.AUTH_CODE);
                String card_type = jsonObject.getString(DatabaseHelper.CARD_TYPE);
                String bank_name = jsonObject.getString(DatabaseHelper.BANK_NAME);
                String mode_of_payment = jsonObject.getString(DatabaseHelper.MODE_OF_PAYMENT);

                PaymentEntity payment = new PaymentEntity();
                payment.setdrsnumber(drsnumber);
                payment.setdocket_number(docket_number);
                payment.setdrs_docket(drs_docket);
                payment.setoriginal_amount_paid(original_amount_paid);
                payment.settransaction_no(transaction_no);
                payment.setauth_code(auth_code);
                payment.setcard_type(card_type);
                payment.setbank_name(bank_name);
//                payment.setcustomer_wallet_contact_number(customer_wallet_contact_number);
                payment.setmode_of_payment(mode_of_payment);

                PaymentHelper.getInstance().insertOrUpdate(payment);
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
        doneDelete = db.delete(DatabaseHelper.PAYMENT_TABLE_NAME, null , null);
        db.close();
        return doneDelete > 0;
    }
}
