package com.gojavas.taskforce.ui.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.gojavas.taskforce.R;
import com.gojavas.taskforce.adapter.SequenceDocketListAdapter;
import com.gojavas.taskforce.database.DrsHelper;
import com.gojavas.taskforce.entity.DrsEntity;
import com.gojavas.taskforce.entity.JobSetting;
import com.gojavas.taskforce.manager.DesignManager;
import com.gojavas.taskforce.utils.Constants;
import com.gojavas.taskforce.utils.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by GJS280 on 17/4/2015.
 */
public class SequenceActivity extends AppCompatActivity {

    private ListView mDocketListView;
    private TextView mPendingTextView, mSequencedTextView;
    private Button mDoneButton;

    private SequenceDocketListAdapter mSequenceDocketListAdapter;
    private ArrayList<DrsEntity> mDocketList = new ArrayList<>();
    private ArrayList<DrsEntity> mSequencedDocketList = new ArrayList<>();
    private ArrayList<JobSetting> mJobSettingList = new ArrayList<>();
    private int mTotalCount, mPendingCount, mSequencedCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sequence);

        modifyActionBar();

        // Get the drs from database
        getDrsFromDatabase();

        registerViews();
        registerListeners();
        updateCount();
        mTotalCount = mPendingCount + mSequencedCount;
    }

    /**
     * Modify action bar
     */
    private void modifyActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Sequence");
        actionBar.hide();
    }

    /**
     * Get all drs from database
     */
    private void getDrsFromDatabase() {
        mDocketList = DrsHelper.getInstance().getAllPendingDrs();
        int size = mDocketList.size();
        for(int i=0; i<size; i++) {
            DrsEntity entity = mDocketList.get(i);
            if(entity.getposition() > 0) {
                mSequencedDocketList.add(entity);
            }
        }
        try {
            JSONObject designJson = DesignManager.getInstance().getDesignJson();
            JSONArray jobTypeObject = designJson.getJSONArray(Constants.JOB_TYPE);
            int length = jobTypeObject.length();
            for(int i=0; i<length; i++) {
                String jobType = jobTypeObject.getString(i);
                JSONObject jobObject = designJson.getJSONObject(jobType);
                JSONObject jobSettingObject = jobObject.getJSONObject("Basic_Settings");
                String title = jobSettingObject.getString("Job_Title");
                String identifier = jobSettingObject.getString("Identifier");
                String color = jobSettingObject.getString("Identifier_Color");

                JobSetting job = new JobSetting();
                job.setJobType(jobType);
                job.setTitle(title);
                job.setIdentifier(identifier);
                job.setColor(color);

                mJobSettingList.add(job);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Register all the views
     */
    private void registerViews() {
        mDocketListView = (ListView) findViewById(R.id.sequence_docket_list_listview);
        mPendingTextView = (TextView) findViewById(R.id.sequence_pending_textview);
        mSequencedTextView = (TextView) findViewById(R.id.sequence_sequenced_textview);
        mDoneButton = (Button) findViewById(R.id.sequence_done_button);

        mSequenceDocketListAdapter = new SequenceDocketListAdapter(SequenceActivity.this, mDocketList, mJobSettingList, "Sequence");
        mDocketListView.setAdapter(mSequenceDocketListAdapter);

        updateCount();
    }

    /**
     * Register listener on all the views
     */
    private void registerListeners() {
        mDoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int size = mSequencedDocketList.size();
                for(int i=0; i<size; i++) {
                    DrsEntity drsEntity = mSequencedDocketList.get(i);
                    String drs_docket = drsEntity.getdrs_docket();
                    DrsHelper.getInstance().updateDrsPosition(drs_docket, drsEntity.getposition());
                }
                finish();
            }
        });

        mDocketListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Check whether this docket is sequenced or not
                int pos = mDocketList.get(position).getposition();
                if(pos == 0) {
                    // Not sequenced
                    mDocketList.get(position).setposition(mTotalCount);
                    mTotalCount--;
                    mSequencedDocketList.add(mDocketList.get(position));
                    mSequenceDocketListAdapter.notifyDataSetChanged();
                    // Update pending and sequenced count
                    updateCount();
                    // If all the dockets are sequenced, then show done button
                    if(mDocketList.size() == mSequencedDocketList.size()) {
                        mDoneButton.setVisibility(View.VISIBLE);
                    }
                } else {
                    // Already sequenced
                    // Ask to re-sequence items
                    showResequenceDialog();
                }
            }
        });
    }

    /**
     * Update pending and sequenced item's count
     */
    private void updateCount() {
        mPendingCount = (mDocketList.size() - mSequencedDocketList.size());
        mSequencedCount = mSequencedDocketList.size();
        mPendingTextView.setText(mPendingCount + "");
        mSequencedTextView.setText(mSequencedCount + "");
    }

    /**
     * Show dialog to enter phone number
     */
    public void showResequenceDialog() {
        final Dialog dialog = new Dialog(SequenceActivity.this, R.style.customDialogTheme);
        dialog.setContentView(R.layout.dialog_resequence);
        Button okButton = (Button) dialog.findViewById(R.id.dialog_ok_button);
        Button cancelButton = (Button) dialog.findViewById(R.id.dialog_cancel_button);

        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = Utility.getMetricsWidth(SequenceActivity.this);
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDoneButton.setVisibility(View.GONE);
                int size = mDocketList.size();
                for(int i=0; i<size; i++) {
                    // Set the position of all dockets as 0
                    DrsEntity entity = mDocketList.get(i);
                    entity.setposition(0);
                    DrsHelper.getInstance().updateDrsPosition(entity.getdrs_docket(), 0);
                    // Clear sequence docket list
                    mSequencedDocketList.clear();
                }
                // Update pending and sequenced items count
                updateCount();
                // Refresh the list
                mSequenceDocketListAdapter.notifyDataSetChanged();
                // Close the dialog
                dialog.dismiss();
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
}
