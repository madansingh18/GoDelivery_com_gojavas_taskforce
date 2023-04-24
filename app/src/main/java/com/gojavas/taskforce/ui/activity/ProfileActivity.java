package com.gojavas.taskforce.ui.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.gojavas.taskforce.R;
import com.gojavas.taskforce.utils.Utility;

/**
 * Created by gjs331 on 5/18/2015.
 */
public class ProfileActivity extends AppCompatActivity{

    TextView tv_name,tv_mobile,tv_empid, text_user_name, text_boy_id,text_user_location;
    Button btn_resetPassword;
    String str_database_current_password,str_empid,str_username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("Profile Details");
        text_user_name =(TextView)findViewById(R.id.text_user_name);
        text_boy_id = (TextView)findViewById(R.id.text_boy_id);
        text_user_location = (TextView)findViewById(R.id.text_user_location);
        String boyId = Utility.getFromSharedPrefs(ProfileActivity.this, "BOY_ID");
        String boyName = Utility.getFromSharedPrefs(ProfileActivity.this, "BOY_NAME");
        String boyLocation = Utility.getFromSharedPrefs(ProfileActivity.this, "HUB_NAME");
        if(boyId!=null&&boyId.length()>0)
            text_boy_id.setText(boyId);

        if(boyName!=null&&boyName.length()>0)
            text_user_name.setText(boyName);


        if(boyLocation!=null&&boyLocation.length()>0)
            text_user_location.setText(boyLocation);
        /*tv_name=(TextView)findViewById(R.id.text_name);
        tv_mobile=(TextView)findViewById(R.id.text_mobile);
        tv_empid=(TextView)findViewById(R.id.text_empid);

        btn_resetPassword=(Button)findViewById(R.id.button_reset);

        ArrayList<UserEntity> arrayList=UserHelper.getInstance().getUserDetail();

        if(arrayList.size() > 0) {
            tv_name.setText(arrayList.get(0).getfirstname());
            tv_mobile.setText(arrayList.get(0).getmobile_no());
            str_database_current_password=arrayList.get(0).getpassword();
            str_empid=arrayList.get(0).getemp_code();
           tv_empid.setText(str_empid);
            str_username=arrayList.get(0).getusername();
        }

        btn_resetPassword.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

// Create custom dialog object
                final Dialog dialog = new Dialog(ProfileActivity.this);
                // Include dialog.xml file
                dialog.setContentView(R.layout.dialog_reset);
                // Set dialog title
                dialog.setTitle("Reset Password");

                // set values for custom dialog components - text, image and button
                final EditText et_currentPassword = (EditText) dialog.findViewById(R.id.text_currentPassword);
                final EditText et_newPassword = (EditText) dialog.findViewById(R.id.text_newpassword);
                final EditText et_confirmPassword = (EditText) dialog.findViewById(R.id.text_confirmpassword);
                Button btn_resetPassword=(Button)dialog.findViewById(R.id.dialog_reset);

                dialog.show();

                btn_resetPassword.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String str_newpassword=et_newPassword.getText().toString().trim();
                        String str_confirmpssword=et_confirmPassword.getText().toString().trim();
                        String str_currentPassword=et_currentPassword.getText().toString().trim();

                        if (str_confirmpssword.isEmpty() || str_newpassword.isEmpty() || str_currentPassword.isEmpty() ){
                            Utility.showToast(ProfileActivity.this, "Please Enter All Fields");
                        }else
                        if (!str_database_current_password.equals(str_currentPassword)){
                            Utility.showToast(ProfileActivity.this, "Current Password is Wrong");
                        }
                        else if (!str_newpassword.equals(str_confirmpssword)){
                            Utility.showToast(ProfileActivity.this, "New and Confirm Password do not match  ");
                        }else {

                            try {
                                JSONObject jsonObject=new JSONObject();
                                jsonObject.put("username",str_username);
                                jsonObject.put("password",str_currentPassword);

                                jsonObject.put("newpassword", str_newpassword);

                                makeResetPasswordRequest(jsonObject, str_username, str_newpassword);
                                dialog.dismiss();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }

                    }
                });*/

//            }
//        });

    }



    /*void makeResetPasswordRequest(JSONObject requestJson, final String userName, final String newPassword){
        JsonObjectRequest pullRequest = new JsonObjectRequest(Request.Method.POST, Constants.RESET_PASSWORD_URL, requestJson, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                System.out.println("response reset password: " + jsonObject.toString());

                UserHelper.getInstance().updateUserPassword(userName, newPassword);
                str_database_current_password=newPassword;

                Utility.showToast(ProfileActivity.this,"Done");
                // Successfully pulled

                // Enable sequence, start and sync layout

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();

                Utility.showToast(ProfileActivity.this, "Failed");
            }
        });
        TaskForceApplication.getInstance().addToRequestQueue(pullRequest);

    }*/


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
