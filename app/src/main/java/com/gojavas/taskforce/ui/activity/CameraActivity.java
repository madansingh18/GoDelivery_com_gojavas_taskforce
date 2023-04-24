package com.gojavas.taskforce.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.gojavas.taskforce.R;
import com.gojavas.taskforce.utils.CameraPreview;
import com.gojavas.taskforce.utils.Utility;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by GJS280 on 10/6/2015.
 */
public class CameraActivity extends AppCompatActivity implements View.OnClickListener {

    private Camera mCamera;
    private CameraPreview mCameraPreview;
    private File mFile;

    private boolean isCaptureClicked = true;
    private FrameLayout mPreview;
    private RelativeLayout mCameraPhotoLayout;
    private ImageView mCameraImage;
    private Button mCapture, mRetake, mProceed;
    private Bitmap mBitmap = null;
    private byte[] mData;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        getSupportActionBar().hide();

        getIntentValues();
        registerViews();
        registerListeners();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mCamera == null) {
            mCamera = getCameraInstance();
        }
        mCamera.startPreview();
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
            case R.id.button_capture:
                if(isCaptureClicked) {
                    System.out.println("clicked");
                    try {
                        isCaptureClicked = false;
                        mCapture.setEnabled(false);
                        mCamera.autoFocus( new Camera.AutoFocusCallback() {
                            public void onAutoFocus(boolean success, Camera camera) {
                                camera.takePicture(null, null, mPicture);
                            }
                        });
//                        mCamera.takePicture(null, null, mPicture);
                    } catch (Exception e) {
                        e.printStackTrace();
                        isCaptureClicked = true;
                        mCapture.setEnabled(true);
                    }
                }
                break;
            case R.id.camera_photo_retake:
                mBitmap.recycle();
                mCameraImage.invalidate();
                mCapture.setEnabled(true);

                if(mCamera == null) {
                    mCamera = getCameraInstance();
                }
                mCamera.startPreview();
                hideCameraPhotoView();
                showCameraView();
                break;
            case R.id.camera_photo_proceed:
                saveCapturedImage();
                break;
        }
    }

    /**
     * Get all the intent values
     */
    private void getIntentValues() {
        String path = getIntent().getStringExtra("Path");
        mFile = new File(path);
    }

    /**
     * Register all the views
     */
    private void registerViews() {

        mCamera = getCameraInstance();
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setRotation(90);
        mCamera.setDisplayOrientation(90);
        mCamera.setParameters(parameters);
        mCameraPreview = new CameraPreview(this, mCamera);
        mPreview = (FrameLayout) findViewById(R.id.camera_preview);
        mPreview.addView(mCameraPreview);


        mCameraPhotoLayout = (RelativeLayout) findViewById(R.id.camera_photo_layout);
        mCapture = (Button) findViewById(R.id.button_capture);
        mCameraImage = (ImageView) findViewById(R.id.camera_photo_image);
        mRetake = (Button) findViewById(R.id.camera_photo_retake);
        mProceed = (Button) findViewById(R.id.camera_photo_proceed);

    }

    /**
     * Register listener on all the views
     */
    private void registerListeners() {
        mCapture.setOnClickListener(this);
        mRetake.setOnClickListener(this);
        mProceed.setOnClickListener(this);
    }

    /**
     * Helper method to access the camera returns null if it cannot get the
     * camera or does not exist
     *
     * @return
     */
    private Camera getCameraInstance() {
        Camera camera = null;
        try {
            camera = Camera.open();
        } catch (Exception e) {
            // cannot get camera or does not exist
        }
        return camera;
    }

    Camera.PictureCallback mPicture = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            isCaptureClicked = true;
            mData = data;
            hideCameraView();
            showCameraPhotoView(data);
        }
    };

    /**
     * Show camera view
     */
    private void showCameraView() {
        mPreview.setVisibility(View.VISIBLE);
        mCapture.setVisibility(View.VISIBLE);

    }

    /**
     * Hide camera view
     */
    private void hideCameraView() {
        mPreview.setVisibility(View.GONE);
        mCapture.setVisibility(View.GONE);
    }

    /**
     * Show camera photo view
     */
    private void showCameraPhotoView(byte[] data) {
        mCapture.setEnabled(true);
        if(data != null) {
            if(mBitmap != null) {
                mBitmap.recycle();
            }

            mBitmap = Utility.getScaledBitmap(data, 500, 500);
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            mBitmap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), matrix, true);

            mCameraImage.setImageBitmap(mBitmap);
        }
        mCameraPhotoLayout.setVisibility(View.VISIBLE);
    }



    /**
     * Hide camera photo view
     */
    private void hideCameraPhotoView() {
        mCameraPhotoLayout.setVisibility(View.GONE);
    }

    /**
     * Save captured image
     */
    private void saveCapturedImage() {
        if (mFile == null) {
            return;
        }

        try {
            FileOutputStream fos = new FileOutputStream(mFile);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 50, fos);
//            fos.write(mData);
            fos.close();

            goToPreviousScreen();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Go to previous screen
     */
    private void goToPreviousScreen() {
        Intent successIntent = new Intent();
        setResult(RESULT_OK, successIntent);
        finish();
    }
}

