package com.gojavas.taskforce.entity;

/**
 * Created by GJS280 on 26/10/2015.
 */
public class PaymentStatusEntity {

    private String drsnumber;
    private String docket_number;
    private String drs_docket;
    private String device_type;
    private String payment_status;

    public String getdrsnumber() {
        return drsnumber;
    }

    public void setdrsnumber(String drsnumber) {
        this.drsnumber = drsnumber;
    }

    public String getdocket_number() {
        return docket_number;
    }

    public void setdocket_number(String docket_number) {
        this.docket_number = docket_number;
    }

    public String getdrs_docket() {
        return drs_docket;
    }

    public void setdrs_docket(String drs_docket) {
        this.drs_docket = drs_docket;
    }

    public String getdevice_type() {
        return device_type;
    }

    public void setdevice_type(String device_type) {
        this.device_type = device_type;
    }

    public String getpayment_status() {
        return payment_status;
    }

    public void setpayment_status(String payment_status) {
        this.payment_status = payment_status;
    }
}
