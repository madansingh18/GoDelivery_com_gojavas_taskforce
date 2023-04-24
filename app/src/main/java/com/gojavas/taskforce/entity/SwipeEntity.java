package com.gojavas.taskforce.entity;

/**
 * Created by GJS280 on 14/5/2015.
 */
public class SwipeEntity {

    private String sr;
    private String docket_no;
    private String drs_no;
    private String device_type;
    private String status;
    private String reason;
    private String datetime;
    private String sync;

    public String getsr() {
        return sr;
    }

    public void setsr(String sr) {
        this.sr = sr;
    }

    public String getdocket_no() {
        return docket_no;
    }

    public void setdocket_no(String docket_no) {
        this.docket_no = docket_no;
    }

    public String getdrs_no() {
        return drs_no;
    }

    public void setdrs_no(String drs_no) {
        this.drs_no = drs_no;
    }

    public String getdevice_type() {
        return device_type;
    }

    public void setdevice_type(String device_type) {
        this.device_type = device_type;
    }

    public String getstatus() {
        return status;
    }

    public void setstatus(String status) {
        this.status = status;
    }

    public String getreason() {
        return reason;
    }

    public void setreason(String reason) {
        this.reason = reason;
    }

    public String getdatetime() {
        return reason;
    }

    public void setdatetime(String reason) {
        this.reason = reason;
    }

    public String getsync() {
        return sync;
    }

    public void setsync(String sync) {
        this.sync = sync;
    }
}
