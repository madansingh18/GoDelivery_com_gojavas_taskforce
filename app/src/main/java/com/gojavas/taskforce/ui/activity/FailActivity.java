package com.gojavas.taskforce.ui.activity;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.gojavas.taskforce.R;
import com.gojavas.taskforce.database.DrsHelper;
import com.gojavas.taskforce.database.ItemHelper;
import com.gojavas.taskforce.entity.DrsEntity;
import com.gojavas.taskforce.entity.ItemEntity;
import com.gojavas.taskforce.manager.DesignManager;
import com.gojavas.taskforce.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by GJS280 on 3/5/2015.
 */
public class FailActivity extends AppCompatActivity {

    private LinearLayout mLinearLayout;

    private static DrsEntity mDrsEntity;

    private JSONArray mFailArray;
    private String mJobType;
    private static String mCODAmount, mDocketNumber;
    private int mItemsCount;
    private int mfour, mSixteen;
    private int mBgColor, mTextColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fail);
        getSupportActionBar().hide();

        getIntentValues();

        registerViews();

        populateFailReasons();
    }

    /**
     * Get all the intent values
     */
    private void getIntentValues() {
        Intent intent = getIntent();
        mJobType = intent.getStringExtra(Constants.JOB_TYPE);
//        mDrsEntity = EventBus.getDefault().removeStickyEvent(DrsEntity.class);
        String drs_docket = intent.getStringExtra(Constants.DRS_DOCKET);
        mDrsEntity = DrsHelper.getInstance().getDrsDetailData(drs_docket);
        mDocketNumber = mDrsEntity.getdocketno();
        mCODAmount = mDrsEntity.getcod_amt();

        ArrayList<ItemEntity> items = ItemHelper.getInstance().getItems(drs_docket);
        mItemsCount = items.size();
    }

    /**
     * Register all the views
     */
    private void registerViews() {
        mLinearLayout = (LinearLayout) findViewById(R.id.fail_main_layout);

        Resources resources = getResources();
        mfour = (int) resources.getDimension(R.dimen.four);
        mSixteen = (int) resources.getDimension(R.dimen.sixteen);
        mBgColor = resources.getColor(R.color.app_color);
        mTextColor = resources.getColor(R.color.white);
    }

    /**
     * Display all the fail reasons
     */
    private void populateFailReasons() {
        try {
            JSONObject designJson = DesignManager.getInstance().getDesignJson();
            JSONObject jobJson = designJson.getJSONObject(mJobType);
            mFailArray = jobJson.getJSONArray(Constants.DESIGN_FAIL);

            Log.i("mFailArray", mFailArray+"");

            ArrayList<String> clients = new ArrayList<>();
            JSONArray clientsJsonArray = designJson.getJSONArray(Constants.DESIGN_CLIENT);
            int size = clientsJsonArray.length();
            for(int i=0; i<size; i++) {
                String client = clientsJsonArray.getString(i);
                clients.add(client);
            }

            int length = mFailArray.length();
            for(int i=0; i<length; i++) {
                JSONObject failReasonJson = mFailArray.getJSONObject(i);
                String type = failReasonJson.getString(Constants.DESIGN_TYPE);
                String title = failReasonJson.getString(Constants.DESIGN_TITLE);

                // Don't show Partial Return for this client
                String clientCode = mDrsEntity.getclient_code();
                if(!clients.contains(clientCode) && title.contains(Constants.PARTIAL_RETURN)) {
                    continue;
                } else {
                    if (mItemsCount <= 1 && title.contains(Constants.PARTIAL_RETURN)) {
                        // Don't show Partial Return for item count less than one
                        continue;
                    }
                }

                TextView textView = createChildTextView(title, type);
                mLinearLayout.addView(textView);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * Create TextView for fail reasons
     * @param text
     * @param type
     * @return
     */
    private TextView createChildTextView(String text, final String type) {
        TextView textView = new TextView(FailActivity.this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, mfour, 0, mfour);
        textView.setLayoutParams(params);
        textView.setBackgroundColor(mBgColor);
        textView.setGravity(Gravity.CENTER);
        textView.setPadding(0, mSixteen, 0, mSixteen);
        textView.setText(text);
        textView.setTextColor(mTextColor);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToFailReason(type, ((TextView) v).getText().toString());
            }
        });
        return textView;
    }

    /**
     * Go to next screen to fill fields for this fail reason
     * @param type
     */
    private void goToFailReason(String type, String text) {
        Intent successIntent = new Intent(FailActivity.this, SuccessActivity.class);
        successIntent.putExtra(Constants.JOB_TYPE, type);
        successIntent.putExtra(Constants.FAIL_CATEGORY, text);
        successIntent.putExtra(Constants.SCREEN, Constants.STATUS_FAIL);
        successIntent.putExtra(Constants.DRS_DOCKET, mDrsEntity.getdrs_docket());
//        EventBus.getDefault().postSticky(mDrsEntity);
        startActivity(successIntent);
    }
}
