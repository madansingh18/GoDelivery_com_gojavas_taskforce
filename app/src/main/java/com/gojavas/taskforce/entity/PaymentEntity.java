package com.gojavas.taskforce.entity;

/**
 * Created by GJS280 on 7/7/2015.
 */
public class PaymentEntity {

    private String drsnumber;
    private String docket_number;
    private String drs_docket;
    private String original_amount_paid;
    private String transaction_no;
    private String auth_code;
    private String card_type;
    private String bank_name;
    private String customer_wallet_contact_number;
    private String mode_of_payment;

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

    public String getoriginal_amount_paid() {
        return original_amount_paid;
    }

    public void setoriginal_amount_paid(String original_amount_paid) {
        this.original_amount_paid = original_amount_paid;
    }

    public String gettransaction_no() {
        return transaction_no;
    }

    public void settransaction_no(String transaction_no) {
        this.transaction_no = transaction_no;
    }

    public String getauth_code() {
        return auth_code;
    }

    public void setauth_code(String auth_code) {
        this.auth_code = auth_code;
    }

    public String getcard_type() {
        return card_type;
    }

    public void setcard_type(String card_type) {
        this.card_type = card_type;
    }

    public String getbank_name() {
        return bank_name;
    }

    public String getcustomer_wallet_mobile_number(){
        return customer_wallet_contact_number;
    }

    public void setbank_name(String bank_name) {
        this.bank_name = bank_name;
    }

    public void setcustomer_wallet_contact_number(String customer_wallet_contact_number) {
        this.customer_wallet_contact_number = customer_wallet_contact_number;
    }

    public String getmode_of_payment() {
        return mode_of_payment;
    }

    public void setmode_of_payment(String mode_of_payment) {
        this.mode_of_payment = mode_of_payment;
    }
}
