package com.gojavas.taskforce.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.gojavas.taskforce.R;
import com.gojavas.taskforce.adapter.SequenceDocketListAdapter;
import com.gojavas.taskforce.database.DrsHelper;
import com.gojavas.taskforce.entity.DrsEntity;
import com.gojavas.taskforce.entity.JobSetting;
import com.gojavas.taskforce.utils.Constants;
import com.gojavas.taskforce.utils.Utility;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Lenovo on 10/23/2016.
 */
public class ManualAWS extends AppCompatActivity implements View.OnClickListener{

    private ListView mDocketListView,mPendingListView,mBulkListView;
    private TextView mPendingTextView, mSequencedTextView,mTabType;
    private Button mSacnAWBNumber, mBarCodeScan,mGenerateDrs,  mValidate, mDoneButton,mDRSTABDRS,mDRSTABBUIK,mDRSTABPENDING,mDRSNonDeleveryBtn,mDRSDeleveryBtn;
    private LinearLayout mBulkListControl;
    EditText mScanAwbNumber;
    ListView awbNumberList;
    String[] ListElements = new String[] {
    };
//    drs_tab_pending

    private SequenceDocketListAdapter mSequenceDocketListAdapter;
    private ArrayList<DrsEntity> mDocketList = new ArrayList<>();
    private ArrayList<DrsEntity> mSequencedDocketList = new ArrayList<>();
    private ArrayList<JobSetting> mJobSettingList = new ArrayList<>();
    private int mTotalCount, mPendingCount, mSequencedCount;
    ProgressDialog progressDialog;
    ArrayList<DrsEntity> drsEntities;
    List< String > ListElementsArrayList;
    ArrayAdapter< String > awbAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_aws);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("DRS LIST");
        awbNumberList = (ListView) findViewById(R.id.awb_list);
        mBarCodeScan = (Button) findViewById(R.id.barCodeScan);
        mGenerateDrs = (Button) findViewById(R.id.generate_drs);
        mValidate = (Button) findViewById(R.id.validate);
        mScanAwbNumber = (EditText) findViewById(R.id.scan_awb_number);
        mBarCodeScan.setOnClickListener(this);
        mGenerateDrs.setOnClickListener(this);
        mValidate.setOnClickListener(this);
        runTimeCameraPermissionCheck();
        ListElementsArrayList = new ArrayList < String >
                (Arrays.asList(ListElements));

        awbAdapter = new ArrayAdapter < String >
                (ManualAWS.this, android.R.layout.simple_list_item_1,
                        ListElementsArrayList);

        awbNumberList.setAdapter(awbAdapter);

//        Addbutton.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//
//                ListElementsArrayList.add(GetValue.getText().toString());
//                adapter.notifyDataSetChanged();
//            }
//        });

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

    private String parseValidateXML(XmlPullParser parser) throws XmlPullParserException,IOException
    {
        int eventType = parser.getEventType();
        String result = null;
        while (eventType != XmlPullParser.END_DOCUMENT){
            String name = null;
            switch (eventType){
                case XmlPullParser.START_DOCUMENT:
                    break;
                case XmlPullParser.START_TAG:
                    name = parser.getName();
                    if (name!=null && name.equalsIgnoreCase("string")){
                        result = parser.nextText();
                    }else if (name!=null && name.equalsIgnoreCase("ValidateManualAWBResult")){
                        result = parser.nextText();
                    }
                    break;
                case XmlPullParser.END_TAG:

            }
            eventType = parser.next();
        }
        return result;
    }

    private String parseXML(XmlPullParser parser) throws XmlPullParserException,IOException
    {
        int eventType = parser.getEventType();
        String result = null;
        while (eventType != XmlPullParser.END_DOCUMENT){
            String name = null;
            switch (eventType){
                case XmlPullParser.START_DOCUMENT:
                    break;
                case XmlPullParser.START_TAG:
                    name = parser.getName();
                    if (name!=null && name.equalsIgnoreCase("string")){
                        result = parser.nextText();
                    }else if (name!=null && name.equalsIgnoreCase("PostDRSDataResult")){
                        result = parser.nextText();
                    }
                    break;
                case XmlPullParser.END_TAG:

            }
            eventType = parser.next();
        }
        return result;

    }

    private void validate(){
        final String boyId = Utility.getFromSharedPrefs(ManualAWS.this, "BOY_ID");
        final String scanedAwbNumber = mScanAwbNumber.getText().toString();
        if (scanedAwbNumber == null || scanedAwbNumber.length() <= 0){
            Utility.showToast(ManualAWS.this, "Please enter/scan AWB number");
            return;
        }
        if(boyId!=null&&boyId.length()>0){

            showProgrss("Please Wait...");
            RequestQueue queue = Volley.newRequestQueue(this);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://c2p.gojavas.net/DataSyncWebApi.asmx/ValidateManualAWB",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            hideProgrss();
                            XmlPullParserFactory pullParserFactory;
                            try {
                                pullParserFactory = XmlPullParserFactory.newInstance();
                                XmlPullParser parser = pullParserFactory.newPullParser();
                                InputStream in_s = new ByteArrayInputStream(response.getBytes("UTF-8"));
                                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                                parser.setInput(in_s, null);
                                String result = parseValidateXML(parser);
                                if (result != null && result.length() > 0) {
                                    if (result.equalsIgnoreCase("yes") || result.equalsIgnoreCase("true") || result.equalsIgnoreCase("SUCCESS")) {
                                        ListElementsArrayList.add(scanedAwbNumber);
                                        awbAdapter.notifyDataSetChanged();
                                        Utility.showToast(ManualAWS.this, "Validated");
                                    } else {
                                        Utility.showToast(ManualAWS.this, result);
                                    }
                                } else {
                                    Utility.showToast(ManualAWS.this, Constants.UNKNOWN_ERROR_MESSAGE);
                                }
                            } catch (XmlPullParserException e) {
                                Utility.showToast(ManualAWS.this, Constants.UNKNOWN_ERROR_MESSAGE);
                                e.printStackTrace();
                            } catch (IOException e) {
                                Utility.showToast(ManualAWS.this, Constants.UNKNOWN_ERROR_MESSAGE);
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    hideProgrss();
                    Utility.showToast(ManualAWS.this, Constants.UNKNOWN_ERROR_MESSAGE + ". Please try after some time.");
                }
            }){
                @Override
                protected Map<String,String> getParams(){
                    Map<String,String> params = new HashMap<String, String>();
                    params.put("BoyID",boyId);
                    params.put("UserID",Utility.getFromSharedPrefs(ManualAWS.this, "USER_ID"));
                    params.put("AWBNo",scanedAwbNumber);
                    return params;
                }
            };
            queue.add(stringRequest);
        }else{
            finish();
            Utility.showToast(ManualAWS.this, Constants.UNKNOWN_ERROR_MESSAGE + ". Please try after some time.");
        }
    }

    public void runTimeCameraPermissionCheck(){
        if (ContextCompat.checkSelfPermission(ManualAWS.this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(ManualAWS.this, Manifest.permission.CAMERA)) {


            } else {
                ActivityCompat.requestPermissions(ManualAWS.this,
                        new String[]{Manifest.permission.CAMERA},
                        10002);
            }

        }

    }
    private void PostDRSData(){
        final String boyId = Utility.getFromSharedPrefs(ManualAWS.this, "BOY_ID");
        final String scanedAwbNumber = TextUtils.join(", ", ListElementsArrayList);
        if (scanedAwbNumber == null || scanedAwbNumber.length() <= 0){
            Utility.showToast(ManualAWS.this, "No Validated AWB numbers found.");
            return;
        }
        if(boyId!=null&&boyId.length()>0){
            showProgrss("Please Wait...");
            RequestQueue queue = Volley.newRequestQueue(this);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://c2p.gojavas.net/DataSyncWebApi.asmx/PostDRSData",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            hideProgrss();
                            XmlPullParserFactory pullParserFactory;
                            try {
                                pullParserFactory = XmlPullParserFactory.newInstance();
                                XmlPullParser parser = pullParserFactory.newPullParser();
                                InputStream in_s = new ByteArrayInputStream(response.getBytes("UTF-8"));
                                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                                parser.setInput(in_s, null);
                                String result = parseXML(parser);
                                if (result != null && result.length() > 0) {
                                    if (result.equalsIgnoreCase("yes") || result.equalsIgnoreCase("true") || result.equalsIgnoreCase("SUCCESS")) {
                                        Utility.showToast(ManualAWS.this, "DRS Created Successfully.");
                                    } else {
                                        Utility.showToast(ManualAWS.this, result);
                                    }
                                } else {
                                    Utility.showToast(ManualAWS.this, Constants.UNKNOWN_ERROR_MESSAGE);
                                }
                            } catch (XmlPullParserException e) {
                                Utility.showToast(ManualAWS.this, Constants.UNKNOWN_ERROR_MESSAGE);
                                e.printStackTrace();
                            } catch (IOException e) {
                                Utility.showToast(ManualAWS.this, Constants.UNKNOWN_ERROR_MESSAGE);
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    hideProgrss();
                    Utility.showToast(ManualAWS.this, Constants.UNKNOWN_ERROR_MESSAGE + ". Please try after some time.");
                }
            }){
                @Override
                protected Map<String,String> getParams(){
                    Map<String,String> params = new HashMap<String, String>();
                    params.put("BoyID",boyId);
                    params.put("UserID",Utility.getFromSharedPrefs(ManualAWS.this, "USER_ID"));
                    params.put("ListOfAWB",scanedAwbNumber);
                    return params;
                }
            };
            queue.add(stringRequest);
        }else{
            finish();
            Utility.showToast(ManualAWS.this, Constants.UNKNOWN_ERROR_MESSAGE + ". Please try after some time.");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        progressDialog = new ProgressDialog(ManualAWS.this);
//        registerViews();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.barCodeScan:
                new IntentIntegrator(this).initiateScan();
                break;
            case R.id.validate:
                validate();
                break;
            case R.id.generate_drs:
                PostDRSData();
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        switch (requestCode) {
            case IntentIntegrator.REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
                    String scannedId = result.getContents();
                    if(scannedId!=null&&scannedId.length()>0){
                        mScanAwbNumber.setText(scannedId);
                    }else{
                        Utility.showNormalAlertDialog(ManualAWS.this, "Scan Result","No Code Found");
                    }

                }
                break;
        }
    }



    private void codDialogBox(final float _totalCODAmount){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ManualAWS.this);
        alertDialog.setTitle("COD Amount");
        alertDialog.setMessage("Received COD Amount");

        final EditText input = new EditText(ManualAWS.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        input.setLayoutParams(lp);
        input.setEnabled(false);
        input.setText(""+_totalCODAmount);
        alertDialog.setView(input);


        alertDialog.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String newCod = input.getText().toString();
                        if(newCod!= null&&newCod.trim().length()>0){
                            dialog.cancel();
                            startPassActivity(_totalCODAmount);
                        }else{
                            Utility.showToast(ManualAWS.this, "Enter valid COD value.");
                        }
                    }
                });

        alertDialog.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }

    private void startPassActivity(float totalcod){
        Intent intent = new Intent(ManualAWS.this, DRSPassActivity.class);
        SequenceDocketListAdapter _mBulkListAdapter = (SequenceDocketListAdapter)mBulkListView.getAdapter();
        ArrayList<String> addedForBulk = new ArrayList<String>();
        if(_mBulkListAdapter!=null && _mBulkListAdapter.getCount()>0) {
            for (int i = 0; i < _mBulkListAdapter.getCount(); i++) {
                DrsEntity _drsEntity = (DrsEntity) _mBulkListAdapter.getItem(i);
                if (_drsEntity.isCheckedForBulk()) {
//                    addedForBulk.add(_drsEntity.getdrsno());
                    addedForBulk.add(_drsEntity.getdocketno());
                }
            }
            if(addedForBulk!=null&&addedForBulk.size()>0){
                intent.putExtra("selectedDrsEntities", addedForBulk);
                intent.putExtra("Operation_Type", "Bulk");
                intent.putExtra("Total_COD", totalcod);
                startActivity(intent);
            }
        }

    }



    private void performDeliveredBulkOperation() {
        try{
            float totalAmount = 0;
            //Get bulk COD details
            SequenceDocketListAdapter _mBulkListAdapter = (SequenceDocketListAdapter)mBulkListView.getAdapter();
            ArrayList<DrsEntity> addedForBulk = new ArrayList<DrsEntity>();
            if(_mBulkListAdapter!=null && _mBulkListAdapter.getCount()>0) {
                for (int i = 0; i < _mBulkListAdapter.getCount(); i++) {
                    DrsEntity _drsEntity = (DrsEntity) _mBulkListAdapter.getItem(i);
                    if (_drsEntity.isCheckedForBulk()) {
                        addedForBulk.add(_drsEntity);
                    }
                }
            }
            if(addedForBulk!=null&&addedForBulk.size()>0){

                for(int i=0;i<addedForBulk.size();i++){
                    DrsEntity _drsEntity = addedForBulk.get(i);
                    if(_drsEntity.getcod_dod()!=null
                            &&_drsEntity.getcod_dod().equalsIgnoreCase("POSTPAID")
                            &&Float.valueOf(_drsEntity.getcod_amt())!=0) {
                        totalAmount = totalAmount + Float.valueOf(_drsEntity.getcod_amt());
                    }
                }
            }

            if(totalAmount!=0){
                codDialogBox(totalAmount);
            }else{
                startPassActivity(0);
            }
        }catch(Exception e){

        }
    }

    private void performNonDeliveredOperation() {
        Intent intent = new Intent(ManualAWS.this, DRSFailActivity.class);
        //get Array list of selected Items
        SequenceDocketListAdapter _mBulkListAdapter = (SequenceDocketListAdapter)mBulkListView.getAdapter();
        ArrayList<String> addedForBulk = new ArrayList<String>();
        if(_mBulkListAdapter!=null && _mBulkListAdapter.getCount()>0){
            for(int i =0;i<_mBulkListAdapter.getCount();i++){
                DrsEntity _drsEntity = (DrsEntity) _mBulkListAdapter.getItem(i);
                if(_drsEntity.isCheckedForBulk()){
//                    addedForBulk.add(_drsEntity.getdrsno().trim());
                    addedForBulk.add(_drsEntity.getdocketno().trim());

                }
            }

            if(addedForBulk!=null&&addedForBulk.size()>0){
                intent.putExtra("selectedDrsEntities", addedForBulk);
                intent.putExtra("Operation_Type", "Bulk");
                startActivity(intent);
            }

        }

//        startActivityForResult(intent, 2);

    }




    private void registerTabs(){
        mDRSTABDRS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTabType.setText("DRS List");
                mDocketListView.setVisibility(View.VISIBLE);
                mPendingListView.setVisibility(View.GONE);
                mBulkListView.setVisibility(View.GONE);
                mBulkListControl.setVisibility(View.GONE);

            }
        });
        mDRSTABBUIK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTabType.setText("Select DRS for Bulk Upload");
                mDocketListView.setVisibility(View.GONE);
                mBulkListControl.setVisibility(View.VISIBLE);
                mPendingListView.setVisibility(View.GONE);
                mBulkListView.setVisibility(View.VISIBLE);
            }
        });
        mDRSTABPENDING.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTabType.setText("Saved Bulk DRS List");
                mDocketListView.setVisibility(View.GONE);
                mBulkListControl.setVisibility(View.GONE);
                mPendingListView.setVisibility(View.VISIBLE);
                mBulkListView.setVisibility(View.GONE);
            }
        });
    }

    /**
     * Register listener on all the views
     */
    private void registerListeners() {
        mDoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int size = mSequencedDocketList.size();
                for(int i=0; i<size; i++) {
                    DrsEntity drsEntity = mSequencedDocketList.get(i);
                    String drs_docket = drsEntity.getdrs_docket();
                    DrsHelper.getInstance().updateDrsPosition(drs_docket, drsEntity.getposition());
                }
                finish();
            }
        });

        mDocketListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Check whether this docket is sequenced or not
                int pos = mDocketList.get(position).getposition();
                if(pos == 0) {
                    // Not sequenced
                    mDocketList.get(position).setposition(mTotalCount);
                    mTotalCount--;
                    mSequencedDocketList.add(mDocketList.get(position));
                    mSequenceDocketListAdapter.notifyDataSetChanged();
                    // Update pending and sequenced count
                    updateCount();
                    // If all the dockets are sequenced, then show done button
                    if(mDocketList.size() == mSequencedDocketList.size()) {
                        mDoneButton.setVisibility(View.VISIBLE);
                    }
                } else {
                    // Already sequenced
                    // Ask to re-sequence items
                    showResequenceDialog();
                }
            }
        });
    }

    /**
     * Update pending and sequenced item's count
     */
    private void updateCount() {
        mPendingCount = (mDocketList.size() - mSequencedDocketList.size());
        mSequencedCount = mSequencedDocketList.size();
        mPendingTextView.setText(mPendingCount + "");
        mSequencedTextView.setText(mSequencedCount + "");
    }

    /**
     * Show dialog to enter phone number
     */
    public void showResequenceDialog() {
        final Dialog dialog = new Dialog(ManualAWS.this, R.style.customDialogTheme);
        dialog.setContentView(R.layout.dialog_resequence);
        Button okButton = (Button) dialog.findViewById(R.id.dialog_ok_button);
        Button cancelButton = (Button) dialog.findViewById(R.id.dialog_cancel_button);

        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = Utility.getMetricsWidth(ManualAWS.this);
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDoneButton.setVisibility(View.GONE);
                int size = mDocketList.size();
                for(int i=0; i<size; i++) {
                    // Set the position of all dockets as 0
                    DrsEntity entity = mDocketList.get(i);
                    entity.setposition(0);
                    DrsHelper.getInstance().updateDrsPosition(entity.getdrs_docket(), 0);
                    // Clear sequence docket list
                    mSequencedDocketList.clear();
                }
                // Update pending and sequenced items count
                updateCount();
                // Refresh the list
                mSequenceDocketListAdapter.notifyDataSetChanged();
                // Close the dialog
                dialog.dismiss();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close the dialog
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
