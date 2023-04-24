package com.gojavas.taskforce.services;

import android.app.IntentService;
import android.content.Intent;
import android.text.TextUtils;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.gojavas.taskforce.database.DatabaseHelper;
import com.gojavas.taskforce.database.DeliveryHelper;
import com.gojavas.taskforce.database.DrsHelper;
import com.gojavas.taskforce.database.ItemHelper;
import com.gojavas.taskforce.entity.DeliveryEntity;
import com.gojavas.taskforce.entity.DrsEntity;
import com.gojavas.taskforce.entity.ItemEntity;
import com.gojavas.taskforce.ui.activity.TaskForceApplication;
import com.gojavas.taskforce.utils.Constants;
import com.gojavas.taskforce.utils.Utility;
import com.gojavas.taskforce.utils.UtilityScheduler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by gjs331 on 5/13/2015.
 */
public class PullSchedulerService extends IntentService {

   public PullSchedulerService(){
       super("SchedularSync");

   }
    public PullSchedulerService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        callPullApi();
    }

    /**
     * Call api to pull data from server
     */
    private void callPullApi() {
        JSONObject requestJson = createPullRequestJson();

        System.out.println("request json: " + requestJson);

        final JsonObjectRequest pullRequest = new JsonObjectRequest(Request.Method.POST, Constants.PULL_URL, requestJson, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                System.out.println("result: " + jsonObject.toString());
                // Successfully pulled
                insertInDatabase(jsonObject);
                // Enable sequence, start and sync layout
                String message = "";
                if(jsonObject.has(Constants.ERROR_MESSAGE)) {
                    try {
                        message = jsonObject.getString(Constants.ERROR_MESSAGE);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                sendMessage(Constants.SUCCESS_RESPONSE, message);

                // Start GPS scheduler
                UtilityScheduler.startGpsScheduler(TaskForceApplication.getInstance());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                sendMessage(Constants.FALIURE_RESPONSE, Constants.TRY_AGAIN);

                // Start GPS scheduler
                UtilityScheduler.startGpsScheduler(TaskForceApplication.getInstance());
            }
        });
        TaskForceApplication.getInstance().addToRequestQueue(pullRequest);

        String contentType = pullRequest.getBodyContentType();
        System.out.println("content type: " + contentType);
        try {
            Map<String, String> headers = pullRequest.getHeaders();

        } catch (AuthFailureError authFailureError) {
            authFailureError.printStackTrace();
        }
    }

    /**
     * create json request for pull data api
     * @return
     */
    private JSONObject createPullRequestJson() {
        JSONObject finalJson = new JSONObject();
        String driverId = Utility.getFromSharedPrefs(this, Constants.EMP_CODE_KEY);
        try {
            finalJson.put("driver_id", driverId);
            finalJson.put("creation_datetime", Utility.getCurrentPullDate());
        } catch (JSONException je) {
            je.printStackTrace();
        }
        return finalJson;
    }

    /**
     * Insert drs data in database
     * @param response
     */
    private void insertInDatabase(JSONObject response) {
        ArrayList<String> docketList = new ArrayList<>();
        JSONArray receivedDocketsJsonArray = new JSONArray();
        try {
            JSONArray pullDataArray = response.getJSONArray(Constants.PULL_DATA);
            int length = pullDataArray.length();
            for(int i=0; i<length; i++){

                JSONObject jsonObject = pullDataArray.getJSONObject(i);
//                String sr = jsonObject.getString(DatabaseHelper.SR);
                String jobtype = jsonObject.getString(DatabaseHelper.JOBTYPE);
                String docketno = jsonObject.getString(DatabaseHelper.DOCKETNO);
                String exchange_requestid = "";
                if(jsonObject.has(DatabaseHelper.EXCHANGE_REQUESTID)) {
                    exchange_requestid = jsonObject.getString(DatabaseHelper.EXCHANGE_REQUESTID);
                }
                String drsno = jsonObject.getString(DatabaseHelper.DRSNO);
                // Skip empty docket
                if(docketno == null || docketno.isEmpty() || drsno == null || drsno.isEmpty()) {
                    continue;
                }
                String drs_docket = drsno + "_" + docketno;
                String pieces = jsonObject.getString(DatabaseHelper.PIECES);
                String csgenm = jsonObject.getString(DatabaseHelper.CSGENM);
                String csgeaddr = jsonObject.getString(DatabaseHelper.CSGEADDR);
                String address_type = jsonObject.getString(DatabaseHelper.ADDRESS_TYPE);
                String pickup_location = jsonObject.getString(DatabaseHelper.PICKUP_LOCATION);
//                String timetoend = jsonObject.getString(DatabaseHelper.TIMETOEND);
                String csgeteleno = jsonObject.getString(DatabaseHelper.CSGETELENO);
                String alternate_number = jsonObject.getString(DatabaseHelper.ALTERNATE_NUMBER);
//                String ctr_no = jsonObject.getString(DatabaseHelper.CTR_NO);
                String csgecity = jsonObject.getString(DatabaseHelper.CSGECITY);
//                String reassign_destcd = jsonObject.getString(DatabaseHelper.REASSIGN_DESTCD);
                String csgepincode = jsonObject.getString(DatabaseHelper.CSGEPINCODE);
                String pkgsno = jsonObject.getString(DatabaseHelper.PKGSNO);
                String cod_dod = jsonObject.getString(DatabaseHelper.COD_DOD);
                String cod_amt = jsonObject.getString(DatabaseHelper.COD_AMT);
//                String delivered = jsonObject.getString(DatabaseHelper.DELIVERED);
//                String drsupdated = jsonObject.getString(DatabaseHelper.DRSUPDATED);
//                String logistic_dt = jsonObject.getString(DatabaseHelper.LOGISTIC_DT);
//                String logistic_time = jsonObject.getString(DatabaseHelper.LOGISTIC_TIME);
//                String start_km = jsonObject.getString(DatabaseHelper.START_KM);
                String total_dockets_in_drs = jsonObject.getString(DatabaseHelper.TOTAL_DOCKETS_IN_DRS);
//                String dkt_count_not_updated = jsonObject.getString(DatabaseHelper.DKT_COUNT_NOT_UPDATED);
                String driver_name = jsonObject.getString(DatabaseHelper.DRIVER_NAME);
                String driver_id = jsonObject.getString(DatabaseHelper.DRIVER_ID);
                String actuwt = jsonObject.getString(DatabaseHelper.ACTUWT);
//                String userid = jsonObject.getString(DatabaseHelper.USERID);
                String client_code = jsonObject.getString(DatabaseHelper.CLIENT_CODE);
                String client_name = jsonObject.getString(DatabaseHelper.CLIENT_NAME);
//                String nextattemptdate = jsonObject.getString(DatabaseHelper.NEXTATTEMPTDATE);
//                String prodcd = jsonObject.getString(DatabaseHelper.PRODCD);
//                String codedesc = jsonObject.getString(DatabaseHelper.CODEDESC);
                String amount_to_cutomer = jsonObject.getString(DatabaseHelper.AMOUNT_TO_CUTOMER);
//                String entrydate = jsonObject.getString(DatabaseHelper.ENTRYDATE);
//                String entryby = jsonObject.getString(DatabaseHelper.ENTRYBY);
//                String lasteditdate = jsonObject.getString(DatabaseHelper.LASTEDITDATE);
//                String lasteditby = jsonObject.getString(DatabaseHelper.LASTEDITBY);
                String status = jsonObject.getString(DatabaseHelper.STATUS);
//                String responsexml = jsonObject.getString(DatabaseHelper.RESPONSEXML);
//                String failreason = jsonObject.getString(DatabaseHelper.FAILREASON);
//                String response_datetime = jsonObject.getString(DatabaseHelper.RESPONSE_DATETIME);
//                String slotno = jsonObject.getString(DatabaseHelper.SLOTNO);
//                String latitude = jsonObject.getString(DatabaseHelper.LATITUDE);
//                String longitude = jsonObject.getString(DatabaseHelper.LONGITUDE);
                String ordernumber = jsonObject.getString(DatabaseHelper.ORDERNUMBER);
                String choiceofpayment = jsonObject.getString(DatabaseHelper.CHOICEOFPAYMENT);
                String date = jsonObject.getString(DatabaseHelper.DATE);
                String olddocketno = jsonObject.getString(DatabaseHelper.OLDDOCKETNO);
//                String sellername = jsonObject.getString(DatabaseHelper.SELLERNAME);
//                String contact_person = jsonObject.getString(DatabaseHelper.CONTACT_PERSON);
                String attempt = jsonObject.getString(DatabaseHelper.ATTEMPT);
                String mobile_pull_status = jsonObject.getString(DatabaseHelper.MOBILE_PULL_STATUS);

                DrsEntity drs = new DrsEntity();
                drs.setsr("");
                drs.setjobtype(jobtype);
                drs.setdocketno(docketno);
                drs.setexchange_requestid(exchange_requestid);
                drs.setdrsno(drsno);
                drs.setdrs_docket(drs_docket);
                drs.setpieces(pieces);
                drs.setcsgenm(csgenm);
                drs.setcsgeaddr(csgeaddr);
                drs.setaddress_type(address_type);
                drs.setpickup_location(pickup_location);
                drs.settimetoend("");
                drs.setcsgeteleno(csgeteleno);
                drs.setalternate_number(alternate_number);
                drs.setctr_no("");
                drs.setcsgecity(csgecity);
                drs.setreassign_destcd("");
                drs.setcsgepincode(csgepincode);
                drs.setpkgsno(pkgsno);
                drs.setcod_dod(cod_dod);
                drs.setcod_amt(cod_amt);
                drs.setdelivered("");
                drs.setdrsupdated("");
                drs.setlogistic_dt("");
                drs.setlogistic_time("");
                drs.setstart_km("");
                drs.settotal_dockets_in_drs(total_dockets_in_drs);
                drs.setdkt_count_not_updated("");
                drs.setdriver_name(driver_name);
                drs.setdriver_id(driver_id);
                drs.setactuwt(actuwt);
                drs.setuserid("");
                drs.setclient_code(client_code);
                drs.setclient_name(client_name);
                drs.setnextattemptdate("");
                drs.setprodcd("");
                drs.setcodedesc("");
                drs.setamount_to_cutomer(amount_to_cutomer);
                drs.setentrydate("");
                drs.setentryby("");
                drs.setlasteditdate("");
                drs.setlasteditby("");
                drs.setstatus(status);
                drs.setresponsexml("");
                drs.setfailreason("");
                drs.setresponse_datetime("");
                drs.setslotno("");
                drs.setlatitude("");
                drs.setlongitude("");
                drs.setordernumber(ordernumber);
                drs.setchoiceofpayment(choiceofpayment);
                drs.setdate(date);
                drs.setolddocketno(olddocketno);
                drs.setsellername("");
                drs.setcontact_person("");
                drs.setattempt(attempt);
                drs.setmobile_pull_status(mobile_pull_status);
                drs.setproduct_description("");
                drs.setreason_for_return("");
                drs.setreturn_request_id("");
                drs.settp_code("");
                drs.setreturn_request_id("");

                DrsHelper.getInstance().insertOrUpdate(drs);

                // Insert this dockets items in database
                if(jsonObject.has(Constants.ITEM)) {
                    JSONArray pullDataItem = jsonObject.getJSONArray(Constants.ITEM);
                    int length1 = pullDataItem.length();
                    // Check whether all the items of this docket are pulled or not
                    if(!ItemHelper.getInstance().allItemsPulled(drs_docket, pieces)) {
                        // All the items are not pulled
                        for(int j=0; j<length1; j++) {
                            JSONObject jsonObjectItem = pullDataItem.getJSONObject(j);

                            String srItem = jsonObjectItem.getString(DatabaseHelper.SR);
                            String drsnoItem = jsonObjectItem.getString(DatabaseHelper.DRS_NO);
                            String docketnoItem = jsonObjectItem.getString(DatabaseHelper.DOCKET_NO);
                            String drs_docketItem = drsnoItem + "_" + docketnoItem;
                            String skuidItem = jsonObjectItem.getString(DatabaseHelper.SKU_ID);
                            String skudescriptionItem = jsonObjectItem.getString(DatabaseHelper.SKU_DESCRIPTION);
                            String statusItem = jsonObjectItem.getString(DatabaseHelper.STATUS);
                            if(statusItem == null || statusItem.equals("") || TextUtils.isEmpty(statusItem)) {
                                statusItem = "0";
                            }
                            String skucost = jsonObjectItem.getString(DatabaseHelper.SKU_COST);
                            String quantity = jsonObjectItem.getString(DatabaseHelper.QUANTITY);

//                        if(ItemHelper.getInstance().itemExist(drs_docketItem, skuidItem)) {
//                            continue;
//                        }

                            ItemEntity itemEntity=new ItemEntity();
                            itemEntity.setsku_id(skuidItem);
                            itemEntity.setsku_description(skudescriptionItem);
                            itemEntity.setStatus(statusItem);
                            itemEntity.setsku_cost(skucost);
                            itemEntity.setdrs_no(drsnoItem);
                            itemEntity.setquantity(quantity);
                            itemEntity.setsr(srItem);
                            itemEntity.setdocket_no(docketnoItem);
                            itemEntity.setdrs_docket(drs_docket);

                            ItemHelper.getInstance().insertOrUpdate(itemEntity);
                        }
                    }
                }

                // Check whether Mobile pull time is sent to the server or not
                if(Integer.parseInt(mobile_pull_status) == 0) {
                    // Mobile pull time not sent to the server
                    // Create received docket jsonobject
                    JSONObject receivedDocketJson = new JSONObject();
                    receivedDocketJson.put(DatabaseHelper.DOCKETNO, docketno);
                    receivedDocketJson.put(DatabaseHelper.DRSNO, drsno);
                    receivedDocketsJsonArray.put(receivedDocketJson);
                }

                // Issue - Docket is sent to server, user logs out, user logs in, its showing as pending
                // Fix - Getting Status as "Y" or "N" from server
                // Issue - Docket is not sent to server, docket is completed, user logs out, user logs in, its showing as pending because even on server Status is not changed to "Y" or "N"
                // Fix - Saving data in logs.txt file and fetching from there
                if(DeliveryHelper.getInstance().deliveryExist(drs_docket)) {
                    // Docket is in delivery table
                    // Check its status in new table
                    // If status is Y or N, update its sync to 1
                    if (status.equalsIgnoreCase("Y") || status.equalsIgnoreCase("N") ) {
                        DeliveryEntity delivery = DeliveryHelper.getInstance().getDeliveryDetail(drs_docket);
                        String sync = delivery.getsync();
                        if(sync.equals("0")) {
                            docketList.add(delivery.getdocket_number());
                        }
                        DeliveryHelper.getInstance().updateDeliverySync(drs_docket);
                    }
                } else {
                    // Docket is not in delivery table
                    // Check its status in new table
                    // If status is Y or N, insert it in Delivery table
                    if (status.equalsIgnoreCase("Y") || status.equalsIgnoreCase("N") ) {

                        DeliveryEntity mDeliveryEntity = new DeliveryEntity();

                        mDeliveryEntity.setjobtype(drs.getjobtype());
                        mDeliveryEntity.setcust_name(drs.getcsgenm());
                        mDeliveryEntity.setcust_address1(drs.getcsgeaddr());
                        mDeliveryEntity.setcust_address2(drs.getcsgeaddr());
                        mDeliveryEntity.setpincode(drs.getcsgepincode());
                        mDeliveryEntity.setcity(drs.getcsgecity());
                        mDeliveryEntity.setcust_contact1(drs.getcsgeteleno());
                        mDeliveryEntity.setalternate_no(drs.getalternate_number());
                        mDeliveryEntity.setorderNumber(drs.getordernumber());
                        mDeliveryEntity.setpackagesNo(drs.getpkgsno());
                        mDeliveryEntity.setDRSNumber(drs.getdrsno());
                        mDeliveryEntity.setTotalDocketsInDRS(drs.gettotal_dockets_in_drs());
                        mDeliveryEntity.setdocket_number(drs.getdocketno());
                        mDeliveryEntity.setchoice_of_payment(drs.getcod_dod());
                        mDeliveryEntity.setamount_tobe_paid(drs.getcod_amt());
                        mDeliveryEntity.setboy_id(drs.getdriver_id());
                        mDeliveryEntity.setboy_name(drs.getdriver_name());
                        mDeliveryEntity.setclosing_km(drs.getstart_km());
                        mDeliveryEntity.setdelivery_time(Utility.getDeliveryTime());
                        mDeliveryEntity.setimei_number(Utility.getDeviceId());
                        mDeliveryEntity.setstatus((status.equalsIgnoreCase("Y")) ? "1" : "0");
                        mDeliveryEntity.setattempt_number(attempt);
                        mDeliveryEntity.setdrs_docket(drs_docket);
                        mDeliveryEntity.setsync("1");

                        DeliveryHelper.getInstance().insertOrUpdate(mDeliveryEntity);
                    }
                }
            }

//            if(response.has(Constants.ITEM)) {
//                JSONArray pullDataItem = response.getJSONArray(Constants.ITEM);
//                int length1 = pullDataItem.length();
//                for(int j=0; j<length1; j++) {
//                    JSONObject jsonObjectItem = pullDataItem.getJSONObject(j);
//
//                    String srItem = jsonObjectItem.getString(DatabaseHelper.SR);
//                    String drsnoItem = jsonObjectItem.getString(DatabaseHelper.DRS_NO);
//                    String docketnoItem = jsonObjectItem.getString(DatabaseHelper.DOCKET_NO);
//                    String drs_docketItem = drsnoItem + "_" + docketnoItem;
//                    String skuidItem = jsonObjectItem.getString(DatabaseHelper.SKU_ID);
//                    String skudescriptionItem = jsonObjectItem.getString(DatabaseHelper.SKU_DESCRIPTION);
//                    String statusItem = jsonObjectItem.getString(DatabaseHelper.STATUS);
//                    if(statusItem == null || statusItem.equals("") || TextUtils.isEmpty(statusItem)) {
//                        statusItem = "0";
//                    }
//                    String skucost = jsonObjectItem.getString(DatabaseHelper.SKU_COST);
//                    String quantity = jsonObjectItem.getString(DatabaseHelper.QUANTITY);
//
//                    if(ItemHelper.getInstance().itemExist(drs_docketItem, skuidItem)) {
//                        continue;
//                    }
//
//                    ItemEntity itemEntity=new ItemEntity();
//                    itemEntity.setsku_id(skuidItem);
//                    itemEntity.setsku_description(skudescriptionItem);
//                    itemEntity.setStatus(statusItem);
//                    itemEntity.setsku_cost(skucost);
//                    itemEntity.setdrs_no(drsnoItem);
//                    itemEntity.setquantity(quantity);
//                    itemEntity.setsr(srItem);
//                    itemEntity.setdocket_no(docketnoItem);
//                    itemEntity.setdrs_docket(drs_docketItem);
//
//                    ItemHelper.getInstance().insertOrUpdate(itemEntity);
//                }
//            }

            // Upload images of un-synced dockets
            int size = docketList.size();
            for(int i=0; i<size; i++) {
                UtilityScheduler.uploadImages(PullSchedulerService.this, docketList.get(i));
            }

            // Acknowledge server about mobile pull time
            callMobilePullApi(receivedDocketsJsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Send mobile pull time to the server
     * @param receivedDocketsJsonArray
     */
    private void callMobilePullApi(JSONArray receivedDocketsJsonArray) {
        int length = receivedDocketsJsonArray.length();
        if(length > 0) {
            JSONObject requestJson = new JSONObject();
            try {
                requestJson.put("ReceivedDockets", receivedDocketsJsonArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            System.out.println("Mobile pull status request: " + requestJson);
            JsonObjectRequest mobilePullStatusRequest = new JsonObjectRequest(Request.Method.POST, Constants.PULL_TIME_URL, requestJson, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    System.out.println("mobile pull status response: " + jsonObject);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {

                }
            });
            TaskForceApplication.getInstance().addToRequestQueue(mobilePullStatusRequest);
        }
    }

    /**
     * Notify calling Activity
     * @param message
     */
    private void sendMessage(String message, String errorMessage) {
        Intent intent = new Intent(Constants.SCHEDULAR_SYNC_ACTION);
        // You can also include some extra data.
        intent.putExtra(Constants.SCHEDULAR_RESPONSE_KEY, message);
        intent.putExtra(Constants.SCHEDULAR_RESPONSE_MESSAGE, errorMessage);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
