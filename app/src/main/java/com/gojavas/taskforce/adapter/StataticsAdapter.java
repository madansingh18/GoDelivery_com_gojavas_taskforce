package com.gojavas.taskforce.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.gojavas.taskforce.R;
import com.gojavas.taskforce.entity.StataticsEntity;

import java.util.ArrayList;

/**
 * Created by gjs331 on 5/15/2015.
 */
public class StataticsAdapter extends RecyclerView.Adapter<StataticsAdapter.ViewHolder>{

    ArrayList<StataticsEntity> arrayList1=new ArrayList<>();

    public StataticsAdapter(ArrayList<StataticsEntity> arrayList){
        arrayList1=arrayList;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_statatics_item, null);

        // create ViewHolder

        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

        String title=arrayList1.get(position).getTitle();
        viewHolder.txtViewTitle.setText(title);
        viewHolder.txtViewValue.setText(arrayList1.get(position).getValue());

        if (title.contains("Call"))
        viewHolder.imgViewIcon.setImageResource(R.drawable.call);
        else
            viewHolder.imgViewIcon.setImageResource(R.drawable.sms);
    }

    @Override
    public int getItemCount() {
        return this.arrayList1.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txtViewTitle,txtViewValue;
        public ImageView imgViewIcon;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            txtViewTitle = (TextView) itemLayoutView.findViewById(R.id.title_stats);
            txtViewValue = (TextView) itemLayoutView.findViewById(R.id.value_stats);
            imgViewIcon = (ImageView) itemLayoutView.findViewById(R.id.image_stats);

        }
    }
}
