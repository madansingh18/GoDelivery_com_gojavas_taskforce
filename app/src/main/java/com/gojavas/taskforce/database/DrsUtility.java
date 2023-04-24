package com.gojavas.taskforce.database;

/**
 * Created by GJS280 on 23/4/2015.
 */
public class DrsUtility {

    // Job type Mobile Pickup
    public static final String[] DRS_TABLE_FIELDS_MOBILE_PICKUP = {DrsHelper.CSGENM, DrsHelper.CSGEADDR, DrsHelper.ADDRESS_TYPE,
            DrsHelper.CSGEPINCODE, DrsHelper.COD_AMT, DrsHelper.OLDDOCKETNO, DrsHelper.ORDERNUMBER, DrsHelper.ACTUWT,
            DrsHelper.CLIENT_CODE, DrsHelper.CLIENT_NAME};

    // Job type Pickup
    public static final String[] DRS_TABLE_FIELDS_PICKUP = {DrsHelper.CSGENM, DrsHelper.CSGEADDR, DrsHelper.ADDRESS_TYPE, DrsHelper.DRSNO,
            DrsHelper.CSGEPINCODE, DrsHelper.OLDDOCKETNO, DrsHelper.ORDERNUMBER, DrsHelper.ACTUWT,
            DrsHelper.CLIENT_CODE, DrsHelper.CLIENT_NAME};

    // Job type 90 Minutes
    public static final String[] DRS_TABLE_FIELDS_90_MINUTES = {DrsHelper.CSGENM, DrsHelper.CSGEADDR, DrsHelper.ADDRESS_TYPE, DrsHelper.CSGEPINCODE,
            DrsHelper.PICKUP_LOCATION, DrsHelper.CLIENT_CODE, DrsHelper.CLIENT_NAME, DrsHelper.ORDERNUMBER};

    // Job type TNB
    public static final String[] DRS_TABLE_FIELDS_TNB = {DrsHelper.CSGENM, DrsHelper.CSGEADDR, DrsHelper.CSGEPINCODE,
            DrsHelper.ORDERNUMBER, DrsHelper.PKGSNO, DrsHelper.COD_DOD, DrsHelper.COD_AMT, DrsHelper.PIECES,
            DrsHelper.TOTAL_DOCKETS_IN_DRS, DrsHelper.CLIENT_CODE, DrsHelper.CLIENT_NAME};

    // Job type First Pickup
    public static final String[] DRS_TABLE_FIELDS_FIRST_PICKUP = {DrsHelper.CSGENM, DrsHelper.CSGEADDR, DrsHelper.ADDRESS_TYPE, DrsHelper.CSGEPINCODE,
            DrsHelper.ORDERNUMBER, DrsHelper.PKGSNO, DrsHelper.COD_DOD, DrsHelper.COD_AMT, DrsHelper.PIECES,
            DrsHelper.TOTAL_DOCKETS_IN_DRS, DrsHelper.CLIENT_CODE, DrsHelper.CLIENT_NAME};

    // Job type Delivery
    public static final String[] DRS_TABLE_FIELDS_DELIVERY = {DrsHelper.CSGENM, DrsHelper.CSGEADDR, DrsHelper.DRSNO, DrsHelper.CSGEPINCODE,
            DrsHelper.ORDERNUMBER, DrsHelper.PKGSNO, DrsHelper.COD_DOD, DrsHelper.COD_AMT, DrsHelper.PIECES,
            DrsHelper.TOTAL_DOCKETS_IN_DRS, DrsHelper.CLIENT_CODE, DrsHelper.CLIENT_NAME};

    // Job type Delivery1
    public static final String[] DRS_TABLE_FIELDS_DELIVERY1 = {DrsHelper.CSGENM, DrsHelper.CSGEADDR, DrsHelper.CSGEPINCODE,
            DrsHelper.ORDERNUMBER, DrsHelper.PKGSNO, DrsHelper.COD_DOD, DrsHelper.COD_AMT, DrsHelper.PIECES,
            DrsHelper.TOTAL_DOCKETS_IN_DRS, DrsHelper.CLIENT_CODE, DrsHelper.CLIENT_NAME};

    // Job type Delivery Success
    // Harshita Added one other field Reverse Docket Number
    public static final String[] DRS_TABLE_FIELDS_DELIVERY_SUCCESS = {DeliveryHelper.CUST_NAME, DeliveryHelper.PINCODE,
            DeliveryHelper.ORDERNUMBER, DeliveryHelper.PACKAGESNO, DeliveryHelper.CHOICE_OF_PAYMENT, DeliveryHelper.AMOUNT_TOBE_PAID,
            DeliveryHelper.TOTALDOCKETSINDRS, DeliveryHelper.ClIENT_NAME, DeliveryHelper.CLIENT_CODE, DeliveryHelper.ORIGINAL_AMOUNT_PAID,
            DeliveryHelper.MODE_OF_PAYMENT, DeliveryHelper.REVERSE_DOCKET_NUMBER, DeliveryHelper.DELIVERED_TO, DeliveryHelper.DELIVERED_TO_RELATION,
            DeliveryHelper.NPS_SCORE, DeliveryHelper.CUSTOMER_IMG, DeliveryHelper.CUSTOMER_IMG2, DeliveryHelper.CUSTOMER_IMG3, DeliveryHelper.CUSTOMER_SIGN,
            DeliveryHelper.HAPPY_DELIVERY_IMG,
    };

    // Job type Delivery, Pickup and TNB Fail
    public static final String[] DRS_TABLE_FIELDS_DELIVERY_PICKUP_FAIL = {DeliveryHelper.CUST_NAME, DeliveryHelper.CUST_ADDRESS1,
            DeliveryHelper.ORDERNUMBER, DeliveryHelper.DRSNUMBER, DeliveryHelper.PINCODE, DeliveryHelper.PACKAGESNO,
            DeliveryHelper.CHOICE_OF_PAYMENT, DeliveryHelper.AMOUNT_TOBE_PAID, DeliveryHelper.TOTALDOCKETSINDRS, DeliveryHelper.ClIENT_NAME,
            DeliveryHelper.CLIENT_CODE, DeliveryHelper.FAILED_REASON, DeliveryHelper.CUSTOMER_IMG,
            DeliveryHelper.CUSTOMER_IMG2, DeliveryHelper.CUSTOMER_IMG3
    };

    // Job type SUCCESS Pickup
    public static final String[] DRS_TABLE_FIELDS_PICKUP_SUCCESS = {DeliveryHelper.CUST_NAME, DeliveryHelper.CUST_ADDRESS1,
            DeliveryHelper.PINCODE, DeliveryHelper.ORDERNUMBER, DeliveryHelper.PACKAGESNO,
            DeliveryHelper.ACTUAL_WT, DeliveryHelper.ClIENT_NAME, DeliveryHelper.CLIENT_CODE, DeliveryHelper.CHOICE_OF_PAYMENT,
            DeliveryHelper.AMOUNT_TOBE_PAID, DeliveryHelper.CUSTOMER_IMG, DeliveryHelper.CUSTOMER_IMG2, DeliveryHelper.CUSTOMER_IMG3,
            DeliveryHelper.REVERSE_DOCKET_NUMBER, DeliveryHelper.EXTRA1
    };

    // Job type SUCCESS Pickup
    public static final String[] DRS_TABLE_FIELDS_90_MINUTES_SUCCESS = {DeliveryHelper.CUST_NAME, DeliveryHelper.CUST_ADDRESS1,
            DeliveryHelper.PINCODE, DeliveryHelper.ClIENT_NAME, DeliveryHelper.CLIENT_CODE,
             DeliveryHelper.CUSTOMER_IMG, DeliveryHelper.CUSTOMER_IMG2, DeliveryHelper.CUSTOMER_IMG3,
            DeliveryHelper.REVERSE_DOCKET_NUMBER, DeliveryHelper.EXTRA1
    };
}
