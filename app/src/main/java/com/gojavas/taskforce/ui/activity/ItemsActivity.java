package com.gojavas.taskforce.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gojavas.taskforce.R;
import com.gojavas.taskforce.adapter.ItemsAdapter;
import com.gojavas.taskforce.database.DrsHelper;
import com.gojavas.taskforce.database.ItemHelper;
import com.gojavas.taskforce.entity.DrsEntity;
import com.gojavas.taskforce.entity.ItemEntity;
import com.gojavas.taskforce.utils.Constants;
import com.gojavas.taskforce.utils.Utility;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by gjs331 on 5/18/2015.
 */
public class ItemsActivity extends AppCompatActivity {

    RecyclerView recyclerView_list;
    ArrayList<ItemEntity> arrayList;
    Button btn_proceed;
    String drs_docket, mFailCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_items);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView_list=(RecyclerView)findViewById(R.id.reclerview_items_list);
        btn_proceed=(Button)findViewById(R.id.btn_proceed);

        recyclerView_list.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ItemsActivity.this);
        recyclerView_list.setLayoutManager(linearLayoutManager);

        Intent i = getIntent();
        drs_docket = i.getStringExtra(Constants.DRS_DOCKET);
        mFailCategory = i.getStringExtra(Constants.FAIL_CATEGORY);

        DrsEntity drs = DrsHelper.getInstance().getDrs(drs_docket);
        String jobType = drs.getjobtype();
        // Get only delivery SKU Items for Exchange job type
        if(jobType.equalsIgnoreCase(Constants.JOB_TYPE_EXCHANGE)) {
            getSupportActionBar().setTitle("Sku Items to be Delivered");
            arrayList = ItemHelper.getInstance().getExchangeItems(drs_docket, drs.getdocketno());
        } else {
            getSupportActionBar().setTitle("Sku Items");
            arrayList = ItemHelper.getInstance().getItems(drs_docket);
        }

        ItemsAdapter adapter=new ItemsAdapter(arrayList);
        recyclerView_list.setAdapter(adapter);

        btn_proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                HashMap<String,Object> map=ItemsAdapter.proceed();
                StringBuilder yes= (StringBuilder) map.get("yes");

                if (yes.toString().contains(",")) {
                    yes.deleteCharAt(yes.lastIndexOf(","));
                    yes.deleteCharAt(yes.lastIndexOf("'"));
                }
                StringBuilder no= (StringBuilder) map.get("no");

                if (no.toString().contains(",")){
                    no.deleteCharAt(no.lastIndexOf(","));
                    no.deleteCharAt(no.lastIndexOf("'"));
                }

                if(mFailCategory != null && mFailCategory.contains(Constants.PARTIAL_RETURN)) {
                    int totalItems = arrayList.size();
                    int selectedItems = (int) map.get("count");
                    if(selectedItems == 0) {
                        Utility.showToast(ItemsActivity.this, "Please select atleast one item");
                        return;
                    } else if(selectedItems == totalItems) {
                        Utility.showToast(ItemsActivity.this, "You cannot select all the items");
                        return;
                    }
                } else {
                    if(!(arrayList.size() > 0 && yes.length() > 1)) {
                        Utility.showToast(ItemsActivity.this, "Select atleast one item");
                        return;
                    }
                }

                System.out.println("yes: " + yes + " == no: " + no);
                ItemHelper.getInstance().statusUpdate(yes, no, drs_docket);

                goToPreviousScreen();

            }
        });


    }

    /**
     * Go to previous screen
     */
    private void goToPreviousScreen() {
        Intent successIntent = new Intent();
        setResult(RESULT_OK, successIntent);
        finish();
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
