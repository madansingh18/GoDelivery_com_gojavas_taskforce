package com.gojavas.taskforce.utils;

import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by GJS280 on 10/6/2015.
 */
public class CameraPreview extends SurfaceView implements
        SurfaceHolder.Callback {
    private SurfaceHolder mSurfaceHolder;
    private Camera mCamera;

    // Constructor that obtains context and camera
    @SuppressWarnings("deprecation")
    public CameraPreview(Context context, Camera camera) {
        super(context);
        this.mCamera = camera;
        this.mSurfaceHolder = this.getHolder();
        this.mSurfaceHolder.addCallback(this);
        this.mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        try {
            mCamera.setDisplayOrientation(90);
            mCamera.setPreviewDisplay(surfaceHolder);
            mCamera.startPreview();
        } catch (Exception e) {
            // left blank for now
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
//        mCamera.stopPreview();
//        mCamera.release();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format,
                               int width, int height) {
        // start preview with new settings
        try {
            mCamera.setPreviewDisplay(surfaceHolder);

//            Camera.Parameters params = mCamera.getParameters();
//
//            List<Camera.Size> mCameraSize = params.getSupportedPictureSizes();
//
//            int thresholdWidth = 1600;
//            int distance = Math.abs(mCameraSize.get(0).width - thresholdWidth);
//            int index = 0;
//            int size = mCameraSize.size();
//            for(int i=1; i<size; i++){
//                int cdistance = Math.abs(mCameraSize.get(i).width - thresholdWidth);
//                if(cdistance < distance){
//                    index = i;
//                    distance = cdistance;
//                }
//            }
//            System.out.println("camera size: " + mCameraSize.get(index).width + " == " + mCameraSize.get(index).height);
//            params.setPictureSize(mCameraSize.get(index).width, mCameraSize.get(index).height);
//            params.setRotation(90);
//            mCamera.setParameters(params);

            mCamera.startPreview();
        } catch (Exception e) {
            // intentionally left blank for a test
        }
    }
}
