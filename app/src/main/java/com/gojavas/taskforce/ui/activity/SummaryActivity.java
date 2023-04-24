package com.gojavas.taskforce.ui.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.gojavas.taskforce.R;
import com.gojavas.taskforce.adapter.SummaryAdapter;
import com.gojavas.taskforce.database.DrsHelper;
import com.gojavas.taskforce.manager.DesignManager;
import com.gojavas.taskforce.utils.Constants;
import com.gojavas.taskforce.utils.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by gjs331 on 6/2/2015.
 */
public class SummaryActivity extends AppCompatActivity {

    ArrayList<HashMap<String,String >> arrayList;
    RecyclerView recyclerView_list;
    ProgressDialog progressDialog;

    SummaryAdapter summaryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_summary);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Summary");

        recyclerView_list=(RecyclerView)findViewById(R.id.reclerview_summary);

        recyclerView_list.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(SummaryActivity.this);
        recyclerView_list.setLayoutManager(linearLayoutManager);
        arrayList=new ArrayList<>();

        progressDialog = new ProgressDialog(SummaryActivity.this);

        callCashCollectionAPI();

        summaryAdapter = new SummaryAdapter(arrayList);
        recyclerView_list.setAdapter(summaryAdapter);
    }

    private float parse(float val){
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        return Float.valueOf(twoDForm.format(val));
    }

    /**
     * Call cash collection API
     */
    private void callCashCollectionAPI() {
        showProgress("Calculating cash collection...");

        JSONObject requestJson = new JSONObject();
        String driverId = Utility.getFromSharedPrefs(this, Constants.EMP_CODE_KEY);
        try {
            requestJson.put("driver_id", driverId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println("cash request: " + requestJson.toString());
        JsonObjectRequest cashCollectionRequest = new JsonObjectRequest(Request.Method.POST, Constants.CASH_COLLECTION_URL, requestJson, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                System.out.println("cash result: " + jsonObject.toString());

                // Get cash summary
                getCashSummary(jsonObject);

                // Get all the dockets summary
                getDocketSummary();

                // Notify adapter
                summaryAdapter.notifyDataSetChanged();

                hideProgress();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                hideProgress();
                // Get all the dockets summary
                getDocketSummary();
                // Notify adapter
                summaryAdapter.notifyDataSetChanged();
                Utility.showToast(SummaryActivity.this, "Please try again");
                volleyError.printStackTrace();
            }
        });
        TaskForceApplication.getInstance().addToRequestQueue(cashCollectionRequest);
    }

//    /**
//     * Insert amounts
//     */
//    private void insertAmounts() {
//        String savedCODAmount = Utility.getFromSharedPrefs(SummaryActivity.this, Constants.AMOUNT_COD);
//        String savedSODAmount = Utility.getFromSharedPrefs(SummaryActivity.this, Constants.AMOUNT_SOD);
//        String savedCODPRAmount = Utility.getFromSharedPrefs(SummaryActivity.this, Constants.AMOUNT_COD_PR);
//        String savedSODPRAmount = Utility.getFromSharedPrefs(SummaryActivity.this, Constants.AMOUNT_SOD_PR);
//        if(savedCODAmount.equalsIgnoreCase("")) {
//            savedCODAmount = "0";
//        }
//        if(savedSODAmount.equalsIgnoreCase("")) {
//            savedSODAmount = "0";
//        }
//        if(savedCODPRAmount.equalsIgnoreCase("")) {
//            savedCODPRAmount = "0";
//        }
//        if(savedSODPRAmount.equalsIgnoreCase("")) {
//            savedSODPRAmount = "0";
//        }
//        Float totalAmount = parse(Float.parseFloat(savedCODAmount) + Float.parseFloat(savedSODAmount) +
//                        Float.parseFloat(savedCODPRAmount) + Float.parseFloat(savedSODPRAmount));
//
//        HashMap<String,String> mapCOD = new HashMap<>();
//        mapCOD.put("jobtype", "COD Amount");
//        mapCOD.put("amount", savedCODAmount);
//
//        HashMap<String,String> mapSOD = new HashMap<>();
//        mapSOD.put("jobtype", "SOD Amount");
//        mapSOD.put("amount", savedSODAmount);
//
//        HashMap<String,String> mapCODPR = new HashMap<>();
//        mapCODPR.put("jobtype", "PR COD Amount");
//        mapCODPR.put("amount", savedCODPRAmount);
//
//        HashMap<String,String> mapSODPR = new HashMap<>();
//        mapSODPR.put("jobtype", "PR SOD Amount");
//        mapSODPR.put("amount", savedSODPRAmount);
//
//        HashMap<String,String> mapTotal = new HashMap<>();
//        mapTotal.put("jobtype", "Total Amount");
//        mapTotal.put("amount", totalAmount + "");
//
//        arrayList.add(mapCOD);
//        arrayList.add(mapSOD);
//        arrayList.add(mapCODPR);
//        arrayList.add(mapSODPR);
//        arrayList.add(mapTotal);
//    }

    /**
     * Get cash summary of the FE
     */
    private void getCashSummary(JSONObject jsonObject) {
        String codAmount = "0";
        String sodAmount = "0";
        String prCodAmount = "0";
        String prSODAmount = "0";
        try {
            if(jsonObject.has("Cash")) {
                codAmount = jsonObject.getString("Cash");
            }
            if(jsonObject.has("SOD")) {
                sodAmount = jsonObject.getString("SOD");
            }
            if(jsonObject.has("Pr Cash")) {
                prCodAmount = jsonObject.getString("Pr Cash");
            }
            if(jsonObject.has("Pr SOD")) {
                prSODAmount = jsonObject.getString("Pr SOD");
            }

            Float totalAmount = parse(Float.parseFloat(codAmount) + Float.parseFloat(sodAmount) +
                    Float.parseFloat(prCodAmount) + Float.parseFloat(prSODAmount));

            HashMap<String,String> mapCOD = new HashMap<>();
            mapCOD.put("jobtype", "Cash Amount");
            mapCOD.put("amount", codAmount);

            HashMap<String,String> mapSOD = new HashMap<>();
            mapSOD.put("jobtype", "SOD Amount");
            mapSOD.put("amount", sodAmount);

            HashMap<String,String> mapCODPR = new HashMap<>();
            mapCODPR.put("jobtype", "PR Cash Amount");
            mapCODPR.put("amount", prCodAmount);

            HashMap<String,String> mapSODPR = new HashMap<>();
            mapSODPR.put("jobtype", "PR SOD Amount");
            mapSODPR.put("amount", prSODAmount);

            HashMap<String,String> mapTotal = new HashMap<>();
            mapTotal.put("jobtype", "Total Amount");
            mapTotal.put("amount", totalAmount + "");

            arrayList.add(mapCOD);
            arrayList.add(mapSOD);
            arrayList.add(mapCODPR);
            arrayList.add(mapSODPR);
            arrayList.add(mapTotal);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get all the dockets summary
     */
    private void getDocketSummary() {
        JSONObject designJson = DesignManager.getInstance().getDesignJson();
        try {
            JSONArray jobJson = designJson.getJSONArray(Constants.JOB_TYPE);
            for (int i=0; i<jobJson.length();i++){
                HashMap<String,String> map=new HashMap<>();
                String jobtype = jobJson.getString(i);

                map.put("jobtype", jobtype);
                map.put("successcount", DrsHelper.getInstance().getSuccessDrsCountJobType(jobtype)+"");
                map.put("failedcount", DrsHelper.getInstance().getFailedDrsCountJobType(jobtype) + "");
                map.put("pendingcount", DrsHelper.getInstance().getPendingDrsCountJobType(jobtype) + "");

                arrayList.add(map);
            }
        } catch (Exception e) {
            e.printStackTrace();
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

    /**
     * Show progress dialog
     * @param message
     */
    public void showProgress(String message){
        if (progressDialog != null && !progressDialog.isShowing()){
            progressDialog.show();
            progressDialog.setMessage(message);
        }
    }

    /**
     * Hide progress dialog
     */
    public void hideProgress(){
        if (progressDialog!=null && progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }
}