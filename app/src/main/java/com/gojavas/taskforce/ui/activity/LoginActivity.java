package com.gojavas.taskforce.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.gojavas.taskforce.R;
import com.gojavas.taskforce.database.DatabaseHelper;
import com.gojavas.taskforce.database.UserHelper;
import com.gojavas.taskforce.entity.UserEntity;
import com.gojavas.taskforce.manager.BackupManager;
import com.gojavas.taskforce.parserbean.LoginBean;
import com.gojavas.taskforce.services.GPSTracker;
import com.gojavas.taskforce.utils.Constants;
import com.gojavas.taskforce.utils.TruPayConstants;
import com.gojavas.taskforce.utils.Utility;
import com.gojavas.taskforce.utils.UtilityScheduler;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private RelativeLayout mLoginLayout;
    private Button mLoginButton, mScanButton;
    private EditText mUsernameEditText, mPasswordEditText;
    private CheckBox mRememberMe;
    private ImageView mLogo;
    private String Folder_Name = "GoJavasApk";
    public static String File_Name;
    String version_App, version_Server, apk_url;
    private static final int REQUEST_READ_PHONE_STATE = 1001;
    private String mUsername, mPassword, mQrCode, mPhoneNumber, mSimNo;
    String PNP_ACCEPTED = "PNP_ACCEPTED";
    protected Context appContext = null;
    public static long downloadId;
    private SubscriptionManager subscriptionManager;
    private TextView deviceId;

    CompleteReceiver completeReceiver;

    ProgressDialog progressDialog;
    DownloadManager downloadmanager;

    class CompleteReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            /**
             * get the id of download which have download success, if the id is my id and it's status is successful,
             * then install it
             **/


            hideProgrss();

            String action = intent.getAction();
            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {

                long completeDownloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);

                if (completeDownloadId == downloadId) {

                    DownloadManager.Query query = new DownloadManager.Query();
                    query.setFilterById(completeDownloadId);
                    Cursor c = downloadmanager.query(query);
                    if (c.moveToFirst()) {
                        int columnIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS);
                        if (DownloadManager.STATUS_SUCCESSFUL == c.getInt(columnIndex)) {

//              Toast.makeText(LoginActivity.this, completeDownloadId + "", Toast.LENGTH_LONG).show();
//              initData();
//              updateView()
//              if download successful, install apk
                            String apkFilePath = new StringBuilder(Environment.getExternalStorageDirectory().getAbsolutePath())
                                    .append(File.separator).append(Folder_Name).append(File.separator)
                                    .append(File_Name).toString();
                            install(context, apkFilePath);

                        }
                    }
                }
            }
        }
    }

    ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
        appContext = getApplicationContext();
        deviceId = findViewById(R.id.device_id);
        deviceId.setText("Device ID : " + Utility.getDeviceId());
        runtimeSecurityPermission();


//        PackageInfo pInfo = null;
//        try {
//            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
//            version_App = pInfo.versionName;
//            checkApiVersion();
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        }
    }


    private void startLoginProcess() {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            subscriptionManager = (SubscriptionManager) appContext.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
            List<SubscriptionInfo> subsList = subscriptionManager.getActiveSubscriptionInfoList();
            if (subsList!=null) {
                for (SubscriptionInfo subsInfo : subsList) {
                    if (subsInfo != null) {
                        mSimNo  = subsInfo.getIccId();
                    }
                }
            }
        }else{
            mSimNo = telephonyManager.getSimSerialNumber();
        }
        int simState = telephonyManager.getSimState();
        if (simState == TelephonyManager.SIM_STATE_ABSENT) {
            // Sim card not present
            Utility.showAlertDialog(LoginActivity.this, Constants.ERROR_SIM_CARD_TITLE, Constants.ERROR_SIM_CARD_MESSAGE);
        } else {
            // Check device date
            try {
                int dateCheck = Settings.System.getInt(getContentResolver(), Settings.System.AUTO_TIME);
                int timeZoneCheck = Settings.System.getInt(getContentResolver(), Settings.System.AUTO_TIME_ZONE);
                if (dateCheck == 0) {
                    // Automatic date option is not selected
                    showDateDialog(Constants.INCORRECT_DATE_TITLE, Constants.INCORRECT_DATE_MESSAGE);
                    return;
                } else if (timeZoneCheck == 0) {
                    // Automatic time zone option is not selected
                    showDateDialog(Constants.INCORRECT_TIME_ZONE_TITLE, Constants.INCORRECT_TIME_ZONE_MESSAGE);
                    return;
                }
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }
            // Sim card is present
            GPSTracker gpsTracker = new GPSTracker(LoginActivity.this);
            if (!gpsTracker.canGetLocation()) {
                gpsTracker.showSettingsAlert(LoginActivity.this);
            } else {
                if (!Utility.isInternetConnected(LoginActivity.this)) {
                    Utility.showToast(LoginActivity.this, Constants.ERROR_INTERNET_CONNECTION);
                }
            }
        }

        registerViews();
        registerListeners();

        progressDialog = new ProgressDialog(LoginActivity.this);
        completeReceiver = new CompleteReceiver();

        SharedPreferences shared = appContext.getSharedPreferences("PrivacyPolicy",MODE_PRIVATE);
        String pnpAccepted = shared.getString("IsSavedPP","");
        if (pnpAccepted != null && !TextUtils.isEmpty(pnpAccepted) && !pnpAccepted.equals("") && pnpAccepted.equals("OK")) {
        }else{
            Intent pPolicy = new Intent(LoginActivity.this, PrivacyPolicyActivity.class);
            pPolicy.putExtra("From", "Login");
            startActivity(pPolicy);
        }
    }

    private void checkApiVersion() {
        showProgrss("Please wait...");

        StringRequest strReq = new StringRequest(Request.Method.GET, Constants.APK_VERSION, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("RESPONSE", response.toString());

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject jsonObject1 = jsonObject.getJSONObject("row");
                    version_Server = jsonObject1.getString("version");
                    apk_url = jsonObject1.getString("url");

                    hideProgrss();
                    if (Float.parseFloat(version_Server) > Float.parseFloat(version_App)) {

                        File_Name = "GoDeliveryApk" + version_Server + ".apk";
                        String servicestring = Context.DOWNLOAD_SERVICE;

                        File folder = Environment.getExternalStoragePublicDirectory(Folder_Name);
                        if (!folder.exists() || !folder.isDirectory()) {
                            folder.mkdirs();
                        } else {

                            // if same file name exit . it may be a case of when download is not completed properly.
                            File f = new File(folder, File_Name);
                            if (f.exists()) {
                                f.delete();
                            }
                        }

                        downloadmanager = (DownloadManager) getSystemService(servicestring);
                        Uri uri = Uri
                                .parse(apk_url);
                        DownloadManager.Request request = new DownloadManager.Request(uri);
                        request.setDestinationInExternalPublicDir(Folder_Name, File_Name);
                        downloadId = downloadmanager.enqueue(request);
                        showProgrss("Downloading latest app. Please wait...");


                        registerReceiver(completeReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    hideProgrss();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgrss();
                VolleyLog.d("tag", "Error: " + error.getMessage());

            }
        });

        TaskForceApplication.getInstance().addToRequestQueue(strReq);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_button:
//                ArrayList<String> imagesList = new ArrayList<>();
//                String basePath = "/storage/emulated/0/DCIM/Camera/";
//                imagesList.add(basePath + "1.jpg");
//                imagesList.add(basePath + "2.jpg");
//                imagesList.add(basePath + "3.jpg");
//                imagesList.add(basePath + "4.jpg");
//                imagesList.add(basePath + "5.jpg");
//                imagesList.add(basePath + "6.jpg");
//                imagesList.add(basePath + "7.jpg");
//                imagesList.add(basePath + "8.jpg");
//                imagesList.add(basePath + "9.jpg");
//                for(String path : imagesList) {
//                    File imageFile = new File(path);
//                    if(imageFile.exists()) {
//                        System.out.println(imageFile.getName() + " inserted in uploading queue");
//                        Intent uploadIntent = new Intent(LoginActivity.this, UploadManager.class);
//                        uploadIntent.putExtra(UploadManager.UPLOAD_FILE_PATH, path);
//                        startService(uploadIntent);
//                    }
//                }
                loginButtonClicked();
                break;
            case R.id.scan_button:
                scanButtonClicked();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            mQrCode = result.getContents();
            if (mQrCode != null && !TextUtils.isEmpty(mQrCode)) {
                Utility.showShortToast(LoginActivity.this, "Scanned success");
//                showPhoneNumberDialog(true);
                login(true);
            } else {
                Utility.showShortToast(LoginActivity.this, "Unable to scan");
            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * Register all the views
     */
    private void registerViews() {
        mLogo = (ImageView) findViewById(R.id.login_logo);
        mLoginLayout = (RelativeLayout) findViewById(R.id.login_layout);
        mLoginButton = (Button) findViewById(R.id.login_button);
        mScanButton = (Button) findViewById(R.id.scan_button);
        mUsernameEditText = (EditText) findViewById(R.id.login_username_edittext);
        mPasswordEditText = (EditText) findViewById(R.id.login_Password_edittext);
        mRememberMe = (CheckBox) findViewById(R.id.login_remember_me);

        String savedUsername = Utility.getFromSharedPrefs(LoginActivity.this, Constants.USERNAME_KEY);
        if (savedUsername != null && !TextUtils.isEmpty(savedUsername) && !savedUsername.equals("")) {
            // username is saved
            mUsernameEditText.setText(savedUsername);
        }
    }

    /**
     * Register listeners on all the views
     */
    private void registerListeners() {
        mLoginButton.setOnClickListener(this);
        mScanButton.setOnClickListener(this);
    }

    /**
     * Handle login button click
     */
    private void loginButtonClicked() {
        if (!Utility.isInternetConnected(LoginActivity.this)) {
            Utility.showToast(LoginActivity.this, Constants.ERROR_INTERNET_CONNECTION);
            return;
        }
        // Save logged in time
        Utility.saveToSharedPrefs(LoginActivity.this, Constants.LOGGED_IN_TIME, System.currentTimeMillis() + "");
        if (mLoginLayout.getVisibility() == View.GONE) {
            // Layout for entering login credentials is not visible
            mLogo.setVisibility(View.GONE);
            mLoginLayout.setVisibility(View.VISIBLE);
        } else {
            // Validate login fields
            if (validateLogin()) {
                // login fields are not blank
//                showPhoneNumberDialog(false);
                login(false);
            } else {
                Utility.showToast(LoginActivity.this, "Fill all the required fields");
            }
        }
    }

    /**
     * Check username and password are entered or not
     *
     * @return
     */
    private boolean validateLogin() {
        mUsername = mUsernameEditText.getText().toString().trim();
        if (mUsername == null || TextUtils.isEmpty(mUsername) || mUsername.equals("")) {
            return false;
        }
//        mPassword = mPasswordEditText.getText().toString().trim();
//        if (mPassword == null || TextUtils.isEmpty(mPassword) || mPassword.equals("")) {
//            return false;
//        }
        return true;
    }

    /**
     * Go to phone number screen
     */
    private void goToPhoneNumberScreen() {
        String userName = mUsernameEditText.getText().toString().trim();
        String password = mPasswordEditText.getText().toString();
        Intent phoneNumberIntent = new Intent(LoginActivity.this, PhoneNumberActivity.class);
        phoneNumberIntent.putExtra("Username", userName);
        phoneNumberIntent.putExtra("Password", password);
        startActivity(phoneNumberIntent);
    }



    public void runtimeSecurityPermission(){
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);
        } else {
            //TODO
            startLoginProcess();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_READ_PHONE_STATE:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    //TODO
                    startLoginProcess();
                }
                break;

            default:
                break;
        }
    }

    /**
     * Go to pincode screen
     */
    private void goToPincodeScreen(JSONObject jsonObject) {
        Intent validationIntent = new Intent(LoginActivity.this, PhoneNumberValidation.class);
        validationIntent.putExtra("OTPRequest", jsonObject.toString());
        startActivity(validationIntent);
        finish();
    }

    /**
     * Show dialog to enter phone number
     */
    public void showPhoneNumberDialog(final boolean isQr) {
        hideProgrss();
        final Dialog dialog = new Dialog(LoginActivity.this, R.style.customDialogTheme);
        dialog.setContentView(R.layout.dialog_edittext);
        final EditText phoneNumberEditText = (EditText) dialog.findViewById(R.id.dialog_edittext);
        Button doneButton = (Button) dialog.findViewById(R.id.dialog_done_button);

        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPhoneNumber = phoneNumberEditText.getText().toString().trim();
                if (mPhoneNumber == null || mPhoneNumber.isEmpty() || TextUtils.isEmpty(mPhoneNumber)) {
                    Utility.showToast(LoginActivity.this, "Please enter mobile number");
                } else if (mPhoneNumber.length() != 10) {
                    Utility.showToast(LoginActivity.this, "Please enter valid mobile number");
                } else {
                    dialog.dismiss();
                    showProgrss(Constants.PLEASE_WAIT);
                    JSONObject requestJson = createOTPLoginRequestJson(isQr);
                    System.out.println("otp login request: " + requestJson);
                    JsonObjectRequest loginRequest = new JsonObjectRequest(Request.Method.POST, Constants.OTP_LOGIN_URL, requestJson, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject jsonObject) {
                            hideProgrss();
                            System.out.println("result: " + jsonObject.toString());
                            try {
                                String success = jsonObject.getString(Constants.SUCCESS);
                                if (success.equalsIgnoreCase("1")) {
                                    if (isQr) {
                                        goToPincodeScreen(createOtpJson("QrLogin"));
                                    } else {
                                        goToPincodeScreen(createOtpJson("Login"));
                                    }
                                } else {
                                    if (jsonObject.has(Constants.ERROR_MESSAGE)) {
                                        String errorMessage = jsonObject.getString(Constants.ERROR_MESSAGE);
                                        Utility.showToast(LoginActivity.this, errorMessage);
                                    } else {
                                        Utility.showToast(LoginActivity.this, Constants.UNKNOWN_ERROR_MESSAGE);
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            volleyError.printStackTrace();
                            hideProgrss();
                            Utility.showToast(LoginActivity.this, Constants.TRY_AGAIN);
                        }
                    });
                    TaskForceApplication.getInstance().addToRequestQueue(loginRequest);
                }
            }
        });

        dialog.show();
    }

    /**
     * Scan button is clicked
     */
    private void scanButtonClicked() {
        if (!Utility.isInternetConnected(LoginActivity.this)) {
            Utility.showToast(LoginActivity.this, Constants.ERROR_INTERNET_CONNECTION);
            return;
        }
        // Save logged in time
        Utility.saveToSharedPrefs(LoginActivity.this, Constants.LOGGED_IN_TIME, System.currentTimeMillis() + "");
        new IntentIntegrator(this).initiateScan();
    }


    private LoginBean parseXML(XmlPullParser parser) throws XmlPullParserException,IOException
    {
        int eventType = parser.getEventType();
        LoginBean loginBean = null;
        while (eventType != XmlPullParser.END_DOCUMENT){
            String name = null;
            switch (eventType){
                case XmlPullParser.START_DOCUMENT:
                    break;
                case XmlPullParser.START_TAG:
                    name = parser.getName();
                    if (name!=null && name.equalsIgnoreCase("MobileUser")){
                        loginBean = new LoginBean();
                    } else if (loginBean != null){
                        if (name.equalsIgnoreCase("BoyID")){
                            loginBean.setBoyID(parser.nextText());
                        } else if (name.equalsIgnoreCase("UserID")){
                            loginBean.setUserID(parser.nextText());
                        } else if (name.equalsIgnoreCase("Message")){
                            loginBean.setMessage(parser.nextText());
                        }else if (name.equalsIgnoreCase("BoyName")){
                            loginBean.setBoyName(parser.nextText());
                        }else if (name.equalsIgnoreCase("HUBName")){
                            loginBean.setHubName(parser.nextText());
                        }
                    }
                    break;
                case XmlPullParser.END_TAG:

            }
            eventType = parser.next();
        }
        return loginBean;
    }

    /**
     * Call login api
     */
    private void callLoginApi() {
//        Intent signatureIntent = new Intent(LoginActivity.this, SignatureActivity.class);
//        startActivityForResult(signatureIntent, 3);
        JSONObject requestJson = createLoginRequestJson();

        if(true){


                RequestQueue queue = Volley.newRequestQueue(this);
                String url ="http://www.google.com";

                StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.BASE_URL+"/ValidateMobileUser",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.i("TEST",response);
                                Log.i("TEST",response);
                                hideProgrss();
                                XmlPullParserFactory pullParserFactory;
                                try {
                                    pullParserFactory = XmlPullParserFactory.newInstance();
                                    XmlPullParser parser = pullParserFactory.newPullParser();
//                                    InputStream in_s = new ByteArrayInputStream(response.getBytes(StandardCharsets.UTF_8));
                                    InputStream in_s = new ByteArrayInputStream(response.getBytes("UTF-8"));
                                    parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                                    parser.setInput(in_s, null);
                                    LoginBean loginBean = parseXML(parser);
                                    if(loginBean!=null&&loginBean.getMessage()!=null){
                                        if(loginBean.getMessage().equalsIgnoreCase("SUCCESS")){
                                            Utility.saveToSharedPrefs(LoginActivity.this, TruPayConstants.TRUPAY_USERNAME, mUsername);
                                            if(loginBean.getBoyID()!=null)
                                            Utility.saveToSharedPrefs(LoginActivity.this, "BOY_ID", loginBean.getBoyID());
                                            if(loginBean.getUserID()!=null)
                                            Utility.saveToSharedPrefs(LoginActivity.this, "USER_ID", loginBean.getUserID());
                                            if(loginBean.getUserID()!=null)
                                                Utility.saveToSharedPrefs(LoginActivity.this, "USER_ID", loginBean.getUserID());
                                            if(loginBean.getBoyName()!=null)
                                                Utility.saveToSharedPrefs(LoginActivity.this, "BOY_NAME", loginBean.getBoyName());
                                            if(loginBean.getHubName()!=null)
                                                Utility.saveToSharedPrefs(LoginActivity.this, "HUB_NAME", loginBean.getHubName());
                                            Utility.saveToSharedPrefs(LoginActivity.this, Constants.USER_LOGGED_IN, "true");
                                            goToHomeScreen();
                                        }else{
                                            Utility.showToast(LoginActivity.this, loginBean.getMessage());
                                        }
                                    }
                                } catch (XmlPullParserException e) {
                                    Utility.showToast(LoginActivity.this, Constants.UNKNOWN_ERROR_MESSAGE);
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    // TODO Auto-generated catch block
                                    Utility.showToast(LoginActivity.this, Constants.UNKNOWN_ERROR_MESSAGE);
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Utility.showToast(LoginActivity.this, Constants.UNKNOWN_ERROR_MESSAGE + ". Please try after some time.");
                        hideProgrss();
                    }
                }){

                    @Override
                    protected Map<String,String> getParams(){


                        Map<String,String> params = new HashMap<String, String>();
//                        params.put(Constants.LOGIN_USERNAME_KEY,"9313644700");
//                        params.put(Constants.LOGIN_IMEI_KEY,"355923071015998");
                          params.put(Constants.LOGIN_USERNAME_KEY,mUsername);
                          params.put(Constants.LOGIN_IMEI_KEY,Utility.getDeviceId());
                          if (mUsername.equalsIgnoreCase("9999999999")) {
                              params.put(Constants.LOGIN_IMEI_KEY,"999999999999999");
                          }
//                          params.put(Constants.LOGIN_USERNAME_KEY,"9871060908");
//                          params.put(Constants.LOGIN_IMEI_KEY,"358213081209279");
                        // Fasto
//                        params.put(Constants.LOGIN_USERNAME_KEY,"7982949347");
//                        params.put(Constants.LOGIN_IMEI_KEY,"911533204542383");
//                        params.put(Constants.LOGIN_USERNAME_KEY,"9781162495");
//                        params.put(Constants.LOGIN_IMEI_KEY,"911595555302626");
//                        params.put(Constants.LOGIN_USERNAME_KEY,"9968546950");
//                        params.put(Constants.LOGIN_USERNAME_KEY,"9041773530");
//                        params.put(Constants.LOGIN_IMEI_KEY,"861428035736322");
//                        params.put(Constants.LOGIN_USERNAME_KEY,"9999656455");
//                        params.put(Constants.LOGIN_IMEI_KEY,"353918058163136"
// ]);


                        return params;
                    }
                };
                queue.add(stringRequest);
            return;
        }





        System.out.println("login request: " + requestJson.toString());
        JsonObjectRequest loginRequest  = new JsonObjectRequest(Request.Method.POST, Constants.LOGIN_URL, requestJson, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {

                System.out.println("result: " + jsonObject.toString());
                try {
                    String success = jsonObject.getString(Constants.SUCCESS);
                    if (success.equalsIgnoreCase("1")) {
                        // save TruPay credentials data in shared preference
                        Utility.saveToSharedPrefs(LoginActivity.this, TruPayConstants.TRUPAY_CLIENT_ID, jsonObject.getString("gt_client_id"));
                        Utility.saveToSharedPrefs(LoginActivity.this, TruPayConstants.TRUPAY_CLIENT_SECRET, jsonObject.getString("gt_client_secret"));
                        Utility.saveToSharedPrefs(LoginActivity.this, TruPayConstants.TRUPAY_GRANTTYPE, jsonObject.getString("gt_grant_type"));
                        Utility.saveToSharedPrefs(LoginActivity.this, TruPayConstants.TRUPAY_USERNAME, jsonObject.getString("gt_username"));
                        Utility.saveToSharedPrefs(LoginActivity.this, TruPayConstants.TRUPAY_PASSWORD, jsonObject.getString("gt_password"));
                        Utility.saveToSharedPrefs(LoginActivity.this, TruPayConstants.MERCHANT_ID, jsonObject.getString("gt_merchantid"));

                        String phone = jsonObject.getString("phone");
                        if (phone != null && phone.equalsIgnoreCase("YES")) {
                            showPhoneNumberDialog(false);
                        } else {
                            loginComplete(jsonObject);
                        }
                    } else {
                        hideProgrss();
                        if (jsonObject.has(Constants.ERROR_MESSAGE)) {
                            String errorMessage = jsonObject.getString(Constants.ERROR_MESSAGE);
                            Utility.showToast(LoginActivity.this, errorMessage);
                        } else {
                            Utility.showToast(LoginActivity.this, Constants.UNKNOWN_ERROR_MESSAGE);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                hideProgrss();
                volleyError.printStackTrace();
            }
        });
        TaskForceApplication.getInstance().addToRequestQueue(loginRequest);
    }

    /**
     * create json to send for login
     *
     * @return
     */
    private JSONObject createLoginRequestJson() {
        JSONObject finalJson = new JSONObject();
        try {
            finalJson.put(Constants.LOGIN_USERNAME_KEY, "9999656477");
//            finalJson.put(Constants.PASSWORD_KEY, mPassword);
            finalJson.put(Constants.LOGIN_IMEI_KEY, Utility.getDeviceId());
//            finalJson.put(Constants.VERSION_KEY, version_App);
//            finalJson.put(Constants.SIM_NO_KEY, mSimNo);



//            finalJson.put(Constants.USERNAME_KEY, mUsername);
//            finalJson.put(Constants.PASSWORD_KEY, mPassword);
//            finalJson.put(Constants.IMEI_KEY, Utility.getDeviceId());
//            finalJson.put(Constants.VERSION_KEY, version_App);
//            finalJson.put(Constants.SIM_NO_KEY, mSimNo);
        } catch (JSONException je) {
            je.printStackTrace();
        }
        return finalJson;
    }

    /**
     * create json to send for login
     *
     * @return
     */
    private JSONObject createOTPLoginRequestJson(boolean isQr) {
        JSONObject finalJson = new JSONObject();

        try {
            if (isQr) {
                finalJson.put(Constants.QR_CODE_KEY, mQrCode);
            } else {
                finalJson.put(Constants.USERNAME_KEY, mUsername);
                finalJson.put(Constants.PASSWORD_KEY, mPassword);
            }
            finalJson.put(Constants.MOBILE_KEY, mPhoneNumber);
            finalJson.put(Constants.IMEI_KEY, Utility.getDeviceId());
        } catch (JSONException je) {
            je.printStackTrace();
        }
        return finalJson;
    }

    /**
     * Login via QR
     */
    private void callQrLoginApi() {



        JSONObject requestJson = new JSONObject();
        try {
            requestJson.put(Constants.QR_CODE_KEY, mQrCode);
            requestJson.put(Constants.IMEI_KEY, Utility.getDeviceId());
            requestJson.put(Constants.VERSION_KEY, version_App);
            requestJson.put(Constants.SIM_NO_KEY, mSimNo);
        } catch (JSONException je) {
            je.printStackTrace();
        }
        JsonObjectRequest loginRequest = new JsonObjectRequest(Request.Method.POST, Constants.LOGIN_URL, requestJson, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                System.out.println("result: " + jsonObject.toString());
                try {
                    String success = jsonObject.getString(Constants.SUCCESS);
                    if (success.equalsIgnoreCase("1")) {
                        // save TruPay credentials data in shared preference
                        Utility.saveToSharedPrefs(LoginActivity.this, TruPayConstants.TRUPAY_CLIENT_ID, jsonObject.getString("gt_client_id"));
                        Utility.saveToSharedPrefs(LoginActivity.this, TruPayConstants.TRUPAY_CLIENT_SECRET, jsonObject.getString("gt_client_secret"));
                        Utility.saveToSharedPrefs(LoginActivity.this, TruPayConstants.TRUPAY_GRANTTYPE, jsonObject.getString("gt_grant_type"));
                        Utility.saveToSharedPrefs(LoginActivity.this, TruPayConstants.TRUPAY_USERNAME, jsonObject.getString("gt_username"));
                        Utility.saveToSharedPrefs(LoginActivity.this, TruPayConstants.TRUPAY_PASSWORD, jsonObject.getString("gt_password"));
                        Utility.saveToSharedPrefs(LoginActivity.this, TruPayConstants.MERCHANT_ID, jsonObject.getString("gt_merchantid"));

//                        save Free Recharge credentials data in shared preference
//                        Utility.saveToSharedPrefs(LoginActivity.this, FreeRechargeConstants.FC_MERCHANT_ID, jsonObject.getString("fc_merchantID"));
//                        Utility.saveToSharedPrefs(LoginActivity.this, FreeRechargeConstants.FC_TERMINAL_ID, jsonObject.getString("fc_terminalID"));
//                        Utility.saveToSharedPrefs(LoginActivity.this, FreeRechargeConstants.FC_PROC_CODE, jsonObject.getString("fc_ProcCode"));
//                        Utility.saveToSharedPrefs(LoginActivity.this, FreeRechargeConstants.FC_WALLET_VOID_PROC_ID, jsonObject.getString("fc_VoidProcCode"));
//                        Utility.saveToSharedPrefs(LoginActivity.this, FreeRechargeConstants.FC_WALLET_REVERSE_PROC_ID, jsonObject.getString("fc_ReverseProcCode"));
//                        Utility.saveToSharedPrefs(LoginActivity.this, FreeRechargeConstants.FC_SALT, jsonObject.getString("fc_salt"));

                        String phone = jsonObject.getString("phone");
                        if (phone != null && phone.equalsIgnoreCase("YES")) {
                            showPhoneNumberDialog(true);
                        } else {
                            loginComplete(jsonObject);
                        }
                    } else {
                        hideProgrss();
                        if (jsonObject.has(Constants.ERROR_MESSAGE)) {
                            String errorMessage = jsonObject.getString(Constants.ERROR_MESSAGE);
                            Utility.showToast(LoginActivity.this, errorMessage);
                        } else {
                            Utility.showToast(LoginActivity.this, Constants.UNKNOWN_ERROR_MESSAGE);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                hideProgrss();
                volleyError.printStackTrace();
                Utility.showToast(LoginActivity.this, Constants.TRY_AGAIN);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> pars = new HashMap<String, String>();
                pars.put("Content-Type", "application/json");
                return pars;
            }


        };
        TaskForceApplication.getInstance().addToRequestQueue(loginRequest);
    }

    /**
     * Go to home screen
     */
    private void goToHomeScreen() {
        Utility.showToast(LoginActivity.this, "Success");
        Intent homeIntent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(homeIntent);
        finish();
    }

    /**
     * Create json for otp api
     *
     * @return
     */
    private JSONObject createOtpJson(String type) {
        JSONObject finalJson = new JSONObject();
        try {
            if (type.equalsIgnoreCase("Login")) {
                finalJson.put(Constants.USERNAME_KEY, mUsername);
                finalJson.put(Constants.PASSWORD_KEY, mPassword);
            } else if (type.equalsIgnoreCase("QrLogin")) {
                finalJson.put(Constants.QR_CODE_KEY, mQrCode);
            }
            finalJson.put(Constants.MOBILE_KEY, mPhoneNumber);
            finalJson.put(Constants.IMEI_KEY, Utility.getDeviceId());
            finalJson.put(Constants.SIM_NO_KEY, mSimNo);
        } catch (JSONException je) {
            je.printStackTrace();
        }
        return finalJson;
    }

    /**
     * login
     *
     * @param isScan
     */
    private void login(boolean isScan) {
        showProgrss(Constants.PLEASE_WAIT);
        if (isScan) {
            callQrLoginApi();
        } else {
            callLoginApi();
        }
    }

    private void loginComplete(JSONObject jsonObject) {
        Log.i("login data", jsonObject + "");
        // Successfully logged in
        // Save user
        insertInDatabase(jsonObject);

        Utility.callDesignApi(new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                hideProgrss();
                System.out.println("Design downloaded: " + jsonObject.toString());
                String fileName = "Design.txt";

                FileOutputStream fos = null;
                try {
                    fos = TaskForceApplication.getInstance().openFileOutput(fileName, Context.MODE_PRIVATE);
                    fos.write(jsonObject.toString().getBytes());
                    fos.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Utility.saveToSharedPrefs(LoginActivity.this, Constants.USER_LOGGED_IN, "true");
                // Read data from local logs.txt file
                BackupManager.readSavedData();
                // Start scheduler
                UtilityScheduler.startAllScheduler(LoginActivity.this);
                UtilityScheduler.startSync(LoginActivity.this);
                // Go to home screen
                goToHomeScreen();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                hideProgrss();
                Utility.showToast(LoginActivity.this, Constants.TRY_AGAIN);
                volleyError.printStackTrace();
            }
        });
    }

    /**
     * Insert user data in database
     *
     * @param response
     */
    private void insertInDatabase(JSONObject response) {
        String branch = "";
        String username = "";
        try {
            JSONObject jsonObject = response.getJSONObject(Constants.USER_DATA);
            String sr = jsonObject.getString(DatabaseHelper.SR);
            String emp_code = jsonObject.getString(DatabaseHelper.EMP_CODE);
            username = jsonObject.getString(DatabaseHelper.USERNAME);
            String password = jsonObject.getString(DatabaseHelper.PASSWORD);
            String firstname = jsonObject.getString(DatabaseHelper.FIRSTNAME);
            String city = jsonObject.getString(DatabaseHelper.CITY);
            branch = jsonObject.getString(DatabaseHelper.BRANCH);
            String imei_no = jsonObject.getString(DatabaseHelper.IMEI_NO);
            String mobile_no = jsonObject.getString(DatabaseHelper.MOBILE_NO);
            String datetime = jsonObject.getString(DatabaseHelper.DATETIME);

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

            UserHelper.getInstance().insertOrUpdate(user);

            Utility.saveToSharedPrefs(LoginActivity.this, Constants.EMP_CODE_KEY, emp_code.replace(Constants.EMP_CODE_SUFFIX, ""));

        } catch (JSONException je) {
            je.printStackTrace();
        }
        // Set Ezetaps username;
        String ezetapUsername = branch + "_" + Utility.getFromSharedPrefs(LoginActivity.this, Constants.EMP_CODE_KEY);
        Utility.saveToSharedPrefs(LoginActivity.this, Constants.EZETAP_USERNAME_KEY, ezetapUsername);
        // Save username in shared preferences
        Utility.saveToSharedPrefs(LoginActivity.this, Constants.USERNAME_KEY, username);
    }


    /**
     * install app
     *
     * @param context
     * @param filePath
     * @return whether apk exist
     */
    public static boolean install(Context context, String filePath) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        File file = new File(filePath);
        if (file != null && file.length() > 0 && file.exists() && file.isFile()) {
            i.setDataAndType(Uri.parse("file://" + filePath), "application/vnd.android.package-archive");
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);

            ((Activity) context).finish();
            return true;
        }
        return false;
    }

    public void showProgrss(String message) {
        if (progressDialog != null && !progressDialog.isShowing()) {
            progressDialog.show();
            progressDialog.setMessage(message);
            progressDialog.setCancelable(false);
        }
    }


    public void hideProgrss() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (completeReceiver != null && completeReceiver.isInitialStickyBroadcast())
            unregisterReceiver(completeReceiver);
    }

    /**
     * Show wrong date alert
     */
    private void showDateDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(android.provider.Settings.ACTION_DATE_SETTINGS));
                finish();
                dialog.dismiss();
            }
        });

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


}