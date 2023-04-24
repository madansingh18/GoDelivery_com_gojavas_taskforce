package com.gojavas.taskforce.entity;

/**
 * Created by GJS280 on 28/10/2015.
 */
public class ImageUploadEntity {

    private String image_name;
    private String upload_status;

    public String getimage_name() {
        return image_name;
    }

    public void setimage_name(String image_name) {
        this.image_name = image_name;
    }

    public String getupload_status() {
        return upload_status;
    }

    public void setupload_status(String upload_status) {
        this.upload_status = upload_status;
    }
}
