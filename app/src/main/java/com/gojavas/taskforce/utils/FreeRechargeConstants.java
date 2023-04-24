package com.gojavas.taskforce.utils;

/**
 * Created by gjs447 on 4/13/2016.
 */
public class FreeRechargeConstants {

    public static final String FR_BASE_URL = " https://stg-posservice.snapdeal.com/v0/pos/"; // Test

    public static final String FR_INITIATE_TRANSACTION_URL = FR_BASE_URL + "walletpay";
    public static final String FR_OTP_STATUS_URL = FR_BASE_URL + "walletpay";
    public static final String FR_CANCEL_TRANSACTION_URL = FR_BASE_URL + "walletvoid";
    public static final String FR_REVERSE_TRANSACTION_URL = FR_BASE_URL + "walletreverse";

    // parameter for free recharge constanst
    public static final String FC_MERCHANT_ID = "fc_merchantID";
    public static final String FC_TERMINAL_ID = "fc_terminalID ";
    public static final String FC_PROC_CODE = "fc_ProcCode ";
    public static final String FC_WALLET_VOID_PROC_ID = "fc_VoidProcCode ";
    public static final String FC_WALLET_REVERSE_PROC_ID = "fc_ReverseProcCode ";
    public static final String FC_SALT = "fc_salt ";
    public static  int FC_COUNT = 0;


}
