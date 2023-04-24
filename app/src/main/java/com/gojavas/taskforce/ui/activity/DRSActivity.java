package com.gojavas.taskforce.ui.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

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

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Lenovo on 10/23/2016.
 */
public class DRSActivity extends AppCompatActivity implements View.OnClickListener{

    private ListView mDocketListView,mPendingListView,mBulkListView;
    private TextView mPendingTextView, mSequencedTextView,mTabType;
    private Button mDoneButton,mDRSTABDRS,mDRSTABBUIK,mDRSTABPENDING,mDRSNonDeleveryBtn,mDRSDeleveryBtn;
    private LinearLayout mBulkListControl;

//    drs_tab_pending

    private SequenceDocketListAdapter mSequenceDocketListAdapter;
    private ArrayList<DrsEntity> mDocketList = new ArrayList<>();
    private ArrayList<DrsEntity> mSequencedDocketList = new ArrayList<>();
    private ArrayList<JobSetting> mJobSettingList = new ArrayList<>();
    private int mTotalCount, mPendingCount, mSequencedCount;
    ProgressDialog progressDialog;
    ArrayList<DrsEntity> drsEntities;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dsr);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("DRS LIST");
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
                        }else if (name.equalsIgnoreCase("ImageRequired")){
                            dsr.setImageRequired(parser.nextText());
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

    private void fetchDSRList(){
        final String boyId = Utility.getFromSharedPrefs(DRSActivity.this, "BOY_ID");
        if(boyId!=null&&boyId.length()>0){
            showProgrss("Please Wait...");
            RequestQueue queue = Volley.newRequestQueue(this);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://c2p.gojavas.net/DataSyncWebApi.asmx/GetDRSData",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            /*response = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                                    "<ArrayOfDRS xmlns=\"http://tempuri.org/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
                                    "   <script id=\"tinyhippos-injected\" />\n" +
                                    "   <DRS>\n" +
                                    "      <DOCKETNO>20000100768</DOCKETNO>\n" +
                                    "      <DRSNO>TPR0017762</DRSNO>\n" +
                                    "      <CSGENM>MANONMANI-FALSE</CSGENM>\n" +
                                    "      <CSGEADDR>NEW PEACOCK INTERNATIONAL 52 PUTHU THOTTAM PALLADAM ROAD TIRUPUR</CSGEADDR>\n" +
                                    "      <ADDRESS_TYPE />\n" +
                                    "      <PICKUP_LOCATION />\n" +
                                    "      <TIMETOEND />\n" +
                                    "      <CSGETELENO>9578533883</CSGETELENO>\n" +
                                    "      <ALTERNATE_NUMBER>9597680920</ALTERNATE_NUMBER>\n" +
                                    "      <CTR_NO />\n" +
                                    "      <CSGECITY>Tiruppur</CSGECITY>\n" +
                                    "      <REASSIGN_DESTCD />\n" +
                                    "      <CSGEPINCODE>641604</CSGEPINCODE>\n" +
                                    "      <PKGSNO>1</PKGSNO>\n" +
                                    "      <COD_DOD>POSTPAID</COD_DOD>\n" +
                                    "      <COD_AMT>306.00</COD_AMT>\n" +
                                    "      <DELIVERED />\n" +
                                    "      <DRSUPDATED />\n" +
                                    "      <LOGISTIC_DT />\n" +
                                    "      <LOGISTIC_TIME />\n" +
                                    "      <START_KM />\n" +
                                    "      <DRIVER_NAME>A.VELAN</DRIVER_NAME>\n" +
                                    "      <DRIVER_ID />\n" +
                                    "      <USERID>TPR</USERID>\n" +
                                    "      <CLIENT_CODE>GJ001</CLIENT_CODE>\n" +
                                    "      <CLIENT_NAME>SNAPDEAL</CLIENT_NAME>\n" +
                                    "      <NEXTATTEMPTDATE />\n" +
                                    "      <PRODCD>POSTPAID</PRODCD>\n" +
                                    "      <CODEDESC />\n" +
                                    "      <AMOUNT_TO_CUTOMER>306.00</AMOUNT_TO_CUTOMER>\n" +
                                    "      <ENTRYDATE>28/11/2016</ENTRYDATE>\n" +
                                    "      <ENTRYBY>TPR</ENTRYBY>\n" +
                                    "      <RESPONSE_DATETIME />\n" +
                                    "      <CHOICEOFPAYMENT />\n" +
                                    "      <DATE>28/11/2016</DATE>\n" +
                                    "      <CONTACT_PERSON />\n" +
                                    "      <ImageRequired>false</ImageRequired>\n" +
                                    "   </DRS>\n" +
                                    "   <DRS>\n" +
                                    "      <DOCKETNO>20000114877</DOCKETNO>\n" +
                                    "      <DRSNO>AHDM0017775</DRSNO>\n" +
                                    "      <CSGENM>Parth Parmar-TRUE</CSGENM>\n" +
                                    "      <CSGEADDR>B 28 uttam nagar nv patel school danilimda ahmedabad</CSGEADDR>\n" +
                                    "      <ADDRESS_TYPE />\n" +
                                    "      <PICKUP_LOCATION />\n" +
                                    "      <TIMETOEND />\n" +
                                    "      <CSGETELENO>7383937280</CSGETELENO>\n" +
                                    "      <ALTERNATE_NUMBER>7383937280</ALTERNATE_NUMBER>\n" +
                                    "      <CTR_NO />\n" +
                                    "      <CSGECITY>AHMEDABAD MANINAGAR</CSGECITY>\n" +
                                    "      <REASSIGN_DESTCD />\n" +
                                    "      <CSGEPINCODE>380026</CSGEPINCODE>\n" +
                                    "      <PKGSNO>1</PKGSNO>\n" +
                                    "      <COD_DOD>POSTPAID</COD_DOD>\n" +
                                    "      <COD_AMT>799.00</COD_AMT>\n" +
                                    "      <DELIVERED />\n" +
                                    "      <DRSUPDATED />\n" +
                                    "      <LOGISTIC_DT />\n" +
                                    "      <LOGISTIC_TIME />\n" +
                                    "      <START_KM />\n" +
                                    "      <DRIVER_NAME>PRAVIN G SHUKLA</DRIVER_NAME>\n" +
                                    "      <DRIVER_ID />\n" +
                                    "      <USERID>SANJAY PATEL</USERID>\n" +
                                    "      <CLIENT_CODE>GJ743</CLIENT_CODE>\n" +
                                    "      <CLIENT_NAME>SUPERHIT BAZAAR</CLIENT_NAME>\n" +
                                    "      <NEXTATTEMPTDATE />\n" +
                                    "      <PRODCD>POSTPAID</PRODCD>\n" +
                                    "      <CODEDESC />\n" +
                                    "      <AMOUNT_TO_CUTOMER>799.00</AMOUNT_TO_CUTOMER>\n" +
                                    "      <ENTRYDATE>28/11/2016</ENTRYDATE>\n" +
                                    "      <ENTRYBY>SANJAY PATEL</ENTRYBY>\n" +
                                    "      <RESPONSE_DATETIME />\n" +
                                    "      <CHOICEOFPAYMENT />\n" +
                                    "      <DATE>28/11/2016</DATE>\n" +
                                    "      <CONTACT_PERSON />\n" +
                                    "      <ImageRequired>true</ImageRequired>\n" +
                                    "   </DRS>\n" +
                                    "   <DRS>\n" +
                                    "      <DOCKETNO>40000032472</DOCKETNO>\n" +
                                    "      <DRSNO>JGA0017561</DRSNO>\n" +
                                    "      <CSGENM>Priya Punjani</CSGENM>\n" +
                                    "      <CSGEADDR>purusharth Krishna Nagar -2,jamnagar</CSGEADDR>\n" +
                                    "      <ADDRESS_TYPE />\n" +
                                    "      <PICKUP_LOCATION />\n" +
                                    "      <TIMETOEND />\n" +
                                    "      <CSGETELENO>9408163316</CSGETELENO>\n" +
                                    "      <ALTERNATE_NUMBER>9408163316</ALTERNATE_NUMBER>\n" +
                                    "      <CTR_NO />\n" +
                                    "      <CSGECITY>JAMNAGAR</CSGECITY>\n" +
                                    "      <REASSIGN_DESTCD />\n" +
                                    "      <CSGEPINCODE>361006</CSGEPINCODE>\n" +
                                    "      <PKGSNO>1</PKGSNO>\n" +
                                    "      <COD_DOD>PREPAID</COD_DOD>\n" +
                                    "      <COD_AMT>0.00</COD_AMT>\n" +
                                    "      <DELIVERED />\n" +
                                    "      <DRSUPDATED />\n" +
                                    "      <LOGISTIC_DT />\n" +
                                    "      <LOGISTIC_TIME />\n" +
                                    "      <START_KM />\n" +
                                    "      <DRIVER_NAME>JAYDEEP RATHOD</DRIVER_NAME>\n" +
                                    "      <DRIVER_ID />\n" +
                                    "      <USERID>JGA</USERID>\n" +
                                    "      <CLIENT_CODE>GJ008</CLIENT_CODE>\n" +
                                    "      <CLIENT_NAME>REWARDME</CLIENT_NAME>\n" +
                                    "      <NEXTATTEMPTDATE />\n" +
                                    "      <PRODCD>PREPAID</PRODCD>\n" +
                                    "      <CODEDESC />\n" +
                                    "      <AMOUNT_TO_CUTOMER>0.00</AMOUNT_TO_CUTOMER>\n" +
                                    "      <ENTRYDATE>28/11/2016</ENTRYDATE>\n" +
                                    "      <ENTRYBY>JGA</ENTRYBY>\n" +
                                    "      <RESPONSE_DATETIME />\n" +
                                    "      <CHOICEOFPAYMENT />\n" +
                                    "      <DATE>28/11/2016</DATE>\n" +
                                    "      <CONTACT_PERSON />\n" +
                                    "   </DRS>\n" +
                                    "   <DRS>\n" +
                                    "      <DOCKETNO>20000720930</DOCKETNO>\n" +
                                    "      <DRSNO>HSHPR0017630</DRSNO>\n" +
                                    "      <CSGENM>sarabjit kumar</CSGENM>\n" +
                                    "      <CSGEADDR>Nare goverment iti piplanwala hoshiarpur</CSGEADDR>\n" +
                                    "      <ADDRESS_TYPE />\n" +
                                    "      <PICKUP_LOCATION />\n" +
                                    "      <TIMETOEND />\n" +
                                    "      <CSGETELENO>8968509382</CSGETELENO>\n" +
                                    "      <ALTERNATE_NUMBER>-</ALTERNATE_NUMBER>\n" +
                                    "      <CTR_NO />\n" +
                                    "      <CSGECITY>Hoshiarpur</CSGECITY>\n" +
                                    "      <REASSIGN_DESTCD />\n" +
                                    "      <CSGEPINCODE>146001</CSGEPINCODE>\n" +
                                    "      <PKGSNO>1</PKGSNO>\n" +
                                    "      <COD_DOD>POSTPAID</COD_DOD>\n" +
                                    "      <COD_AMT>380.00</COD_AMT>\n" +
                                    "      <DELIVERED />\n" +
                                    "      <DRSUPDATED />\n" +
                                    "      <LOGISTIC_DT />\n" +
                                    "      <LOGISTIC_TIME />\n" +
                                    "      <START_KM />\n" +
                                    "      <DRIVER_NAME>VIVEK KUMAR</DRIVER_NAME>\n" +
                                    "      <DRIVER_ID />\n" +
                                    "      <USERID>HSHPR</USERID>\n" +
                                    "      <CLIENT_CODE>GJ001</CLIENT_CODE>\n" +
                                    "      <CLIENT_NAME>SNAPDEAL</CLIENT_NAME>\n" +
                                    "      <NEXTATTEMPTDATE />\n" +
                                    "      <PRODCD>POSTPAID</PRODCD>\n" +
                                    "      <CODEDESC />\n" +
                                    "      <AMOUNT_TO_CUTOMER>380.00</AMOUNT_TO_CUTOMER>\n" +
                                    "      <ENTRYDATE>28/11/2016</ENTRYDATE>\n" +
                                    "      <ENTRYBY>HSHPR</ENTRYBY>\n" +
                                    "      <RESPONSE_DATETIME />\n" +
                                    "      <CHOICEOFPAYMENT />\n" +
                                    "      <DATE>28/11/2016</DATE>\n" +
                                    "      <CONTACT_PERSON />\n" +
                                    "   </DRS>\n" +
                                    "   <DRS>\n" +
                                    "      <DOCKETNO>20000730139</DOCKETNO>\n" +
                                    "      <DRSNO>TPR0017762</DRSNO>\n" +
                                    "      <CSGENM>ganesh</CSGENM>\n" +
                                    "      <CSGEADDR>Canimar exports Nehru street Ring road Tirupur .</CSGEADDR>\n" +
                                    "      <ADDRESS_TYPE />\n" +
                                    "      <PICKUP_LOCATION />\n" +
                                    "      <TIMETOEND />\n" +
                                    "      <CSGETELENO>8508069422</CSGETELENO>\n" +
                                    "      <ALTERNATE_NUMBER>8508069422</ALTERNATE_NUMBER>\n" +
                                    "      <CTR_NO />\n" +
                                    "      <CSGECITY>Tiruppur</CSGECITY>\n" +
                                    "      <REASSIGN_DESTCD />\n" +
                                    "      <CSGEPINCODE>641652</CSGEPINCODE>\n" +
                                    "      <PKGSNO>1</PKGSNO>\n" +
                                    "      <COD_DOD>POSTPAID</COD_DOD>\n" +
                                    "      <COD_AMT>399.00</COD_AMT>\n" +
                                    "      <DELIVERED />\n" +
                                    "      <DRSUPDATED />\n" +
                                    "      <LOGISTIC_DT />\n" +
                                    "      <LOGISTIC_TIME />\n" +
                                    "      <START_KM />\n" +
                                    "      <DRIVER_NAME>A.VELAN</DRIVER_NAME>\n" +
                                    "      <DRIVER_ID />\n" +
                                    "      <USERID>TPR</USERID>\n" +
                                    "      <CLIENT_CODE>GJ001</CLIENT_CODE>\n" +
                                    "      <CLIENT_NAME>SNAPDEAL</CLIENT_NAME>\n" +
                                    "      <NEXTATTEMPTDATE />\n" +
                                    "      <PRODCD>POSTPAID</PRODCD>\n" +
                                    "      <CODEDESC />\n" +
                                    "      <AMOUNT_TO_CUTOMER>399.00</AMOUNT_TO_CUTOMER>\n" +
                                    "      <ENTRYDATE>28/11/2016</ENTRYDATE>\n" +
                                    "      <ENTRYBY>TPR</ENTRYBY>\n" +
                                    "      <RESPONSE_DATETIME />\n" +
                                    "      <CHOICEOFPAYMENT />\n" +
                                    "      <DATE>28/11/2016</DATE>\n" +
                                    "      <CONTACT_PERSON />\n" +
                                    "   </DRS>\n" +
                                    "   <DRS>\n" +
                                    "      <DOCKETNO>20000728703</DOCKETNO>\n" +
                                    "      <DRSNO>MBNR0017423</DRSNO>\n" +
                                    "      <CSGENM>abdul mujeeb</CSGENM>\n" +
                                    "      <CSGEADDR>8 81 1 mvs college road marlu mahaboobnagar</CSGEADDR>\n" +
                                    "      <ADDRESS_TYPE />\n" +
                                    "      <PICKUP_LOCATION />\n" +
                                    "      <TIMETOEND />\n" +
                                    "      <CSGETELENO>9052604928</CSGETELENO>\n" +
                                    "      <ALTERNATE_NUMBER>-</ALTERNATE_NUMBER>\n" +
                                    "      <CTR_NO />\n" +
                                    "      <CSGECITY>Mahboobnagar</CSGECITY>\n" +
                                    "      <REASSIGN_DESTCD />\n" +
                                    "      <CSGEPINCODE>509001</CSGEPINCODE>\n" +
                                    "      <PKGSNO>1</PKGSNO>\n" +
                                    "      <COD_DOD>POSTPAID</COD_DOD>\n" +
                                    "      <COD_AMT>51.00</COD_AMT>\n" +
                                    "      <DELIVERED />\n" +
                                    "      <DRSUPDATED />\n" +
                                    "      <LOGISTIC_DT />\n" +
                                    "      <LOGISTIC_TIME />\n" +
                                    "      <START_KM />\n" +
                                    "      <DRIVER_NAME>P.SRINUVASULU</DRIVER_NAME>\n" +
                                    "      <DRIVER_ID />\n" +
                                    "      <USERID>MBNR</USERID>\n" +
                                    "      <CLIENT_CODE>GJ001</CLIENT_CODE>\n" +
                                    "      <CLIENT_NAME>SNAPDEAL</CLIENT_NAME>\n" +
                                    "      <NEXTATTEMPTDATE />\n" +
                                    "      <PRODCD>POSTPAID</PRODCD>\n" +
                                    "      <CODEDESC />\n" +
                                    "      <AMOUNT_TO_CUTOMER>51.00</AMOUNT_TO_CUTOMER>\n" +
                                    "      <ENTRYDATE>28/11/2016</ENTRYDATE>\n" +
                                    "      <ENTRYBY>MBNR</ENTRYBY>\n" +
                                    "      <RESPONSE_DATETIME />\n" +
                                    "      <CHOICEOFPAYMENT />\n" +
                                    "      <DATE>28/11/2016</DATE>\n" +
                                    "      <CONTACT_PERSON />\n" +
                                    "   </DRS>\n" +
                                    "   <DRS>\n" +
                                    "      <DOCKETNO>20000734115</DOCKETNO>\n" +
                                    "      <DRSNO>BHMP0017460</DRSNO>\n" +
                                    "      <CSGENM>jdhdshdh</CSGENM>\n" +
                                    "      <CSGEADDR>jsbshdgshdgdvdv gdhdhdhhdhdhdhdh hdhdhdbdbdjsjs</CSGEADDR>\n" +
                                    "      <ADDRESS_TYPE />\n" +
                                    "      <PICKUP_LOCATION />\n" +
                                    "      <TIMETOEND />\n" +
                                    "      <CSGETELENO>8436431770</CSGETELENO>\n" +
                                    "      <ALTERNATE_NUMBER>8436431770</ALTERNATE_NUMBER>\n" +
                                    "      <CTR_NO />\n" +
                                    "      <CSGECITY>Berhampore</CSGECITY>\n" +
                                    "      <REASSIGN_DESTCD />\n" +
                                    "      <CSGEPINCODE>742101</CSGEPINCODE>\n" +
                                    "      <PKGSNO>1</PKGSNO>\n" +
                                    "      <COD_DOD>POSTPAID</COD_DOD>\n" +
                                    "      <COD_AMT>529.00</COD_AMT>\n" +
                                    "      <DELIVERED />\n" +
                                    "      <DRSUPDATED />\n" +
                                    "      <LOGISTIC_DT />\n" +
                                    "      <LOGISTIC_TIME />\n" +
                                    "      <START_KM />\n" +
                                    "      <DRIVER_NAME>PRITAM PAL</DRIVER_NAME>\n" +
                                    "      <DRIVER_ID />\n" +
                                    "      <USERID>BHMP</USERID>\n" +
                                    "      <CLIENT_CODE>GJ001</CLIENT_CODE>\n" +
                                    "      <CLIENT_NAME>SNAPDEAL</CLIENT_NAME>\n" +
                                    "      <NEXTATTEMPTDATE />\n" +
                                    "      <PRODCD>POSTPAID</PRODCD>\n" +
                                    "      <CODEDESC />\n" +
                                    "      <AMOUNT_TO_CUTOMER>529.00</AMOUNT_TO_CUTOMER>\n" +
                                    "      <ENTRYDATE>28/11/2016</ENTRYDATE>\n" +
                                    "      <ENTRYBY>BHMP</ENTRYBY>\n" +
                                    "      <RESPONSE_DATETIME />\n" +
                                    "      <CHOICEOFPAYMENT />\n" +
                                    "      <DATE>28/11/2016</DATE>\n" +
                                    "      <CONTACT_PERSON />\n" +
                                    "   </DRS>\n" +
                                    "   <DRS>\n" +
                                    "      <DOCKETNO>20000726751</DOCKETNO>\n" +
                                    "      <DRSNO>MHL0017419</DRSNO>\n" +
                                    "      <CSGENM>ajoy irungbam</CSGENM>\n" +
                                    "      <CSGEADDR>Doaba college boys hostel</CSGEADDR>\n" +
                                    "      <ADDRESS_TYPE />\n" +
                                    "      <PICKUP_LOCATION />\n" +
                                    "      <TIMETOEND />\n" +
                                    "      <CSGETELENO>9878805271</CSGETELENO>\n" +
                                    "      <ALTERNATE_NUMBER>-</ALTERNATE_NUMBER>\n" +
                                    "      <CTR_NO />\n" +
                                    "      <CSGECITY>Kharar</CSGECITY>\n" +
                                    "      <REASSIGN_DESTCD />\n" +
                                    "      <CSGEPINCODE>140301</CSGEPINCODE>\n" +
                                    "      <PKGSNO>1</PKGSNO>\n" +
                                    "      <COD_DOD>POSTPAID</COD_DOD>\n" +
                                    "      <COD_AMT>374.00</COD_AMT>\n" +
                                    "      <DELIVERED />\n" +
                                    "      <DRSUPDATED />\n" +
                                    "      <LOGISTIC_DT />\n" +
                                    "      <LOGISTIC_TIME />\n" +
                                    "      <START_KM />\n" +
                                    "      <DRIVER_NAME>KARAMPAL</DRIVER_NAME>\n" +
                                    "      <DRIVER_ID />\n" +
                                    "      <USERID>MHL</USERID>\n" +
                                    "      <CLIENT_CODE>GJ001</CLIENT_CODE>\n" +
                                    "      <CLIENT_NAME>SNAPDEAL</CLIENT_NAME>\n" +
                                    "      <NEXTATTEMPTDATE />\n" +
                                    "      <PRODCD>POSTPAID</PRODCD>\n" +
                                    "      <CODEDESC />\n" +
                                    "      <AMOUNT_TO_CUTOMER>374.00</AMOUNT_TO_CUTOMER>\n" +
                                    "      <ENTRYDATE>28/11/2016</ENTRYDATE>\n" +
                                    "      <ENTRYBY>MHL</ENTRYBY>\n" +
                                    "      <RESPONSE_DATETIME />\n" +
                                    "      <CHOICEOFPAYMENT />\n" +
                                    "      <DATE>28/11/2016</DATE>\n" +
                                    "      <CONTACT_PERSON />\n" +
                                    "   </DRS>\n" +
                                    "   <DRS>\n" +
                                    "      <DOCKETNO>20000735448</DOCKETNO>\n" +
                                    "      <DRSNO>NLDA0017359</DRSNO>\n" +
                                    "      <CSGENM>upender</CSGENM>\n" +
                                    "      <CSGEADDR>Ashok nagar old city nalgonda</CSGEADDR>\n" +
                                    "      <ADDRESS_TYPE />\n" +
                                    "      <PICKUP_LOCATION />\n" +
                                    "      <TIMETOEND />\n" +
                                    "      <CSGETELENO>9505919186</CSGETELENO>\n" +
                                    "      <ALTERNATE_NUMBER>-</ALTERNATE_NUMBER>\n" +
                                    "      <CTR_NO />\n" +
                                    "      <CSGECITY>Nalgonda</CSGECITY>\n" +
                                    "      <REASSIGN_DESTCD />\n" +
                                    "      <CSGEPINCODE>508001</CSGEPINCODE>\n" +
                                    "      <PKGSNO>1</PKGSNO>\n" +
                                    "      <COD_DOD>POSTPAID</COD_DOD>\n" +
                                    "      <COD_AMT>499.00</COD_AMT>\n" +
                                    "      <DELIVERED />\n" +
                                    "      <DRSUPDATED />\n" +
                                    "      <LOGISTIC_DT />\n" +
                                    "      <LOGISTIC_TIME />\n" +
                                    "      <START_KM />\n" +
                                    "      <DRIVER_NAME>BELLI SURYA RAJ YADAV</DRIVER_NAME>\n" +
                                    "      <DRIVER_ID />\n" +
                                    "      <USERID>NLDA</USERID>\n" +
                                    "      <CLIENT_CODE>GJ001</CLIENT_CODE>\n" +
                                    "      <CLIENT_NAME>SNAPDEAL</CLIENT_NAME>\n" +
                                    "      <NEXTATTEMPTDATE />\n" +
                                    "      <PRODCD>POSTPAID</PRODCD>\n" +
                                    "      <CODEDESC />\n" +
                                    "      <AMOUNT_TO_CUTOMER>499.00</AMOUNT_TO_CUTOMER>\n" +
                                    "      <ENTRYDATE>28/11/2016</ENTRYDATE>\n" +
                                    "      <ENTRYBY>NLDA</ENTRYBY>\n" +
                                    "      <RESPONSE_DATETIME />\n" +
                                    "      <CHOICEOFPAYMENT />\n" +
                                    "      <DATE>28/11/2016</DATE>\n" +
                                    "      <CONTACT_PERSON />\n" +
                                    "   </DRS>\n" +
                                    "   <DRS>\n" +
                                    "      <DOCKETNO>20000730693</DOCKETNO>\n" +
                                    "      <DRSNO>GOIN0017807</DRSNO>\n" +
                                    "      <CSGENM>philomena fernandes</CSGENM>\n" +
                                    "      <CSGEADDR>Saunta vaddo calangute bardez goa</CSGEADDR>\n" +
                                    "      <ADDRESS_TYPE />\n" +
                                    "      <PICKUP_LOCATION />\n" +
                                    "      <TIMETOEND />\n" +
                                    "      <CSGETELENO>9881180603</CSGETELENO>\n" +
                                    "      <ALTERNATE_NUMBER>-</ALTERNATE_NUMBER>\n" +
                                    "      <CTR_NO />\n" +
                                    "      <CSGECITY>Panaji</CSGECITY>\n" +
                                    "      <REASSIGN_DESTCD />\n" +
                                    "      <CSGEPINCODE>403516</CSGEPINCODE>\n" +
                                    "      <PKGSNO>1</PKGSNO>\n" +
                                    "      <COD_DOD>POSTPAID</COD_DOD>\n" +
                                    "      <COD_AMT>699.00</COD_AMT>\n" +
                                    "      <DELIVERED />\n" +
                                    "      <DRSUPDATED />\n" +
                                    "      <LOGISTIC_DT />\n" +
                                    "      <LOGISTIC_TIME />\n" +
                                    "      <START_KM />\n" +
                                    "      <DRIVER_NAME>SHAILESH YESHWANT MANDREKAR</DRIVER_NAME>\n" +
                                    "      <DRIVER_ID />\n" +
                                    "      <USERID>GOIN</USERID>\n" +
                                    "      <CLIENT_CODE>GJ001</CLIENT_CODE>\n" +
                                    "      <CLIENT_NAME>SNAPDEAL</CLIENT_NAME>\n" +
                                    "      <NEXTATTEMPTDATE />\n" +
                                    "      <PRODCD>POSTPAID</PRODCD>\n" +
                                    "      <CODEDESC />\n" +
                                    "      <AMOUNT_TO_CUTOMER>699.00</AMOUNT_TO_CUTOMER>\n" +
                                    "      <ENTRYDATE>28/11/2016</ENTRYDATE>\n" +
                                    "      <ENTRYBY>GOIN</ENTRYBY>\n" +
                                    "      <RESPONSE_DATETIME />\n" +
                                    "      <CHOICEOFPAYMENT />\n" +
                                    "      <DATE>28/11/2016</DATE>\n" +
                                    "      <CONTACT_PERSON />\n" +
                                    "   </DRS>\n" +
                                    "</ArrayOfDRS>";*/
                            hideProgrss();
                            XmlPullParserFactory pullParserFactory;
                            try {
                                pullParserFactory = XmlPullParserFactory.newInstance();
                                XmlPullParser parser = pullParserFactory.newPullParser();
                                InputStream in_s = new ByteArrayInputStream(response.getBytes("UTF-8"));
                                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                                parser.setInput(in_s, null);
                                drsEntities = parseXML(parser);
                                mSequenceDocketListAdapter = new SequenceDocketListAdapter(DRSActivity.this, drsEntities, mJobSettingList, "Sequence");
                                mDocketListView.setAdapter(mSequenceDocketListAdapter);
                                mDocketListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> a, View v, int position,
                                                            long id) {
                                        Intent intent = new Intent(DRSActivity.this, DRSDetailActivity.class);
                                        intent.putExtra("selectedDrsEntities", drsEntities.get(position));
                                        startActivity(intent);
                                    }
                                });

                                SequenceDocketListAdapter mBulkListAdapter = new SequenceDocketListAdapter(DRSActivity.this, drsEntities, mJobSettingList, "Bulk");
                                mBulkListView.setAdapter(mBulkListAdapter);
//                                mBulkListView.setOnItemClickListener();

                            } catch (XmlPullParserException e) {
                                finish();
                                Utility.showToast(DRSActivity.this, Constants.UNKNOWN_ERROR_MESSAGE);
                                e.printStackTrace();
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                finish();
                                Utility.showToast(DRSActivity.this, Constants.UNKNOWN_ERROR_MESSAGE);
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    finish();
                    Utility.showToast(DRSActivity.this, Constants.UNKNOWN_ERROR_MESSAGE + ". Please try after some time.");
                    hideProgrss();
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
        }else{
            finish();
            Utility.showToast(DRSActivity.this, Constants.UNKNOWN_ERROR_MESSAGE + ". Please try after some time.");
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        progressDialog = new ProgressDialog(DRSActivity.this);
        registerViews();
    }

    /**
     * Register all the views
     */
    private void registerViews() {
        mTabType = (TextView) findViewById(R.id.tabType);
        mDocketListView = (ListView) findViewById(R.id.sequence_docket_list_listview);
        mPendingListView = (ListView) findViewById(R.id.pending_list_listview);
        mBulkListView = (ListView) findViewById(R.id.bulk_list_listview);
        mPendingTextView = (TextView) findViewById(R.id.sequence_pending_textview);
        mSequencedTextView = (TextView) findViewById(R.id.sequence_sequenced_textview);
        mDoneButton = (Button) findViewById(R.id.sequence_done_button);
        mDRSTABPENDING = (Button) findViewById(R.id.drs_tab_pending);
        mDRSTABBUIK = (Button) findViewById(R.id.drs_tab_bulk);
        mDRSTABDRS = (Button) findViewById(R.id.drs_tab_drs);
        mBulkListControl = (LinearLayout) findViewById(R.id.bulk_list_control);
        registerTabs();

        mDRSDeleveryBtn = (Button) findViewById(R.id.drs_delivered_button);
        mDRSNonDeleveryBtn = (Button) findViewById(R.id.drs_non_delivered_button);
        mDRSDeleveryBtn.setOnClickListener(this);
        mDRSNonDeleveryBtn.setOnClickListener(this);
        fetchDSRList();
//        DrsEntity drsEntity1 = new DrsEntity();
//        drsEntity1.setdocketno("1");
//        DrsEntity drsEntity2 = new DrsEntity();
//        drsEntity2.setdocketno("2");
//        mDocketList.add(drsEntity1);
//        mDocketList.add(drsEntity2);
//        mSequenceDocketListAdapter = new SequenceDocketListAdapter(DRSActivity.this, mDocketList, mJobSettingList, "Sequence");
//        mDocketListView.setAdapter(mSequenceDocketListAdapter);

//        updateCount();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.drs_non_delivered_button:
                performNonDeliveredOperation();
                break;
            case R.id.drs_delivered_button:
                performDeliveredBulkOperation();
                break;
            default:
                break;
        }
    }

    private void codDialogBox(final float _totalCODAmount){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(DRSActivity.this);
        alertDialog.setTitle("COD Amount");
        alertDialog.setMessage("Received COD Amount");

        final EditText input = new EditText(DRSActivity.this);
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
                            Utility.showToast(DRSActivity.this, "Enter valid COD value.");
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
        Intent intent = new Intent(DRSActivity.this, DRSPassActivity.class);
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
        Intent intent = new Intent(DRSActivity.this, DRSFailActivity.class);
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
        final Dialog dialog = new Dialog(DRSActivity.this, R.style.customDialogTheme);
        dialog.setContentView(R.layout.dialog_resequence);
        Button okButton = (Button) dialog.findViewById(R.id.dialog_ok_button);
        Button cancelButton = (Button) dialog.findViewById(R.id.dialog_cancel_button);

        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = Utility.getMetricsWidth(DRSActivity.this);
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
