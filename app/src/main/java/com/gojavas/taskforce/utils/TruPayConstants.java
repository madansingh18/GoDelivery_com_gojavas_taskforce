package com.gojavas.taskforce.utils;

/**
 * Created by gjs447 on 4/5/2016.
 */
public class TruPayConstants {

    public static final String TRUPAY_BASE_URL = "http://trupaybeanstalk-env.ap-southeast-1.elasticbeanstalk.com/TruPay/"; // Test
//    public static final String TRUPAY_BASE_URL = "https://uat.trupay.in/TruPay/"; // Test
//    public static final String TRUPAY_BASE_URL = "https://app.trupay.in/TruPay/"; // Production

    public static final String GENERATE_TOKEN_URL = TRUPAY_BASE_URL + "oauth/token";
    public static final String REQUEST_MONEY_URL = TRUPAY_BASE_URL + "v1/merchant/requestPayment";
    public static final String TRANSACTION_STATUS_URL = TRUPAY_BASE_URL + "v1/merchant/checkRequestPaymentStatus";
    public static final String CANCEL_TRANSACTION_STATUS_URL = TRUPAY_BASE_URL + "v1/merchant/cancelRequest";

    // parameters for trupay constant
    public static final String MERCHANT_ID = "gt_merchantid";
    public static final String TRUPAY_CLIENT_ID = "gt_client_id";
    public static final String TRUPAY_CLIENT_SECRET = "gt_client_secret";
    public static final String TRUPAY_GRANTTYPE = "gt_grant_type";
    public static final String CUSTOMER_MOBILE_NO = "09599385154";
    public static final String TRUPAY_USERNAME = "gt_username";
    public static final String TRUPAY_PASSWORD = "gt_password";

}
