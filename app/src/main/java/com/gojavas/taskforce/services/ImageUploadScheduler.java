package com.gojavas.taskforce.services;

import android.app.IntentService;
import android.content.Intent;

import com.gojavas.taskforce.database.ImageUploadHelper;
import com.gojavas.taskforce.entity.ImageUploadEntity;
import com.gojavas.taskforce.utils.UtilityScheduler;

import java.util.ArrayList;


/**
 * Created by gjs331 on 7/8/2015.
 */
public class ImageUploadScheduler extends IntentService {


    public ImageUploadScheduler(String name) {
        super(name);
    }

    public  ImageUploadScheduler(){
        super("podimages");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ArrayList<ImageUploadEntity> pendingImages = ImageUploadHelper.getInstance().getPendingImages();
        for(ImageUploadEntity image : pendingImages) {
            // Upload this image
            UtilityScheduler.uploadSingleImage(this, image.getimage_name());
        }
    }
}
