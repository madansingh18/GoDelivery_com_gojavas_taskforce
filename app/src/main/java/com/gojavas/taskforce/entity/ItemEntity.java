package com.gojavas.taskforce.entity;

/**
 * Created by GJS280 on 14/5/2015.
 */
public class ItemEntity {

    private String sr;
    private String drs_no;
    private String docket_no;
    private String drs_docket;
    private String sku_id;
    private String sku_description;
    private String sku_cost;
    private String quantity;
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getsr() {
        return sr;
    }

    public void setsr(String sr) {
        this.sr = sr;
    }

    public String getdrs_no() {
        return drs_no;
    }

    public void setdrs_no(String drs_no) {
        this.drs_no = drs_no;
    }

    public String getdocket_no() {
        return docket_no;
    }

    public void setdocket_no(String docket_no) {
        this.docket_no = docket_no;
    }

    public String getdrs_docket() {
        return drs_docket;
    }

    public void setdrs_docket(String drs_docket) {
        this.drs_docket = drs_docket;
    }

    public String getsku_id() {
        return sku_id;
    }

    public void setsku_id(String sku_id) {
        this.sku_id = sku_id;
    }

    public String getsku_description() {
        return sku_description;
    }

    public void setsku_description(String sku_description) {
        this.sku_description = sku_description;
    }

    public String getsku_cost() {
        return sku_cost;
    }

    public void setsku_cost(String sku_cost) {
        this.sku_cost = sku_cost;
    }

    public String getquantity() {
        return quantity;
    }

    public void setquantity(String quantity) {
        this.quantity = quantity;
    }
}
