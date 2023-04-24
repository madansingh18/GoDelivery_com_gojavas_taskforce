package com.gojavas.taskforce.ui.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.gojavas.taskforce.R;
import com.gojavas.taskforce.entity.DrsEntity;
import com.gojavas.taskforce.entity.NonDelivery;
import com.gojavas.taskforce.utils.Constants;
import com.gojavas.taskforce.utils.Utility;

import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.MarshalBase64;
import org.ksoap2.serialization.MarshalHashtable;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by MadanS on 10/23/2016.
 */
public class DRSFailActivity extends Activity implements View.OnClickListener {


    private Button delivered_fail_update_button,delivered_fail_update_save_button;
    private Button delivered_fail_update_cancel_button;
    private ProgressDialog progressDialog;
    private TextView non_delivery_another_date_label;
    private DatePicker non_delivery_another_date;
    private Spinner non_delivery_reason_spinner_id;
    private DrsEntity mDrsEntity;

    private boolean isBulkSubmission = false;
    private String UserID= "";
    private String BoyID= "";
    private String AWBNo= "";
    private String NonDeliveryDateTime= "";
    private String ReasonID = "";
    private String Latitude = "";
    private String Longitude = "";
    private String IsTesting = "false";
    private String nonDeliveryId = "";
    private ArrayList<String> selectedBulkDrsEntities;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drs_fail);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setTitle("DRS Detail");
        progressDialog = new ProgressDialog(DRSFailActivity.this);
        Intent i = getIntent();
//
        String operationType = i.getStringExtra("Operation_Type");
        if(operationType!=null&&operationType.length()>0&&operationType.equalsIgnoreCase("Bulk")){
            selectedBulkDrsEntities = (ArrayList<String>) getIntent().getSerializableExtra("selectedDrsEntities");
            AWBNo= "";
            if(selectedBulkDrsEntities!=null && selectedBulkDrsEntities.size()>0){
                String tmpdata = "";
                for(int t=0;t<selectedBulkDrsEntities.size();t++){
                    if(selectedBulkDrsEntities!=null&&selectedBulkDrsEntities.get(t)!=null&&selectedBulkDrsEntities.get(t).length()>0){
                        tmpdata = tmpdata + selectedBulkDrsEntities.get(t) +",";
                    }

                }
                if(tmpdata!=null&&tmpdata.length()>0){
                    tmpdata = tmpdata.substring(0,tmpdata.length()-1);
                    AWBNo= tmpdata;
                }

            }
            isBulkSubmission = true;

        }else{
            mDrsEntity = (DrsEntity)i.getSerializableExtra("selectedDrsEntities");
            if(mDrsEntity!=null){
                Log.i("TEST>", mDrsEntity.getdocketno());
                AWBNo= mDrsEntity.getdocketno();
            }
        }

        BoyID = Utility.getFromSharedPrefs(DRSFailActivity.this, "BOY_ID");
        UserID = Utility.getFromSharedPrefs(DRSFailActivity.this, "USER_ID");
        registerViews();
        registerListeners();
        if(Utility.reasonList!=null&&Utility.reasonList.size()>0){
            Log.i("DATA",Utility.reasonList.toString());
            ArrayAdapter<NonDelivery> adapter = new ArrayAdapter<NonDelivery>(DRSFailActivity.this,R.layout.spinner_textview, Utility.reasonList);
            non_delivery_reason_spinner_id.setAdapter(adapter);
            non_delivery_reason_spinner_id.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    // Your code here
                    String selectedCode = ((NonDelivery) non_delivery_reason_spinner_id.getSelectedItem()).getNDRCode();
                    if(selectedCode!=null&&selectedCode.length()>0&&selectedCode.equalsIgnoreCase("CRDA")){
                        non_delivery_another_date_label.setVisibility(View.VISIBLE);
                        non_delivery_another_date.setVisibility(View.VISIBLE);
                    }else{
                        non_delivery_another_date_label.setVisibility(View.GONE);
                        non_delivery_another_date.setVisibility(View.GONE);
                    }
                    Log.i("DATA", ((NonDelivery) non_delivery_reason_spinner_id.getSelectedItem()).getNDRCode());
                }

                public void onNothingSelected(AdapterView<?> adapterView) {
                    return;
                }
            });
        }else{
            fetchNDRReason();
        }

    }


    /**
     * Register all the views
     */
    private void registerViews() {
        non_delivery_reason_spinner_id = (Spinner)findViewById(R.id.non_delivery_reason_spinner_id);
        delivered_fail_update_button = (Button) findViewById(R.id.delivered_fail_update_button);
        delivered_fail_update_save_button= (Button) findViewById(R.id.delivered_fail_update_save_button);
        delivered_fail_update_cancel_button = (Button) findViewById(R.id.delivered_fail_update_cancel_button);
        non_delivery_another_date = (DatePicker) findViewById(R.id.non_delivery_another_date);
//        long MILLS_IN_YEAR = 1000L * 60 * 60 * 24 * 365; // Returns 31536000000
//        Calendar cal = Calendar.getInstance();
//        cal.set(non_delivery_another_date.getYear(), non_delivery_another_date.getMonth() + 1, non_delivery_another_date.getDayOfMonth());
//        cal.add(Calendar.DATE, 1);

        Calendar calMin = Calendar.getInstance();
//        calMin.set(non_delivery_another_date.getYear(), non_delivery_another_date.getMonth() - 1, non_delivery_another_date.getDayOfMonth());
        calMin.add(Calendar.DATE, 1);

        Calendar calMax = Calendar.getInstance();
//        calMax.set(non_delivery_another_date.getYear(), non_delivery_another_date.getMonth() + 1, non_delivery_another_date.getDayOfMonth());
        calMax.add(Calendar.DATE, 1);
        calMax.add(Calendar.YEAR, 5);
//        non_delivery_another_date.updateDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)-1, cal.get(Calendar.DATE));
        non_delivery_another_date.updateDate(calMin.get(Calendar.YEAR), calMin.get(Calendar.MONTH)-1, calMin.get(Calendar.DATE));
        non_delivery_another_date.setMinDate(calMin.getTimeInMillis());
        non_delivery_another_date.setMaxDate(calMax.getTimeInMillis());
//        non_delivery_another_date.setMinDate(System.currentTimeMillis() - 1000);
        non_delivery_another_date_label = (TextView) findViewById(R.id.non_delivery_another_date_label);

    }


    /**
     * Register listeners on all the views
     */
    private void registerListeners() {

        delivered_fail_update_button.setOnClickListener(this);
        delivered_fail_update_save_button.setOnClickListener(this);
        if(isBulkSubmission){
//            delivered_fail_update_save_button.setVisibility(View.VISIBLE);
        }
        delivered_fail_update_cancel_button.setOnClickListener(this);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.delivered_fail_update_save_button:
                long id = System.currentTimeMillis();
                String savedPassed = Utility.getFromSharedPrefs(DRSFailActivity.this, BoyID + "_pendingDRS");
                if(savedPassed!=null&&savedPassed.length()>0){
                    Utility.saveToSharedPrefs(DRSFailActivity.this,BoyID+"_pendingDRS",savedPassed+","+id);
                }else{
                    Utility.saveToSharedPrefs(DRSFailActivity.this,BoyID+"_pendingDRS",""+id);
                }

                JSONObject jsonObject = new JSONObject();
                Utility.saveToSharedPrefs(DRSFailActivity.this,""+id,jsonObject.toString());
                break;
            case R.id.delivered_fail_update_button:
                if(isBulkSubmission){
                    performNonDeliveredBulkOperation();
                }else{
                    performNonDeliveredOperation();
                }
                break;

            case R.id.delivered_fail_update_cancel_button:
                finish();
                break;

            default:
                break;
        }
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





    private ArrayList<NonDelivery> parseNDRReason(XmlPullParser parser) throws XmlPullParserException,IOException
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



    private void fetchNDRReason(){
        showProgrss("Please Wait. Fetching Reason.");
        RequestQueue queue = Volley.newRequestQueue(this);


        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://c2p.gojavas.net/DataSyncWebApi.asmx/GetNDRReason",
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
                            final ArrayList<NonDelivery> nonDeliveries = parseNDRReason(parser);
                            Log.i("TEST", nonDeliveries.toString());
                            ArrayAdapter<NonDelivery> adapter = new ArrayAdapter<NonDelivery>(DRSFailActivity.this,R.layout.spinner_textview, nonDeliveries);
                            non_delivery_reason_spinner_id.setAdapter(adapter);
                            non_delivery_reason_spinner_id.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                    // Your code here
                                    String selectedCode = ((NonDelivery) non_delivery_reason_spinner_id.getSelectedItem()).getNDRCode();
                                    if(selectedCode!=null&&selectedCode.length()>0&&selectedCode.equalsIgnoreCase("CRDA")){
                                        non_delivery_another_date_label.setVisibility(View.VISIBLE);
                                        non_delivery_another_date.setVisibility(View.VISIBLE);
                                    }else{
                                        non_delivery_another_date_label.setVisibility(View.GONE);
                                        non_delivery_another_date.setVisibility(View.GONE);
                                    }
                                    Log.i("DATA", ((NonDelivery) non_delivery_reason_spinner_id.getSelectedItem()).getNDRCode());
                                }

                                public void onNothingSelected(AdapterView<?> adapterView) {
                                    return;
                                }
                            });
                        } catch (XmlPullParserException e) {
                            Utility.showToast(DRSFailActivity.this, Constants.UNKNOWN_ERROR_MESSAGE);
                            e.printStackTrace();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            Utility.showToast(DRSFailActivity.this, Constants.UNKNOWN_ERROR_MESSAGE);
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utility.showToast(DRSFailActivity.this, Constants.UNKNOWN_ERROR_MESSAGE + ". Please try after some time.");
                hideProgrss();
            }
        });
        queue.add(stringRequest);
        return;
    }


    private String parseServiceStatus(XmlPullParser parser) throws XmlPullParserException,IOException
    {
        int eventType = parser.getEventType();
        String serviceStatus = "";
        while (eventType != XmlPullParser.END_DOCUMENT){
            String name = null;
            switch (eventType){
                case XmlPullParser.START_DOCUMENT:
                    break;
                case XmlPullParser.START_TAG:
                    name = parser.getName();
                    if (name.equalsIgnoreCase("string")){
                        serviceStatus = parser.nextText();;
                    }
                    break;
                case XmlPullParser.END_TAG:
                    break;
            }
            eventType = parser.next();
        }
        return serviceStatus;
    }

    /*@Override
    protected void onStop() {
        setResult(2);
        super.onStop();
    }
    @Override
    protected void onDestroy() {
        setResult(2);
        super.onDestroy();
    }*/


    private void performNonDeliveredBulkOperation() {
        try{
            if(non_delivery_reason_spinner_id!=null){
                nonDeliveryId = ((NonDelivery)non_delivery_reason_spinner_id.getSelectedItem()).getNDRReasonID();
            }
        }catch (Exception e){}
        if(nonDeliveryId!=null&&nonDeliveryId.length()>0&&AWBNo!=null&&AWBNo.length()>0 &&UserID!=null&&UserID.length()>0&&BoyID!=null&&BoyID.length()>0){
            showProgrss("Please Wait...");





            new Thread(new Runnable(){
                @Override
                public void run() {
                    try {

                        String expDeliveryDate = DateFormat.getDateTimeInstance().format(new Date());
                        if(non_delivery_another_date!=null && non_delivery_another_date.getVisibility() == View.VISIBLE){
                            int   day  = non_delivery_another_date.getDayOfMonth();
                            int   month= non_delivery_another_date.getMonth();
                            int   year = non_delivery_another_date.getYear();
                            Calendar calendar = Calendar.getInstance();
                            calendar.set(year, month, day);
                            expDeliveryDate = DateFormat.getDateTimeInstance().format(new Date(calendar.getTime().toString()));
                        }
//            RequestQueue queue = Volley.newRequestQueue(this);
                        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                        String latitude = "";
                        String longitude = "";
                        try{
                            HashMap<String, String> location = Utility.getLocation(DRSFailActivity.this);
                            latitude = location.get(Constants.LATITUDE);
                            longitude = location.get(Constants.LONGITUDE);
                        }catch (Exception e){
                        }
                        // Your implementation
                        String METHOD_NAME = "POST_BulkNDR";
                        String NAMESPACE = "http://tempuri.org/";
                        String SOAP_ACTION = NAMESPACE + METHOD_NAME;
                        final String URL = "http://c2p.gojavas.net/DataSyncWebApi.asmx";
                        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

                        PropertyInfo sayHelloPI = new PropertyInfo();
                        sayHelloPI.setName("UserID");
                        sayHelloPI.setValue("" + UserID);
                        sayHelloPI.setType(String.class);
                        request.addProperty(sayHelloPI);

                        PropertyInfo sayHelloPI1001 = new PropertyInfo();
                        sayHelloPI1001.setName("BoyID");
                        sayHelloPI1001.setValue("" + BoyID);
                        sayHelloPI1001.setType(String.class);
                        request.addProperty(sayHelloPI1001);


                        /*PropertyInfo sayHelloPI2 = new PropertyInfo();
                        sayHelloPI2.setName("lstAWBNo");
                        CustomKvmSerializable stringVector = new CustomKvmSerializable("string", String.class);
                        if(selectedBulkDrsEntities!=null)
                            for(int i=0;i<selectedBulkDrsEntities.size();i++){
                                stringVector.add(selectedBulkDrsEntities.get(i));
                            }
                        sayHelloPI2.setValue(stringVector);
                        sayHelloPI2.setType(stringVector.getClass());
                        request.addProperty(sayHelloPI2);*/

                        PropertyInfo sayHelloPI2 = new PropertyInfo();
                        sayHelloPI2.setName("ListOfAWB");
//                        CustomKvmSerializable stringVector = new CustomKvmSerializable("string", String.class);
                        String _docetNumber = "";
                        if(selectedBulkDrsEntities!=null)
                            for(int i=0;i<selectedBulkDrsEntities.size();i++){
                                if(i==0){
                                    _docetNumber = selectedBulkDrsEntities.get(i);
                                }else{
                                    _docetNumber = _docetNumber + ","+selectedBulkDrsEntities.get(i);
                                }
//                                stringVector.add(selectedBulkDrsEntities.get(i));
//                                _docetNumber = _docetNumber + ","+selectedBulkDrsEntities.get(i);
                            }
                        sayHelloPI2.setValue(_docetNumber);
                        sayHelloPI2.setType(String.class);
                        request.addProperty(sayHelloPI2);



                        Date date = Calendar.getInstance().getTime();
                        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
                        String deliveryDateTimeString = formatter.format(date);
                        PropertyInfo sayHelloPI3 = new PropertyInfo();
                        sayHelloPI3.setName("NonDeliveryDateTime");
                        sayHelloPI3.setValue(deliveryDateTimeString);
                        sayHelloPI3.setType(String.class);
                        request.addProperty(sayHelloPI3);


                        PropertyInfo sayHelloPI6 = new PropertyInfo();
                        sayHelloPI6.setName("ReasonID");
                        sayHelloPI6.setValue("" + nonDeliveryId);
                        sayHelloPI6.setType(String.class);
                        request.addProperty(sayHelloPI6);


                        PropertyInfo sayHelloPI9 = new PropertyInfo();
                        sayHelloPI9.setName("Latitude");
                        sayHelloPI9.setValue("" + latitude);
                        sayHelloPI9.setType(String.class);
                        request.addProperty(sayHelloPI9);


                        PropertyInfo sayHelloPI10 = new PropertyInfo();
                        sayHelloPI10.setName("Longitude");
                        sayHelloPI10.setValue("" + longitude);
                        sayHelloPI10.setType(String.class);
                        request.addProperty(sayHelloPI10);

                        PropertyInfo sayHelloexd = new PropertyInfo();
                        sayHelloexd.setName("ExpDeliveryDate");
                        sayHelloexd.setValue("" + expDeliveryDate);
                        sayHelloexd.setType(String.class);
                        request.addProperty(sayHelloexd);

                        PropertyInfo sayHelloPI12 = new PropertyInfo();
                        sayHelloPI12.setName("IsTesting");
                        sayHelloPI12.setValue("0");
                        sayHelloPI12.setType(String.class);
                        request.addProperty(sayHelloPI12);


                        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                                SoapEnvelope.VER11);
                        new MarshalHashtable().register(envelope);
                        new MarshalBase64().register(envelope); // serialization
                        envelope.encodingStyle = SoapEnvelope.ENC;
                        envelope.bodyOut = request;
                        envelope.dotNet = true;
                        envelope.setAddAdornments(false);
                        envelope.implicitTypes = true;
                        envelope.setOutputSoapObject(request);


                        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
                        androidHttpTransport.debug=true;
                        envelope.dotNet = true;
                        try {
                            androidHttpTransport.call(SOAP_ACTION, envelope);
                            if (envelope.bodyIn instanceof SoapFault) {
                                final SoapFault sf = (SoapFault) envelope.bodyIn;
                            }
                            SoapPrimitive resultsRequestSOAP = null;
                            resultsRequestSOAP = (SoapPrimitive) envelope.getResponse();
                            hideProgrss();
                            String jsonstringRes = resultsRequestSOAP.toString();
                            Log.i("TEST", "777");
                            System.out.println("---------RES ===>>>" + jsonstringRes);
                            if(jsonstringRes!=null&&jsonstringRes.length()>0&&jsonstringRes.trim().equalsIgnoreCase("SUCCESS")){
                                setResult(2);
                                finish();
                                showUIToast("Status updated successfully");
                            }else{
                                if(jsonstringRes!=null&&jsonstringRes.length()>0){
                                    showUIToast(""+jsonstringRes);
                                }else{
                                    showUIToast("Unable to process. Please try after some time.");
                                }
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                            hideProgrss();
                            showUIToast("Unable to process. Please contact admin.");
                        }
                    }
                    catch (Exception ex) {
                        ex.printStackTrace();
                        showUIToast("Unable to process. Please contact admin.");
                    }
                }
            }).start();


//            url = "http://c2p.gojavas.net/DataSyncWebApi.asmx/POST_BulkNDR?UserID="+UserID+"&BoyID="+BoyID+"&lstAWBNo="+AWBNo+"&NonDeliveryDateTime="+ URLEncoder.encode(currentDateTimeString, "UTF-8")+"&ReasonID="+nonDeliveryId+"&Latitude="+latitude+"&Longitude="+longitude+"&IsTesting=1&ExpDeliveryDate="+URLEncoder.encode(expDeliveryDate, "UTF-8");









//
//            if(true)
//                return;
//
//
//
//
//
//            String url = null;
//            try {
//                url = "http://c2p.gojavas.net/DataSyncWebApi.asmx/POST_BulkNDR?UserID="+UserID+"&BoyID="+BoyID+"&lstAWBNo="+AWBNo+"&NonDeliveryDateTime="+ URLEncoder.encode(currentDateTimeString, "UTF-8")+"&ReasonID="+nonDeliveryId+"&Latitude="+latitude+"&Longitude="+longitude+"&IsTesting=1&ExpDeliveryDate="+URLEncoder.encode(expDeliveryDate, "UTF-8");
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            }
//            URI  uri =null;
//            try {
//                uri = new URI(url);
//            } catch (URISyntaxException e) {
//                e.printStackTrace();
//            }
//            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
//                    new Response.Listener<String>() {
//                        @Override
//                        public void onResponse(String response) {
//                            Log.i("TEST", response);
//                            hideProgrss();
//                            XmlPullParserFactory pullParserFactory;
//                            try {
//                                pullParserFactory = XmlPullParserFactory.newInstance();
//                                XmlPullParser parser = pullParserFactory.newPullParser();
//                                InputStream in_s = new ByteArrayInputStream(response.getBytes("UTF-8"));
//                                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
//                                parser.setInput(in_s, null);
//                                String serviceStatus = parseServiceStatus(parser);
//                                if(serviceStatus!=null&&serviceStatus.length()>0){
//                                    if(serviceStatus.equalsIgnoreCase("SUCCESS")){
//                                        setResult(2);
//                                        finish();
//                                        Utility.showToast(DRSFailActivity.this, "Status updated successfully ");
//                                    }else{
//                                        Utility.showToast(DRSFailActivity.this, serviceStatus);
//                                    }
//                                }else{
//                                    Utility.showToast(DRSFailActivity.this, Constants.UNKNOWN_ERROR_MESSAGE);
//                                }
//                            } catch (XmlPullParserException e) {
//                                Utility.showToast(DRSFailActivity.this, Constants.UNKNOWN_ERROR_MESSAGE);
//                                e.printStackTrace();
//                            } catch (IOException e) {
//                                // TODO Auto-generated catch block
//                                Utility.showToast(DRSFailActivity.this, Constants.UNKNOWN_ERROR_MESSAGE);
//                                e.printStackTrace();
//                            }
//                        }
//                    }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    Log.i("TEST",error.toString());
//                    Utility.showToast(DRSFailActivity.this, Constants.UNKNOWN_ERROR_MESSAGE + ". Please try after some time.");
//                    hideProgrss();
//                }
//            });
//            queue.add(stringRequest);
        }else{
            Utility.showToast(DRSFailActivity.this, "Unable to find non delivery reason.");
        }
    }


    public void showUIToast(final String message){
        runOnUiThread(new Runnable() {
            public void run() {
                Utility.showToast(DRSFailActivity.this, message);
            }
        });
    }

    private void performNonDeliveredOperation() {
//        try{}catch (Exception e){}
        try{
            if(non_delivery_reason_spinner_id!=null){

                nonDeliveryId = ((NonDelivery)non_delivery_reason_spinner_id.getSelectedItem()).getNDRReasonID();
//                Log.i("DATA",((NonDelivery)non_delivery_reason_spinner_id.getSelectedItem()).getNDRReasonID());
//                Log.i("DATA",((NonDelivery)non_delivery_reason_spinner_id.getSelectedItem()).getIsNDR());
//                Log.i("DATA",((NonDelivery)non_delivery_reason_spinner_id.getSelectedItem()).getIsReversePickup());
                Log.i("DATA",((NonDelivery)non_delivery_reason_spinner_id.getSelectedItem()).getNDRCode());
//                Log.i("DATA",((NonDelivery)non_delivery_reason_spinner_id.getSelectedItem()).getNDRReason());

            }
        }catch (Exception e){}

        String expDeliveryDate = DateFormat.getDateTimeInstance().format(new Date());

        if(non_delivery_another_date!=null && non_delivery_another_date.getVisibility() == View.VISIBLE){
            int   day  = non_delivery_another_date.getDayOfMonth();
            int   month= non_delivery_another_date.getMonth();
            int   year = non_delivery_another_date.getYear();
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, day);
            expDeliveryDate = DateFormat.getDateTimeInstance().format(new Date(calendar.getTime().toString()));

        }

        if(nonDeliveryId!=null&&nonDeliveryId.length()>0&&AWBNo!=null&&AWBNo.length()>0 &&UserID!=null&&UserID.length()>0&&BoyID!=null&&BoyID.length()>0){
            showProgrss("Please Wait...");

            RequestQueue queue = Volley.newRequestQueue(this);
            String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
            String latitude = "";
            String longitude = "";
            try{
                HashMap<String, String> location = Utility.getLocation(DRSFailActivity.this);
                latitude = location.get(Constants.LATITUDE);
                longitude = location.get(Constants.LONGITUDE);
            }catch (Exception e){

            }
             String url = null;
            try {

                url = "http://c2p.gojavas.net/DataSyncWebApi.asmx/PostUnDeliveredUpdation?UserID="+UserID+"&BoyID="+BoyID+"&AWBNo="+AWBNo+"&NonDeliveryDateTime="+ URLEncoder.encode(currentDateTimeString, "UTF-8")+"&ReasonID="+nonDeliveryId+"&Latitude="+latitude+"&Longitude="+longitude+"&IsTesting=0&ExpDeliveryDate="+URLEncoder.encode(expDeliveryDate, "UTF-8");
//                Log.i("DATA",url);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }


            URI  uri =null;
            try {
                uri = new URI(url);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.i("TEST", response);
//                            Utility.showToast(DRSFailActivity.this, response);
                            hideProgrss();
                            XmlPullParserFactory pullParserFactory;
                            try {
                                pullParserFactory = XmlPullParserFactory.newInstance();
                                XmlPullParser parser = pullParserFactory.newPullParser();
                                InputStream in_s = new ByteArrayInputStream(response.getBytes("UTF-8"));
                                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                                parser.setInput(in_s, null);
                                String serviceStatus = parseServiceStatus(parser);
                                if(serviceStatus!=null&&serviceStatus.length()>0){
                                    if(serviceStatus.equalsIgnoreCase("SUCCESS")){
                                        setResult(2);
                                        finish();
                                        Utility.showToast(DRSFailActivity.this, "Status updated successfully ");
                                    }else{
                                        Utility.showToast(DRSFailActivity.this, serviceStatus);
                                    }
                                }else{
                                    Utility.showToast(DRSFailActivity.this, Constants.UNKNOWN_ERROR_MESSAGE);
                                }
                            } catch (XmlPullParserException e) {
                                Utility.showToast(DRSFailActivity.this, Constants.UNKNOWN_ERROR_MESSAGE);
                                e.printStackTrace();
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                Utility.showToast(DRSFailActivity.this, Constants.UNKNOWN_ERROR_MESSAGE);
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.i("TEST",error.toString());
                    Utility.showToast(DRSFailActivity.this, Constants.UNKNOWN_ERROR_MESSAGE + ". Please try after some time.");
                    hideProgrss();
                }
            })/*{

                @Override
                protected Map<String,String> getParams(){
                    Map<String,String> params = new HashMap<String, String>();
                    params.put("UserID",UserID);
                    params.put("BoyID",BoyID);
                    params.put("AWBNo",AWBNo);
                    String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                    params.put("NonDeliveryDateTime",currentDateTimeString);
                    params.put("ReasonID",nonDeliveryId);
                    String latitude = "";
                    String longitude = "";
                    try{
                        HashMap<String, String> location = Utility.getLocation(DRSFailActivity.this);
                        latitude = location.get(Constants.LATITUDE);
                        longitude = location.get(Constants.LONGITUDE);
                    }catch (Exception e){

                    }
                    params.put("Latitude","28.599580");
                    params.put("Longitude","77.402053");
                    params.put("IsTesting","true");
                    Log.i("TEST",params.toString());
                    return params;
                }
            }*/;
            queue.add(stringRequest);

        }else{
            Utility.showToast(DRSFailActivity.this, "Unable to find non delivery reason.");
        }


    }

}

