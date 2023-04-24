package com.gojavas.taskforce.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ezetap.sdk.AppConstants;
import com.ezetap.sdk.EzetapPayApis;
import com.gojavas.taskforce.R;
import com.gojavas.taskforce.database.DrsHelper;
import com.gojavas.taskforce.database.ItemHelper;
import com.gojavas.taskforce.database.UserHelper;
import com.gojavas.taskforce.entity.DrsEntity;
import com.gojavas.taskforce.entity.ItemEntity;
import com.gojavas.taskforce.entity.NonDelivery;
import com.gojavas.taskforce.entity.Proof;
import com.gojavas.taskforce.entity.Relation;
import com.gojavas.taskforce.entity.UserEntity;
import com.gojavas.taskforce.manager.Compress;
import com.gojavas.taskforce.services.GPSTracker;
import com.gojavas.taskforce.ui.activity.TaskForceApplication;
import com.mswipe.wisepad.apkkit.WisePadController;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by GJS280 on 10/4/2015.
 */
public class Utility {

    public static String type = Constants.PERSONAL;

//    private static AmazonS3Client sS3Client;
//    private static CognitoCachingCredentialsProvider sCredentialProvider;
//    private static TransferUtility sTransferUtility;

    /**
     * Amazon
     * Gets an instance of CognitoCachingCredentialsProvider which is
     * constructed using the given Context.
     *
     * @param context An Context instance.
     * @return A default credential provider.
     */
//    private static CognitoCachingCredentialsProvider getCredentialProvider(Context context) {
//        if (sCredentialProvider == null) {
//            sCredentialProvider = new CognitoCachingCredentialsProvider(
//                    context.getApplicationContext(),
//                    Constants.COGNITO_POOL_ID,
//                    Regions.US_EAST_1);
//        }
//
//        return sCredentialProvider;
//    }

    /**
     * Amazon
     * Gets an instance of a S3 client which is constructed using the given
     * Context.
     *
     * @param context An Context instance.
     * @return A default S3 client.
     */
//    public static AmazonS3Client getS3Client(Context context) {
//        if (sS3Client == null) {
//            sS3Client = new AmazonS3Client(getCredentialProvider(context.getApplicationContext()));
//            // Set the region of your S3 bucket
//            sS3Client.setRegion(Region.getRegion(Regions.US_EAST_1));
//        }
//        return sS3Client;
//    }

    /**
     * Amazon
     * Gets an instance of the TransferUtility which is constructed using the
     * given Context
     *
     * @param context
     * @return a TransferUtility instance
     */
//    public static TransferUtility getTransferUtility(Context context) {
//        if (sTransferUtility == null) {
//            sTransferUtility = new TransferUtility(getS3Client(context.getApplicationContext()),
//                    context.getApplicationContext());
//        }
//
//        return sTransferUtility;
//    }

    /**
     * Save data in shared preferences
     * @param mContext
     * @param key
     * @param data
     */
    public static void saveToSharedPrefs(Context mContext, String key, String data) {
        final String PREFS_NAME = "com.gojavas.taskforce.preferences";
        final SharedPreferences taskForceData = mContext.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = taskForceData.edit();
        editor.putString(key, data);
        editor.commit();
    }


    public static String getDateFromMilliseconds(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        String date = DateFormat.format("dd-MM-yyyy HH:mm:ss", cal).toString();
        return date;
    }

    /**
     * Get data from shared preferences
     * @param mContext
     * @param key
     * @return saved value
     */
    public static String getFromSharedPrefs(Context mContext, String key) {
        final String PREFS_NAME = "com.gojavas.taskforce.preferences";
        final SharedPreferences taskForceData = mContext.getSharedPreferences(PREFS_NAME, 0);
        final String preData = taskForceData.getString(key, "");
        return preData;
    }

    /**
     * Delete data from shared preference based on key
     * @param mContext
     * @param key
     */

    public static void deleteFromSharedPrefs(Context mContext, String key){
        final String PREFS_NAME = "com.gojavas.taskforce.preferences";
        final SharedPreferences taskForceData = mContext.getSharedPreferences(PREFS_NAME, 0);
        taskForceData.edit().remove(key).commit();
    }


    /**
     * Hide keyboard
     * @param activity
     */
    public static void hideKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    /**
     * Get device id
     * @return
     */
    public static String getDeviceId() {
        Context context = TaskForceApplication.getInstance();
        TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
//        return telephonyManager.getDeviceId();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            return Settings.Secure.getString(context.getContentResolver(),Settings.Secure.ANDROID_ID);
        }else {
            return telephonyManager.getDeviceId();
        }
    }

    /**
     * Get screen width
     * @return
     */
    public static int getScreenWidth(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int screenWidth = (int) (displayMetrics.widthPixels / displayMetrics.density);
        return screenWidth;
    }

    /**
     * Get screen metrics width
     * @param activity
     * @return
     */
    public static int getMetricsWidth(Activity activity) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return  displayMetrics.widthPixels;
    }

    /**
     * Get screen height
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int screenHeight = (int) (displayMetrics.heightPixels / displayMetrics.density);
        return screenHeight;
    }

    /**
     * Get screen metrics height
     * @param activity
     * @return
     */
    public static int getMetricsHeight(Activity activity) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return  displayMetrics.heightPixels;
    }

    /**
     * Show toast message
     * @param mContext
     * @param string
     */
    public static void showToast(Context mContext, String string) {
        Toast toast = Toast.makeText(mContext, string, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.TOP, 10, 100);
        toast.show();
    }

    public static void showProgrss(String message,AlertDialog progressDialog) {
        if (progressDialog != null && !progressDialog.isShowing()) {
            progressDialog.show();
            progressDialog.setMessage(message);
            progressDialog.setCancelable(false);
        }
    }


    public static void hideProgrss(AlertDialog progressDialog) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    public static ArrayList<Proof> parseProof(XmlPullParser parser) throws XmlPullParserException,IOException
    {
        int eventType = parser.getEventType();
        ArrayList<Proof> dsrs = null;
        Proof dsr = null;
        while (eventType != XmlPullParser.END_DOCUMENT){
            String name = null;
            switch (eventType){
                case XmlPullParser.START_DOCUMENT:
                    dsrs = new ArrayList();
                    break;
                case XmlPullParser.START_TAG:
                    name = parser.getName();
                    if (name!=null && name.equalsIgnoreCase("Proof")){
                        dsr = new Proof();
                    } else if (dsr != null){
                        if (name.equalsIgnoreCase("ProofID")){
                            dsr.setProofID(parser.nextText());
                        } else if (name.equalsIgnoreCase("ProofName")){
                            dsr.setProofName(parser.nextText());
                        } else if (name.equalsIgnoreCase("ProofDetailRequired")){
                            dsr.setProofDetailRequired(parser.nextText());
                        }
                    }
                    break;
                case XmlPullParser.END_TAG:
                    name = parser.getName();
                    if (name.equalsIgnoreCase("Proof") && dsr != null){
                        dsrs.add(dsr);
                    }
            }
            eventType = parser.next();
        }
        return dsrs;
    }

    public static ArrayList<NonDelivery> parseNDRReason(XmlPullParser parser) throws XmlPullParserException,IOException
    {
        int eventType = parser.getEventType();
        ArrayList<NonDelivery> dsrs = null;
        NonDelivery dsr = null;
        while (eventType != XmlPullParser.END_DOCUMENT){
            String name = null;
            switch (eventType){
                case XmlPullParser.START_DOCUMENT:
                    dsrs = new ArrayList();
                    break;
                case XmlPullParser.START_TAG:
                    name = parser.getName();
                    if (name!=null && name.equalsIgnoreCase("NDRReasonMaster")){
                        dsr = new NonDelivery();
                    } else if (dsr != null){
                        if (name.equalsIgnoreCase("NDRReasonID")){
                            dsr.setNDRReasonID(parser.nextText());
                        } else if (name.equalsIgnoreCase("NDRCode")){
                            dsr.setNDRCode(parser.nextText());
                        } else if (name.equalsIgnoreCase("NDRReason")){
                            dsr.setNDRReason(parser.nextText());
                        }else if (name.equalsIgnoreCase("IsReversePickup")){
                            dsr.setIsReversePickup(parser.nextText());
                        }else if (name.equalsIgnoreCase("IsNDR")){
                            dsr.setIsNDR(parser.nextText());
                        }
                    }
                    break;
                case XmlPullParser.END_TAG:
                    name = parser.getName();
                    if (name.equalsIgnoreCase("NDRReasonMaster") && dsr != null){
                        dsrs.add(dsr);
                    }
            }
            eventType = parser.next();
        }
        return dsrs;
    }


    public static ArrayList<Relation> parseRelation(XmlPullParser parser) throws XmlPullParserException,IOException
    {
        int eventType = parser.getEventType();
        ArrayList<Relation> dsrs = null;
        Relation dsr = null;
        while (eventType != XmlPullParser.END_DOCUMENT){
            String name = null;
            switch (eventType){
                case XmlPullParser.START_DOCUMENT:
                    dsrs = new ArrayList();
                    break;
                case XmlPullParser.START_TAG:
                    name = parser.getName();
                    if (name!=null && name.equalsIgnoreCase("Relation")){
                        dsr = new Relation();
                    } else if (dsr != null){
                        if (name.equalsIgnoreCase("RelationID")){
                            dsr.setRelationID(parser.nextText());
                        } else if (name.equalsIgnoreCase("RelationName")){
                            dsr.setRelationName(parser.nextText());
                        }
                    }
                    break;
                case XmlPullParser.END_TAG:
                    name = parser.getName();
                    if (name.equalsIgnoreCase("Relation") && dsr != null){
                        dsrs.add(dsr);
                    }
            }
            eventType = parser.next();
        }
        return dsrs;
    }
    public static ArrayList<Relation> relationList;
    public static void getRelationList(final Context context){
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://c2p.gojavas.net/DataSyncWebApi.asmx/GetRelation",
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
                            relationList = parseRelation(parser);
                        } catch (XmlPullParserException e) {
//                            Utility.showToast(context, Constants.UNKNOWN_ERROR_MESSAGE);
                            e.printStackTrace();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
//                            Utility.showToast(context, Constants.UNKNOWN_ERROR_MESSAGE);
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Utility.showToast(context, Constants.UNKNOWN_ERROR_MESSAGE + "Please try after some time.");
            }
        });
        queue.add(stringRequest);
    }

    public static ArrayList<NonDelivery> reasonList;
    public static void getReasonList(final Context context){
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://c2p.gojavas.net/DataSyncWebApi.asmx/GetNDRReason",
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
                            reasonList = parseNDRReason(parser);
                        } catch (XmlPullParserException e) {
//                            Utility.showToast(context, Constants.UNKNOWN_ERROR_MESSAGE);
                            e.printStackTrace();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
//                            Utility.showToast(context, Constants.UNKNOWN_ERROR_MESSAGE);
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Utility.showToast(context, Constants.UNKNOWN_ERROR_MESSAGE + ". Please try after some time.");
            }
        });
        queue.add(stringRequest);
    }


    public static ArrayList<Proof> proofList;
    public static void getProofList(final Context context){
        boolean isLoadingScreenRequired = false;
        if(proofList!=null &&proofList.size()>0){
            return;
        }else{
//            isLoadingScreenRequired = true;
            final AlertDialog progressDialog = new ProgressDialog(context);
//            if(isLoadingScreenRequired){

//                showProgrss("Please Wait. Fetching Proof.",progressDialog);
//            }
            RequestQueue queue = Volley.newRequestQueue(context);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://c2p.gojavas.net/DataSyncWebApi.asmx/GetProof",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
//                            hideProgrss(progressDialog);
                            XmlPullParserFactory pullParserFactory;
                            try {
                                pullParserFactory = XmlPullParserFactory.newInstance();
                                XmlPullParser parser = pullParserFactory.newPullParser();
                                InputStream in_s = new ByteArrayInputStream(response.getBytes("UTF-8"));
                                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                                parser.setInput(in_s, null);
                                proofList = parseProof(parser);
                            } catch (XmlPullParserException e) {
//                                Utility.showToast(context, Constants.UNKNOWN_ERROR_MESSAGE);
                                e.printStackTrace();
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
//                                Utility.showToast(context, Constants.UNKNOWN_ERROR_MESSAGE);
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
//                    Utility.showToast(context, Constants.UNKNOWN_ERROR_MESSAGE + "Please try after some time.");
//                    hideProgrss(progressDialog);
                }
            });
            queue.add(stringRequest);
            return;
        }
//        return proofList;
    }


    /**
     * Show toast message
     * @param mContext
     * @param string
     */
    public static void showShortToast(Context mContext, String string) {
        Toast toast = Toast.makeText(mContext, string, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP, 10, 100);
        toast.show();
    }

    /**
     * Show alert dialog
     * @param context
     * @param title
     * @param message
     */
    public static void showAlertDialog(final Context context, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ((Activity) context).finish();
                dialog.dismiss();
            }
        });

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Show alert dialog
     * @param context
     * @param title
     * @param message
     */
    public static void showNormalAlertDialog(final Context context, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public static String getApplicationPath(String directory) {
        return Environment.getExternalStorageDirectory() + "/GoDelivery/Current/" + directory;
    }

    public static String getApplicationBackupPath() {
        String path =  Environment.getExternalStorageDirectory() + "/GoDelivery/Backup/";
        File file = new File(path);
        if(!file.exists()) {
            file.mkdirs();
        }
        return path;
    }

    /**
     * Check whether internet is connected or not
     * @param mContext
     * @return
     */
    public static boolean isInternetConnected(Context mContext) {
        final ConnectivityManager connection = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connection != null&& (connection.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED)|| (connection.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED)) {
            return true;
        }
        return false;
    }

    /**
     * Check whether device support camera
     * @return
     */
    private boolean isDeviceSupportCamera() {
        if (TaskForceApplication.getInstance().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    /**
     * Get resource id from name
     * @param resName
     * @return
     */
    public static int getResId(String resName) {
        try {
            Class res = R.drawable.class;
            Field field = res.getField(resName);
            return field.getInt(null);
        }
        catch (Exception e) {
            Log.e("MyTag", "Failure to get drawable id.", e);
            return -1;
        }
    }

    /**
     * Extract the sqlite database from device
     * @param context
     */
    public static void extractDatabaseFromDevice(Context context) {
        File sd = Environment.getExternalStorageDirectory();
        String DB_PATH;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            DB_PATH = context.getFilesDir().getAbsolutePath().replace("files", "databases") + File.separator;
        }
        else {
            DB_PATH = context.getFilesDir().getPath() + context.getPackageName() + "/databases/";
        }
        if (sd.canWrite()) {
            //TaskForce DB
            String currentDBPath = "TaskForce.db";
            String backupDBPath = "TaskForce_backup.db";
            File currentDB = new File(DB_PATH, currentDBPath);
            File backupDB = new File(sd, backupDBPath);

            if (currentDB.exists()) {
                try {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }


        String PATH;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            PATH = context.getFilesDir().getAbsolutePath() + File.separator;
        }
        else {
            PATH = context.getFilesDir().getPath() + context.getPackageName() + "/files/";
        }
        if (sd.canWrite()) {
            //TaskForce DB
            String currentPath = "Design.txt";
            String backupPath = "Design_backup.txt";
            File current = new File(PATH, currentPath);
            File backup = new File(sd, backupPath);

            if (current.exists()) {
                try {
                    FileChannel src = new FileInputStream(current).getChannel();
                    FileChannel dst = new FileOutputStream(backup).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Call design api and save design in phone
     */
    public static void callDesignApi(Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
//        JSONObject request = new JSONObject();
//        try {
//            request.put("job_id", "gojavas");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

        // Response listener
        if(responseListener == null) {
            responseListener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    System.out.println("Design downloaded");
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
                }
            };
        }
        if(errorListener == null) {
            errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    volleyError.printStackTrace();
                }
            };
        }
        String url = Constants.DESIGN_URL + "?job_id=gojavas";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url/*Constants.DESIGN_URL*/, null, responseListener, errorListener);

        TaskForceApplication.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    /**
     * Get design from storage
     * @return
     */
    public static String getDesign() {
        FileInputStream fis = null;
        StringBuilder sb = new StringBuilder();
        try {
            fis = TaskForceApplication.getInstance().openFileInput("Design.txt");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            fis.close();
            System.out.println("Design found");
        } catch (FileNotFoundException e) {
            // Get design from server
            System.out.println("Design not found");
            callDesignApi(null, null);
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    /**
     * Get design from file saved in assets
     * @return
     */
    public static String loadDesign() {
        String countryJsonString = null;
        try {
            InputStream is = TaskForceApplication.getInstance().getAssets().open("Design.txt");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            countryJsonString = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return countryJsonString;
    }

    /**
     * Get current date in string
     * @return
     */
    public static String getCurrentDate() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat(Constants.DATE_FORMAT);
        String formattedDate = df.format(c.getTime());
        return formattedDate;
    }

    /**
     * Get current date in string
     * @return
     */
    public static String getCurrentPullDate() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat(Constants.PULL_DATE_FORMAT);
        String formattedDate = df.format(c.getTime());
        if(formattedDate.startsWith("0")) {
            formattedDate = formattedDate.substring(1);
        }
        return formattedDate;
    }

    /**
     * Get current date in string
     * @return
     */
    public static String getDeliveryTime() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat(Constants.DELIVERY_DATE_FORMAT);
        String formattedDate = df.format(c.getTime());
        return formattedDate;
    }

    /**
     * Convert string into date
     * @param dateString
     * @return
     */
    public static Date convertStringToDate(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT);
        Date date = null;
        try {
            date = dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    // Check dates are equal or not
    public static boolean areEqual(long logintime, long currentTimeMillis) {
        try {
            Calendar c1 = Calendar.getInstance();
            c1.setTime(new Date(logintime));

            Calendar c2 = Calendar.getInstance();
            c2.setTime(new Date(currentTimeMillis));

            return ((c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR))
                    &&
                    (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)));

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Copy bitmap to a location
     * @param bitmap
     * @param destinationPath
     * @param fileName
     * @return
     */
    public static boolean copyBitmap(Bitmap bitmap, String destinationPath, String fileName) {
        boolean isok = true;
        FileOutputStream fos = null;
        File dir = new File(destinationPath);
        if(!dir.exists())
            dir.mkdirs();
        File file = new File (dir, fileName);
        if (file.exists ()) file.delete ();
        try {
            fos = new FileOutputStream(file);
            bitmap.setHasAlpha(true);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            isok = false;
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (Exception e) {
                isok = false;
                e.printStackTrace();
            }
        }
        return isok;
    }

    /**
     * Send sms to customer
     * @param phoneNo
     * @param sms
     * @param mContext
     */
    public static void sendSms(String phoneNo, String sms, Context mContext) {
        SmsManager smsManager = SmsManager.getDefault();
        ArrayList<String> parts = smsManager.divideMessage(sms);
        smsManager.sendMultipartTextMessage(phoneNo, null, parts, null, null);

//        CallLogEntity callLogEntity = new CallLogEntity();
//        callLogEntity.setusername(Utility.getFromSharedPrefs(mContext, Constants.USERNAME_KEY));
//        callLogEntity.setname(phoneNo);
//        callLogEntity.setnumber(phoneNo);
//        callLogEntity.setduration("0");
//        callLogEntity.setsync("0");
//        callLogEntity.settype(Constants.PROFESSIONAL);
//        callLogEntity.setdate(System.currentTimeMillis() + "");
//        callLogEntity.setcall_sms(Constants.SMS);
//
//        CallLogHelper.getInstance().insertCallLog(callLogEntity);
    }

    /**
     * Make phone call
     * @param phoneNo
     */
    public static void phoneCall(String phoneNo) {
        type = Constants.PROFESSIONAL;
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + phoneNo));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        TaskForceApplication.getInstance().startActivity(intent);
    }

    /**
     * Payment via Ezetap
     * @param activity
     * @param orderId
     * @param amount
     * @param mobileNumber
     */
    public static void paymentEzetap(Activity activity, String orderId, String amount, String mobileNumber) {
        String ezetapUsername = Utility.getFromSharedPrefs(TaskForceApplication.getInstance(), Constants.EZETAP_USERNAME_KEY);
        EzetapPayApis.create(EzetapConstants.API_CONFIG).startCardPayment(activity, AppConstants.REQ_CODE_PAY_CARD, ezetapUsername, Double.parseDouble(amount), orderId, 0, mobileNumber, null);
    }

    /**
     * Payment via Mswipe
     * @param mWisePadController
     */
    public static void paymentMswipe(String referenceId, String sessionTokeniser, WisePadController mWisePadController, String orderId, String amount, String mobileNumber) {
        mWisePadController.processCardSale(
                referenceId,
                sessionTokeniser,
                amount, // Amount
                mobileNumber, // Phone no
                orderId, // Order id
                "", // Email
                "", // Notes
                "", // Extra 1
                "", // Extra 2
                "", // Extra 3
                false,
                MswipeConstants.IS_PRODUCTION,
                WisePadController.ORIENTATION.PORTRAIT);
    }

    /**
     * Show login dialog
     */
    public static void showMswipeLoginDialog(final Context context, final WisePadController mWisePadController) {
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(10, 10, 10, 10);

        final EditText usernameEditText = new EditText(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        usernameEditText.setLayoutParams(params);
        usernameEditText.setHint("Username");
        usernameEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        final EditText passwordEditText = new EditText(context);
        passwordEditText.setLayoutParams(params);
        passwordEditText.setHint("Password");

        linearLayout.addView(usernameEditText);
        linearLayout.addView(passwordEditText);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Login");
        builder.setView(linearLayout);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String mswipeUsername = usernameEditText.getText().toString().trim();
                String mswipePassword = passwordEditText.getText().toString().trim();
                if(mswipeUsername == null || mswipeUsername.isEmpty() || mswipePassword == null || mswipePassword.isEmpty()) {
                    Utility.showToast(context, "Please fill required fields");
                } else {
                    mWisePadController.login(
                            mswipeUsername,
                            mswipePassword,
                            MswipeConstants.IS_PRODUCTION,
                            WisePadController.ORIENTATION.PORTRAIT);
                    dialog.dismiss();
                }
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
     * Show dialog
     */
    public static void showDialog(Context context, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Get current latitude and longitude
     * @param context
     * @return
     */
    public static HashMap<String,String> getLocation(Context context){

        GPSTracker gps = new GPSTracker(context);
        HashMap<String,String> map=new HashMap<>();

        // check if GPS enablred
        if(gps.canGetLocation()) {
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            String network_type=gps.getNetworkType();


            map.put(Constants.LATITUDE, latitude + "");
            map.put(Constants.LONGITUDE,longitude+"");
            map.put(Constants.PROVIDER,network_type);

            // \n is for new line
            // Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert(context);
        }
        return map;
    }


    public static HashMap<String,String> getLocationScheduler(Context context){

        GPSTracker gps = new GPSTracker(context);
        HashMap<String,String> map=new HashMap<>();

        // check if GPS enablred
        if(gps.canGetLocation()) {
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            String network_type=gps.getNetworkType();


            map.put(Constants.LATITUDE, latitude + "");
            map.put(Constants.LONGITUDE,longitude+"");
            map.put(Constants.PROVIDER,network_type);

            // \n is for new line
            // Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
        }
        return map;
    }


    /**
     * Get the actual bitmap from path
     * @return
     */
    public static Bitmap getBitmap() {
        File image = new File("/storage/emulated/0/DCIM/Screenshots/Screenshot_2015-05-21-01-39-53.png");
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);
        return  bitmap;
    }

    public static void saveBitmap() {
        Bitmap bitmap = getBitmap();
        FileOutputStream fos = null;
        try {
            fos = TaskForceApplication.getInstance().openFileOutput("screenshot.png", Context.MODE_PRIVATE);
//            fos.write(jsonObject.toString().getBytes());
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void getBitmapFromData() {
        File sd = Environment.getExternalStorageDirectory();
        Context context = TaskForceApplication.getInstance();
        String PATH;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            PATH = context.getFilesDir().getAbsolutePath() + File.separator;
        }
        else {
            PATH = context.getFilesDir().getPath() + context.getPackageName() + "/files/";
        }
        if (sd.canWrite()) {
            //TaskForce DB
            String currentPath = "screenshot.png";
            String backupPath = "screenshot_backup.png";
            File current = new File(PATH, currentPath);
            File backup = new File(sd, backupPath);

            if (current.exists()) {
                try {
                    FileChannel src = new FileInputStream(current).getChannel();
                    FileChannel dst = new FileOutputStream(backup).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Decode bitmap and scale it to required size
     * @param file
     * @return
     */
    public static Bitmap decodeFile(File file) {
        Context context = TaskForceApplication.getInstance();
        int mSize = getScreenWidth(context) / 2;
        try {
            // Decode image size
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(file), null, options);

            // Find the correct scale value. It should be the power of 2.
            int width_tmp = options.outWidth, height_tmp = options.outHeight;
            int scale = 1;
            while(true) {
                if(width_tmp/2 < mSize || height_tmp/2 < mSize)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            // Decode with inSampleSize
            BitmapFactory.Options options2 = new BitmapFactory.Options();
            options2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(file), null, options2);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get file name from its path
     * @param filePath
     * @return
     */
    public static String getFileNameFromPath(String filePath) {
        String name = "";
        if(filePath.contains("/")) {
            name = filePath.substring(filePath.lastIndexOf("/")+1, filePath.length());
        }
        return name;
    }

    /**
     * Get sms text
     * @param OrderNo
     * @param clientName
     * @return
     */
    public static String smsText(String jobType, String OrderNo, String clientName){
        String message = "Hi, I am waiting at your address to deliver " + OrderNo + " Ordered with " + clientName +
                ". I may have to leave in 10 mins to serve the next customer. Kindly accept your order :-Team gojavas";
        switch (jobType) {
            case Constants.JOB_TYPE_PICKUP:
            case Constants.JOB_TYPE_90_MINUTES:
                message = "Hi, I am waiting at your address to pick order# " + OrderNo +
                        ". I may have to leave in 10 mins to serve the next customer. Kindly return your shipment :-Team gojavas";
                break;
        }
        return message;
    }

    /**
     * Create backup zip
     */
    public static void createBackup() {
        Context context = TaskForceApplication.getInstance();
        String username = Utility.getFromSharedPrefs(context, Constants.USERNAME_KEY);
        String loginDate = Utility.getDate(Long.parseLong(Utility.getFromSharedPrefs(context, Constants.LOGGED_IN_TIME)));
        String zipPath = Utility.getApplicationBackupPath() + "backup_" + username + "_" + loginDate + ".zip";
        File zipFile = new File(zipPath);
        if(zipFile.exists()) {
            zipFile.delete();
        }
        Compress compress = new Compress();
        compress.zipFileAtPath(Utility.getApplicationPath(""), zipPath);
    }

    public static String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        String date = DateFormat.format("dd-MM-yyyy", cal).toString();
        return date;
    }

    /**
     * Get scaled bitmap from bytes array
     * @param data
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static Bitmap getScaledBitmap(byte[] data, int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(data, 0, data.length, options);
    }

    /**
     * Get scaled bitmap from file path
     * @param path
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static Bitmap getScaledBitmap(String path, int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }

    /**
     * Calculate bitmap insample size
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }
        }
        return inSampleSize;
    }

    /**
     * Get all the details of 90 minutes docket
     * @param requestId
     */
    public static void getNinetyMinuteDocket(final Context context, final String requestId) {
        JSONObject requestJson = new JSONObject();
        try {
            requestJson.put("request_id", requestId);
        } catch (JSONException je) {
            je.printStackTrace();
        }
        System.out.println("90 minute request: " + requestJson.toString());
        JsonObjectRequest ninetyMinuteRequest = new JsonObjectRequest(Request.Method.POST, Constants.NINETY_MINUTE_DETAIL_URL, requestJson, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                System.out.println("90 minute result: " + jsonObject.toString());
                try {
                    String success = jsonObject.getString(Constants.SUCCESS);
                    if(success.equalsIgnoreCase("1")) {
                        // Insert it in database
                        String docketNo = jsonObject.getString("job_request_id");
                        String clientName = jsonObject.getString("client_name");
                        String clientCode = jsonObject.getString("client_code");
                        String orderNumber = jsonObject.getString("order_number");
                        String customerName = jsonObject.getString("customer_name");
                        String address = jsonObject.getString("address");
                        String locality = jsonObject.getString("locality");
                        String city = jsonObject.getString("city");
                        String pinCode = jsonObject.getString("pincode");
                        String hub = jsonObject.getString("hub");
                        String contactNumber = jsonObject.getString("contact_number");
                        String alternateNumber = jsonObject.getString("alternate_number");
                        String productDescription = jsonObject.getString("product_description");
                        String reasonForReturn = jsonObject.getString("reason_for_return");
                        String returnRequestId = jsonObject.getString("return_request_id");
                        String tpCode = jsonObject.getString("tp_code");
                        String returnPincode = jsonObject.getString("return_pincode");
//                        String jobStatus = jsonObject.getString("job_status");
                        String jobCreationDate = jsonObject.getString("job_creation_date_time");
                        String drs_docket = returnRequestId + "_" + docketNo;

                        UserEntity user = UserHelper.getInstance().getUserCompleteDetail();
                        String driver_name = user.getfirstname();
                        String driver_id = user.getemp_code();
                        driver_id = driver_id.replace(Constants.EMP_CODE_SUFFIX, "");

                        DrsEntity drsEntity = new DrsEntity();
                        drsEntity.setjobtype(Constants.JOB_TYPE_90_MINUTES);
                        drsEntity.setdocketno(docketNo);
                        drsEntity.setdrsno(returnRequestId);
                        drsEntity.setdrs_docket(drs_docket);
                        drsEntity.setclient_name(clientName);
                        drsEntity.setclient_code(clientCode);
                        drsEntity.setdriver_name(driver_name);
                        drsEntity.setdriver_id(driver_id);
                        drsEntity.setordernumber(orderNumber);
                        drsEntity.setcsgenm(customerName);
                        drsEntity.setcsgeaddr(address + ", " + locality);
                        drsEntity.setcsgecity(city);
                        drsEntity.setcsgepincode(pinCode);
                        drsEntity.setreassign_destcd(hub);
                        drsEntity.setcsgeteleno(contactNumber);
                        drsEntity.setalternate_number(alternateNumber);
                        drsEntity.setproduct_description(productDescription);
                        drsEntity.setreason_for_return(reasonForReturn);
                        drsEntity.setreturn_request_id(returnRequestId);
                        drsEntity.settp_code(tpCode);
                        drsEntity.setreturn_pincode(returnPincode);
                        drsEntity.setstatus("");
                        drsEntity.setlogistic_dt(jobCreationDate);

                        DrsHelper.getInstance().insertOrUpdate(drsEntity);

                        // Insert item details
                        if(!ItemHelper.getInstance().allItemsPulled(drs_docket, "1")) {
                            ItemEntity itemEntity=new ItemEntity();
                            itemEntity.setsku_id("");
                            itemEntity.setsku_description(productDescription);
                            itemEntity.setStatus("");
                            itemEntity.setsku_cost("");
                            itemEntity.setdrs_no(returnRequestId);
                            itemEntity.setquantity("");
                            itemEntity.setsr(drs_docket);
                            itemEntity.setdocket_no(docketNo);
                            itemEntity.setdrs_docket(drs_docket);

                            ItemHelper.getInstance().insertOrUpdate(itemEntity);
                        }

                        Intent intent = new Intent(Constants.SCHEDULAR_SYNC_ACTION);
                        // You can also include some extra data.
                        intent.putExtra(Constants.SCHEDULAR_RESPONSE_KEY, Constants.SUCCESS_RESPONSE);
                        intent.putExtra(Constants.SCHEDULAR_RESPONSE_MESSAGE, "");
                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

                        // Show push notification
                        TaskForceApplication.getInstance().showNinetyMinutesNotification(requestId, "Done");
                    }
                } catch (JSONException e) {
                    System.out.println("90 minute error");
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
            }
        });
        TaskForceApplication.getInstance().addToRequestQueue(ninetyMinuteRequest);
    }

    // get current date and time for free recharge
    public static String getDateForFR(){

        // get current date and time
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyyHHmmss");
        String strDate = sdf.format(cal.getTime());
        System.out.println("Current date in String Format: "+strDate);

        return strDate;

    }
}
