package com.gojavas.taskforce.ui.activity;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.gridlayout.widget.GridLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ezetap.sdk.EzeConstants;
import com.ezetap.sdk.TransactionDetails;
import com.gojavas.taskforce.R;
import com.gojavas.taskforce.database.DrsHelper;
import com.gojavas.taskforce.database.ItemHelper;
import com.gojavas.taskforce.database.PaymentHelper;
import com.gojavas.taskforce.database.PaymentStatusHelper;
import com.gojavas.taskforce.database.SwipeHelper;
import com.gojavas.taskforce.database.UserHelper;
import com.gojavas.taskforce.entity.DrsEntity;
import com.gojavas.taskforce.entity.PaymentEntity;
import com.gojavas.taskforce.entity.PaymentStatusEntity;
import com.gojavas.taskforce.entity.SwipeEntity;
import com.gojavas.taskforce.entity.UserEntity;
import com.gojavas.taskforce.manager.DesignManager;
import com.gojavas.taskforce.utils.Constants;
import com.gojavas.taskforce.utils.EzetapConstants;
import com.gojavas.taskforce.utils.FreeRechargeConstants;
import com.gojavas.taskforce.utils.MswipeConstants;
import com.gojavas.taskforce.utils.TruPayConstants;
import com.gojavas.taskforce.utils.Utility;
import com.gojavas.taskforce.utils.XmlDomParser;
import com.mswipe.wisepad.apkkit.WisePadController;
import com.mswipe.wisepad.apkkit.WisePadControllerListener;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by GJS280 on 28/4/2015.
 */
public class PaymentActivity extends AppCompatActivity implements WisePadControllerListener {

    private GridLayout mGridLayout;
    ProgressDialog progressDialog;

    private static DrsEntity mDrsEntity;
    private static UserEntity userEntity;

    private JSONArray mSuccessArray;
    private JSONObject mPaymentResponse = new JSONObject();
    private int mPosition = 0;
    private int mEight, mHeight, mScreenWidth;
    private float minPayment = 0, maxPayment = 0;
    private JSONObject mDesignJson;
    private String mPaymentType = "Cash", mFailCategory = "", mOldPaymentDevice = "";
    private String mCODAmount;
    private ArrayList<String> mClientCodeList = new ArrayList<>();
    private ArrayList<String> olxClientCodeList = new ArrayList<>();
    private final String PAYMENT_SUCCESS = "Payment done successfully", PAYMENT_FAIL = "Payment not done";
    String trupayRefNo, fcAuthToken, fc_ServerID, customerMobileNo;
    int count = 0;
    private long currentMilliSeconds;
    String buttonMsg = "Status";

    private WisePadController mWisePadController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        mWisePadController = WisePadController.sharedInstance(this, this);
        userEntity = UserHelper.getInstance().getUserCompleteDetail();

//        mClientCodeList.clear();
//        String[] clientCodes = getResources().getStringArray(R.array.SOD_client_codes);
//        for (String code : clientCodes) {
//            mClientCodeList.add(code);
//        }

        // get SOD client list
        try {
            mClientCodeList.clear();
            JSONObject designJson = DesignManager.getInstance().getDesignJson();
            JSONArray clientsJsonArray = designJson.getJSONArray(Constants.DESIGN_CLIENT_SOD);
            int size = clientsJsonArray.length();
            for (int i = 0; i < size; i++) {
                String client = clientsJsonArray.getString(i);
                mClientCodeList.add(client);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // get OLX client
        try {
            olxClientCodeList.clear();
            JSONObject designJson = DesignManager.getInstance().getDesignJson();
            JSONArray clientsJsonArray = designJson.getJSONArray(Constants.DESIGN_CLIENT_OLX);
            int size = clientsJsonArray.length();
            for (int i = 0; i < size; i++) {
                String client = clientsJsonArray.getString(i);
                olxClientCodeList.add(client);
                Log.i("olxClientCodeList", olxClientCodeList + "");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // get current time in milliseconds
        Calendar cal = Calendar.getInstance();
        currentMilliSeconds = cal.getTimeInMillis();

        getIntentValues();
        registerViews();
        customActionBar();

        // Check whether doing payment first time or not
        PaymentStatusEntity payment = PaymentStatusHelper.getInstance().getPaymentStatusDetail(mDrsEntity.getdrs_docket());
        if (payment != null) {
            // Get the device with which the payment was tried earlier
            mOldPaymentDevice = payment.getdevice_type();
        }

        if (mFailCategory != null && mFailCategory.contains(Constants.PARTIAL_RETURN)) {
            // Add Rs 49 for partial delivery
            mCODAmount = ItemHelper.getInstance().skuItemsSum(mDrsEntity.getdrs_docket());
            getSupportActionBar().setTitle("Payment: Rs " + mCODAmount);
            float amount = Float.parseFloat(mCODAmount);
            amount += 49.0;
            mCODAmount = String.valueOf(amount);
            PaymentActivity.this.getSupportActionBar().setTitle("Payment: Rs " + mCODAmount);
//            showPaymentEditDialog();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (mPaymentType) {
            case Constants.PAYMENT_TYPE_EZETAP:
                paymentResponseEzetap(resultCode, data);
                break;
            case Constants.PAYMENT_TYPE_MSWIPE:
                paymentMswipeResult(requestCode, resultCode, data);
                break;
            default:
                break;
        }
    }

    /**
     * customize action bar
     */
    private void customActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Payment: Rs " + mCODAmount);
    }

    /**
     * Get intent values
     */
    private void getIntentValues() {
        Intent intent = getIntent();
        mFailCategory = intent.getStringExtra(Constants.FAIL_CATEGORY);
        String drs_docket = intent.getStringExtra(Constants.DRS_DOCKET);

        mDrsEntity = DrsHelper.getInstance().getDrsDetailData(drs_docket);
        mCODAmount = mDrsEntity.getcod_amt();
        try {
            mDesignJson = new JSONObject(intent.getStringExtra(Constants.DESIGN_JSON));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Register all the views
     */
    private void registerViews() {
        progressDialog = new ProgressDialog(PaymentActivity.this);

        mEight = (int) getResources().getDimension(R.dimen.eight);
        mHeight = (int) getResources().getDimension(R.dimen.hundredfifty);
        mScreenWidth = Utility.getMetricsWidth(PaymentActivity.this);
        mGridLayout = (GridLayout) findViewById(R.id.payment_gridlayout);
        try {
            mSuccessArray = mDesignJson.getJSONArray(Constants.DESIGN_SUCCESS);
            int length = mSuccessArray.length();
            for (int i = 0; i < length; i++) {
                JSONObject jsonObject = mSuccessArray.getJSONObject(i);
                String title = jsonObject.getString(Constants.DESIGN_TITLE);
                String type = jsonObject.getString(Constants.DESIGN_TYPE);
                String icon = jsonObject.getString(Constants.DESIGN_ICON);
                int iconId = Utility.getResId(icon);
                String compulsory = "false";
                if (jsonObject.has(Constants.DESIGN_COMPULSORY)) {
                    compulsory = jsonObject.getString(Constants.DESIGN_COMPULSORY);
                }

                String clientCode = mDrsEntity.getclient_code();
                // If client code is that of Jabong or Snapdeal then show SOD otherwise not
                if (mClientCodeList.contains(clientCode)) {
                    // It is Jabong or Snapdeal.
                    // Added by Harshita for TruPay and FreeCharge
                    if (title.equalsIgnoreCase("TP")) {
                        title = "TruPay";
                    }
//                    else if (title.equalsIgnoreCase("FC")) {
//                        title = "FreeCharge";
//                    }
                    LinearLayout linearLayout = createLayout(title, iconId);
                    linearLayout.setId(i);
                    mGridLayout.addView(linearLayout);
                } else {
                    // It is not Jabong or Snapdeal
                    if (title.equalsIgnoreCase("Cash") || title.equalsIgnoreCase("TP")) {
                        // Added by Harshita for TruPay and FreeCharge
                        if (title.equalsIgnoreCase("TP")) {
                            title = "TruPay";
                        }
//                        else if (title.equalsIgnoreCase("FC")) {
//                            title = "FreeCharge";
//                        }
                        LinearLayout linearLayout = createLayout(title, iconId);
                        linearLayout.setId(i);
                        mGridLayout.addView(linearLayout);
                        // Added by Harshita for OLX client
                    } else if (olxClientCodeList.contains(clientCode) && mDrsEntity.getclient_name().equalsIgnoreCase("OLX")) {
                        Log.i("olxClientCodeList", "olxClientCodeList");
                        title = "Cheque";
                        LinearLayout linearLayout = createLayout(title, iconId);
                        linearLayout.setId(i);
                        mGridLayout.addView(linearLayout);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Create layout
     *
     * @param title
     * @return
     */
    private LinearLayout createLayout(final String title, int icon) {
        LinearLayout layout = new LinearLayout(PaymentActivity.this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.width = (mScreenWidth - (mEight * 2)) / 2;
        params.height = mHeight;
        layout.setLayoutParams(params);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setBackgroundResource(R.drawable.appcolor_white_border);
        layout.setGravity(Gravity.CENTER);

        ImageView imageView = createChildImageView(icon);
        TextView textView = createChildTextView(title);

        layout.addView(imageView);
        layout.addView(textView);

        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutClicked(title, v);
            }
        });

        return layout;
    }

    /**
     * Get child ImageView
     *
     * @param icon
     * @return
     */
    private ImageView createChildImageView(int icon) {
        ImageView imageView = new ImageView(PaymentActivity.this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        imageView.setLayoutParams(params);
        imageView.setImageResource(icon);
        return imageView;
    }

    /**
     * Get child TextView
     *
     * @param text
     * @return
     */
    private TextView createChildTextView(String text) {
        TextView textView = new TextView(PaymentActivity.this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        textView.setLayoutParams(params);
        textView.setGravity(Gravity.CENTER);
        textView.setPadding(mEight, 0, mEight, 0);
        textView.setSingleLine(true);
        textView.setText(text);
        textView.setTextColor(Color.WHITE);
        return textView;
    }

    /**
     * layout is clicked
     *
     * @param view
     */
    private void layoutClicked(String title, View view) {
        mPosition = (int) view.getId();
        JSONObject jsonObject = null;
        String type = "";
        try {
            jsonObject = mSuccessArray.getJSONObject(mPosition);
            type = jsonObject.getString(Constants.DESIGN_TYPE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        switch (type) {
            case Constants.PAYMENT_TYPE_CASH:
                // Payment via cash
                mPaymentType = Constants.PAYMENT_TYPE_CASH;
//                showCashDialog();
                try {
                    mPaymentResponse.put(Constants.TRANSACTION_NO, "");
                    mPaymentResponse.put(Constants.AUTH_CODE, "");
                    mPaymentResponse.put(Constants.CARD_TYPE, "");
                    mPaymentResponse.put(Constants.BANK_NAME, "");
                    mPaymentResponse.put(Constants.MODE_OF_PAYMENT, Constants.PAYMENT_TYPE_CASH);
                    mPaymentResponse.put(Constants.RECEIPT_URL, "");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                goToPreviousScreen();
                break;
            case Constants.PAYMENT_TYPE_EZETAP:
                // Payment via Ezetap
                mPaymentType = Constants.PAYMENT_TYPE_EZETAP;
                startEzetapPayment();
                break;
            case Constants.PAYMENT_TYPE_MSWIPE:
                // Payment via Mswipe
                mPaymentType = Constants.PAYMENT_TYPE_MSWIPE;
                startMswipePayment();
                break;
            case Constants.PAYMENT_TYPE_TRUPAY:
                // Payment Via TruPay
               // Added by Harshita
                mPaymentType = Constants.PAYMENT_TYPE_TRUPAY;
                startTruPayPayment();
                break;
            case Constants.PAYMENT_TYPE_FREE_RECHARGE:
                // Payment via Free Charge
                // Added by Harshita
                mPaymentType = Constants.PAYMENT_TYPE_FREE_RECHARGE;
//                startFreeRechargePayment();
                break;
            case Constants.PAYMENT_TYPE_CHEQUE:
                // Payment Via Cheque
                // Added by Harshita
                mPaymentType = Constants.PAYMENT_TYPE_CHEQUE;
                try {
                    mPaymentResponse.put(Constants.TRANSACTION_NO, "");
                    mPaymentResponse.put(Constants.AUTH_CODE, "");
                    mPaymentResponse.put(Constants.CARD_TYPE, "OLX");
                    mPaymentResponse.put(Constants.BANK_NAME, "OLX");
                    mPaymentResponse.put(Constants.MODE_OF_PAYMENT, Constants.PAYMENT_TYPE_CHEQUE);
                    mPaymentResponse.put(Constants.RECEIPT_URL, "");

                    // go to next screen
                    goToPreviousScreen();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    /**
     * Show dialog for cash payment
     */
    public void showCashDialog() {
        final Dialog dialog = new Dialog(PaymentActivity.this, R.style.customDialogTheme);
        dialog.setContentView(R.layout.dialog_resequence);
        TextView messageTextView = (TextView) dialog.findViewById(R.id.dialog_message);
        Button okButton = (Button) dialog.findViewById(R.id.dialog_ok_button);
        Button cancelButton = (Button) dialog.findViewById(R.id.dialog_cancel_button);

        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = Utility.getMetricsWidth(PaymentActivity.this);
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);

        messageTextView.setText("COD Amount: Rs " + mCODAmount);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    mPaymentResponse.put(Constants.TRANSACTION_NO, "");
                    mPaymentResponse.put(Constants.AUTH_CODE, "");
                    mPaymentResponse.put(Constants.CARD_TYPE, "");
                    mPaymentResponse.put(Constants.BANK_NAME, "");
                    mPaymentResponse.put(Constants.MODE_OF_PAYMENT, Constants.PAYMENT_TYPE_CASH);
                    mPaymentResponse.put(Constants.RECEIPT_URL, "");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                goToPreviousScreen();
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

    /**
     * Go to previous screen
     */
    private void goToPreviousScreen() {
        try {
            String paymentType = mPaymentResponse.getString(Constants.MODE_OF_PAYMENT);
            if (!paymentType.equalsIgnoreCase(Constants.PAYMENT_TYPE_CASH)) {
                insertPaymentDetail();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Intent successIntent = new Intent();
        successIntent.putExtra(Constants.PAYMENT_MESSAGE, mPaymentResponse.toString());
        successIntent.putExtra(Constants.PAYMENT_AMOUNT, mCODAmount);
        setResult(RESULT_OK, successIntent);
        finish();
    }

    /**
     * Insert payment detail in database
     */
    private void insertPaymentDetail() {
        try {
            String drsnumber = mDrsEntity.getdrsno();
            String docket_number = mDrsEntity.getdocketno();
            String drs_docket = mDrsEntity.getdrs_docket();
            String transaction_no = mPaymentResponse.getString(Constants.TRANSACTION_NO);
            String auth_code = mPaymentResponse.getString(Constants.AUTH_CODE);
            String card_type = mPaymentResponse.getString(Constants.CARD_TYPE);
            String bank_name = mPaymentResponse.getString(Constants.BANK_NAME);
            String mode_of_payment = mPaymentResponse.getString(Constants.MODE_OF_PAYMENT);

            PaymentEntity payment = new PaymentEntity();
            payment.setdrsnumber(drsnumber);
            payment.setdocket_number(docket_number);
            payment.setdrs_docket(drs_docket);
            payment.settransaction_no(transaction_no);
            payment.setoriginal_amount_paid(mCODAmount);
            payment.setauth_code(auth_code);
            payment.setcard_type(card_type);
            payment.setbank_name(bank_name);
            payment.setmode_of_payment(mode_of_payment);

            PaymentHelper.getInstance().insertOrUpdate(payment);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Payment using Ezetap device
     */
    private void startEzetapPayment() {
        if (Utility.isInternetConnected(PaymentActivity.this)) {
            // If payment is not tried earlier or was tried with different device the continue normal payment process
//            if (mOldPaymentDevice == null || TextUtils.isEmpty(mOldPaymentDevice) ||
//                    mOldPaymentDevice.equals("") || !mOldPaymentDevice.equals(Constants.PAYMENT_TYPE_EZETAP)) {
//                // Normal eztap payment process
//                normalEzetapPayment();
//            } else {
//                // Payment was tried with the same device earlier
            verifyEzetapPayment();
//            }
        } else {
            Utility.showToast(PaymentActivity.this, "Internet not connected");
        }
    }

    /**
     * Continue ezetap payment process normally
     */
    private void normalEzetapPayment() {
        // Insert its entry in payment status table
        PaymentStatusEntity paymentStatusEntity = new PaymentStatusEntity();
        paymentStatusEntity.setdrsnumber(mDrsEntity.getdrsno());
        paymentStatusEntity.setdocket_number(mDrsEntity.getdocketno());
        paymentStatusEntity.setdrs_docket(mDrsEntity.getdrs_docket());
        paymentStatusEntity.setdevice_type(Constants.PAYMENT_TYPE_EZETAP);
        paymentStatusEntity.setpayment_status("");
        PaymentStatusHelper.getInstance().insertOrUpdate(paymentStatusEntity);
        // Start payment process
        Utility.paymentEzetap(PaymentActivity.this, mDrsEntity.getdocketno(), mCODAmount, mDrsEntity.getcsgeteleno());
    }

    /**
     * Verify ezetap payment status
     */
    private void verifyEzetapPayment() {
        showProgress("Verifying Ezetap payment...");
        JSONObject requestJson = new JSONObject();
        try {
            requestJson.put(EzeConstants.KEY_APPKEY, EzetapConstants.APP_KEY);
            requestJson.put(EzeConstants.KEY_USERNAME, Utility.getFromSharedPrefs(PaymentActivity.this, Constants.EZETAP_USERNAME_KEY));
            requestJson.put(EzeConstants.KEY_TXN_EXT_REF_NUM, mDrsEntity.getdocketno());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest paymentVerifyRequest = new JsonObjectRequest(Request.Method.POST, Constants.EZETAP_PAYMENT_VERIFY_URL, requestJson, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                hideProgress();
                System.out.println("ezetap payment result: " + jsonObject.toString());
                try {
                    String success = jsonObject.getString("success");
                    if (success != null && success.equalsIgnoreCase("true")) {
                        // Payment already done
                        String status = jsonObject.getString(EzeConstants.KEY_TXN_STATUS);
                        if (status != null && status.equalsIgnoreCase(Constants.EZETAP_PAYMENT_DONE)) {
                            mPaymentResponse.put(Constants.TRANSACTION_NO, jsonObject.get("txnId"));
                            mPaymentResponse.put(Constants.AUTH_CODE, jsonObject.get("authCode"));
                            mPaymentResponse.put(Constants.CARD_TYPE, jsonObject.get("paymentCardBrand"));
                            mPaymentResponse.put(Constants.BANK_NAME, "");
                            mPaymentResponse.put(Constants.MODE_OF_PAYMENT, Constants.PAYMENT_TYPE_EZETAP);
                            mPaymentResponse.put(Constants.RECEIPT_URL, jsonObject.get("customerReceiptUrl"));
                            showAlertDialog(PAYMENT_SUCCESS);
                        } else {
                            // Continue ezetap normal payment process
                            normalEzetapPayment();
                        }
                    } else {
                        // Payment not done
                        // Continue ezetap normal payment process
                        normalEzetapPayment();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                hideProgress();
                volleyError.printStackTrace();
            }
        });
        TaskForceApplication.getInstance().addToRequestQueue(paymentVerifyRequest);
    }

    /**
     * Payment using Mswipe device
     */
    private void startMswipePayment() {
        if (Utility.isInternetConnected(PaymentActivity.this)) {
            // If payment is not tried earlier or was tried with different device the continue normal payment process
//            if (mOldPaymentDevice == null || TextUtils.isEmpty(mOldPaymentDevice) ||
//                    mOldPaymentDevice.equals("") || !mOldPaymentDevice.equals(Constants.PAYMENT_TYPE_MSWIPE)) {
//                // Normal eztap payment process
//                normalMswipePayment();
//            } else {
//                // Payment was tried with the same device earlier
            verifyMswipePayment();
//            }
        } else {
            Utility.showToast(PaymentActivity.this, "Internet not connected");
        }
    }

    /**
     * Continue mswipe payment process normally
     */
    private void normalMswipePayment() {
        // Insert its entry in payment status table
        PaymentStatusEntity paymentStatusEntity = new PaymentStatusEntity();
        paymentStatusEntity.setdrsnumber(mDrsEntity.getdrsno());
        paymentStatusEntity.setdocket_number(mDrsEntity.getdocketno());
        paymentStatusEntity.setdrs_docket(mDrsEntity.getdrs_docket());
        paymentStatusEntity.setdevice_type(Constants.PAYMENT_TYPE_MSWIPE);
        paymentStatusEntity.setpayment_status("");
        PaymentStatusHelper.getInstance().insertOrUpdate(paymentStatusEntity);

        // Start payment process
        String referenceId = Utility.getFromSharedPrefs(PaymentActivity.this, MswipeConstants.REFERENCE_ID);
        String sessionTokeniser = Utility.getFromSharedPrefs(PaymentActivity.this, MswipeConstants.SESSION_TOKENISER);
        if (referenceId != null && sessionTokeniser != null &&
                !TextUtils.isEmpty(referenceId) && !TextUtils.isEmpty(sessionTokeniser) &&
                referenceId.length() != 0 && sessionTokeniser.length() != 0) {
            // Logged in
            // Make payment
            String amount = mCODAmount;
            Float finalAmount = Float.parseFloat(amount);
            DecimalFormat df = new DecimalFormat("0.00");
            df.setMaximumFractionDigits(2);
            amount = df.format(finalAmount);
            String phoneNumber = mDrsEntity.getcsgeteleno();
            phoneNumber = phoneNumber.substring(1);
            Utility.paymentMswipe(referenceId, sessionTokeniser, mWisePadController, mDrsEntity.getdocketno(), amount, phoneNumber);
        } else {
            // Not logged in
            // Show log in dialog
            Utility.showMswipeLoginDialog(PaymentActivity.this, mWisePadController);
        }
    }

    /**
     * Verify mswipe payment status
     */
    private void verifyMswipePayment() {
        MswipePaymentVerifyTask paymentTask = new MswipePaymentVerifyTask();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            paymentTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        else
            paymentTask.execute();
    }

    /**
     * Get document from xml string
     *
     * @param xml
     * @return
     */
    private Document getDomElement(String xml) {
        Document doc = null;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();

            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xml));
            doc = db.parse(is);

        } catch (ParserConfigurationException e) {
            Log.e("Error: ", e.getMessage());
            return null;
        } catch (SAXException e) {
            Log.e("Error: ", e.getMessage());
            return null;
        } catch (IOException e) {
            Log.e("Error: ", e.getMessage());
            return null;
        }
        // return DOM
        return doc;
    }

    /**
     * Extract Ezetap response
     *
     * @param resultCode
     * @param data
     * @return
     */
    private void paymentResponseEzetap(int resultCode, Intent data) {
        switch (resultCode) {
            case EzeConstants.RESULT_SUCCESS:
                try {
                    TransactionDetails transactionDetails = TransactionDetails.getTransactionDetails(new JSONObject(data.getStringExtra(EzeConstants.KEY_RESPONSE_DATA)));
                    String transactionId = transactionDetails.getTransactionId();
                    String cardType = transactionDetails.getCardType();
                    String authCode = transactionDetails.getAuthCode();
                    String receiptUrl = transactionDetails.getCustomerReceiptUrl();
                    mPaymentResponse.put(Constants.TRANSACTION_NO, transactionId);
                    mPaymentResponse.put(Constants.AUTH_CODE, authCode);
                    mPaymentResponse.put(Constants.CARD_TYPE, cardType);
                    mPaymentResponse.put(Constants.BANK_NAME, "");
                    mPaymentResponse.put(Constants.MODE_OF_PAYMENT, Constants.PAYMENT_TYPE_EZETAP);
                    mPaymentResponse.put(Constants.RECEIPT_URL, receiptUrl);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                showAlertDialog(PAYMENT_SUCCESS);
                break;
            case EzeConstants.RESULT_FAILED:
            default:
                String code = "";
                String message = "";
                try {
                    JSONObject responseJson = new JSONObject(data.getStringExtra(EzeConstants.KEY_RESPONSE_DATA));
                    code = responseJson.get(EzeConstants.KEY_ERROR_CODE).toString();
                    message = responseJson.get(EzeConstants.KEY_ERROR_MESSAGE).toString();
                } catch (Exception e) {
                    e.printStackTrace();
                    message = "Could not complete transaction.";
                }
                // Insert in database
                insertSwipeDetail(code, "Ezetap", message);
                if (message.contains(Constants.EZETAP_TRANSACTION_ERROR)) {
                    try {
                        mPaymentResponse.put(Constants.TRANSACTION_NO, "");
                        mPaymentResponse.put(Constants.AUTH_CODE, "");
                        mPaymentResponse.put(Constants.CARD_TYPE, "");
                        mPaymentResponse.put(Constants.BANK_NAME, "");
                        mPaymentResponse.put(Constants.MODE_OF_PAYMENT, Constants.PAYMENT_TYPE_EZETAP);
                        mPaymentResponse.put(Constants.RECEIPT_URL, "");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    goToPreviousScreen();
                } else {
                    showAlertDialog(PAYMENT_FAIL);
                }
                break;
        }
    }

    /**
     * check result after using Mswipe
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    private void paymentMswipeResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case WisePadController.MS_LOGIN_ACTIVITY_REQUEST_CODE:
                    String referenceId = data.getStringExtra(MswipeConstants.REFERENCE_ID);
                    String sessionTokeniser = data.getStringExtra(MswipeConstants.SESSION_TOKENISER);
                    Utility.saveToSharedPrefs(PaymentActivity.this, MswipeConstants.SESSION_TOKENISER, sessionTokeniser);
                    Utility.saveToSharedPrefs(PaymentActivity.this, MswipeConstants.REFERENCE_ID, referenceId);
                    boolean status = data.getExtras().getBoolean("status");
                    if (!status) {
                        Utility.showDialog(PaymentActivity.this, "Error", data.getExtras().getString("errMsg"));
                    } else {
                        Utility.showToast(PaymentActivity.this, "Login success");
                        startMswipePayment();
                    }
                    break;
                case WisePadController.MS_CARDSALE_ACTIVITY_REQUEST_CODE:
                    if (data != null) {
                        Bundle bundle = data.getExtras();
                        String transactionNo = bundle.getString("RRNo");
                        String authCode = bundle.getString("AuthCode");
                        String cardType = bundle.getString("SwitchCardType");
                        String bankName = "";
                        try {
                            mPaymentResponse.put(Constants.TRANSACTION_NO, transactionNo);
                            mPaymentResponse.put(Constants.AUTH_CODE, authCode);
                            mPaymentResponse.put(Constants.CARD_TYPE, cardType);
                            mPaymentResponse.put(Constants.BANK_NAME, bankName);
                            mPaymentResponse.put(Constants.MODE_OF_PAYMENT, Constants.PAYMENT_TYPE_MSWIPE);
                            mPaymentResponse.put(Constants.RECEIPT_URL, "");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    showAlertDialog(PAYMENT_SUCCESS);
                    break;
                default:
                    break;
            }
        } else if (resultCode == RESULT_CANCELED) {
            printResponse(data);
            if (data != null && data.hasExtra("newversoinavailable")) {
                Log.v("Test", "Creditsaleview data is null");
                String newversionmsg = data.getExtras().getString("newversoinavailable");
                if (newversionmsg.equalsIgnoreCase("true")) {
                    mWisePadController.processUpdateMswipeApplication();
                }
            } else if (data != null && data.hasExtra("errMsg")) {
                String message = data.getExtras().getString("errMsg");
                insertSwipeDetail("", "Mswipe", message);
                if (message.equalsIgnoreCase(Constants.MSWIPE_TRANSACTION_ERROR)) {
                    try {
                        mPaymentResponse.put(Constants.TRANSACTION_NO, "");
                        mPaymentResponse.put(Constants.AUTH_CODE, "");
                        mPaymentResponse.put(Constants.CARD_TYPE, "");
                        mPaymentResponse.put(Constants.BANK_NAME, "");
                        mPaymentResponse.put(Constants.MODE_OF_PAYMENT, Constants.PAYMENT_TYPE_MSWIPE);
                        mPaymentResponse.put(Constants.RECEIPT_URL, "");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    goToPreviousScreen();
                } else {
                    showAlertDialog(PAYMENT_FAIL);
                }
            }
        }
    }

    /**
     * Insert swipe response in database
     *
     * @param code
     * @param deviceType
     * @param reason
     */
    private void insertSwipeDetail(String code, String deviceType, String reason) {
        SwipeEntity swipeEntity = new SwipeEntity();
        swipeEntity.setdocket_no(mDrsEntity.getdocketno());
        swipeEntity.setdrs_no(mDrsEntity.getdrsno());
        swipeEntity.setdevice_type(deviceType);
        swipeEntity.setstatus("Fail: " + code);
        swipeEntity.setreason(reason);
        swipeEntity.setdatetime(Utility.getDeliveryTime());
        swipeEntity.setsync("0");

        SwipeHelper.getInstance().insertSwipe(swipeEntity);
    }

    private void printResponse(Intent data) {
        if (data != null) {
            Bundle bundle = data.getExtras();
            for (String key : bundle.keySet()) {
                Object value = bundle.get(key);
                System.out.println("\nKey: " + key + " ==== Value: " + value);
            }
        }
    }


    /**
     * Show dialog for editing amount
     */
    private void showPaymentEditDialog() {
        float amount = Float.parseFloat(mCODAmount);
        minPayment = amount + Constants.MINIMUM_AMOUNT;
        maxPayment = amount + Constants.MAXIMUM_AMOUNT;
        LinearLayout linearLayout = new LinearLayout(PaymentActivity.this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        // Contact TextView
        final EditText paymentEditText = new EditText(PaymentActivity.this);
        paymentEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_NUMBER);
        paymentEditText.setPadding(mEight, mEight, mEight, mEight);
        paymentEditText.setText(mCODAmount);
        paymentEditText.setTextColor(Color.BLACK);

        linearLayout.addView(paymentEditText);

        AlertDialog.Builder builder = new AlertDialog.Builder(PaymentActivity.this);
        builder.setTitle("Payment");
        builder.setView(linearLayout);
        builder.setCancelable(false);

        builder.setPositiveButton(android.R.string.ok, null);

        final AlertDialog alertDialog = builder.create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {

                Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        String payment = paymentEditText.getText().toString();
                        if (payment == null || TextUtils.isEmpty(payment) || payment.equalsIgnoreCase("")) {
                            Utility.showToast(PaymentActivity.this, "Amount should be between " + minPayment + " and " + maxPayment);
                        } else {
                            try {
                                float amount = Float.parseFloat(paymentEditText.getText().toString());
                                if (amount < minPayment || amount > maxPayment) {
                                    Utility.showToast(PaymentActivity.this, "Amount should be between " + minPayment + " and " + maxPayment);
                                } else {
                                    mCODAmount = String.valueOf(amount);
                                    PaymentActivity.this.getSupportActionBar().setTitle("Payment: Rs " + mCODAmount);
                                    alertDialog.dismiss();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                Utility.showToast(PaymentActivity.this, "Please enter valid amount");
                            }
                        }
                    }
                });
            }
        });

        alertDialog.show();
    }

    @Override
    public void onError(String error, int errorCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Mswipe APKKit");
        builder.setMessage(error);
        builder.setPositiveButton("OK", null);
        builder.create().show();
    }

    @Override
    public void onMswipeAppInstalled() {

    }

    @Override
    public void onMswipeAppUpdated() {

    }

    /**
     * Show alert dialog after payment is success/fail
     *
     * @param message
     */
    private void showAlertDialog(final String message) {
        // If payment is successful, update its payment status
        if (message.equalsIgnoreCase(PAYMENT_SUCCESS)) {
            PaymentStatusHelper.getInstance().updatePaymentStatus(mDrsEntity.getdrs_docket(), Constants.SUCCESS);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(PaymentActivity.this);
        builder.setMessage(message);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (message.equalsIgnoreCase(PAYMENT_SUCCESS)) {
                    goToPreviousScreen();
                }
            }
        });

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Show alert dialog when token is expired
     *
     * @param message
     */

    private void showAlertTokenDialog(final String message) {
        // If payment is successful, update its payment status
        AlertDialog.Builder builder = new AlertDialog.Builder(PaymentActivity.this);
        builder.setMessage(message);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (message.equalsIgnoreCase(PAYMENT_FAIL)) {
                    getAccessToken();
                }
            }
        });

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Show progress dialog
     *
     * @param message
     */
    public void showProgress(String message) {
        if (progressDialog != null && !progressDialog.isShowing()) {
            progressDialog.show();
            progressDialog.setMessage(message);
            progressDialog.setCancelable(false);
        }
    }

    /**
     * Hide progress dialog
     */
    public void hideProgress() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    /**
     * Mswipe payment verification task
     */
    class MswipePaymentVerifyTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress("Verifying Mswipe payment...");
        }

        @Override
        protected String doInBackground(Void... params) {
            String request = getMswipePaymentRequest();
            String response = getMswipePaymentResponse(Constants.MSWIPE_PAYMENT_VERIFY_URL, request);
            return response;
//            return getPaymentStatus();
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            hideProgress();
            // Parse response
            XmlDomParser parser = new XmlDomParser();
            Document document = getDomElement(response);
            NodeList statusNodeList = document.getElementsByTagName("ResponseStatus");

            // Get response status
            String status = "";
            int length = statusNodeList.getLength();
            for (int i = 0; i < length; i++) {
                Element element = (Element) statusNodeList.item(i);
                status = parser.getValue(element, "Status");
                break;
            }

            // If status is true then get the repsonse message
            String paymentStatus = "", transactionNo = "", authCode = "", cardType = "";
            float amount = 0.00F;
            if (status != null && status.equalsIgnoreCase("True")) {
                NodeList messageNodeList = document.getElementsByTagName("ResponseData");
                int len = messageNodeList.getLength();
                for (int i = 0; i < len; i++) {
                    Element element = (Element) messageNodeList.item(i);
                    paymentStatus = parser.getValue(element, "ResponseMsg");
                    transactionNo = parser.getValue(element, "RRNO");
                    authCode = parser.getValue(element, "AuthCode");
                    // If payment status is approved then get the amount
                    if (paymentStatus != null && paymentStatus.contains("approved")) {
                        String amt = parser.getValue(element, "Amount");
                        amount = amount + Float.parseFloat(amt);
                    }
                }
            }

            // If amount is positive then payment is done
            // If amount is 0 then its void payment, start again
            // Else start payment
            if ((int) amount > 0) {
                try {
                    mPaymentResponse.put(Constants.TRANSACTION_NO, transactionNo);
                    mPaymentResponse.put(Constants.AUTH_CODE, authCode);
                    mPaymentResponse.put(Constants.CARD_TYPE, "");
                    mPaymentResponse.put(Constants.BANK_NAME, "");
                    mPaymentResponse.put(Constants.MODE_OF_PAYMENT, Constants.PAYMENT_TYPE_MSWIPE);
                    mPaymentResponse.put(Constants.RECEIPT_URL, "");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                showAlertDialog(PAYMENT_SUCCESS);
            } else {
                // Continue mswipe normal payment process
                normalMswipePayment();
            }
        }
    }

    /**
     * Get mswipe payment request
     *
     * @return
     */
    private String getMswipePaymentRequest() {
        String request = "<x:Envelope xmlns:x=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:msw=\"MSWIPEUTIL\">" +
                "<x:Header>" +
                "<msw:MyHeader>" +
                "<msw:Client_Code>" + MswipeConstants.CLIENT_CODE_VALUE + "</msw:Client_Code>" +
                "<msw:UserId>" + MswipeConstants.USER_ID_VALUE + "</msw:UserId>" +
                "<msw:UserPwd>" + MswipeConstants.USER_PWD_VALUE + "</msw:UserPwd>" +
                "</msw:MyHeader>" +
                "</x:Header>" +
                "<x:Body>" +
                "<msw:VerifyTxnUsingRefNo>" +
                "<msw:RefNo>" + mDrsEntity.getdocketno() + "</msw:RefNo>" +
                "</msw:VerifyTxnUsingRefNo>" +
                "</x:Body>" +
                "</x:Envelope>";
        return request;
    }

    /**
     * Get mswipe payemnt response
     *
     * @param URL
     * @param request
     * @return
     */
    public String getMswipePaymentResponse(String URL, String request) {
        HttpPost httpPost = new HttpPost(URL);
        StringEntity entity;
        String response_string = null;
        try {
            entity = new StringEntity(request, HTTP.UTF_8);
            httpPost.setHeader("Content-Type", "text/xml;charset=UTF-8");
            httpPost.setEntity(entity);
            HttpClient client = new DefaultHttpClient();
            HttpResponse response = client.execute(httpPost);
            response_string = EntityUtils.toString(response.getEntity());
            Log.d("request", response_string);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response_string;
    }


    /**
     * Payment using Trupay
     */
    private void startTruPayPayment() {
        if (Utility.isInternetConnected(PaymentActivity.this)) {
            String authToken = Utility.getFromSharedPrefs(PaymentActivity.this, Constants.ACCESS_TOKEN);
            String tokeType = Utility.getFromSharedPrefs(PaymentActivity.this, Constants.TOKEN_TYPE);
            String auth = authToken + " " + tokeType;
            Log.e("auth: ", authToken + "" + auth);
            getAccessToken();
        } else {
            Utility.showToast(PaymentActivity.this, "Internet not connected");
        }
    }

    /**
     * Payment using Free Charge
     */
    private void startFreeRechargePayment() {
        if (Utility.isInternetConnected(PaymentActivity.this)) {
            getFCToken();
        } else {
            Utility.showToast(PaymentActivity.this, "Internet not connected");
        }
    }


    /**
     * Function to show TruPay alert dialog
     * On pressing status  button then will get transaction status and on cancel we cancel the transaction
     */
    public void showTruPayAlert(final Context context, String message, final String buttonMsg) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setCancelable(false);

        // Setting Dialog Title
        alertDialog.setTitle("TruPay Payment");

        // Setting Dialog Message
        alertDialog.setMessage(message);

        // On pressing Update/Status button
        alertDialog.setPositiveButton(buttonMsg, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (buttonMsg.equalsIgnoreCase("Update")) {
                    try {
                        mPaymentResponse.put(Constants.TRANSACTION_NO, trupayRefNo);
                        mPaymentResponse.put(Constants.AUTH_CODE, "");
                        mPaymentResponse.put(Constants.CARD_TYPE, "");
                        mPaymentResponse.put(Constants.BANK_NAME, "");
                        mPaymentResponse.put(Constants.MODE_OF_PAYMENT, Constants.PAYMENT_TYPE_TRUPAY);
                        mPaymentResponse.put(Constants.RECEIPT_URL, "");
                        Utility.showToast(PaymentActivity.this, "Payment Done Sucessfully.");
//                      showAlertDialog(PAYMENT_SUCCESS);
                        goToPreviousScreen();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
//                    Utility.showToast(PaymentActivity.this, "Your Trupay RefId is " + trupayRefNo);
                    getTranscationStatus(trupayRefNo);
                }
                dialog.dismiss();
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                cancelTransaction("Manual Cancel");
                dialog.dismiss();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    // get access token from TruPay for authorization
    public void getAccessToken() {
        showProgress("Starting TruPay payment...");
        // make generate token api request
        StringRequest tokenRequest = new StringRequest(Request.Method.POST, TruPayConstants.GENERATE_TOKEN_URL, new Response.Listener<String>() {

            @Override

            public void onResponse(String response) {
                try {
                    System.out.println("RESPONSE:          >>> " + response + "<<<");
                    JSONObject json_Response = new JSONObject(response);
                    String authToken = json_Response.getString("access_token");
                    String tokeType = json_Response.getString("token_type");
                    String auth = authToken + " " + tokeType;
                    Log.i("auth", auth);
                    if (authToken != null && !TextUtils.isEmpty(authToken) && !authToken.equals("")) {
                        // save access token and token type in shared preferences
                        Utility.saveToSharedPrefs(PaymentActivity.this, Constants.ACCESS_TOKEN, authToken);
                        Utility.saveToSharedPrefs(PaymentActivity.this, Constants.TOKEN_TYPE, tokeType);
                        hideProgress();
                        showProgress("Verifying TruPay payment...");
                        requestingMoneyFromCustomer();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.i("exception error", e + "");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                hideProgress();
                Utility.showToast(PaymentActivity.this, "Server is not reachable.");
                Log.i("error", error + "");
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("grant_type", Utility.getFromSharedPrefs(PaymentActivity.this, TruPayConstants.TRUPAY_GRANTTYPE));
                params.put("client_id", Utility.getFromSharedPrefs(PaymentActivity.this, TruPayConstants.TRUPAY_CLIENT_ID));
                params.put("client_secret", Utility.getFromSharedPrefs(PaymentActivity.this, TruPayConstants.TRUPAY_CLIENT_SECRET));
                params.put("username", Utility.getFromSharedPrefs(PaymentActivity.this, TruPayConstants.TRUPAY_USERNAME));
                params.put("password", Utility.getFromSharedPrefs(PaymentActivity.this, TruPayConstants.TRUPAY_PASSWORD));
                return params;
            }
        };
        TaskForceApplication.getInstance().addToRequestQueue(tokenRequest);
    }

    // requesting money from customer(TruPay)
    public void requestingMoneyFromCustomer() {
        JSONObject requestJson = new JSONObject();
        try {
            requestJson.put("merchantId", Utility.getFromSharedPrefs(PaymentActivity.this, TruPayConstants.MERCHANT_ID));
            requestJson.put("customerMobNum", mDrsEntity.getcsgeteleno());
            requestJson.put("requestedAmount", mDrsEntity.getcod_amt());
            requestJson.put("remarks", "requesting money from customer");
            requestJson.put("transactionRefNumber", mDrsEntity.getdrs_docket());
            requestJson.put("collectorMobNo", userEntity.getmobile_no());
            Log.i("cash Amount", mDrsEntity.getcod_amt() + " " + mDrsEntity.getcsgeteleno() + "  " + userEntity.getmobile_no());
            Log.i("json", requestJson + "");
            Log.i("url", TruPayConstants.REQUEST_MONEY_URL);
        } catch (JSONException je) {
            je.printStackTrace();
        }
        Log.i("json", TruPayConstants.REQUEST_MONEY_URL);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, TruPayConstants.REQUEST_MONEY_URL, requestJson, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                System.out.println("result: " + jsonObject.toString());
                try {
                    int requestStatus = jsonObject.getInt("requestStatus");
                    if (requestStatus == 1 || requestStatus == 2) {
                        trupayRefNo = jsonObject.getString("trupayRefNumber");
                        showTruPayAlert(PaymentActivity.this, "Verifying Your TruPay Payment.", buttonMsg);
                    }
                    // Check request status is rejected or fail
//                    else if (requestStatus == 3 || requestStatus ==4) { // request status is rejected or expired
//                        String description = jsonObject.getString("description");
//                        String errorCode = jsonObject.getString("errorCode");
//                        insertSwipeDetail(errorCode, "TP", description);
//                        showAlertDialog(PAYMENT_FAIL);
//                    }
                    else {
                        String description = jsonObject.getString("description");
                        String errorCode = jsonObject.getString("errorCode");
                        insertSwipeDetail(errorCode, "TP", description);
                        showAlertDialog(PAYMENT_FAIL);
                    }
                } catch (JSONException e) {
                    Log.i("exception msg", e + "");
                    e.printStackTrace();
                }

                hideProgress();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                hideProgress();
                Utility.showToast(PaymentActivity.this, "Server Error");
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                String authToken = Utility.getFromSharedPrefs(PaymentActivity.this, Constants.ACCESS_TOKEN);
                String tokeType = Utility.getFromSharedPrefs(PaymentActivity.this, Constants.TOKEN_TYPE);
                String auth = String.format("%s %s", tokeType, authToken);
                Log.i("auth1", auth);
                params.put("Authorization", auth);
                params.put("Content-Type", "application/json; charset=utf-8");
                return params;
            }
        };
        TaskForceApplication.getInstance().addToRequestQueue(jsonObjectRequest);
    }


    // get transcation satus from TruPay
    public void getTranscationStatus(final String trupayRefNo) {
        // increase count by 1
        count = count + 1;
        showProgress("Getting Payment Status");
        JSONObject requestJson = new JSONObject();
        try {
            // get today date
            Date date = Calendar.getInstance().getTime();
            DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            String today = formatter.format(date);
            requestJson.put("merchantId", Utility.getFromSharedPrefs(PaymentActivity.this, TruPayConstants.MERCHANT_ID));
            requestJson.put("customerMobNum", mDrsEntity.getcsgeteleno());
            requestJson.put("trupayRefNumber", trupayRefNo);
            requestJson.put("transactionDate", today);
            requestJson.put("transactionRefNumber", mDrsEntity.getdrs_docket());
            Log.i("ststus json", requestJson + "");
            Log.i("url status", TruPayConstants.TRANSACTION_STATUS_URL);
        } catch (JSONException je) {
            je.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, TruPayConstants.TRANSACTION_STATUS_URL, requestJson, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                System.out.println("result: " + jsonObject.toString());
                try {
                    // check payment status
                    int paymentStatus = jsonObject.getInt("paymentStatus");
                    if (paymentStatus == 1 || paymentStatus == 3) {
                        buttonMsg = "Update";
                        showTruPayAlert(PaymentActivity.this, "Payement Sucess.", buttonMsg);
                    } else if (paymentStatus == 0) {
                        if (count == 5) {
                            count = 0;
                            showProgress("Cancelling Trupay Transaction");
                            cancelTransaction("Auto Cancel");
                        } else {
                            showTruPayAlert(PaymentActivity.this, "Verifying Your TruPay Payment.", "Status");
                        }
                    } else {
                        String errorMsg = jsonObject.getString("description");
                        String errorCode = jsonObject.getString("errorCode");
                        insertSwipeDetail(errorCode, "TP", errorMsg);
                        showAlertDialog(PAYMENT_FAIL);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                hideProgress();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                hideProgress();
                Utility.showToast(PaymentActivity.this, "Server Error");
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                String authToken = Utility.getFromSharedPrefs(PaymentActivity.this, Constants.ACCESS_TOKEN);
                String tokeType = Utility.getFromSharedPrefs(PaymentActivity.this, Constants.TOKEN_TYPE);
                String auth = String.format("%s %s", tokeType, authToken);
                params.put("Authorization", auth);
                params.put("Content-Type", "application/json; charset=utf-8");
                return params;
            }
        };
        TaskForceApplication.getInstance().addToRequestQueue(jsonObjectRequest);

    }

    // cancel TruPay Transaction
    public void cancelTransaction(final String ErrorMessage) {
        showProgress("Cancelling Trupay Transaction");
        JSONObject requestJson = new JSONObject();
        try {
            requestJson.put("merchantId", Utility.getFromSharedPrefs(PaymentActivity.this, TruPayConstants.MERCHANT_ID));
            requestJson.put("trupayRefNumber", trupayRefNo);
            requestJson.put("transactionRefNumber", mDrsEntity.getdrs_docket());
            requestJson.put("Remarks", ErrorMessage);
        } catch (JSONException je) {
            je.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, TruPayConstants.CANCEL_TRANSACTION_STATUS_URL, requestJson, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                System.out.println("cancel result: " + jsonObject.toString());
                try {
                    System.out.println("RESPONSE:          >>> " + jsonObject.toString() + "<<<");
                    int requestStatus = jsonObject.getInt("requestStatus");
                    int paymentStatus = jsonObject.getInt("paymentStatus");
                    if (requestStatus == 5 || requestStatus == 6 && paymentStatus == 4 || paymentStatus == 5) {
                        insertSwipeDetail(jsonObject.getString("errorCode"), "TP", ErrorMessage);
                        showAlertDialog(PAYMENT_FAIL);
                    }
//                    else {
//                        showTruPayAlert(PaymentActivity.this, "You Cannot Cancelled Transaction", buttonMsg);
//                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                hideProgress();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                hideProgress();
                Utility.showToast(PaymentActivity.this, "Sever Error");
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                String authToken = Utility.getFromSharedPrefs(PaymentActivity.this, Constants.ACCESS_TOKEN);
                String tokeType = Utility.getFromSharedPrefs(PaymentActivity.this, Constants.TOKEN_TYPE);
                String auth = String.format("%s %s", tokeType, authToken);
                params.put("Authorization", auth);
                params.put("Content-Type", "application/json; charset=utf-8");
                return params;
            }
        };
        TaskForceApplication.getInstance().addToRequestQueue(jsonObjectRequest);

    }


    // payment via free Charge

    public void EnterOTPDialog(Context context) {
        final Dialog dialog = new Dialog(context, R.style.customDialogTheme);
        dialog.setContentView(R.layout.free_recharge_otp_dialog);
        final EditText otpNumberEditText = (EditText) dialog.findViewById(R.id.dialog_edittext);
        otpNumberEditText.setHint("Enter OTP");
        Button doneButton = (Button) dialog.findViewById(R.id.dialog_done_button);

        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String getOTP = otpNumberEditText.getText().toString().trim();
                if (getOTP.isEmpty()) {
                    Utility.showToast(PaymentActivity.this, "Please Enter OTP");
                } else {
                    sendOTP(getOTP);
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    dialog.dismiss();
                }

            }
        });

        dialog.show();
    }

    // Verify Phone Number of Customer with Free Charge Accunt
    public void showPhoneNumberDialog(Context context) {
        final Dialog dialog = new Dialog(context, R.style.customDialogTheme);
        dialog.setContentView(R.layout.dialog_otp_phone_number);
        final EditText phoneNumberEditText = (EditText) dialog.findViewById(R.id.dialog_edittext);
        Log.i("phone Number", mDrsEntity.getcsgeteleno());
        phoneNumberEditText.setText(mDrsEntity.getcsgeteleno());
        TextView amounttxt = (TextView) dialog.findViewById(R.id.dialog_text_amount);
        amounttxt.setText("Your Docket Amount is Rs. " + mDrsEntity.getcod_amt());
        Button doneButton = (Button) dialog.findViewById(R.id.dialog_done_button);

        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String get_phoneNumber = phoneNumberEditText.getText().toString().trim();
                customerMobileNo = get_phoneNumber;
                if (customerMobileNo.isEmpty()) {
                    Utility.showToast(PaymentActivity.this, "Please Enter Customer Mobile Number.");
                } else {
                    initiateTransaction(customerMobileNo);
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    dialog.dismiss();
                }

            }
        });

        dialog.show();
    }

    /**
     * Function to show TruPay alert dialog
     * On pressing status  button then will get transaction status and on cancel we cancel the transaction
     */
    public void showFreeRechargeAlert(final Context context, String message) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setCancelable(false);

        // Setting Dialog Title
        alertDialog.setTitle("FreeCharge Payment");

        // Setting Dialog Message
        alertDialog.setMessage(message);

        // On pressing Update/Status button
        alertDialog.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                try {
                    FreeRechargeConstants.FC_COUNT = 0;
                    mPaymentResponse.put(Constants.TRANSACTION_NO, fc_ServerID);
                    mPaymentResponse.put(Constants.AUTH_CODE, "");
                    mPaymentResponse.put(Constants.CARD_TYPE, "");
                    mPaymentResponse.put(Constants.BANK_NAME, customerMobileNo);// In case of freeRecharge we have send customer wallet mobile number instead of bank name//
                    mPaymentResponse.put(Constants.MODE_OF_PAYMENT, Constants.PAYMENT_TYPE_FREE_RECHARGE);
                    mPaymentResponse.put(Constants.RECEIPT_URL, "");
                    Utility.showToast(PaymentActivity.this, "Payment Done Sucessfully.");
                    goToPreviousScreen();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                cancelFCTransaction();
                dialog.dismiss();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }


    // get access token for free charge
    void getFCToken() {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            StringBuilder hash = new StringBuilder(Utility.getFromSharedPrefs(PaymentActivity.this, FreeRechargeConstants.FC_MERCHANT_ID)); // merchantId
            hash.append(Utility.getFromSharedPrefs(PaymentActivity.this, FreeRechargeConstants.FC_TERMINAL_ID)); // terminalId
            hash.append(currentMilliSeconds + FreeRechargeConstants.FC_COUNT); // transaction reference number to be passed
            hash.append(Utility.getFromSharedPrefs(PaymentActivity.this, FreeRechargeConstants.FC_SALT)); // salt
            md.reset();
            byte[] bytesOfMessage = hash.toString().getBytes("UTF-8");
            md.update(bytesOfMessage);
            byte[] messageDigest = md.digest();
            StringBuilder hexString = new StringBuilder();
            for (int i = 0; i < messageDigest.length; i++) {
                String hex = Integer.toHexString(0xFF & messageDigest[i]);
                if (hex.length() == 1) {
                    hexString.append("0");
                }
                hexString.append(hex);
            }
            fcAuthToken = hexString.toString();
            Log.i("Access Token", fcAuthToken);

            if (!fcAuthToken.equals("")) {
                showPhoneNumberDialog(PaymentActivity.this);
            } else {
                hideProgress();
                Utility.showToast(PaymentActivity.this, "Token not found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("Exception ", e + "");
//            hideProgress();
            Utility.showToast(PaymentActivity.this, "Token not find.");
        }
    }

    // Initiate transaction from app to Free Charge Server
    void initiateTransaction(String phoneNumber) {

        showProgress("Initiate Transaction with FreeCharge...");

        JSONObject requestJson = new JSONObject();
        try {
            requestJson.put("merchantID", Utility.getFromSharedPrefs(PaymentActivity.this, FreeRechargeConstants.FC_MERCHANT_ID));
            requestJson.put("terminalID", Utility.getFromSharedPrefs(PaymentActivity.this, FreeRechargeConstants.FC_TERMINAL_ID));
            requestJson.put("transactionRefNumber", currentMilliSeconds + FreeRechargeConstants.FC_COUNT);
            requestJson.put("walletID", phoneNumber);
            requestJson.put("txnAmount", mDrsEntity.getcod_amt());
            requestJson.put("txnDatenTime", Utility.getDateForFR());
            requestJson.put("ProcCode", Utility.getFromSharedPrefs(PaymentActivity.this, FreeRechargeConstants.FC_PROC_CODE));
            requestJson.put("authToken", fcAuthToken);

            // create json for addition info
            JSONObject additionalInfo = new JSONObject();
            additionalInfo.put("Tag", mDrsEntity.getdocketno());
            additionalInfo.put("Value", mDrsEntity.getdrsno());
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(additionalInfo);

            requestJson.put("AdditinalInfo", jsonArray);
            Log.i("AdditinalInfo", jsonArray + "");
            Log.i("requestJson", requestJson + "");

        } catch (JSONException je) {
            je.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, FreeRechargeConstants.FR_INITIATE_TRANSACTION_URL, requestJson, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                System.out.println("result: " + jsonObject.toString());
                try {
                    System.out.println("RESPONSE:          >>> " + jsonObject.toString() + "<<<");
                    if (jsonObject.getString("responseCode").equals("00")) {
                        EnterOTPDialog(PaymentActivity.this);
                    }
//                    else if (jsonObject.getString("responseCode").equals("ER-1001") || jsonObject.getString("responseCode").equals("ER-1003") || jsonObject.getString("responseCode").equals("ER-1004") ||
//                            jsonObject.getString("responseCode").equals("ER-1005") || jsonObject.getString("responseCode").equals("ER-1006") || jsonObject.getString("responseCode").equals("ER-1007") ||
//                            jsonObject.getString("responseCode").equals("ER-1008") || jsonObject.getString("responseCode").equals("ER-1009")) {
//                        FreeRechargeConstants.FC_COUNT = FreeRechargeConstants.FC_COUNT + 1;
//                        String description = jsonObject.getString("responseDescription");
//                        String errorCode = jsonObject.getString("responseCode");
//                        Utility.showToast(PaymentActivity.this, "Some Internal Error Occurred.");
//                        insertSwipeDetail(errorCode, "FC", description);
//                        showAlertDialog(PAYMENT_FAIL);
//                    } else if (jsonObject.getString("responseCode").equals("ER-1002")) {
//                        FreeRechargeConstants.FC_COUNT = FreeRechargeConstants.FC_COUNT + 1;
//                        String description = jsonObject.getString("responseDescription");
//                        String errorCode = jsonObject.getString("responseCode");
//                        Utility.showToast(PaymentActivity.this, "Trasaction Failed.");
//                        insertSwipeDetail(errorCode, "FC", description);
//                        showAlertDialog(PAYMENT_FAIL);
//                    } else if (jsonObject.getString("responseCode").equals("ER-3014")) {
//                        FreeRechargeConstants.FC_COUNT = FreeRechargeConstants.FC_COUNT + 1;
//                        String description = jsonObject.getString("responseDescription");
//                        String errorCode = jsonObject.getString("responseCode");
//                        insertSwipeDetail(errorCode, "FC", description);
//                        Utility.showToast(PaymentActivity.this, "Customer does not have a wallet Account.");
//                        showAlertDialog(PAYMENT_FAIL);
//                    } else if (jsonObject.getString("responseCode").equals("ER-500") || jsonObject.getString("responseCode").equals("ER-7012") || jsonObject.getString("responseCode").equals("ER-7005")) {
//                        FreeRechargeConstants.FC_COUNT = FreeRechargeConstants.FC_COUNT + 1;
//                        insertSwipeDetail(jsonObject.getString("responseCode"), "FC", jsonObject.getString("responseDescription"));
//                        Utility.showToast(PaymentActivity.this, "Internal Server Error Please Try after somtime.");
//                        showAlertDialog(PAYMENT_FAIL);
//                    } else if (jsonObject.getString("responseCode").equals("ER-3015")) {
//                        FreeRechargeConstants.FC_COUNT = FreeRechargeConstants.FC_COUNT + 1;
//                        insertSwipeDetail(jsonObject.getString("responseCode"), "FC", jsonObject.getString("responseDescription"));
//                        Utility.showToast(PaymentActivity.this, "Unable to send OTP, Send new request.");
//                        showAlertDialog(PAYMENT_FAIL);
//                    } else if (jsonObject.getString("responseCode").equals("ER-3016")) {
//                        FreeRechargeConstants.FC_COUNT = FreeRechargeConstants.FC_COUNT + 1;
//                        insertSwipeDetail(jsonObject.getString("responseCode"), "FC", jsonObject.getString("responseDescription"));
//                        Utility.showToast(PaymentActivity.this, "Wallet Account Issue, Please contact customer care.");
//                        showAlertDialog(PAYMENT_FAIL);
//                    }
                    else {
                        FreeRechargeConstants.FC_COUNT = FreeRechargeConstants.FC_COUNT + 1;
                        String description = jsonObject.getString("responseDescription");
                        String errorCode = jsonObject.getString("responseCode");
                        Utility.showToast(PaymentActivity.this, description);
                        insertSwipeDetail(errorCode, "FC", description);
                        showAlertDialog(PAYMENT_FAIL);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    FreeRechargeConstants.FC_COUNT = FreeRechargeConstants.FC_COUNT + 1;
                    Log.i("Exception", e + "");
                }
                hideProgress();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                Utility.showToast(PaymentActivity.this, "Server is not reachable.");
                hideProgress();
            }
        });
        TaskForceApplication.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    // send OTP to Free Charge Server for payment
    public void sendOTP(String otp) {

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);

        showProgress("Sending OTP to FreeCharge.....");

        JSONObject requestJson = new JSONObject();
        try {
            requestJson.put("merchantID", Utility.getFromSharedPrefs(PaymentActivity.this, FreeRechargeConstants.FC_MERCHANT_ID));
            requestJson.put("terminalID", Utility.getFromSharedPrefs(PaymentActivity.this, FreeRechargeConstants.FC_TERMINAL_ID));
            requestJson.put("transactionRefNumber", currentMilliSeconds + FreeRechargeConstants.FC_COUNT);
            requestJson.put("walletID", customerMobileNo);
            requestJson.put("txnAmount", mDrsEntity.getcod_amt());
            requestJson.put("txnDatenTime", Utility.getDateForFR());
            requestJson.put("ProcCode", Utility.getFromSharedPrefs(PaymentActivity.this, FreeRechargeConstants.FC_PROC_CODE));
            requestJson.put("OTP", otp);// Otp came on customer phone
            requestJson.put("authToken", fcAuthToken);
            // create json for addition info
            JSONObject additionalInfo = new JSONObject();
            additionalInfo.put("Tag", mDrsEntity.getdocketno());
            additionalInfo.put("Value", mDrsEntity.getdrsno());
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(additionalInfo);

            requestJson.put("AdditinalInfo", jsonArray);
            Log.i("AdditinalInfo", jsonArray + "");
            Log.i("requestJson", requestJson + "");
        } catch (JSONException je) {
            je.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, FreeRechargeConstants.FR_OTP_STATUS_URL, requestJson, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                System.out.println("result: " + jsonObject.toString());
                try {
                    System.out.println("RESPONSE:          >>> " + jsonObject.toString() + "<<<");
                    if (jsonObject.getString("responseCode").equals("00")) {
                        fc_ServerID = jsonObject.getString("txnId");
                        showFreeRechargeAlert(PaymentActivity.this, "Your Payment done.");
                    }
//                    else if (jsonObject.getString("responseCode").equals("ER-3012")) {
//                        Utility.showToast(PaymentActivity.this, "InCorrect OTP.");
//                        FreeRechargeConstants.FC_COUNT = FreeRechargeConstants.FC_COUNT + 1;
//                        String description = jsonObject.getString("responseDescription");
//                        String errorCode = jsonObject.getString("responseCode");
//                        insertSwipeDetail(errorCode, "FC", description);
//                        showAlertDialog(PAYMENT_FAIL);
//                    } else if (jsonObject.getString("responseCode").equals("ER-1001") || jsonObject.getString("responseCode").equals("ER-1003") || jsonObject.getString("responseCode").equals("ER-1004") ||
//                            jsonObject.getString("responseCode").equals("ER-1005") || jsonObject.getString("responseCode").equals("ER-1006") || jsonObject.getString("responseCode").equals("ER-1007") ||
//                            jsonObject.getString("responseCode").equals("ER-1008") || jsonObject.getString("responseCode").equals("ER-1009")) {
//                        FreeRechargeConstants.FC_COUNT = FreeRechargeConstants.FC_COUNT + 1;
//                        String description = jsonObject.getString("responseDescription");
//                        String errorCode = jsonObject.getString("responseCode");
//                        Utility.showToast(PaymentActivity.this, "Some Internal Error Occurred.");
//                        insertSwipeDetail(errorCode, "FC", description);
//                        showAlertDialog(PAYMENT_FAIL);
//                    } else if (jsonObject.getString("responseCode").equals("ER-1002")) {
//                        FreeRechargeConstants.FC_COUNT = FreeRechargeConstants.FC_COUNT + 1;
//                        String description = jsonObject.getString("responseDescription");
//                        String errorCode = jsonObject.getString("responseCode");
//                        insertSwipeDetail(errorCode, "FC", description);
//                        showAlertDialog(PAYMENT_FAIL);
//                    } else if (jsonObject.getString("responseCode").equals("ER-2001")) {
//                        FreeRechargeConstants.FC_COUNT = FreeRechargeConstants.FC_COUNT + 1;
//                        insertSwipeDetail(jsonObject.getString("responseCode"), "FC", jsonObject.getString("responseDescription"));
//                        Utility.showToast(PaymentActivity.this, jsonObject.getString("responseDescription"));
//                        showAlertDialog(PAYMENT_FAIL);
//                    } else if (jsonObject.getString("responseCode").equals("ER-3001")) {
//                        FreeRechargeConstants.FC_COUNT = FreeRechargeConstants.FC_COUNT + 1;
//                        insertSwipeDetail(jsonObject.getString("responseCode"), "FC", jsonObject.getString("responseDescription"));
//                        Utility.showToast(PaymentActivity.this, "Insufficient balance in Wallet.");
//                        showAlertDialog(PAYMENT_FAIL);
//                    } else if (jsonObject.getString("responseCode").equals("ER-500") || jsonObject.getString("responseCode").equals("ER-7012") || jsonObject.getString("responseCode").equals("ER-7005")) {
//                        FreeRechargeConstants.FC_COUNT = FreeRechargeConstants.FC_COUNT + 1;
//                        insertSwipeDetail(jsonObject.getString("responseCode"), "FC", jsonObject.getString("responseDescription"));
//                        Utility.showToast(PaymentActivity.this, "Internal Server Error Please Try after somtime.");
//                        showAlertDialog(PAYMENT_FAIL);
//                    }
                    else {
                        FreeRechargeConstants.FC_COUNT = FreeRechargeConstants.FC_COUNT + 1;
                        String description = jsonObject.getString("responseDescription");
                        String errorCode = jsonObject.getString("responseCode");
                        Utility.showToast(PaymentActivity.this, description);
                        insertSwipeDetail(errorCode, "FC", description);
                        showAlertDialog(PAYMENT_FAIL);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.i("Exception msg", e + "");
                }
                hideProgress();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                Utility.showToast(PaymentActivity.this, "Server is not reachable.");
                hideProgress();
                // make reverse transaction performed when read time out is occur
                reverseTransaction();
                Log.i("error", volleyError + "");
            }
        });

        int socketTimeout = 30 * 1000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(policy);
        queue.add(jsonObjectRequest);
    }

    // Auto cancel transaction when status of Free Charge Transaction is unknown

    public void reverseTransaction() {

        showProgress("Cancelling Your FreeCharge Transaction.....");

        // create json request for cancel transaction
        JSONObject requestJson = new JSONObject();
        try {
            requestJson.put("merchantID", Utility.getFromSharedPrefs(PaymentActivity.this, FreeRechargeConstants.FC_MERCHANT_ID));
            requestJson.put("terminalID", Utility.getFromSharedPrefs(PaymentActivity.this, FreeRechargeConstants.FC_TERMINAL_ID));
            requestJson.put("transactionRefNumber", currentMilliSeconds + FreeRechargeConstants.FC_COUNT);
            requestJson.put("txnAmount", mDrsEntity.getcod_amt());
            requestJson.put("txnDatenTime", Utility.getDateForFR());
            requestJson.put("ProcCode", Utility.getFromSharedPrefs(PaymentActivity.this, FreeRechargeConstants.FC_WALLET_REVERSE_PROC_ID));
            requestJson.put("authToken", fcAuthToken);
            // create json for addition info
            JSONObject additionalInfo = new JSONObject();
            additionalInfo.put("Tag", mDrsEntity.getdocketno());
            additionalInfo.put("Value", mDrsEntity.getdrsno());
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(additionalInfo);

            requestJson.put("AdditinalInfo", jsonArray);
            Log.i("AdditinalInfo", jsonArray + "");
            Log.i("requestJson", requestJson + "");
        } catch (JSONException je) {
            je.printStackTrace();
        }

        Log.i("requestJson", requestJson + "");

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, FreeRechargeConstants.FR_REVERSE_TRANSACTION_URL, requestJson, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                System.out.println("result: " + jsonObject.toString());
                try {
                    String responseDescription = jsonObject.getString("responseDescription");
                    if (jsonObject.getString("responseCode").equals("00")) {
                        String description = jsonObject.getString("responseDescription");
                        String errorCode = jsonObject.getString("responseCode");
                        Utility.showToast(PaymentActivity.this, "Your Transaction Sucessfully Cancel.");
                        insertSwipeDetail(errorCode, "FC", description);
                        showAlertDialog(PAYMENT_FAIL);
                    }
//                    else if (jsonObject.getString("responseCode").equals("ER-500") || jsonObject.getString("responseCode").equals("ER-7012") || jsonObject.getString("responseCode").equals("ER-7005")) {
//                        FreeRechargeConstants.FC_COUNT = FreeRechargeConstants.FC_COUNT + 1;
//                        insertSwipeDetail(jsonObject.getString("responseCode"), "FC", jsonObject.getString("responseDescription"));
//                        Utility.showToast(PaymentActivity.this, "Internal Server Error Please Try after somtime.");
//                        showAlertDialog(PAYMENT_FAIL);
//                    } else if (jsonObject.getString("responseCode").equals("ER-3052")) {
//                        FreeRechargeConstants.FC_COUNT = FreeRechargeConstants.FC_COUNT + 1;
//                        insertSwipeDetail(jsonObject.getString("responseCode"), "FC", jsonObject.getString("responseDescription"));
//                        Utility.showToast(PaymentActivity.this, "Technical Issue, try after sometime.");
//                        showAlertDialog(PAYMENT_FAIL);
//                    } else if (jsonObject.getString("responseCode").equals("ER-3058") || jsonObject.getString("responseCode").equals("ER-3059")) {
//                        FreeRechargeConstants.FC_COUNT = FreeRechargeConstants.FC_COUNT + 1;
//                        insertSwipeDetail(jsonObject.getString("responseCode"), "FC", jsonObject.getString("responseDescription"));
//                        Utility.showToast(PaymentActivity.this, "Your Trasaction is Failed.");
//                        showAlertDialog(PAYMENT_FAIL);
//                    } else if (jsonObject.getString("responseCode").equals("ER-3057")) {
//                        FreeRechargeConstants.FC_COUNT = FreeRechargeConstants.FC_COUNT + 1;
//                        insertSwipeDetail(jsonObject.getString("responseCode"), "FC", jsonObject.getString("responseDescription"));
//                        Utility.showToast(PaymentActivity.this, "Trasaction not Present.");
//                        showAlertDialog(PAYMENT_FAIL);
//                    }
                    else {
                        FreeRechargeConstants.FC_COUNT = FreeRechargeConstants.FC_COUNT + 1;
                        insertSwipeDetail(jsonObject.getString("responseCode"), "FC", jsonObject.getString("responseDescription"));
//                        Utility.showToast(PaymentActivity.this, "Your Trasaction is Failed.");
                        Utility.showToast(PaymentActivity.this, jsonObject.getString("responseDescription"));
                        showAlertDialog(PAYMENT_FAIL);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                hideProgress();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                Utility.showToast(PaymentActivity.this, "Server is not reachable.");
                hideProgress();
            }
        });
        TaskForceApplication.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    // cancel transaction
    public void cancelFCTransaction() {

        showProgress("Cancelling FreeCharge Trasaction.....");

        // create json request for cancel transaction
        JSONObject requestJson = new JSONObject();
        try {
            requestJson.put("merchantID", Utility.getFromSharedPrefs(PaymentActivity.this, FreeRechargeConstants.FC_MERCHANT_ID));
            requestJson.put("terminalID", Utility.getFromSharedPrefs(PaymentActivity.this, FreeRechargeConstants.FC_TERMINAL_ID));
            requestJson.put("transactionRefNumber", currentMilliSeconds + FreeRechargeConstants.FC_COUNT);
            requestJson.put("ServerTransactionID", fc_ServerID);
            requestJson.put("txnDatenTime", Utility.getDateForFR());
            requestJson.put("ProcCode", Utility.getFromSharedPrefs(PaymentActivity.this, FreeRechargeConstants.FC_WALLET_VOID_PROC_ID));
            requestJson.put("authToken", fcAuthToken);
            // create json for addition info
            JSONObject additionalInfo = new JSONObject();
            additionalInfo.put("Tag", mDrsEntity.getdocketno());
            additionalInfo.put("Value", mDrsEntity.getdrsno());
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(additionalInfo);

            requestJson.put("AdditinalInfo", jsonArray);
            Log.i("AdditinalInfo", jsonArray + "");
            Log.i("requestJson", requestJson + "");
        } catch (JSONException je) {
            je.printStackTrace();
        }
        Log.i("requestJson", requestJson + "");
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, FreeRechargeConstants.FR_CANCEL_TRANSACTION_URL, requestJson, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                System.out.println("result: " + jsonObject.toString());
                try {
                    System.out.println("RESPONSE:          >>> " + jsonObject.toString() + "<<<");
                    String responseCode = jsonObject.getString("responseCode");
                    String responseDescription = jsonObject.getString("responseDescription");
                    if (responseCode.equalsIgnoreCase("00")) {
                        FreeRechargeConstants.FC_COUNT = FreeRechargeConstants.FC_COUNT + 1;
                        insertSwipeDetail(responseCode, "FC", responseDescription);
                        Utility.showToast(PaymentActivity.this, "Your Transaction Sucessfully Cancel.");
                        showAlertDialog(PAYMENT_FAIL);
                    }
//                    else if (jsonObject.getString("responseCode").equals("ER-500") || jsonObject.getString("responseCode").equals("ER-7012") || jsonObject.getString("responseCode").equals("ER-7005")) {
//                        FreeRechargeConstants.FC_COUNT = FreeRechargeConstants.FC_COUNT + 1;
//                        insertSwipeDetail(jsonObject.getString("responseCode"), "FC", jsonObject.getString("responseDescription"));
//                        Utility.showToast(PaymentActivity.this, "Internal Server Error Please Try after somtime.");
//                        showAlertDialog(PAYMENT_FAIL);
//                    } else if (jsonObject.getString("responseCode").equals("ER-3051")) {
//                        FreeRechargeConstants.FC_COUNT = FreeRechargeConstants.FC_COUNT + 1;
//                        insertSwipeDetail(jsonObject.getString("responseCode"), "FC", jsonObject.getString("responseDescription"));
//                        Utility.showToast(PaymentActivity.this, "Transaction Failed.");
//                        showAlertDialog(PAYMENT_FAIL);
//                    } else if (jsonObject.getString("responseCode").equals("ER-3056")) {
//                        FreeRechargeConstants.FC_COUNT = FreeRechargeConstants.FC_COUNT + 1;
//                        insertSwipeDetail(jsonObject.getString("responseCode"), "FC", jsonObject.getString("responseDescription"));
//                        Utility.showToast(PaymentActivity.this, "Transaction not Present.");
//                        showAlertDialog(PAYMENT_FAIL);
//                    }
                    else {
                        FreeRechargeConstants.FC_COUNT = FreeRechargeConstants.FC_COUNT + 1;
                        insertSwipeDetail(jsonObject.getString("responseCode"), "FC", jsonObject.getString("responseDescription"));
//                        Utility.showToast(PaymentActivity.this, "Your Trasaction is Failed.");
                        Utility.showToast(PaymentActivity.this, jsonObject.getString("responseDescription"));
                        showAlertDialog(PAYMENT_FAIL);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                hideProgress();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                Utility.showToast(PaymentActivity.this, "Server is not reachable.");
                hideProgress();
            }
        });
        TaskForceApplication.getInstance().addToRequestQueue(jsonObjectRequest);
    }


//    /**
//     * Get paymentStatus
//     */
//    public String getPaymentStatus() {
//        String responseString = "";
//        // Add headers
//        org.kxml2.kdom.Element parentElement = new org.kxml2.kdom.Element().createElement(MSWIPE_PAYMENT_VERIFY_NAMESPACE, "MyHeader");
//        // Add client code header
//        org.kxml2.kdom.Element clientCodeElement = new org.kxml2.kdom.Element().createElement(MSWIPE_PAYMENT_VERIFY_NAMESPACE, MswipeConstants.CLIENT_CODE_KEY);
//        clientCodeElement.addChild(Node.TEXT, MswipeConstants.CLIENT_CODE_VALUE);
//        parentElement.addChild(Node.ELEMENT, clientCodeElement);
//        // Add user id header
//        org.kxml2.kdom.Element userIdElement = new org.kxml2.kdom.Element().createElement(MSWIPE_PAYMENT_VERIFY_NAMESPACE, MswipeConstants.USER_ID_KEY);
//        userIdElement.addChild(Node.TEXT, MswipeConstants.USER_ID_VALUE);
//        parentElement.addChild(Node.ELEMENT, userIdElement);
//        // Add user pwd header
//        org.kxml2.kdom.Element userPwdElement = new org.kxml2.kdom.Element().createElement(MSWIPE_PAYMENT_VERIFY_NAMESPACE, MswipeConstants.USER_ID_KEY);
//        userPwdElement.addChild(Node.TEXT, MswipeConstants.USER_PWD_VALUE);
//        parentElement.addChild(Node.ELEMENT, userPwdElement);
//
//        //Create request
//        SoapObject request = new SoapObject(MSWIPE_PAYMENT_VERIFY_NAMESPACE, MSWIPE_PAYMENT_VERIFY_METHOD_NAME);
//        //Add the property to request object
//        request.addProperty(MswipeConstants.REF_NO_KEY, "UNISDDSC1228540"/*mDrsEntity.getdocketno()*/);
//
//        //Create envelope
//        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
//        envelope.dotNet = true;
//        //Set output SOAP object
//        envelope.setOutputSoapObject(request);
//
//        //Create HTTP call object
//        HttpTransportSE androidHttpTransport = new HttpTransportSE(Constants.MSWIPE_PAYMENT_VERIFY_URL);
//
//        try {
//            //Invoke web service
//            androidHttpTransport.call(MSWIPE_PAYMENT_VERIFY_SOAP_ACTION, envelope);
//            //Get the response
//            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
//
//            responseString = response.toString();
//
//            String requestDump = androidHttpTransport.requestDump;
//            System.out.println("mswipe request: " + requestDump);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return responseString;
//    }
}
