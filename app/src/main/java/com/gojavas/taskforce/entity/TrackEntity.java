package com.gojavas.taskforce.entity;

/**
 * Created by GJS280 on 14/5/2015.
 */
public class TrackEntity {

    private String sr;
    private String username;
    private String latitude;
    private String longitude;
    private String datetime;
    private String sync;
    private String networktype;

    public String getNetworkType(){
        return networktype;
    }

    public void setNetWorkType(String type) {
        this.networktype = type;
    }

    public String getsr() {
        return sr;
    }

    public void setsr(String sr) {
        this.sr = sr;
    }

    public String getusername() {
        return username;
    }

    public void setusername(String username) {
        this.username = username;
    }

    public String getlatitude() {
        return latitude;
    }

    public void setlatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getlongitude() {
        return longitude;
    }

    public void setlongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getdatetime() {
        return datetime;
    }

    public void setdatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getsync() {
        return sync;
    }

    public void setsync(String sync) {
        this.sync = sync;
    }
}
