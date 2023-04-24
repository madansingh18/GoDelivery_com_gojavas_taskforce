package com.gojavas.taskforce.entity;

/**
 * Created by GJS280 on 14/5/2015.
 */
public class CallLogEntity {

    private String id;
    private String username;
    private String name;
    private String number;
    private String duration;
    private String date;
    private String type;
    private String call_sms;
    private String docket_no;
    private String drsno;
    private String sync;


    public String getid() {
        return id;
    }

    public void setid(String id) {
        this.id = id;
    }

    public String getusername() {
        return username;
    }

    public void setusername(String username) {
        this.username = username;
    }

    public String getname() {
        return name;
    }

    public void setname(String name) {
        this.name = name;
    }

    public String getnumber() {
        return number;
    }

    public void setnumber(String number) {
        this.number = number;
    }

    public String getduration() {
        return duration;
    }

    public void setduration(String duration) {
        this.duration = duration;
    }

    public String getdate() {
        return date;
    }

    public void setdate(String date) {
        this.date = date;
    }

    public String gettype() {
        return type;
    }

    public void settype(String type) {
        this.type = type;
    }

    public String getcall_sms() {
        return call_sms;
    }

    public void setcall_sms(String call_sms) {
        this.call_sms = call_sms;
    }

    public String getdocket_no() {
        return docket_no;
    }

    public void setdocket_no(String docket_no) {
        this.docket_no = docket_no;
    }

    public String getdrsno() {
        return drsno;
    }

    public void setdrsno(String drsno) {
        this.drsno = drsno;
    }

    public String getsync() {
        return sync;
    }

    public void setsync(String sync) {
        this.sync = sync;
    }
}
