package com.gojavas.taskforce.ui.activity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gojavas.taskforce.R;
import com.gojavas.taskforce.adapter.StataticsAdapter;
import com.gojavas.taskforce.database.CallLogHelper;
import com.gojavas.taskforce.entity.StataticsEntity;
import com.gojavas.taskforce.utils.Constants;
import com.gojavas.taskforce.utils.Utility;

import java.util.ArrayList;

/**
 * Created by gjs331 on 5/15/2015.
 */
public class StataticsAcitivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView mVersionTextView;
    ArrayList<StataticsEntity> entityArrayList;
    StataticsEntity stataticsEntity;
    String version_App="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_statatics);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Statistics");

        recyclerView=(RecyclerView)findViewById(R.id.recylerview_list);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(StataticsAcitivity.this);
        recyclerView.setLayoutManager(linearLayoutManager);

        entityArrayList=new ArrayList<>();

        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            version_App = pInfo.versionName;
//            checkApiVersion();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        mVersionTextView = (TextView) findViewById(R.id.version_textview);
//        mVersionTextView.setText("Version "+version_App);

        mVersionTextView.setText("Version 2.1.1");


        stataticsEntity =new StataticsEntity();
        stataticsEntity.setTitle("Login Time");
        stataticsEntity.setValue(Utility.getDateFromMilliseconds(Long.parseLong(Utility.getFromSharedPrefs(StataticsAcitivity.this, Constants.LOGGED_IN_TIME))));
        entityArrayList.add(stataticsEntity);


        stataticsEntity =new StataticsEntity();
        int pe_cl=CallLogHelper.getInstance().getNumberOfCallandSms(Constants.PERSONAL, Constants.CALL);
        int pe_cl_duration=CallLogHelper.getInstance().getDurationSum(Constants.PERSONAL, Constants.CALL);
        stataticsEntity.setTitle("Outgoing Personal Calls (" + pe_cl + ")");
        stataticsEntity.setValue(secondsToMin_Sec(pe_cl_duration));
        entityArrayList.add(stataticsEntity);


        stataticsEntity=new StataticsEntity();
        int pro_cl=CallLogHelper.getInstance().getNumberOfCallandSms(Constants.PROFESSIONAL, Constants.CALL);
        int pro_cl_duration=CallLogHelper.getInstance().getDurationSum(Constants.PROFESSIONAL, Constants.CALL);
        stataticsEntity.setTitle("Outgoing Professional Calls (" + pro_cl +")");
        stataticsEntity.setValue(secondsToMin_Sec(pro_cl_duration));
        entityArrayList.add(stataticsEntity);

        stataticsEntity=new StataticsEntity();
        int pro_sms=CallLogHelper.getInstance().getNumberOfCallandSms(Constants.PROFESSIONAL, Constants.SMS);
        stataticsEntity.setTitle("Outgoing Professional SMS");
        stataticsEntity.setValue(pro_sms + "");
        entityArrayList.add(stataticsEntity);

        stataticsEntity=new StataticsEntity();
        int pe_sms=CallLogHelper.getInstance().getNumberOfCallandSms(Constants.PERSONAL, Constants.SMS);
        stataticsEntity.setTitle("Outgoing Personal SMS");
        stataticsEntity.setValue(pe_sms + "");
        entityArrayList.add(stataticsEntity);


        StataticsAdapter adapter=new StataticsAdapter(entityArrayList);
        recyclerView.setAdapter(adapter);

    }


    private  String secondsToMin_Sec(int seconds){

        String sb="";

        int minute=seconds/60;
        int sec=seconds%60;
        sb=(minute +" min "+sec+ "sec");
        return sb;
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
