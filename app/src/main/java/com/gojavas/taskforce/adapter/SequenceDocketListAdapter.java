package com.gojavas.taskforce.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.gojavas.taskforce.R;
import com.gojavas.taskforce.entity.DrsEntity;
import com.gojavas.taskforce.entity.JobSetting;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by GJS280 on 20/4/2015.
 */
public class SequenceDocketListAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<DrsEntity> mDocketList;
    private ArrayList<DrsEntity> mDocketListFilter=new ArrayList<>();
    private ArrayList<JobSetting> mJobSettingList;
    private String mType = "";

    public SequenceDocketListAdapter() {}

    public SequenceDocketListAdapter(Context context, ArrayList<DrsEntity> docketList, ArrayList<JobSetting> jobList, String type){
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
        TextView mDocketNumber;
        TextView mDocketCustomerName;
        TextView mDocketCustomerAddress;
        TextView mDRSNumber;
        CheckBox mDRSCheckBox;
    }

    private static class PlanetViewHolder {
        private CheckBox checkBox ;
        private TextView mDocketNumber;
        private TextView mDRSNumber;
        private TextView mDocketCustomerName;

        public TextView getmDocketCustomerAddress() {
            return mDocketCustomerAddress;
        }

        public void setmDocketCustomerAddress(TextView mDocketCustomerAddress) {
            this.mDocketCustomerAddress = mDocketCustomerAddress;
        }

        public TextView getmDocketCustomerName() {
            return mDocketCustomerName;
        }

        public void setmDocketCustomerName(TextView mDocketCustomerName) {
            this.mDocketCustomerName = mDocketCustomerName;
        }

        public TextView getmDRSNumber() {
            return mDRSNumber;
        }

        public void setmDRSNumber(TextView mDRSNumber) {
            this.mDRSNumber = mDRSNumber;
        }

        public TextView getmDocketNumber() {
            return mDocketNumber;
        }

        public void setmDocketNumber(TextView mDocketNumber) {
            this.mDocketNumber = mDocketNumber;
        }

        private TextView mDocketCustomerAddress;
        public PlanetViewHolder() {}
        public PlanetViewHolder( TextView mDocketNumber,TextView  mDRSNumber,TextView mDocketCustomerName,TextView mDocketCustomerAddress, CheckBox checkBox ) {
            this.checkBox = checkBox ;
            this.mDocketNumber = mDocketNumber;
            this.mDRSNumber = mDRSNumber;
            this.mDocketCustomerName = mDocketCustomerName;
            this.mDocketCustomerAddress = mDocketCustomerAddress;

        }
        public CheckBox getCheckBox() {
            return checkBox;
        }
        public void setCheckBox(CheckBox checkBox) {
            this.checkBox = checkBox;
        }

    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Planet to display
        DrsEntity planet = (DrsEntity) this.getItem( position );
//        final DrsEntity entity = mDocketList.get(position);
        // The child views in each row.
        CheckBox checkBox ;
        TextView mDocketNumber,mDRSNumber,mDocketCustomerName,mDocketCustomerAddress;

        // Create a new row view
        if ( convertView == null ) {
            LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.item_list_sequence_docket, null);

            // Find the child views.
            mDocketNumber =  (TextView) convertView.findViewById(R.id.item_docket_number);
            mDRSNumber =  (TextView) convertView.findViewById(R.id.item_drs_number);

            mDocketCustomerName = (TextView)  convertView.findViewById(R.id.item_customer_name);
            mDocketCustomerAddress = (TextView)  convertView.findViewById(R.id.item_customer_address);
            checkBox = (CheckBox) convertView.findViewById( R.id.bulk_list_checkbox );
            if(this.mType.equalsIgnoreCase("Bulk")){
                checkBox.setVisibility(View.VISIBLE);
            }
            // Optimization: Tag the row with it's child views, so we don't have to
            // call findViewById() later when we reuse the row.
            convertView.setTag( new PlanetViewHolder(mDocketNumber,mDRSNumber,mDocketCustomerName,mDocketCustomerAddress,checkBox) );

            // If CheckBox is toggled, update the planet it is tagged with.
            checkBox.setOnClickListener( new View.OnClickListener() {
                public void onClick(View v) {
                    CheckBox cb = (CheckBox) v ;
                    DrsEntity planet = (DrsEntity) cb.getTag();
                    planet.setIsCheckedForBulk( cb.isChecked() );
                }
            });
        }
        // Reuse existing row view
        else {
            // Because we use a ViewHolder, we avoid having to call findViewById().
            PlanetViewHolder viewHolder = (PlanetViewHolder) convertView.getTag();
            checkBox = viewHolder.getCheckBox() ;
            mDocketNumber= viewHolder.getmDocketNumber();
            mDRSNumber= viewHolder.getmDRSNumber();
            mDocketCustomerName= viewHolder.getmDocketCustomerName();
            mDocketCustomerAddress= viewHolder.getmDocketCustomerAddress();
        }

        mDocketCustomerAddress.setText("");
        mDocketCustomerName.setText("");
        mDRSNumber.setText("");
        mDocketNumber.setText("");

        // Tag the CheckBox with the Planet it is displaying, so that we can
        // access the planet in onClick() when the CheckBox is toggled.
        checkBox.setTag(planet);

        // Display planet data
        checkBox.setChecked(planet.isCheckedForBulk());
        if(planet.getdocketno()!=null&&planet.getdocketno().length()>0)
        mDocketNumber.setText(planet.getdocketno());

        if(planet.getdrsno()!=null&&planet.getdrsno().length()>0)
            mDRSNumber.setText( planet.getdrsno() );

        if(planet.getcsgenm()!=null&&planet.getcsgenm().length()>0)
            mDocketCustomerName.setText( planet.getcsgenm() );

        if(planet.getcsgeaddr()!=null&&planet.getcsgeaddr().length()>0)
            mDocketCustomerAddress.setText(planet.getcsgeaddr() );


        return convertView;
    }




    public View getVieww(final int position, View view, ViewGroup viewGroup) {
        final DrsEntity entity = mDocketList.get(position);
        ViewHolder holder = null;
        if (view == null) {
            holder = new ViewHolder();
            LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            view = mInflater.inflate(R.layout.item_list_sequence_docket, null);

            holder.mDocketNumber =  (TextView) view.findViewById(R.id.item_docket_number);
            holder.mDRSNumber =  (TextView) view.findViewById(R.id.item_drs_number);

            holder.mDocketCustomerName = (TextView)  view.findViewById(R.id.item_customer_name);
            holder.mDocketCustomerAddress = (TextView)  view.findViewById(R.id.item_customer_address);

            if(this.mType.equalsIgnoreCase("Bulk")){
                holder.mDRSCheckBox = ((CheckBox) view.findViewById(R.id.bulk_list_checkbox));
                holder.mDRSCheckBox.setVisibility(View.VISIBLE);
            }
            view.setTag(holder);
            if(this.mType.equalsIgnoreCase("Bulk")){
                final ViewHolder finalHolder = holder;
                holder.mDRSCheckBox.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v;
                        entity.setIsCheckedForBulk(cb.isChecked());
                    }
                });
            }


        } else {
            holder = (ViewHolder) view.getTag();
        }


        holder.mDocketNumber.setText(entity.getdocketno());
        if(entity.getdrsno()!=null&&entity.getdrsno().length()>0){
            holder.mDRSNumber.setText(entity.getdrsno());
        }
        if(this.mType.equalsIgnoreCase("Bulk")){
            holder.mDRSCheckBox.setChecked(false);
            if(holder.mDRSCheckBox!=null){
                if(entity.isCheckedForBulk()){
                    holder.mDRSCheckBox.setChecked(true);
                }else{
                    holder.mDRSCheckBox.setChecked(false);
                }
            }
        }

        if(entity.getcsgenm()!=null){
            if(entity.getcsgenm().equalsIgnoreCase("null")){
                holder.mDocketCustomerName.setText("");
            }else {
                holder.mDocketCustomerName.setText(entity.getcsgenm());
            }
        }else{
            holder.mDocketCustomerName.setText("");
        }
        if(entity.getcsgeaddr()!=null){
            if(entity.getcsgeaddr().equalsIgnoreCase("null")){
                holder.mDocketCustomerAddress.setText("");
            }else{
                holder.mDocketCustomerAddress.setText(entity.getcsgeaddr());
            }
        }else{
            holder.mDocketCustomerAddress.setText("");
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
            for (DrsEntity entity : mDocketListFilter) {
                // search both docket no and customer name
                if (entity.getdocketno().toLowerCase(Locale.getDefault()).contains(searchText) ||
                        entity.getcsgenm().toLowerCase(Locale.getDefault()).contains(searchText)) {
                    mDocketList.add(entity);
                }
            }
        }
        notifyDataSetChanged();
    }
}
