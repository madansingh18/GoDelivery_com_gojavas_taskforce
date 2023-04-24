package com.gojavas.taskforce.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gojavas.taskforce.R;
import com.gojavas.taskforce.entity.JobSetting;
import com.gojavas.taskforce.entity.ReversePickup;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by GJS280 on 20/4/2015.
 */
public class PickupListAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<ReversePickup> mDocketList;
    private ArrayList<ReversePickup> mDocketListFilter=new ArrayList<>();
    private ArrayList<JobSetting> mJobSettingList;
    private String mType = "";

    public PickupListAdapter() {}

    public PickupListAdapter(Context context, ArrayList<ReversePickup> docketList, ArrayList<JobSetting> jobList, String type){
        this.mContext = context;
        this.mDocketList = docketList;
        this.mJobSettingList = jobList;
        mDocketListFilter.addAll(docketList);
        this.mType = type;
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
        TextView mPickupTicketNumber;
        TextView mPersonName;
        TextView mPickupAddress;
        TextView mPickupProduct;
        TextView mPickupTime;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (view == null) {
            holder = new ViewHolder();
            LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            view = mInflater.inflate(R.layout.item_list_pickup, null);

            holder.mPickupTicketNumber =  (TextView) view.findViewById(R.id.item_ticket_number);
            holder.mPickupProduct =  (TextView) view.findViewById(R.id.item_product);

            holder.mPersonName = (TextView)  view.findViewById(R.id.item_person_name);
            holder.mPickupAddress = (TextView)  view.findViewById(R.id.item_pickup_address);
            holder.mPickupTime = (TextView)  view.findViewById(R.id.item_pickup_time);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        ReversePickup entity = mDocketList.get(position);
        try{
            holder.mPickupTicketNumber.setText("Ticket#: "+entity.getTicketNo());
        }catch (Exception e){}
        try{
            holder.mPersonName.setText("Name: "+entity.getPickupPersonName());
        }catch (Exception e){}
        try{
            holder.mPickupAddress.setText("Address "+entity.getPickupAddress()+", "+entity.getPickupCity());
        }catch (Exception e){}
        try{
            holder.mPickupProduct.setText("Content: "+entity.getContents());
        }catch (Exception e){}
        try{
            holder.mPickupTime.setText("Pickup Time: "+entity.getPickupTime());
        }catch (Exception e){}


        return view;
    }

    // Filter Class
    public void filter(String searchText) {
        searchText = searchText.toLowerCase(Locale.getDefault());
        mDocketList.clear();
        if (searchText.length() == 0) {
            mDocketList.addAll(mDocketListFilter);
        } else {
            for (ReversePickup entity : mDocketListFilter) {
                // search both docket no and customer name
//                if (entity.getdocketno().toLowerCase(Locale.getDefault()).contains(searchText) ||
//                        entity.getcsgenm().toLowerCase(Locale.getDefault()).contains(searchText)) {
//                    mDocketList.add(entity);
//                }
            }
        }
        notifyDataSetChanged();
    }
}
