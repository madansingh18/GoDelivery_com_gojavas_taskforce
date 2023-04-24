package com.gojavas.taskforce.ui.activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.gojavas.taskforce.R;
import com.gojavas.taskforce.database.DatabaseHelper;
import com.gojavas.taskforce.database.UserHelper;
import com.gojavas.taskforce.entity.UserEntity;
import com.gojavas.taskforce.manager.BackupManager;
import com.gojavas.taskforce.utils.Constants;
import com.gojavas.taskforce.utils.Utility;
import com.gojavas.taskforce.utils.UtilityScheduler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by GJS280 on 10/4/2015.
 */
public class PhoneNumberValidation extends AppCompatActivity implements View.OnClickListener {

    private Button mDoneButton;
    private EditText mValidationCodeEditText1, mValidationCodeEditText2, mValidationCodeEditText3, mValidationCodeEditText4;

    private JSONObject mOtpJson;

    ProgressDialog progressDialog;

    /**
     *  visible layout after pulling data from server using SchedularSync Service
     */
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if(intent.getAction().equals(Constants.OTP_ACTION)){
                String message=intent.getStringExtra("message");
                String msg[]=message.split(" ");
                mValidationCodeEditText1.setText(msg[2].charAt(0)+"");
                mValidationCodeEditText2.setText(msg[2].charAt(1)+"");
                mValidationCodeEditText3.setText(msg[2].charAt(2)+"");
                mValidationCodeEditText4.setText(msg[2].charAt(3)+"");

                doneButtonClicked();
            }
            }

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_validation);
        getSupportActionBar().setTitle("Enter Pin");

        getIntentValues();
        registerViews();
        registerListeners();
        progressDialog = new ProgressDialog(PhoneNumberValidation.this);

//        SmsSentObserver smsSentObserver = new SmsSentObserver(new Handler(), PhoneNumberValidation.this);
//        getContentResolver().registerContentObserver(HomeActivity.STATUS_URI, true, smsSentObserver);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.validation_done_button:
                doneButtonClicked();
                break;
            default:
                break;
        }
    }

    private void getIntentValues() {
        String jsonString = getIntent().getStringExtra("OTPRequest");
        try {
            mOtpJson = new JSONObject(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter(Constants.OTP_ACTION));

    }

    /**
     * Register all the views
     */



    private void registerViews() {
        mValidationCodeEditText1 = (EditText) findViewById(R.id.validation_code_edittex_1);
        mValidationCodeEditText2 = (EditText) findViewById(R.id.validation_code_edittex_2);
        mValidationCodeEditText3 = (EditText) findViewById(R.id.validation_code_edittex_3);
        mValidationCodeEditText4 = (EditText) findViewById(R.id.validation_code_edittex_4);
        mDoneButton = (Button) findViewById(R.id.validation_done_button);
    }

    /**
     * Register listener on all the views
     */
    private void registerListeners() {
        mDoneButton.setOnClickListener(this);
        // Validation EditText 1 listener
        mValidationCodeEditText1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                String code = mValidationCodeEditText1.getText().toString();
                int length = code.length();
                if(length == 1) {
                    mValidationCodeEditText2.requestFocus();
                }
            }
        });
        // Validation EditText 2 listener
        mValidationCodeEditText2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                String code = mValidationCodeEditText2.getText().toString();
                int length = code.length();
                if(length == 1) {
                    mValidationCodeEditText3.requestFocus();
                }
            }
        });
        // Validation EditText 3 listener
        mValidationCodeEditText3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                String code = mValidationCodeEditText3.getText().toString();
                int length = code.length();
                if(length == 1) {
                    mValidationCodeEditText4.requestFocus();
                }
            }
        });
        // Validation EditText 4 listener
        mValidationCodeEditText4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String code = mValidationCodeEditText4.getText().toString();
                int length = code.length();
                if (length == 1) {
                    // All digits entered
                }
            }
        });
    }

    /**
     * Done button is clicked. Verify the code button entered
     */
    private void doneButtonClicked() {
        String otp = mValidationCodeEditText1.getText().toString().trim() +
                mValidationCodeEditText2.getText().toString().trim() +
                mValidationCodeEditText3.getText().toString().trim() +
                mValidationCodeEditText4.getText().toString().trim();

        if(otp == null || otp.length() < 4) {
            Utility.showToast(PhoneNumberValidation.this, "Please enter OTP");
            return;
        }

        showProgrss(Constants.PLEASE_WAIT);

        try {
            mOtpJson.put(Constants.OTP_KEY, otp);
            System.out.println("otp request: " + mOtpJson.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest loginRequest = new JsonObjectRequest(Request.Method.POST, Constants.OTP_RESPONSE_URL, mOtpJson, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                System.out.println("result: " + jsonObject.toString());
                try {
                    String success = jsonObject.getString(Constants.SUCCESS);
                    if(success.equalsIgnoreCase("1")) {
                        // Successfully logged in
                        // Save user
                        insertInDatabase(jsonObject);

                        Utility.callDesignApi(new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject jsonObject) {
                                hideProgrss();
                                System.out.println("Design downloaded: " + jsonObject.toString());
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

                                Utility.saveToSharedPrefs(PhoneNumberValidation.this, Constants.USER_LOGGED_IN, "true");
                                // Read data from local logs.txt file
                                BackupManager.readSavedData();
                                // Start scheduler
                                UtilityScheduler.startAllScheduler(PhoneNumberValidation.this);
                                UtilityScheduler.startSync(PhoneNumberValidation.this);
                                // Go to home screen
                                goToHomeScreen();

                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                hideProgrss();
                                Utility.showToast(PhoneNumberValidation.this, Constants.TRY_AGAIN);
                                volleyError.printStackTrace();
                            }
                        });
                    } else {
                        hideProgrss();
                        if(jsonObject.has(Constants.ERROR_MESSAGE)) {
                            String errorMessage = jsonObject.getString(Constants.ERROR_MESSAGE);
                            Utility.showToast(PhoneNumberValidation.this, errorMessage);
                        } else {
                            Utility.showToast(PhoneNumberValidation.this, Constants.UNKNOWN_ERROR_MESSAGE);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                hideProgrss();
                Utility.showToast(PhoneNumberValidation.this, Constants.TRY_AGAIN);
                volleyError.printStackTrace();
            }
        });
        TaskForceApplication.getInstance().addToRequestQueue(loginRequest);
    }

    /**
     * Insert user data in database
     * @param response
     */
    private void insertInDatabase(JSONObject response) {
        String branch = "";
        String username = "";
        try {
            JSONObject jsonObject = response.getJSONObject(Constants.USER_DATA);
            String sr = jsonObject.getString(DatabaseHelper.SR);
            String emp_code = jsonObject.getString(DatabaseHelper.EMP_CODE);
            username = jsonObject.getString(DatabaseHelper.USERNAME);
            String password = jsonObject.getString(DatabaseHelper.PASSWORD);
            String firstname = jsonObject.getString(DatabaseHelper.FIRSTNAME);
            String city = jsonObject.getString(DatabaseHelper.CITY);
            branch = jsonObject.getString(DatabaseHelper.BRANCH);
            String imei_no = jsonObject.getString(DatabaseHelper.IMEI_NO);
            String mobile_no = jsonObject.getString(DatabaseHelper.MOBILE_NO);
            String datetime = jsonObject.getString(DatabaseHelper.DATETIME);

            UserEntity user = new UserEntity();
            user.setsr(sr);
            user.setemp_code(emp_code);
            user.setusername(username);
            user.setpassword(password);
            user.setfirstname(firstname);
            user.setcity(city);
            user.setbranch(branch);
            user.setimei_no(imei_no);
            user.setmobile_no(mobile_no);
            user.setdatetime(datetime);

            UserHelper.getInstance().insertOrUpdate(user);

            Utility.saveToSharedPrefs(PhoneNumberValidation.this, Constants.EMP_CODE_KEY, emp_code.replace(Constants.EMP_CODE_SUFFIX, ""));

        } catch (JSONException je) {
            je.printStackTrace();
        }
        // Set Ezetaps username;
        String ezetapUsername = branch + "_" + Utility.getFromSharedPrefs(PhoneNumberValidation.this, Constants.EMP_CODE_KEY);
        Utility.saveToSharedPrefs(PhoneNumberValidation.this, Constants.EZETAP_USERNAME_KEY, ezetapUsername);
        // Save username in shared preferences
        Utility.saveToSharedPrefs(PhoneNumberValidation.this, Constants.USERNAME_KEY, username);
    }

    public void showProgrss(String message){
        if (progressDialog!=null && !progressDialog.isShowing()){
            progressDialog.show();
            progressDialog.setMessage(message);
            progressDialog.setCancelable(false);
        }
    }


    public void hideProgrss(){
        if (progressDialog!=null && progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }

    /**
     * Go to home screen
     */
    private void goToHomeScreen() {
        Utility.showToast(PhoneNumberValidation.this, "Success");
        Intent homeIntent = new Intent(PhoneNumberValidation.this, HomeActivity.class);
        startActivity(homeIntent);
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();

        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);

    }
}