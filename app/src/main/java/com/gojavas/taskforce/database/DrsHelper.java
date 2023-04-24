package com.gojavas.taskforce.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.gojavas.taskforce.entity.DrsEntity;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by GJS280 on 16/4/2015.
 */
public class DrsHelper {

    // DRS table fields
    public static final String SR = "SR";
    public static final String JOBTYPE = "Job Type";
    public static final String DOCKETNO = "Docket Number";
    public static final String EXCHANGE_REQUESTID = "Exchange RequestId";
    public static final String DRSNO = "Runsheet Number";
    public static final String DRS_DOCKET = "Drs Docket";
    public static final String PIECES = "Pieces";
    public static final String CSGENM = "Consignee Name";
    public static final String CSGEADDR = "Consignee Address";
    public static final String ADDRESS_TYPE = "Address Type";
    public static final String PICKUP_LOCATION = "Pickup Location";
    public static final String TIMETOEND = "Time to End";
    public static final String CSGETELENO = "Consignee Telephone Number";
    public static final String ALTERNATE_NUMBER = "Alternate Number";
    public static final String CTR_NO = "CTR Number";
    public static final String CSGECITY = "Consignee City";
    public static final String REASSIGN_DESTCD = "Reassign Destination code";
    public static final String CSGEPINCODE = "Consignee Pincode";
    public static final String PKGSNO = "Package Number";
    public static final String COD_DOD = "Mode of Payment";
    public static final String COD_AMT = "Amount";
    public static final String DELIVERED = "Delivered";
    public static final String DRSUPDATED = "DRS Updated";
    public static final String LOGISTIC_DT = "Logistic Date";
    public static final String LOGISTIC_TIME = "Logistic Time";
    public static final String START_KM = "Start km";
    public static final String TOTAL_DOCKETS_IN_DRS = "Total Dockets in Runsheet";
    public static final String DKT_COUNT_NOT_UPDATED = "Docket count not Updated";
    public static final String DRIVER_NAME = "Driver Name";
    public static final String DRIVER_ID = "Driver Id";
    public static final String VEHICLE_NUMBER = "Vehicle Number";
    public static final String ACTUWT = "Actual Weight";
    public static final String USERID = "User Id";
    public static final String CLIENT_CODE = "Client Code";
    public static final String CLIENT_NAME = "Client Name";
    public static final String NEXTATTEMPTDATE = "Next Attempt Date";
    public static final String PRODCD = "PROD CD";
    public static final String CODEDESC = "Code Description";
    public static final String AMOUNT_TO_CUTOMER = "Amount to Customer";
    public static final String ENTRYDATE = "Entry Date";
    public static final String ENTRYBY = "Entry By";
    public static final String LASTEDITDATE = "Last Edit Date";
    public static final String LASTEDITBY = "Last Edit By";
    public static final String STATUS = "Status";
    public static final String RESPONSEXML = "Response XML";
    public static final String FAILREASON = "Fail Reason";
    public static final String RESPONSE_DATETIME = "Response Datetime";
    public static final String SLOTNO = "Slot Number";
    public static final String LATITUDE = "Latitude";
    public static final String LONGITUDE = "Longitude";
    public static final String ORDERNUMBER = "Order Number";
    public static final String CHOICEOFPAYMENT = "Choice of Payment";
    public static final String DATE = "Date";
    public static final String OLDDOCKETNO = "Old Docket Number";
    public static final String SELLERNAME = "Seller Name";
    public static final String CONTACT_PERSON = "Contact Person";
    public static final String ATTEMPT = "Attempt";
    public static final String MOBILE_PULL_STATUS = "Mobile Pull Status";
    public static final String POSITION = "Position";
    public static final String PRODUCT_DESCRIPTION = "Product Description";
    public static final String REASON_FOR_RETURN = "Reason For Return";
    public static final String RETURN_REQUEST_ID = "Return Request ID";
    public static final String TP_CODE = "TP Code";
    public static final String RETURN_PINCODE = "Return Pincode";

    private static DrsHelper instance = null;

    public static DrsHelper getInstance() {
        if(instance == null) {
            instance = new DrsHelper();
        }
        return instance;
    }

    /**
     * Get all pending drs
     * @return
     */
    public ArrayList<DrsEntity> getAllPendingDrs() {
        ArrayList<DrsEntity> arrayList = new ArrayList<>();
        SQLiteDatabase db = DatabaseHelper.getInstance().getReadableDatabase();
        Cursor cursor =  db.rawQuery( "select * from  " + DatabaseHelper.DRS_TABLE_NAME + " drs where drs." + DatabaseHelper.DRS_DOCKET + " not in (select " + DatabaseHelper.DRS_DOCKET + " from " + DatabaseHelper.DELIVERY_TABLE_NAME + ") order by " + DatabaseHelper.POSITION + " asc", null);
        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            while(cursor.isAfterLast() == false) {
                DrsEntity drs = getDrsEntity(cursor);
                arrayList.add(drs);
                cursor.moveToNext();
            }
        }
        cursor.close();
        return arrayList;
    }

    /**
     * Get all success or fail drs
     * @return
     */
    public ArrayList<DrsEntity> getAllSuccessFailDrs(String type) {
        ArrayList<DrsEntity> arrayList = new ArrayList<>();
        SQLiteDatabase db = DatabaseHelper.getInstance().getReadableDatabase();
        String query = "SELECT d.* FROM " + DatabaseHelper.DRS_TABLE_NAME + " d INNER JOIN " + DatabaseHelper.DELIVERY_TABLE_NAME + " ON (d." + DatabaseHelper.DRS_DOCKET + " = delivery." + DatabaseHelper.DRS_DOCKET + " AND delivery." + DatabaseHelper.STATUS + " = '" + type + "') order by " + DatabaseHelper.POSITION + " asc";
        Cursor cursor =  db.rawQuery( query, null);
        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            while(cursor.isAfterLast() == false) {
                DrsEntity drs = getDrsEntity(cursor);
                arrayList.add(drs);
                cursor.moveToNext();
            }
        }
        cursor.close();
        return arrayList;
    }

    /**
     * Get pending drs count
     * @return
     */
    public int getPendingDrsCount() {
        SQLiteDatabase db = DatabaseHelper.getInstance().getReadableDatabase();
        Cursor cursor =  db.rawQuery( "select * from  " + DatabaseHelper.DRS_TABLE_NAME + " drs where drs." + DatabaseHelper.DRS_DOCKET + " not in (select " + DatabaseHelper.DRS_DOCKET + " from " + DatabaseHelper.DELIVERY_TABLE_NAME + ") order by " + DatabaseHelper.POSITION + " asc", null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    /**
     * Get success drs count
     * @return
     */
    public int getSuccessDrsCount() {
        SQLiteDatabase db = DatabaseHelper.getInstance().getReadableDatabase();
        String query = "SELECT d.* FROM " + DatabaseHelper.DRS_TABLE_NAME + " d INNER JOIN " + DatabaseHelper.DELIVERY_TABLE_NAME + " ON (d." + DatabaseHelper.DRS_DOCKET + " = delivery." + DatabaseHelper.DRS_DOCKET + " AND delivery." + DatabaseHelper.STATUS + " = '1')";
        Cursor cursor =  db.rawQuery( query, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    /**
     * Get failed drs count
     * @return
     */
    public int getFailedDrsCount() {
        SQLiteDatabase db = DatabaseHelper.getInstance().getReadableDatabase();
        String query = "SELECT d.* FROM " + DatabaseHelper.DRS_TABLE_NAME + " d INNER JOIN " + DatabaseHelper.DELIVERY_TABLE_NAME + " ON (d." + DatabaseHelper.DRS_DOCKET + " = delivery." + DatabaseHelper.DRS_DOCKET + " AND delivery." + DatabaseHelper.STATUS + " = '0') order by " + DatabaseHelper.POSITION + " asc";
        Cursor cursor =  db.rawQuery( query, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    /**
     * Get all drs
     * @return
     */
    public DrsEntity getDrs(String drs_docket) {
        DrsEntity drs = new DrsEntity();
        SQLiteDatabase db = DatabaseHelper.getInstance().getReadableDatabase();
        Cursor cursor =  db.rawQuery( "select * from  " + DatabaseHelper.DRS_TABLE_NAME + " where " + DatabaseHelper.DRS_DOCKET + " = ? ", new String[] {drs_docket});
        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            while(cursor.isAfterLast() == false) {
                drs = getDrsEntity(cursor);
                cursor.moveToNext();
                break;
            }
        }
        cursor.close();
        return drs;
    }

    /**
     * Get all drs
     * @return
     */
    public DrsEntity getDrsDetailData(String drs_docket) {
        DrsEntity drs = new DrsEntity();
        SQLiteDatabase db = DatabaseHelper.getInstance().getReadableDatabase();
        Cursor cursor =  db.rawQuery( "select * from  " + DatabaseHelper.DRS_TABLE_NAME + " where " + DatabaseHelper.DRS_DOCKET + " = ? ", new String[] {drs_docket});
        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            while(cursor.isAfterLast() == false) {
                drs = getDrsEntity(cursor);
                cursor.moveToNext();
                break;
            }
        }
        cursor.close();
        return drs;
    }

    /**
     * Get all drs
     * @return
     */
    public DrsEntity getDrsData(String drs_docket) {
        DrsEntity drs = new DrsEntity();
        SQLiteDatabase db = DatabaseHelper.getInstance().getReadableDatabase();
        Cursor cursor =  db.rawQuery( "select * from  " + DatabaseHelper.DRS_TABLE_NAME + " where " + DatabaseHelper.DRS_DOCKET + " = ? ", new String[] {drs_docket});
        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            while(cursor.isAfterLast() == false) {
                drs = getDrsEntity(cursor);
                cursor.moveToNext();
                break;
            }
        }
        cursor.close();
        return drs;
    }

    /**
     * Get all drs
     * @return
     */
    public HashMap<String, String> getDrsDetail(String drsdocket) {
        HashMap<String, String> drsMap = new HashMap<>();
        SQLiteDatabase db = DatabaseHelper.getInstance().getReadableDatabase();
        Cursor cursor =  db.rawQuery( "select * from  " + DatabaseHelper.DRS_TABLE_NAME + " where " + DatabaseHelper.DRS_DOCKET + " = ? ", new String[] {drsdocket});
        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            while(cursor.isAfterLast() == false) {

                String sr = cursor.getString(cursor.getColumnIndex(DatabaseHelper.SR));
                String jobtype = cursor.getString(cursor.getColumnIndex(DatabaseHelper.JOBTYPE));
                String docketno = cursor.getString(cursor.getColumnIndex(DatabaseHelper.DOCKETNO));
                String exchange_requestid = cursor.getString(cursor.getColumnIndex(DatabaseHelper.EXCHANGE_REQUESTID));
                String drsno = cursor.getString(cursor.getColumnIndex(DatabaseHelper.DRSNO));
                String drs_docket = cursor.getString(cursor.getColumnIndex(DatabaseHelper.DRS_DOCKET));
                String pieces = cursor.getString(cursor.getColumnIndex(DatabaseHelper.PIECES));
                String csgenm = cursor.getString(cursor.getColumnIndex(DatabaseHelper.CSGENM));
                String csgeaddr = cursor.getString(cursor.getColumnIndex(DatabaseHelper.CSGEADDR));
                String address_type = cursor.getString(cursor.getColumnIndex(DatabaseHelper.ADDRESS_TYPE));
                String pickup_location = cursor.getString(cursor.getColumnIndex(DatabaseHelper.PICKUP_LOCATION));
                String timetoend = cursor.getString(cursor.getColumnIndex(DatabaseHelper.TIMETOEND));
                String csgeteleno = cursor.getString(cursor.getColumnIndex(DatabaseHelper.CSGETELENO));
                String alternate_number = cursor.getString(cursor.getColumnIndex(DatabaseHelper.ALTERNATE_NUMBER));
                String ctr_no = cursor.getString(cursor.getColumnIndex(DatabaseHelper.CTR_NO));
                String csgecity = cursor.getString(cursor.getColumnIndex(DatabaseHelper.CSGECITY));
                String reassign_destcd = cursor.getString(cursor.getColumnIndex(DatabaseHelper.REASSIGN_DESTCD));
                String csgepincode = cursor.getString(cursor.getColumnIndex(DatabaseHelper.CSGEPINCODE));
                String pkgsno = cursor.getString(cursor.getColumnIndex(DatabaseHelper.PKGSNO));
                String cod_dod = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COD_DOD));
                String cod_amt = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COD_AMT));
                String delivered = cursor.getString(cursor.getColumnIndex(DatabaseHelper.DELIVERED));
                String drsupdated = cursor.getString(cursor.getColumnIndex(DatabaseHelper.DRSUPDATED));
                String logistic_dt = cursor.getString(cursor.getColumnIndex(DatabaseHelper.LOGISTIC_DT));
                String logistic_time = cursor.getString(cursor.getColumnIndex(DatabaseHelper.LOGISTIC_TIME));
                String start_km = cursor.getString(cursor.getColumnIndex(DatabaseHelper.START_KM));
                String total_dockets_in_drs = cursor.getString(cursor.getColumnIndex(DatabaseHelper.TOTAL_DOCKETS_IN_DRS));
                String dkt_count_not_updated = cursor.getString(cursor.getColumnIndex(DatabaseHelper.DKT_COUNT_NOT_UPDATED));
                String driver_name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.DRIVER_NAME));
                String driver_id = cursor.getString(cursor.getColumnIndex(DatabaseHelper.DRIVER_ID));
                String vehicle_number = cursor.getString(cursor.getColumnIndex(DatabaseHelper.VEHICLE_NUMBER));
                String actuwt = cursor.getString(cursor.getColumnIndex(DatabaseHelper.ACTUWT));
                String userid = cursor.getString(cursor.getColumnIndex(DatabaseHelper.USERID));
                String client_code = cursor.getString(cursor.getColumnIndex(DatabaseHelper.CLIENT_CODE));
                String client_name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.CLIENT_NAME));
                String nextattemptdate = cursor.getString(cursor.getColumnIndex(DatabaseHelper.NEXTATTEMPTDATE));
                String prodcd = cursor.getString(cursor.getColumnIndex(DatabaseHelper.PRODCD));
                String codedesc = cursor.getString(cursor.getColumnIndex(DatabaseHelper.CODEDESC));
                String amount_to_cutomer = cursor.getString(cursor.getColumnIndex(DatabaseHelper.AMOUNT_TO_CUTOMER));
                String entrydate = cursor.getString(cursor.getColumnIndex(DatabaseHelper.ENTRYDATE));
                String entryby = cursor.getString(cursor.getColumnIndex(DatabaseHelper.ENTRYBY));
                String lasteditdate = cursor.getString(cursor.getColumnIndex(DatabaseHelper.LASTEDITDATE));
                String lasteditby = cursor.getString(cursor.getColumnIndex(DatabaseHelper.LASTEDITBY));
                String status = cursor.getString(cursor.getColumnIndex(DatabaseHelper.STATUS));
                String responsexml = cursor.getString(cursor.getColumnIndex(DatabaseHelper.RESPONSEXML));
                String failreason = cursor.getString(cursor.getColumnIndex(DatabaseHelper.FAILREASON));
                String response_datetime = cursor.getString(cursor.getColumnIndex(DatabaseHelper.RESPONSE_DATETIME));
                String slotno = cursor.getString(cursor.getColumnIndex(DatabaseHelper.SLOTNO));
                String latitude = cursor.getString(cursor.getColumnIndex(DatabaseHelper.LATITUDE));
                String longitude = cursor.getString(cursor.getColumnIndex(DatabaseHelper.LONGITUDE));
                String ordernumber = cursor.getString(cursor.getColumnIndex(DatabaseHelper.ORDERNUMBER));
                String choiceofpayment = cursor.getString(cursor.getColumnIndex(DatabaseHelper.CHOICEOFPAYMENT));
                String date = cursor.getString(cursor.getColumnIndex(DatabaseHelper.DATE));
                String olddocketno = cursor.getString(cursor.getColumnIndex(DatabaseHelper.OLDDOCKETNO));
                String sellername = cursor.getString(cursor.getColumnIndex(DatabaseHelper.SELLERNAME));
                String contact_person = cursor.getString(cursor.getColumnIndex(DatabaseHelper.CONTACT_PERSON));
                String attempt = cursor.getString(cursor.getColumnIndex(DatabaseHelper.ATTEMPT));
                String mobile_pull_status = cursor.getString(cursor.getColumnIndex(DatabaseHelper.MOBILE_PULL_STATUS));
                int position = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.POSITION));
                String product_description = cursor.getString(cursor.getColumnIndex(DatabaseHelper.PRODUCT_DESCRIPTION));
                String reason_for_return = cursor.getString(cursor.getColumnIndex(DatabaseHelper.REASON_FOR_RETURN));
                String return_request_id = cursor.getString(cursor.getColumnIndex(DatabaseHelper.RETURN_REQUEST_ID));
                String tp_code = cursor.getString(cursor.getColumnIndex(DatabaseHelper.TP_CODE));
                String return_pincode = cursor.getString(cursor.getColumnIndex(DatabaseHelper.RETURN_PINCODE));

                drsMap.put(DrsHelper.SR, sr);
                drsMap.put(DrsHelper.JOBTYPE, jobtype);
                drsMap.put(DrsHelper.DOCKETNO, docketno);
                drsMap.put(DrsHelper.EXCHANGE_REQUESTID, exchange_requestid);
                drsMap.put(DrsHelper.DRSNO, drsno);
                drsMap.put(DrsHelper.DRS_DOCKET, drs_docket);
                drsMap.put(DrsHelper.PIECES, pieces);
                drsMap.put(DrsHelper.CSGENM, csgenm);
                drsMap.put(DrsHelper.CSGEADDR, csgeaddr);
                drsMap.put(DrsHelper.ADDRESS_TYPE, address_type);
                drsMap.put(DrsHelper.PICKUP_LOCATION, pickup_location);
                drsMap.put(DrsHelper.TIMETOEND, timetoend);
                drsMap.put(DrsHelper.CSGETELENO, csgeteleno);
                drsMap.put(DrsHelper.ALTERNATE_NUMBER, alternate_number);
                drsMap.put(DrsHelper.CTR_NO, ctr_no);
                drsMap.put(DrsHelper.CSGECITY, csgecity);
                drsMap.put(DrsHelper.REASSIGN_DESTCD, reassign_destcd);
                drsMap.put(DrsHelper.CSGEPINCODE, csgepincode);
                drsMap.put(DrsHelper.PKGSNO, pkgsno);
                drsMap.put(DrsHelper.COD_DOD, cod_dod);
                drsMap.put(DrsHelper.COD_AMT, cod_amt);
                drsMap.put(DrsHelper.DELIVERED, delivered);
                drsMap.put(DrsHelper.DRSUPDATED, drsupdated);
                drsMap.put(DrsHelper.LOGISTIC_DT, logistic_dt);
                drsMap.put(DrsHelper.LOGISTIC_TIME, logistic_time);
                drsMap.put(DrsHelper.START_KM, start_km);
                drsMap.put(DrsHelper.TOTAL_DOCKETS_IN_DRS, total_dockets_in_drs);
                drsMap.put(DrsHelper.DKT_COUNT_NOT_UPDATED, dkt_count_not_updated);
                drsMap.put(DrsHelper.DRIVER_NAME, driver_name);
                drsMap.put(DrsHelper.DRIVER_ID, driver_id);
                drsMap.put(DrsHelper.VEHICLE_NUMBER, vehicle_number);
                drsMap.put(DrsHelper.ACTUWT, actuwt);
                drsMap.put(DrsHelper.USERID, userid);
                drsMap.put(DrsHelper.CLIENT_CODE, client_code);
                drsMap.put(DrsHelper.CLIENT_NAME, client_name);
                drsMap.put(DrsHelper.NEXTATTEMPTDATE, nextattemptdate);
                drsMap.put(DrsHelper.PRODCD, prodcd);
                drsMap.put(DrsHelper.CODEDESC, codedesc);
                drsMap.put(DrsHelper.AMOUNT_TO_CUTOMER, amount_to_cutomer);
                drsMap.put(DrsHelper.ENTRYDATE, entrydate);
                drsMap.put(DrsHelper.ENTRYBY, entryby);
                drsMap.put(DrsHelper.LASTEDITDATE, lasteditdate);
                drsMap.put(DrsHelper.LASTEDITBY, lasteditby);
                drsMap.put(DrsHelper.STATUS, status);
                drsMap.put(DrsHelper.RESPONSEXML, responsexml);
                drsMap.put(DrsHelper.FAILREASON, failreason);
                drsMap.put(DrsHelper.RESPONSE_DATETIME, response_datetime);
                drsMap.put(DrsHelper.SLOTNO, slotno);
                drsMap.put(DrsHelper.LATITUDE, latitude);
                drsMap.put(DrsHelper.LONGITUDE, longitude);
                drsMap.put(DrsHelper.ORDERNUMBER, ordernumber);
                drsMap.put(DrsHelper.CHOICEOFPAYMENT, choiceofpayment);
                drsMap.put(DrsHelper.DATE, date);
                drsMap.put(DrsHelper.OLDDOCKETNO, olddocketno);
                drsMap.put(DrsHelper.SELLERNAME, sellername);
                drsMap.put(DrsHelper.CONTACT_PERSON, contact_person);
                drsMap.put(DrsHelper.ATTEMPT, attempt);
                drsMap.put(DrsHelper.MOBILE_PULL_STATUS, mobile_pull_status);
                drsMap.put(DrsHelper.PRODUCT_DESCRIPTION, product_description);
                drsMap.put(DrsHelper.REASON_FOR_RETURN, reason_for_return);
                drsMap.put(DrsHelper.RETURN_REQUEST_ID, return_request_id);
                drsMap.put(DrsHelper.TP_CODE, tp_code);
                drsMap.put(DrsHelper.RETURN_PINCODE, return_pincode);

                cursor.moveToNext();
                break;
            }
        }
        cursor.close();
        return drsMap;
    }

    /**
     * Update drs if exist otherwise insert new drs
     * @param drs
     */
    public void insertOrUpdate(DrsEntity drs) {
        String drs_docket = drs.getdrs_docket();
        SQLiteDatabase db = DatabaseHelper.getInstance().getReadableDatabase();
        Cursor cursor =  db.rawQuery( "select " + DatabaseHelper.DRS_DOCKET + " from  " + DatabaseHelper.DRS_TABLE_NAME + " where " + DatabaseHelper.DRS_DOCKET + " = ? ", new String[] {drs_docket});
        if(cursor.getCount() > 0) {
            // Update drs
            updateDrs(drs);
        } else {
            // Insert drs
            insertDrs(drs);
        }
    }

    /**
     * Insert new drs
     * @param drs
     * @return
     */
    public long insertDrs(DrsEntity drs){
        SQLiteDatabase db = DatabaseHelper.getInstance().getWritableDatabase();
        ContentValues contentValues = getContentValues(drs);
        long rowid = db.insert(DatabaseHelper.DRS_TABLE_NAME, null, contentValues);
        return rowid;
    }

    /**
     * Update drs
     * @param drs
     */
    private void updateDrs(DrsEntity drs) {
        SQLiteDatabase db = DatabaseHelper.getInstance().getWritableDatabase();
        ContentValues contentValues = getContentValues(drs);
        db.update(DatabaseHelper.DRS_TABLE_NAME, contentValues, DatabaseHelper.DRS_DOCKET + " = ? ", new String[] {drs.getdrs_docket()});
    }

    /**
     * Get list of all the contact numbers
     * @return
     */
    public ArrayList<String> getContactNumber() {
        ArrayList<String> arrayList = new ArrayList<>();
        SQLiteDatabase db = DatabaseHelper.getInstance().getReadableDatabase();
        Cursor cursor =  db.rawQuery( "select " + DatabaseHelper.CSGETELENO + ", " + DatabaseHelper.ALTERNATE_NUMBER + " from " + DatabaseHelper.DRS_TABLE_NAME + " order by " + DatabaseHelper.POSITION + " asc", null);
        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            while(cursor.isAfterLast() == false) {
                String contactNo = cursor.getString(cursor.getColumnIndex(DatabaseHelper.CSGETELENO));
                String alternateContactNo = cursor.getString(cursor.getColumnIndex(DatabaseHelper.ALTERNATE_NUMBER));
                arrayList.add(contactNo);
                arrayList.add(alternateContactNo);
                cursor.moveToNext();
            }
        }
        cursor.close();
        return arrayList;
    }

    /**
     * Get list of all the contact numbers
     * @return
     */
    public ArrayList<String> getDocketDrsNumber(String contactNo) {
        ArrayList<String> detail = new ArrayList<>();
        SQLiteDatabase db = DatabaseHelper.getInstance().getReadableDatabase();
        Cursor cursor =  db.rawQuery( "select " + DatabaseHelper.DOCKETNO + ", " + DatabaseHelper.DRSNO + " from " + DatabaseHelper.DRS_TABLE_NAME + " where " + DatabaseHelper.CSGETELENO + " = ? OR " + DatabaseHelper.ALTERNATE_NUMBER + " = ? order by " + DatabaseHelper.ID + " desc", new String[] {contactNo, contactNo});
        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            while(cursor.isAfterLast() == false) {
                String docketNo = cursor.getString(cursor.getColumnIndex(DatabaseHelper.DOCKETNO));
                String drsNo = cursor.getString(cursor.getColumnIndex(DatabaseHelper.DRSNO));
                detail.add(docketNo);
                detail.add(drsNo);
                break;
            }
        }
        cursor.close();
        return detail;
    }

    /**
     * Delete Drs Table
     * @return
     */
    public boolean deleteDrsTable() {
        int doneDelete = 0;
        SQLiteDatabase db = DatabaseHelper.getInstance().getReadableDatabase();
        doneDelete = db.delete(DatabaseHelper.DRS_TABLE_NAME, null , null);
        db.close();
        return doneDelete > 0;
    }

    /**
     * Get Drs object
     * @param cursor
     * @return
     */
    private DrsEntity getDrsEntity(Cursor cursor) {
        String sr = cursor.getString(cursor.getColumnIndex(DatabaseHelper.SR));
        String jobtype = cursor.getString(cursor.getColumnIndex(DatabaseHelper.JOBTYPE));
        String docketno = cursor.getString(cursor.getColumnIndex(DatabaseHelper.DOCKETNO));
        String exchange_requestid = cursor.getString(cursor.getColumnIndex(DatabaseHelper.EXCHANGE_REQUESTID));
        String drsno = cursor.getString(cursor.getColumnIndex(DatabaseHelper.DRSNO));
        String drs_docket = cursor.getString(cursor.getColumnIndex(DatabaseHelper.DRS_DOCKET));
        String pieces = cursor.getString(cursor.getColumnIndex(DatabaseHelper.PIECES));
        String csgenm = cursor.getString(cursor.getColumnIndex(DatabaseHelper.CSGENM));
        String csgeaddr = cursor.getString(cursor.getColumnIndex(DatabaseHelper.CSGEADDR));
        String addess_type = cursor.getString(cursor.getColumnIndex(DatabaseHelper.ADDRESS_TYPE));
        String pickup_location = cursor.getString(cursor.getColumnIndex(DatabaseHelper.PICKUP_LOCATION));
        String timetoend = cursor.getString(cursor.getColumnIndex(DatabaseHelper.TIMETOEND));
        String csgeteleno = cursor.getString(cursor.getColumnIndex(DatabaseHelper.CSGETELENO));
        String alternate_number = cursor.getString(cursor.getColumnIndex(DatabaseHelper.ALTERNATE_NUMBER));
        String ctr_no = cursor.getString(cursor.getColumnIndex(DatabaseHelper.CTR_NO));
        String csgecity = cursor.getString(cursor.getColumnIndex(DatabaseHelper.CSGECITY));
        String reassign_destcd = cursor.getString(cursor.getColumnIndex(DatabaseHelper.REASSIGN_DESTCD));
        String csgepincode = cursor.getString(cursor.getColumnIndex(DatabaseHelper.CSGEPINCODE));
        String pkgsno = cursor.getString(cursor.getColumnIndex(DatabaseHelper.PKGSNO));
        String cod_dod = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COD_DOD));
        String cod_amt = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COD_AMT));
        String delivered = cursor.getString(cursor.getColumnIndex(DatabaseHelper.DELIVERED));
        String drsupdated = cursor.getString(cursor.getColumnIndex(DatabaseHelper.DRSUPDATED));
        String logistic_dt = cursor.getString(cursor.getColumnIndex(DatabaseHelper.LOGISTIC_DT));
        String logistic_time = cursor.getString(cursor.getColumnIndex(DatabaseHelper.LOGISTIC_TIME));
        String start_km = cursor.getString(cursor.getColumnIndex(DatabaseHelper.START_KM));
        String total_dockets_in_drs = cursor.getString(cursor.getColumnIndex(DatabaseHelper.TOTAL_DOCKETS_IN_DRS));
        String dkt_count_not_updated = cursor.getString(cursor.getColumnIndex(DatabaseHelper.DKT_COUNT_NOT_UPDATED));
        String driver_name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.DRIVER_NAME));
        String driver_id = cursor.getString(cursor.getColumnIndex(DatabaseHelper.DRIVER_ID));
        String vehicle_number = cursor.getString(cursor.getColumnIndex(DatabaseHelper.VEHICLE_NUMBER));
        String actuwt = cursor.getString(cursor.getColumnIndex(DatabaseHelper.ACTUWT));
        String userid = cursor.getString(cursor.getColumnIndex(DatabaseHelper.USERID));
        String client_code = cursor.getString(cursor.getColumnIndex(DatabaseHelper.CLIENT_CODE));
        String client_name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.CLIENT_NAME));
        String nextattemptdate = cursor.getString(cursor.getColumnIndex(DatabaseHelper.NEXTATTEMPTDATE));
        String prodcd = cursor.getString(cursor.getColumnIndex(DatabaseHelper.PRODCD));
        String codedesc = cursor.getString(cursor.getColumnIndex(DatabaseHelper.CODEDESC));
        String amount_to_cutomer = cursor.getString(cursor.getColumnIndex(DatabaseHelper.AMOUNT_TO_CUTOMER));
        String entrydate = cursor.getString(cursor.getColumnIndex(DatabaseHelper.ENTRYDATE));
        String entryby = cursor.getString(cursor.getColumnIndex(DatabaseHelper.ENTRYBY));
        String lasteditdate = cursor.getString(cursor.getColumnIndex(DatabaseHelper.LASTEDITDATE));
        String lasteditby = cursor.getString(cursor.getColumnIndex(DatabaseHelper.LASTEDITBY));
        String status = cursor.getString(cursor.getColumnIndex(DatabaseHelper.STATUS));
        String responsexml = cursor.getString(cursor.getColumnIndex(DatabaseHelper.RESPONSEXML));
        String failreason = cursor.getString(cursor.getColumnIndex(DatabaseHelper.FAILREASON));
        String response_datetime = cursor.getString(cursor.getColumnIndex(DatabaseHelper.RESPONSE_DATETIME));
        String slotno = cursor.getString(cursor.getColumnIndex(DatabaseHelper.SLOTNO));
        String latitude = cursor.getString(cursor.getColumnIndex(DatabaseHelper.LATITUDE));
        String longitude = cursor.getString(cursor.getColumnIndex(DatabaseHelper.LONGITUDE));
        String ordernumber = cursor.getString(cursor.getColumnIndex(DatabaseHelper.ORDERNUMBER));
        String choiceofpayment = cursor.getString(cursor.getColumnIndex(DatabaseHelper.CHOICEOFPAYMENT));
        String date = cursor.getString(cursor.getColumnIndex(DatabaseHelper.DATE));
        String olddocketno = cursor.getString(cursor.getColumnIndex(DatabaseHelper.OLDDOCKETNO));
        String sellername = cursor.getString(cursor.getColumnIndex(DatabaseHelper.SELLERNAME));
        String contact_person = cursor.getString(cursor.getColumnIndex(DatabaseHelper.CONTACT_PERSON));
        String attempt = cursor.getString(cursor.getColumnIndex(DatabaseHelper.ATTEMPT));
        String mobile_pull_status = cursor.getString(cursor.getColumnIndex(DatabaseHelper.MOBILE_PULL_STATUS));
        int position = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.POSITION));
        String product_description = cursor.getString(cursor.getColumnIndex(DatabaseHelper.PRODUCT_DESCRIPTION));
        String reason_for_return = cursor.getString(cursor.getColumnIndex(DatabaseHelper.REASON_FOR_RETURN));
        String _return_request_id = cursor.getString(cursor.getColumnIndex(DatabaseHelper.RETURN_REQUEST_ID));
        String tp_code = cursor.getString(cursor.getColumnIndex(DatabaseHelper.TP_CODE));
        String return_pincode = cursor.getString(cursor.getColumnIndex(DatabaseHelper.RETURN_PINCODE));

        DrsEntity drs = new DrsEntity();
        drs.setsr(sr);
        drs.setjobtype(jobtype);
        drs.setdocketno(docketno);
        drs.setexchange_requestid(exchange_requestid);
        drs.setdrsno(drsno);
        drs.setdrs_docket(drs_docket);
        drs.setpieces(pieces);
        drs.setcsgenm(csgenm);
        drs.setcsgeaddr(csgeaddr);
        drs.setaddress_type(addess_type);
        drs.setpickup_location(pickup_location);
        drs.settimetoend(timetoend);
        drs.setcsgeteleno(csgeteleno);
        drs.setalternate_number(alternate_number);
        drs.setctr_no(ctr_no);
        drs.setcsgecity(csgecity);
        drs.setreassign_destcd(reassign_destcd);
        drs.setcsgepincode(csgepincode);
        drs.setpkgsno(pkgsno);
        drs.setcod_dod(cod_dod);
        drs.setcod_amt(cod_amt);
        drs.setdelivered(delivered);
        drs.setdrsupdated(drsupdated);
        drs.setlogistic_dt(logistic_dt);
        drs.setlogistic_time(logistic_time);
        drs.setstart_km(start_km);
        drs.settotal_dockets_in_drs(total_dockets_in_drs);
        drs.setdkt_count_not_updated(dkt_count_not_updated);
        drs.setdriver_name(driver_name);
        drs.setdriver_id(driver_id);
        drs.setvehicle_number(vehicle_number);
        drs.setactuwt(actuwt);
        drs.setuserid(userid);
        drs.setclient_code(client_code);
        drs.setclient_name(client_name);
        drs.setnextattemptdate(nextattemptdate);
        drs.setprodcd(prodcd);
        drs.setcodedesc(codedesc);
        drs.setamount_to_cutomer(amount_to_cutomer);
        drs.setentrydate(entrydate);
        drs.setentryby(entryby);
        drs.setlasteditdate(lasteditdate);
        drs.setlasteditby(lasteditby);
        drs.setstatus(status);
        drs.setresponsexml(responsexml);
        drs.setfailreason(failreason);
        drs.setresponse_datetime(response_datetime);
        drs.setslotno(slotno);
        drs.setlatitude(latitude);
        drs.setlongitude(longitude);
        drs.setordernumber(ordernumber);
        drs.setchoiceofpayment(choiceofpayment);
        drs.setdate(date);
        drs.setolddocketno(olddocketno);
        drs.setsellername(sellername);
        drs.setcontact_person(contact_person);
        drs.setattempt(attempt);
        drs.setmobile_pull_status(mobile_pull_status);
        drs.setposition(position);
        drs.setproduct_description(product_description);
        drs.setreason_for_return(reason_for_return);
        drs.setreturn_request_id(_return_request_id);
        drs.settp_code(tp_code);
        drs.setreturn_pincode(return_pincode);

        return drs;
    }

    private ContentValues getContentValues(DrsEntity drs) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(DatabaseHelper.SR, drs.getsr());
        contentValues.put(DatabaseHelper.JOBTYPE, drs.getjobtype());
        contentValues.put(DatabaseHelper.DOCKETNO, drs.getdocketno());
        contentValues.put(DatabaseHelper.EXCHANGE_REQUESTID, drs.getexchange_requestid());
        contentValues.put(DatabaseHelper.DRSNO, drs.getdrsno());
        contentValues.put(DatabaseHelper.DRS_DOCKET, drs.getdrs_docket());
        contentValues.put(DatabaseHelper.PIECES, drs.getpieces());
        contentValues.put(DatabaseHelper.CSGENM, drs.getcsgenm());
        contentValues.put(DatabaseHelper.CSGEADDR, drs.getcsgeaddr());
        contentValues.put(DatabaseHelper.ADDRESS_TYPE, drs.getaddress_type());
        contentValues.put(DatabaseHelper.PICKUP_LOCATION, drs.getpickup_location());
        contentValues.put(DatabaseHelper.TIMETOEND, drs.gettimetoend());
        contentValues.put(DatabaseHelper.CSGETELENO, drs.getcsgeteleno());
        contentValues.put(DatabaseHelper.ALTERNATE_NUMBER, drs.getalternate_number());
        contentValues.put(DatabaseHelper.CTR_NO, drs.getctr_no());
        contentValues.put(DatabaseHelper.CSGECITY, drs.getcsgecity());
        contentValues.put(DatabaseHelper.REASSIGN_DESTCD, drs.getreassign_destcd());
        contentValues.put(DatabaseHelper.CSGEPINCODE, drs.getcsgepincode());
        contentValues.put(DatabaseHelper.PKGSNO, drs.getpkgsno());
        contentValues.put(DatabaseHelper.COD_DOD, drs.getcod_dod());
        contentValues.put(DatabaseHelper.COD_AMT, drs.getcod_amt());
        contentValues.put(DatabaseHelper.DELIVERED, drs.getdelivered());
        contentValues.put(DatabaseHelper.DRSUPDATED, drs.getdrsupdated());
        contentValues.put(DatabaseHelper.LOGISTIC_DT, drs.getlogistic_dt());
        contentValues.put(DatabaseHelper.LOGISTIC_TIME, drs.getlogistic_time());
        contentValues.put(DatabaseHelper.START_KM, drs.getstart_km());
        contentValues.put(DatabaseHelper.TOTAL_DOCKETS_IN_DRS, drs.gettotal_dockets_in_drs());
        contentValues.put(DatabaseHelper.DKT_COUNT_NOT_UPDATED, drs.getdkt_count_not_updated());
        contentValues.put(DatabaseHelper.DRIVER_NAME, drs.getdriver_name());
        contentValues.put(DatabaseHelper.DRIVER_ID, drs.getdriver_id());
        contentValues.put(DatabaseHelper.VEHICLE_NUMBER, drs.getvehicle_number());
        contentValues.put(DatabaseHelper.ACTUWT, drs.getactuwt());
        contentValues.put(DatabaseHelper.USERID, drs.getuserid());
        contentValues.put(DatabaseHelper.CLIENT_CODE, drs.getclient_code());
        contentValues.put(DatabaseHelper.CLIENT_NAME, drs.getclient_name());
        contentValues.put(DatabaseHelper.NEXTATTEMPTDATE, drs.getnextattemptdate());
        contentValues.put(DatabaseHelper.PRODCD, drs.getprodcd());
        contentValues.put(DatabaseHelper.CODEDESC, drs.getcodedesc());
        contentValues.put(DatabaseHelper.AMOUNT_TO_CUTOMER, drs.getamount_to_cutomer());
        contentValues.put(DatabaseHelper.ENTRYDATE, drs.getentrydate());
        contentValues.put(DatabaseHelper.ENTRYBY, drs.getentryby());
        contentValues.put(DatabaseHelper.LASTEDITDATE, drs.getlasteditdate());
        contentValues.put(DatabaseHelper.LASTEDITBY, drs.getlasteditby());
        contentValues.put(DatabaseHelper.STATUS, drs.getstatus());
        contentValues.put(DatabaseHelper.RESPONSEXML, drs.getresponsexml());
        contentValues.put(DatabaseHelper.FAILREASON, drs.getfailreason());
        contentValues.put(DatabaseHelper.RESPONSE_DATETIME, drs.getresponse_datetime());
        contentValues.put(DatabaseHelper.SLOTNO, drs.getslotno());
        contentValues.put(DatabaseHelper.LATITUDE, drs.getlatitude());
        contentValues.put(DatabaseHelper.LONGITUDE, drs.getlongitude());
        contentValues.put(DatabaseHelper.ORDERNUMBER, drs.getordernumber());
        contentValues.put(DatabaseHelper.CHOICEOFPAYMENT, drs.getchoiceofpayment());
        contentValues.put(DatabaseHelper.DATE, drs.getdate());
        contentValues.put(DatabaseHelper.OLDDOCKETNO, drs.getolddocketno());
        contentValues.put(DatabaseHelper.SELLERNAME, drs.getsellername());
        contentValues.put(DatabaseHelper.CONTACT_PERSON, drs.getcontact_person());
        contentValues.put(DatabaseHelper.ATTEMPT, drs.getattempt());
        contentValues.put(DatabaseHelper.MOBILE_PULL_STATUS, drs.getmobile_pull_status());
        contentValues.put(DatabaseHelper.PRODUCT_DESCRIPTION, drs.getproduct_description());
        contentValues.put(DatabaseHelper.REASON_FOR_RETURN, drs.getreason_for_return());
        contentValues.put(DatabaseHelper.RETURN_REQUEST_ID, drs.getreturn_request_id());
        contentValues.put(DatabaseHelper.TP_CODE, drs.gettp_code());
        contentValues.put(DatabaseHelper.RETURN_PINCODE, drs.getreturn_pincode());

        return contentValues;
    }

    /**
     * Update drs position
     * @param drs_docket
     * @param position
     */
    public void updateDrsPosition(String drs_docket, int position) {
        SQLiteDatabase db = DatabaseHelper.getInstance().getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.POSITION, position);
        db.update(DatabaseHelper.DRS_TABLE_NAME, contentValues, DatabaseHelper.DRS_DOCKET + " = ? ", new String[] {drs_docket});
    }

    /**
     * Get number od rows in drs table
     * @return
     */
    public int getDrsCount() {
        SQLiteDatabase db = DatabaseHelper.getInstance().getReadableDatabase();
        Cursor cursor =  db.rawQuery( "select * from  " + DatabaseHelper.DRS_TABLE_NAME + " order by " + DatabaseHelper.POSITION + " asc", null);
        return cursor.getCount();
    }

    /**
     * Get success drs count according to job type
     * @return
     */
    public int getSuccessDrsCountJobType(String jobtype) {
        SQLiteDatabase db = DatabaseHelper.getInstance().getReadableDatabase();
        String query = "SELECT d.* FROM " + DatabaseHelper.DRS_TABLE_NAME + " d INNER JOIN " + DatabaseHelper.DELIVERY_TABLE_NAME + " ON (d." + DatabaseHelper.DRS_DOCKET + " = delivery." + DatabaseHelper.DRS_DOCKET +" AND delivery."+DatabaseHelper.JOBTYPE+" = '"+jobtype+ "' AND delivery." + DatabaseHelper.STATUS + " = '1' )";
        Cursor cursor =  db.rawQuery( query, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    /**
     * Get failed drs count according to job type
     * @return
     */
    public int getFailedDrsCountJobType(String jobtype) {
        SQLiteDatabase db = DatabaseHelper.getInstance().getReadableDatabase();
        String query = "SELECT d.* FROM " + DatabaseHelper.DRS_TABLE_NAME + " d INNER JOIN " + DatabaseHelper.DELIVERY_TABLE_NAME + " ON (d." + DatabaseHelper.DRS_DOCKET + " = delivery." + DatabaseHelper.DRS_DOCKET +" AND delivery."+DatabaseHelper.JOBTYPE+" = '"+jobtype+ "' AND delivery." + DatabaseHelper.STATUS + " = '0')";
        Cursor cursor =  db.rawQuery( query, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    /**
     * Get pending drs count according o job type
     * @return
     */
    public int getPendingDrsCountJobType(String jobtype){
        SQLiteDatabase db = DatabaseHelper.getInstance().getReadableDatabase();
        Cursor cursor =  db.rawQuery("select * from  " + DatabaseHelper.DRS_TABLE_NAME + " drs where drs." + DatabaseHelper.DRS_DOCKET +" not in (select " + DatabaseHelper.DRS_DOCKET + " from " + DatabaseHelper.DELIVERY_TABLE_NAME + ")AND drs."+DatabaseHelper.JOBTYPE+" = '"+jobtype+ "' ", null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    /**
     * Check whether docket is synced or not
     * @param drs_docket
     * @return
     */
    public boolean isDocketSynced(String drs_docket) {
        SQLiteDatabase db = DatabaseHelper.getInstance().getReadableDatabase();
        boolean isSynced = false;
        Cursor cursor =  db.rawQuery( "select " + DatabaseHelper.STATUS + " from  " + DatabaseHelper.DRS_TABLE_NAME + " where " + DatabaseHelper.DRS_DOCKET + " = ? ", new String[] {drs_docket});
        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            String status = cursor.getString(cursor.getColumnIndex(DatabaseHelper.STATUS));
            if(status.equalsIgnoreCase("Y") || status.equalsIgnoreCase("N")) {
                isSynced = true;
            } else {
                isSynced = false;
            }
        }
        cursor.close();
        return isSynced;
    }
}
