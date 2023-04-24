package com.gojavas.taskforce.utils;

import com.ezetap.sdk.EzeConstants;
import com.ezetap.sdk.EzetapApiConfig;

/**
 * Created by GJS280 on 7/5/2015.
 */
public class EzetapConstants {

    public static final EzeConstants.LoginAuthMode AUTH_MODE = EzeConstants.LoginAuthMode.EZETAP_LOGIN_BYPASS;
//    public static final String APP_KEY = "8979a5af-c1d5-4295-80cd-6ff04c481509"; // Test
    public static final String APP_KEY = "37b87158-604c-49a6-b6ff-1e3049a54f86"; // Production
    public static final String MERCHANT_NAME = "Quickdel";
    public static final String CURRENCY_CODE = "INR";
//    public static final EzeConstants.AppMode APP_MODE = EzeConstants.AppMode.EZETAP_DEMO; // Test
    public static final EzeConstants.AppMode APP_MODE = EzeConstants.AppMode.EZETAP_PROD; // Production
    public static final boolean CAPTURE_SIGNATURE = false;
    public static final EzeConstants.CommunicationChannel PREFERRED_CHANNEL = EzeConstants.CommunicationChannel.NONE;

    public static EzetapApiConfig API_CONFIG = new EzetapApiConfig(AUTH_MODE, APP_KEY, MERCHANT_NAME, CURRENCY_CODE, APP_MODE, CAPTURE_SIGNATURE, PREFERRED_CHANNEL);
}
