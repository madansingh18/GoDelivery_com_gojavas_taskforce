package com.gojavas.taskforce.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.gojavas.taskforce.R;
import com.gojavas.taskforce.entity.DrsEntity;
import com.gojavas.taskforce.utils.Utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by MadanS on 10/23/2016.
 */
public class DRSDetailActivity extends AppCompatActivity implements View.OnClickListener {


    private Button mNonDelivered;
    private Button mAddMoreDRS;
    private Button mDelivered;
    private DrsEntity mDrsEntity;
    private TextView DOCKETNO;
    private TextView DRSNO;
    private TextView CSGENM;
    private TextView CSGEADDR;
    private TextView CSGETELENO;
    private TextView ALTERNATE_NUMBER;
    private TextView CSGECITY;
    private TextView CSGEPINCODE;
    private TextView PKGSNO;
    private TextView COD_DOD;
    private TextView COD_AMT;
    private TextView DRIVER_NAME;
    private TextView USERID;
    private TextView CLIENT_CODE;
    private TextView CLIENT_NAME;
    private TextView PRODCD;
    private TextView AMOUNT_TO_CUTOMER;
    private TextView ENTRYDATE;
    private TextView ENTRYBY;
    private TextView DATE;
    private TextView ADDRESS_TYPE;
    private TextView DRIVER_ID;
    private TextView CONTACT_PERSON;
    SharedPreferences sharedpreferences;
    public static String MyPREFERENCES = "" ;
    ArrayList<String> addedDRS = new ArrayList<String>();
    final Handler handler = new Handler();
    private Runnable runnableCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drs_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("DRS Detail");
        Intent i = getIntent();
        mDrsEntity = (DrsEntity)i.getSerializableExtra("selectedDrsEntities");
        if(mDrsEntity!=null){
            Log.i("TEST>", mDrsEntity.getdocketno());
        }
        registerViews();
        registerListeners();
//        MyPREFERENCES = "auto_save_"+mDrsEntity.getdocketno();
//        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
//        setRecoveryMode();
    }


    private void setRecoveryMode(){

        if(sharedpreferences!=null){
            String savedDrsData = sharedpreferences.getString("DRS_FORM_DATA", "");
            if(savedDrsData!=null&&savedDrsData.length()>0){
                LinearLayout linearLayout = (LinearLayout)findViewById(R.id.add_more_drs_layout);
                Toast.makeText(DRSDetailActivity.this,"Recovered From DRS Data",Toast.LENGTH_LONG).show();
                //Set the UI from recovery mode
                String replace = savedDrsData.replace("[","");
                String replace1 = replace.replace("]","");
                List<String> myList = new ArrayList<String>(Arrays.asList(replace1.split(",")));
                Log.i("MAD", myList.toString());
                if(myList!=null&&myList.size()>0){
                    for(int i=0;i<myList.size();i++){
                        EditText et = new EditText(this);
                        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        et.setLayoutParams(p);
                        et.setText(myList.get(i).toString());
                        linearLayout.addView(et);
                    }
                }
            }
            runnableCode = new Runnable() {
                @Override
                public void run() {
                    Log.d("Handlers", "Called on main thread");
                    autoSave();
                    handler.postDelayed(this, 5000);
                }
            };
            if(runnableCode!=null){
                handler.post(runnableCode);
            }
        }
    }


    @Override
    protected void onDestroy() {
        if(handler!=null&&runnableCode!=null){
            handler.removeCallbacks(runnableCode);
        }
        super.onDestroy();
    }

    /**
     * Register all the views
     */
    private void registerViews() {
        mDelivered = (Button) findViewById(R.id.delivered_button);
        mNonDelivered = (Button) findViewById(R.id.non_delivered_button);
        mAddMoreDRS= (Button) findViewById(R.id.add_more_drs);

        DOCKETNO = (TextView) findViewById(R.id.DOCKETNO);
        DOCKETNO.setText(mDrsEntity.getdocketno());
        DRSNO = (TextView) findViewById(R.id.DRSNO);
        DRSNO.setText(mDrsEntity.getdrsno());
        CSGENM = (TextView) findViewById(R.id.CSGENM);
        CSGENM.setText(mDrsEntity.getcsgenm());
        CSGEADDR = (TextView) findViewById(R.id.CSGEADDR);
        CSGEADDR.setText(mDrsEntity.getcsgeaddr());
        CSGETELENO = (TextView) findViewById(R.id.CSGETELENO);
        CSGETELENO.setText(mDrsEntity.getcsgeteleno());
        ALTERNATE_NUMBER = (TextView) findViewById(R.id.ALTERNATE_NUMBER);
        ALTERNATE_NUMBER.setText(mDrsEntity.getalternate_number());
        CSGECITY = (TextView) findViewById(R.id.CSGECITY);
        CSGECITY.setText(mDrsEntity.getcsgecity());
        CSGEPINCODE = (TextView) findViewById(R.id.CSGEPINCODE);
        CSGEPINCODE.setText(mDrsEntity.getcsgepincode());
        PKGSNO = (TextView) findViewById(R.id.PKGSNO);
        PKGSNO.setText("");
        COD_DOD = (TextView) findViewById(R.id.COD_DOD);
        COD_DOD.setText(mDrsEntity.getcod_dod());
        COD_AMT = (TextView) findViewById(R.id.COD_AMT);
        COD_AMT.setText(mDrsEntity.getcod_amt());
        DRIVER_NAME = (TextView) findViewById(R.id.DRIVER_NAME);
        DRIVER_NAME.setText(mDrsEntity.getdriver_name());
        USERID = (TextView) findViewById(R.id.USERID);
        USERID.setText(mDrsEntity.getuserid());
        CLIENT_CODE = (TextView) findViewById(R.id.CLIENT_CODE);
        CLIENT_CODE.setText(mDrsEntity.getclient_code());
        CLIENT_NAME = (TextView) findViewById(R.id.CLIENT_NAME);
        CLIENT_NAME.setText(mDrsEntity.getclient_name());
        PRODCD = (TextView) findViewById(R.id.PRODCD);
        PRODCD.setText(mDrsEntity.getprodcd());
        AMOUNT_TO_CUTOMER = (TextView) findViewById(R.id.AMOUNT_TO_CUTOMER);
        AMOUNT_TO_CUTOMER.setText(mDrsEntity.getamount_to_cutomer());
        ENTRYDATE = (TextView) findViewById(R.id.ENTRYDATE);
        ENTRYDATE.setText(mDrsEntity.getentrydate());
        ENTRYBY = (TextView) findViewById(R.id.ENTRYBY);
        ENTRYBY.setText(mDrsEntity.getentryby());
        DATE = (TextView) findViewById(R.id.DATE);
        DATE.setText(mDrsEntity.getdate());
        CONTACT_PERSON = (TextView) findViewById(R.id.CONTACT_PERSON);
        CONTACT_PERSON.setText(mDrsEntity.getcontact_person());
        ADDRESS_TYPE = (TextView) findViewById(R.id.ADDRESS_TYPE);
        ADDRESS_TYPE.setText(mDrsEntity.getaddress_type());
        DRIVER_ID = (TextView) findViewById(R.id.DATE);
        DRIVER_ID.setText(mDrsEntity.getdriver_id());



    }



    private void setTextView(TextView textView){
        textView.setText("TES");
    }


    /**
     * Register listeners on all the views
     */
    private void registerListeners() {
        mDelivered.setOnClickListener(this);
        mNonDelivered.setOnClickListener(this);
        mAddMoreDRS.setOnClickListener(this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==2){
            finish();
        }
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




    private void addMoreDRSData(){
        LinearLayout linearLayout = (LinearLayout)findViewById(R.id.add_more_drs_layout);
        EditText et = new EditText(this);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        et.setLayoutParams(p);
        linearLayout.addView(et);

    }



    private void autoSave(){
        addedDRS = new ArrayList<String>();
        LinearLayout linearLayout = (LinearLayout)findViewById(R.id.add_more_drs_layout);
        if(linearLayout!=null && linearLayout.getChildCount()>0){
            for(int i=0;i<linearLayout.getChildCount();i++){
                if(linearLayout.getChildAt(i)!=null){
                    if(linearLayout.getChildAt(i) instanceof EditText){
                        if(((EditText) linearLayout.getChildAt(i)).getText().toString().length()>0){
                            addedDRS.add(((EditText) linearLayout.getChildAt(i)).getText().toString());
                        }
                    }
                }
            }
        }

        if(addedDRS!=null&&addedDRS.size()>0){
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString("DRS_FORM_DATA", addedDRS.toString());
            editor.commit();
        }
        Log.i("MAD", addedDRS.toString());
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.non_delivered_button:
                performNonDeliveredOperation();
                break;
            case R.id.delivered_button:
                performDeliveredOperation();
                break;
            case R.id.add_more_drs:
                addMoreDRSData();
                break;
            default:
                break;
        }
    }

    private void performNonDeliveredOperation() {
        Intent intent = new Intent(DRSDetailActivity.this, DRSFailActivity.class);
        intent.putExtra("selectedDrsEntities", mDrsEntity);
//        startActivity(intent);
        startActivityForResult(intent, 2);

    }


    private void startPassActivity(){
        Intent intent = new Intent(DRSDetailActivity.this, DRSPassActivity.class);
        intent.putExtra("selectedDrsEntities", mDrsEntity);
        startActivityForResult(intent, 2);
    }


    private void codDialogBox(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(DRSDetailActivity.this);
        alertDialog.setTitle("COD Amount");
        alertDialog.setMessage("Received COD Amount");

        final EditText input = new EditText(DRSDetailActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        input.setLayoutParams(lp);
        input.setEnabled(false);
        input.setText(""+mDrsEntity.getcod_amt());
        alertDialog.setView(input);


        alertDialog.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String newCod = input.getText().toString();
                        if(newCod!= null&&newCod.trim().length()>0){
                            mDrsEntity.setAccepted_cod(newCod);
                            dialog.cancel();
                            startPassActivity();
                        }else{
                            Utility.showToast(DRSDetailActivity.this, "Enter valid COD value.");
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



    private void performDeliveredOperation() {
        //Check for the cod amount
        try{

//

//            mDrsEntity.setcod_amt("10.0");
//            mDrsEntity.setcod_dod("POSTPAID");
            if(mDrsEntity.getcod_dod()!=null&&mDrsEntity.getcod_dod().equalsIgnoreCase("POSTPAID")&&Float.valueOf(mDrsEntity.getcod_amt())!=0){
//            if(true){
                codDialogBox();
            }else{
                startPassActivity();
            }
        }catch(Exception e){
            startPassActivity();
        }
    }
}

