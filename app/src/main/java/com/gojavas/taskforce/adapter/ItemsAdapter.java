package com.gojavas.taskforce.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.gojavas.taskforce.R;
import com.gojavas.taskforce.entity.ItemEntity;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by gjs331 on 5/19/2015.
 */
public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ViewHolder> {

    static ArrayList<ItemEntity> arrayList;

    public  ItemsAdapter(ArrayList<ItemEntity> itemEntityArrayList){

        arrayList=itemEntityArrayList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_items_adapter, null);

        // create ViewHolder

        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.tv_id.setText("SKU Id: " + arrayList.get(position).getsku_id());
        holder.tv_cost.setText("SKU Cost: " + arrayList.get(position).getsku_cost());
        holder.tv_description.setText("Description: " + arrayList.get(position).getsku_description());

        String check=arrayList.get(position).getStatus();
        if (check.equalsIgnoreCase("1"))
        holder.checkBox.setChecked(true);
        else
            holder.checkBox.setChecked(false);

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b)
                    arrayList.get(position).setStatus("1");
                else
                    arrayList.get(position).setStatus("0");

            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tv_id,tv_description,tv_cost;
        public CheckBox checkBox;

        public ViewHolder(View itemView) {
            super(itemView);

            tv_id = (TextView) itemView.findViewById(R.id.item_id);
            tv_description = (TextView) itemView.findViewById(R.id.item_description);
            tv_cost = (TextView) itemView.findViewById(R.id.item_cost);
            checkBox=(CheckBox)itemView.findViewById(R.id.item_check);

        }
    }

    public static HashMap<String,Object> proceed(){
        StringBuilder stringBuilder_yes=new StringBuilder();
        StringBuilder stringBuilder_no=new StringBuilder();
        int count=0;
        stringBuilder_yes.append("");
        stringBuilder_no.append("");
        for (int i=0;i<arrayList.size();i++) {
            if (arrayList.get(i).getStatus().equalsIgnoreCase("1")) {
                stringBuilder_yes.append(arrayList.get(i).getsr()+"','");
                count++;
            } else {
                stringBuilder_no.append(arrayList.get(i).getsr()+"','");
            }
        }
        HashMap<String,Object> map=new HashMap<>();
        map.put("yes", stringBuilder_yes);
        map.put("no", stringBuilder_no);
        map.put("count",count);
        return map;
    }
}
