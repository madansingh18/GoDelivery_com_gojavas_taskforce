package com.gojavas.taskforce.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.gojavas.taskforce.R;
import com.gojavas.taskforce.database.DatabaseHelper;
import com.gojavas.taskforce.database.DeliveryHelper;
import com.gojavas.taskforce.database.DrsHelper;
import com.gojavas.taskforce.database.DrsUtility;
import com.gojavas.taskforce.database.ItemHelper;
import com.gojavas.taskforce.database.UserHelper;
import com.gojavas.taskforce.entity.DeliveryEntity;
import com.gojavas.taskforce.entity.DrsEntity;
import com.gojavas.taskforce.entity.ItemEntity;
import com.gojavas.taskforce.manager.DesignManager;
import com.gojavas.taskforce.utils.Constants;
import com.gojavas.taskforce.utils.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by GJS280 on 22/4/2015.
 */
public class DocketDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout mScrollMainLayout, mMiddleLayout;
    private ImageView mSmsButton, mCallButton, mCustomerCareButton;

    private JSONObject mDesignJson;
    private String mScreen = "", mJobType;
    private DrsEntity mDrsEntity;
    private HashMap<String, String> drsMap;
    private int mMinHeight, mEight, mSixteen, mFortyEight;
    String showAllDetails = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_docket_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initializeValues();
        regsiterViews();
        registerListeners();

        populateDocketDetails();

        switch (mScreen) {
            case Constants.DESIGN_SUCCESS:
            case Constants.DESIGN_FAIL:
                mMiddleLayout.setVisibility(View.GONE);
                break;
            case Constants.DESIGN_PENDING:
                mMiddleLayout.setVisibility(View.VISIBLE);
                mSmsButton.setVisibility(View.VISIBLE);
                mCallButton.setVisibility(View.VISIBLE);
                mCustomerCareButton.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.docket_detail_sms_button:
                showContactDialog("Send SMS", "sms");
                break;
            case R.id.docket_detail_call_button:
                showContactDialog("Make phone call", "call");
                break;
            case R.id.docket_detail_customercare_button:
                showCustomerCareAlertDialog();
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Initialize required values
     */
    private void initializeValues() {
        // Get Drs from database
        Intent intent = getIntent();
        mDesignJson = DesignManager.getInstance().getDesignJson();
        mScreen = intent.getStringExtra(Constants.SCREEN); // Tells from which fragment from tab screen we are coming
        mJobType = intent.getStringExtra(Constants.JOB_TYPE);
        String drs_docket = getIntent().getStringExtra(DatabaseHelper.DRS_DOCKET);
        mDrsEntity = DrsHelper.getInstance().getDrs(drs_docket);
        showAllDetails = intent.getStringExtra("showalldetails");
        String docketNo = mDrsEntity.getdocketno();
        String title = "";
        switch (showAllDetails) {
            case Constants.DESIGN_PENDING:
                drsMap = DrsHelper.getInstance().getDrsDetail(drs_docket);
                title = "<b>" + docketNo + " (Pending)</b>";
                break;
            case Constants.DESIGN_SUCCESS:
                drsMap = DeliveryHelper.getInstance().getDeliveryDetailHashMap(drs_docket);
                title = "<b>" + docketNo + " (Success)</b>";
                break;
            case Constants.DESIGN_FAIL:
                drsMap = DeliveryHelper.getInstance().getDeliveryDetailHashMap(drs_docket);
                title = "<b>" + docketNo + " (Failed)</b>";
                break;
        }

        getSupportActionBar().setTitle(Html.fromHtml(title));
    }

    /**
     * Register all the views
     */
    private void regsiterViews() {
        mScrollMainLayout = (LinearLayout) findViewById(R.id.scroll_main_layout);
        mMiddleLayout = (LinearLayout) findViewById(R.id.middle_layout);
        mSmsButton = (ImageView) findViewById(R.id.docket_detail_sms_button);
        mCallButton = (ImageView) findViewById(R.id.docket_detail_call_button);
        mCustomerCareButton = (ImageView) findViewById(R.id.docket_detail_customercare_button);

        // Height and padding
        mMinHeight = (int) getResources().getDimension(R.dimen.fortyeight);
        mEight = (int) getResources().getDimension(R.dimen.eight);
        mSixteen = (int) getResources().getDimension(R.dimen.sixteen);
        mFortyEight = (int) getResources().getDimension(R.dimen.fortyeight);
    }

    private void registerListeners() {
        mSmsButton.setOnClickListener(this);
        mCallButton.setOnClickListener(this);
        mCustomerCareButton.setOnClickListener(this);
    }

    /**
     * Populate docket details on the basis of job type
     */
    private void populateDocketDetails() {
        switch (mJobType) {
            case Constants.JOB_TYPE_MOBILE_PICKUP:
                addFields(DrsUtility.DRS_TABLE_FIELDS_MOBILE_PICKUP);
                addMiddleButtons(Constants.DESIGN_ACTION);
                break;
            case Constants.JOB_TYPE_PICKUP:
                switch (showAllDetails) {
                    case Constants.DESIGN_PENDING:
                        addFields(DrsUtility.DRS_TABLE_FIELDS_PICKUP);
                        break;
                    case Constants.DESIGN_SUCCESS:
                        addFields(DrsUtility.DRS_TABLE_FIELDS_PICKUP_SUCCESS);
                        break;
                    case Constants.DESIGN_FAIL:
                        addFields(DrsUtility.DRS_TABLE_FIELDS_DELIVERY_PICKUP_FAIL);
                        break;
                }
                addMiddleButtons(Constants.DESIGN_ACTION);
                break;
            case Constants.JOB_TYPE_90_MINUTES:
                // Remove push notification if visible
                TaskForceApplication.getInstance().removeNinetyMinutesNotification();

                switch (showAllDetails) {
                    case Constants.DESIGN_PENDING:
                    case Constants.DESIGN_ACCEPT:
                    case Constants.DESIGN_REJECT:
                        addFields(DrsUtility.DRS_TABLE_FIELDS_90_MINUTES);
                        break;
                    case Constants.DESIGN_SUCCESS:
                    case Constants.DESIGN_FAIL:
                        addFields(DrsUtility.DRS_TABLE_FIELDS_90_MINUTES_SUCCESS);
                        break;
                }
                String status = mDrsEntity.getstatus();
                if (status != null && status.equalsIgnoreCase(Constants.DESIGN_ACCEPT)) {
                    addMiddleButtons(Constants.DESIGN_ACTION);
                } else {
                    addMiddleButtons(Constants.DESIGN_PRIMARY_ACTION);
                }
                break;
            case Constants.JOB_TYPE_TNB:
                switch (showAllDetails) {
                    case Constants.DESIGN_PENDING:
                        addFields(DrsUtility.DRS_TABLE_FIELDS_TNB);
                        break;
                    case Constants.DESIGN_SUCCESS:
                        addFields(DrsUtility.DRS_TABLE_FIELDS_DELIVERY_SUCCESS);
                        break;
                    case Constants.DESIGN_FAIL:
                        addFields(DrsUtility.DRS_TABLE_FIELDS_DELIVERY_PICKUP_FAIL);
                        break;
                }
                addMiddleButtons(Constants.DESIGN_ACTION);
                break;
            case Constants.JOB_TYPE_FIRST_PICKUP:
                addFields(DrsUtility.DRS_TABLE_FIELDS_FIRST_PICKUP);
                addMiddleButtons(Constants.DESIGN_ACTION);
                break;
            case Constants.JOB_TYPE_DELIVERY:
//            case Constants.JOB_TYPE_EXCHANGE:
                switch (showAllDetails) {
                    case Constants.DESIGN_PENDING:
                        addFields(DrsUtility.DRS_TABLE_FIELDS_DELIVERY);
                        break;
                    case Constants.DESIGN_SUCCESS:
                        addFields(DrsUtility.DRS_TABLE_FIELDS_DELIVERY_SUCCESS);
                        break;
                    case Constants.DESIGN_FAIL:
                        addFields(DrsUtility.DRS_TABLE_FIELDS_DELIVERY_PICKUP_FAIL);
                        break;
                }
                addMiddleButtons(Constants.DESIGN_ACTION);
                break;
            case Constants.JOB_TYPE_DELIVERY1:
                addFields(DrsUtility.DRS_TABLE_FIELDS_DELIVERY1);
                addMiddleButtons(Constants.DESIGN_ACTION);
                break;
        }
    }

    /**
     * Add fields form the databse
     *
     * @param tableFields
     */
    private void addFields(String[] tableFields) {
        int length = tableFields.length;
        for (int i = 0; i < length; i++) {
            String title = tableFields[i];
            String detail = drsMap.get(tableFields[i]);
            LinearLayout layout = createChildLayout(title, detail, i);
            mScrollMainLayout.addView(layout);
            mScrollMainLayout.addView(createView());
        }

        ArrayList<ItemEntity> arrayList = new ArrayList<>();
        if (mJobType.equalsIgnoreCase(Constants.JOB_TYPE_EXCHANGE)) {
            arrayList = ItemHelper.getInstance().getExchangeItems(mDrsEntity.getdrs_docket(), mDrsEntity.getexchange_requestid());
        } else {
            arrayList = ItemHelper.getInstance().getItems(mDrsEntity.getdrs_docket());
        }
        int size = arrayList.size();
        Log.i("arrayList", size + "");

        if (size > 0)
            if(mJobType.equalsIgnoreCase(Constants.JOB_TYPE_EXCHANGE)){
                // Added By Harshita
                mScrollMainLayout.addView(createChildTextViewDivider("PICKUP SKU ITEMS", mSixteen, mEight));
            }else{
                mScrollMainLayout.addView(createChildTextViewDivider("SKU ITEMS", mSixteen, mEight));
            }
        for (int i = 0; i < size; i++) {
            mScrollMainLayout.addView(createSkuItem(arrayList.get(i)));
            mScrollMainLayout.addView(createView());

        }
    }

    /**
     * This method returns the child LinearLayout for docket details
     *
     * @param title
     * @param detail
     * @return
     */
    private LinearLayout createChildLayout(String title, String detail, int boldPosition) {
        LinearLayout layout = new LinearLayout(DocketDetailActivity.this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layout.setLayoutParams(params);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setMinimumHeight(mMinHeight);

        TextView titleTextView = createChildTextView(title, mSixteen, mEight, true, boldPosition);
        layout.addView(titleTextView);
        if (detail != null && (detail.endsWith(".png") || detail.endsWith(".PNG") || detail.endsWith(".jpg") || detail.endsWith(".JPG"))) {
            ImageView imageView = createImageView(detail, mEight, mSixteen, false, boldPosition);
            layout.addView(imageView);
        } else {
            if (title.equalsIgnoreCase("Mode of Payment")) {
                switch (detail) {
                    case "Y":
                    case "y":
                        detail = "Cash";
                        break;
                    case "S":
                    case "s":
                        detail = "SOD";
                        break;
                    case "N":
                    case "n":
                        detail = "Prepaid";
                        break;
                    // added by harshita for Excahnge Job Type
                    case "E":
                    case "e":
                        detail = "Exchange";
                        break;
                    default:
                        detail = "";
                        break;
                }
            }
            TextView detailTextView = createChildTextView(detail, mEight, mSixteen, false, boldPosition);
            layout.addView(detailTextView);

        }

        return layout;
    }

    /**
     * Create child TextView
     *
     * @param text
     * @param leftPadding
     * @param rightPadding
     * @return
     */
    private TextView createChildTextView(String text, int leftPadding, int rightPadding, boolean isTitle, int boldPosition) {
        TextView textView = new TextView(DocketDetailActivity.this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        params.weight = 1;
        textView.setLayoutParams(params);
        textView.setPadding(leftPadding, 2, rightPadding, 2);
        textView.setGravity(Gravity.CENTER_VERTICAL);
        textView.setText(text);
        if (isTitle)
            textView.setTextColor(Color.BLACK);

        if (boldPosition == 0 || boldPosition == 1) {
            textView.setTypeface(null, Typeface.BOLD);
            textView.setTextColor(Color.BLACK);
        }

        return textView;
    }

    /**
     * Add divider between rows
     *
     * @return
     */
    private View createView() {
        View view = new View(DocketDetailActivity.this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        view.setBackgroundColor(Color.LTGRAY);
        view.setMinimumHeight(1);

        return view;
    }

    private TextView createChildTextViewDivider(String text, int leftPadding, int rightPadding) {
        TextView textView = new TextView(DocketDetailActivity.this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        params.weight = 1;
        textView.setLayoutParams(params);
        textView.setPadding(leftPadding, 17, rightPadding, 17);
        textView.setGravity(Gravity.CENTER_VERTICAL);
        textView.setText(text);
        textView.setBackgroundColor(Color.GRAY);
        textView.setTextColor(Color.WHITE);

        return textView;
    }

    private ImageView createImageView(String text, int leftPadding, int rightPadding, boolean isTitle, int boldPosition) {

        ImageView imageView = new ImageView(DocketDetailActivity.this);
        imageView.setPadding(leftPadding, 2, rightPadding, 2);

        imageView.setLayoutParams(new FrameLayout.LayoutParams(250, 250, Gravity.CENTER));

        Bitmap bitmap = null;
        bitmap = Utility.getScaledBitmap(Utility.getApplicationPath(Constants.PATH_CAPTURED_IMAGE) + text,
                Utility.getScreenWidth(DocketDetailActivity.this),
                Utility.getScreenHeight(DocketDetailActivity.this));
        if (bitmap != null) {
//            imageView.setImageBitmap(bitmap);
            imageView.setBackgroundDrawable(new BitmapDrawable(bitmap));
        }


        return imageView;
    }

    /**
     * This method returns the child LinearLayout for docket details
     *
     * @param itemEntity
     * @return
     */
    private LinearLayout createSkuItem(ItemEntity itemEntity) {
        LinearLayout layout = new LinearLayout(DocketDetailActivity.this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layout.setLayoutParams(params);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setMinimumHeight(mMinHeight);
        layout.setPadding(0, 3, 0, 3);

        // Get sku item status on the basis of jobtype
        String jobType = mDrsEntity.getjobtype();
        String status = "";
        switch (jobType) {
            case Constants.JOB_TYPE_DELIVERY:
            case Constants.JOB_TYPE_DELIVERY1:
            case Constants.JOB_TYPE_TNB:
                status = (itemEntity.getStatus().equalsIgnoreCase("1") ? "Delivered" : "Not Delivered");
                break;
            case Constants.JOB_TYPE_PICKUP:
            case Constants.JOB_TYPE_MOBILE_PICKUP:
            case Constants.JOB_TYPE_FIRST_PICKUP:
            case Constants.JOB_TYPE_90_MINUTES:
            case Constants.JOB_TYPE_EXCHANGE:
                status = (itemEntity.getStatus().equalsIgnoreCase("1") ? "Close" : "Open");
                break;
        }
        TextView skuIdTextView = createChildTextView("SKU Id - " + itemEntity.getsku_id(), mSixteen, mEight, true, 3);
        TextView skuCostTextView = createChildTextView("SKU Cost - " + itemEntity.getsku_cost(), mSixteen, mEight, true, 3);
        TextView skuDescriptionTextView = createChildTextView("SKU Desc - " + itemEntity.getsku_description(), mSixteen, mEight, true, 3);
        TextView skuStatusTextView = createChildTextView("SKU Status - " + status, mSixteen, mEight, true, 3);
        skuStatusTextView.setTextColor(getResources().getColor(R.color.app_color));

        layout.addView(skuIdTextView);
        layout.addView(skuCostTextView);
        layout.addView(skuDescriptionTextView);
        layout.addView(skuStatusTextView);

        return layout;
    }


    /**
     * Add buttons in middle layout
     */
    private void addMiddleButtons(String action) {
        try {
            JSONObject jobJson = mDesignJson.getJSONObject(mJobType);
            JSONArray buttonArray = jobJson.getJSONArray(action);
            int length = buttonArray.length();
            for (int i = 0; i < length; i++) {
                JSONObject buttonJson = buttonArray.getJSONObject(i);
                final String type = buttonJson.getString(Constants.DESIGN_TYPE);
                String title = buttonJson.getString(Constants.DESIGN_TITLE);
                String color = buttonJson.getString(Constants.DESIGN_COLOR);
                Button button = creatChildButton(title, Color.parseColor(color));
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (type) {
                            case Constants.DESIGN_SUCCESS:
                                goToSuccessScreen();
                                break;
                            case Constants.DESIGN_FAIL:
                                goToFailScreen();
                                break;
                            case Constants.DESIGN_ACCEPT:
                                // Send accept action to server
                                ninetyMinuteAction(Constants.DESIGN_ACCEPT);
                                // Update database
                                mDrsEntity.setstatus(Constants.DESIGN_ACCEPT);
                                DrsHelper.getInstance().insertOrUpdate(mDrsEntity);
                                finish();
                                break;
                            case Constants.DESIGN_REJECT:
                                // Send reject action to server
                                ninetyMinuteAction(Constants.DESIGN_REJECT);
                                // Update database
                                jobRejected();
                                break;
                        }
                    }
                });
                mMiddleLayout.addView(button);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Create child button
     *
     * @param text
     * @param color
     * @return
     */
    private Button creatChildButton(String text, int color) {
        Button button = new Button(DocketDetailActivity.this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        params.weight = 1;
        params.gravity = Gravity.CENTER;
        button.setLayoutParams(params);
        button.setText(text);
        button.setBackgroundColor(color);
        button.setMinimumHeight(mFortyEight);
        return button;
    }

    /**
     * Go to success screen to perform success operations
     */
    private void goToSuccessScreen() {
        Intent successIntent = new Intent(DocketDetailActivity.this, SuccessActivity.class);
        successIntent.putExtra(Constants.JOB_TYPE, mJobType);
        successIntent.putExtra(Constants.SCREEN, Constants.STATUS_SUCCESS);
        successIntent.putExtra(Constants.DRS_DOCKET, mDrsEntity.getdrs_docket());
        successIntent.putExtra(Constants.FAIL_CATEGORY, "false");
//        EventBus.getDefault().postSticky(mDrsEntity);
        startActivity(successIntent);
    }

    /**
     * Go to fail screen to perform fail operations
     */
    private void goToFailScreen() {
        Intent failIntent = new Intent(DocketDetailActivity.this, FailActivity.class);
        failIntent.putExtra(Constants.JOB_TYPE, mJobType);
        failIntent.putExtra(Constants.DRS_DOCKET, mDrsEntity.getdrs_docket());
//        EventBus.getDefault().postSticky(mDrsEntity);
        startActivity(failIntent);
    }

    /**
     * Show dialog for contacting user via sms or call
     *
     * @param title
     * @param type
     */
    private void showContactDialog(String title, final String type) {
        LinearLayout linearLayout = new LinearLayout(DocketDetailActivity.this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        // Contact TextView
        TextView contactTextView = new TextView(DocketDetailActivity.this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.height = mFortyEight;
        contactTextView.setLayoutParams(params);
        contactTextView.setGravity(Gravity.CENTER);
        contactTextView.setPadding(0, mEight, 0, mEight);
        contactTextView.setText("Contact");
        contactTextView.setTextColor(Color.BLACK);

        // Alternate TextView
        TextView alternateContactTextView = new TextView(DocketDetailActivity.this);
        alternateContactTextView.setLayoutParams(params);
        alternateContactTextView.setGravity(Gravity.CENTER);
        alternateContactTextView.setPadding(0, mEight, 0, mEight);
        alternateContactTextView.setText("Alternate Contact");
        alternateContactTextView.setTextColor(Color.BLACK);

        // Divider
        ImageView divider = new ImageView(this);
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params1.height = (int) getResources().getDimension(R.dimen.one);
        params1.setMargins(mFortyEight, 0, mFortyEight, 0);
        divider.setLayoutParams(params1);
        divider.setBackgroundColor(Color.LTGRAY);

        linearLayout.addView(contactTextView);
        linearLayout.addView(divider);
        linearLayout.addView(alternateContactTextView);

        AlertDialog.Builder builder = new AlertDialog.Builder(DocketDetailActivity.this);
        builder.setTitle(title);
        builder.setView(linearLayout);

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        // Click listeners
        contactTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String contactNo = mDrsEntity.getcsgeteleno();
                if (type.equalsIgnoreCase("sms")) {
                    smsAlertDialog(contactNo);
                } else if (type.equalsIgnoreCase("call")) {
                    Utility.phoneCall(contactNo);
                }
                alertDialog.dismiss();
            }
        });
        alternateContactTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String alternateContactNo = mDrsEntity.getalternate_number();
                if (alternateContactNo == null || alternateContactNo.isEmpty()) {
                    Utility.showToast(DocketDetailActivity.this, "No alternate contact number");
                } else {
                    if (type.equalsIgnoreCase("sms")) {
                        smsAlertDialog(alternateContactNo);
                    } else if (type.equalsIgnoreCase("call")) {
                        Utility.phoneCall(alternateContactNo);
                    }
                    alertDialog.dismiss();
                }
            }
        });
    }

    /**
     * Create Delivery entity
     */
    private void jobRejected() {
        DeliveryEntity mDeliveryEntity = new DeliveryEntity();
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
        mDeliveryEntity.setchoice_of_payment(mDrsEntity.getchoiceofpayment());
        mDeliveryEntity.setamount_tobe_paid(mDrsEntity.getcod_amt());
        mDeliveryEntity.setboy_id(mDrsEntity.getdriver_id());
        mDeliveryEntity.setboy_id(UserHelper.getInstance().getUserName());
        mDeliveryEntity.setclosing_km(mDrsEntity.getstart_km());
        mDeliveryEntity.setdelivery_time(Utility.getDeliveryTime());
        mDeliveryEntity.setimei_number(Utility.getDeviceId());
        mDeliveryEntity.setstatus(Constants.DESIGN_REJECT);

        DeliveryHelper.getInstance().insertOrUpdate(mDeliveryEntity);

        goToTabScreen();
    }

    /**
     * Go to tab screen
     */
    private void goToTabScreen() {
        Intent tabIntent = new Intent(DocketDetailActivity.this, TabActivity.class);
        tabIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(tabIntent);
        finish();
    }

    /**
     * Show sms message in alert dialog
     *
     * @param phoneNo
     */
    private void smsAlertDialog(final String phoneNo) {
        String orderNumber = mDrsEntity.getordernumber();
        if (orderNumber == null || TextUtils.isEmpty(orderNumber) || orderNumber.equals("")) {
            orderNumber = mDrsEntity.getdocketno();
        }
        final String message = Utility.smsText(mJobType, orderNumber, mDrsEntity.getclient_name());
        AlertDialog.Builder builder = new AlertDialog.Builder(DocketDetailActivity.this);
        builder.setTitle("Send SMS");
        builder.setMessage(message);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (phoneNo == null || TextUtils.isEmpty(phoneNo) || phoneNo.length() == 0 || phoneNo.contains("array") || phoneNo.contains("Array")) {
                    Utility.showToast(DocketDetailActivity.this, "Invalid phone number. Please try different phone number");
                    return;
                }
                Utility.sendSms(phoneNo, message, DocketDetailActivity.this);
                dialog.dismiss();
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
     * Show logout alert dialog
     */
    private void showCustomerCareAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(DocketDetailActivity.this);
        builder.setMessage("Call customer care");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Utility.phoneCall(Constants.CUSTOMER_CARE);
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
     * Accept/Reject ninety minute request
     *
     * @param action
     */
    private void ninetyMinuteAction(String action) {
        JSONObject actionRequestJson = new JSONObject();
        try {
            actionRequestJson.put("request_id", mDrsEntity.getdocketno());
            actionRequestJson.put(DatabaseHelper.DRIVER_ID, mDrsEntity.getdriver_id());
            actionRequestJson.put("action", action);
        } catch (JSONException je) {
            je.printStackTrace();
        }
        JsonObjectRequest ninetyMinuteActionRequest = new JsonObjectRequest(Request.Method.POST, Constants.NINETY_MINUTE_ACTION_URL, actionRequestJson, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                System.out.println("90 minute action: " + jsonObject.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
            }
        });
        TaskForceApplication.getInstance().addToRequestQueue(ninetyMinuteActionRequest);
    }
}
