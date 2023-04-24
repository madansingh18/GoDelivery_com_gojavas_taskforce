package com.gojavas.taskforce.ui.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.gridlayout.widget.GridLayout;

import com.gojavas.taskforce.R;
import com.gojavas.taskforce.database.DatabaseHelper;
import com.gojavas.taskforce.database.DeliveryHelper;
import com.gojavas.taskforce.database.DrsHelper;
import com.gojavas.taskforce.database.ItemHelper;
import com.gojavas.taskforce.entity.DeliveryEntity;
import com.gojavas.taskforce.entity.DrsEntity;
import com.gojavas.taskforce.manager.DesignManager;
import com.gojavas.taskforce.services.GPSTracker;
import com.gojavas.taskforce.utils.Constants;
import com.gojavas.taskforce.utils.Utility;
import com.gojavas.taskforce.utils.UtilityScheduler;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by GJS280 on 27/4/2015.
 */
public class SuccessActivity extends AppCompatActivity {

    private static final int REQUEST_CAMERA_IMAGE = 100;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int REQUEST_PAYMENT_CODE = 2;
    public static final int REQUEST_SIGNATURE_CODE = 3;
    public static final int REQUEST_ITEM_CODE = 4;
    private static final int REQUEST_CAMERA_CODE = 5;

    private Uri fileUri;
    private GridLayout mGridLayout;

    private static DrsEntity mDrsEntity;
    private DeliveryEntity mDeliveryEntity = new DeliveryEntity();

    private JSONArray mSuccessArray;
    private JSONObject mPaymentResponse;
    private String mReasonId;
    private int mScreenWidth, mHeight, mEight;
    private String mJobType, mScreen, mImageTitle, mSignatureName, mFailCategory = "";
    private static String mCODAmount, mDocketNumber;
    private int mPosition = 0;
    private ArrayList<Boolean> mIsCompulsory = new ArrayList<>();

    private String mColumnName, mData, paymentType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        getSupportActionBar().hide();

        getIntentValues();
        registerViews();

        initializeDeliveryEntity();
        createDeliveryEntity();

        try {
            // Check device date and time zone
            int dateCheck = android.provider.Settings.System.getInt(getContentResolver(), android.provider.Settings.System.AUTO_TIME);
            int timeZoneCheck = android.provider.Settings.System.getInt(getContentResolver(), Settings.System.AUTO_TIME_ZONE);
            if (dateCheck == 0) {
                // Automatic date option is not selected
                showDateDialog(Constants.INCORRECT_DATE_TITLE, Constants.INCORRECT_DATE_MESSAGE);
            } else if (timeZoneCheck == 0) {
                // Automatic time zone option is not selected
                showDateDialog(Constants.INCORRECT_TIME_ZONE_TITLE, Constants.INCORRECT_TIME_ZONE_MESSAGE);
            } else {
                // Automatically click first layout
                autoClick(mPosition);
            }
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
    }

//    @Override
//    public void onSaveInstanceState(Bundle outState) {
//        outState.putString(Constants.JOB_TYPE, mJobType);
//        super.onSaveInstanceState(outState);
//    }
//
//    @Override
//    protected void onRestoreInstanceState(Bundle savedInstanceState) {
//        if (savedInstanceState != null) {
//            if (savedInstanceState.containsKey(Constants.JOB_TYPE)) {
//                mJobType = savedInstanceState.getString(Constants.JOB_TYPE);
//            }
//            mDrsEntity = DrsHelper.getInstance().getDrsDetailData(mDocketNumber);
//        }
//        super.onRestoreInstanceState(savedInstanceState);
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check scanner response
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            mData = result.getContents();
            if (mData != null) {
                if (mJobType.equalsIgnoreCase(Constants.JOB_TYPE_PICKUP) || mJobType.equalsIgnoreCase(Constants.JOB_TYPE_EXCHANGE)) {
                    // In case of Pickup docket number should start with P
                    if (mData.startsWith("P") || mData.startsWith("p")) {
                        boolean reverseDocketNumberExist = DeliveryHelper.getInstance().checkReverseDocketNumberExist(mData);
                        if (reverseDocketNumberExist) {
                            Utility.showToast(SuccessActivity.this, "Docket already scanned");
                        } else {
                            // Get child TextView and change its text
                            LinearLayout layout = (LinearLayout) mGridLayout.getChildAt(mPosition);
                            TextView textView = (TextView) layout.getChildAt(1);
                            textView.setText(mData);
                            updateDeliveryEntity();
                            // Enable and automatically click next layout
                            enableLayouts(mPosition + 1);
                        }
                    } else {

                        Utility.showNormalAlertDialog(SuccessActivity.this, "Pickup Scan", mData + " should start with 'P'");

                    }
                }

            } else {
                Utility.showToast(SuccessActivity.this, "Unable to scan");
            }
        } else {
            // This is not scanner response
            // Check payment or signature response
            super.onActivityResult(requestCode, resultCode, data);
            if (resultCode == RESULT_OK) {
                if (requestCode == REQUEST_CAMERA_IMAGE) {
                    displayCapturedImage();
                } else if (requestCode == REQUEST_CAMERA_CODE) {
                    System.out.println("camera self");
                    displayCapturedImage();
                } else if (requestCode == REQUEST_PAYMENT_CODE) {
                    // Get child TextView and change its text
                    LinearLayout layout = (LinearLayout) mGridLayout.getChildAt(mPosition);
                    layout.setEnabled(false);
                    layout.setAlpha(0.5F);
//                    layout.setBackgroundDrawable(getResources().getDrawable(R.drawable.appcolor_a_white_border));
                    TextView textView = (TextView) layout.getChildAt(1);
                    try {
                        mPaymentResponse = new JSONObject(data.getStringExtra(Constants.PAYMENT_MESSAGE));
                        // Add amount paid in delivery entity
                        mData = data.getStringExtra(Constants.PAYMENT_AMOUNT);
                        mColumnName = DatabaseHelper.ORIGINAL_AMOUNT_PAID;
                        updateDeliveryEntity();
                        // Add transaction number in delivery entity
                        mData = mPaymentResponse.getString(Constants.TRANSACTION_NO);
                        mColumnName = DatabaseHelper.TRANSACTION_NO;
                        updateDeliveryEntity();
                        // Add auth code in delivery entity
                        mData = mPaymentResponse.getString(Constants.AUTH_CODE);
                        mColumnName = DatabaseHelper.AUTH_CODE;
                        updateDeliveryEntity();
                        // Add card type in delivery entity
                        mData = mPaymentResponse.getString(Constants.CARD_TYPE);
                        mColumnName = DatabaseHelper.CARD_TYPE;
                        updateDeliveryEntity();
                        // Add bank name in delivery entity
                        mData = mPaymentResponse.getString(Constants.BANK_NAME);
                        mColumnName = DatabaseHelper.BANK_NAME;
                        updateDeliveryEntity();
//                        // Add customer wallet mobile number in delivery entity
//                        mData = mPaymentResponse.getString(Constants.CUSTOMER_WALLET_CONTACT_NUMBER);
//                        mColumnName = DatabaseHelper.CUSTOMER_WALLET_CONTACT_NUMBER;
//                        updateDeliveryEntity();
                        // Add mode of payment in delivery entity
                        mData = mPaymentResponse.getString(Constants.MODE_OF_PAYMENT);
                        mColumnName = DatabaseHelper.MODE_OF_PAYMENT;
                        updateDeliveryEntity();
                        // Add receipt url in delivery entity
                        mData = mPaymentResponse.getString(Constants.RECEIPT_URL);
                        mColumnName = DatabaseHelper.RECEIPT_URL;
                        updateDeliveryEntity();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (mDrsEntity.getcod_dod().equalsIgnoreCase("Y") && mFailCategory.equalsIgnoreCase("false")) {
                        Log.i("cash type", "docket");
                    }
//                    else if(mDrsEntity.getclient_name().equalsIgnoreCase("OLX")){
//                        textView.setText("Done");
//                    }
                    else {
                        textView.setText("Done");
                    }
                } else if (requestCode == REQUEST_SIGNATURE_CODE) {
                    LinearLayout layout = (LinearLayout) mGridLayout.getChildAt(mPosition);
                    ImageView imageView = (ImageView) layout.getChildAt(0);
                    imageView.setImageBitmap(null);
                    TextView textView = (TextView) layout.getChildAt(1);
                    textView.setText("");
                    // Extract Bitmap bytes from intent bundle
                    byte[] bitmapBytes = data.getByteArrayExtra(Constants.SIGNATURE_IMAGE);
                    Bitmap signatureBitmap = null;
                    signatureBitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);

                    String path = Utility.getApplicationPath(Constants.PATH_CAPTURED_IMAGE);
                    String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HHmmss", Locale.getDefault()).format(new Date());
                    mSignatureName = "SIGN_" + mDocketNumber + "_" + timeStamp + "##" + mDrsEntity.getdriver_id() + "##" + Utility.getDeviceId() + ".png";
                    Utility.copyBitmap(signatureBitmap, path, mSignatureName);
                    layout.setBackgroundDrawable(new BitmapDrawable(signatureBitmap));
                } else if (requestCode == REQUEST_ITEM_CODE) {
                    LinearLayout layout = (LinearLayout) mGridLayout.getChildAt(mPosition);
                    TextView textView = (TextView) layout.getChildAt(1);
                    textView.setText("Done");
                }
                // Update Entity
                updateDeliveryEntity();
                // Enable and automatically click next layout
                enableLayouts(mPosition + 1);
            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled operation
                Utility.showToast(SuccessActivity.this, "User cancelled");
            } else {
                // operation failed
                Utility.showToast(SuccessActivity.this, "Failed");
            }
        }
    }

    /**
     * Get all the intent values
     */
    private void getIntentValues() {
        Intent intent = getIntent();
        try {
            System.out.println("intent 1: " + intent);
            mJobType = intent.getStringExtra(Constants.JOB_TYPE);
            mScreen = intent.getStringExtra(Constants.SCREEN);
            mFailCategory = intent.getStringExtra(Constants.FAIL_CATEGORY);
//            mDrsEntity = EventBus.getDefault().removeStickyEvent(DrsEntity.class);
            String drs_docket = intent.getStringExtra(Constants.DRS_DOCKET);

            mDrsEntity = DrsHelper.getInstance().getDrsDetailData(drs_docket);
            mDocketNumber = mDrsEntity.getdocketno();
            mCODAmount = mDrsEntity.getcod_amt();
            Log.i("mCODAmount", mCODAmount + " " + mJobType + " " + mScreen);
            Log.i("mFailCategory", mFailCategory);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Register all the views
     */
    private void registerViews() {
        mEight = (int) getResources().getDimension(R.dimen.eight);
        mHeight = (int) getResources().getDimension(R.dimen.hundredfifty);
        mScreenWidth = Utility.getMetricsWidth(SuccessActivity.this);
        mGridLayout = (GridLayout) findViewById(R.id.success_gridlayout);
        try {
            JSONObject designJson = DesignManager.getInstance().getDesignJson();
            JSONObject jobJson = designJson.getJSONObject(mJobType);
            mSuccessArray = jobJson.getJSONArray(Constants.DESIGN_SUCCESS);
            if (jobJson.has(Constants.DESIGN_REASON_ID)) {
                mReasonId = jobJson.getString(Constants.DESIGN_REASON_ID);
            }
            int length = mSuccessArray.length();
            Log.i("mSuccessArray", mSuccessArray + "");
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
                LinearLayout linearLayout = createLayout(title, iconId);
                linearLayout.setEnabled(false);
                linearLayout.setAlpha(0.5F);
//                linearLayout.setBackgroundDrawable(getResources().getDrawable(R.drawable.appcolor_a_white_border));
                linearLayout.setId(i);
                mGridLayout.addView(linearLayout);
                mIsCompulsory.add((compulsory.equalsIgnoreCase("true")) ? true : false);
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
        Log.i("title", title);
        LinearLayout layout = new LinearLayout(SuccessActivity.this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.width = (mScreenWidth - (mEight * 2)) / 2;
        params.height = mHeight;
        layout.setLayoutParams(params);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setBackgroundResource(R.drawable.appcolor_white_border);
        layout.setGravity(Gravity.CENTER);

        ImageView imageView = createChildImageView(icon);
        TextView textView = createChildTextView(title);

        // display only complete tiles for cash type docket
        Log.i("mFailCategory", mFailCategory);
        if (mDrsEntity.getcod_dod().equalsIgnoreCase("Y") && mFailCategory.equalsIgnoreCase("false")) {
            Log.i("client", mDrsEntity.getclient_name());
            if (title.equalsIgnoreCase("Complete")) {
                layout.addView(imageView);
                layout.addView(textView);
            } else if (title.equalsIgnoreCase("Photo 1") && mDrsEntity.getclient_name().equalsIgnoreCase("OLX")
                    || title.equalsIgnoreCase("Delivered To") && mDrsEntity.getclient_name().equalsIgnoreCase("OLX")) {
                layout.addView(imageView);
                layout.addView(textView);
            } else {
                layout.removeView(imageView);
                layout.removeView(textView);
                layout.setVisibility(View.GONE);
            }
        } else {
            layout.addView(imageView);
            layout.addView(textView);
        }

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
        ImageView imageView = new ImageView(SuccessActivity.this);
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
        TextView textView = new TextView(SuccessActivity.this);
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
        ArrayList<String> spinnerArray = new ArrayList<String>();
        try {
            jsonObject = mSuccessArray.getJSONObject(mPosition);
            type = jsonObject.getString(Constants.DESIGN_TYPE);
            if (jsonObject.has(Constants.DESIGN_COLUMN_NAME)) {
                mColumnName = jsonObject.getString(Constants.DESIGN_COLUMN_NAME);
            }
            if (jsonObject.has(Constants.DESIGN_VALUES)) {
                JSONArray values = jsonObject.getJSONArray(Constants.DESIGN_VALUES);
                int length = values.length();
                for (int i = 0; i < length; i++) {
                    spinnerArray.add(values.getString(i));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String text = getDeliveryEntityData();

        switch (type) {
            case Constants.DESIGN_TYPE_PAYMENT:
//                PaymentEntity paymentEntity = PaymentHelper.getInstance().getPaymentDetail(mDrsEntity.getdrs_docket());
//                if(paymentEntity != null) {
//                    // Payment is already done, skip payment
//                    // Get child TextView and change its text
//                    LinearLayout layout = (LinearLayout) mGridLayout.getChildAt(mPosition);
//                    layout.setEnabled(false);
//                    layout.setAlpha(0.5F);
////                    layout.setBackgroundDrawable(getResources().getDrawable(R.drawable.appcolor_a_white_border));
//                    TextView textView = (TextView) layout.getChildAt(1);
//                    // Add amount paid in delivery entity
//                    mData = paymentEntity.getoriginal_amount_paid();
//                    mColumnName = DatabaseHelper.ORIGINAL_AMOUNT_PAID;
//                    updateDeliveryEntity();
//                    // Add transaction number in delivery entity
//                    mData = paymentEntity.gettransaction_no();
//                    mColumnName = DatabaseHelper.TRANSACTION_NO;
//                    updateDeliveryEntity();
//                    // Add auth code in delivery entity
//                    mData = paymentEntity.getauth_code();
//                    mColumnName = DatabaseHelper.AUTH_CODE;
//                    updateDeliveryEntity();
//                    // Add card type in delivery entity
//                    mData = paymentEntity.getcard_type();
//                    mColumnName = DatabaseHelper.CARD_TYPE;
//                    updateDeliveryEntity();
//                    // Add bank name in delivery entity
//                    mData = paymentEntity.getbank_name();
//                    mColumnName = DatabaseHelper.BANK_NAME;
//                    updateDeliveryEntity();
//                    // Add mode of payment in delivery entity
//                    mData = paymentEntity.getmode_of_payment();
//                    mColumnName = DatabaseHelper.MODE_OF_PAYMENT;
//                    updateDeliveryEntity();
//
//                    // Enable and automatically click next layout
//                    enableLayouts(mPosition + 1);
//
//                    textView.setText("Done");
//                } else {
                // Payment is not done, get the payment
                // Go to payment activity
                String payment = mDrsEntity.getcod_dod();
                if (payment != null && (payment.equalsIgnoreCase("Y") || payment.equalsIgnoreCase("S"))) {
                    // Non-prepaid docket
                    try {
                        JSONObject designJson = DesignManager.getInstance().getDesignJson();
                        JSONObject paymentJson = designJson.getJSONObject(type);
                        Intent paymentIntent = new Intent(SuccessActivity.this, PaymentActivity.class);
                        paymentIntent.putExtra(Constants.DRS_DOCKET, mDrsEntity.getdrs_docket());
                        paymentIntent.putExtra(Constants.DESIGN_JSON, paymentJson.toString());
                        paymentIntent.putExtra(Constants.FAIL_CATEGORY, mFailCategory);
//                            EventBus.getDefault().postSticky(mDrsEntity);
                        startActivityForResult(paymentIntent, REQUEST_PAYMENT_CODE);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    // Prepaid docket
                    LinearLayout linearLayout = (LinearLayout) mGridLayout.getChildAt(mPosition);
                    TextView textView = (TextView) linearLayout.getChildAt(1);
                    textView.setText("Done");
                    linearLayout.setEnabled(false);
                    linearLayout.setAlpha(0.5F);
//                    linearLayout.setBackgroundDrawable(getResources().getDrawable(R.drawable.appcolor_a_white_border));
                    skipLayouts(mPosition);
                }
//                }
                break;
            case Constants.DESIGN_TYPE_DROP_DOWN_SINGLE_CHOICE:
                // Show Spinner in dialog
                Log.i("choice", mDeliveryEntity.getmode_of_payment());
                // check if docket is cash type or not
                if (mDrsEntity.getcod_dod().equalsIgnoreCase("Y") && mFailCategory.equalsIgnoreCase("false")) {
                    skipLayouts(mPosition);
                } else {

                    Spinner spinner = new Spinner(this, Spinner.MODE_DIALOG);
                    Log.i("spinner array", spinnerArray + "");
                    ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_activated_1, spinnerArray);
                    spinner.setAdapter(spinnerArrayAdapter);
                    if (!TextUtils.isEmpty(text)) {
                        int length = spinnerArray.size();
                        for (int i = 0; i < length; i++) {
                            String data = spinnerArray.get(i);
                            if (data.equalsIgnoreCase(text)) {
                                spinner.setSelection(i);
                                break;
                            }
                        }
                    }
                    showCustomDialog(title, spinner);
                }
                break;
            case Constants.DESIGN_TYPE_USER_INPUT:

                // check if docket is cash type or not
                if (mDrsEntity.getcod_dod().equalsIgnoreCase("Y") && mFailCategory.equalsIgnoreCase("false")) {
                    if (mDrsEntity.getclient_name().equalsIgnoreCase("OLX")) {
                        // Show EditTExt in dialog
                        EditText editText = new EditText(SuccessActivity.this);
                        editText.setText(text);
                        // Change cursor color
                        try {
                            Field f = TextView.class.getDeclaredField("mCursorDrawableRes");
                            f.setAccessible(true);
                            f.set(editText, R.drawable.black_cursor);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        showCustomDialog("Collected From", editText);
                    } else {
                        skipLayouts(mPosition);
                    }
                } else {
                    // Show EditTExt in dialog
                    EditText editText = new EditText(SuccessActivity.this);
                    editText.setText(text);
                    // Change cursor color
                    try {
                        Field f = TextView.class.getDeclaredField("mCursorDrawableRes");
                        f.setAccessible(true);
                        f.set(editText, R.drawable.black_cursor);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    showCustomDialog(title, editText);
                }
                break;
            case Constants.DESIGN_TYPE_RATING:
                // Show RatingBar in dialog
                RatingBar ratingBar = new RatingBar(SuccessActivity.this);
                ratingBar.setRating(1.0F);
                ratingBar.setNumStars(5);
                ratingBar.setStepSize(1.0F);
                if (!TextUtils.isEmpty(text)) {
                    ratingBar.setRating(Float.parseFloat(text));
                }
                showCustomDialog(title, ratingBar);
                break;
            case Constants.DESIGN_TYPE_SIGNATURE:
                // Go to signature activity
                Intent signatureIntent = new Intent(SuccessActivity.this, SignatureActivity.class);
                startActivityForResult(signatureIntent, REQUEST_SIGNATURE_CODE);
                break;
            case Constants.DESIGN_TYPE_CAMERA:
                // check if docket is cash type or not
                if (mDrsEntity.getcod_dod().equalsIgnoreCase("Y") && mFailCategory.equalsIgnoreCase("false")) {
                    if (mDrsEntity.getclient_name().equalsIgnoreCase("OLX")) {
                        // Start camera
                        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
//                captureImage();
                        goToCameraScreen();
                    } else {
                        skipLayouts(mPosition);
                    }
                } else {
                    // Start camera
                    fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
//                captureImage();
                    goToCameraScreen();
                }
                break;
            case Constants.DESIGN_TYPE_DATE:
                // Show DatePicker in dialog
                String d = "", m = "", y = "";
                if (!TextUtils.isEmpty(text)) {
                    String[] dateArray = text.split("/");
                    if (dateArray.length == 3) {
                        d = dateArray[0];
                        m = dateArray[1];
                        y = dateArray[2];
                    }
                }
                DatePicker datePicker = new DatePicker(SuccessActivity.this);
                final Calendar c = Calendar.getInstance();
                int year = TextUtils.isEmpty(y) ? c.get(Calendar.YEAR) : Integer.parseInt(y);
                int month = TextUtils.isEmpty(m) ? c.get(Calendar.MONTH) : Integer.parseInt(m);
                int day = TextUtils.isEmpty(d) ? c.get(Calendar.DAY_OF_MONTH) : Integer.parseInt(d);

                Calendar maxC = c;
                maxC.add(Calendar.MONTH, 1);

                // set current date into DatePicker
                datePicker.setCalendarViewShown(false);
                // Set minimum date to current
                datePicker.setMinDate(System.currentTimeMillis() - 1000);
                // Set maximum date to after one month
                datePicker.setMaxDate(maxC.getTimeInMillis());
                datePicker.init(year, month, day, null);
                showCustomDialog(title, datePicker);
                break;
            case Constants.DESIGN_TYPE_SCANNER:
                // Start the scanner
                new IntentIntegrator(this).initiateScan();
                break;
            case Constants.DESIGN_TYPE_CHECKBOX:
                LinearLayout linearLayout = new LinearLayout(SuccessActivity.this);
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                int size = spinnerArray.size();
                for (int i = 0; i < size; i++) {
                    CheckBox checkBox = new CheckBox(SuccessActivity.this);
                    checkBox.setPadding(0, mEight, 0, mEight);
                    checkBox.setText(spinnerArray.get(i));
                    linearLayout.addView(checkBox);
                }
                showCustomDialog(title, linearLayout);
                break;
            case Constants.DESIGN_TYPE_ITEMS_ARRAY:
                Intent intent = new Intent(SuccessActivity.this, ItemsActivity.class);
                intent.putExtra(Constants.DRS_DOCKET, mDrsEntity.getdrs_docket());
                intent.putExtra(Constants.FAIL_CATEGORY, mFailCategory);
                startActivityForResult(intent, REQUEST_ITEM_CODE);
                break;
            case Constants.DESIGN_TYPE_COMPLETE:
                // Insert the data in database
                insertInDatabase();
                break;
            default:
                break;
        }
    }

    /**
     * Show custom dialog
     *
     * @param title
     * @param childView
     */
    public void showCustomDialog(final String title, final View childView) {
        final Dialog dialog = new Dialog(SuccessActivity.this, R.style.customDialogTheme);
        dialog.setContentView(R.layout.dialog_custom);
        LinearLayout linearLayout = (LinearLayout) dialog.findViewById(R.id.dialog_main_layout);
        TextView titleTextView = (TextView) dialog.findViewById(R.id.dialog_title);
        Button okButton = (Button) dialog.findViewById(R.id.dialog_ok_button);
        Button cancelButton = (Button) dialog.findViewById(R.id.dialog_cancel_button);

        LinearLayout.LayoutParams childPrams;
        if (childView instanceof RatingBar) {
            childPrams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        } else {
            childPrams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        }
        childView.setLayoutParams(childPrams);
        linearLayout.addView(childView);

        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        int width = Utility.getMetricsWidth(SuccessActivity.this);
        int height = Utility.getMetricsHeight(SuccessActivity.this);
        params.width = (width <= height) ? width : height;
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);

        titleTextView.setText(title);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout layout = (LinearLayout) mGridLayout.getChildAt(mPosition);
                TextView textView = (TextView) layout.getChildAt(1);
                if (childView instanceof Spinner) {
                    mData = ((Spinner) childView).getSelectedItem().toString();
                    // Update delivery entity
                    updateDeliveryEntity();
                    if ((title.equalsIgnoreCase(Constants.DESIGN_HAPPY_DELIVERY) && mData.equalsIgnoreCase(Constants.DESIGN_HAPPY_DELIVERY_AUTO_ACTION)) ||
                            title.equalsIgnoreCase(Constants.DESIGN_CUSTOMER_INITIATED_DELAY) && checkDataContains()) {
                        // Automatically click next layout
                        autoClick(mPosition + 1);
                    }
                    // Added by Harshita for Exchange Job Type
                    else if (mData.equalsIgnoreCase("No") && mJobType.equalsIgnoreCase(Constants.JOB_TYPE_EXCHANGE)) {
                        /// Go to Fail Activity
                        goToFailScreen();
                    } else {
                        if (title.equalsIgnoreCase(Constants.DESIGN_CUSTOMER_INITIATED_DELAY) && !checkDataContains()) {
                            // Skip and automatically click next layout
//                            skipLayouts(mPosition + 1);
                            // change
                            if (mData.equalsIgnoreCase("CID - CONSIGNEE REQUESTED DELIVERY ON ANOTHER DATE")) {
                                autoClick(mPosition + 1);
                            } else {
                                skipLayouts(mPosition + 1);
                            }
                        } else {
                            // Enable and automatically click next layout
                            enableLayouts(mPosition + 1);
                        }
                    }
                } else if (childView instanceof EditText) {
                    mData = ((EditText) childView).getText().toString().trim();
                    if (TextUtils.isEmpty(mData) || mData.equals("")) {
                        Utility.showToast(SuccessActivity.this, "Cannot be left blank");
                    } else {
                        // Update delivery entity
                        updateDeliveryEntity();
                        // Enable and automatically click next layout
                        enableLayouts(mPosition + 1);
                    }
                } else if (childView instanceof RatingBar) {
                    int rating = (int) ((RatingBar) childView).getRating();
                    mData = String.valueOf(rating);
                    // Update delivery entity
                    updateDeliveryEntity();
                    // Enable and automatically click next layout
                    enableLayouts(mPosition + 1);
                } else if (childView instanceof DatePicker) {
                    int year = ((DatePicker) childView).getYear();
                    int month = ((DatePicker) childView).getMonth();
                    int day = ((DatePicker) childView).getDayOfMonth();
                    mData = day + "/" + (month + 1) + "/" + year;
                    // Update delivery entity
                    updateDeliveryEntity();
                    // Enable and automatically click next layout
                    enableLayouts(mPosition + 1);
                }
                textView.setText(mData);
                // Check whether text is empty or not
                if (mData != null && !TextUtils.isEmpty(mData) && !mData.equals("")) {
                    // Close the dialog
                    dialog.dismiss();
                }
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
     * Launch camera to capture image
     */
    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        // start the image capture Intent
        startActivityForResult(intent, REQUEST_CAMERA_IMAGE);
    }

    /**
     * Display captured Image
     */
    private void displayCapturedImage() {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;
            final Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(), options);

            Bitmap photo = bitmap;
            photo = Bitmap.createScaledBitmap(photo, 500, 500, false);
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.JPEG, 50, bytes);

//            File f = new File(fileUri.getPath());
//            FileOutputStream fo = null;
//            try {
//                f.createNewFile();
//                fo = new FileOutputStream(f);
//                fo.write(bytes.toByteArray());
//                fo.close();
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

            // Display image
            LinearLayout layout = (LinearLayout) mGridLayout.getChildAt(mPosition);
            ImageView imageView = (ImageView) layout.getChildAt(0);
            imageView.setImageBitmap(null);
            layout.setBackgroundDrawable(new BitmapDrawable(photo));
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get file uri
     *
     * @param type
     * @return
     */
    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /**
     * get image file
     */
    private static File getOutputMediaFile(int type) {
        File file = new File(Utility.getApplicationPath(Constants.PATH_CAPTURED_IMAGE));
        // Create the storage directory if it does not exist
        if (!file.exists()) {
            if (!file.mkdirs()) {
                return null;
            }
        }

        // Create file name
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HHmmss", Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {

            mediaFile = new File(file.getPath() + File.separator + "IMG_" + mDocketNumber + "_" + timeStamp + "##" + mDrsEntity.getdriver_id() + "##" + Utility.getDeviceId() + ".jpg");
            System.out.println("mediafile: " + mediaFile.getPath());
        } else {
            return null;
        }
        return mediaFile;
    }

    /**
     * Enable the layout and automatically click it
     *
     * @param position
     */
    private void enableLayouts(int position) {
        LinearLayout linearLayout = (LinearLayout) mGridLayout.getChildAt(position);
        linearLayout.setEnabled(true);
        linearLayout.setAlpha(1.0F);
//        linearLayout.setBackgroundDrawable(getResources().getDrawable(R.drawable.appcolor_white_border));
        boolean isCompulsory = mIsCompulsory.get(position);
        if (isCompulsory) {
            linearLayout.performClick();
        } else {
            if (mSuccessArray.length() > (position + 1)) {
                enableLayouts(position + 1);
            }
        }
    }

    /**
     * Enable the layout and automatically click it
     *
     * @param position
     */
    private void skipLayouts(int position) {
        LinearLayout linearLayout = (LinearLayout) mGridLayout.getChildAt(position);
//        linearLayout.setEnabled(true);
//        linearLayout.setAlpha(1.0F);
//        boolean isCompulsory = mIsCompulsory.get(position);
//        if(isCompulsory) {
//            linearLayout.performClick();
//        } else {
        if (mSuccessArray.length() > (position + 1)) {
            enableLayouts(position + 1);
        }
//        }
    }

    /**
     * Automatically click a layout
     *
     * @param position
     */
    private void autoClick(int position) {
        LinearLayout linearLayout = (LinearLayout) mGridLayout.getChildAt(position);
        linearLayout.setEnabled(true);
        linearLayout.setAlpha(1.0F);
//        linearLayout.setBackgroundDrawable(getResources().getDrawable(R.drawable.appcolor_white_border));
        linearLayout.performClick();
    }

    private void initializeDeliveryEntity() {
        mDeliveryEntity.setjobtype("");
        mDeliveryEntity.setcust_name("");
        mDeliveryEntity.setcust_address1("");
        mDeliveryEntity.setcust_address2("");
        mDeliveryEntity.setpincode("");
        mDeliveryEntity.setcity("");
        mDeliveryEntity.setstate("");
        mDeliveryEntity.setlandmark("");
        mDeliveryEntity.setcust_contact1("");
        mDeliveryEntity.setalternate_no("");
        mDeliveryEntity.setorderNumber("");
        mDeliveryEntity.setpackagesNo("");
        mDeliveryEntity.setDRSNumber("");
        mDeliveryEntity.setTotalDocketsInDRS("");
        mDeliveryEntity.setProdcutDesc("");
        mDeliveryEntity.setdocket_number("");
        mDeliveryEntity.setchoice_of_payment("");
        mDeliveryEntity.setamount_tobe_paid("");
        mDeliveryEntity.setdue_date("");
        mDeliveryEntity.setboy_id("");
        mDeliveryEntity.setboy_name("");
        mDeliveryEntity.setstatus("");
        mDeliveryEntity.setreason_id("");
        mDeliveryEntity.setfailed_reason("");
        mDeliveryEntity.setdelivery_time("");
        mDeliveryEntity.setcomments("");
        mDeliveryEntity.setmode_of_payment("");
        mDeliveryEntity.settransaction_no("");
        mDeliveryEntity.setauth_code("");
        mDeliveryEntity.setcard_type("");
        mDeliveryEntity.setbank_name("");
        mDeliveryEntity.setcustomer_sign("");
        mDeliveryEntity.setcustomer_img("");
        mDeliveryEntity.setcustomer_img2("");
        mDeliveryEntity.setcustomer_img3("");
        mDeliveryEntity.sethappy_delivery("");
        mDeliveryEntity.sethappy_delivery_img("");
        mDeliveryEntity.setlatitude("");
        mDeliveryEntity.setlongitude("");
        mDeliveryEntity.setsku_details("");
        mDeliveryEntity.setlast_updated("");
        mDeliveryEntity.setpreferred_transaction_time("");
        mDeliveryEntity.setoriginal_amount_paid("");
        mDeliveryEntity.setuser_id("");
        mDeliveryEntity.setdelivered_to("");
        mDeliveryEntity.setdelivered_to_relation("");
        mDeliveryEntity.setprirority_delivery("");
        mDeliveryEntity.setclosing_km("");
        mDeliveryEntity.setimei_number("");
        mDeliveryEntity.setsync_update("");
        mDeliveryEntity.setupdated_on_android("");
        mDeliveryEntity.setupdated_on_webx("");
        mDeliveryEntity.setloc_id("");
        mDeliveryEntity.setc_id("");
        mDeliveryEntity.setadmin_id("");
        mDeliveryEntity.setextra1("");
        mDeliveryEntity.setextra2("");
        mDeliveryEntity.setextra3("");
        mDeliveryEntity.setextra4("");
        mDeliveryEntity.setextra5("");
        mDeliveryEntity.setextra6("");
        mDeliveryEntity.setsync_flag("");
        mDeliveryEntity.setlast_lat("");
        mDeliveryEntity.setlast_lng("");
        mDeliveryEntity.setlast_attempt_status("");
        mDeliveryEntity.setattempt_number("");
        mDeliveryEntity.setattempt_slot("");
        mDeliveryEntity.setnps_score("");
        mDeliveryEntity.setseq_predicted("");
        mDeliveryEntity.setseq_selected("");
        mDeliveryEntity.setseq_transaction("");
        mDeliveryEntity.setuser_comments("");
        mDeliveryEntity.setbattery_level("");
        mDeliveryEntity.settime_erp_import_initiated("");
        mDeliveryEntity.settime_erp_import_completed("");
        mDeliveryEntity.settime_available_android_initiated("");
        mDeliveryEntity.settime_available_android_completed("");
        mDeliveryEntity.setreverse_docket_number("");
        mDeliveryEntity.setdrs_docket("");
        mDeliveryEntity.setsync("");
        mDeliveryEntity.setreceipt_url("");
    }

    /**
     * Create Delivery entity
     */
    private void createDeliveryEntity() {
        mDeliveryEntity.setjobtype(mDrsEntity.getjobtype());
        mDeliveryEntity.setcust_name(mDrsEntity.getcsgenm());
        mDeliveryEntity.setcust_address1(mDrsEntity.getcsgeaddr());
        mDeliveryEntity.setcust_address2(mDrsEntity.getcsgeaddr());
        mDeliveryEntity.setpincode(mDrsEntity.getcsgepincode());
        mDeliveryEntity.setcity(mDrsEntity.getcsgecity());
        mDeliveryEntity.setcust_contact1(mDrsEntity.getcsgeteleno());
        mDeliveryEntity.setalternate_no(mDrsEntity.getalternate_number());
        mDeliveryEntity.setorderNumber(mDrsEntity.getordernumber());
        mDeliveryEntity.setpackagesNo(mDrsEntity.getpkgsno());
        mDeliveryEntity.setDRSNumber(mDrsEntity.getdrsno());
        mDeliveryEntity.setTotalDocketsInDRS(mDrsEntity.gettotal_dockets_in_drs());
        mDeliveryEntity.setdocket_number(mDrsEntity.getdocketno());
        mDeliveryEntity.setchoice_of_payment(mDrsEntity.getcod_dod());
        mDeliveryEntity.setamount_tobe_paid(mDrsEntity.getcod_amt());
        mDeliveryEntity.setboy_id(mDrsEntity.getdriver_id());
        if (mReasonId != null && !TextUtils.isEmpty(mReasonId) && !mReasonId.equals("")) {
            // Save the failed reason id
            mDeliveryEntity.setreason_id(mReasonId);
        }
        mDeliveryEntity.setclosing_km(mDrsEntity.getstart_km());
        mDeliveryEntity.setattempt_number(mDrsEntity.getattempt());
        mDeliveryEntity.setdrs_docket(mDrsEntity.getdrs_docket());
    }

    /**
     * Update delivery entity
     */
    private void updateDeliveryEntity() {
        if (mColumnName != null && !TextUtils.isEmpty(mColumnName) && !mColumnName.equals("")) {
            switch (mColumnName) {
//                case DatabaseHelper.CUST_NAME:
//                    break;
//                case DatabaseHelper.CUST_ADDRESS1:
//                    break;
//                case DatabaseHelper.CUST_ADDRESS2:
//                    break;
//                case DatabaseHelper.PINCODE:
//                    break;
//                case DatabaseHelper.CITY:
//                    break;
//                case DatabaseHelper.STATE:
//                    break;
//                case DatabaseHelper.LANDMARK:
//                    break;
//                case DatabaseHelper.CUST_CONTACT1:
//                    break;
//                case DatabaseHelper.ALTERNATE_NO:
//                    break;
//                case DatabaseHelper.ORDERNUMBER:
//                    break;
//                case DatabaseHelper.PACKAGESNO:
//                    break;
//                case DatabaseHelper.DRSNUMBER:
//                    break;
//                case DatabaseHelper.TOTALDOCKETSINDRS:
//                    break;
//                case DatabaseHelper.PRODCUTDESC:
//                    break;
//                case DatabaseHelper.DOCKET_NUMBER:
//                    break;
//                case DatabaseHelper.CHOICE_OF_PAYMENT:
//                    break;
//                case DatabaseHelper.AMOUNT_TOBE_PAID:
//                    break;
                case DatabaseHelper.DUE_DATE:
                    mDeliveryEntity.setdue_date(mData);
                    break;
//                case DatabaseHelper.BOY_ID:
//                    break;
//                case DatabaseHelper.STATUS:
//                    break;
                case DatabaseHelper.FAILED_REASON:
                    mDeliveryEntity.setfailed_reason(mData);
                    break;
//                case DatabaseHelper.DELIVERY_TIME:
//                    break;
//                case DatabaseHelper.COMMENTS:
//                    break;
                case DatabaseHelper.MODE_OF_PAYMENT:
                    mDeliveryEntity.setmode_of_payment(mData);
                    break;
                case DatabaseHelper.TRANSACTION_NO:
                    mDeliveryEntity.settransaction_no(mData);
                    break;
                case DatabaseHelper.AUTH_CODE:
                    mDeliveryEntity.setauth_code(mData);
                    break;
                case DatabaseHelper.CARD_TYPE:
                    mDeliveryEntity.setcard_type(mData);
                    break;
                case DatabaseHelper.BANK_NAME:
                    mDeliveryEntity.setbank_name(mData);
                    break;
//                case DatabaseHelper.CUSTOMER_WALLET_CONTACT_NUMBER:
//                    mDeliveryEntity.setcustomer_wallet_contact_number(mData);
//                    break;
                case DatabaseHelper.CUSTOMER_SIGN:
                    mDeliveryEntity.setcustomer_sign(mSignatureName);
                    break;
                case DatabaseHelper.CUSTOMER_IMG:
                    mDeliveryEntity.setcustomer_img(new File(fileUri.getPath()).getName());
                    break;
                case DatabaseHelper.CUSTOMER_IMG2:
                    mDeliveryEntity.setcustomer_img2(new File(fileUri.getPath()).getName());
                    break;
                case DatabaseHelper.CUSTOMER_IMG3:
                    mDeliveryEntity.setcustomer_img3(new File(fileUri.getPath()).getName());
                    break;
                case DatabaseHelper.HAPPY_DELIVERY:
                    mDeliveryEntity.sethappy_delivery(mData);
                    break;
                case DatabaseHelper.HAPPY_DELIVERY_IMG:
                    mDeliveryEntity.sethappy_delivery_img(new File(fileUri.getPath()).getName());
                    break;
//                case DatabaseHelper.LATITUDE:
//                    break;
//                case DatabaseHelper.LONGITUDE:
//                    break;
//                case DatabaseHelper.SKU_DETAILS:
//                    break;
//                case DatabaseHelper.LAST_UPDATED:
//                    break;
//                case DatabaseHelper.PREFERRED_TRANSACTION_TIME:
//                    break;
                case DatabaseHelper.ORIGINAL_AMOUNT_PAID:
                    mDeliveryEntity.setoriginal_amount_paid(mData);
                    break;
//                case DatabaseHelper.USER_ID:
//                    break;
                case DatabaseHelper.DELIVERED_TO:
                    mDeliveryEntity.setdelivered_to(mData);
                    break;
                case DatabaseHelper.DELIVERED_TO_RELATION:
                    mDeliveryEntity.setdelivered_to_relation(mData);
                    break;
//                case DatabaseHelper.PRIRORITY_DELIVERY:
//                    break;
//                case DatabaseHelper.CLOSING_KM:
//                    break;
//                case DatabaseHelper.IMEI_NUMBER:
//                    break;
//                case DatabaseHelper.SYNC_UPDATE:
//                    break;
//                case DatabaseHelper.UPDATED_ON_ANDROID:
//                    break;
//                case DatabaseHelper.UPDATED_ON_WEBX:
//                    break;
//                case DatabaseHelper.LOC_ID:
//                    break;
//                case DatabaseHelper.C_ID:
//                    break;
//                case DatabaseHelper.ADMIN_ID:
//                    break;
                case DatabaseHelper.EXTRA1:
                    mDeliveryEntity.setextra1(mData);
                    break;
//                case DatabaseHelper.EXTRA2:
//                    break;
//                case DatabaseHelper.EXTRA3:
//                    break;
//                case DatabaseHelper.EXTRA4:
//                    break;
//                case DatabaseHelper.EXTRA5:
//                    break;
//                case DatabaseHelper.EXTRA6:
//                    break;
//                case DatabaseHelper.SYNC_FLAG:
//                    break;
//                case DatabaseHelper.LAST_LAT:
//                    break;
//                case DatabaseHelper.LAST_LNG:
//                    break;
//                case DatabaseHelper.LAST_ATTEMPT_STATUS:
//                    break;
//                case DatabaseHelper.ATTEMPT_NUMBER:
//                    break;
//                case DatabaseHelper.ATTEMPT_SLOT:
//                    break;
                case DatabaseHelper.NPS_SCORE:
                    mDeliveryEntity.setnps_score(mData);
                    break;
//                case DatabaseHelper.SEQ_PREDICTED:
//                    break;
//                case DatabaseHelper.SEQ_SELECTED:
//                    break;
//                case DatabaseHelper.SEQ_TRANSACTION:
//                    break;
//                case DatabaseHelper.USER_COMMENTS:
//                    break;
//                case DatabaseHelper.BATTERY_LEVEL:
//                    break;
//                case DatabaseHelper.TIME_ERP_IMPORT_INITIATED:
//                    break;
//                case DatabaseHelper.TIME_ERP_IMPORT_COMPLETED:
//                    break;
//                case DatabaseHelper.TIME_AVAILABLE_ANDROID_INITIATED:
//                    break;
//                case DatabaseHelper.TIME_AVAILABLE_ANDROID_COMPLETED:
//                    break;
                case DatabaseHelper.REVERSE_DOCKET_NUMBER:
                    mDeliveryEntity.setreverse_docket_number(mData);
                    break;
                case DatabaseHelper.RECEIPT_URL:
                    mDeliveryEntity.setreceipt_url(mData);
                    break;
            }
        }
    }

    /**
     * Update delivery entity
     */
    private String getDeliveryEntityData() {
        if (mColumnName != null && !TextUtils.isEmpty(mColumnName) && !mColumnName.equals("")) {
            switch (mColumnName) {
                case DatabaseHelper.CUST_NAME:
                    return mDeliveryEntity.getcust_name();
                case DatabaseHelper.CUST_ADDRESS1:
                    return mDeliveryEntity.getcust_address1();
                case DatabaseHelper.CUST_ADDRESS2:
                    return mDeliveryEntity.getcust_address2();
                case DatabaseHelper.PINCODE:
                    return mDeliveryEntity.getpincode();
                case DatabaseHelper.CITY:
                    return mDeliveryEntity.getcity();
                case DatabaseHelper.STATE:
                    return mDeliveryEntity.getstate();
                case DatabaseHelper.LANDMARK:
                    return mDeliveryEntity.getlandmark();
                case DatabaseHelper.CUST_CONTACT1:
                    return mDeliveryEntity.getcust_contact1();
                case DatabaseHelper.ALTERNATE_NO:
                    return mDeliveryEntity.getalternate_no();
                case DatabaseHelper.ORDERNUMBER:
                    return mDeliveryEntity.getorderNumber();
                case DatabaseHelper.PACKAGESNO:
                    return mDeliveryEntity.getpackagesNo();
                case DatabaseHelper.DRSNUMBER:
                    return mDeliveryEntity.getDRSNumber();
                case DatabaseHelper.TOTALDOCKETSINDRS:
                    return mDeliveryEntity.getTotalDocketsInDRS();
                case DatabaseHelper.PRODCUTDESC:
                    return mDeliveryEntity.getProdcutDesc();
                case DatabaseHelper.DOCKET_NUMBER:
                    return mDeliveryEntity.getdocket_number();
                case DatabaseHelper.CHOICE_OF_PAYMENT:
                    return mDeliveryEntity.getchoice_of_payment();
                case DatabaseHelper.AMOUNT_TOBE_PAID:
                    return mDeliveryEntity.getamount_tobe_paid();
                case DatabaseHelper.DUE_DATE:
                    return mDeliveryEntity.getdue_date();
                case DatabaseHelper.BOY_ID:
                    return mDeliveryEntity.getboy_id();
                case DatabaseHelper.STATUS:
                    return mDeliveryEntity.getstatus();
                case DatabaseHelper.FAILED_REASON:
                    return mDeliveryEntity.getfailed_reason();
                case DatabaseHelper.DELIVERY_TIME:
                    return mDeliveryEntity.getdelivery_time();
                case DatabaseHelper.COMMENTS:
                    return mDeliveryEntity.getcomments();
                case DatabaseHelper.MODE_OF_PAYMENT:
                    return mDeliveryEntity.getmode_of_payment();
                case DatabaseHelper.TRANSACTION_NO:
                    return mDeliveryEntity.gettransaction_no();
                case DatabaseHelper.AUTH_CODE:
                    return mDeliveryEntity.getauth_code();
                case DatabaseHelper.CARD_TYPE:
                    return mDeliveryEntity.getcard_type();
                case DatabaseHelper.BANK_NAME:
                    return mDeliveryEntity.getbank_name();
                case DatabaseHelper.CUSTOMER_SIGN:
                    return mDeliveryEntity.getcustomer_sign();
                case DatabaseHelper.CUSTOMER_IMG:
                    return mDeliveryEntity.getcustomer_img();
                case DatabaseHelper.CUSTOMER_IMG2:
                    return mDeliveryEntity.getcustomer_img2();
                case DatabaseHelper.CUSTOMER_IMG3:
                    return mDeliveryEntity.getcustomer_img3();
                case DatabaseHelper.HAPPY_DELIVERY:
                    return mDeliveryEntity.gethappy_delivery();
                case DatabaseHelper.HAPPY_DELIVERY_IMG:
                    return mDeliveryEntity.gethappy_delivery_img();
                case DatabaseHelper.LATITUDE:
                    return mDeliveryEntity.getlatitude();
                case DatabaseHelper.LONGITUDE:
                    return mDeliveryEntity.getlongitude();
                case DatabaseHelper.SKU_DETAILS:
                    return mDeliveryEntity.getsku_details();
                case DatabaseHelper.LAST_UPDATED:
                    return mDeliveryEntity.getlast_updated();
                case DatabaseHelper.PREFERRED_TRANSACTION_TIME:
                    return mDeliveryEntity.getpreferred_transaction_time();
                case DatabaseHelper.ORIGINAL_AMOUNT_PAID:
                    return mDeliveryEntity.getoriginal_amount_paid();
                case DatabaseHelper.USER_ID:
                    return mDeliveryEntity.getuser_id();
                case DatabaseHelper.DELIVERED_TO:
                    return mDeliveryEntity.getdelivered_to();
                case DatabaseHelper.DELIVERED_TO_RELATION:
                    return mDeliveryEntity.getdelivered_to_relation();
                case DatabaseHelper.PRIRORITY_DELIVERY:
                    return mDeliveryEntity.getprirority_delivery();
                case DatabaseHelper.CLOSING_KM:
                    return mDeliveryEntity.getclosing_km();
                case DatabaseHelper.IMEI_NUMBER:
                    return mDeliveryEntity.getimei_number();
                case DatabaseHelper.SYNC_UPDATE:
                    return mDeliveryEntity.getsync_update();
                case DatabaseHelper.UPDATED_ON_ANDROID:
                    return mDeliveryEntity.getupdated_on_android();
                case DatabaseHelper.UPDATED_ON_WEBX:
                    return mDeliveryEntity.getupdated_on_webx();
                case DatabaseHelper.LOC_ID:
                    return mDeliveryEntity.getloc_id();
                case DatabaseHelper.C_ID:
                    return mDeliveryEntity.getc_id();
                case DatabaseHelper.ADMIN_ID:
                    return mDeliveryEntity.getadmin_id();
                case DatabaseHelper.EXTRA1:
                    return mDeliveryEntity.getextra1();
                case DatabaseHelper.EXTRA2:
                    return mDeliveryEntity.getextra2();
                case DatabaseHelper.EXTRA3:
                    return mDeliveryEntity.getextra3();
                case DatabaseHelper.EXTRA4:
                    return mDeliveryEntity.getextra4();
                case DatabaseHelper.EXTRA5:
                    return mDeliveryEntity.getextra5();
                case DatabaseHelper.EXTRA6:
                    return mDeliveryEntity.getextra6();
                case DatabaseHelper.SYNC_FLAG:
                    return mDeliveryEntity.getsync_flag();
                case DatabaseHelper.LAST_LAT:
                    return mDeliveryEntity.getlast_lat();
                case DatabaseHelper.LAST_LNG:
                    return mDeliveryEntity.getlast_lng();
                case DatabaseHelper.LAST_ATTEMPT_STATUS:
                    return mDeliveryEntity.getlast_attempt_status();
                case DatabaseHelper.ATTEMPT_NUMBER:
                    return mDeliveryEntity.getattempt_number();
                case DatabaseHelper.ATTEMPT_SLOT:
                    return mDeliveryEntity.getattempt_slot();
                case DatabaseHelper.NPS_SCORE:
                    return mDeliveryEntity.getnps_score();
                case DatabaseHelper.SEQ_PREDICTED:
                    return mDeliveryEntity.getseq_predicted();
                case DatabaseHelper.SEQ_SELECTED:
                    return mDeliveryEntity.getseq_selected();
                case DatabaseHelper.SEQ_TRANSACTION:
                    return mDeliveryEntity.gettransaction_no();
                case DatabaseHelper.USER_COMMENTS:
                    return mDeliveryEntity.getuser_comments();
                case DatabaseHelper.BATTERY_LEVEL:
                    return mDeliveryEntity.getbattery_level();
                case DatabaseHelper.TIME_ERP_IMPORT_INITIATED:
                    return mDeliveryEntity.gettime_erp_import_completed();
                case DatabaseHelper.TIME_ERP_IMPORT_COMPLETED:
                    return mDeliveryEntity.gettime_erp_import_completed();
                case DatabaseHelper.TIME_AVAILABLE_ANDROID_INITIATED:
                    return mDeliveryEntity.gettime_available_android_initiated();
                case DatabaseHelper.TIME_AVAILABLE_ANDROID_COMPLETED:
                    return mDeliveryEntity.gettime_available_android_completed();
                case DatabaseHelper.REVERSE_DOCKET_NUMBER:
                    return mDeliveryEntity.getreverse_docket_number();
                case DatabaseHelper.RECEIPT_URL:
                    return mDeliveryEntity.getreceipt_url();
            }
        }
        return "";
    }

    /**
     * Insert data in delivery table
     */
    private void insertInDatabase() {
        // Check location is available or not
        GPSTracker gpsTracker = new GPSTracker(SuccessActivity.this);
        if (gpsTracker.canGetLocation()) {
            HashMap<String, String> location = Utility.getLocation(SuccessActivity.this);
            String latitude = location.get(Constants.LATITUDE);
            String longitude = location.get(Constants.LONGITUDE);

            // If latitude or longitude is 0.0, try again
            if (latitude == null || latitude.equalsIgnoreCase("0.0") || longitude.equalsIgnoreCase("0.0")) {
                // Latitude and longitude not available
                Utility.showToast(SuccessActivity.this, "Fetching location. Please try again.");
            } else {
                // Latitude and longitude are available
                mDeliveryEntity.setdelivery_time(Utility.getDeliveryTime());
                mDeliveryEntity.setimei_number(Utility.getDeviceId());
                mDeliveryEntity.setboy_name(mDrsEntity.getdriver_name());
                mDeliveryEntity.setlatitude(latitude);
                mDeliveryEntity.setlongitude(longitude);
                mDeliveryEntity.setstatus(mScreen);
                mDeliveryEntity.setsync("0");

                DeliveryHelper.getInstance().insertOrUpdate(mDeliveryEntity);
                if (mScreen.equalsIgnoreCase(Constants.STATUS_SUCCESS)) {
                    if (mJobType.equalsIgnoreCase(Constants.JOB_TYPE_DELIVERY) ||
                            mJobType.equalsIgnoreCase(Constants.JOB_TYPE_DELIVERY1) ||
                            mJobType.equalsIgnoreCase(Constants.JOB_TYPE_TNB)) {
                        // If jobtype is Delivery ot TNB
                        // If docket is delivered, then set status of all the items as "1"
                        ItemHelper.getInstance().updateSkuItemStatus(mDrsEntity.getdrs_docket(), "1");
                    }
                } else if (mScreen.equalsIgnoreCase(Constants.STATUS_FAIL) && !mFailCategory.contains("Partial Return")) {
                    // If docket is failed, then set status of all the items as "0"
                    ItemHelper.getInstance().updateSkuItemStatus(mDrsEntity.getdrs_docket(), "0");
                }

                UtilityScheduler.startPushScheduler(SuccessActivity.this, mDeliveryEntity.getdrs_docket());

                // Save Amount COD and SOD in shared preferences
                saveAmount();

                // Successfully submitted, now go to tab screen
                goToTabScreen();
            }
        } else {
            Utility.showNormalAlertDialog(SuccessActivity.this, "GPS Error", "Please turn your Gps ON");
        }
    }

    /**
     * Check whether this data contains or not
     *
     * @return
     */
    private boolean checkDataContains() {
        int length = Constants.DESIGN_CUSTOMER_INITIATED_DELAY_AUTO_ACTION.length;
        for (int i = 0; i < length; i++) {
            String text = Constants.DESIGN_CUSTOMER_INITIATED_DELAY_AUTO_ACTION[i];
            if (mData.equalsIgnoreCase(text)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Save COD and SOD in shared preferences
     */
    private void saveAmount() {
        String modeOfPayment = mDeliveryEntity.getmode_of_payment();
        String amount = mDeliveryEntity.getoriginal_amount_paid();
        float finalAmount = 0;
        switch (modeOfPayment) {
            case Constants.PAYMENT_TYPE_EZETAP:
            case Constants.PAYMENT_TYPE_MSWIPE:
                String SOD = null;
                switch (mScreen) {
                    case Constants.STATUS_SUCCESS:
                        // If success, payment is of Delivered case
                        SOD = Constants.AMOUNT_SOD;
                        break;
                    case Constants.STATUS_FAIL:
                        // If fail payment is of Partial Return case
                        SOD = Constants.AMOUNT_SOD_PR;
                        break;
                }

                if (SOD != null) {
                    String savedSOD = Utility.getFromSharedPrefs(SuccessActivity.this, SOD);
                    if (savedSOD.equalsIgnoreCase("")) {
                        savedSOD = "0";
                    }
                    finalAmount = Float.parseFloat(savedSOD) + Float.parseFloat(amount);
                    Utility.saveToSharedPrefs(SuccessActivity.this, SOD, String.valueOf(finalAmount));
                }

                break;
            case Constants.PAYMENT_TYPE_CASH:
                String COD = null;
                switch (mScreen) {
                    case Constants.STATUS_SUCCESS:
                        // If success payment is of Delivered case
                        COD = Constants.AMOUNT_COD;
                        break;
                    case Constants.STATUS_FAIL:
                        // If fail payment is of Partial Return case
                        COD = Constants.AMOUNT_COD_PR;
                        break;
                }

                if (COD != null) {
                    String savedCOD = Utility.getFromSharedPrefs(SuccessActivity.this, COD);
                    if (savedCOD.equalsIgnoreCase("")) {
                        savedCOD = "0";
                    }
                    finalAmount = Float.parseFloat(savedCOD) + Float.parseFloat(amount);
                    Utility.saveToSharedPrefs(SuccessActivity.this, COD, String.valueOf(finalAmount));
                }

                break;
        }
    }

    /**
     * Show wrong date alert
     */
    private void showDateDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(SuccessActivity.this);
        builder.setTitle(title);
        builder.setMessage(message);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(android.provider.Settings.ACTION_DATE_SETTINGS));
                dialog.dismiss();
            }
        });

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Go to tab screen
     */
    private void goToTabScreen() {
        Intent tabIntent = new Intent(SuccessActivity.this, TabActivity.class);
        tabIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(tabIntent);
        finish();
    }

    /**
     * Go to camera screen
     */
    private void goToCameraScreen() {
        Intent cameraIntent = new Intent(SuccessActivity.this, CameraActivity.class);
        cameraIntent.putExtra("Path", fileUri.getPath());
        startActivityForResult(cameraIntent, REQUEST_CAMERA_CODE);
    }

    /**
     * Go to fail screen to perform fail operations
     * Added by Harshita
     */
    private void goToFailScreen() {
        Intent failIntent = new Intent(SuccessActivity.this, FailActivity.class);
        failIntent.putExtra(Constants.JOB_TYPE, mJobType);
        failIntent.putExtra(Constants.DRS_DOCKET, mDrsEntity.getdrs_docket());
//        EventBus.getDefault().postSticky(mDrsEntity);
        startActivity(failIntent);
        finish();
    }
}
