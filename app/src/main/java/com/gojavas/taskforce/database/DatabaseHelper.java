package com.gojavas.taskforce.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.gojavas.taskforce.ui.activity.TaskForceApplication;

/**
 * Created by GJS280 on 14/4/2015.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public static DatabaseHelper instance = null;

    public static final String DATABASE_NAME = "TaskForce.db";
    public static final String USER_TABLE_NAME = "user";
    public static final String DRS_TABLE_NAME = "drs";
    public static final String DELIVERY_TABLE_NAME = "delivery";
    public static final String ITEM_TABLE_NAME = "item";
    public static final String SLAB_TABLE_NAME = "slab";
    public static final String SWIPE_TABLE_NAME = "swipe";
    public static final String TRACK_TABLE_NAME = "track";
    public static final String CALL_LOG_TABLE_NAME = "call_log";
    public static final String PAYMENT_TABLE_NAME = "payment";
    public static final String PAYMENT_STATUS_TABLE_NAME = "payment_status";
    public static final String IMAGE_UPLOAD_TABLE_NAME = "image_upload";

    // Common table fields
    public static final String ID = "id";
    public static final String SR = "sr";
    public static final String CITY = "city";
    public static final String STATUS = "status";
    public static final String SYNC = "sync";

    // Common Drs and Delivery fields
    public static final String JOBTYPE = "jobtype";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String ORDERNUMBER = "ordernumber";
    public static final String DATETIME = "datetime";
    public static final String DRS_DOCKET = "drs_docket";

    // User table fields
    public static final String EMP_CODE = "emp_code";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String FIRSTNAME = "firstname";
    public static final String BRANCH = "branch";
    public static final String IMEI_NO = "imei_no";
    public static final String MOBILE_NO = "mobile_no";

    // DRS table fields
    public static final String DOCKETNO = "docketno";
    public static final String EXCHANGE_REQUESTID = "exchange_requestid";
    public static final String DRSNO = "drsno";
    public static final String PIECES = "pieces";
    public static final String CSGENM = "csgenm";
    public static final String CSGEADDR = "csgeaddr";
    public static final String ADDRESS_TYPE = "address_type";
    public static final String PICKUP_LOCATION = "pickup_location";
    public static final String TIMETOEND = "timetoend";
    public static final String CSGETELENO = "csgeteleno";
    public static final String ALTERNATE_NUMBER = "alternate_number";
    public static final String CTR_NO = "ctr_no";
    public static final String CSGECITY = "csgecity";
    public static final String REASSIGN_DESTCD = "reassign_destcd";
    public static final String CSGEPINCODE = "csgepincode";
    public static final String PKGSNO = "pkgsno";
    public static final String COD_DOD = "cod_dod";
    public static final String COD_AMT = "cod_amt";
    public static final String DELIVERED = "delivered";
    public static final String DRSUPDATED = "drsupdated";
    public static final String LOGISTIC_DT = "logistic_dt";
    public static final String LOGISTIC_TIME = "logistic_time";
    public static final String START_KM = "start_km";
    public static final String TOTAL_DOCKETS_IN_DRS = "total_dockets_in_drs";
    public static final String DKT_COUNT_NOT_UPDATED = "dkt_count_not_updated";
    public static final String DRIVER_NAME = "driver_name";
    public static final String DRIVER_ID = "driver_id";
    public static final String VEHICLE_NUMBER = "vehicle_number";
    public static final String ACTUWT = "actuwt";
    public static final String USERID = "userid";
    public static final String CLIENT_CODE = "client_code";
    public static final String CLIENT_NAME = "client_name";
    public static final String NEXTATTEMPTDATE = "nextattemptdate";
    public static final String PRODCD = "prodcd";
    public static final String CODEDESC = "codedesc";
    public static final String AMOUNT_TO_CUTOMER = "amount_to_cutomer";
    public static final String ENTRYDATE = "entrydate";
    public static final String ENTRYBY = "entryby";
    public static final String LASTEDITDATE = "lasteditdate";
    public static final String LASTEDITBY = "lasteditby";
    public static final String RESPONSEXML = "responsexml";
    public static final String FAILREASON = "failreason";
    public static final String RESPONSE_DATETIME = "response_datetime";
    public static final String SLOTNO = "slotno";
    public static final String CHOICEOFPAYMENT = "choiceofpayment";
    public static final String DATE = "date";
    public static final String OLDDOCKETNO = "olddocketno";
    public static final String SELLERNAME = "sellername";
    public static final String CONTACT_PERSON = "contact_person";
    public static final String ATTEMPT = "attempt";
    public static final String MOBILE_PULL_STATUS = "mobile_pull_status";
    public static final String POSITION = "position";

    // 90 Minutes drs extra fields
    public static final String PRODUCT_DESCRIPTION = "product_description";
    public static final String REASON_FOR_RETURN = "reason_for_return";
    public static final String RETURN_REQUEST_ID = "return_request_id";
    public static final String TP_CODE = "tp_code";
    public static final String RETURN_PINCODE = "return_pincode";

    // Delivery table fields
    public static final String CUST_NAME = "cust_name";
    public static final String CUST_ADDRESS1 = "cust_address1";
    public static final String CUST_ADDRESS2 = "cust_address2";
    public static final String PINCODE = "pincode";
    public static final String STATE = "state";
    public static final String LANDMARK = "landmark";
    public static final String CUST_CONTACT1 = "cust_contact1";
    public static final String ALTERNATE_NO = "alternate_no";
    public static final String PACKAGESNO = "packagesNo";
    public static final String DRSNUMBER = "DRSNumber";
    public static final String TOTALDOCKETSINDRS = "TotalDocketsInDRS";
    public static final String PRODCUTDESC = "ProdcutDesc";
    public static final String DOCKET_NUMBER = "docket_number";
    public static final String CHOICE_OF_PAYMENT = "choice_of_payment";
    public static final String AMOUNT_TOBE_PAID = "amount_tobe_paid";
    public static final String DUE_DATE = "due_date";
    public static final String BOY_ID = "boy_id";
    public static final String BOY_NAME = "boy_name";
    public static final String REASON_ID = "reason_id";
    public static final String FAILED_REASON = "failed_reason";
    public static final String DELIVERY_TIME = "delivery_time";
    public static final String COMMENTS = "comments";
    public static final String MODE_OF_PAYMENT = "mode_of_payment";
    public static final String TRANSACTION_NO = "transaction_no";
    public static final String AUTH_CODE = "auth_code";
    public static final String CARD_TYPE = "card_type";
    public static final String BANK_NAME = "bank_name";
    public static final String CUSTOMER_SIGN = "customer_sign";
    public static final String CUSTOMER_IMG = "customer_img";
    public static final String CUSTOMER_IMG2 = "customer_img2";
    public static final String CUSTOMER_IMG3 = "customer_img3";
    public static final String HAPPY_DELIVERY = "happy_delivery";
    public static final String HAPPY_DELIVERY_IMG = "happy_delivery_img";
    public static final String SKU_DETAILS = "sku_details";
    public static final String LAST_UPDATED = "last_updated";
    public static final String PREFERRED_TRANSACTION_TIME = "preferred_transaction_time";
    public static final String ORIGINAL_AMOUNT_PAID = "original_amount_paid";
    public static final String USER_ID = "user_id";
    public static final String DELIVERED_TO = "delivered_to";
    public static final String DELIVERED_TO_RELATION = "delivered_to_relation";
    public static final String PRIRORITY_DELIVERY = "prirority_delivery";
    public static final String CLOSING_KM = "closing_km";
    public static final String IMEI_NUMBER = "imei_number";
    public static final String SYNC_UPDATE = "sync_update";
    public static final String UPDATED_ON_ANDROID = "updated_on_android";
    public static final String UPDATED_ON_WEBX = "updated_on_webx";
    public static final String LOC_ID = "loc_id";
    public static final String C_ID = "c_id";
    public static final String ADMIN_ID = "admin_id";
    public static final String EXTRA1 = "extra1";
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
    public static final String NPS_SCORE = "nps_score";
    public static final String SEQ_PREDICTED = "seq_predicted";
    public static final String SEQ_SELECTED = "seq_selected";
    public static final String SEQ_TRANSACTION = "seq_transaction";
    public static final String USER_COMMENTS = "user_comments";
    public static final String BATTERY_LEVEL = "battery_level";
    public static final String TIME_ERP_IMPORT_INITIATED = "time_erp_import_initiated";
    public static final String TIME_ERP_IMPORT_COMPLETED = "time_erp_import_completed";
    public static final String TIME_AVAILABLE_ANDROID_INITIATED = "time_available_android_initiated";
    public static final String TIME_AVAILABLE_ANDROID_COMPLETED = "time_available_android_completed";
    public static final String REVERSE_DOCKET_NUMBER = "reverse_docket_number";
    public static final String RECEIPT_URL = "receipt_url";

    // Common fields Item, slab and swipe
    public static final String DRS_NO = "drs_no";
    public static final String DOCKET_NO = "docket_no";

    // Item table fields
    public static final String SKU_ID = "sku_id";
    public static final String SKU_DESCRIPTION = "sku_description";
    public static final String SKU_COST = "sku_cost";
    public static final String QUANTITY = "quantity";

    // Slab table fields
    public static final String SLABFROM_TO = "slabfrom_to";
    public static final String SLAB_RATE = "slab_rate";

    // Swipe table fields
    public static final String DEVICE_TYPE = "device_type";
    public static final String REASON = "reason";

    // Call Log table fields
    public static final String NAME = "name";
    public static final String NUMBER = "number";
    public static final String DURATION = "duration";
    public static final String TYPE = "type";
    public static final String CALL_SMS = "call_sms";

    // Track table fields
    public static final String NETWORK_TYPE = "network_type";

    // Payment table fields
    public static final String PAYMENT_STATUS = "payment_status";

    // Image upload table fields
    public static final String IMAGE_NAME = "image_name";
    public static final String UPLOAD_STATUS = "upload_status";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 3);
    }

    public static DatabaseHelper getInstance() {
        if(instance == null) {
            instance = new DatabaseHelper(TaskForceApplication.getInstance());
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL( "create table " + USER_TABLE_NAME + " (" +
                        ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        SR + " TEXT, " +
                        EMP_CODE + " TEXT, " +
                        USERNAME + " TEXT, " +
                        PASSWORD + " TEXT, " +
                        FIRSTNAME + " TEXT, " +
                        CITY + " TEXT, " +
                        BRANCH + " TEXT, " +
                        IMEI_NO + " TEXT, " +
                        MOBILE_NO + " TEXT, " +
                        DATETIME + " TEXT" +
                        ")"
        );

        sqLiteDatabase.execSQL( "create table " + DRS_TABLE_NAME + " (" +
                        ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        SR + " TEXT, " +
                        JOBTYPE + " TEXT, " +
                        DOCKETNO + " TEXT, " +
                        EXCHANGE_REQUESTID + " TEXT, " +
                        DRSNO + " TEXT, " +
                        DRS_DOCKET + " TEXT, " +
                        PIECES + " TEXT, " +
                        CSGENM + " TEXT, " +
                        CSGEADDR + " TEXT, " +
                        ADDRESS_TYPE + " TEXT, " +
                        PICKUP_LOCATION + " TEXT, " +
                        TIMETOEND + " TEXT, " +
                        CSGETELENO + " TEXT, " +
                        ALTERNATE_NUMBER + " TEXT, " +
                        CTR_NO + " TEXT, " +
                        CSGECITY + " TEXT, " +
                        REASSIGN_DESTCD + " TEXT, " +
                        CSGEPINCODE + " TEXT, " +
                        PKGSNO + " TEXT, " +
                        COD_DOD + " TEXT, " +
                        COD_AMT + " TEXT, " +
                        DELIVERED + " TEXT, " +
                        DRSUPDATED + " TEXT, " +
                        LOGISTIC_DT + " TEXT, " +
                        LOGISTIC_TIME + " TEXT, " +
                        START_KM + " TEXT, " +
                        TOTAL_DOCKETS_IN_DRS + " TEXT, " +
                        DKT_COUNT_NOT_UPDATED + " TEXT, " +
                        DRIVER_NAME + " TEXT, " +
                        DRIVER_ID + " TEXT, " +
                        VEHICLE_NUMBER + " TEXT, " +
                        ACTUWT + " TEXT, " +
                        USERID + " TEXT, " +
                        CLIENT_CODE + " TEXT, " +
                        CLIENT_NAME + " TEXT, " +
                        NEXTATTEMPTDATE + " TEXT, " +
                        PRODCD + " TEXT, " +
                        CODEDESC + " TEXT, " +
                        AMOUNT_TO_CUTOMER + " TEXT, " +
                        ENTRYDATE + " TEXT, " +
                        ENTRYBY + " TEXT, " +
                        LASTEDITDATE + " TEXT, " +
                        LASTEDITBY + " TEXT, " +
                        STATUS + " TEXT, " +
                        RESPONSEXML + " TEXT, " +
                        FAILREASON + " TEXT, " +
                        RESPONSE_DATETIME + " TEXT, " +
                        SLOTNO + " TEXT, " +
                        LATITUDE + " TEXT, " +
                        LONGITUDE + " TEXT, " +
                        ORDERNUMBER + " TEXT, " +
                        CHOICEOFPAYMENT + " TEXT, " +
                        DATE + " TEXT, " +
                        OLDDOCKETNO + " TEXT, " +
                        SELLERNAME + " TEXT, " +
                        CONTACT_PERSON + " TEXT, " +
                        ATTEMPT + " TEXT, " +
                        MOBILE_PULL_STATUS + " TEXT, " +
                        POSITION + " INTEGER DEFAULT 0, " +
                        PRODUCT_DESCRIPTION + " TEXT DEFAULT '', " +
                        REASON_FOR_RETURN + " TEXT DEFAULT '', " +
                        RETURN_REQUEST_ID + " TEXT DEFAULT '', " +
                        TP_CODE + " TEXT DEFAULT '', " +
                        RETURN_PINCODE + " TEXT DEFAULT ''" +
                        ")"
        );

        sqLiteDatabase.execSQL( "create table " + DELIVERY_TABLE_NAME + " (" +
                        ID + " TEXT, " +
                        JOBTYPE + " TEXT, " +
                        CUST_NAME + " TEXT, " +
                        CUST_ADDRESS1 + " TEXT, " +
                        CUST_ADDRESS2 + " TEXT, " +
                        PINCODE + " TEXT, " +
                        CITY + " TEXT, " +
                        STATE + " TEXT, " +
                        LANDMARK + " TEXT, " +
                        CUST_CONTACT1 + " TEXT, " +
                        ALTERNATE_NO + " TEXT, " +
                        ORDERNUMBER + " TEXT, " +
                        PACKAGESNO + " TEXT, " +
                        DRSNUMBER + " TEXT, " +
                        TOTALDOCKETSINDRS + " TEXT, " +
                        PRODCUTDESC + " TEXT, " +
                        DOCKET_NUMBER + " TEXT, " +
                        CHOICE_OF_PAYMENT + " TEXT, " +
                        AMOUNT_TOBE_PAID + " TEXT, " +
                        DUE_DATE + " TEXT, " +
                        BOY_ID + " TEXT, " +
                        BOY_NAME + " TEXT, " +
                        STATUS + " TEXT, " +
                        REASON_ID + " TEXT, " +
                        FAILED_REASON + " TEXT, " +
                        DELIVERY_TIME + " TEXT, " +
                        COMMENTS + " TEXT, " +
                        MODE_OF_PAYMENT + " TEXT, " +
                        TRANSACTION_NO + " TEXT, " +
                        AUTH_CODE + " TEXT, " +
                        CARD_TYPE + " TEXT, " +
                        BANK_NAME + " TEXT, " +
                        CUSTOMER_SIGN + " TEXT, " +
                        CUSTOMER_IMG + " TEXT, " +
                        CUSTOMER_IMG2 + " TEXT, " +
                        CUSTOMER_IMG3 + " TEXT, " +
                        HAPPY_DELIVERY + " TEXT, " +
                        HAPPY_DELIVERY_IMG + " TEXT, " +
                        LATITUDE + " TEXT, " +
                        LONGITUDE + " TEXT, " +
                        SKU_DETAILS + " TEXT, " +
                        LAST_UPDATED + " TEXT, " +
                        PREFERRED_TRANSACTION_TIME + " TEXT, " +
                        ORIGINAL_AMOUNT_PAID + " TEXT, " +
                        USER_ID + " TEXT, " +
                        DELIVERED_TO + " TEXT, " +
                        DELIVERED_TO_RELATION + " TEXT, " +
                        PRIRORITY_DELIVERY + " TEXT, " +
                        CLOSING_KM + " TEXT, " +
                        IMEI_NUMBER + " TEXT, " +
                        SYNC_UPDATE + " TEXT, " +
                        UPDATED_ON_ANDROID + " TEXT, " +
                        UPDATED_ON_WEBX + " TEXT, " +
                        LOC_ID + " TEXT, " +
                        C_ID + " TEXT, " +
                        ADMIN_ID + " TEXT, " +
                        EXTRA1 + " TEXT, " +
                        EXTRA2 + " TEXT, " +
                        EXTRA3 + " TEXT, " +
                        EXTRA4 + " TEXT, " +
                        EXTRA5 + " TEXT, " +
                        EXTRA6 + " TEXT, " +
                        SYNC_FLAG + " TEXT, " +
                        LAST_LAT + " TEXT, " +
                        LAST_LNG + " TEXT, " +
                        LAST_ATTEMPT_STATUS + " TEXT, " +
                        ATTEMPT_NUMBER + " TEXT, " +
                        ATTEMPT_SLOT + " TEXT, " +
                        NPS_SCORE + " TEXT, " +
                        SEQ_PREDICTED + " TEXT, " +
                        SEQ_SELECTED + " TEXT, " +
                        SEQ_TRANSACTION + " TEXT, " +
                        USER_COMMENTS + " TEXT, " +
                        BATTERY_LEVEL + " TEXT, " +
                        TIME_ERP_IMPORT_INITIATED + " TEXT, " +
                        TIME_ERP_IMPORT_COMPLETED + " TEXT, " +
                        TIME_AVAILABLE_ANDROID_INITIATED + " TEXT, " +
                        TIME_AVAILABLE_ANDROID_COMPLETED + " TEXT, " +
                        REVERSE_DOCKET_NUMBER + " TEXT, " +
                        DRS_DOCKET + " TEXT, " +
                        SYNC + " TEXT, " +
                        PRODUCT_DESCRIPTION + " TEXT DEFAULT '', " +
                        REASON_FOR_RETURN + " TEXT DEFAULT '', " +
                        RETURN_REQUEST_ID + " TEXT DEFAULT '', " +
                        TP_CODE + " TEXT DEFAULT '', " +
                        RETURN_PINCODE + " TEXT DEFAULT '', " +
                        RECEIPT_URL + " TEXT DEFAULT ''" +
                        ")"
        );

        sqLiteDatabase.execSQL( "create table " + ITEM_TABLE_NAME + " (" +
                        ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        SR + " TEXT, " +
                        DRS_NO + " TEXT, " +
                        DOCKET_NO + " TEXT, " +
                        DRS_DOCKET + " TEXT, " +
                        SKU_ID + " TEXT, " +
                        SKU_DESCRIPTION + " TEXT, " +
                        SKU_COST + " TEXT, " +
                        STATUS + " TEXT, " +
                        QUANTITY + " TEXT" +
                        ")"
        );

        sqLiteDatabase.execSQL( "create table " + SLAB_TABLE_NAME + " (" +
                        ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        SR + " TEXT, " +
                        DRS_NO + " TEXT, " +
                        DOCKET_NO + " TEXT, " +
                        SLABFROM_TO + " TEXT, " +
                        SLAB_RATE + " TEXT" +
                        ")"
        );

        sqLiteDatabase.execSQL( "create table " + SWIPE_TABLE_NAME + " (" +
                        ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        SR + " TEXT, " +
                        DOCKET_NO + " TEXT, " +
                        DRS_NO + " TEXT, " +
                        DEVICE_TYPE + " TEXT, " +
                        STATUS + " TEXT, " +
                        REASON + " TEXT, " +
                        DATETIME + " TEXT, " +
                        SYNC + " TEXT" +
                        ")"
        );

        sqLiteDatabase.execSQL( "create table " + TRACK_TABLE_NAME + " (" +
                        ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        SR + " TEXT, " +
                        USERNAME + " TEXT, " +
                        LATITUDE + " TEXT, " +
                        LONGITUDE + " TEXT, " +
                        DATETIME + " TEXT, " +
                        NETWORK_TYPE + " TEXT, " +
                        SYNC + " TEXT" +
                        ")"
        );

        sqLiteDatabase.execSQL( "create table " + CALL_LOG_TABLE_NAME + " (" +
                        ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        USERNAME + " TEXT, " +
                        NAME + " TEXT, " +
                        NUMBER + " TEXT, " +
                        DURATION + " TEXT, " +
                        DATE + " TEXT, " +
                        TYPE + " TEXT, " +
                        CALL_SMS + " TEXT, " +
                        DOCKET_NO + " TEXT, " +
                        DRSNO + " TEXT, " +
                        SYNC + " TEXT" +
                        ")"
        );

        sqLiteDatabase.execSQL( "create table " + PAYMENT_TABLE_NAME + " (" +
                        ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        DRSNUMBER + " TEXT, " +
                        DOCKET_NUMBER + " TEXT, " +
                        DRS_DOCKET + " TEXT, " +
                        ORIGINAL_AMOUNT_PAID + " TEXT, " +
                        TRANSACTION_NO + " TEXT, " +
                        AUTH_CODE + " TEXT, " +
                        CARD_TYPE + " TEXT, " +
                        BANK_NAME + " TEXT, " +
                        MODE_OF_PAYMENT + " TEXT" +
                        ")"
        );

        sqLiteDatabase.execSQL( "create table " + PAYMENT_STATUS_TABLE_NAME + " (" +
                        ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        DRSNUMBER + " TEXT, " +
                        DOCKET_NUMBER + " TEXT, " +
                        DRS_DOCKET + " TEXT, " +
                        DEVICE_TYPE + " TEXT, " +
                        PAYMENT_STATUS + " TEXT" +
                        ")"
        );

        sqLiteDatabase.execSQL( "create table " + IMAGE_UPLOAD_TABLE_NAME + " (" +
                        ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        IMAGE_NAME + " TEXT, " +
                        UPLOAD_STATUS + " TEXT" +
                        ")"
        );
    }

    // Alter table commands for 90 minutes
    // Drs table changes
    private static final String DATABASE_ALTER_V2_DRS_1 = "ALTER TABLE " + DRS_TABLE_NAME + " ADD COLUMN " + PRODUCT_DESCRIPTION + " TEXT DEFAULT ''";
    private static final String DATABASE_ALTER_V2_DRS_2 = "ALTER TABLE " + DRS_TABLE_NAME + " ADD COLUMN " + REASON_FOR_RETURN + " TEXT DEFAULT ''";
    private static final String DATABASE_ALTER_V2_DRS_3 = "ALTER TABLE " + DRS_TABLE_NAME + " ADD COLUMN " + RETURN_REQUEST_ID + " TEXT DEFAULT ''";
    private static final String DATABASE_ALTER_V2_DRS_4 = "ALTER TABLE " + DRS_TABLE_NAME + " ADD COLUMN " + TP_CODE + " TEXT DEFAULT ''";
    private static final String DATABASE_ALTER_V2_DRS_5 = "ALTER TABLE " + DRS_TABLE_NAME + " ADD COLUMN " + RETURN_PINCODE + " TEXT DEFAULT ''";
    private static final String DATABASE_ALTER_V2_DRS_6 = "ALTER TABLE " + DRS_TABLE_NAME + " ADD COLUMN " + ADDRESS_TYPE + " TEXT DEFAULT ''";
    // Delivery table changes
    private static final String DATABASE_ALTER_V2_DELIVERY_1 = "ALTER TABLE " + DELIVERY_TABLE_NAME + " ADD COLUMN " + PRODUCT_DESCRIPTION + " TEXT DEFAULT ''";
    private static final String DATABASE_ALTER_V2_DELIVERY_2 = "ALTER TABLE " + DELIVERY_TABLE_NAME + " ADD COLUMN " + REASON_FOR_RETURN + " TEXT DEFAULT ''";
    private static final String DATABASE_ALTER_V2_DELIVERY_3 = "ALTER TABLE " + DELIVERY_TABLE_NAME + " ADD COLUMN " + RETURN_REQUEST_ID + " TEXT DEFAULT ''";
    private static final String DATABASE_ALTER_V2_DELIVERY_4 = "ALTER TABLE " + DELIVERY_TABLE_NAME + " ADD COLUMN " + TP_CODE + " TEXT DEFAULT ''";
    private static final String DATABASE_ALTER_V2_DELIVERY_5 = "ALTER TABLE " + DELIVERY_TABLE_NAME + " ADD COLUMN " + RETURN_PINCODE + " TEXT DEFAULT ''";
    private static final String DATABASE_ALTER_V2_DELIVERY_6 = "ALTER TABLE " + DELIVERY_TABLE_NAME + " ADD COLUMN " + RECEIPT_URL + " TEXT DEFAULT ''";

    // Alter table commands for exchange jobtype
    // Drs table changes
    private static final String DATABASE_ALTER_V3_DRS_1 = "ALTER TABLE " + DRS_TABLE_NAME + " ADD COLUMN " + EXCHANGE_REQUESTID + " TEXT DEFAULT ''";

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(newVersion > oldVersion) {
            if(oldVersion == 1) {
                // Alter table commands V2
                db.execSQL(DATABASE_ALTER_V2_DRS_1);
                db.execSQL(DATABASE_ALTER_V2_DRS_2);
                db.execSQL(DATABASE_ALTER_V2_DRS_3);
                db.execSQL(DATABASE_ALTER_V2_DRS_4);
                db.execSQL(DATABASE_ALTER_V2_DRS_5);
                db.execSQL(DATABASE_ALTER_V2_DRS_6);
                db.execSQL(DATABASE_ALTER_V2_DELIVERY_1);
                db.execSQL(DATABASE_ALTER_V2_DELIVERY_2);
                db.execSQL(DATABASE_ALTER_V2_DELIVERY_3);
                db.execSQL(DATABASE_ALTER_V2_DELIVERY_4);
                db.execSQL(DATABASE_ALTER_V2_DELIVERY_5);
                db.execSQL(DATABASE_ALTER_V2_DELIVERY_6);
                // Alter table commands V3
                db.execSQL(DATABASE_ALTER_V3_DRS_1);
            } else if(oldVersion == 2) {
                // Alter table commands V3
                db.execSQL(DATABASE_ALTER_V3_DRS_1);
            }
        }
    }
}
