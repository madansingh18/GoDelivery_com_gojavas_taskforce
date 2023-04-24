package com.gojavas.taskforce.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.gojavas.taskforce.entity.DeliveryEntity;
import com.gojavas.taskforce.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by GJS280 on 16/4/2015.
 */
public class DeliveryHelper {

    // Delivery table fields
    public static final String CUST_NAME = "Customer Name";
    public static final String CUST_ADDRESS1 = "Customer Address";
    public static final String CUST_ADDRESS2 = "cust_address2";
    public static final String PINCODE = "PinCode";
    public static final String STATE = "State";
    public static final String LANDMARK = "LandMark";
    public static final String CUST_CONTACT1 = "Customer Contact";
    public static final String ALTERNATE_NO = "alternate_no";
    public static final String PACKAGESNO = "No of Packages";
    public static final String DRSNUMBER = "Runsheet Number";
    public static final String TOTALDOCKETSINDRS = "Total Dockets In Runsheet";
    public static final String PRODCUTDESC = "Prodcut Desc";
    public static final String DOCKET_NUMBER = "Docket Number";
    public static final String CHOICE_OF_PAYMENT = "Mode of Payment";
    public static final String AMOUNT_TOBE_PAID = "Original Amount";
    public static final String DUE_DATE = "Due Date";
    public static final String BOY_ID = "Boy Id";
    public static final String BOY_NAME = "Boy Name";
    public static final String REASON_ID = "Failed Reason ID";
    public static final String FAILED_REASON = "Failed Reason";
    public static final String DELIVERY_TIME = "Delivery Time";
    public static final String COMMENTS = "Comments";
    public static final String MODE_OF_PAYMENT = "Payment";
    public static final String TRANSACTION_NO = "Transaction No";
    public static final String AUTH_CODE = "Auth Code";
    public static final String CARD_TYPE = "Card Type";
    public static final String BANK_NAME = "Bank Name";
    public static final String CUSTOMER_SIGN = "Signature";
    public static final String CUSTOMER_IMG = "Photo1";
    public static final String CUSTOMER_IMG2 = "Photo2";
    public static final String CUSTOMER_IMG3 = "Photo3";
    public static final String HAPPY_DELIVERY = "Happy Delivery";
    public static final String HAPPY_DELIVERY_IMG = "Happy Delivery Image";
    public static final String SKU_DETAILS = "Sku Details";
    public static final String LAST_UPDATED = "Last Updated";
    public static final String PREFERRED_TRANSACTION_TIME = "Preferred Transaction Time";
    public static final String ORIGINAL_AMOUNT_PAID = "Actual Amount";
    public static final String USER_ID = "User Id";
    public static final String DELIVERED_TO = "Delivered To";
    public static final String DELIVERED_TO_RELATION = "Delivered To Relation";
    public static final String PRIRORITY_DELIVERY = "Prirority Delivery";
    public static final String CLOSING_KM = "closing_km";
    public static final String IMEI_NUMBER = "imei_number";
    public static final String SYNC_UPDATE = "sync_update";
    public static final String UPDATED_ON_ANDROID = "updated_on_android";
    public static final String UPDATED_ON_WEBX = "updated_on_webx";
    public static final String LOC_ID = "loc_id";
    public static final String C_ID = "c_id";
    public static final String ADMIN_ID = "admin_id";
    public static final String EXTRA1 = "Extra Item Picked";
    public static final String EXTRA2 = "extra2";
    public static final String EXTRA3 = "extra3";
    public static final String EXTRA4 = "extra4";
    public static final String EXTRA5 = "extra5";
    public static final String EXTRA6 = "extra6";
    public static final String SYNC_FLAG = "sync_flag";
    public static final String LAST_LAT = "last_lat";
    public static final String LAST_LNG = "last_lng";
    public static final String LAST_ATTEMPT_STATUS = "last_attempt_status";
    public static final String ATTEMPT_NUMBER = "attempt_number";
    public static final String ATTEMPT_SLOT = "attempt_slot";
    public static final String NPS_SCORE = "NPS Score";
    public static final String SEQ_PREDICTED = "seq_predicted";
    public static final String SEQ_SELECTED = "seq_selected";
    public static final String SEQ_TRANSACTION = "seq_transaction";
    public static final String USER_COMMENTS = "user_comments";
    public static final String BATTERY_LEVEL = "battery_level";
    public static final String TIME_ERP_IMPORT_INITIATED = "time_erp_import_initiated";
    public static final String TIME_ERP_IMPORT_COMPLETED = "time_erp_import_completed";
    public static final String TIME_AVAILABLE_ANDROID_INITIATED = "time_available_android_initiated";
    public static final String TIME_AVAILABLE_ANDROID_COMPLETED = "time_available_android_completed";
    public static final String REVERSE_DOCKET_NUMBER = "Reverse Docket Number";
    public static final String DRS_DOCKET = "drs_docket";
    public static final String JOBTYPE = "JobType";
    public static final String CITY = "City";
    public static final String ORDERNUMBER ="Order No." ;
    public static final String STATUS ="Status" ;
    public static final String LATITUDE ="Latitude" ;
    public static final String LONGITUDE ="Logitude" ;
    public static final String SYNC ="Sync" ;
    public static final String CLIENT_CODE ="Clinet Code" ;
    public static String ClIENT_NAME = "Client Name";
    public static String ACTUAL_WT = "Weight";
    public static final String PRODUCT_DESCRIPTION = "Product Description";
    public static final String REASON_FOR_RETURN = "Reason For Return";
    public static final String RETURN_REQUEST_ID = "Return Request ID";
    public static final String TP_CODE = "TP Code";
    public static final String RETURN_PINCODE = "Return Pincode";
    public static final String RECEIPT_URL = "Receipt Url";

    private static DeliveryHelper instance = null;

    public static DeliveryHelper getInstance() {
        if(instance == null) {
            instance = new DeliveryHelper();
        }
        return instance;
    }

    /**
     * Get delivery detail
     * @return
     */
    public DeliveryEntity getDeliveryDetail(String drs_docket) {
        DeliveryEntity delivery = new DeliveryEntity();
        SQLiteDatabase db = DatabaseHelper.getInstance().getReadableDatabase();
        Cursor cursor =  db.rawQuery( "select * from  " + DatabaseHelper.DELIVERY_TABLE_NAME + " where " + DatabaseHelper.DRS_DOCKET + " = ? ", new String[] {drs_docket});
        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            while(cursor.isAfterLast() == false) {
                delivery = getDeliveryEntity(cursor);
                cursor.moveToNext();
                break;
            }
        }
        cursor.close();
        return delivery;
    }

    /**
     * Get delivery detail
     * @return
     */
    public boolean deliveryExist(String drs_docket) {
        SQLiteDatabase db = DatabaseHelper.getInstance().getReadableDatabase();
        Cursor cursor =  db.rawQuery( "select * from  " + DatabaseHelper.DELIVERY_TABLE_NAME + " where " + DatabaseHelper.DRS_DOCKET + " = ? ", new String[] {drs_docket});
        if(cursor.getCount() > 0) {
            cursor.close();
            return true;
        }
        cursor.close();
        return false;
    }

    /**
     * Get all deliveries
     * @return
     */
    public ArrayList<DeliveryEntity> getAllDeliveries() {
        ArrayList<DeliveryEntity> arrayList = new ArrayList<>();
        SQLiteDatabase db = DatabaseHelper.getInstance().getReadableDatabase();
        Cursor cursor =  db.rawQuery( "select * from  " + DatabaseHelper.DELIVERY_TABLE_NAME, null);
        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            while(cursor.isAfterLast() == false) {
                DeliveryEntity delivery = getDeliveryEntity(cursor);
                arrayList.add(delivery);
                cursor.moveToNext();
            }
        }
        cursor.close();
        return arrayList;
    }

    /**
     * Get all success/Fail deliveries
     * @return
     */
    public ArrayList<DeliveryEntity> getAllSuccessFailDeliveries(String status) {
        ArrayList<DeliveryEntity> arrayList = new ArrayList<>();
        SQLiteDatabase db = DatabaseHelper.getInstance().getReadableDatabase();
        Cursor cursor =  db.rawQuery( "select * from  " + DatabaseHelper.DELIVERY_TABLE_NAME + " where " + DatabaseHelper.STATUS + " = ? ", new String[] {status});
        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            while(cursor.isAfterLast() == false) {
                DeliveryEntity delivery = getDeliveryEntity(cursor);
                arrayList.add(delivery);
                cursor.moveToNext();
            }
        }
        cursor.close();
        return arrayList;
    }

    /**
     * Get the delivery object
     * @param cursor
     * @return
     */
    private DeliveryEntity getDeliveryEntity(Cursor cursor) {
        DeliveryEntity delivery = new DeliveryEntity();
        String id = cursor.getString(cursor.getColumnIndex(DatabaseHelper.ID));
        String jobtype = cursor.getString(cursor.getColumnIndex(DatabaseHelper.JOBTYPE));
        String cust_name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.CUST_NAME));
        String cust_address1 = cursor.getString(cursor.getColumnIndex(DatabaseHelper.CUST_ADDRESS1));
        String cust_address2 = cursor.getString(cursor.getColumnIndex(DatabaseHelper.CUST_ADDRESS2));
        String pincode = cursor.getString(cursor.getColumnIndex(DatabaseHelper.PINCODE));
        String city = cursor.getString(cursor.getColumnIndex(DatabaseHelper.CITY));
        String state = cursor.getString(cursor.getColumnIndex(DatabaseHelper.STATE));
        String landmark = cursor.getString(cursor.getColumnIndex(DatabaseHelper.LANDMARK));
        String cust_contact1 = cursor.getString(cursor.getColumnIndex(DatabaseHelper.CUST_CONTACT1));
        String alternate_no = cursor.getString(cursor.getColumnIndex(DatabaseHelper.ALTERNATE_NO));
        String orderNumber = cursor.getString(cursor.getColumnIndex(DatabaseHelper.ORDERNUMBER));
        String packagesNo = cursor.getString(cursor.getColumnIndex(DatabaseHelper.PACKAGESNO));
        String DRSNumber = cursor.getString(cursor.getColumnIndex(DatabaseHelper.DRSNUMBER));
        String TotalDocketsInDRS = cursor.getString(cursor.getColumnIndex(DatabaseHelper.TOTALDOCKETSINDRS));
        String ProdcutDesc = cursor.getString(cursor.getColumnIndex(DatabaseHelper.PRODCUTDESC));
        String docket_number = cursor.getString(cursor.getColumnIndex(DatabaseHelper.DOCKET_NUMBER));
        String choice_of_payment = cursor.getString(cursor.getColumnIndex(DatabaseHelper.CHOICE_OF_PAYMENT));
        String amount_tobe_paid = cursor.getString(cursor.getColumnIndex(DatabaseHelper.AMOUNT_TOBE_PAID));
        String due_date = cursor.getString(cursor.getColumnIndex(DatabaseHelper.DUE_DATE));
        String boy_id = cursor.getString(cursor.getColumnIndex(DatabaseHelper.BOY_ID));
        String boy_name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.BOY_NAME));
        String status = cursor.getString(cursor.getColumnIndex(DatabaseHelper.STATUS));
        String reason_id = cursor.getString(cursor.getColumnIndex(DatabaseHelper.REASON_ID));
        String failed_reason = cursor.getString(cursor.getColumnIndex(DatabaseHelper.FAILED_REASON));
        String delivery_time = cursor.getString(cursor.getColumnIndex(DatabaseHelper.DELIVERY_TIME));
        String comments = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COMMENTS));
        String mode_of_payment = cursor.getString(cursor.getColumnIndex(DatabaseHelper.MODE_OF_PAYMENT));
        String transaction_no = cursor.getString(cursor.getColumnIndex(DatabaseHelper.TRANSACTION_NO));
        String auth_code = cursor.getString(cursor.getColumnIndex(DatabaseHelper.AUTH_CODE));
        String card_type = cursor.getString(cursor.getColumnIndex(DatabaseHelper.CARD_TYPE));
        String bank_name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.BANK_NAME));
        String customer_sign = cursor.getString(cursor.getColumnIndex(DatabaseHelper.CUSTOMER_SIGN));
        String customer_img = cursor.getString(cursor.getColumnIndex(DatabaseHelper.CUSTOMER_IMG));
        String customer_img2 = cursor.getString(cursor.getColumnIndex(DatabaseHelper.CUSTOMER_IMG2));
        String customer_img3 = cursor.getString(cursor.getColumnIndex(DatabaseHelper.CUSTOMER_IMG3));
        String happy_delivery = cursor.getString(cursor.getColumnIndex(DatabaseHelper.HAPPY_DELIVERY));
        String happy_delivery_img = cursor.getString(cursor.getColumnIndex(DatabaseHelper.HAPPY_DELIVERY_IMG));
        String latitude = cursor.getString(cursor.getColumnIndex(DatabaseHelper.LATITUDE));
        String longitude = cursor.getString(cursor.getColumnIndex(DatabaseHelper.LONGITUDE));
        String sku_details = cursor.getString(cursor.getColumnIndex(DatabaseHelper.SKU_DETAILS));
        String last_updated = cursor.getString(cursor.getColumnIndex(DatabaseHelper.LAST_UPDATED));
        String preferred_transaction_time = cursor.getString(cursor.getColumnIndex(DatabaseHelper.PREFERRED_TRANSACTION_TIME));
        String original_amount_paid = cursor.getString(cursor.getColumnIndex(DatabaseHelper.ORIGINAL_AMOUNT_PAID));
        String user_id = cursor.getString(cursor.getColumnIndex(DatabaseHelper.USER_ID));
        String delivered_to = cursor.getString(cursor.getColumnIndex(DatabaseHelper.DELIVERED_TO));
        String delivered_to_relation = cursor.getString(cursor.getColumnIndex(DatabaseHelper.DELIVERED_TO_RELATION));
        String prirority_delivery = cursor.getString(cursor.getColumnIndex(DatabaseHelper.PRIRORITY_DELIVERY));
        String closing_km = cursor.getString(cursor.getColumnIndex(DatabaseHelper.CLOSING_KM));
        String imei_number = cursor.getString(cursor.getColumnIndex(DatabaseHelper.IMEI_NUMBER));
        String sync_update = cursor.getString(cursor.getColumnIndex(DatabaseHelper.SYNC_UPDATE));
        String updated_on_android = cursor.getString(cursor.getColumnIndex(DatabaseHelper.UPDATED_ON_ANDROID));
        String updated_on_webx = cursor.getString(cursor.getColumnIndex(DatabaseHelper.UPDATED_ON_WEBX));
        String loc_id = cursor.getString(cursor.getColumnIndex(DatabaseHelper.LOC_ID));
        String c_id = cursor.getString(cursor.getColumnIndex(DatabaseHelper.C_ID));
        String admin_id = cursor.getString(cursor.getColumnIndex(DatabaseHelper.ADMIN_ID));
        String extra1 = cursor.getString(cursor.getColumnIndex(DatabaseHelper.EXTRA1));
        String extra2 = cursor.getString(cursor.getColumnIndex(DatabaseHelper.EXTRA2));
        String extra3 = cursor.getString(cursor.getColumnIndex(DatabaseHelper.EXTRA3));
        String extra4 = cursor.getString(cursor.getColumnIndex(DatabaseHelper.EXTRA4));
        String extra5 = cursor.getString(cursor.getColumnIndex(DatabaseHelper.EXTRA5));
        String extra6 = cursor.getString(cursor.getColumnIndex(DatabaseHelper.EXTRA6));
        String sync_flag = cursor.getString(cursor.getColumnIndex(DatabaseHelper.SYNC_FLAG));
        String last_lat = cursor.getString(cursor.getColumnIndex(DatabaseHelper.LAST_LAT));
        String last_lng = cursor.getString(cursor.getColumnIndex(DatabaseHelper.LAST_LNG));
        String last_attempt_status = cursor.getString(cursor.getColumnIndex(DatabaseHelper.LAST_ATTEMPT_STATUS));
        String attempt_number = cursor.getString(cursor.getColumnIndex(DatabaseHelper.ATTEMPT_NUMBER));
        String attempt_slot = cursor.getString(cursor.getColumnIndex(DatabaseHelper.ATTEMPT_SLOT));
        String nps_score = cursor.getString(cursor.getColumnIndex(DatabaseHelper.NPS_SCORE));
        String seq_predicted = cursor.getString(cursor.getColumnIndex(DatabaseHelper.SEQ_PREDICTED));
        String seq_selected = cursor.getString(cursor.getColumnIndex(DatabaseHelper.SEQ_SELECTED));
        String seq_transaction = cursor.getString(cursor.getColumnIndex(DatabaseHelper.SEQ_TRANSACTION));
        String user_comments = cursor.getString(cursor.getColumnIndex(DatabaseHelper.USER_COMMENTS));
        String battery_level = cursor.getString(cursor.getColumnIndex(DatabaseHelper.BATTERY_LEVEL));
        String time_erp_import_initiated = cursor.getString(cursor.getColumnIndex(DatabaseHelper.TIME_ERP_IMPORT_INITIATED));
        String time_erp_import_completed = cursor.getString(cursor.getColumnIndex(DatabaseHelper.TIME_ERP_IMPORT_COMPLETED));
        String time_available_android_initiated = cursor.getString(cursor.getColumnIndex(DatabaseHelper.TIME_AVAILABLE_ANDROID_INITIATED));
        String time_available_android_completed = cursor.getString(cursor.getColumnIndex(DatabaseHelper.TIME_AVAILABLE_ANDROID_COMPLETED));
        String reverse_docket_number = cursor.getString(cursor.getColumnIndex(DatabaseHelper.REVERSE_DOCKET_NUMBER));
        String drs_docket = cursor.getString(cursor.getColumnIndex(DatabaseHelper.DRS_DOCKET));
        String sync = cursor.getString(cursor.getColumnIndex(DatabaseHelper.SYNC));
        String product_description = cursor.getString(cursor.getColumnIndex(DatabaseHelper.PRODUCT_DESCRIPTION));
        String reason_for_return = cursor.getString(cursor.getColumnIndex(DatabaseHelper.REASON_FOR_RETURN));
        String return_request_id = cursor.getString(cursor.getColumnIndex(DatabaseHelper.RETURN_REQUEST_ID));
        String tp_code = cursor.getString(cursor.getColumnIndex(DatabaseHelper.TP_CODE));
        String return_pincode = cursor.getString(cursor.getColumnIndex(DatabaseHelper.RETURN_PINCODE));
        String receipt_url = cursor.getString(cursor.getColumnIndex(DatabaseHelper.RECEIPT_URL));

        delivery.setid(id);
        delivery.setjobtype(jobtype);
        delivery.setcust_name(cust_name);
        delivery.setcust_address1(cust_address1);
        delivery.setcust_address2(cust_address2);
        delivery.setpincode(pincode);
        delivery.setcity(city);
        delivery.setstate(state);
        delivery.setlandmark(landmark);
        delivery.setcust_contact1(cust_contact1);
        delivery.setalternate_no(alternate_no);
        delivery.setorderNumber(orderNumber);
        delivery.setpackagesNo(packagesNo);
        delivery.setDRSNumber(DRSNumber);
        delivery.setTotalDocketsInDRS(TotalDocketsInDRS);
        delivery.setProdcutDesc(ProdcutDesc);
        delivery.setdocket_number(docket_number);
        delivery.setchoice_of_payment(choice_of_payment);
        delivery.setamount_tobe_paid(amount_tobe_paid);
        delivery.setdue_date(due_date);
        delivery.setboy_id(boy_id);
        delivery.setboy_name(boy_name);
        delivery.setstatus(status);
        delivery.setreason_id(reason_id);
        delivery.setstatus(failed_reason);
        delivery.setdelivery_time(delivery_time);
        delivery.setcomments(comments);
        delivery.setmode_of_payment(mode_of_payment);
        delivery.settransaction_no(transaction_no);
        delivery.setauth_code(auth_code);
        delivery.setcard_type(card_type);
        delivery.setbank_name(bank_name);
//        delivery.setcustomer_wallet_contact_number(customer_wallet_mobile_number);
        delivery.setcustomer_sign(customer_sign);
        delivery.setcustomer_img(customer_img);
        delivery.setcustomer_img2(customer_img2);
        delivery.setcustomer_img3(customer_img3);
        delivery.sethappy_delivery(happy_delivery);
        delivery.sethappy_delivery_img(happy_delivery_img);
        delivery.setlatitude(latitude);
        delivery.setlongitude(longitude);
        delivery.setsku_details(sku_details);
        delivery.setlast_updated(last_updated);
        delivery.setpreferred_transaction_time(preferred_transaction_time);
        delivery.setoriginal_amount_paid(original_amount_paid);
        delivery.setuser_id(user_id);
        delivery.setdelivered_to(delivered_to);
        delivery.setdelivered_to_relation(delivered_to_relation);
        delivery.setprirority_delivery(prirority_delivery);
        delivery.setclosing_km(closing_km);
        delivery.setimei_number(imei_number);
        delivery.setsync_update(sync_update);
        delivery.setupdated_on_android(updated_on_android);
        delivery.setupdated_on_webx(updated_on_webx);
        delivery.setloc_id(loc_id);
        delivery.setc_id(c_id);
        delivery.setadmin_id(admin_id);
        delivery.setextra1(extra1);
        delivery.setextra2(extra2);
        delivery.setextra3(extra3);
        delivery.setextra4(extra4);
        delivery.setextra5(extra5);
        delivery.setextra6(extra6);
        delivery.setsync_flag(sync_flag);
        delivery.setlast_lat(last_lat);
        delivery.setlast_lng(last_lng);
        delivery.setlast_attempt_status(last_attempt_status);
        delivery.setattempt_number(attempt_number);
        delivery.setattempt_slot(attempt_slot);
        delivery.setnps_score(nps_score);
        delivery.setseq_predicted(seq_predicted);
        delivery.setseq_selected(seq_selected);
        delivery.setseq_transaction(seq_transaction);
        delivery.setuser_comments(user_comments);
        delivery.setbattery_level(battery_level);
        delivery.settime_erp_import_initiated(time_erp_import_initiated);
        delivery.settime_erp_import_completed(time_erp_import_completed);
        delivery.settime_available_android_initiated(time_available_android_initiated);
        delivery.settime_available_android_completed(time_available_android_completed);
        delivery.setreverse_docket_number(reverse_docket_number);
        delivery.setdrs_docket(drs_docket);
        delivery.setsync(sync);
        delivery.setproduct_description(product_description);
        delivery.setreason_for_return(reason_for_return);
        delivery.setreturn_request_id(return_request_id);
        delivery.settp_code(tp_code);
        delivery.setreturn_pincode(return_pincode);
        delivery.setreceipt_url(receipt_url);

        return delivery;
    }

    /**
     * Update delivery if exist otherwise insert new delivery
     * @param delivery
     */
    public void insertOrUpdate(DeliveryEntity delivery) {
        String drs_docket = delivery.getdrs_docket();
        SQLiteDatabase db = DatabaseHelper.getInstance().getReadableDatabase();
        Cursor cursor =  db.rawQuery( "select " + DatabaseHelper.DRS_DOCKET + " from  " + DatabaseHelper.DELIVERY_TABLE_NAME + " where " + DatabaseHelper.DRS_DOCKET + " = ? ", new String[] {drs_docket});
        if(cursor.getCount() > 0) {
            // Update delivery
            updateDelivery(delivery);
        } else {
            // Insert delivery
            insertDelivery(delivery);
        }
    }

    /**
     * Insert new delivery
     * @param delivery
     * @return
     */
    private long insertDelivery(DeliveryEntity delivery){
        SQLiteDatabase db = DatabaseHelper.getInstance().getWritableDatabase();
        ContentValues contentValues = getContentValues(delivery);
        long rowid = db.insert(DatabaseHelper.DELIVERY_TABLE_NAME, null, contentValues);
        return rowid;
    }

    /**
     * Update delivery
     * @param delivery
     */
    private void updateDelivery(DeliveryEntity delivery) {
        SQLiteDatabase db = DatabaseHelper.getInstance().getWritableDatabase();
        ContentValues contentValues = getContentValues(delivery);
        db.update(DatabaseHelper.DELIVERY_TABLE_NAME, contentValues, DatabaseHelper.DRS_DOCKET + " = ? ", new String[]{delivery.getdrs_docket()});
    }

//    /**
//     * Update delivery sync
//     * @param drs_docket
//     */
//    public void updateDeliverySync(String drs_docket) {
//        SQLiteDatabase db = DatabaseHelper.getInstance().getWritableDatabase();
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(DatabaseHelper.SYNC, "1");
//        db.update(DatabaseHelper.DELIVERY_TABLE_NAME, contentValues, DatabaseHelper.DRS_DOCKET + " = ? ", new String[] {drs_docket});
//    }

    public void updateDeliverySync() {
        SQLiteDatabase db = DatabaseHelper.getInstance().getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.SYNC, "1");
        db.update(DatabaseHelper.DELIVERY_TABLE_NAME, contentValues, DatabaseHelper.SYNC + " = ? ", new String[] {"0"});
    }

    public void updateDeliverySync(String drs_docket) {
        SQLiteDatabase db = DatabaseHelper.getInstance().getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.SYNC, "1");
        db.update(DatabaseHelper.DELIVERY_TABLE_NAME, contentValues, DatabaseHelper.DRS_DOCKET + " = ? ", new String[]{drs_docket});
    }

    /**
     * Check whether this reverse docket number already exist or not
     * @param reverseDocketNo
     * @return
     */
    public boolean checkReverseDocketNumberExist(String reverseDocketNo) {
        SQLiteDatabase db = DatabaseHelper.getInstance().getReadableDatabase();
        Cursor cursor =  db.rawQuery( "select * from  " + DatabaseHelper.DELIVERY_TABLE_NAME + " where " + DatabaseHelper.REVERSE_DOCKET_NUMBER + " = ? ", new String[] {reverseDocketNo});
        if(cursor.getCount() > 0) {
            return true;
        }
        cursor.close();
        return false;
    }

    public HashMap<String,String> getDeliveryDetailHashMap(String drs_docket) {
        HashMap<String,String> delivery = new HashMap<>();
        SQLiteDatabase db = DatabaseHelper.getInstance().getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT delivery.*, drs.client_name, drs.actuwt  ,drs.client_code FROM delivery LEFT JOIN drs ON delivery.drs_docket = drs.drs_docket WHERE drs.drs_docket = ?", new String[] {drs_docket});
        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            while(cursor.isAfterLast() == false) {
                delivery = getDeliveryHashMAp(cursor);
                cursor.moveToNext();
                break;
            }
        }
        cursor.close();
        return delivery;
    }

    private HashMap<String,String> getDeliveryHashMAp(Cursor cursor) {
        HashMap<String,String> delivery = new HashMap<>();
        String id = cursor.getString(cursor.getColumnIndex(DatabaseHelper.ID));
        String jobtype = cursor.getString(cursor.getColumnIndex(DatabaseHelper.JOBTYPE));
        String cust_name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.CUST_NAME));
        String cust_address1 = cursor.getString(cursor.getColumnIndex(DatabaseHelper.CUST_ADDRESS1));
        String cust_address2 = cursor.getString(cursor.getColumnIndex(DatabaseHelper.CUST_ADDRESS2));
        String pincode = cursor.getString(cursor.getColumnIndex(DatabaseHelper.PINCODE));
        String city = cursor.getString(cursor.getColumnIndex(DatabaseHelper.CITY));
        String state = cursor.getString(cursor.getColumnIndex(DatabaseHelper.STATE));
        String landmark = cursor.getString(cursor.getColumnIndex(DatabaseHelper.LANDMARK));
        String cust_contact1 = cursor.getString(cursor.getColumnIndex(DatabaseHelper.CUST_CONTACT1));
        String alternate_no = cursor.getString(cursor.getColumnIndex(DatabaseHelper.ALTERNATE_NO));
        String orderNumber = cursor.getString(cursor.getColumnIndex(DatabaseHelper.ORDERNUMBER));
        String packagesNo = cursor.getString(cursor.getColumnIndex(DatabaseHelper.PACKAGESNO));
        String DRSNumber = cursor.getString(cursor.getColumnIndex(DatabaseHelper.DRSNUMBER));
        String TotalDocketsInDRS = cursor.getString(cursor.getColumnIndex(DatabaseHelper.TOTALDOCKETSINDRS));
        String ProdcutDesc = cursor.getString(cursor.getColumnIndex(DatabaseHelper.PRODCUTDESC));
        String docket_number = cursor.getString(cursor.getColumnIndex(DatabaseHelper.DOCKET_NUMBER));
        String choice_of_payment = cursor.getString(cursor.getColumnIndex(DatabaseHelper.CHOICE_OF_PAYMENT));
        String amount_tobe_paid = cursor.getString(cursor.getColumnIndex(DatabaseHelper.AMOUNT_TOBE_PAID));
        String due_date = cursor.getString(cursor.getColumnIndex(DatabaseHelper.DUE_DATE));
        String boy_id = cursor.getString(cursor.getColumnIndex(DatabaseHelper.BOY_ID));
        String boy_name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.BOY_NAME));
        String status = cursor.getString(cursor.getColumnIndex(DatabaseHelper.STATUS));
        String reason_id = cursor.getString(cursor.getColumnIndex(DatabaseHelper.REASON_ID));
        String failed_reason = cursor.getString(cursor.getColumnIndex(DatabaseHelper.FAILED_REASON));
        String delivery_time = cursor.getString(cursor.getColumnIndex(DatabaseHelper.DELIVERY_TIME));
        String comments = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COMMENTS));
        String mode_of_payment = cursor.getString(cursor.getColumnIndex(DatabaseHelper.MODE_OF_PAYMENT));
        String transaction_no = cursor.getString(cursor.getColumnIndex(DatabaseHelper.TRANSACTION_NO));
        String auth_code = cursor.getString(cursor.getColumnIndex(DatabaseHelper.AUTH_CODE));
        String card_type = cursor.getString(cursor.getColumnIndex(DatabaseHelper.CARD_TYPE));
        String bank_name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.BANK_NAME));
        String customer_sign = cursor.getString(cursor.getColumnIndex(DatabaseHelper.CUSTOMER_SIGN));
        String customer_img = cursor.getString(cursor.getColumnIndex(DatabaseHelper.CUSTOMER_IMG));
        String customer_img2 = cursor.getString(cursor.getColumnIndex(DatabaseHelper.CUSTOMER_IMG2));
        String customer_img3 = cursor.getString(cursor.getColumnIndex(DatabaseHelper.CUSTOMER_IMG3));
        String happy_delivery = cursor.getString(cursor.getColumnIndex(DatabaseHelper.HAPPY_DELIVERY));
        String happy_delivery_img = cursor.getString(cursor.getColumnIndex(DatabaseHelper.HAPPY_DELIVERY_IMG));
        String latitude = cursor.getString(cursor.getColumnIndex(DatabaseHelper.LATITUDE));
        String longitude = cursor.getString(cursor.getColumnIndex(DatabaseHelper.LONGITUDE));
        String sku_details = cursor.getString(cursor.getColumnIndex(DatabaseHelper.SKU_DETAILS));
        String last_updated = cursor.getString(cursor.getColumnIndex(DatabaseHelper.LAST_UPDATED));
        String preferred_transaction_time = cursor.getString(cursor.getColumnIndex(DatabaseHelper.PREFERRED_TRANSACTION_TIME));
        String original_amount_paid = cursor.getString(cursor.getColumnIndex(DatabaseHelper.ORIGINAL_AMOUNT_PAID));
        String user_id = cursor.getString(cursor.getColumnIndex(DatabaseHelper.USER_ID));
        String delivered_to = cursor.getString(cursor.getColumnIndex(DatabaseHelper.DELIVERED_TO));
        String delivered_to_relation = cursor.getString(cursor.getColumnIndex(DatabaseHelper.DELIVERED_TO_RELATION));
        String prirority_delivery = cursor.getString(cursor.getColumnIndex(DatabaseHelper.PRIRORITY_DELIVERY));
        String closing_km = cursor.getString(cursor.getColumnIndex(DatabaseHelper.CLOSING_KM));
        String imei_number = cursor.getString(cursor.getColumnIndex(DatabaseHelper.IMEI_NUMBER));
        String sync_update = cursor.getString(cursor.getColumnIndex(DatabaseHelper.SYNC_UPDATE));
        String updated_on_android = cursor.getString(cursor.getColumnIndex(DatabaseHelper.UPDATED_ON_ANDROID));
        String updated_on_webx = cursor.getString(cursor.getColumnIndex(DatabaseHelper.UPDATED_ON_WEBX));
        String loc_id = cursor.getString(cursor.getColumnIndex(DatabaseHelper.LOC_ID));
        String c_id = cursor.getString(cursor.getColumnIndex(DatabaseHelper.C_ID));
        String admin_id = cursor.getString(cursor.getColumnIndex(DatabaseHelper.ADMIN_ID));
        String extra1 = cursor.getString(cursor.getColumnIndex(DatabaseHelper.EXTRA1));
        String extra2 = cursor.getString(cursor.getColumnIndex(DatabaseHelper.EXTRA2));
        String extra3 = cursor.getString(cursor.getColumnIndex(DatabaseHelper.EXTRA3));
        String extra4 = cursor.getString(cursor.getColumnIndex(DatabaseHelper.EXTRA4));
        String extra5 = cursor.getString(cursor.getColumnIndex(DatabaseHelper.EXTRA5));
        String extra6 = cursor.getString(cursor.getColumnIndex(DatabaseHelper.EXTRA6));
        String sync_flag = cursor.getString(cursor.getColumnIndex(DatabaseHelper.SYNC_FLAG));
        String last_lat = cursor.getString(cursor.getColumnIndex(DatabaseHelper.LAST_LAT));
        String last_lng = cursor.getString(cursor.getColumnIndex(DatabaseHelper.LAST_LNG));
        String last_attempt_status = cursor.getString(cursor.getColumnIndex(DatabaseHelper.LAST_ATTEMPT_STATUS));
        String attempt_number = cursor.getString(cursor.getColumnIndex(DatabaseHelper.ATTEMPT_NUMBER));
        String attempt_slot = cursor.getString(cursor.getColumnIndex(DatabaseHelper.ATTEMPT_SLOT));
        String nps_score = cursor.getString(cursor.getColumnIndex(DatabaseHelper.NPS_SCORE));
        String seq_predicted = cursor.getString(cursor.getColumnIndex(DatabaseHelper.SEQ_PREDICTED));
        String seq_selected = cursor.getString(cursor.getColumnIndex(DatabaseHelper.SEQ_SELECTED));
        String seq_transaction = cursor.getString(cursor.getColumnIndex(DatabaseHelper.SEQ_TRANSACTION));
        String user_comments = cursor.getString(cursor.getColumnIndex(DatabaseHelper.USER_COMMENTS));
        String battery_level = cursor.getString(cursor.getColumnIndex(DatabaseHelper.BATTERY_LEVEL));
        String time_erp_import_initiated = cursor.getString(cursor.getColumnIndex(DatabaseHelper.TIME_ERP_IMPORT_INITIATED));
        String time_erp_import_completed = cursor.getString(cursor.getColumnIndex(DatabaseHelper.TIME_ERP_IMPORT_COMPLETED));
        String time_available_android_initiated = cursor.getString(cursor.getColumnIndex(DatabaseHelper.TIME_AVAILABLE_ANDROID_INITIATED));
        String time_available_android_completed = cursor.getString(cursor.getColumnIndex(DatabaseHelper.TIME_AVAILABLE_ANDROID_COMPLETED));
        String reverse_docket_number = cursor.getString(cursor.getColumnIndex(DatabaseHelper.REVERSE_DOCKET_NUMBER));
        String drs_docket = cursor.getString(cursor.getColumnIndex(DatabaseHelper.DRS_DOCKET));
        String sync = cursor.getString(cursor.getColumnIndex(DatabaseHelper.SYNC));
        String clientname = cursor.getString(cursor.getColumnIndex(DatabaseHelper.CLIENT_NAME));
        String clientcode = cursor.getString(cursor.getColumnIndex(DatabaseHelper.CLIENT_CODE));
        String actualwt = cursor.getString(cursor.getColumnIndex(DatabaseHelper.ACTUWT));
        String product_description = cursor.getString(cursor.getColumnIndex(DatabaseHelper.PRODUCT_DESCRIPTION));
        String reason_for_return = cursor.getString(cursor.getColumnIndex(DatabaseHelper.REASON_FOR_RETURN));
        String return_request_id = cursor.getString(cursor.getColumnIndex(DatabaseHelper.RETURN_REQUEST_ID));
        String tp_code = cursor.getString(cursor.getColumnIndex(DatabaseHelper.TP_CODE));
        String return_pincode = cursor.getString(cursor.getColumnIndex(DatabaseHelper.RETURN_PINCODE));
        String receipt_url = cursor.getString(cursor.getColumnIndex(DatabaseHelper.RECEIPT_URL));

        delivery.put(DeliveryHelper.JOBTYPE, jobtype);
        delivery.put(DeliveryHelper.CUST_NAME, cust_name);
        delivery.put(DeliveryHelper.CUST_ADDRESS1, cust_address1);
        delivery.put(DeliveryHelper.CUST_ADDRESS2, cust_address2);
        delivery.put(DeliveryHelper.PINCODE,pincode);
        delivery.put(DeliveryHelper.CITY,city);
        delivery.put(DeliveryHelper.STATE,state);
        delivery.put(DeliveryHelper.LANDMARK,landmark);
        delivery.put(DeliveryHelper.CUST_CONTACT1,cust_contact1);
        delivery.put(DeliveryHelper.ALTERNATE_NO,alternate_no);
        delivery.put(DeliveryHelper.ORDERNUMBER,orderNumber);
        delivery.put(DeliveryHelper.PACKAGESNO,packagesNo);
        delivery.put(DeliveryHelper.DRSNUMBER,DRSNumber);
        delivery.put(DeliveryHelper.TOTALDOCKETSINDRS,TotalDocketsInDRS);
        delivery.put(DeliveryHelper.PRODCUTDESC,ProdcutDesc);
        delivery.put(DeliveryHelper.DOCKET_NUMBER,docket_number);
        delivery.put(DeliveryHelper.CHOICE_OF_PAYMENT,choice_of_payment);
        delivery.put(DeliveryHelper.AMOUNT_TOBE_PAID, amount_tobe_paid);
        delivery.put(DeliveryHelper.DUE_DATE, due_date);
        delivery.put(DeliveryHelper.BOY_ID, boy_id);
        delivery.put(DeliveryHelper.BOY_NAME, boy_name);
        delivery.put(DeliveryHelper.STATUS, status);
        delivery.put(DeliveryHelper.REASON_ID, reason_id);
        delivery.put(DeliveryHelper.FAILED_REASON, failed_reason);
        delivery.put(DeliveryHelper.DELIVERY_TIME, delivery_time);
        delivery.put(DeliveryHelper.COMMENTS, comments);
        delivery.put(DeliveryHelper.MODE_OF_PAYMENT, mode_of_payment);
        delivery.put(DeliveryHelper.TRANSACTION_NO, transaction_no);
        delivery.put(DeliveryHelper.AUTH_CODE, auth_code);
        delivery.put(DeliveryHelper.CARD_TYPE, card_type);
        delivery.put(DeliveryHelper.BANK_NAME, bank_name);
        delivery.put(DeliveryHelper.CUSTOMER_SIGN, customer_sign);
        delivery.put(DeliveryHelper.CUSTOMER_IMG, customer_img);
        delivery.put(DeliveryHelper.CUSTOMER_IMG2, customer_img2);
        delivery.put(DeliveryHelper.CUSTOMER_IMG3, customer_img3);
        delivery.put(DeliveryHelper.HAPPY_DELIVERY, happy_delivery);
        delivery.put(DeliveryHelper.HAPPY_DELIVERY_IMG, happy_delivery_img);
        delivery.put(DeliveryHelper.LATITUDE, latitude);
        delivery.put(DeliveryHelper.LONGITUDE, longitude);
        delivery.put(DeliveryHelper.SKU_DETAILS, sku_details);
        delivery.put(DeliveryHelper.LAST_UPDATED, last_updated);
        delivery.put(DeliveryHelper.PREFERRED_TRANSACTION_TIME, preferred_transaction_time);
        delivery.put(DeliveryHelper.ORIGINAL_AMOUNT_PAID, original_amount_paid);
        delivery.put(DeliveryHelper.USER_ID, user_id);
        delivery.put(DeliveryHelper.DELIVERED_TO, delivered_to);
        delivery.put(DeliveryHelper.DELIVERED_TO_RELATION, delivered_to_relation);
        delivery.put(DeliveryHelper.PRIRORITY_DELIVERY, prirority_delivery);
        delivery.put(DeliveryHelper.CLOSING_KM,closing_km);
        delivery.put(DeliveryHelper.IMEI_NUMBER, imei_number);
        delivery.put(DeliveryHelper.SYNC_UPDATE, sync_update);
        delivery.put(DeliveryHelper.UPDATED_ON_ANDROID, updated_on_android);
        delivery.put(DeliveryHelper.UPDATED_ON_WEBX, updated_on_webx);
        delivery.put(DeliveryHelper.LOC_ID, loc_id);
        delivery.put(DeliveryHelper.C_ID, c_id);
        delivery.put(DeliveryHelper.ADMIN_ID, admin_id);
        delivery.put(DeliveryHelper.EXTRA1, extra1);
        delivery.put(DeliveryHelper.EXTRA2, extra2);
        delivery.put(DeliveryHelper.EXTRA3, extra3);
        delivery.put(DeliveryHelper.EXTRA4, extra4);
        delivery.put(DeliveryHelper.EXTRA5, extra5);
        delivery.put(DeliveryHelper.EXTRA6, extra6);
        delivery.put(DeliveryHelper.SYNC_FLAG, sync_flag);
        delivery.put(DeliveryHelper.LAST_LAT, last_lat);
        delivery.put(DeliveryHelper.LAST_LNG, last_lng);
        delivery.put(DeliveryHelper.LAST_ATTEMPT_STATUS, last_attempt_status);
        delivery.put(DeliveryHelper.ATTEMPT_NUMBER, attempt_number);
        delivery.put(DeliveryHelper.ATTEMPT_SLOT, attempt_slot);
        delivery.put(DeliveryHelper.NPS_SCORE, nps_score);
        delivery.put(DeliveryHelper.SEQ_PREDICTED, seq_predicted);
        delivery.put(DeliveryHelper.SEQ_SELECTED, seq_selected);
        delivery.put(DeliveryHelper.SEQ_TRANSACTION, seq_transaction);
        delivery.put(DeliveryHelper.USER_COMMENTS, user_comments);
        delivery.put(DeliveryHelper.BATTERY_LEVEL, battery_level);
        delivery.put(DeliveryHelper.TIME_ERP_IMPORT_INITIATED, time_erp_import_initiated);
        delivery.put(DeliveryHelper.TIME_ERP_IMPORT_COMPLETED, time_erp_import_completed);
        delivery.put(DeliveryHelper.TIME_AVAILABLE_ANDROID_INITIATED, time_available_android_initiated);
        delivery.put(DeliveryHelper.TIME_AVAILABLE_ANDROID_COMPLETED, time_available_android_completed);
        delivery.put(DeliveryHelper.REVERSE_DOCKET_NUMBER, reverse_docket_number);
        delivery.put(DeliveryHelper.DRS_DOCKET, drs_docket);
        delivery.put(DeliveryHelper.SYNC, sync);
        delivery.put(DeliveryHelper.ClIENT_NAME,clientname);
        delivery.put(DeliveryHelper.CLIENT_CODE, clientcode);
        delivery.put(DeliveryHelper.ACTUAL_WT, actualwt);
        delivery.put(DeliveryHelper.PRODUCT_DESCRIPTION, product_description);
        delivery.put(DeliveryHelper.REASON_FOR_RETURN, reason_for_return);
        delivery.put(DeliveryHelper.RETURN_REQUEST_ID, return_request_id);
        delivery.put(DeliveryHelper.TP_CODE, TP_CODE);
        delivery.put(DeliveryHelper.RETURN_PINCODE, return_pincode);
        delivery.put(DeliveryHelper.RECEIPT_URL, receipt_url);

        return delivery;
    }

    /**
     * Delete Delivery table
     * @return
     */
    public boolean deleteDeliveryTable() {
        int doneDelete = 0;
        SQLiteDatabase db = DatabaseHelper.getInstance().getReadableDatabase();
        doneDelete = db.delete(DatabaseHelper.DELIVERY_TABLE_NAME, null , null);
        db.close();
        return doneDelete > 0;
    }

    private ContentValues getContentValues(DeliveryEntity delivery) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(DatabaseHelper.ID, delivery.getid());
        contentValues.put(DatabaseHelper.JOBTYPE, delivery.getjobtype());
        contentValues.put(DatabaseHelper.CUST_NAME, delivery.getcust_name());
        contentValues.put(DatabaseHelper.CUST_ADDRESS1, delivery.getcust_address1());
        contentValues.put(DatabaseHelper.CUST_ADDRESS2, delivery.getcust_address2());
        contentValues.put(DatabaseHelper.PINCODE, delivery.getpincode());
        contentValues.put(DatabaseHelper.CITY, delivery.getcity());
        contentValues.put(DatabaseHelper.STATE, delivery.getstate());
        contentValues.put(DatabaseHelper.LANDMARK, delivery.getlandmark());
        contentValues.put(DatabaseHelper.CUST_CONTACT1, delivery.getcust_contact1());
        contentValues.put(DatabaseHelper.ALTERNATE_NO, delivery.getalternate_no());
        contentValues.put(DatabaseHelper.ORDERNUMBER, delivery.getorderNumber());
        contentValues.put(DatabaseHelper.PACKAGESNO, delivery.getpackagesNo());
        contentValues.put(DatabaseHelper.DRSNUMBER, delivery.getDRSNumber());
        contentValues.put(DatabaseHelper.TOTALDOCKETSINDRS, delivery.getTotalDocketsInDRS());
        contentValues.put(DatabaseHelper.PRODCUTDESC, delivery.getProdcutDesc());
        contentValues.put(DatabaseHelper.DOCKET_NUMBER, delivery.getdocket_number());
        contentValues.put(DatabaseHelper.CHOICE_OF_PAYMENT, delivery.getchoice_of_payment());
        contentValues.put(DatabaseHelper.AMOUNT_TOBE_PAID, delivery.getamount_tobe_paid());
        contentValues.put(DatabaseHelper.DUE_DATE, delivery.getdue_date());
        contentValues.put(DatabaseHelper.BOY_ID, delivery.getboy_id());
        contentValues.put(DatabaseHelper.BOY_NAME, delivery.getboy_name());
        contentValues.put(DatabaseHelper.STATUS, delivery.getstatus());
        contentValues.put(DatabaseHelper.REASON_ID, delivery.getreason_id());
        contentValues.put(DatabaseHelper.FAILED_REASON, delivery.getfailed_reason());
        contentValues.put(DatabaseHelper.DELIVERY_TIME, delivery.getdelivery_time());
        contentValues.put(DatabaseHelper.COMMENTS, delivery.getcomments());
        contentValues.put(DatabaseHelper.MODE_OF_PAYMENT, delivery.getmode_of_payment());
        contentValues.put(DatabaseHelper.TRANSACTION_NO, delivery.gettransaction_no());
        contentValues.put(DatabaseHelper.AUTH_CODE, delivery.getauth_code());
        contentValues.put(DatabaseHelper.CARD_TYPE, delivery.getcard_type());
        contentValues.put(DatabaseHelper.BANK_NAME, delivery.getbank_name());
        contentValues.put(DatabaseHelper.CUSTOMER_SIGN, delivery.getcustomer_sign());
        contentValues.put(DatabaseHelper.CUSTOMER_IMG, delivery.getcustomer_img());
        contentValues.put(DatabaseHelper.CUSTOMER_IMG2, delivery.getcustomer_img2());
        contentValues.put(DatabaseHelper.CUSTOMER_IMG3, delivery.getcustomer_img3());
        contentValues.put(DatabaseHelper.HAPPY_DELIVERY, delivery.gethappy_delivery());
        contentValues.put(DatabaseHelper.HAPPY_DELIVERY_IMG, delivery.gethappy_delivery_img());
        contentValues.put(DatabaseHelper.LATITUDE, delivery.getlatitude());
        contentValues.put(DatabaseHelper.LONGITUDE, delivery.getlongitude());
        contentValues.put(DatabaseHelper.SKU_DETAILS, delivery.getsku_details());
        contentValues.put(DatabaseHelper.LAST_UPDATED, delivery.getlast_updated());
        contentValues.put(DatabaseHelper.PREFERRED_TRANSACTION_TIME, delivery.getpreferred_transaction_time());
        contentValues.put(DatabaseHelper.ORIGINAL_AMOUNT_PAID, delivery.getoriginal_amount_paid());
        contentValues.put(DatabaseHelper.USER_ID, delivery.getuser_id());
        contentValues.put(DatabaseHelper.DELIVERED_TO, delivery.getdelivered_to());
        contentValues.put(DatabaseHelper.DELIVERED_TO_RELATION, delivery.getdelivered_to_relation());
        contentValues.put(DatabaseHelper.PRIRORITY_DELIVERY, delivery.getprirority_delivery());
        contentValues.put(DatabaseHelper.CLOSING_KM, delivery.getclosing_km());
        contentValues.put(DatabaseHelper.IMEI_NUMBER, delivery.getimei_number());
        contentValues.put(DatabaseHelper.SYNC_UPDATE, delivery.getsync_update());
        contentValues.put(DatabaseHelper.UPDATED_ON_ANDROID, delivery.getupdated_on_android());
        contentValues.put(DatabaseHelper.UPDATED_ON_WEBX, delivery.getupdated_on_webx());
        contentValues.put(DatabaseHelper.LOC_ID, delivery.getloc_id());
        contentValues.put(DatabaseHelper.C_ID, delivery.getc_id());
        contentValues.put(DatabaseHelper.ADMIN_ID, delivery.getadmin_id());
        contentValues.put(DatabaseHelper.EXTRA1, delivery.getextra1());
        contentValues.put(DatabaseHelper.EXTRA2, delivery.getextra2());
        contentValues.put(DatabaseHelper.EXTRA3, delivery.getextra3());
        contentValues.put(DatabaseHelper.EXTRA4, delivery.getextra4());
        contentValues.put(DatabaseHelper.EXTRA5, delivery.getextra5());
        contentValues.put(DatabaseHelper.EXTRA6, delivery.getextra6());
        contentValues.put(DatabaseHelper.SYNC_FLAG, delivery.getsync_flag());
        contentValues.put(DatabaseHelper.LAST_LAT, delivery.getlast_lat());
        contentValues.put(DatabaseHelper.LAST_LNG, delivery.getlast_lng());
        contentValues.put(DatabaseHelper.LAST_ATTEMPT_STATUS, delivery.getlast_attempt_status());
        contentValues.put(DatabaseHelper.ATTEMPT_NUMBER, delivery.getattempt_number());
        contentValues.put(DatabaseHelper.ATTEMPT_SLOT, delivery.getattempt_slot());
        contentValues.put(DatabaseHelper.NPS_SCORE, delivery.getnps_score());
        contentValues.put(DatabaseHelper.SEQ_PREDICTED, delivery.getseq_predicted());
        contentValues.put(DatabaseHelper.SEQ_SELECTED, delivery.getseq_selected());
        contentValues.put(DatabaseHelper.SEQ_TRANSACTION, delivery.getseq_transaction());
        contentValues.put(DatabaseHelper.USER_COMMENTS, delivery.getuser_comments());
        contentValues.put(DatabaseHelper.BATTERY_LEVEL, delivery.getbattery_level());
        contentValues.put(DatabaseHelper.TIME_ERP_IMPORT_INITIATED, delivery.gettime_erp_import_initiated());
        contentValues.put(DatabaseHelper.TIME_ERP_IMPORT_COMPLETED, delivery.gettime_erp_import_completed());
        contentValues.put(DatabaseHelper.TIME_AVAILABLE_ANDROID_INITIATED, delivery.gettime_available_android_initiated());
        contentValues.put(DatabaseHelper.TIME_AVAILABLE_ANDROID_COMPLETED, delivery.gettime_available_android_completed());
        contentValues.put(DatabaseHelper.REVERSE_DOCKET_NUMBER, delivery.getreverse_docket_number());
        contentValues.put(DatabaseHelper.DRS_DOCKET, delivery.getdrs_docket());
        contentValues.put(DatabaseHelper.SYNC, delivery.getsync());
        contentValues.put(DatabaseHelper.PRODUCT_DESCRIPTION, delivery.getproduct_description());
        contentValues.put(DatabaseHelper.REASON_FOR_RETURN, delivery.getreason_for_return());
        contentValues.put(DatabaseHelper.RETURN_REQUEST_ID, delivery.getreturn_request_id());
        contentValues.put(DatabaseHelper.TP_CODE, delivery.gettp_code());
        contentValues.put(DatabaseHelper.RETURN_PINCODE, delivery.getreturn_pincode());
        contentValues.put(DatabaseHelper.RECEIPT_URL, delivery.getreceipt_url());

        return contentValues;
    }

    /**
     * Get delivery dockets of non synced data
     * @return
     */
    public ArrayList<String> getNonSyncDeliveryDockets() {
        ArrayList<String> docketList = new ArrayList<>();
        SQLiteDatabase db = DatabaseHelper.getInstance().getReadableDatabase();
        Cursor cursor =  db.rawQuery( "select * from  " + DatabaseHelper.DELIVERY_TABLE_NAME + " where " + DatabaseHelper.SYNC + " = 0 ",null);
        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            while(cursor.isAfterLast() == false) {
                String docket_number = cursor.getString(cursor.getColumnIndex(DatabaseHelper.DOCKET_NUMBER));
                docketList.add(docket_number);
                cursor.moveToNext();
            }
        }
        cursor.close();
        return  docketList;
    }

    /**
     * Get delivery json of non synced data
     * @return
     */
    public JSONArray getNonSyncDeliveryJson() {
        JSONArray jsonArray=new JSONArray();
        SQLiteDatabase db = DatabaseHelper.getInstance().getReadableDatabase();
        Cursor cursor =  db.rawQuery( "select * from  " + DatabaseHelper.DELIVERY_TABLE_NAME + " where " + DatabaseHelper.SYNC + " = 0 ",null);

        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            while(cursor.isAfterLast() == false) {
                JSONObject jsonObject = getDeliveryJsonObject(cursor);
                jsonArray.put(jsonObject);
                cursor.moveToNext();
            }
        }
        cursor.close();
        return  jsonArray;
    }


    /**
     * Get delivery json of non synced data
     * @return
     */
    public JSONArray getDeliveryDocketJson(String drs_docket) {
        JSONArray jsonArray=new JSONArray();
        SQLiteDatabase db = DatabaseHelper.getInstance().getReadableDatabase();
        Cursor cursor =  db.rawQuery( "select * from  " + DatabaseHelper.DELIVERY_TABLE_NAME + " where " + DatabaseHelper.DRS_DOCKET + " = ? ", new String[]{drs_docket});

        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            while(cursor.isAfterLast() == false) {
                JSONObject jsonObject = getDeliveryJsonObject(cursor);
                jsonArray.put(jsonObject);
                cursor.moveToNext();
            }
        }
        cursor.close();
        return  jsonArray;
    }
    /**
     * Get delivery json object
     * @param cursor
     * @return
     */
    public JSONObject getDeliveryJsonObject(Cursor cursor) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject();
            String drs_docket = cursor.getString(cursor.getColumnIndex(DatabaseHelper.DRS_DOCKET));
            jsonObject.put(DatabaseHelper.ID, cursor.getString(cursor.getColumnIndex(DatabaseHelper.ID)));
            jsonObject.put(DatabaseHelper.JOBTYPE, cursor.getString(cursor.getColumnIndex(DatabaseHelper.JOBTYPE)));
            jsonObject.put(DatabaseHelper.CUST_NAME, cursor.getString(cursor.getColumnIndex(DatabaseHelper.CUST_NAME)));
            jsonObject.put(DatabaseHelper.CUST_ADDRESS1, cursor.getString(cursor.getColumnIndex(DatabaseHelper.CUST_ADDRESS1)));
            jsonObject.put(DatabaseHelper.CUST_ADDRESS2, cursor.getString(cursor.getColumnIndex(DatabaseHelper.CUST_ADDRESS2)));
            jsonObject.put(DatabaseHelper.PINCODE, cursor.getString(cursor.getColumnIndex(DatabaseHelper.PINCODE)));
            jsonObject.put(DatabaseHelper.CITY, cursor.getString(cursor.getColumnIndex(DatabaseHelper.CITY)));
            jsonObject.put(DatabaseHelper.STATE, cursor.getString(cursor.getColumnIndex(DatabaseHelper.STATE)));
            jsonObject.put(DatabaseHelper.LANDMARK, cursor.getString(cursor.getColumnIndex(DatabaseHelper.LANDMARK)));
            jsonObject.put(DatabaseHelper.CUST_CONTACT1, cursor.getString(cursor.getColumnIndex(DatabaseHelper.CUST_CONTACT1)));
            jsonObject.put(DatabaseHelper.ALTERNATE_NO, cursor.getString(cursor.getColumnIndex(DatabaseHelper.ALTERNATE_NO)));
            jsonObject.put(DatabaseHelper.ORDERNUMBER, cursor.getString(cursor.getColumnIndex(DatabaseHelper.ORDERNUMBER)));
            jsonObject.put(DatabaseHelper.PACKAGESNO, cursor.getString(cursor.getColumnIndex(DatabaseHelper.PACKAGESNO)));
            jsonObject.put(DatabaseHelper.DRSNUMBER, cursor.getString(cursor.getColumnIndex(DatabaseHelper.DRSNUMBER)));
            jsonObject.put(DatabaseHelper.TOTALDOCKETSINDRS, cursor.getString(cursor.getColumnIndex(DatabaseHelper.TOTALDOCKETSINDRS)));
            jsonObject.put(DatabaseHelper.PRODCUTDESC, cursor.getString(cursor.getColumnIndex(DatabaseHelper.PRODCUTDESC)));
            jsonObject.put(DatabaseHelper.DOCKET_NUMBER, cursor.getString(cursor.getColumnIndex(DatabaseHelper.DOCKET_NUMBER)));
            jsonObject.put(DatabaseHelper.CHOICE_OF_PAYMENT, cursor.getString(cursor.getColumnIndex(DatabaseHelper.CHOICE_OF_PAYMENT)));
            jsonObject.put(DatabaseHelper.AMOUNT_TOBE_PAID, cursor.getString(cursor.getColumnIndex(DatabaseHelper.AMOUNT_TOBE_PAID)));
            jsonObject.put(DatabaseHelper.DUE_DATE, cursor.getString(cursor.getColumnIndex(DatabaseHelper.DUE_DATE)));
            jsonObject.put(DatabaseHelper.BOY_ID, cursor.getString(cursor.getColumnIndex(DatabaseHelper.BOY_ID)));
            jsonObject.put(DatabaseHelper.BOY_NAME, cursor.getString(cursor.getColumnIndex(DatabaseHelper.BOY_NAME)));
            jsonObject.put(DatabaseHelper.STATUS, cursor.getString(cursor.getColumnIndex(DatabaseHelper.STATUS)));
            jsonObject.put(DatabaseHelper.REASON_ID, cursor.getString(cursor.getColumnIndex(DatabaseHelper.REASON_ID)));
            jsonObject.put(DatabaseHelper.FAILED_REASON, cursor.getString(cursor.getColumnIndex(DatabaseHelper.FAILED_REASON)));
            jsonObject.put(DatabaseHelper.DELIVERY_TIME, cursor.getString(cursor.getColumnIndex(DatabaseHelper.DELIVERY_TIME)));
            jsonObject.put(DatabaseHelper.COMMENTS, cursor.getString(cursor.getColumnIndex(DatabaseHelper.COMMENTS)));
            jsonObject.put(DatabaseHelper.MODE_OF_PAYMENT, cursor.getString(cursor.getColumnIndex(DatabaseHelper.MODE_OF_PAYMENT)));
            jsonObject.put(DatabaseHelper.TRANSACTION_NO, cursor.getString(cursor.getColumnIndex(DatabaseHelper.TRANSACTION_NO)));
            jsonObject.put(DatabaseHelper.AUTH_CODE, cursor.getString(cursor.getColumnIndex(DatabaseHelper.AUTH_CODE)));
            jsonObject.put(DatabaseHelper.CARD_TYPE, cursor.getString(cursor.getColumnIndex(DatabaseHelper.CARD_TYPE)));
            jsonObject.put(DatabaseHelper.BANK_NAME, cursor.getString(cursor.getColumnIndex(DatabaseHelper.BANK_NAME)));
            jsonObject.put(DatabaseHelper.CUSTOMER_SIGN, cursor.getString(cursor.getColumnIndex(DatabaseHelper.CUSTOMER_SIGN)));
            jsonObject.put(DatabaseHelper.CUSTOMER_IMG, cursor.getString(cursor.getColumnIndex(DatabaseHelper.CUSTOMER_IMG)));
            jsonObject.put(DatabaseHelper.CUSTOMER_IMG2, cursor.getString(cursor.getColumnIndex(DatabaseHelper.CUSTOMER_IMG2)));
            jsonObject.put(DatabaseHelper.CUSTOMER_IMG3, cursor.getString(cursor.getColumnIndex(DatabaseHelper.CUSTOMER_IMG3)));
            jsonObject.put(DatabaseHelper.HAPPY_DELIVERY, cursor.getString(cursor.getColumnIndex(DatabaseHelper.HAPPY_DELIVERY)));
            jsonObject.put(DatabaseHelper.HAPPY_DELIVERY_IMG, cursor.getString(cursor.getColumnIndex(DatabaseHelper.HAPPY_DELIVERY_IMG)));
            jsonObject.put(DatabaseHelper.LATITUDE, cursor.getString(cursor.getColumnIndex(DatabaseHelper.LATITUDE)));
            jsonObject.put(DatabaseHelper.LONGITUDE, cursor.getString(cursor.getColumnIndex(DatabaseHelper.LONGITUDE)));
            jsonObject.put(DatabaseHelper.SKU_DETAILS, cursor.getString(cursor.getColumnIndex(DatabaseHelper.SKU_DETAILS)));
            jsonObject.put(DatabaseHelper.LAST_UPDATED, cursor.getString(cursor.getColumnIndex(DatabaseHelper.LAST_UPDATED)));
            jsonObject.put(DatabaseHelper.PREFERRED_TRANSACTION_TIME, cursor.getString(cursor.getColumnIndex(DatabaseHelper.PREFERRED_TRANSACTION_TIME)));
            jsonObject.put(DatabaseHelper.ORIGINAL_AMOUNT_PAID, cursor.getString(cursor.getColumnIndex(DatabaseHelper.ORIGINAL_AMOUNT_PAID)));
            jsonObject.put(DatabaseHelper.USER_ID, cursor.getString(cursor.getColumnIndex(DatabaseHelper.USER_ID)));
            jsonObject.put(DatabaseHelper.DELIVERED_TO, cursor.getString(cursor.getColumnIndex(DatabaseHelper.DELIVERED_TO)));
            jsonObject.put(DatabaseHelper.DELIVERED_TO_RELATION, cursor.getString(cursor.getColumnIndex(DatabaseHelper.DELIVERED_TO_RELATION)));
            jsonObject.put(DatabaseHelper.PRIRORITY_DELIVERY, cursor.getString(cursor.getColumnIndex(DatabaseHelper.PRIRORITY_DELIVERY)));
            jsonObject.put(DatabaseHelper.CLOSING_KM, cursor.getString(cursor.getColumnIndex(DatabaseHelper.CLOSING_KM)));
            jsonObject.put(DatabaseHelper.IMEI_NUMBER, cursor.getString(cursor.getColumnIndex(DatabaseHelper.IMEI_NUMBER)));
            jsonObject.put(DatabaseHelper.SYNC_UPDATE, cursor.getString(cursor.getColumnIndex(DatabaseHelper.SYNC_UPDATE)));
            jsonObject.put(DatabaseHelper.UPDATED_ON_ANDROID, cursor.getString(cursor.getColumnIndex(DatabaseHelper.UPDATED_ON_ANDROID)));
            jsonObject.put(DatabaseHelper.UPDATED_ON_WEBX, cursor.getString(cursor.getColumnIndex(DatabaseHelper.UPDATED_ON_WEBX)));
            jsonObject.put(DatabaseHelper.LOC_ID, cursor.getString(cursor.getColumnIndex(DatabaseHelper.LOC_ID)));
            jsonObject.put(DatabaseHelper.C_ID, cursor.getString(cursor.getColumnIndex(DatabaseHelper.C_ID)));
            jsonObject.put(DatabaseHelper.ADMIN_ID, cursor.getString(cursor.getColumnIndex(DatabaseHelper.ADMIN_ID)));
            jsonObject.put(DatabaseHelper.EXTRA1, cursor.getString(cursor.getColumnIndex(DatabaseHelper.EXTRA1)));
            jsonObject.put(DatabaseHelper.EXTRA2, cursor.getString(cursor.getColumnIndex(DatabaseHelper.EXTRA2)));
            jsonObject.put(DatabaseHelper.EXTRA3, cursor.getString(cursor.getColumnIndex(DatabaseHelper.EXTRA3)));
            jsonObject.put(DatabaseHelper.EXTRA4, cursor.getString(cursor.getColumnIndex(DatabaseHelper.EXTRA4)));
            jsonObject.put(DatabaseHelper.EXTRA5, cursor.getString(cursor.getColumnIndex(DatabaseHelper.EXTRA5)));
            jsonObject.put(DatabaseHelper.EXTRA6, cursor.getString(cursor.getColumnIndex(DatabaseHelper.EXTRA6)));
            jsonObject.put(DatabaseHelper.SYNC_FLAG, cursor.getString(cursor.getColumnIndex(DatabaseHelper.SYNC_FLAG)));
            jsonObject.put(DatabaseHelper.LAST_LAT, cursor.getString(cursor.getColumnIndex(DatabaseHelper.LAST_LAT)));
            jsonObject.put(DatabaseHelper.LAST_LNG, cursor.getString(cursor.getColumnIndex(DatabaseHelper.LAST_LNG)));
            jsonObject.put(DatabaseHelper.LAST_ATTEMPT_STATUS, cursor.getString(cursor.getColumnIndex(DatabaseHelper.LAST_ATTEMPT_STATUS)));
            jsonObject.put(DatabaseHelper.ATTEMPT_NUMBER, cursor.getString(cursor.getColumnIndex(DatabaseHelper.ATTEMPT_NUMBER)));
            jsonObject.put(DatabaseHelper.ATTEMPT_SLOT, cursor.getString(cursor.getColumnIndex(DatabaseHelper.ATTEMPT_SLOT)));
            jsonObject.put(DatabaseHelper.NPS_SCORE, cursor.getString(cursor.getColumnIndex(DatabaseHelper.NPS_SCORE)));
            jsonObject.put(DatabaseHelper.SEQ_PREDICTED, cursor.getString(cursor.getColumnIndex(DatabaseHelper.SEQ_PREDICTED)));
            jsonObject.put(DatabaseHelper.SEQ_SELECTED, cursor.getString(cursor.getColumnIndex(DatabaseHelper.SEQ_SELECTED)));
            jsonObject.put(DatabaseHelper.SEQ_TRANSACTION, cursor.getString(cursor.getColumnIndex(DatabaseHelper.SEQ_TRANSACTION)));
            jsonObject.put(DatabaseHelper.USER_COMMENTS, cursor.getString(cursor.getColumnIndex(DatabaseHelper.USER_COMMENTS)));
            jsonObject.put(DatabaseHelper.BATTERY_LEVEL, cursor.getString(cursor.getColumnIndex(DatabaseHelper.BATTERY_LEVEL)));
            jsonObject.put(DatabaseHelper.TIME_ERP_IMPORT_INITIATED, cursor.getString(cursor.getColumnIndex(DatabaseHelper.TIME_ERP_IMPORT_INITIATED)));
            jsonObject.put(DatabaseHelper.TIME_ERP_IMPORT_COMPLETED, cursor.getString(cursor.getColumnIndex(DatabaseHelper.TIME_ERP_IMPORT_COMPLETED)));
            jsonObject.put(DatabaseHelper.TIME_AVAILABLE_ANDROID_INITIATED, cursor.getString(cursor.getColumnIndex(DatabaseHelper.TIME_AVAILABLE_ANDROID_INITIATED)));
            jsonObject.put(DatabaseHelper.TIME_AVAILABLE_ANDROID_COMPLETED, cursor.getString(cursor.getColumnIndex(DatabaseHelper.TIME_AVAILABLE_ANDROID_COMPLETED)));
            jsonObject.put(DatabaseHelper.REVERSE_DOCKET_NUMBER, cursor.getString(cursor.getColumnIndex(DatabaseHelper.REVERSE_DOCKET_NUMBER)));
            jsonObject.put(DatabaseHelper.DRS_DOCKET, cursor.getString(cursor.getColumnIndex(DatabaseHelper.DRS_DOCKET)));
            jsonObject.put(DatabaseHelper.SYNC, cursor.getString(cursor.getColumnIndex(DatabaseHelper.SYNC)));
            jsonObject.put(DatabaseHelper.ITEM_TABLE_NAME, ItemHelper.getInstance().getItemsJsonArray(drs_docket));
            jsonObject.put(DatabaseHelper.PRODUCT_DESCRIPTION, cursor.getString(cursor.getColumnIndex(DatabaseHelper.PRODUCT_DESCRIPTION)));
            jsonObject.put(DatabaseHelper.REASON_FOR_RETURN, cursor.getString(cursor.getColumnIndex(DatabaseHelper.REASON_FOR_RETURN)));
            jsonObject.put(DatabaseHelper.RETURN_REQUEST_ID, cursor.getString(cursor.getColumnIndex(DatabaseHelper.RETURN_REQUEST_ID)));
            jsonObject.put(DatabaseHelper.TP_CODE, cursor.getString(cursor.getColumnIndex(DatabaseHelper.TP_CODE)));
            jsonObject.put(DatabaseHelper.RETURN_PINCODE, cursor.getString(cursor.getColumnIndex(DatabaseHelper.RETURN_PINCODE)));
            jsonObject.put(DatabaseHelper.RECEIPT_URL, cursor.getString(cursor.getColumnIndex(DatabaseHelper.RECEIPT_URL)));


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    /**
     * Get delivery json object
     * @param deliveryEntity
     * @return
     */
    public JSONObject getDeliveryJsonObject(DeliveryEntity deliveryEntity) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject();
            jsonObject.put(DatabaseHelper.ID, deliveryEntity.getid());
            jsonObject.put(DatabaseHelper.JOBTYPE, deliveryEntity.getjobtype());
            jsonObject.put(DatabaseHelper.CUST_NAME, deliveryEntity.getcust_name());
            jsonObject.put(DatabaseHelper.CUST_ADDRESS1, deliveryEntity.getcust_address1());
            jsonObject.put(DatabaseHelper.CUST_ADDRESS2, deliveryEntity.getcust_address2());
            jsonObject.put(DatabaseHelper.PINCODE, deliveryEntity.getpincode());
            jsonObject.put(DatabaseHelper.CITY, deliveryEntity.getcity());
            jsonObject.put(DatabaseHelper.STATE, deliveryEntity.getstate());
            jsonObject.put(DatabaseHelper.LANDMARK, deliveryEntity.getlandmark());
            jsonObject.put(DatabaseHelper.CUST_CONTACT1,deliveryEntity.getcust_contact1());
            jsonObject.put(DatabaseHelper.ALTERNATE_NO, deliveryEntity.getalternate_no());
            jsonObject.put(DatabaseHelper.ORDERNUMBER,deliveryEntity.getorderNumber());
            jsonObject.put(DatabaseHelper.PACKAGESNO, deliveryEntity.getpackagesNo());
            jsonObject.put(DatabaseHelper.DRSNUMBER, deliveryEntity.getDRSNumber());
            jsonObject.put(DatabaseHelper.TOTALDOCKETSINDRS, deliveryEntity.getTotalDocketsInDRS());
            jsonObject.put(DatabaseHelper.PRODCUTDESC, deliveryEntity.getProdcutDesc());
            jsonObject.put(DatabaseHelper.DOCKET_NUMBER, deliveryEntity.getdocket_number());
            jsonObject.put(DatabaseHelper.CHOICE_OF_PAYMENT, deliveryEntity.getchoice_of_payment());
            jsonObject.put(DatabaseHelper.AMOUNT_TOBE_PAID,deliveryEntity.getamount_tobe_paid());
            jsonObject.put(DatabaseHelper.DUE_DATE, deliveryEntity.getdue_date());
            jsonObject.put(DatabaseHelper.BOY_ID, deliveryEntity.getboy_id());
            jsonObject.put(DatabaseHelper.BOY_NAME, deliveryEntity.getboy_name());
            jsonObject.put(DatabaseHelper.STATUS, deliveryEntity.getstatus());
            jsonObject.put(DatabaseHelper.DELIVERY_TIME,deliveryEntity.getdelivery_time());
            jsonObject.put(DatabaseHelper.COMMENTS,deliveryEntity.getcomments());
            jsonObject.put(DatabaseHelper.MODE_OF_PAYMENT,deliveryEntity.getmode_of_payment());
            jsonObject.put(DatabaseHelper.TRANSACTION_NO, deliveryEntity.gettransaction_no());
            jsonObject.put(DatabaseHelper.AUTH_CODE, deliveryEntity.getauth_code());
            jsonObject.put(DatabaseHelper.CARD_TYPE, deliveryEntity.getcard_type());
            jsonObject.put(DatabaseHelper.BANK_NAME,deliveryEntity.getbank_name());
            jsonObject.put(DatabaseHelper.CUSTOMER_SIGN, deliveryEntity.getcustomer_sign());
            jsonObject.put(DatabaseHelper.CUSTOMER_IMG,deliveryEntity.getcustomer_img());
            jsonObject.put(DatabaseHelper.CUSTOMER_IMG2, deliveryEntity.getcustomer_img2());
            jsonObject.put(DatabaseHelper.CUSTOMER_IMG3, deliveryEntity.getcustomer_img3());
            jsonObject.put(DatabaseHelper.HAPPY_DELIVERY, deliveryEntity.gethappy_delivery());
            jsonObject.put(DatabaseHelper.HAPPY_DELIVERY_IMG, deliveryEntity.gethappy_delivery_img());
            jsonObject.put(DatabaseHelper.LATITUDE, deliveryEntity.getlatitude());
            jsonObject.put(DatabaseHelper.LONGITUDE, deliveryEntity.getlongitude());
            jsonObject.put(DatabaseHelper.SKU_DETAILS, deliveryEntity.getsku_details());
            jsonObject.put(DatabaseHelper.LAST_UPDATED, deliveryEntity.getlast_updated());
            jsonObject.put(DatabaseHelper.PREFERRED_TRANSACTION_TIME, deliveryEntity.getpreferred_transaction_time());
            jsonObject.put(DatabaseHelper.ORIGINAL_AMOUNT_PAID, deliveryEntity.getoriginal_amount_paid());
            jsonObject.put(DatabaseHelper.USER_ID,deliveryEntity.getuser_id());
            jsonObject.put(DatabaseHelper.DELIVERED_TO, deliveryEntity.getdelivered_to());
            jsonObject.put(DatabaseHelper.DELIVERED_TO_RELATION, deliveryEntity.getdelivered_to_relation());
            jsonObject.put(DatabaseHelper.PRIRORITY_DELIVERY, deliveryEntity.getprirority_delivery());
            jsonObject.put(DatabaseHelper.CLOSING_KM, deliveryEntity.getclosing_km());
            jsonObject.put(DatabaseHelper.IMEI_NUMBER, deliveryEntity.getimei_number());
            jsonObject.put(DatabaseHelper.SYNC_UPDATE, deliveryEntity.getsync_update());
            jsonObject.put(DatabaseHelper.UPDATED_ON_ANDROID, deliveryEntity.getupdated_on_android());
            jsonObject.put(DatabaseHelper.UPDATED_ON_WEBX, deliveryEntity.getupdated_on_webx());
            jsonObject.put(DatabaseHelper.LOC_ID, deliveryEntity.getloc_id());
            jsonObject.put(DatabaseHelper.C_ID, deliveryEntity.getc_id());
            jsonObject.put(DatabaseHelper.ADMIN_ID, deliveryEntity.getadmin_id());
            jsonObject.put(DatabaseHelper.EXTRA1,deliveryEntity.getextra1());
            jsonObject.put(DatabaseHelper.EXTRA2,deliveryEntity.getextra2());
            jsonObject.put(DatabaseHelper.EXTRA3,deliveryEntity.getextra3());
            jsonObject.put(DatabaseHelper.EXTRA4,deliveryEntity.getextra4() );
            jsonObject.put(DatabaseHelper.EXTRA5, deliveryEntity.getextra5());
            jsonObject.put(DatabaseHelper.EXTRA6, deliveryEntity.getextra6());
            jsonObject.put(DatabaseHelper.SYNC_FLAG, deliveryEntity.getsync_flag());
            jsonObject.put(DatabaseHelper.LAST_LAT, deliveryEntity.getlast_lat());
            jsonObject.put(DatabaseHelper.LAST_LNG, deliveryEntity.getlast_lng());
            jsonObject.put(DatabaseHelper.LAST_ATTEMPT_STATUS, deliveryEntity.getlast_attempt_status());
            jsonObject.put(DatabaseHelper.ATTEMPT_NUMBER, deliveryEntity.getattempt_number());
            jsonObject.put(DatabaseHelper.ATTEMPT_SLOT, deliveryEntity.getattempt_slot());
            jsonObject.put(DatabaseHelper.NPS_SCORE, deliveryEntity.getnps_score());
            jsonObject.put(DatabaseHelper.SEQ_PREDICTED, deliveryEntity.getseq_predicted());
            jsonObject.put(DatabaseHelper.SEQ_SELECTED,deliveryEntity.getseq_selected());
            jsonObject.put(DatabaseHelper.SEQ_TRANSACTION, deliveryEntity.getseq_transaction());
            jsonObject.put(DatabaseHelper.USER_COMMENTS, deliveryEntity.getuser_comments());
            jsonObject.put(DatabaseHelper.BATTERY_LEVEL, deliveryEntity.getbattery_level());
            jsonObject.put(DatabaseHelper.TIME_ERP_IMPORT_INITIATED, deliveryEntity.gettime_erp_import_initiated());
            jsonObject.put(DatabaseHelper.TIME_ERP_IMPORT_COMPLETED, deliveryEntity.gettime_erp_import_completed());
            jsonObject.put(DatabaseHelper.TIME_AVAILABLE_ANDROID_INITIATED, deliveryEntity.gettime_available_android_initiated());
            jsonObject.put(DatabaseHelper.TIME_AVAILABLE_ANDROID_COMPLETED, deliveryEntity.gettime_available_android_completed());
            jsonObject.put(DatabaseHelper.REVERSE_DOCKET_NUMBER, deliveryEntity.getreverse_docket_number());
            jsonObject.put(DatabaseHelper.DRS_DOCKET, deliveryEntity.getdrs_docket());
            jsonObject.put(DatabaseHelper.SYNC, deliveryEntity.getsync());
            jsonObject.put(DatabaseHelper.ITEM_TABLE_NAME,ItemHelper.getInstance().getItemsJsonArray(deliveryEntity.getdrs_docket()));
            jsonObject.put(DatabaseHelper.PRODUCT_DESCRIPTION, deliveryEntity.getproduct_description());
            jsonObject.put(DatabaseHelper.REASON_FOR_RETURN, deliveryEntity.getreason_for_return());
            jsonObject.put(DatabaseHelper.RETURN_REQUEST_ID,deliveryEntity.getreturn_request_id());
            jsonObject.put(DatabaseHelper.TP_CODE, deliveryEntity.gettp_code());
            jsonObject.put(DatabaseHelper.RETURN_PINCODE, deliveryEntity.getreturn_pincode());
            jsonObject.put(DatabaseHelper.RECEIPT_URL, deliveryEntity.getreceipt_url());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    /**
     * Insert delivery data in database
     * @param response
     */
    public void insertJsonInDatabase(JSONObject response) {
        try {
            JSONArray pushDataArray = response.getJSONArray("PushData");
            int length = pushDataArray.length();
            for(int i=0; i<length; i++){
                JSONObject jsonObject = pushDataArray.getJSONObject(i);
                String jobtype = jsonObject.getString(DatabaseHelper.JOBTYPE);
                String cust_name = jsonObject.getString(DatabaseHelper.CUST_NAME);
                String cust_address1 = jsonObject.getString(DatabaseHelper.CUST_ADDRESS1);
                String cust_address2 = jsonObject.getString(DatabaseHelper.CUST_ADDRESS2);
                String pincode = jsonObject.getString(DatabaseHelper.PINCODE);
                String city = jsonObject.getString(DatabaseHelper.CITY);
                String state = jsonObject.getString(DatabaseHelper.STATE);
                String landmark = jsonObject.getString(DatabaseHelper.LANDMARK);
                String cust_contact1 = jsonObject.getString(DatabaseHelper.CUST_CONTACT1);
                String alternate_no = jsonObject.getString(DatabaseHelper.ALTERNATE_NO);
                String orderNumber = jsonObject.getString(DatabaseHelper.ORDERNUMBER);
                String packagesNo = jsonObject.getString(DatabaseHelper.PACKAGESNO);
                String DRSNumber = jsonObject.getString(DatabaseHelper.DRSNUMBER);
                String TotalDocketsInDRS = jsonObject.getString(DatabaseHelper.TOTALDOCKETSINDRS);
                String ProdcutDesc = jsonObject.getString(DatabaseHelper.PRODCUTDESC);
                String docket_number = jsonObject.getString(DatabaseHelper.DOCKET_NUMBER);
                String choice_of_payment = jsonObject.getString(DatabaseHelper.CHOICE_OF_PAYMENT);
                String amount_tobe_paid = jsonObject.getString(DatabaseHelper.AMOUNT_TOBE_PAID);
                String due_date = jsonObject.getString(DatabaseHelper.DUE_DATE);
                String boy_id = jsonObject.getString(DatabaseHelper.BOY_ID);
                String boy_name = jsonObject.getString(DatabaseHelper.BOY_NAME);
                String status = jsonObject.getString(DatabaseHelper.STATUS);
                String reason_id = jsonObject.getString(DatabaseHelper.REASON_ID);
                String failed_reason = jsonObject.getString(DatabaseHelper.FAILED_REASON);
                String delivery_time = jsonObject.getString(DatabaseHelper.DELIVERY_TIME);
                String comments = jsonObject.getString(DatabaseHelper.COMMENTS);
                String mode_of_payment = jsonObject.getString(DatabaseHelper.MODE_OF_PAYMENT);
                String transaction_no = jsonObject.getString(DatabaseHelper.TRANSACTION_NO);
                String auth_code = jsonObject.getString(DatabaseHelper.AUTH_CODE);
                String card_type = jsonObject.getString(DatabaseHelper.CARD_TYPE);
                String bank_name = jsonObject.getString(DatabaseHelper.BANK_NAME);
                String customer_sign = jsonObject.getString(DatabaseHelper.CUSTOMER_SIGN);
                String customer_img = jsonObject.getString(DatabaseHelper.CUSTOMER_IMG);
                String customer_img2 = jsonObject.getString(DatabaseHelper.CUSTOMER_IMG2);
                String customer_img3 = jsonObject.getString(DatabaseHelper.CUSTOMER_IMG3);
                String happy_delivery = jsonObject.getString(DatabaseHelper.HAPPY_DELIVERY);
                String happy_delivery_img = jsonObject.getString(DatabaseHelper.HAPPY_DELIVERY_IMG);
                String latitude = jsonObject.getString(DatabaseHelper.LATITUDE);
                String longitude = jsonObject.getString(DatabaseHelper.LONGITUDE);
                String sku_details = jsonObject.getString(DatabaseHelper.SKU_DETAILS);
                String last_updated = jsonObject.getString(DatabaseHelper.LAST_UPDATED);
                String preferred_transaction_time = jsonObject.getString(DatabaseHelper.PREFERRED_TRANSACTION_TIME);
                String original_amount_paid = jsonObject.getString(DatabaseHelper.ORIGINAL_AMOUNT_PAID);
                String user_id = jsonObject.getString(DatabaseHelper.USER_ID);
                String delivered_to = jsonObject.getString(DatabaseHelper.DELIVERED_TO);
                String delivered_to_relation = jsonObject.getString(DatabaseHelper.DELIVERED_TO_RELATION);
                String prirority_delivery = jsonObject.getString(DatabaseHelper.PRIRORITY_DELIVERY);
                String closing_km = jsonObject.getString(DatabaseHelper.CLOSING_KM);
                String imei_number = jsonObject.getString(DatabaseHelper.IMEI_NUMBER);
                String sync_update = jsonObject.getString(DatabaseHelper.SYNC_UPDATE);
                String updated_on_android = jsonObject.getString(DatabaseHelper.UPDATED_ON_ANDROID);
                String updated_on_webx = jsonObject.getString(DatabaseHelper.UPDATED_ON_WEBX);
                String loc_id = jsonObject.getString(DatabaseHelper.LOC_ID);
                String c_id = jsonObject.getString(DatabaseHelper.C_ID);
                String admin_id = jsonObject.getString(DatabaseHelper.ADMIN_ID);
                String extra1 = jsonObject.getString(DatabaseHelper.EXTRA1);
                String extra2 = jsonObject.getString(DatabaseHelper.EXTRA2);
                String extra3 = jsonObject.getString(DatabaseHelper.EXTRA3);
                String extra4 = jsonObject.getString(DatabaseHelper.EXTRA4);
                String extra5 = jsonObject.getString(DatabaseHelper.EXTRA5);
                String extra6 = jsonObject.getString(DatabaseHelper.EXTRA6);
                String sync_flag = jsonObject.getString(DatabaseHelper.SYNC_FLAG);
                String last_lat = jsonObject.getString(DatabaseHelper.LAST_LAT);
                String last_lng = jsonObject.getString(DatabaseHelper.LAST_LNG);
                String last_attempt_status = jsonObject.getString(DatabaseHelper.LAST_ATTEMPT_STATUS);
                String attempt_number = jsonObject.getString(DatabaseHelper.ATTEMPT_NUMBER);
                String attempt_slot = jsonObject.getString(DatabaseHelper.ATTEMPT_SLOT);
                String nps_score = jsonObject.getString(DatabaseHelper.NPS_SCORE);
                String seq_predicted = jsonObject.getString(DatabaseHelper.SEQ_PREDICTED);
                String seq_selected = jsonObject.getString(DatabaseHelper.SEQ_SELECTED);
                String seq_transaction = jsonObject.getString(DatabaseHelper.SEQ_TRANSACTION);
                String user_comments = jsonObject.getString(DatabaseHelper.USER_COMMENTS);
                String battery_level = jsonObject.getString(DatabaseHelper.BATTERY_LEVEL);
                String time_erp_import_initiated = jsonObject.getString(DatabaseHelper.TIME_ERP_IMPORT_INITIATED);
                String time_erp_import_completed = jsonObject.getString(DatabaseHelper.TIME_ERP_IMPORT_COMPLETED);
                String time_available_android_initiated = jsonObject.getString(DatabaseHelper.TIME_AVAILABLE_ANDROID_INITIATED);
                String time_available_android_completed = jsonObject.getString(DatabaseHelper.TIME_AVAILABLE_ANDROID_COMPLETED);
                String reverse_docket_number = jsonObject.getString(DatabaseHelper.REVERSE_DOCKET_NUMBER);
                String drs_docket = jsonObject.getString(DatabaseHelper.DRS_DOCKET);
                String sync = jsonObject.getString(DatabaseHelper.SYNC);
                String product_description = jsonObject.getString(DatabaseHelper.PRODUCT_DESCRIPTION);
                String reason_for_return = jsonObject.getString(DatabaseHelper.REASON_FOR_RETURN);
                String return_request_id = jsonObject.getString(DatabaseHelper.RETURN_REQUEST_ID);
                String tp_code = jsonObject.getString(DatabaseHelper.TP_CODE);
                String return_pincode = jsonObject.getString(DatabaseHelper.RETURN_PINCODE);
                String receipt_url = jsonObject.getString(DatabaseHelper.RECEIPT_URL);

                DeliveryEntity delivery = new DeliveryEntity();
                delivery.setjobtype(jobtype);
                delivery.setcust_name(cust_name);
                delivery.setcust_address1(cust_address1);
                delivery.setcust_address2(cust_address2);
                delivery.setpincode(pincode);
                delivery.setcity(city);
                delivery.setstate(state);
                delivery.setlandmark(landmark);
                delivery.setcust_contact1(cust_contact1);
                delivery.setalternate_no(alternate_no);
                delivery.setorderNumber(orderNumber);
                delivery.setpackagesNo(packagesNo);
                delivery.setDRSNumber(DRSNumber);
                delivery.setTotalDocketsInDRS(TotalDocketsInDRS);
                delivery.setProdcutDesc(ProdcutDesc);
                delivery.setdocket_number(docket_number);
                delivery.setchoice_of_payment(choice_of_payment);
                delivery.setamount_tobe_paid(amount_tobe_paid);
                delivery.setdue_date(due_date);
                delivery.setboy_id(boy_id);
                delivery.setboy_name(boy_name);
                delivery.setstatus(status);
                delivery.setreason_id(reason_id);
                delivery.setfailed_reason(failed_reason);
                delivery.setdelivery_time(delivery_time);
                delivery.setcomments(comments);
                delivery.setmode_of_payment(mode_of_payment);
                delivery.settransaction_no(transaction_no);
                delivery.setauth_code(auth_code);
                delivery.setcard_type(card_type);
                delivery.setbank_name(bank_name);
//                delivery.setcustomer_wallet_contact_number(customer_wallet_contact_number);
                delivery.setcustomer_sign(customer_sign);
                delivery.setcustomer_img(customer_img);
                delivery.setcustomer_img2(customer_img2);
                delivery.setcustomer_img3(customer_img3);
                delivery.sethappy_delivery(happy_delivery);
                delivery.sethappy_delivery_img(happy_delivery_img);
                delivery.setlatitude(latitude);
                delivery.setlongitude(longitude);
                delivery.setsku_details(sku_details);
                delivery.setlast_updated(last_updated);
                delivery.setpreferred_transaction_time(preferred_transaction_time);
                delivery.setoriginal_amount_paid(original_amount_paid);
                delivery.setuser_id(user_id);
                delivery.setdelivered_to(delivered_to);
                delivery.setdelivered_to_relation(delivered_to_relation);
                delivery.setprirority_delivery(prirority_delivery);
                delivery.setclosing_km(closing_km);
                delivery.setimei_number(imei_number);
                delivery.setsync_update(sync_update);
                delivery.setupdated_on_android(updated_on_android);
                delivery.setupdated_on_webx(updated_on_webx);
                delivery.setloc_id(loc_id);
                delivery.setc_id(c_id);
                delivery.setadmin_id(admin_id);
                delivery.setextra1(extra1);
                delivery.setextra2(extra2);
                delivery.setextra3(extra3);
                delivery.setextra4(extra4);
                delivery.setextra5(extra5);
                delivery.setextra6(extra6);
                delivery.setsync_flag(sync_flag);
                delivery.setlast_lat(last_lat);
                delivery.setlast_lng(last_lng);
                delivery.setlast_attempt_status(last_attempt_status);
                delivery.setattempt_number(attempt_number);
                delivery.setattempt_slot(attempt_slot);
                delivery.setnps_score(nps_score);
                delivery.setseq_predicted(seq_predicted);
                delivery.setseq_selected(seq_selected);
                delivery.setseq_transaction(seq_transaction);
                delivery.setuser_comments(user_comments);
                delivery.setbattery_level(battery_level);
                delivery.settime_erp_import_initiated(time_erp_import_initiated);
                delivery.settime_erp_import_completed(time_erp_import_completed);
                delivery.settime_available_android_initiated(time_available_android_initiated);
                delivery.settime_available_android_completed(time_available_android_completed);
                delivery.setreverse_docket_number(reverse_docket_number);
                delivery.setdrs_docket(drs_docket);
                delivery.setsync(sync);
                delivery.setproduct_description(product_description);
                delivery.setreason_for_return(reason_for_return);
                delivery.setreturn_request_id(return_request_id);
                delivery.settp_code(tp_code);
                delivery.setreturn_pincode(return_pincode);
                delivery.setreceipt_url(receipt_url);

                DeliveryHelper.getInstance().insertOrUpdate(delivery);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int getAmount(String paymentType, String status) {
        int amount=0;
        SQLiteDatabase db = DatabaseHelper.getInstance().getWritableDatabase();
        Cursor cursor = null;
        if(status.equalsIgnoreCase("1")) {
            cursor = db.rawQuery("select SUM(original_amount_paid) from  " + DatabaseHelper.DELIVERY_TABLE_NAME + " where " + DatabaseHelper.MODE_OF_PAYMENT + " = ? AND " + DatabaseHelper.STATUS + " = ?", new String[]{paymentType, status});
        } else {

        }
        if(cursor!=null && cursor.getCount()>0)
        {
            cursor.moveToFirst();
            amount= cursor.getInt(0);
        }
        return amount;


    }

    public int getSODTotal() {
        int amount=0;
        SQLiteDatabase db = DatabaseHelper.getInstance().getWritableDatabase();
        Cursor cursor =  db.rawQuery( "select SUM(original_amount_paid) from  " + DatabaseHelper.DELIVERY_TABLE_NAME + " where " + DatabaseHelper.MODE_OF_PAYMENT + " = ? OR "+DatabaseHelper.MODE_OF_PAYMENT+" = ? ", new String[] {Constants.PAYMENT_TYPE_EZETAP,Constants.PAYMENT_TYPE_MSWIPE});
        if(cursor!=null && cursor.getCount()>0)
        {
            cursor.moveToFirst();
            amount= cursor.getInt(0);
        }
        return amount;


    }
}