package com.gojavas.taskforce.ui.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.gojavas.taskforce.R;
import com.gojavas.taskforce.entity.NonDelivery;
import com.gojavas.taskforce.entity.ReversePickup;
import com.gojavas.taskforce.utils.Constants;
import com.gojavas.taskforce.utils.Utility;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by MadanS on 5/30/2017.
 */
public class ReversePickupCancelActivity extends AppCompatActivity implements View.OnClickListener {


    private Button delivered_fail_update_button;
    private Button delivered_fail_update_cancel_button;
    private ProgressDialog progressDialog;
    private TextView non_delivery_another_date_label,non_delivery_reason_spinner_id_for_unpicked_cancel_level;
    private DatePicker non_delivery_another_date;
    private Spinner non_delivery_reason_spinner_id,non_delivery_reason_spinner_id_for_unpicked_cancel;
//    private DrsEntity mDrsEntity;
    private ReversePickup mReversePickup;
    private String UserID= "";
    private String BoyID= "";
    private String AWBNo= "";
    private String NonDeliveryDateTime= "";
    private String ReasonID = "";
    private String Latitude = "";
    private String Longitude = "";
    private String IsTesting = "false";
    private String nonDeliveryId = "";
    private String remarks;
    private EditText reverse_fail_remarks;

    String URL = "http://c2p.gojavas.net/DataSyncWebApi.asmx?WSDL";
    String NAMESPACE = "http://tempuri.org/";
    String SOAP_ACTION = "http://tempuri.org/PostPickupStatus_M";
    String METHOD_NAME = "PostPickupStatus_M";
    String PARAMETER_NAME = "UserID";
    private RadioGroup radioGroup;
    private LinearLayout reverse_pickup_dynamic_sku;
    private EditText reverse_fail_awb;
    private String mTicketId;
    private Button mBarCodeScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reverse_cancel);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Update Status");
        progressDialog = new ProgressDialog(ReversePickupCancelActivity.this);
        Intent i = getIntent();
        mReversePickup = (ReversePickup)i.getSerializableExtra("selectedPickupEntities");
        if(mReversePickup!=null){
            mTicketId = mReversePickup.getTicketNo();
        }

        BoyID = Utility.getFromSharedPrefs(ReversePickupCancelActivity.this, "BOY_ID");
        UserID = Utility.getFromSharedPrefs(ReversePickupCancelActivity.this, "USER_ID");
        registerViews();
        registerListeners();


        if(Utility.reasonList!=null&&Utility.reasonList.size()>0){
            createDynamicView();
            onRadioButtonChange();
            setOnItemClickListerForStatusSpinner();
        }else{
            fetchNDRReasonNew();
        }

    }



    private void setOnItemClickListerForStatusSpinner() {
        if(reverse_pickup_dynamic_sku!=null){
            for(int i = 0; i<reverse_pickup_dynamic_sku.getChildCount();i++){
                if(reverse_pickup_dynamic_sku.getChildAt(i)!=null&&reverse_pickup_dynamic_sku.getChildAt(i) instanceof LinearLayout){
                    final LinearLayout linearLayout = (LinearLayout) reverse_pickup_dynamic_sku.getChildAt(i);
                    if(linearLayout!=null&&linearLayout.getChildCount()>0){
                        for(int j=0;j<linearLayout.getChildCount();j++){
                            final int iV = j;
                            if(linearLayout.getChildAt(j)!=null&&linearLayout.getChildAt(j) instanceof Spinner){
                                Spinner spinner = (Spinner) linearLayout.getChildAt(j);
                                String spinnerType = spinner.getTag().toString();
                                if(spinnerType!=null){
                                    if(spinnerType.equalsIgnoreCase("status")){
                                        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                            @Override
                                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                if (position == 0) {
                                                    ((Spinner) linearLayout.getChildAt(iV + 2)).setVisibility(View.GONE);
                                                    ((TextView) linearLayout.getChildAt(iV + 1)).setVisibility(View.GONE);

                                                } else {
                                                    ((Spinner) linearLayout.getChildAt(iV + 2)).setVisibility(View.VISIBLE);
                                                    ((TextView) linearLayout.getChildAt(iV + 1)).setVisibility(View.VISIBLE);
                                                }
                                            }

                                            @Override
                                            public void onNothingSelected(AdapterView<?> parent) {

                                            }
                                        });

                                    }
                                }
                            }
                        }
                    }
                }

            }

        }

    }

    private void onRadioButtonChange(){
        radioGroup = (RadioGroup) findViewById(R.id.myRadioGroup);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                mBarCodeScan.setVisibility(View.GONE);
                if (checkedId == R.id.update_reverse_full_picked) {
                    hideUnpickedSpinner();
                    reverse_fail_awb.setVisibility(View.VISIBLE);
                    reverse_pickup_dynamic_sku.setVisibility(View.GONE);
                    if(reverse_pickup_dynamic_sku!=null){
                        for(int i = 0; i<reverse_pickup_dynamic_sku.getChildCount();i++){
                            if(reverse_pickup_dynamic_sku.getChildAt(i)!=null&&reverse_pickup_dynamic_sku.getChildAt(i) instanceof LinearLayout){
                                LinearLayout linearLayout = (LinearLayout) reverse_pickup_dynamic_sku.getChildAt(i);
                                if(linearLayout!=null&&linearLayout.getChildCount()>0){
                                    for(int j=0;j<linearLayout.getChildCount();j++){
                                        if(linearLayout.getChildAt(j)!=null&&linearLayout.getChildAt(j) instanceof Spinner){
                                            Spinner spinner = (Spinner) linearLayout.getChildAt(j);
                                            String spinnerType = spinner.getTag().toString();
                                            if(spinnerType!=null){
                                                if(spinnerType.equalsIgnoreCase("status")){
                                                    spinner.setSelection(0);
                                                }else if(spinnerType.equalsIgnoreCase("reason_id")){
                                                    spinner.setEnabled(false);
                                                    spinner.setClickable(false);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else if (checkedId == R.id.update_reverse_picked) {
                    mBarCodeScan.setVisibility(View.VISIBLE);
                    hideUnpickedSpinner();
                    reverse_fail_awb.setVisibility(View.VISIBLE);
                    reverse_pickup_dynamic_sku.setVisibility(View.VISIBLE);
                    if(reverse_pickup_dynamic_sku!=null){
                        for(int i = 0; i<reverse_pickup_dynamic_sku.getChildCount();i++){
                            if(reverse_pickup_dynamic_sku.getChildAt(i)!=null&&reverse_pickup_dynamic_sku.getChildAt(i) instanceof LinearLayout){
                                LinearLayout linearLayout = (LinearLayout) reverse_pickup_dynamic_sku.getChildAt(i);
                                if(linearLayout!=null&&linearLayout.getChildCount()>0){
                                    for(int j=0;j<linearLayout.getChildCount();j++){
                                        if(linearLayout.getChildAt(j)!=null&&linearLayout.getChildAt(j) instanceof Spinner){
                                            Spinner spinner = (Spinner) linearLayout.getChildAt(j);
                                            String spinnerType = spinner.getTag().toString();
                                            if(spinnerType!=null){
                                                if(spinnerType.equalsIgnoreCase("status")){
                                                    spinner.setSelection(1);
                                                }else if(spinnerType.equalsIgnoreCase("reason_id")){
                                                    spinner.setEnabled(true);
                                                    spinner.setClickable(true);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }else if (checkedId == R.id.update_reverse_unpicked) {
                    showUnpickedSpinner();
                    reverse_pickup_dynamic_sku.setVisibility(View.GONE);
                    reverse_fail_awb.setVisibility(View.GONE);
                    if(reverse_pickup_dynamic_sku!=null){
                        for(int i = 0; i<reverse_pickup_dynamic_sku.getChildCount();i++){
                            if(reverse_pickup_dynamic_sku.getChildAt(i)!=null&&reverse_pickup_dynamic_sku.getChildAt(i) instanceof LinearLayout){
                                LinearLayout linearLayout = (LinearLayout) reverse_pickup_dynamic_sku.getChildAt(i);
                                if(linearLayout!=null&&linearLayout.getChildCount()>0){
                                    for(int j=0;j<linearLayout.getChildCount();j++){
                                        if(linearLayout.getChildAt(j)!=null&&linearLayout.getChildAt(j) instanceof Spinner){
                                            Spinner spinner = (Spinner) linearLayout.getChildAt(j);
                                            String spinnerType = spinner.getTag().toString();
                                            if(spinnerType!=null){
                                                if(spinnerType.equalsIgnoreCase("status")){
                                                    spinner.setSelection(1);
                                                }else if(spinnerType.equalsIgnoreCase("reason_id")){
                                                    spinner.setEnabled(true);
                                                    spinner.setClickable(true);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else if (checkedId == R.id.update_reverse_cancel) {
                    showUnpickedSpinner();
                    reverse_pickup_dynamic_sku.setVisibility(View.GONE);
                    reverse_fail_awb.setVisibility(View.GONE);
                    if(reverse_pickup_dynamic_sku!=null){
                        for(int i = 0; i<reverse_pickup_dynamic_sku.getChildCount();i++){
                            if(reverse_pickup_dynamic_sku.getChildAt(i)!=null&&reverse_pickup_dynamic_sku.getChildAt(i) instanceof LinearLayout){
                                LinearLayout linearLayout = (LinearLayout) reverse_pickup_dynamic_sku.getChildAt(i);
                                if(linearLayout!=null&&linearLayout.getChildCount()>0){
                                    for(int j=0;j<linearLayout.getChildCount();j++){
                                        if(linearLayout.getChildAt(j)!=null&&linearLayout.getChildAt(j) instanceof Spinner){
                                            Spinner spinner = (Spinner) linearLayout.getChildAt(j);
                                            String spinnerType = spinner.getTag().toString();
                                            if(spinnerType!=null){
                                                if(spinnerType.equalsIgnoreCase("status")){
                                                    spinner.setSelection(2);
                                                }else if(spinnerType.equalsIgnoreCase("reason_id")){
                                                    spinner.setEnabled(false);
                                                    spinner.setClickable(false);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

        });
    }


    private void createDynamicView(){
        if(mReversePickup!=null){
            LayoutInflater inflater=(LayoutInflater)getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            for(int i=0;i<mReversePickup.getSKU_ID().size();i++)
            {
                View llView = inflater.inflate(R.layout.activity_reverse_pickup_row, null);
                llView.setTag(""+mReversePickup.getSKU_ID().get(i));
                EditText txt=(EditText)llView.findViewById(R.id.reverse_sku_id);
                if(mReversePickup.getSKU_ID().get(i)!=null&&mReversePickup.getSKU_ID().get(i).length()>0){
                    txt.setText("" + mReversePickup.getSKU_ID().get(i));
                    Spinner status = (Spinner)llView.findViewById(R.id.non_delivery_status);
                    status.setTag("status");
                    final Spinner tmpSpinner = (Spinner)llView.findViewById(R.id.non_delivery_reason_spinner_id);
                    tmpSpinner.setTag("reason_id");
                    tmpSpinner.setEnabled(false);
                    tmpSpinner.setClickable(false);

                    final ArrayList<NonDelivery> nonDeliveriesWithReversePickup = getNDRReasonWithIsReversePickup(Utility.reasonList);
                    ArrayAdapter<NonDelivery> adapter = new ArrayAdapter<NonDelivery>(ReversePickupCancelActivity.this,R.layout.spinner_textview, nonDeliveriesWithReversePickup);
                    tmpSpinner.setAdapter(adapter);
//                    tmpSpinner.setVisibility(View.GONE);
                    reverse_pickup_dynamic_sku.addView(llView);
                }
            }
        }
    }



    private void showUnpickedSpinner(){
        non_delivery_reason_spinner_id_for_unpicked_cancel_level.setVisibility(View.VISIBLE);
        non_delivery_reason_spinner_id_for_unpicked_cancel.setVisibility(View.VISIBLE);
    }


    private void hideUnpickedSpinner(){
        non_delivery_reason_spinner_id_for_unpicked_cancel_level.setVisibility(View.GONE);
        non_delivery_reason_spinner_id_for_unpicked_cancel.setVisibility(View.GONE);
    }


    /**
     * Register all the views
     */
    private void registerViews() {
        mBarCodeScan = (Button) findViewById(R.id.bar_code_scan);
        non_delivery_reason_spinner_id_for_unpicked_cancel = (Spinner) findViewById(R.id.non_delivery_reason_spinner_id_for_unpicked_cancel);
        final ArrayList<NonDelivery> _nonDeliveriesWithReversePickup = getNDRReasonWithIsReversePickup(Utility.reasonList);
        ArrayAdapter<NonDelivery> _adapter = new ArrayAdapter<NonDelivery>(ReversePickupCancelActivity.this,R.layout.spinner_textview, _nonDeliveriesWithReversePickup);
        non_delivery_reason_spinner_id_for_unpicked_cancel.setAdapter(_adapter);
        non_delivery_reason_spinner_id_for_unpicked_cancel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int index, long l) {
                // Your code here


                if(reverse_pickup_dynamic_sku!=null){
                    for(int i = 0; i<reverse_pickup_dynamic_sku.getChildCount();i++){
                        if(reverse_pickup_dynamic_sku.getChildAt(i)!=null&&reverse_pickup_dynamic_sku.getChildAt(i) instanceof LinearLayout){
                            final LinearLayout linearLayout = (LinearLayout) reverse_pickup_dynamic_sku.getChildAt(i);
                            if(linearLayout!=null&&linearLayout.getChildCount()>0){
                                for(int j=0;j<linearLayout.getChildCount();j++){
                                    final int iV = j;
                                    if(linearLayout.getChildAt(j)!=null&&linearLayout.getChildAt(j) instanceof Spinner){
                                        Spinner spinner = (Spinner) linearLayout.getChildAt(j);
                                        String spinnerType = spinner.getTag().toString();
                                        if(spinnerType!=null){
                                            if(spinnerType.equalsIgnoreCase("reason_id")){
                                                spinner.setSelection(index);
                                            }
                                        }
                                    }
                                }
                            }
                        }

                    }

                }


            }

            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });
        non_delivery_reason_spinner_id_for_unpicked_cancel_level = (TextView) findViewById(R.id.non_delivery_reason_spinner_id_for_unpicked_cancel_level);

        reverse_fail_remarks = (EditText)findViewById(R.id.reverse_fail_remarks);
        reverse_fail_awb = (EditText)findViewById(R.id.reverse_fail_awb);


//        non_delivery_reason_spinner_id = (Spinner)findViewById(R.id.non_delivery_reason_spinner_id);
        delivered_fail_update_button = (Button) findViewById(R.id.delivered_fail_update_button);
        delivered_fail_update_cancel_button = (Button) findViewById(R.id.delivered_fail_update_cancel_button);
//        non_delivery_another_date = (DatePicker) findViewById(R.id.non_delivery_another_date);
        reverse_pickup_dynamic_sku = (LinearLayout) findViewById(R.id.reverse_pickup_dynamic_sku);

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
//        non_delivery_another_date.updateDate(calMin.get(Calendar.YEAR), calMin.get(Calendar.MONTH)-1, calMin.get(Calendar.DATE));
//        non_delivery_another_date.setMinDate(calMin.getTimeInMillis());
//        non_delivery_another_date.setMaxDate(calMax.getTimeInMillis());
//        non_delivery_another_date.setMinDate(System.currentTimeMillis() - 1000);
//        non_delivery_another_date_label = (TextView) findViewById(R.id.non_delivery_another_date_label);

    }


    /**
     * Register listeners on all the views
     */
    private void registerListeners() {
        mBarCodeScan.setOnClickListener(this);
        delivered_fail_update_button.setOnClickListener(this);
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


    public void scanItems(){
        new IntentIntegrator(this).initiateScan();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        switch (requestCode) {
            case IntentIntegrator.REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
                    String scannedId = result.getContents();
                    boolean itemFound = false;
                    if(scannedId!=null&&scannedId.length()>0){
                        if(mReversePickup!=null&&mReversePickup.getSKU_ID()!=null&&mReversePickup.getSKU_ID().size()>0) {
                            for (int i = 0; i < mReversePickup.getSKU_ID().size(); i++) {
                                if (mReversePickup.getSKU_ID().get(i) != null && mReversePickup.getSKU_ID().get(i).length() > 0 && scannedId.equalsIgnoreCase(mReversePickup.getSKU_ID().get(i))) {
                                    itemFound = true;
                                    break;
                                }
                            }
                            if (itemFound) {
                                for(int i = 0; i<reverse_pickup_dynamic_sku.getChildCount();i++){
                                    if(reverse_pickup_dynamic_sku.getChildAt(i)!=null&&reverse_pickup_dynamic_sku.getChildAt(i) instanceof LinearLayout){
                                        LinearLayout linearLayout = (LinearLayout) reverse_pickup_dynamic_sku.getChildAt(i);
                                        if(linearLayout!=null&&linearLayout.getChildCount()>0 && linearLayout.getTag().toString().equalsIgnoreCase(scannedId)){
                                            for(int j=0;j<linearLayout.getChildCount();j++){
                                                if(linearLayout.getChildAt(j)!=null&&linearLayout.getChildAt(j) instanceof Spinner){
                                                    Spinner spinner = (Spinner) linearLayout.getChildAt(j);
                                                    String spinnerType = spinner.getTag().toString();
                                                    if(spinnerType!=null){
                                                        if(spinnerType.equalsIgnoreCase("status")){
                                                            spinner.setSelection(0);
                                                            Toast.makeText(ReversePickupCancelActivity.this,"SKU #"+scannedId+" picked.",Toast.LENGTH_LONG).show();
                                                            break;
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                        }else{
                                Utility.showNormalAlertDialog(ReversePickupCancelActivity.this, "Scan Result","No Code Found");
                            }
                        }else{
                            Utility.showNormalAlertDialog(ReversePickupCancelActivity.this, "Scan Result","No Code Found");
                        }

                    }else{
                        Utility.showNormalAlertDialog(ReversePickupCancelActivity.this, "Scan Result","No Code Found");
                    }

                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.delivered_fail_update_button:
                performNonDeliveredOperation();
                break;

            case R.id.bar_code_scan:
                scanItems();
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



    private void parseResponseXML(String xml){
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(new InputSource(new StringReader(xml)));
            Element element=doc.getDocumentElement();
            element.normalize();
            NodeList nList = doc.getElementsByTagName("PostPickupStatus_MResponse");
            if(nList!=null&&nList.getLength()>0){
                for (int i=0; i<nList.getLength(); i++) {
                    Node node = nList.item(0);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        Element element2 = (Element) node;
                        if(element2!=null){
                            String serRes = getXMLValue("PostPickupStatus_MResult", element2);
                            if(serRes!=null&&serRes.length()>0){
                                if(serRes.equalsIgnoreCase("SUCCESS")){
                                    Toast.makeText(ReversePickupCancelActivity.this,"Status updated successfully",Toast.LENGTH_LONG).show();
                                    setResult(2);
                                    finish();
                                }else{
                                    Utility.showNormalAlertDialog(ReversePickupCancelActivity.this, "Message", serRes);
                                }
                            }else{
                                Utility.showNormalAlertDialog(ReversePickupCancelActivity.this, "Message", "Unable to connect. Please try after some time.");
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {e.printStackTrace();}
    }


    private static String getXMLValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node node = nodeList.item(0);
        return node.getNodeValue();
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


    public ArrayList<NonDelivery> getNDRReasonWithIsReversePickup(ArrayList<NonDelivery> _nonDeliveries){
        ArrayList<NonDelivery> nonDeliveriesTmp = new ArrayList<NonDelivery>();
        if(_nonDeliveries!=null&&_nonDeliveries.size()>0){
            for(int i=0;i<_nonDeliveries.size();i++){
                if(_nonDeliveries.get(i).getIsReversePickup().equalsIgnoreCase("true")){
                    nonDeliveriesTmp.add(_nonDeliveries.get(i));
                }
            }
        }
        return nonDeliveriesTmp;
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
                            final ArrayList<NonDelivery> nonDeliveriesWithReversePickup = getNDRReasonWithIsReversePickup(nonDeliveries);
                            ArrayAdapter<NonDelivery> adapter = new ArrayAdapter<NonDelivery>(ReversePickupCancelActivity.this,R.layout.spinner_textview, nonDeliveriesWithReversePickup);
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
                            Utility.showToast(ReversePickupCancelActivity.this, Constants.UNKNOWN_ERROR_MESSAGE);
                            e.printStackTrace();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            Utility.showToast(ReversePickupCancelActivity.this, Constants.UNKNOWN_ERROR_MESSAGE);
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utility.showToast(ReversePickupCancelActivity.this, Constants.UNKNOWN_ERROR_MESSAGE + ". Please try after some time.");
                hideProgrss();
            }
        });
        queue.add(stringRequest);
        return;
    }



    private void fetchNDRReasonNew(){
        showProgrss("Please Wait. Fetching Reason.");
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.BASE_URL+"/GetNDRReason",
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
                            Utility.reasonList = nonDeliveries;
                            if(Utility.reasonList!=null&&Utility.reasonList.size()>0){
                                createDynamicView();
                                onRadioButtonChange();
                            }else{
                                Utility.showToast(ReversePickupCancelActivity.this, "Unable to fetch NDR Reason. Please contact office.");
                                finish();
                            }
                        } catch (XmlPullParserException e) {
                            Utility.showToast(ReversePickupCancelActivity.this, Constants.UNKNOWN_ERROR_MESSAGE);
                            e.printStackTrace();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            Utility.showToast(ReversePickupCancelActivity.this, Constants.UNKNOWN_ERROR_MESSAGE);
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utility.showToast(ReversePickupCancelActivity.this, Constants.UNKNOWN_ERROR_MESSAGE + ". Please try after some time.");
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

    private void performNonDeliveredOperation() {
        String totalDynamicItem = "<lstSKUStatus>";
        String _statusValue = "";
        if(reverse_pickup_dynamic_sku!=null){
            for(int i = 0; i<reverse_pickup_dynamic_sku.getChildCount();i++){
                totalDynamicItem = totalDynamicItem + "<SKUStatus>";
                if(reverse_pickup_dynamic_sku.getChildAt(i)!=null&&reverse_pickup_dynamic_sku.getChildAt(i) instanceof LinearLayout){
                    LinearLayout linearLayout = (LinearLayout) reverse_pickup_dynamic_sku.getChildAt(i);
                    if(linearLayout!=null&&linearLayout.getChildCount()>0){
                        for(int j=0;j<linearLayout.getChildCount();j++){
                            if(linearLayout.getChildAt(j)!=null&&linearLayout.getChildAt(j) instanceof EditText){
                                EditText editText = (EditText) linearLayout.getChildAt(j);
                                String s1 = "<SKU_ID>"+editText.getText()+"</SKU_ID>";
                                totalDynamicItem = totalDynamicItem + s1;
                            }else if(linearLayout.getChildAt(j)!=null&&linearLayout.getChildAt(j) instanceof Spinner){
                                Spinner spinner = (Spinner) linearLayout.getChildAt(j);
                                String spinnerType = spinner.getTag().toString();
                                String spinnerSelectedItem = spinner.getSelectedItem().toString();
                                if(spinnerType!=null){

                                    if(spinnerType.equalsIgnoreCase("status")){
                                        String s1 = "<Status>"+spinnerSelectedItem+"</Status>";
                                        _statusValue = spinnerSelectedItem;
                                        Log.i("MAD","11>> "+_statusValue);
                                        totalDynamicItem = totalDynamicItem + s1;
                                    }else if(spinnerType.equalsIgnoreCase("reason_id")){
                                        String tempId = ((NonDelivery)spinner.getSelectedItem()).getNDRReasonID();
                                        Log.i("MAD","22>> "+_statusValue);
                                        if(_statusValue.equalsIgnoreCase("pick")){
                                            tempId = "00000000-0000-0000-0000-000000000000";
                                        }
                                        String s1 = "<ReasonID>"+tempId+"</ReasonID>";
                                        totalDynamicItem = totalDynamicItem + s1;
                                    }
                                }
                            }
                        }
                    }
                }
                totalDynamicItem = totalDynamicItem + "</SKUStatus>";
            }
            totalDynamicItem = totalDynamicItem + "</lstSKUStatus>";
        }
//        Utility.showAlertDialog(ReversePickupCancelActivity.this,"Title",totalDynamicItem);
        String awbNo = "0";
        String userId = Utility.getFromSharedPrefs(ReversePickupCancelActivity.this, "USER_ID");
        if (userId != null && userId.length() > 0) {
            remarks = "";
            if(reverse_fail_remarks!=null&&reverse_fail_remarks.getText()!=null&&reverse_fail_remarks.getText().toString()!=null&&reverse_fail_remarks.getText().toString().length()>0){
                remarks = reverse_fail_remarks.getText().toString();
            }
            String statusDate = "";
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Calendar cal = Calendar.getInstance();
            statusDate = dateFormat.format(cal.getTime());

            if(radioGroup.getCheckedRadioButtonId() == R.id.update_reverse_picked || radioGroup.getCheckedRadioButtonId() == R.id.update_reverse_full_picked){
                if(reverse_fail_awb!=null&&reverse_fail_awb.getText()!=null&&reverse_fail_awb.getText().toString()!=null&&reverse_fail_awb.getText().toString().length()>0){
                    awbNo = reverse_fail_awb.getText().toString();
                }else{
                    Utility.showToast(ReversePickupCancelActivity.this, "Enter AWB Number.");
                    return;
                }
            }else if((radioGroup.getCheckedRadioButtonId() == R.id.update_reverse_unpicked) || (radioGroup.getCheckedRadioButtonId() == R.id.update_reverse_cancel)) {
                if(reverse_fail_awb!=null&&reverse_fail_awb.getText()!=null&&reverse_fail_awb.getText().toString()!=null&&reverse_fail_awb.getText().toString().length()>0){
                    awbNo = reverse_fail_awb.getText().toString();
                }
            }

            String xml3 = "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                    "  <soap:Body>\n" +
                    "    <PostPickupStatus_M xmlns=\"http://tempuri.org/\">\n" +
                    "      <objPickupStatus>\n" +
                    "        <UserID>" + userId + "</UserID>\n" +
                    "        <TicketNo>"+mTicketId+"</TicketNo>\n" +
                    "        <StatusDate>" + statusDate + "</StatusDate>\n" +
                    "        <AWBNo>"+ awbNo +"</AWBNo>\n" +
                    "        <Remarks>" + remarks + "</Remarks>\n" + totalDynamicItem +
                    "        <IsTesting>0</IsTesting>\n" +
                    "      </objPickupStatus>\n" +
                    "    </PostPickupStatus_M>\n" +
                    "</soap:Body>\n" +
                    "</soap:Envelope>";
            Log.i("MAD",xml3);
//            Utility.showAlertDialog(ReversePickupCancelActivity.this,"Title",xml3);
            new CallWebService().execute(xml3);
        }else{
            finish();
            Utility.showToast(ReversePickupCancelActivity.this, "User id not found.");
        }

    }



    class CallWebService extends AsyncTask<String, Void, String> {
        @Override
        protected void onPostExecute(String s) {
            Log.i("MAD",s);
//            Utility.showAlertDialog(ReversePickupCancelActivity.this,"Title",s);
            parseResponseXML(s);
//            Utility.showNormalAlertDialog(ReversePickupCancelActivity.this,"Title",s);
        }

        @Override
        protected String doInBackground(String... params) {
            String result = "";
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(Constants.BASE_URL+"?op=PostPickupStatus_M");
            SoapSerializationEnvelope envelope =  new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.encodingStyle = SoapEnvelope.ENC;
            /*String xml2 = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                    "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                    "  <soap:Body>\n" +
                    "    <PostPickupStatus_M xmlns=\"http://tempuri.org/\">\n" +
                    "      <objPickupStatus>\n" +
                    "        <UserID>31231231279387128937192</UserID>\n" +
                    "        <TicketNo>long</TicketNo>\n" +
                    "        <StatusDate>dateTime</StatusDate>\n" +
                    "        <AWBNo>long</AWBNo>\n" +
                    "        <Remarks>string</Remarks>\n" +
                    "        <lstSKUStatus>\n" +
                    "          <SKUStatus>\n" +
                    "            <SKU_ID>string</SKU_ID>\n" +
                    "            <Status>string</Status>\n" +
                    "            <ReasonID>guid</ReasonID>\n" +
                    "          </SKUStatus>\n" +
                    "          <SKUStatus>\n" +
                    "            <SKU_ID>string</SKU_ID>\n" +
                    "            <Status>string</Status>\n" +
                    "            <ReasonID>guid</ReasonID>\n" +
                    "          </SKUStatus>\n" +
                    "        </lstSKUStatus>\n" +
                    "        <IsTesting>int</IsTesting>\n" +
                    "      </objPickupStatus>\n" +
                    "    </PostPickupStatus_M>\n" +
                    "  </soap:Body>\n" +
                    "</soap:Envelope>";*/
            StringEntity se = null;
            try {
                se = new StringEntity(params[0], HTTP.UTF_8);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            se.setContentType("text/xml");
            httpPost.addHeader(SOAP_ACTION, NAMESPACE + "DataSyncWebApi.asmx");
            httpPost.setEntity(se);
            HttpResponse httpResponse = null;
            try {
                httpResponse = httpClient.execute(httpPost);
            } catch (IOException e) {
                e.printStackTrace();
            }
            HttpEntity resEntity = httpResponse.getEntity();
            try {
                String xml = EntityUtils.toString(resEntity);
                result = xml;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }
    }

}


