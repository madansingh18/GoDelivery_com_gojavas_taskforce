package com.gojavas.taskforce.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.gojavas.taskforce.R;
import com.gojavas.taskforce.entity.DrsEntity;
import com.gojavas.taskforce.entity.Proof;
import com.gojavas.taskforce.entity.Relation;
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
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
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
import java.util.zip.Deflater;

//import org.ksoap2.serialization.SoapObject;

/**
 * Created by MadanS on 10/23/2016.
 */
public class DRSPassActivity extends Activity implements View.OnClickListener {


    private Button delivered_pass_update_button,delivered_pass_update_save_button;
    private Button delivered_pass_update_cancel_button;
    private ProgressDialog progressDialog;
    private Spinner non_delivery_reason_spinner_id;
    private Spinner delivery_proof;

    private DrsEntity mDrsEntity;
    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private String UserID= "";
    private String BoyID= "";
    private String AWBNo= "";
    private String NonDeliveryDateTime= "";
    private String ReasonID = "";
    private String Latitude = "";
    private String Longitude = "";
    private String IsTesting = "false";
    private String nonDeliveryId = "";
    private String proofId = "";
    private float mTotalBulkCOD =0;
    private ArrayList<Proof> proofOfList;
    private EditText drs_pass_receiver_phone;
    private EditText drs_pass_receiver_name;
    private EditText drs_pass_proof_details;
    private TextView drs_pass_proof_details_label;
    private ImageView drs_pass_receiver_signature, drs_pass_pod_image;
    private ArrayList<String> selectedBulkDrsEntities;
    private boolean isBulkSubmission = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drs_pass);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setTitle("DRS Detail");
        progressDialog = new ProgressDialog(DRSPassActivity.this);
        Intent i = getIntent();
        String operationType = i.getStringExtra("Operation_Type");
        mTotalBulkCOD = i.getFloatExtra("Total_COD",0);
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

        }else {
            mDrsEntity = (DrsEntity) i.getSerializableExtra("selectedDrsEntities");
            if (mDrsEntity != null) {
                Log.i("TEST>", mDrsEntity.getdocketno());
                AWBNo = mDrsEntity.getdocketno();
            }
        }
        BoyID = Utility.getFromSharedPrefs(DRSPassActivity.this, "BOY_ID");
        UserID = Utility.getFromSharedPrefs(DRSPassActivity.this, "USER_ID");
        registerViews();
        registerListeners();

        if(Utility.proofList!=null&&Utility.proofList.size()>0){
            proofOfList = Utility.proofList;
            ArrayAdapter<Proof> adapter = new ArrayAdapter<Proof>(DRSPassActivity.this,R.layout.spinner_textview, proofOfList);
            delivery_proof.setAdapter(adapter);
            setUpRelationDetails();
            delivery_proof.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1,
                                           int position, long arg3) {
                    String isProofReq = ((Proof) proofOfList.get(position)).getProofDetailRequired();
                    if (isProofReq != null && isProofReq.length() > 0 && isProofReq.equalsIgnoreCase("False")) {
                        drs_pass_proof_details.setVisibility(View.GONE);
                        drs_pass_proof_details.setText("");
                        drs_pass_proof_details_label.setVisibility(View.GONE);
                    } else {
                        drs_pass_proof_details.setText("");
                        drs_pass_proof_details.setVisibility(View.VISIBLE);
                        drs_pass_proof_details_label.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                }
            });
        }else{
            fetchProof();
        }





//
    }

    private void setUpRelationDetails(){
        if(Utility.relationList !=null&&Utility.reasonList.size()>0){
            ArrayAdapter<Relation> adapter = new ArrayAdapter<Relation>(DRSPassActivity.this,R.layout.spinner_textview, Utility.relationList);
            non_delivery_reason_spinner_id.setAdapter(adapter);
        }else{
            fetchRelation();
        }
    }


    /**
     * Register all the views
     */
    private void registerViews() {
        non_delivery_reason_spinner_id = (Spinner)findViewById(R.id.non_delivery_reason_spinner_id);
        delivery_proof = (Spinner)findViewById(R.id.delivery_proof);
        drs_pass_receiver_name = (EditText) findViewById(R.id.drs_pass_receiver_name);
        drs_pass_receiver_phone = (EditText) findViewById(R.id.drs_pass_receiver_phone);
        drs_pass_proof_details = (EditText)findViewById(R.id.drs_pass_proof_details);
        drs_pass_proof_details_label = (TextView)findViewById(R.id.drs_pass_proof_details_label);

        delivered_pass_update_button = (Button) findViewById(R.id.delivered_pass_update_button);
        delivered_pass_update_save_button = (Button) findViewById(R.id.delivered_pass_update_save_button);

        delivered_pass_update_cancel_button = (Button) findViewById(R.id.delivered_pass_update_cancel_button);
        drs_pass_receiver_signature = (ImageView) findViewById(R.id.drs_pass_receiver_signature);
        drs_pass_pod_image = (ImageView) findViewById(R.id.drs_pass_pod_image);
    }


    /**
     * Register listeners on all the views
     */
    private void registerListeners() {
        delivered_pass_update_button.setOnClickListener(this);
        delivered_pass_update_cancel_button.setOnClickListener(this);
        delivered_pass_update_save_button.setOnClickListener(this);
        if(isBulkSubmission){
//            delivered_pass_update_save_button.setVisibility(View.VISIBLE);
        }
        drs_pass_receiver_signature.setOnClickListener(this);
        drs_pass_pod_image.setOnClickListener(this);
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
            case R.id.delivered_pass_update_save_button:
                long id = System.currentTimeMillis();
                String savedPassed = Utility.getFromSharedPrefs(DRSPassActivity.this, BoyID + "_pendingDRS");
                if(savedPassed!=null&&savedPassed.length()>0){
                    Utility.saveToSharedPrefs(DRSPassActivity.this,BoyID+"_pendingDRS",savedPassed+","+id);
                }else{
                    Utility.saveToSharedPrefs(DRSPassActivity.this,BoyID+"_pendingDRS",""+id);
                }

                JSONObject jsonObject = new JSONObject();
                Utility.saveToSharedPrefs(DRSPassActivity.this,""+id,jsonObject.toString());

                break;

            case R.id.delivered_pass_update_button:
                if(isBulkSubmission){
                    performNonDeliveredBulkOperation();
                }else{
                    performNonDeliveredOperation();
                }

                break;

            case R.id.delivered_pass_update_cancel_button:
                finish();
                break;
            case R.id.drs_pass_receiver_signature:
                onSignatureViewClicked();
                break;
            case R.id.drs_pass_pod_image:
                onPODViewClicked();
                break;
            default:
                break;
        }
    }

    private void onPODViewClicked() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA},
                        MY_CAMERA_PERMISSION_CODE);
            } else {
//                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(cameraIntent, CAMERA_REQUEST);
                openBackCamera();
            }
        } else {
//            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//            startActivityForResult(cameraIntent, CAMERA_REQUEST);
            openBackCamera();
        }
    }


    private String pictureImagePath = "";
    static final int REQUEST_PICTURE_CAPTURE = 191;
    private void openBackCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
//            startActivityForResult(cameraIntent, REQUEST_PICTURE_CAPTURE);

            File pictureFile = null;
            try {
                pictureFile = getPictureFile();
            } catch (IOException ex) {
                Toast.makeText(this,
                        "Photo file can't be created, please try again",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            if (pictureFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.gojavas.taskforce.fileprovider",
                        pictureFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(cameraIntent, REQUEST_PICTURE_CAPTURE);
            }
        }
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        String imageFileName = timeStamp + ".jpg";
//        File storageDir = Environment.getExternalStoragePublicDirectory(
//                Environment.DIRECTORY_PICTURES);
//        pictureImagePath = storageDir.getAbsolutePath() + "/" + imageFileName;
//        File file = new File(pictureImagePath);
//        Uri outputFileUri = Uri.fromFile(file);
//        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
//        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new
                        Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }

        }

//        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//            if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
//                Bitmap photo = (Bitmap) data.getExtras().get("data");
//                imageView.setImageBitmap(photo);
//            }
//        }
    }

    private void performNonDeliveredBulkOperation() {
        String ReceiverName = null;
        if(drs_pass_receiver_name!=null)
            ReceiverName = drs_pass_receiver_name.getText().toString();
        String ReceiverPhone = null;
        if(drs_pass_receiver_phone!=null)
            ReceiverPhone =  drs_pass_receiver_phone.getText().toString();
        String ProofDetails = null;
        if(drs_pass_proof_details!=null)
            ProofDetails = drs_pass_proof_details.getText().toString();

        String cod_amount = "";
        cod_amount = ""+mTotalBulkCOD;


        if(ReceiverName==null||ReceiverName.trim().length()<=0){
            Utility.showToast(DRSPassActivity.this, "Enter Receiver Name.");
            return;
        }
        if(ReceiverPhone==null||ReceiverPhone.trim().length()<=0){
            Utility.showToast(DRSPassActivity.this, "Enter Receiver Phone.");
            return;
        }

        try{

            if(!ReceiverPhone.matches("((\\+*)((0[ -]+)*|(91 )*)(\\d{12}+|\\d{10}+))|\\d{5}([- ]*)\\d{6}")){
                Utility.showToast(DRSPassActivity.this, "Enter Valid Receiver Phone.");
                return;
            }
        }catch(Exception e){
            Utility.showToast(DRSPassActivity.this, "Enter Valid Receiver Phone.dsdas");
        }
        if(drs_pass_proof_details!=null&&drs_pass_proof_details.getVisibility() == View.VISIBLE){
            if(ProofDetails==null||ProofDetails.trim().length()<=0){
                Utility.showToast(DRSPassActivity.this, "Enter Proof Details.");
                return;
            }
        }


        try{
            if(non_delivery_reason_spinner_id!=null){
                nonDeliveryId = ((Relation)non_delivery_reason_spinner_id.getSelectedItem()).getRelationID();
            }
        }catch (Exception e){}

        try{
            if(delivery_proof!=null){
                proofId = ((Proof)delivery_proof.getSelectedItem()).getProofID();
            }
        }catch (Exception e){}



        if(nonDeliveryId!=null&&nonDeliveryId.length()>0&&AWBNo!=null&&AWBNo.length()>0 &&UserID!=null&&UserID.length()>0&&BoyID!=null&&BoyID.length()>0&&proofId!=null&&proofId.length()>0&&ReceiverName!=null&&ReceiverName.length()>0&&ReceiverPhone!=null&&ReceiverPhone.length()>0){
            if(bitmapBytes==null){
                Utility.showToast(DRSPassActivity.this, "Signature is required.");
                return;
            }

//            if(bitmapPODBytes==null){
//                Utility.showToast(DRSPassActivity.this, "POD image is required.");
//                return;
//            }
            showProgrss("Please Wait...");

            RequestQueue queue = Volley.newRequestQueue(this);
            String currentDateTimeString = DateFormat.getDateInstance().format(new Date());
            String currentTimeString = DateFormat.getTimeInstance().format(new Date());
            String deliveryDateTime = "";
            try{
                deliveryDateTime = URLEncoder.encode(currentDateTimeString, "UTF-8");
            }catch (UnsupportedEncodingException e){}
            String latitude = "";
            String longitude = "";
            try{
                HashMap<String, String> location = Utility.getLocation(DRSPassActivity.this);
                latitude = location.get(Constants.LATITUDE);
                longitude = location.get(Constants.LONGITUDE);
            }catch (Exception e){

            }

            String signImageBase64 = "";
            if(signatureImgString!=null&&signatureImgString.length()>0){
                signImageBase64 = signatureImgString;
            }


                byte[] bytearrayimage = null;
                if (android.os.Build.VERSION.SDK_INT > 9) {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                }


                    String METHOD_NAME = "PostBulkDelivery";
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
                    String _docetNumber = "";
                    if(selectedBulkDrsEntities!=null)
                        for(int i=0;i<selectedBulkDrsEntities.size();i++){
                            if(i==0){
                                _docetNumber = selectedBulkDrsEntities.get(i);
                            }else{
                                _docetNumber = _docetNumber + ","+selectedBulkDrsEntities.get(i);
                            }
//                            _docetNumber = _docetNumber + ","+selectedBulkDrsEntities.get(i);
//                            stringVector.add(selectedBulkDrsEntities.get(i));
                        }
                    sayHelloPI2.setValue(_docetNumber);
                    sayHelloPI2.setType(String.class);
                    request.addProperty(sayHelloPI2);

                    Date date = Calendar.getInstance().getTime();
                    SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
                    String deliveryDateTimeString = formatter.format(date);
                    PropertyInfo sayHelloPI3 = new PropertyInfo();
                    sayHelloPI3.setName("DeliveryDateTime");
                    sayHelloPI3.setValue(deliveryDateTimeString);
                    sayHelloPI3.setType(String.class);
                    request.addProperty(sayHelloPI3);

                    SimpleDateFormat formatterTime = new SimpleDateFormat("HH:mm");
                    String todayformatterTime = formatterTime.format(date);
                    PropertyInfo delTime = new PropertyInfo();
                    delTime.setName("DeliveryTime");
                    delTime.setValue(todayformatterTime);
                    delTime.setType(String.class);
                    request.addProperty(delTime);

                    PropertyInfo sayHelloPI4001 = new PropertyInfo();
                    sayHelloPI4001.setName("ProofID");
                    sayHelloPI4001.setValue("" + proofId);
                    sayHelloPI4001.setType(String.class);
                    request.addProperty(sayHelloPI4001);

                    PropertyInfo sayHelloPI5 = new PropertyInfo();
                    sayHelloPI5.setName("ProofDetail");
                    sayHelloPI5.setValue("" + ProofDetails);
                    sayHelloPI5.setType(String.class);
                    request.addProperty(sayHelloPI5);

                    PropertyInfo sayHelloPI6 = new PropertyInfo();
                    sayHelloPI6.setName("ReceiverName");
                    sayHelloPI6.setValue("" + ReceiverName);
                    sayHelloPI6.setType(String.class);
                    request.addProperty(sayHelloPI6);

                    PropertyInfo sayHelloPI7 = new PropertyInfo();
                    sayHelloPI7.setName("ReceiverPhone");
                    sayHelloPI7.setValue("" + ReceiverPhone);
                    sayHelloPI7.setType(String.class);
                    request.addProperty(sayHelloPI7);

                    PropertyInfo sayHelloPI8 = new PropertyInfo();
                    sayHelloPI8.setName("RelationID");
                    sayHelloPI8.setValue("" + nonDeliveryId);
                    sayHelloPI8.setType(String.class);
                    request.addProperty(sayHelloPI8);

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

                    PropertyInfo sayHelloPI12 = new PropertyInfo();
                    sayHelloPI12.setName("IsTesting");
                    sayHelloPI12.setValue("0");
                    sayHelloPI12.setType(String.class);
                    request.addProperty(sayHelloPI12);

                    PropertyInfo sayHelloPI13 = new PropertyInfo();
                    sayHelloPI13.setName("CODAmount");
                    sayHelloPI13.setValue("" + cod_amount);
                    sayHelloPI13.setType(String.class);
                    request.addProperty(sayHelloPI13);


                    PropertyInfo sayHelloPI14 = new PropertyInfo();
                    sayHelloPI14.setName("Signature");

                    sayHelloPI14.setValue(bitmapBytes);
                    sayHelloPI14.setType(byte[].class);
                    request.addProperty(sayHelloPI14);

//                    PropertyInfo podImage = new PropertyInfo();
//                    podImage.setName("PODImage");
//                    podImage.setValue(bitmapPODBytes);
//                    podImage.setType(byte[].class);
//                    request.addProperty(podImage);

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
                    // Property which holds input parameters

                    // Add the property to request object
                    //Set envelope as dotNet
                    envelope.dotNet = true;
                    try {

                        // Invoke web service
                        androidHttpTransport.call(SOAP_ACTION, envelope);
                        // Get the response
                        if (envelope.bodyIn instanceof SoapFault) {
                            final SoapFault sf = (SoapFault) envelope.bodyIn;
                            // Stuff
                            System.out.println("valuu tests falut=" + sf.toString());
                        }
                        SoapPrimitive resultsRequestSOAP = null;
                        Log.i("TEST","666");
                        resultsRequestSOAP = (SoapPrimitive) envelope.getResponse();
                        hideProgrss();
                        String jsonstringRes = resultsRequestSOAP.toString();
                        Log.i("TEST", "777");
                        System.out.println("---------RES ===>>>" + jsonstringRes);
                        if(jsonstringRes!=null&&jsonstringRes.length()>0&&jsonstringRes.trim().equalsIgnoreCase("SUCCESS")){
                            setResult(2);
                            finish();
                            Utility.showToast(DRSPassActivity.this, "Status updated successfully ");
                        }else{
                            if(jsonstringRes!=null&&jsonstringRes.length()>0){
                                Utility.showToast(DRSPassActivity.this, ""+jsonstringRes);
                            }else{
                                Utility.showToast(DRSPassActivity.this, "Unable to process. After some time.");
                            }

                        }


                    }catch (Exception e){
                        e.printStackTrace();
                        hideProgrss();
                    }


        }else{
            Utility.showToast(DRSPassActivity.this, "Enter all the fields.");
        }
    }

    public static final int REQUEST_SIGNATURE_CODE = 3;
    public void onSignatureViewClicked(){
//        Toast.makeText(this,"IMAGE VIEW CLICKED",Toast.LENGTH_LONG).show();
        Intent signatureIntent = new Intent(DRSPassActivity.this, SignatureActivity.class);
        signatureIntent.putExtra("AWBNo", AWBNo);
        startActivityForResult(signatureIntent, REQUEST_SIGNATURE_CODE);
    }

    public String signatureImgString;
    public String mDocketNumber;
    public String mSignatureName;
    byte[] bitmapBytes = null;
    byte[] bitmapPODBytes = null;
    private String pictureFilePath;
    private File getPictureFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String pictureFile = "DELIVERY_" + timeStamp;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(pictureFile,  ".jpg", storageDir);
        pictureFilePath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_PICTURE_CAPTURE && resultCode == RESULT_OK) {
            File imgFile = new  File(pictureFilePath);
            if(imgFile.exists()){
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                int height = 800;
                LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(drs_pass_pod_image.getWidth(),height);
                drs_pass_pod_image.setLayoutParams(parms);
                drs_pass_pod_image.setImageBitmap(myBitmap);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                myBitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
                bitmapPODBytes  = stream.toByteArray();
            }
        } else if(requestCode == REQUEST_SIGNATURE_CODE && resultCode == RESULT_OK){
            if(data!=null){
                bitmapBytes = data.getByteArrayExtra(Constants.SIGNATURE_IMAGE);
                if(bitmapBytes!=null&&bitmapBytes.length>0){
                    signatureImgString = Base64.encodeToString(bitmapBytes,
                            Base64.NO_WRAP);
                    Bitmap signatureBitmap = null;
                    signatureBitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
                    drs_pass_receiver_signature.setImageBitmap(signatureBitmap);
                }else{
                    bitmapBytes = null;
                }
            }
        }
    }

    protected void onActivityResultA(int requestCode, int resultCode, Intent data) {
        // Extract Bitmap bytes from intent bundle
        if(data!=null){
            if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
//                Bitmap photo = (Bitmap) data.getExtras().get("data");
//                drs_pass_pod_image.setImageBitmap(photo);
//                ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                photo.compress(Bitmap.CompressFormat.JPEG, 100, stream);
//                bitmapPODBytes  = stream.toByteArray();
                File imgFile = new  File(pictureImagePath);
                if(imgFile.exists()){
                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    drs_pass_pod_image.setImageBitmap(myBitmap);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    bitmapPODBytes  = stream.toByteArray();
                }
            } else {
                bitmapBytes = data.getByteArrayExtra(Constants.SIGNATURE_IMAGE);
                if(bitmapBytes!=null&&bitmapBytes.length>0){
                    signatureImgString = Base64.encodeToString(bitmapBytes,
                            Base64.NO_WRAP);
                    Bitmap signatureBitmap = null;
                    signatureBitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
                    drs_pass_receiver_signature.setImageBitmap(signatureBitmap);
                }else{
                    bitmapBytes = null;
                }
            }

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







    private ArrayList<Relation> parseNDRReason(XmlPullParser parser) throws XmlPullParserException,IOException
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

    private ArrayList<Proof> parseProof(XmlPullParser parser) throws XmlPullParserException,IOException
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


    private void fetchProof(){

        showProgrss("Please Wait. Fetching Proof.");

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://c2p.gojavas.net/DataSyncWebApi.asmx/GetProof",
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
                            ArrayList<Proof> nonDeliveries = parseProof(parser);
                            if(nonDeliveries!=null&&nonDeliveries.size()>0){
                                proofOfList = nonDeliveries;
                                Utility.proofList = nonDeliveries;
                                Log.i("TEST", nonDeliveries.toString());
                                ArrayAdapter<Proof> adapter = new ArrayAdapter<Proof>(DRSPassActivity.this,R.layout.spinner_textview, nonDeliveries);
                                delivery_proof.setAdapter(adapter);
                                setUpRelationDetails();
                                delivery_proof.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> arg0, View arg1,
                                                               int position, long arg3) {
                                        if(proofOfList!=null&&proofOfList.size()>0){
                                            String isProofReq = ((Proof) proofOfList.get(position)).getProofDetailRequired();
                                            if (isProofReq != null && isProofReq.length() > 0 && isProofReq.equalsIgnoreCase("False")) {
                                                drs_pass_proof_details.setVisibility(View.GONE);
                                                drs_pass_proof_details.setText("");
                                                drs_pass_proof_details_label.setVisibility(View.GONE);
                                            } else {
                                                drs_pass_proof_details.setText("");
                                                drs_pass_proof_details.setVisibility(View.VISIBLE);
                                                drs_pass_proof_details_label.setVisibility(View.VISIBLE);
                                            }
                                        }else{
                                            Utility.showToast(DRSPassActivity.this, "Unable to fetch proof details.");
                                            finish();
                                        }

                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> arg0) {
                                    }
                                });
                            }else{
                                finish();
                                Utility.showToast(DRSPassActivity.this, "Unable to fetch proof details.");
                            }

                        } catch (XmlPullParserException e) {
                            finish();
                            Utility.showToast(DRSPassActivity.this, "Unable to fetch proof details.");
                            e.printStackTrace();
                        } catch (IOException e) {
                            finish();
                            Utility.showToast(DRSPassActivity.this, "Unable to fetch proof details.");
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                finish();
                Utility.showToast(DRSPassActivity.this, "Unable to fetch proof details." + ". Please try after some time.");
                hideProgrss();
            }
        });
        queue.add(stringRequest);
        return;
    }


    private void fetchRelation(){

        showProgrss("Please Wait. Fetching Relation.");

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://c2p.gojavas.net/DataSyncWebApi.asmx/GetRelation",
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
                            ArrayList<Relation> nonDeliveries = parseNDRReason(parser);
                            if(nonDeliveries!=null&&nonDeliveries.size()>0){
                                Utility.relationList = nonDeliveries;
                                Log.i("TEST", nonDeliveries.toString());
                                ArrayAdapter<Relation> adapter = new ArrayAdapter<Relation>(DRSPassActivity.this,R.layout.spinner_textview, nonDeliveries);
                                non_delivery_reason_spinner_id.setAdapter(adapter);
                            }else{
                                finish();
                                Utility.showToast(DRSPassActivity.this, "Unable to fetch relation details.");
                            }

                        } catch (XmlPullParserException e) {
                            finish();
                            Utility.showToast(DRSPassActivity.this, "Unable to fetch relation details.");
                            e.printStackTrace();
                        } catch (IOException e) {
                            finish();
                            Utility.showToast(DRSPassActivity.this, "Unable to fetch relation details.");
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                finish();
                Utility.showToast(DRSPassActivity.this, "Unable to fetch relation details"+ ". Please try after some time.");
                hideProgrss();
            }
        });
        queue.add(stringRequest);
        return;
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    private void performNonDeliveredOperation() {
//        try{}catch (Exception e){}
        String ReceiverName = null;
                if(drs_pass_receiver_name!=null)
                    ReceiverName = drs_pass_receiver_name.getText().toString();
        String ReceiverPhone = null;
                if(drs_pass_receiver_phone!=null)
                    ReceiverPhone =  drs_pass_receiver_phone.getText().toString();
        String ProofDetails = null;
                if(drs_pass_proof_details!=null)
                    ProofDetails = drs_pass_proof_details.getText().toString();

        String cod_amount = "";
        if(mDrsEntity!=null&& mDrsEntity.getcod_amt().length()>0){
            cod_amount = mDrsEntity.getcod_amt();
        }


        if(ReceiverName==null||ReceiverName.trim().length()<=0){
            Utility.showToast(DRSPassActivity.this, "Enter Receiver Name.");
            return;
        }
        if(ReceiverPhone==null||ReceiverPhone.trim().length()<=0){
            Utility.showToast(DRSPassActivity.this, "Enter Receiver Phone.");
            return;
        }

        try{
//            if ("".match("^0[0-9]{9}") || match("^+91[0-9]{10}") || match("^0091[0-9]{9}")){
            if(!ReceiverPhone.matches("((\\+*)((0[ -]+)*|(91 )*)(\\d{12}+|\\d{10}+))|\\d{5}([- ]*)\\d{6}")){
            Utility.showToast(DRSPassActivity.this, "Enter Valid Receiver Phone.");
            return;
        }
        }catch(Exception e){
            Utility.showToast(DRSPassActivity.this, "Enter Valid Receiver Phone.dsdas");
        }
        if(drs_pass_proof_details!=null&&drs_pass_proof_details.getVisibility() == View.VISIBLE){
            if(ProofDetails==null||ProofDetails.trim().length()<=0){
                Utility.showToast(DRSPassActivity.this, "Enter Proof Details.");
                return;
            }
        }


        try{
            if(non_delivery_reason_spinner_id!=null){
                nonDeliveryId = ((Relation)non_delivery_reason_spinner_id.getSelectedItem()).getRelationID();
            }
        }catch (Exception e){}

        try{
            if(delivery_proof!=null){
                proofId = ((Proof)delivery_proof.getSelectedItem()).getProofID();
            }
        }catch (Exception e){}



        if(nonDeliveryId!=null&&nonDeliveryId.length()>0&&AWBNo!=null&&AWBNo.length()>0 &&UserID!=null&&UserID.length()>0&&BoyID!=null&&BoyID.length()>0&&proofId!=null&&proofId.length()>0&&ReceiverName!=null&&ReceiverName.length()>0&&ReceiverPhone!=null&&ReceiverPhone.length()>0){
            if(bitmapBytes==null){
                Utility.showToast(DRSPassActivity.this, "Signature is required.");
                return;
            }

            if(bitmapPODBytes==null){
                if (mDrsEntity.getImageRequired() != null && mDrsEntity.getImageRequired().equalsIgnoreCase("true")){
                    Utility.showToast(DRSPassActivity.this, "POD image is required.");
                    return;
                }
            }

            showProgrss("Please Wait...");

            RequestQueue queue = Volley.newRequestQueue(this);
            String currentDateTimeString = DateFormat.getDateInstance().format(new Date());
            String currentTimeString = DateFormat.getTimeInstance().format(new Date());
            String deliveryDateTime = "";
            try{
                deliveryDateTime = URLEncoder.encode(currentDateTimeString, "UTF-8");
            }catch (UnsupportedEncodingException e){}
            String latitude = "";
            String longitude = "";
            try{
                HashMap<String, String> location = Utility.getLocation(DRSPassActivity.this);
                latitude = location.get(Constants.LATITUDE);
                longitude = location.get(Constants.LONGITUDE);
            }catch (Exception e){

            }

            String signImageBase64 = "";
            if(signatureImgString!=null&&signatureImgString.length()>0){
                signImageBase64 = signatureImgString;
            }
            String url = null;
            try {
                url = "http://c2p.gojavas.net/DataSyncWebApi.asmx/PostDelivery_SGN_PODImage?UserID="+UserID+"&BoyID="+BoyID+"&AWBNo="+AWBNo+"&DeliveryDateTime="+ URLEncoder.encode(currentDateTimeString, "UTF-8")+"&ProofID="+proofId+"&ProofDetail="+ProofDetails+"&ReceiverName="+ReceiverName+"&ReceiverPhone="+ReceiverPhone+"&RelationID="+nonDeliveryId+"&Latitude="+latitude+"&Longitude="+longitude+"&IsTesting=0&CODAmount="+cod_amount;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }







            if(true){
                byte[] bytearrayimage = null;
                if (android.os.Build.VERSION.SDK_INT > 9) {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                }


                if(true){
                    String METHOD_NAME = "PostDelivery_SGN_PODImage";
                    String NAMESPACE = "http://tempuri.org/";
                    String SOAP_ACTION = NAMESPACE + METHOD_NAME;
                    final String URL = "http://c2p.gojavas.net/DataSyncWebApi.asmx";
                    showProgrss("Please Wait...");
//                    try {
//
//                        String path = Utility.getApplicationPath(Constants.PATH_CAPTURED_IMAGE);
//                        bytearrayimage = convertDocToByteArrayFoRImage(path+mSignatureName);
//                        Toast.makeText(DRSPassActivity.this,"111",Toast.LENGTH_LONG).show();
//                        Toast.makeText(DRSPassActivity.this,"111"+bytearrayimage,Toast.LENGTH_LONG).show();
//
//                    } catch (IOException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                        Toast.makeText(DRSPassActivity.this,"111 ERROR",Toast.LENGTH_LONG).show();
//                    } // bitmapToByteArray(bmBef);



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


                    PropertyInfo sayHelloPI2 = new PropertyInfo();
                    sayHelloPI2.setName("AWBNo");
                    //Check if bulk AWB NO present then upload with qoma separated values
                    sayHelloPI2.setValue("" + AWBNo);
                    sayHelloPI2.setType(String.class);
                    request.addProperty(sayHelloPI2);

                    Date date = Calendar.getInstance().getTime();
                    SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
                    String deliveryDateTimeString = formatter.format(date);
                    PropertyInfo sayHelloPI3 = new PropertyInfo();
                    sayHelloPI3.setName("DeliveryDateTime");
                    sayHelloPI3.setValue(deliveryDateTimeString);
                    sayHelloPI3.setType(String.class);
                    request.addProperty(sayHelloPI3);
//                    Log.i("DATETIME = ", deliveryDateTimeString);
//                    Toast.makeText(DRSPassActivity.this,"DATE = "+deliveryDateTimeString,Toast.LENGTH_LONG).show();


                    SimpleDateFormat formatterTime = new SimpleDateFormat("HH:mm");
                    String todayformatterTime = formatterTime.format(date);
                    PropertyInfo delTime = new PropertyInfo();
                    delTime.setName("DeliveryTime");
                    delTime.setValue(todayformatterTime);
                    delTime.setType(String.class);
                    request.addProperty(delTime);
//                    Log.i("DATETIME = ", todayformatterTime);
//                    Toast.makeText(DRSPassActivity.this,"TIME = "+todayformatterTime,Toast.LENGTH_LONG).show();

                    PropertyInfo sayHelloPI4001 = new PropertyInfo();
                    sayHelloPI4001.setName("ProofID");
                    sayHelloPI4001.setValue("" + proofId);
                    sayHelloPI4001.setType(String.class);
                    request.addProperty(sayHelloPI4001);

                    PropertyInfo sayHelloPI5 = new PropertyInfo();
                    sayHelloPI5.setName("ProofDetail");
                    sayHelloPI5.setValue("" + ProofDetails);
                    sayHelloPI5.setType(String.class);
                    request.addProperty(sayHelloPI5);

                    PropertyInfo sayHelloPI6 = new PropertyInfo();
                    sayHelloPI6.setName("ReceiverName");
                    sayHelloPI6.setValue("" + ReceiverName);
                    sayHelloPI6.setType(String.class);
                    request.addProperty(sayHelloPI6);

                    PropertyInfo sayHelloPI7 = new PropertyInfo();
                    sayHelloPI7.setName("ReceiverPhone");
                    sayHelloPI7.setValue("" + ReceiverPhone);
                    sayHelloPI7.setType(String.class);
                    request.addProperty(sayHelloPI7);

                    PropertyInfo sayHelloPI8 = new PropertyInfo();
                    sayHelloPI8.setName("RelationID");
                    sayHelloPI8.setValue("" + nonDeliveryId);
                    sayHelloPI8.setType(String.class);
                    request.addProperty(sayHelloPI8);

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

                    PropertyInfo sayHelloPI12 = new PropertyInfo();
                    sayHelloPI12.setName("IsTesting");
                    sayHelloPI12.setValue("0");
                    sayHelloPI12.setType(String.class);
                    request.addProperty(sayHelloPI12);

                    PropertyInfo sayHelloPI13 = new PropertyInfo();
                    sayHelloPI13.setName("CODAmount");
                    sayHelloPI13.setValue("" + cod_amount);
                    sayHelloPI13.setType(String.class);
                    request.addProperty(sayHelloPI13);

//                    Log.i("IMAGE DATA ", "" + bitmapBytes.length);

                    PropertyInfo sayHelloPI14 = new PropertyInfo();
                    sayHelloPI14.setName("Signature");
//                    Toast.makeText(DRSPassActivity.this,"===",Toast.LENGTH_LONG).show();

//                    Toast.makeText(DRSPassActivity.this,"==="+bitmapBytes.length,Toast.LENGTH_LONG).show();
                    sayHelloPI14.setValue(bitmapBytes);
                    sayHelloPI14.setType(byte[].class);
                    request.addProperty(sayHelloPI14);

                    PropertyInfo podImage = new PropertyInfo();
                    podImage.setName("PODImage");
                    podImage.setValue(bitmapPODBytes);
                    podImage.setType(byte[].class);
                    request.addProperty(podImage);

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
                    // Property which holds input parameters

                    // Add the property to request object
                    //Set envelope as dotNet
                    envelope.dotNet = true;
                    try {

                        // Invoke web service
                        androidHttpTransport.call(SOAP_ACTION, envelope);
                        // Get the response
                        if (envelope.bodyIn instanceof SoapFault) {
                            final SoapFault sf = (SoapFault) envelope.bodyIn;
                            // Stuff
                            System.out.println("valuu tests falut=" + sf.toString());
                        }
                        SoapPrimitive resultsRequestSOAP = null;
                        Log.i("TEST","666");
                        resultsRequestSOAP = (SoapPrimitive) envelope.getResponse();
                        hideProgrss();
                        String jsonstringRes = resultsRequestSOAP.toString();
                        Log.i("TEST", "777");
                        System.out.println("---------RES ===>>>" + jsonstringRes);
                        if(jsonstringRes!=null&&jsonstringRes.length()>0&&jsonstringRes.trim().equalsIgnoreCase("SUCCESS")){
                            hideProgrss();
                            setResult(2);
                            finish();
                            Utility.showToast(DRSPassActivity.this, "Status updated successfully ");
                        }else{
                            hideProgrss();
                            if(jsonstringRes!=null&&jsonstringRes.length()>0){
                                Utility.showToast(DRSPassActivity.this, ""+jsonstringRes);
                            }else{
                                Utility.showToast(DRSPassActivity.this, "Unable to process. After some time.");
                            }

                        }


                    }catch (Exception e){
                        e.printStackTrace();
                        hideProgrss();
                    }
                    return;
                }

                return;
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
                                        Utility.showToast(DRSPassActivity.this, "Status updated successfully ");
                                    }else{
                                        Utility.showToast(DRSPassActivity.this, serviceStatus);
                                    }
                                }else{
                                    Utility.showToast(DRSPassActivity.this, Constants.UNKNOWN_ERROR_MESSAGE);
                                }
                            } catch (XmlPullParserException e) {
                                Utility.showToast(DRSPassActivity.this, Constants.UNKNOWN_ERROR_MESSAGE);
                                e.printStackTrace();
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                Utility.showToast(DRSPassActivity.this, Constants.UNKNOWN_ERROR_MESSAGE);
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Utility.showToast(DRSPassActivity.this, Constants.UNKNOWN_ERROR_MESSAGE + ". Please try after some time.");
                    hideProgrss();
                }
            });
            queue.add(stringRequest);

        }else{
            Utility.showToast(DRSPassActivity.this, "Enter all the fields.");
        }


    }


    public String signatureFileName = null;
    public String signatureImgFileLocation = "";
    public byte[] bytearray;
    public String jsonstringRes;
    public String stripHtml(String html) {
        return Html.fromHtml(html).toString();
    }


    public byte[] convertDocToByteArray(String sourcePath) throws IOException {
        File f = new File(sourcePath);
        long l = f.length();
        byte[] buf = new byte[(int) l];
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            InputStream fis = new FileInputStream(sourcePath);

            for (int readNum; (readNum = fis.read(buf)) != -1;) {
                bos.write(buf, 0, readNum);
                Log.i("", "read num bytes: " + readNum);
            }
        } catch (IOException e) {
            System.out.println("IO Ex" + e);
        }
        byte[] bytes1 = bos.toByteArray();

        return bytes1;
    }

    public byte[] convertDocToByteArrayFoRImage(String sourcePath)
            throws IOException {
        File f = new File(sourcePath);
        long l = f.length();
        byte[] buf = new byte[(int) l];
        Deflater deflater = new Deflater();
        deflater.setInput(buf);
        deflater.setLevel(Deflater.BEST_COMPRESSION);
        ByteArrayOutputStream bos = new ByteArrayOutputStream(buf.length);
        deflater.finish();
        byte[] buffer = new byte[1024];
        while (!deflater.finished()) {
            int count = deflater.deflate(buffer); // returns the generated
            bos.write(buffer, 0, count);
        }
        bos.close();
        byte[] output = bos.toByteArray();
        return output;
    }
}

