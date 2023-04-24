package com.gojavas.taskforce.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
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
import com.gojavas.taskforce.adapter.PickupListAdapter;
import com.gojavas.taskforce.entity.JobSetting;
import com.gojavas.taskforce.entity.ReversePickup;
import com.gojavas.taskforce.utils.Constants;
import com.gojavas.taskforce.utils.Utility;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by MadanS on 4/8/2017.
 */
public class PickupAlertActivity extends AppCompatActivity {

    private ListView mDocketListView;
    private TextView mPendingTextView, mSequencedTextView;
    private Button mDoneButton;

    private PickupListAdapter mPickupListAdapter;
    private ArrayList<ReversePickup> mDocketList = new ArrayList<>();
    private ArrayList<ReversePickup> mSequencedDocketList = new ArrayList<>();
    private ArrayList<JobSetting> mJobSettingList = new ArrayList<>();
    private int mTotalCount, mPendingCount, mSequencedCount;
    ProgressDialog progressDialog;
    ArrayList<ReversePickup> reversePickups;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pickup_alert);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Reverse Pickups");
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

    private ArrayList<String> readSKUId(XmlPullParser parser,ArrayList<String> parsedResult) throws IOException, XmlPullParserException {
        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG) {
                String name = parser.getName();
                if (name == null) {
                    continue;
                }
                if (name.equals("string")) {
                    parsedResult.add(parser.nextText());
                }
            }
            eventType = parser.next();
        }
        return parsedResult;
    }

    private ArrayList<ReversePickup> parseXML(XmlPullParser parser) throws XmlPullParserException,IOException
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
//                            Toast.makeText(PickupAlertActivity.this,"=="+dsr.getTicketNo(),Toast.LENGTH_LONG).show();
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
                        }else if (name.equalsIgnoreCase("SKU_ID")){
                            ArrayList<String> skus = new ArrayList<String>();
                            skus.add("1");
                            skus.add("2");
                            skus.add("3");
                            skus.add("4");
                            dsr.setSKU_ID(skus);
//                            dsr.setSKU_ID(readSKUId(parser,skus));
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


    private static String getValue(String tag, Element element) {
        String value = "";
        try{
            NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
            Node node = nodeList.item(0);
            if(node.getNodeValue()!=null&&node.getNodeValue().length()>0){
                value = node.getNodeValue();
            }
        }catch (Exception e){

        }
        return value;
    }

    private ArrayList<ReversePickup>  parseReversPickUpListXML(String xml){
        ArrayList<ReversePickup> dsrs = null;
        dsrs = new ArrayList();
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(new InputSource(new StringReader(xml)));
            Element element=doc.getDocumentElement();

            element.normalize();
            NodeList nList = doc.getElementsByTagName("ReversePickup");
            for (int i=0; i<nList.getLength(); i++) {
                Node node = nList.item(i);
                ReversePickup reversePickup = new ReversePickup();
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element2 = (Element) node;
                    reversePickup.setProcessID(getValue("ProcessID", element2));
                    reversePickup.setTicketNo(getValue("TicketNo", element2));
                    reversePickup.setPickupPersonName(getValue("PickupPersonName", element2));
                    reversePickup.setPickupAddress(getValue("PickupAddress", element2));
                    reversePickup.setPickupPincode(getValue("PickupPincode", element2));
                    reversePickup.setPickupCity(getValue("PickupCity", element2));
                    reversePickup.setPickupPhone(getValue("PickupPhone", element2));
                    reversePickup.setPickupMobile(getValue("PickupMobile", element2));
                    reversePickup.setPickupEmail(getValue("PickupEmail", element2));
                    reversePickup.setContents(getValue("Contents", element2));
                    reversePickup.setProduct(getValue("Product", element2));
                    String heightTmp = getValue("Height", element2);
                    reversePickup.setHeight(0.0);
                    if(heightTmp!=null&&heightTmp.length()>0){
                        try{
                            Double aDouble =  Double.parseDouble(heightTmp);
                            reversePickup.setHeight(aDouble);
                        }catch (Exception e){}

                    }

                    String lengthTmp = getValue("Length", element2);
                    reversePickup.setLength(0.0);
                    if(lengthTmp!=null&&lengthTmp.length()>0){
                        try{
                            Double aDouble =  Double.parseDouble(lengthTmp);
                            reversePickup.setLength(aDouble);
                        }catch (Exception e){}

                    }

                    String widthTmp = getValue("Width", element2);
                    reversePickup.setWidth(0.0);
                    if(widthTmp!=null&&widthTmp.length()>0){
                        try{
                            Double aDouble =  Double.parseDouble(widthTmp);
                            reversePickup.setWidth(aDouble);
                        }catch (Exception e){}

                    }


                    String weightTmp = getValue("Weight", element2);
                    reversePickup.setWeight(0.0);
                    if(weightTmp!=null&&weightTmp.length()>0){
                        try{
                            Double aDouble =  Double.parseDouble(weightTmp);
                            reversePickup.setWeight(aDouble);
                        }catch (Exception e){}

                    }


                    String goodsValueTmp = getValue("GoodsValue", element2);
                    reversePickup.setGoodsValue(0.0);
                    if(goodsValueTmp!=null&&goodsValueTmp.length()>0){
                        try{
                            Double aDouble =  Double.parseDouble(goodsValueTmp);
                            reversePickup.setGoodsValue(aDouble);
                        }catch (Exception e){}

                    }

                    String PcsTmp = getValue("Pcs", element2);
                    reversePickup.setPcs(0);
                    if(PcsTmp!=null&&PcsTmp.length()>0){
                        try{
                            Integer integer =  Integer.parseInt(PcsTmp);
                            reversePickup.setPcs(integer);
                        }catch (Exception e){}

                    }

                    reversePickup.setPickupTime(getValue("PickupTime", element2));
                    reversePickup.setReturn_Reason(getValue("Return_Reason", element2));
                    reversePickup.setRemarks(getValue("Remarks", element2));
                    reversePickup.setSyncDateTime(getValue("SyncDateTime", element2));
                    NodeList element21 = element2.getElementsByTagName("string");
                    ArrayList<String> skus = new ArrayList<String>();
                    for (int j=0; j<element21.getLength(); j++) {
                        skus.add(element21.item(j).getFirstChild().getNodeValue());
//                        Toast.makeText(PickupAlertActivity.this,""+element21.item(j).getFirstChild().getNodeValue(),Toast.LENGTH_LONG).show();
                    }
                    if(skus!=null&&skus.size()>0){
                        reversePickup.setSKU_ID(skus);
                    }
                }
                dsrs.add(reversePickup);
            }

        } catch (Exception e) {e.printStackTrace();}

        return dsrs;
    }



    private void fetchReversePickupList(){
        final String boyId = Utility.getFromSharedPrefs(PickupAlertActivity.this, "BOY_ID");
        if(boyId!=null&&boyId.length()>0){
            showProgrss("Please Wait...");
            RequestQueue queue = Volley.newRequestQueue(this);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.BASE_URL+"/GetReversePickup_M",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
//                            response = "<?xml version=\"1.0\" encoding=\"utf-8\"?> <soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"> <soap:Body> <GetReversePickup_MResponse xmlns=\"http://tempuri.org/\"> <GetReversePickup_MResult> <ReversePickup> <ProcessID>guid</ProcessID> <TicketNo>long</TicketNo> <ManifestNo>string</ManifestNo> <PickupPersonName>string</PickupPersonName> <PickupAddress>string</PickupAddress> <PickupPincode>string</PickupPincode> <PickupCity>string</PickupCity> <PickupPhone>string</PickupPhone> <PickupMobile>string</PickupMobile> <PickupEmail>string</PickupEmail> <Contents>string</Contents> <Product>string</Product> <Height>decimal</Height> <Length>decimal</Length> <Width>decimal</Width> <Weight>decimal</Weight> <GoodsValue>decimal</GoodsValue> <Pcs>int</Pcs> <PickupTime>string</PickupTime> <SKU_ID> <string>001</string> <string>002</string> </SKU_ID> <Return_Reason>string</Return_Reason> <Remarks>string</Remarks> <SyncDateTime>string</SyncDateTime> </ReversePickup> <ReversePickup> <ProcessID>guid</ProcessID> <TicketNo>long</TicketNo> <ManifestNo>string</ManifestNo> <PickupPersonName>string</PickupPersonName> <PickupAddress>string</PickupAddress> <PickupPincode>string</PickupPincode> <PickupCity>string</PickupCity> <PickupPhone>string</PickupPhone> <PickupMobile>string</PickupMobile> <PickupEmail>string</PickupEmail> <Contents>string</Contents> <Product>string</Product> <Height>decimal</Height> <Length>decimal</Length> <Width>decimal</Width> <Weight>decimal</Weight> <GoodsValue>decimal</GoodsValue> <Pcs>int</Pcs> <PickupTime>string</PickupTime> <SKU_ID> <string>003</string> <string>004</string> </SKU_ID> <Return_Reason>string</Return_Reason> <Remarks>string</Remarks> <SyncDateTime>string</SyncDateTime> </ReversePickup> </GetReversePickup_MResult> </GetReversePickup_MResponse> </soap:Body> </soap:Envelope>";
                            hideProgrss();
                            XmlPullParserFactory pullParserFactory;
                            try {
                                pullParserFactory = XmlPullParserFactory.newInstance();
                                XmlPullParser parser = pullParserFactory.newPullParser();
                                InputStream in_s = new ByteArrayInputStream(response.getBytes("UTF-8"));
                                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                                parser.setInput(in_s, null);
//                                reversePickups = parseXML(parser);
                                reversePickups = parseReversPickUpListXML(response);
                                mPickupListAdapter = new PickupListAdapter(PickupAlertActivity.this, reversePickups, mJobSettingList, "Sequence");
                                mDocketListView.setAdapter(mPickupListAdapter);
                                mDocketListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> a, View v, int position,
                                                            long id) {
                                        Intent intent = new Intent(PickupAlertActivity.this, PickupDetailActivity.class);
                                        intent.putExtra("selectedPickupEntities", reversePickups.get(position));
                                        startActivity(intent);
                                    }
                                });

                            } catch (XmlPullParserException e) {
                                finish();
                                Utility.showToast(PickupAlertActivity.this, Constants.UNKNOWN_ERROR_MESSAGE);
                                e.printStackTrace();
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                finish();
                                Utility.showToast(PickupAlertActivity.this, Constants.UNKNOWN_ERROR_MESSAGE);
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    finish();
                    Utility.showToast(PickupAlertActivity.this, Constants.UNKNOWN_ERROR_MESSAGE + ". Please try after some time.");
                    hideProgrss();
                }
            }){

                @Override
                protected Map<String,String> getParams(){
                    Map<String,String> params = new HashMap<String, String>();
                    Log.i("MAD",boyId);
                    params.put("BoyID", boyId);
                    params.put("SyncDateTime","05/05/2014");
                    return params;
                }
            };
            queue.add(stringRequest);
        }else{
            finish();
            Utility.showToast(PickupAlertActivity.this, Constants.UNKNOWN_ERROR_MESSAGE + ". Please try after some time.");
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        progressDialog = new ProgressDialog(PickupAlertActivity.this);
        registerViews();
    }

    /**
     * Register all the views
     */
    private void registerViews() {
        mDocketListView = (ListView) findViewById(R.id.sequence_docket_list_listview);
        mPendingTextView = (TextView) findViewById(R.id.sequence_pending_textview);
        mSequencedTextView = (TextView) findViewById(R.id.sequence_sequenced_textview);
        mDoneButton = (Button) findViewById(R.id.sequence_done_button);
        fetchReversePickupList();
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

