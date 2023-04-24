package com.gojavas.taskforce.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gojavas.taskforce.R;
import com.gojavas.taskforce.entity.DeliveryEntity;
import com.gojavas.taskforce.entity.JobSetting;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by GJS280 on 3/6/2015.
 */
public class DeliveredDocketListAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<DeliveryEntity> mDocketList;
    private ArrayList<DeliveryEntity> mDocketListFilter=new ArrayList<>();
    private ArrayList<JobSetting> mJobSettingList;

    public DeliveredDocketListAdapter() {}

    public DeliveredDocketListAdapter(Context context, ArrayList<DeliveryEntity> docketList, ArrayList<JobSetting> jobList){
        this.mContext = context;
        this.mDocketList = docketList;
        mDocketListFilter.addAll(docketList);
        this.mJobSettingList = jobList;
    }

    @Override
    public int getCount() {
        return mDocketList.size();
    }

    @Override
    public Object getItem(int position) {
        return mDocketList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        TextView mDocketNumber;
        TextView mDocketCustomerName;
        TextView mDocketCustomerAddress;
        TextView mDocketType;
        TextView mDocketSyncStatus;
        TextView mDocketAttmept;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (view == null) {
            holder = new ViewHolder();
            LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            view = mInflater.inflate(R.layout.item_list_delivered_docket, null);

            holder.mDocketNumber =  (TextView) view.findViewById(R.id.item_docket_number);
            holder.mDocketCustomerName = (TextView)  view.findViewById(R.id.item_docket_customer_name);
            holder.mDocketCustomerAddress = (TextView)  view.findViewById(R.id.item_docket_customer_address);
            holder.mDocketType = (TextView)  view.findViewById(R.id.item_docket_type);
            holder.mDocketSyncStatus = (TextView) view.findViewById(R.id.item_docket_sync_status);
            holder.mDocketAttmept = (TextView) view.findViewById(R.id.item_docket_attempt);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        DeliveryEntity entity = mDocketList.get(position);
        holder.mDocketNumber.setText(entity.getdocket_number());
        holder.mDocketCustomerName.setText(entity.getcust_name());
        holder.mDocketCustomerAddress.setText(entity.getcust_address1());
        String sync = entity.getsync();
        String jobType = entity.getjobtype();
        String attempt = entity.getattempt_number();
        holder.mDocketAttmept.setText("Attempt " + attempt);
        int size = mJobSettingList.size();
        for(int i=0; i<mJobSettingList.size(); i++) {
            JobSetting job = mJobSettingList.get(i);
            String type = job.getJobType();
            if(type.equalsIgnoreCase(jobType)) {
                String identifier = job.getIdentifier();
                String color = job.getColor();
                holder.mDocketType.setText(identifier);
                holder.mDocketType.setTextColor(Color.parseColor(color));
                break;
            }
        }

        if(sync != null && sync.equalsIgnoreCase("0")) {
            holder.mDocketSyncStatus.setVisibility(View.VISIBLE);
        } else {
            holder.mDocketSyncStatus.setVisibility(View.GONE);
        }

        return view;
    }

    // Filter Class
    public void filter(String searchText) {
        searchText = searchText.toLowerCase(Locale.getDefault());
        mDocketList.clear();
        if (searchText.length() == 0) {
            mDocketList.addAll(mDocketListFilter);
        } else {
            for (DeliveryEntity entity : mDocketListFilter) {
                // search both docket no and customer name
                if (entity.getdocket_number().toLowerCase(Locale.getDefault()).contains(searchText) ||
                        entity.getcust_name().toLowerCase(Locale.getDefault()).contains(searchText)) {
                    mDocketList.add(entity);
                }
            }
        }
        notifyDataSetChanged();
    }
}
