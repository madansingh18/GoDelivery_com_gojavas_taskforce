package com.gojavas.taskforce.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.gojavas.taskforce.R;
import com.gojavas.taskforce.entity.ReversePickup;

/**
 * Created by MadanS on 10/23/2016.
 */
public class PickupDetailActivity extends AppCompatActivity implements View.OnClickListener {


    private Button mReverseDetailUpdateStatus;
    private ImageButton mMapDirection;
    private Button mReverseDetailClose;
    private ReversePickup mReversePickup;
//    private TextView DOCKETNO;
    private TextView ProcessID;
    private TextView TicketNo;
    private TextView PickupPersonName;
    private TextView PickupAddress;
    private TextView PickupPincode;
    private TextView PickupCity;
    private TextView PickupMobile;
    private TextView PickupPhone;
    private TextView PickupEmail;
    private TextView Product;
    private TextView Pcs;
    private TextView Weight;
    private TextView Length;
    private TextView Width;
    private TextView Height;
    private TextView Contents;
    private TextView GoodsValue;
    private TextView PickupTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pickup_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Pickup Details");
        Intent i = getIntent();
        mReversePickup = (ReversePickup)i.getSerializableExtra("selectedPickupEntities");
        if(mReversePickup!=null){
            Log.i("TEST>", ""+mReversePickup.getTicketNo());
        }
        registerViews();
        registerListeners();
    }


    /**
     * Register all the views
     */
    private void registerViews() {
        mReverseDetailClose = (Button) findViewById(R.id.reverse_detail_close);
        mReverseDetailUpdateStatus = (Button) findViewById(R.id.reverse_detail_update_status);

        mMapDirection = (ImageButton) findViewById(R.id.maps_driving);

        try{
            ProcessID = (TextView) findViewById(R.id.ProcessID);
            ProcessID.setText(mReversePickup.getManifestNo());
        }catch (Exception e){}


        try{
            TicketNo = (TextView) findViewById(R.id.TicketNo);
            TicketNo.setText(""+mReversePickup.getTicketNo());
        }catch (Exception e){}


        try{
            PickupPersonName = (TextView) findViewById(R.id.PickupPersonName);
            PickupPersonName.setText(""+mReversePickup.getPickupPersonName());
        }catch (Exception e){}


        try{
            PickupAddress = (TextView) findViewById(R.id.PickupAddress);
            PickupAddress.setText(""+mReversePickup.getPickupAddress());
//            mMapDirection.setVisibility(View.VISIBLE);
//            mMapDirection.setOnClickListener(this);
            PickupAddress.setOnClickListener(this);

        }catch (Exception e){}

        try{
            PickupPincode = (TextView) findViewById(R.id.PickupPincode);
            PickupPincode.setText(""+mReversePickup.getPickupPincode());
        }catch (Exception e){}

        try{
            PickupCity = (TextView) findViewById(R.id.PickupCity);
            PickupCity.setText(""+mReversePickup.getPickupCity());
        }catch (Exception e){}


        try{
            PickupMobile = (TextView) findViewById(R.id.PickupMobile);
            PickupMobile.setText(""+mReversePickup.getPickupMobile());
        }catch (Exception e){}

        try{
            PickupPhone = (TextView) findViewById(R.id.PickupPhone);
            PickupPhone.setText(""+mReversePickup.getPickupPhone());
        }catch (Exception e){}

        try{
            PickupEmail = (TextView) findViewById(R.id.PickupEmail);
            PickupEmail.setText(""+mReversePickup.getPickupEmail());
        }catch (Exception e){}

        try{
            Product = (TextView) findViewById(R.id.Product);
            Product.setText(""+mReversePickup.getProduct());
        }catch (Exception e){}

        try{
            Pcs = (TextView) findViewById(R.id.Pcs);
            Pcs.setText(""+mReversePickup.getPcs());
        }catch (Exception e){}

        try{
            Weight = (TextView) findViewById(R.id.Weight);
            Weight.setText(""+mReversePickup.getWeight());
        }catch (Exception e){}

        try{
            Length = (TextView) findViewById(R.id.Length);
            Length.setText(""+mReversePickup.getLength());
        }catch (Exception e){}


        try{
            Width = (TextView) findViewById(R.id.Width);
            Width.setText(""+mReversePickup.getWidth());
        }catch (Exception e){}


        try{
            Height = (TextView) findViewById(R.id.Height);
            Height.setText(""+mReversePickup.getHeight());
        }catch (Exception e){}


        try{
            Contents = (TextView) findViewById(R.id.Contents);
            Contents.setText(""+mReversePickup.getContents());
        }catch (Exception e){}


        try{
            GoodsValue = (TextView) findViewById(R.id.GoodsValue);
            GoodsValue.setText(""+mReversePickup.getGoodsValue());
        }catch (Exception e){}

        try{
            PickupTime = (TextView) findViewById(R.id.PickupTime);
            PickupTime.setText(""+mReversePickup.getPickupTime());
        }catch (Exception e){}

    }



    /**
     * Register listeners on all the views
     */
    private void registerListeners() {
        mReverseDetailClose.setOnClickListener(this);
        mReverseDetailUpdateStatus.setOnClickListener(this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==2){
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.reverse_detail_update_status:
                performNonDeliveredOperation();
                break;
            case R.id.reverse_detail_close:
                    finish();
                break;

            case R.id.maps_driving:
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("google.navigation:q="+PickupAddress.getText().toString()));
                startActivity(intent);
                break;

            case R.id.PickupAddress:
                Intent intent1 = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("google.navigation:q="+PickupAddress.getText().toString()));
                startActivity(intent1);
                break;

            default:
                break;
        }
    }

    private void performNonDeliveredOperation() {
        Intent intent = new Intent(PickupDetailActivity.this, ReversePickupCancelActivity.class);
        intent.putExtra("selectedPickupEntities", mReversePickup);
        startActivityForResult(intent, 2);
    }


    private void startPassActivity(){
        Intent intent = new Intent(PickupDetailActivity.this, DRSPassActivity.class);
        intent.putExtra("selectedDrsEntities", mReversePickup);
        startActivityForResult(intent, 2);
    }


}

