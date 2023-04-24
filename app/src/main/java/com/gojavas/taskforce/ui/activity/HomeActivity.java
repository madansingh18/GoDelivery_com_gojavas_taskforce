package com.gojavas.taskforce.ui.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ezetap.sdk.AppConstants;
import com.ezetap.sdk.EzeConstants;
import com.ezetap.sdk.EzetapPayApis;
import com.gojavas.taskforce.R;
import com.gojavas.taskforce.database.DatabaseHelper;
import com.gojavas.taskforce.database.DrsHelper;
import com.gojavas.taskforce.entity.DrsEntity;
import com.gojavas.taskforce.entity.ReversePickup;
import com.gojavas.taskforce.gcm.QuickstartPreferences;
import com.gojavas.taskforce.manager.BackupManager;
import com.gojavas.taskforce.services.GPSTracker;
import com.gojavas.taskforce.utils.Constants;
import com.gojavas.taskforce.utils.EzetapConstants;
import com.gojavas.taskforce.utils.MswipeConstants;
import com.gojavas.taskforce.utils.Utility;
import com.gojavas.taskforce.utils.UtilityScheduler;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.mswipe.wisepad.apkkit.WisePadController;
import com.mswipe.wisepad.apkkit.WisePadControllerListener;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;

//import com.crittercism.app.Crittercism;

/**
 * Created by GJS280 on 14/4/2015.
 */

public class HomeActivity extends AppCompatActivity implements View.OnClickListener, WisePadControllerListener {


    private RelativeLayout mSequenceLayout,mDRSLayout, mStartLayout, mSyncLayout, mPrivacyPolicy, mSummaryLayout, mEzetapLayout, mMSwipeLayout, mSataticsLayout, mProfileLayout, mLogoutLayout, mHoneManualAWS;
    private TextView mSuccessCountTextView, mFailedCountTextView, mPendingCountTextView,mPickupBadge;
    private ImageView mSyncImage;
    private PieChartView mPieChart;
    private static final int REQUEST_READ_PHONE_STATE = 1001;

    private PieChartData mPieChartData;
    private WisePadController mWisePadController;

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "HomeActivity";

    private static final Uri STATUS_URI = Uri.parse("content://sms");
    ArrayList<DrsEntity> drsEntities;
    /**
     *  visible layout after pulling data from server using SchedularSync Service
     */
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getExtras().getString(Constants.SCHEDULAR_RESPONSE_KEY);
            String errorMessage = intent.getExtras().getString(Constants.SCHEDULAR_RESPONSE_MESSAGE);
            if(message != null && message.equalsIgnoreCase(Constants.SUCCESS_RESPONSE)) {
                // Enable the layouts
                enableSyncLayout();
                if(errorMessage != null && !TextUtils.isEmpty(errorMessage)) {
                    Utility.showToast(HomeActivity.this, errorMessage);
                } else {
                    enableSequenceLayout();
                    enableStartLayout();
                    populateChart();
                }
            } else {
                Utility.showToast(HomeActivity.this, errorMessage);
                enableSyncLayout();
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_READ_PHONE_STATE:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    //TODO
                }
                break;

            default:
                break;
        }
    }

    /**
     * GCM registration receiver
     */
    private BroadcastReceiver mRegistrationBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            boolean sentToken = sharedPreferences.getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);
//            if (sentToken) {
//                mInformationTextView.setText(getString(R.string.gcm_send_message));
//            } else {
//                mInformationTextView.setText(getString(R.string.token_error_message));
//            }
        }
    };
    private RelativeLayout mPickupAlert;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getSupportActionBar().hide();
//        runtimeSecurityPermission();

        // Initialize crittercism
//        Crittercism.initialize(getApplicationContext(), "5608f6578d4d8c0a00d07b0d"); // app-id
//        Crittercism.setUsername(Utility.getDeviceId());

        String userLoggedIn = Utility.getFromSharedPrefs(HomeActivity.this, Constants.USER_LOGGED_IN);
        if(userLoggedIn == null || TextUtils.isEmpty(userLoggedIn) || userLoggedIn.equalsIgnoreCase("false")) {
            // User is not logged in
            // Go to login screen
            goToLoginScreen();
        } else {
            // Check Device date and time zone
            try {
                int dateCheck = android.provider.Settings.System.getInt(getContentResolver(), android.provider.Settings.System.AUTO_TIME);
                int timeZoneCheck = android.provider.Settings.System.getInt(getContentResolver(), Settings.System.AUTO_TIME_ZONE);
                if(dateCheck == 0) {
                    // Automatic date option is not selected
                    showDateDialog(Constants.INCORRECT_DATE_TITLE, Constants.INCORRECT_DATE_MESSAGE);
                } else if(timeZoneCheck == 0) {
                    // Automatic time zone option is not selected
                    showDateDialog(Constants.INCORRECT_TIME_ZONE_TITLE, Constants.INCORRECT_TIME_ZONE_MESSAGE);
                }
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }

            // User is logged in
            registerViews();
            registerListeners();
            Utility.getProofList(HomeActivity.this);
            Utility.getReasonList(HomeActivity.this);
            Utility.getRelationList(HomeActivity.this);

            GPSTracker gpsTracker = new GPSTracker(HomeActivity.this);
            if(!gpsTracker.canGetLocation()) {
                gpsTracker.showSettingsAlert(HomeActivity.this);
            }

            /*if (checkPlayServices()) {
                // Start IntentService to register this application with GCM.
                Intent intent = new Intent(this, RegistrationIntentService.class);
                startService(intent);
            }*/
        }
        mPickupBadge = (TextView) findViewById(R.id.pickup_badge);
        mPickupBadge.setText("0");
        fetchReversePickupList();
    }

    public void runtimeSecurityPermission(){
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);
        } else {
            //TODO
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // If date is changed
        // Delete data and logout user
        String saveDate = Utility.getFromSharedPrefs(HomeActivity.this,Constants.LOGGED_IN_TIME);
        if(!Utility.areEqual(Long.parseLong(saveDate),System.currentTimeMillis())){
            // Logout user
            TaskForceApplication.getInstance().clearApplicationData();
            // Delete saved images
            BackupManager.clearSavedData();
            // Show login screen
            goToLoginScreen();
        }
        fetchDSRList();
//        populateChart();
        // Register all the receivers
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter(Constants.SCHEDULAR_SYNC_ACTION));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver, new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
    }

    @Override
    protected void onPause() {
        // Un-register all the receivers
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister receivers
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            String msg = "" + data.getExtras().getString("errMsg");
            System.out.println(msg);
            switch (requestCode) {
                case WisePadController.MS_LOGIN_ACTIVITY_REQUEST_CODE:
                    String referenceId = data.getStringExtra(MswipeConstants.REFERENCE_ID);
                    String sessionTokeniser = data.getStringExtra(MswipeConstants.SESSION_TOKENISER);
                    Utility.saveToSharedPrefs(HomeActivity.this, MswipeConstants.SESSION_TOKENISER, sessionTokeniser);
                    Utility.saveToSharedPrefs(HomeActivity.this, MswipeConstants.REFERENCE_ID, referenceId);
                    boolean status = data.getExtras().getBoolean("status");
                    if(!status) {
                        Utility.showDialog(HomeActivity.this, "Error", data.getExtras().getString("errMsg"));
                    } else {
                        Utility.showDialog(HomeActivity.this, "MSwipe Login", "You are succesfully logged in");
                        System.out.println("ReferenceId: " + referenceId);
                        System.out.println("SessionTokeniser" + sessionTokeniser);
                    }
                    break;
                case AppConstants.REQ_CODE_INIT_DEVICE:
                    String title = "Initialize Device";
                    String message = "";
                    if (resultCode == EzeConstants.RESULT_SUCCESS) {
                        message = "Initialization of device is completed successfully";
                    } else {
                        message = "Initialization of device is unsuccessful";
                    }
                    Utility.showNormalAlertDialog(HomeActivity.this, title, message);
                    break;


                case REQUEST_SIGNATURE_CODE:
                    Log.i("SIGN","1");
                    byte[] bitmapBytes = data.getByteArrayExtra(Constants.SIGNATURE_IMAGE);
                    Bitmap signatureBitmap = null;
                    signatureBitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
                    ImageView home_sync_image = (ImageView)findViewById(R.id.home_sync_image);
                    home_sync_image.setImageBitmap(signatureBitmap);
                    Log.i("SIGN",signatureBitmap.toString());
                    String path = Utility.getApplicationPath(Constants.PATH_CAPTURED_IMAGE);
                    Log.i("SIGN","333");
                    String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HHmmss", Locale.getDefault()).format(new Date());
                    Log.i("SIGN","444");
                    mSignatureName = "SIGN_" + mDocketNumber + "_" + timeStamp + "##001##" + Utility.getDeviceId() + ".png";
                    Utility.copyBitmap(signatureBitmap, path, mSignatureName);
                    Log.i("SIGN", "555");
                    break;



                default:
                    break;
            }
        } else if(resultCode == RESULT_CANCELED) {
            if(data != null && data.hasExtra("newversoinavailable")){
                Log.v("Test", "Creditsaleview data is null");
                String newversionmsg = data.getExtras().getString("newversoinavailable");
                if(newversionmsg.equalsIgnoreCase("true")){
                    mWisePadController.processUpdateMswipeApplication();
                }
            }
            else if(data != null && data.hasExtra("errMsg")){
                Log.v("Test","Creditsaleview data is null");
                Utility.showDialog(HomeActivity.this, "Cancelled", data.getExtras().getString("errMsg"));

            }
        }else if(resultCode == REQUEST_SIGNATURE_CODE){
// Extract Bitmap bytes from intent bundle
            Log.i("SIGN","1");
            byte[] bitmapBytes = data.getByteArrayExtra(Constants.SIGNATURE_IMAGE);
            Bitmap signatureBitmap = null;
            signatureBitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
            Log.i("SIGN",signatureBitmap.toString());
            String path = Utility.getApplicationPath(Constants.PATH_CAPTURED_IMAGE);
            Log.i("SIGN","333");
            String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HHmmss", Locale.getDefault()).format(new Date());
            Log.i("SIGN","444");
            mSignatureName = "SIGN_" + mDocketNumber + "_" + timeStamp + "##001##" + Utility.getDeviceId() + ".png";
            Utility.copyBitmap(signatureBitmap, path, mSignatureName);
            Log.i("SIGN", "555");
        }
    }

    public String mDocketNumber;
    public String mSignatureName;

    @Override
    public void onClick(View v) {
        Log.i("TEST","111"+v.getId());
        switch (v.getId()) {
            case R.id.home_sequence_layout:
                goToSequenceScreen();
                break;
            case R.id.home_privacy_policy:
                Intent pPolicy = new Intent(HomeActivity.this, PrivacyPolicyActivity.class);
                pPolicy.putExtra("From", "Home");
                startActivity(pPolicy);
                break;

//            case R.id.home_drs_layout:
//                goToDRSScreen();
//                break;
            case R.id.home_start_layout:
                goToTabScreen();
                break;
            case R.id.home_manual_aws:
                Intent manualAws = new Intent(HomeActivity.this, ManualAWS.class);
                startActivity(manualAws);
                break;
            case R.id.home_pickup:
                Intent pickUpIntent = new Intent(HomeActivity.this, PickupAlertActivity.class);
                startActivity(pickUpIntent);
                break;

            case R.id.home_sync_layout:
                if(true){
                    return;
                }
                // Pull call/sms data
                UtilityScheduler.startCallSmsService(HomeActivity.this);
                if(!Utility.isInternetConnected(HomeActivity.this)) {
                    // Backup unsynced files
                    BackupManager.getInstance().updateBackup();
                    Utility.showToast(HomeActivity.this, Constants.ERROR_INTERNET_CONNECTION);
                } else {
                    disableSyncLayout();
                    // Pull data from server
                    UtilityScheduler.startSync(HomeActivity.this);
                }
                break;
            case R.id.home_summary_layout:
                goToDRSScreen();
//                Utility.extractDatabaseFromDevice(HomeActivity.this);
//                Intent summaryIntent = new Intent(HomeActivity.this, SummaryActivity.class);
//                startActivity(summaryIntent);
                break;
            case R.id.home_statistics_layout:
                goToDRSScreen();
//                Intent statisticsIntent = new Intent(HomeActivity.this, StataticsAcitivity.class);
//                startActivity(statisticsIntent);
                break;
            case R.id.home_profile_layout:
                Intent profileIntent = new Intent(HomeActivity.this, ProfileActivity.class);
                startActivity(profileIntent);
                break;
            case R.id.home_ezetap_layout:
                String ezetapUsername = Utility.getFromSharedPrefs(HomeActivity.this, Constants.EZETAP_USERNAME_KEY);
                EzetapPayApis.create(EzetapConstants.API_CONFIG).initializeDevice(this, AppConstants.REQ_CODE_INIT_DEVICE, ezetapUsername, null);
                break;
            case R.id.home_mswipe_layout:
//                String referenceId = Utility.getFromSharedPrefs(HomeActivity.this, MswipeConstants.REFERENCE_ID);
//                String sessionTokeniser = Utility.getFromSharedPrefs(HomeActivity.this, MswipeConstants.SESSION_TOKENISER);
//                if(referenceId != null && sessionTokeniser != null &&
//                        !TextUtils.isEmpty(referenceId) && !TextUtils.isEmpty(sessionTokeniser) &&
//                        referenceId.length() != 0 && sessionTokeniser.length() != 0) {
//                    // Logged in
//                    Utility.showNormalAlertDialog(HomeActivity.this, "Mswipe Login", "Already logged in");
//                    System.out.println("ReferenceId: " + referenceId);
//                    System.out.println("SessionTokeniser" + sessionTokeniser);
//                } else {
//                    // Not logged in
//                    // Show log in dialog

                    mWisePadController = WisePadController.sharedInstance(this, this);
                    Utility.showMswipeLoginDialog(HomeActivity.this, mWisePadController);
//                }
                break;
            case R.id.home_logout_layout:
                showLogoutAlertDialog();
                break;
            default:
                break;
        }
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    /**
     * Register all the views
     */
    private void registerViews() {
        mSequenceLayout = (RelativeLayout) findViewById(R.id.home_sequence_layout);
//        mDRSLayout = (RelativeLayout) findViewById(R.id.home_sequence_layout);
        mStartLayout = (RelativeLayout) findViewById(R.id.home_start_layout);
        mSyncLayout = (RelativeLayout) findViewById(R.id.home_sync_layout);

        mPrivacyPolicy = (RelativeLayout) findViewById(R.id.home_privacy_policy);
        mHoneManualAWS = (RelativeLayout) findViewById(R.id.home_manual_aws);
        mSyncImage = (ImageView) findViewById(R.id.home_sync_image);
        mSummaryLayout = (RelativeLayout) findViewById(R.id.home_summary_layout);
        mSataticsLayout = (RelativeLayout) findViewById(R.id.home_statistics_layout);
        mProfileLayout=(RelativeLayout)findViewById(R.id.home_profile_layout);
        mMSwipeLayout = (RelativeLayout) findViewById(R.id.home_mswipe_layout);
        mEzetapLayout = (RelativeLayout) findViewById(R.id.home_ezetap_layout);
        mLogoutLayout = (RelativeLayout) findViewById(R.id.home_logout_layout);
        mPickupAlert = (RelativeLayout) findViewById(R.id.home_pickup);
        mSuccessCountTextView = (TextView) findViewById(R.id.home_success_count);
        mFailedCountTextView = (TextView) findViewById(R.id.home_failed_count_textview);
        mPendingCountTextView = (TextView) findViewById(R.id.home_pending_count_textview);
        mPieChart = (PieChartView) findViewById(R.id.home_piechart);
        mPieChart.setChartRotation(270, true);
        mPieChart.setChartRotationEnabled(false);

        int count = DrsHelper.getInstance().getDrsCount();
        if(count <= 0) {
            disableSequenceLayout();
            disableStartLayout();
        }
    }

    /**
     * Register listener on all the views
     */
    private void registerListeners() {
        mSequenceLayout.setOnClickListener(this);
//        mDRSLayout.setOnClickListener(this);
        mStartLayout.setOnClickListener(this);
        mSyncLayout.setOnClickListener(this);
        mPrivacyPolicy.setOnClickListener(this);
        mHoneManualAWS.setOnClickListener(this);
        mSummaryLayout.setOnClickListener(this);
        mSataticsLayout.setOnClickListener(this);
        mProfileLayout.setOnClickListener(this);
        mMSwipeLayout.setOnClickListener(this);
        mEzetapLayout.setOnClickListener(this);
        mLogoutLayout.setOnClickListener(this);
        mPickupAlert.setOnClickListener(this);
    }



    private ArrayList<ReversePickup> parsePickupXML(XmlPullParser parser) throws XmlPullParserException,IOException
    {
        int eventType = parser.getEventType();
        ArrayList<ReversePickup> dsrs = null;
        ReversePickup dsr = null;
        while (eventType != XmlPullParser.END_DOCUMENT){
            String name = null;
            switch (eventType){
                case XmlPullParser.START_DOCUMENT:
                    dsrs = new ArrayList();
                    break;
                case XmlPullParser.START_TAG:
                    name = parser.getName();
                    if (name!=null && name.equalsIgnoreCase("ReversePickup")){
                        dsr = new ReversePickup();
                    } else if (dsr != null){
                        if (name.equalsIgnoreCase("ProcessID")){
                            dsr.setProcessID(parser.nextText());
                        }else if (name.equalsIgnoreCase("ManifestNo")){
                            dsr.setManifestNo(parser.nextText());
                        }else if (name.equalsIgnoreCase("TicketNo")){
                            dsr.setTicketNo(parser.nextText());
                        }else if (name.equalsIgnoreCase("PickupPersonName")){
                            dsr.setPickupPersonName(parser.nextText());
                        }else if (name.equalsIgnoreCase("PickupAddress")){
                            dsr.setPickupAddress(parser.nextText());
                        }else if (name.equalsIgnoreCase("PickupPincode")){
                            dsr.setPickupPincode(parser.nextText());
                        }else if (name.equalsIgnoreCase("PickupCity")){
                            dsr.setPickupCity(parser.nextText());
                        }else if (name.equalsIgnoreCase("PickupMobile")){
                            dsr.setPickupMobile(parser.nextText());
                        }else if (name.equalsIgnoreCase("PickupPhone")){
                            dsr.setPickupPhone(parser.nextText());
                        }else if (name.equalsIgnoreCase("PickupEmail")){
                            dsr.setPickupEmail(parser.nextText());
                        }else if (name.equalsIgnoreCase("Product")){
                            dsr.setProduct(parser.nextText());
                        }else if (name.equalsIgnoreCase("Pcs")){
                            dsr.setPcs(parser.next());
                        }else if (name.equalsIgnoreCase("Weight")){
                            dsr.setWeight(parser.next());
                        }else if (name.equalsIgnoreCase("Length")){
                            dsr.setLength(parser.next());
                        }else if (name.equalsIgnoreCase("Width")){
                            dsr.setWidth(parser.next());
                        }else if (name.equalsIgnoreCase("Height")){
                            dsr.setHeight(parser.next());
                        }else if (name.equalsIgnoreCase("Contents")){
                            dsr.setContents(parser.nextText());
                        }else if (name.equalsIgnoreCase("GoodsValue")){
                            dsr.setGoodsValue(parser.next());
                        }else if (name.equalsIgnoreCase("PickupTime")){
                            dsr.setPickupTime(parser.nextText());
                        }

                    }
                    break;
                case XmlPullParser.END_TAG:
                    name = parser.getName();
                    if (name.equalsIgnoreCase("ReversePickup") && dsr != null){
                        dsrs.add(dsr);
                    }
            }
            eventType = parser.next();
        }
        Log.i("MAD",dsrs.toString());
        return dsrs;
    }

    private void fetchReversePickupList(){
        final String boyId = Utility.getFromSharedPrefs(HomeActivity.this, "BOY_ID");
        if(boyId!=null&&boyId.length()>0){
            RequestQueue queue = Volley.newRequestQueue(this);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.BASE_URL+"/GetReversePickup_M",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.i("MAD",response);


                            XmlPullParserFactory pullParserFactory;
                            try {
                                pullParserFactory = XmlPullParserFactory.newInstance();
                                XmlPullParser parser = pullParserFactory.newPullParser();
                                InputStream in_s = new ByteArrayInputStream(response.getBytes("UTF-8"));
                                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                                parser.setInput(in_s, null);
                                ArrayList<ReversePickup> reversePickups = parsePickupXML(parser);
                                if(reversePickups!=null&&reversePickups.size()>0){
                                    if(mPickupBadge!=null)
                                        mPickupBadge.setText(""+reversePickups.size());
                                    try{
                                        String tempLastSyncDateTime = reversePickups.get(0).getSyncDateTime();
                                        if(tempLastSyncDateTime!=null&&tempLastSyncDateTime.length()>0){
                                            Utility.saveToSharedPrefs(HomeActivity.this, "lastSyncDateTime", tempLastSyncDateTime);
                                        }
                                    }catch (Exception e){}
                                }
                            } catch (XmlPullParserException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }){

                @Override
                protected Map<String,String> getParams(){
                    Map<String,String> params = new HashMap<String, String>();
                    params.put("BoyID",boyId);
                    String lastSyncDateTime = "2015-05-17";
                    try{
                        String _lastSyncDateTime = Utility.getFromSharedPrefs(HomeActivity.this,"lastSyncDateTime");
                        if(_lastSyncDateTime!=null&&_lastSyncDateTime.length()>0){
                            lastSyncDateTime = _lastSyncDateTime;
                        }
                    }catch (Exception e){}
                    params.put("SyncDateTime",lastSyncDateTime);
                    return params;
                }
            };
            queue.add(stringRequest);
        }
    }



    /**
     * create json request for pull data api
     * @return
     */
    private JSONObject createPullRequestJson() {
        JSONObject finalJson = new JSONObject();
        String driverId = Utility.getFromSharedPrefs(HomeActivity.this, Constants.EMP_CODE_KEY);
        try {
            finalJson.put("driver_id", driverId);
            finalJson.put("entrydate", "2015-05-17"/*Utility.getCurrentDate()*/);
        } catch (JSONException je) {
            je.printStackTrace();
        }
        return finalJson;
    }

    /**
     * Populate chart
     */
    private void populateChart() {
        List<SliceValue> chartValues = new ArrayList<SliceValue>();
        Resources resources = getResources();
        int successColor = resources.getColor(R.color.success);
        int failedColor = resources.getColor(R.color.failed);
        int pendingColor = resources.getColor(R.color.pending);

        int pendingCount = DrsHelper.getInstance().getPendingDrsCount();
        int successCount = DrsHelper.getInstance().getSuccessDrsCount();
        int failedCount = DrsHelper.getInstance().getFailedDrsCount();
        int total = pendingCount + successCount + failedCount;

        mSuccessCountTextView.setText(successCount + "");
        mFailedCountTextView.setText(failedCount + "");
        mPendingCountTextView.setText(pendingCount + "");

        try {
            Float successPercentage = Float.valueOf((successCount * 100) / total);
            if(successPercentage > 0) {
                SliceValue successValue = new SliceValue(successPercentage, successColor);
                successValue.setLabel("S");
                chartValues.add(successValue);
            }
            Float failedPercentage = Float.valueOf((failedCount * 100) / total);
            if(failedPercentage > 0) {
                SliceValue failedValue = new SliceValue(failedPercentage, failedColor);
                failedValue.setLabel("F");
                chartValues.add(failedValue);
            }

            Float pendingPercentage = Float.valueOf((pendingCount * 100) / total);
            if(pendingPercentage > 0) {
                SliceValue pendingValue = new SliceValue(pendingPercentage, pendingColor);
                pendingValue.setLabel("P");
                chartValues.add(pendingValue);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        mPieChartData = new PieChartData(chartValues);
        mPieChartData.setHasCenterCircle(true);
        mPieChartData.setHasLabels(true);

        mPieChartData.setCenterText1Color(Color.WHITE);
        mPieChartData.setCenterText2Color(Color.WHITE);
        mPieChartData.setCenterText1(total + "");
        mPieChartData.setCenterText2("Total Jobs");

        mPieChart.setPieChartData(mPieChartData);
    }

    /**
     * Disable sequence layout
     */
    private void disableSequenceLayout() {
        mSequenceLayout.setEnabled(false);
        mSequenceLayout.setAlpha(0.5F);
    }

    /**
     * Enable sequence layout
     */
    private void enableSequenceLayout() {
        mSequenceLayout.setEnabled(true);
        mSequenceLayout.setAlpha(1.0F);
    }

    /**
     * Disable and dim start layout
     */
    private void disableStartLayout() {
        mStartLayout.setEnabled(false);
        mStartLayout.setAlpha(0.5F);
    }

    /**
     * Enable sequence layout
     */
    private void enableStartLayout() {
        mStartLayout.setEnabled(true);
        mStartLayout.setAlpha(1.0F);
    }

    /**
     * Disable and dim start layout
     */
    private void disableSyncLayout() {
        mSyncImage.startAnimation(AnimationUtils.loadAnimation(HomeActivity.this, R.anim.rotate));
        mSyncLayout.setEnabled(false);
//        mSyncLayout.setAlpha(0.5F);
        mSyncLayout.setBackgroundColor(getResources().getColor(R.color.app_color_a));
    }

    /**
     * Enable sequence layout
     */
    private void enableSyncLayout() {
        mSyncImage.clearAnimation();
        mSyncLayout.setEnabled(true);
//        mSyncLayout.setAlpha(1.0F);
        mSyncLayout.setBackgroundColor(getResources().getColor(R.color.app_color));
    }

    /**
     * Go to login screen
     */
    private void goToLoginScreen() {
        Intent loginIntent = new Intent(HomeActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }

    /**
     * Go to login screen
     */
    private void goToSequenceScreen() {
        Intent sequenceIntent = new Intent(HomeActivity.this, SequenceActivity.class);
        startActivity(sequenceIntent);
    }


    public static final int REQUEST_SIGNATURE_CODE = 3;
    /**
     * Go to DRS List screen
     */
    private void goToDRSScreen() {
        Log.i("TEST", "GO TO DRS SCREEN");
        if(false){
            Intent signatureIntent = new Intent(HomeActivity.this, SignatureActivity.class);
            startActivityForResult(signatureIntent, REQUEST_SIGNATURE_CODE);
        }else{

            Intent sequenceIntent = new Intent(HomeActivity.this, DRSActivity.class);

//            Intent sequenceIntent = new Intent(HomeActivity.this, DRSActivity.class);
            startActivity(sequenceIntent);
        }

    }

    private void fetchDSRList(){
        final String boyId = Utility.getFromSharedPrefs(HomeActivity.this, "BOY_ID");
        if(boyId!=null&&boyId.length()>0){
            RequestQueue queue = Volley.newRequestQueue(this);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://c2p.gojavas.net/DataSyncWebApi.asmx/GetDRSData",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            XmlPullParserFactory pullParserFactory;
                            try {
                                pullParserFactory = XmlPullParserFactory.newInstance();
                                XmlPullParser parser = pullParserFactory.newPullParser();
                                InputStream in_s = new ByteArrayInputStream(response.getBytes("UTF-8"));
                                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                                parser.setInput(in_s, null);
                                drsEntities = parseXML(parser);
                                if(drsEntities!=null&&drsEntities.size()>0){
                                    if(mPendingCountTextView!=null){
                                        mPendingCountTextView.setText("" + drsEntities.size());
//                                        populateNewChart(drsEntities.size());
                                    }
                                }
                            } catch (XmlPullParserException e) {
                                finish();
                                e.printStackTrace();
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                finish();
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    finish();
                }
            }){

                @Override
                protected Map<String,String> getParams(){
                    Map<String,String> params = new HashMap<String, String>();
                    params.put("BoyID",boyId);
                    return params;
                }
            };
            queue.add(stringRequest);
        }else {
            finish();
        }
    }

    private void populateNewChart(int pendingCountValue) {
        List<SliceValue> chartValues = new ArrayList<SliceValue>();
        Resources resources = getResources();
        int successColor = resources.getColor(R.color.success);
        int failedColor = resources.getColor(R.color.failed);
        int pendingColor = resources.getColor(R.color.pending);

        int pendingCount = pendingCountValue;
        int successCount = 0;
        int failedCount = 0;
        int total = pendingCount + successCount + failedCount;

        mSuccessCountTextView.setText(successCount + "");
        mFailedCountTextView.setText(failedCount + "");
        mPendingCountTextView.setText(pendingCount + "");

        try {
            Float successPercentage = Float.valueOf((successCount * 100) / total);
            if(successPercentage > 0) {
                SliceValue successValue = new SliceValue(successPercentage, successColor);
                successValue.setLabel("S");
                chartValues.add(successValue);
            }
            Float failedPercentage = Float.valueOf((failedCount * 100) / total);
            if(failedPercentage > 0) {
                SliceValue failedValue = new SliceValue(failedPercentage, failedColor);
                failedValue.setLabel("F");
                chartValues.add(failedValue);
            }

            Float pendingPercentage = Float.valueOf((pendingCount * 100) / total);
            if(pendingPercentage > 0) {
                SliceValue pendingValue = new SliceValue(pendingPercentage, pendingColor);
                pendingValue.setLabel("P");
                chartValues.add(pendingValue);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        mPieChartData = new PieChartData(chartValues);
        mPieChartData.setHasCenterCircle(true);
        mPieChartData.setHasLabels(true);

        mPieChartData.setCenterText1Color(Color.WHITE);
        mPieChartData.setCenterText2Color(Color.WHITE);
        mPieChartData.setCenterText1(total + "");
        mPieChartData.setCenterText2("Total Jobs");

        mPieChart.setPieChartData(mPieChartData);
    }

    private ArrayList<DrsEntity> parseXML(XmlPullParser parser) throws XmlPullParserException,IOException
    {
        int eventType = parser.getEventType();
        ArrayList<DrsEntity> dsrs = null;
        DrsEntity dsr = null;
        while (eventType != XmlPullParser.END_DOCUMENT){
            String name = null;
            switch (eventType){
                case XmlPullParser.START_DOCUMENT:
                    dsrs = new ArrayList();
                    break;
                case XmlPullParser.START_TAG:
                    name = parser.getName();
                    if (name!=null && name.equalsIgnoreCase("DRS")){
                        dsr = new DrsEntity();
                    } else if (dsr != null){
                        if (name.equalsIgnoreCase("DOCKETNO")){
                            dsr.setdocketno(parser.nextText());
                        }else if (name.equalsIgnoreCase("DRSNO")){
                            dsr.setdrsno(parser.nextText());
                        } else if (name.equalsIgnoreCase("CSGENM")){
                            dsr.setcsgenm(parser.nextText());
                        } else if (name.equalsIgnoreCase("CSGEADDR")){
                            dsr.setcsgeaddr(parser.nextText());
                        } else if (name.equalsIgnoreCase("ADDRESS_TYPE")){
                            dsr.setaddress_type(parser.nextText());
                        } else if (name.equalsIgnoreCase("CSGETELENO")){
                            dsr.setcsgeteleno(parser.nextText());
                        } else if (name.equalsIgnoreCase("ALTERNATE_NUMBER")){
                            dsr.setalternate_number(parser.nextText());
                        }else if (name.equalsIgnoreCase("CSGECITY")){
                            dsr.setcsgecity(parser.nextText());
                        }else if (name.equalsIgnoreCase("CSGEPINCODE")){
                            dsr.setcsgepincode(parser.nextText());
                        }else if (name.equalsIgnoreCase("COD_DOD")){
                            dsr.setcod_dod(parser.nextText());
                        }else if (name.equalsIgnoreCase("COD_AMT")){
                            dsr.setcod_amt(parser.nextText());
                        }else if (name.equalsIgnoreCase("DRIVER_NAME")){
                            dsr.setdriver_name(parser.nextText());
                        }else if (name.equalsIgnoreCase("USERID")){
                            dsr.setuserid(parser.nextText());
                        }else if (name.equalsIgnoreCase("CLIENT_CODE")){
                            dsr.setclient_code(parser.nextText());
                        }else if (name.equalsIgnoreCase("CLIENT_NAME")){
                            dsr.setclient_name(parser.nextText());
                        }else if (name.equalsIgnoreCase("PRODCD")){
                            dsr.setprodcd(parser.nextText());
                        }else if (name.equalsIgnoreCase("AMOUNT_TO_CUTOMER")){
                            dsr.setamount_to_cutomer(parser.nextText());
                        }else if (name.equalsIgnoreCase("ENTRYDATE")){
                            dsr.setentrydate(parser.nextText());
                        }else if (name.equalsIgnoreCase("ENTRYBY")){
                            dsr.setentryby(parser.nextText());
                        }else if (name.equalsIgnoreCase("DATE")){
                            dsr.setdate(parser.nextText());
                        }else if (name.equalsIgnoreCase("CONTACT_PERSON")){
                            dsr.setcontact_person(parser.nextText());
                        }else if (name.equalsIgnoreCase("DRIVER_ID")){
                            dsr.setdriver_id(parser.nextText());
                        }

                    }
                    break;
                case XmlPullParser.END_TAG:
                    name = parser.getName();
                    if (name.equalsIgnoreCase("DRS") && dsr != null){
                        dsrs.add(dsr);
                    }
            }
            eventType = parser.next();
        }
        return dsrs;
    }

    /**
     * Go to tab screen
     */
    private void goToTabScreen() {
        Intent loginIntent = new Intent(HomeActivity.this, TabActivity.class);
        startActivity(loginIntent);
    }

    /**
     * Show logout alert dialog
     */
    private void showLogoutAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        builder.setMessage(Constants.LOGOUT_MESSAGE);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Delete database
                getApplicationContext().deleteDatabase(DatabaseHelper.DATABASE_NAME);
                // Cancel all the pending requests
                TaskForceApplication.getInstance().cancelPendingRequests();
                // Clear all the application data
                TaskForceApplication.getInstance().clearApplicationData();
                // Restart activity
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Show wrong date alert
     */
    private void showDateDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
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

    @Override
    public void onError(String error, int errorCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        builder.setTitle("Mswipe Error");
        builder.setMessage(error);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onMswipeAppInstalled() {

    }

    @Override
    public void onMswipeAppUpdated() {

    }
}
