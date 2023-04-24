package com.gojavas.taskforce.ui.fragment;

/**
 * Created by GJS280 on 21/4/2015.
 */

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;

import com.gojavas.taskforce.R;
import com.gojavas.taskforce.adapter.DeliveredDocketListAdapter;
import com.gojavas.taskforce.database.DatabaseHelper;
import com.gojavas.taskforce.database.DeliveryHelper;
import com.gojavas.taskforce.entity.DeliveryEntity;
import com.gojavas.taskforce.entity.JobSetting;
import com.gojavas.taskforce.manager.DesignManager;
import com.gojavas.taskforce.ui.activity.DocketDetailActivity;
import com.gojavas.taskforce.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FailedFragment extends Fragment {

    private ListView mDocketListView;
    public static DeliveredDocketListAdapter mDocketListAdapter;
    SearchView mSearchView;
    private ArrayList<DeliveryEntity> mDocketList = new ArrayList<>();
    private ArrayList<JobSetting> mJobSettingList = new ArrayList<>();


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.fragment_pending,container,false);

        // Get the drs from database
        getDrsFromDatabase();

        registerViews(v);
        registerListeners();

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.menu_tab, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mDocketListAdapter.filter(newText.toLowerCase());
                return false;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    /**
     * Register all the views
     * @param view
     */
    private void registerViews(View view) {
        mDocketListView = (ListView) view.findViewById(R.id.pending_listview);

        mDocketListAdapter = new DeliveredDocketListAdapter(getActivity(), mDocketList, mJobSettingList);
        mDocketListView.setAdapter(mDocketListAdapter);
    }

    /**
     * Register listener on all the views
     */
    private void registerListeners() {
        mDocketListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DeliveryEntity entity = mDocketList.get(position);
                goToDocketDetailScreen(entity.getjobtype(), entity.getdrs_docket());
            }
        });
    }

    /**
     * Get all drs from database
     */
    private void getDrsFromDatabase() {
        mDocketList = DeliveryHelper.getInstance().getAllSuccessFailDeliveries("0");
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

    private void goToDocketDetailScreen(String jobType, String drs_docket) {
        Intent detailIntent = new Intent(getActivity(), DocketDetailActivity.class);
        detailIntent.putExtra(Constants.SCREEN, Constants.DESIGN_FAIL);
        detailIntent.putExtra(Constants.JOB_TYPE, jobType);
        detailIntent.putExtra(DatabaseHelper.DRS_DOCKET, drs_docket);
        detailIntent.putExtra("showalldetails",Constants.DESIGN_FAIL);
        startActivity(detailIntent);
    }
}
