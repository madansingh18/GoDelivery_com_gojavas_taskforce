package com.gojavas.taskforce.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.gojavas.taskforce.R;
import com.gojavas.taskforce.entity.DrsEntity;

import java.util.ArrayList;

/**
 * Created by GJS280 on 16/4/2015.
 */
public class DocketListAdapter extends RecyclerView.Adapter<DocketListAdapter.DocketListViewHolder> {

    private ArrayList<DrsEntity> mDocketList;
    private ArrayList<DrsEntity> mSequencedDocketList = new ArrayList<>();

    public DocketListAdapter(ArrayList<DrsEntity> list) {
        this.mDocketList = list;
    }

    @Override
    public int getItemCount() {
        return mDocketList.size();
    }

    @Override
    public DocketListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.item_list_sequence_docket, parent, false);

        return new DocketListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(DocketListViewHolder holder, int position) {
        DrsEntity entity = mDocketList.get(position);
        String docketNumber = entity.getdocketno();
        String docketCustomerName = entity.getcsgenm();
        String docketStatus = entity.getstatus();

        holder.mDocketNumber.setText(docketNumber);
        holder.mDocketCustomerName.setText(docketCustomerName);
        holder.mDocketStatus.setText("P");
    }

    public static class DocketListViewHolder extends RecyclerView.ViewHolder/* implements View.OnClickListener*/ {
        protected TextView mDocketNumber;
        protected TextView mDocketCustomerName;
        protected TextView mDocketStatus;
        protected TextView mDocketPosition;

        public DocketListViewHolder(View view) {
            super(view);
            mDocketNumber =  (TextView) view.findViewById(R.id.item_docket_number);
            mDocketCustomerName = (TextView)  view.findViewById(R.id.item_docket_customer_name);
//            mDocketStatus = (TextView)  view.findViewById(R.id.item_docket_status);
//            mDocketPosition = (TextView) view.findViewById(R.id.item_docket_position);
//            view.setOnClickListener(this);
        }

//        @Override
//        public void onClick(View v) {
//            mDocketPosition.setVisibility(View.VISIBLE);
//        }
    }
}
