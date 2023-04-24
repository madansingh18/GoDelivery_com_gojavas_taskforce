package com.gojavas.taskforce.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.gojavas.taskforce.R;
import com.gojavas.taskforce.utils.Constants;
import com.gojavas.taskforce.utils.SignatureView;
import com.gojavas.taskforce.utils.Utility;

import java.io.ByteArrayOutputStream;

/**
 * Created by GJS280 on 4/5/2015.
 */
public class SignatureActivity extends AppCompatActivity implements View.OnClickListener {

    private SignatureView mSignatureView;
    private Button mDoneButton, mClearButton;
    private Bitmap mBitmap;
    public static boolean isEmpty = true;
    String aWBNo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signature);
        aWBNo = getIntent().getStringExtra("AWBNo");
        customActionBar();

        registerViews();
        registerListeners();

        isEmpty = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mBitmap != null) {
            mBitmap.recycle();
            mBitmap = null;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signature_done_button:
                doneButtonClicked();
                break;
            case R.id.signature_clear_button:
                isEmpty=true;
                clearSignature();
                break;
            default:
                break;
        }
    }

    /**
     * customize action bar
     */
    private void customActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Signature");
    }

    /**
     * Regsiter all the views
     */
    private void registerViews() {
        mSignatureView = (SignatureView) findViewById(R.id.signature_view);
        mSignatureView.setBrushSize(2.0F);
        if(aWBNo!=null&&aWBNo.length()>0)
        mSignatureView.setAWBNo(aWBNo);
        mDoneButton = (Button) findViewById(R.id.signature_done_button);
        mClearButton = (Button) findViewById(R.id.signature_clear_button);
    }

    /**
     * Register listeners on all the views
     */
    private void registerListeners() {
        mDoneButton.setOnClickListener(this);
        mClearButton.setOnClickListener(this);
    }

    /**
     * Done button is clicked
     */
    private void doneButtonClicked() {
        System.out.println("done clicked");
        if (isEmpty == true){
            Utility.showToast(SignatureActivity.this, "Please sign");
            return;
        }
        mDoneButton.setEnabled(false);
        mClearButton.setEnabled(false);
        // Get the signature bitmap
        mSignatureView.setDrawingCacheEnabled(true);
        mBitmap = Bitmap.createScaledBitmap(mSignatureView.getDrawingCache(), Utility.getScreenWidth(SignatureActivity.this), Utility.getScreenHeight(SignatureActivity.this), false);
        // Convert bitmap into byte array
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        mBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] bitmapBytes = stream.toByteArray();
        mSignatureView.setDrawingCacheEnabled(false);

        // Send bytes via intent
        Intent successIntent = new Intent();
        successIntent.putExtra(Constants.SIGNATURE_IMAGE, bitmapBytes);
        setResult(RESULT_OK, successIntent);
        finish();
    }

    /**
     * Clear signature
     */
    public void clearSignature() {
        if(mBitmap != null) {
            mBitmap.recycle();
        }
        mSignatureView.startNew();
    }
}
