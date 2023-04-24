package com.gojavas.taskforce.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.gojavas.taskforce.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by gjs331 on 6/3/2015.
 */
public class SummaryAdapter extends RecyclerView.Adapter<SummaryAdapter.ViewHolder> {

    ArrayList<HashMap<String,String>> arrayListSummary;
    public SummaryAdapter(ArrayList<HashMap<String,String>> arrayList){
        arrayListSummary=arrayList;

    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_summary_adapter_item, null);
        // create ViewHolder

        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        String jobType = arrayListSummary.get(position).get("jobtype");
        holder.tv_header.setText(jobType);
        if(jobType.contains("Amount")) {
            holder.rl_job.setVisibility(View.GONE);
            holder.tv_amount.setVisibility(View.VISIBLE);

            holder.tv_amount.setText(arrayListSummary.get(position).get("amount"));
        } else {
            holder.rl_job.setVisibility(View.VISIBLE);
            holder.tv_amount.setVisibility(View.GONE);

            holder.tv_success.setText(arrayListSummary.get(position).get("successcount"));
            holder.tv_failed.setText(arrayListSummary.get(position).get("failedcount"));
            holder.tv_pending.setText(arrayListSummary.get(position).get("pendingcount"));
        }

    }

    @Override
    public int getItemCount() {
        return arrayListSummary.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public RelativeLayout rl_job;
        public TextView tv_header, tv_success, tv_failed, tv_pending, tv_amount;

        public ViewHolder(View itemView) {
            super(itemView);
            rl_job = (RelativeLayout) itemView.findViewById(R.id.summary_job_layout);
            tv_header= (TextView) itemView.findViewById(R.id.summary_header);
            tv_success = (TextView) itemView.findViewById(R.id.summary_success);
            tv_pending = (TextView) itemView.findViewById(R.id.summary_pending);
            tv_failed = (TextView) itemView.findViewById(R.id.summary_failed);
            tv_amount = (TextView) itemView.findViewById(R.id.summary_amount);

        }

    }
}
