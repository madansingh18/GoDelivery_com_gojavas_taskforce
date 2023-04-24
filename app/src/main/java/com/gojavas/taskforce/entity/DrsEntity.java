package com.gojavas.taskforce.entity;
import java.io.Serializable;
/**
 * Created by GJS280 on 16/4/2015.
 */
@SuppressWarnings("serial")
public class DrsEntity implements Serializable {

    private String sr;
    private String jobtype;
    private String docketno;
    private String exchange_requestid;
    private String drsno;
    private String drs_docket;
    private String pieces;
    private String csgenm;
    private String csgeaddr;
    private String address_type;
    private String pickup_location;
    private String timetoend;
    private String csgeteleno;
    private String alternate_number;
    private String ctr_no;
    private String csgecity;
    private String reassign_destcd;
    private String csgepincode;
    private String pkgsno;
    private String cod_dod;
    private String cod_amt;
    private String delivered;
    private String drsupdated;
    private String logistic_dt;
    private String logistic_time;
    private String start_km;
    private String total_dockets_in_drs;
    private String dkt_count_not_updated;
    private String driver_name;
    private String driver_id;
    private String vehicle_number;
    private String actuwt;
    private String userid;
    private String client_code;
    private String client_name;
    private String nextattemptdate;
    private String prodcd;
    private String codedesc;
    private String amount_to_cutomer;
    private String entrydate;
    private String entryby;
    private String lasteditdate;
    private String lasteditby;
    private String status;
    private String responsexml;
    private String failreason;
    private String response_datetime;
    private String slotno;
    private String latitude;
    private String longitude;
    private String ordernumber;
    private String choiceofpayment;
    private String imageRequired;
    private String date;
    private String olddocketno;
    private String sellername;
    private String contact_person;
    private String attempt;
    private String mobile_pull_status;
    private int position;
    private String product_description;
    private String reason_for_return;
    private String return_request_id;
    private String tp_code;
    private String return_pincode;

    public boolean isCheckedForBulk() {
        return isCheckedForBulk;
    }

    public void setIsCheckedForBulk(boolean isCheckedForBulk) {
        this.isCheckedForBulk = isCheckedForBulk;
    }

    private boolean isCheckedForBulk;

    public String getAccepted_cod() {
        return accepted_cod;
    }

    public void setAccepted_cod(String accepted_cod) {
        this.accepted_cod = accepted_cod;
    }

    private String accepted_cod;

    public String getsr() {
        return sr;
    }

    public void setsr(String sr) {
        this.sr = sr;
    }

    public String getjobtype() {
        return jobtype;
    }

    public void setjobtype(String jobtype) {
        this.jobtype = jobtype;
    }

    public String getdocketno() {
        return docketno;
    }

    public void setdocketno(String docketno) {
        this.docketno = docketno;
    }

    public String getexchange_requestid() {
        return exchange_requestid;
    }

    public void setexchange_requestid(String exchange_requestid) {
        this.exchange_requestid = exchange_requestid;
    }

    public String getdrsno() {
        return drsno;
    }

    public void setdrsno(String drsno) {
        this.drsno = drsno;
    }

    public String getdrs_docket() {
        return drs_docket;
    }

    public void setdrs_docket(String drs_docket) {
        this.drs_docket = drs_docket;
    }

    public String getpieces() {
        return pieces;
    }

    public void setpieces(String pieces) {
        this.pieces = pieces;
    }

    public String getcsgenm() {
        return csgenm;
    }

    public void setcsgenm(String csgenm) {
        this.csgenm = csgenm;
    }

    public String getcsgeaddr() {
        return csgeaddr;
    }

    public void setcsgeaddr(String csgeaddr) {
        this.csgeaddr = csgeaddr;
    }

    public String getaddress_type() {
        return address_type;
    }

    public void setaddress_type(String address_type) {
        this.address_type = address_type;
    }

    public String getpickup_location() {
        return pickup_location;
    }

    public void setpickup_location(String pickup_location) {
        this.pickup_location = pickup_location;
    }

    public String gettimetoend() {
        return timetoend;
    }

    public void settimetoend(String timetoend) {
        this.timetoend = timetoend;
    }

    public String getcsgeteleno() {
        return csgeteleno;
    }

    public void setcsgeteleno(String csgeteleno) {
        this.csgeteleno = csgeteleno;
    }

    public String getalternate_number() {
        return alternate_number;
    }

    public void setalternate_number(String alternate_number) {
        this.alternate_number = alternate_number;
    }

    public String getctr_no() {
        return ctr_no;
    }

    public void setctr_no(String ctr_no) {
        this.ctr_no = ctr_no;
    }

    public String getcsgecity() {
        return csgecity;
    }

    public void setcsgecity(String csgecity) {
        this.csgecity = csgecity;
    }

    public String getreassign_destcd() {
        return reassign_destcd;
    }

    public void setreassign_destcd(String reassign_destcd) {
        this.reassign_destcd = reassign_destcd;
    }

    public String getcsgepincode() {
        return csgepincode;
    }

    public void setcsgepincode(String csgepincode) {
        this.csgepincode = csgepincode;
    }

    public String getpkgsno() {
        return pkgsno;
    }

    public void setpkgsno(String pkgsno) {
        this.pkgsno = pkgsno;
    }

    public String getcod_dod() {
        return cod_dod;
    }

    public void setcod_dod(String cod_dod) {
        this.cod_dod = cod_dod;
    }

    public String getcod_amt() {
        return cod_amt;
    }

    public void setcod_amt(String cod_amt) {
        this.cod_amt = cod_amt;
    }

    public String getdelivered() {
        return delivered;
    }

    public void setdelivered(String delivered) {
        this.delivered = delivered;
    }

    public String getdrsupdated() {
        return drsupdated;
    }

    public void setdrsupdated(String drsupdated) {
        this.drsupdated = drsupdated;
    }

    public String getlogistic_dt() {
        return logistic_dt;
    }

    public void setlogistic_dt(String logistic_dt) {
        this.logistic_dt = logistic_dt;
    }

    public String getlogistic_time() {
        return logistic_time;
    }

    public void setlogistic_time(String logistic_time) {
        this.logistic_time = logistic_time;
    }

    public String getstart_km() {
        return start_km;
    }

    public void setstart_km(String start_km) {
        this.start_km = start_km;
    }

    public String gettotal_dockets_in_drs() {
        return total_dockets_in_drs;
    }

    public void settotal_dockets_in_drs(String total_dockets_in_drs) {
        this.total_dockets_in_drs = total_dockets_in_drs;
    }

    public String getdkt_count_not_updated() {
        return dkt_count_not_updated;
    }

    public void setdkt_count_not_updated(String dkt_count_not_updated) {
        this.dkt_count_not_updated = dkt_count_not_updated;
    }

    public String getdriver_name() {
        return driver_name;
    }

    public void setdriver_name(String driver_name) {
        this.driver_name = driver_name;
    }

    public String getdriver_id() {
        return driver_id;
    }

    public void setdriver_id(String driver_id) {
        this.driver_id = driver_id;
    }

    public String getvehicle_number() {
        return vehicle_number;
    }

    public void setvehicle_number(String vehicle_number) {
        this.vehicle_number = vehicle_number;
    }

    public String getactuwt() {
        return actuwt;
    }

    public void setactuwt(String actuwt) {
        this.actuwt = actuwt;
    }

    public String getuserid() {
        return userid;
    }

    public void setuserid(String userid) {
        this.userid = userid;
    }

    public String getclient_code() {
        return client_code;
    }

    public void setclient_code(String client_code) {
        this.client_code = client_code;
    }

    public String getclient_name() {
        return client_name;
    }

    public void setclient_name(String client_name) {
        this.client_name = client_name;
    }

    public String getnextattemptdate() {
        return nextattemptdate;
    }

    public void setnextattemptdate(String nextattemptdate) {
        this.nextattemptdate = nextattemptdate;
    }

    public String getprodcd() {
        return prodcd;
    }

    public void setprodcd(String prodcd) {
        this.prodcd = prodcd;
    }

    public String getcodedesc() {
        return codedesc;
    }

    public void setcodedesc(String codedesc) {
        this.codedesc = codedesc;
    }

    public String getamount_to_cutomer() {
        return amount_to_cutomer;
    }

    public void setamount_to_cutomer(String amount_to_cutomer) {
        this.amount_to_cutomer = amount_to_cutomer;
    }

    public String getentrydate() {
        return entrydate;
    }

    public void setentrydate(String entrydate) {
        this.entrydate = entrydate;
    }

    public String getentryby() {
        return entryby;
    }

    public void setentryby(String entryby) {
        this.entryby = entryby;
    }

    public String getlasteditdate() {
        return lasteditdate;
    }

    public void setlasteditdate(String lasteditdate) {
        this.lasteditdate = lasteditdate;
    }

    public String getlasteditby() {
        return lasteditby;
    }

    public void setlasteditby(String lasteditby) {
        this.lasteditby = lasteditby;
    }

    public String getstatus() {
        return status;
    }

    public void setstatus(String status) {
        this.status = status;
    }

    public String getresponsexml() {
        return responsexml;
    }

    public void setresponsexml(String responsexml) {
        this.responsexml = responsexml;
    }

    public String getfailreason() {
        return failreason;
    }

    public void setfailreason(String failreason) {
        this.failreason = failreason;
    }

    public String getresponse_datetime() {
        return response_datetime;
    }

    public void setresponse_datetime(String response_datetime) {
        this.response_datetime = response_datetime;
    }

    public String getslotno() {
        return slotno;
    }

    public void setslotno(String slotno) {
        this.slotno = slotno;
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

    public String getordernumber() {
        return ordernumber;
    }

    public void setordernumber(String ordernumber) {
        this.ordernumber = ordernumber;
    }

    public String getchoiceofpayment() {
        return choiceofpayment;
    }

    public void setchoiceofpayment(String choiceofpayment) {
        this.choiceofpayment = choiceofpayment;
    }

    public String getdate() {
        return date;
    }

    public void setdate(String date) {
        this.date = date;
    }

    public String getolddocketno() {
        return olddocketno;
    }

    public void setolddocketno(String olddocketno) {
        this.olddocketno = olddocketno;
    }

    public String getsellername() {
        return sellername;
    }

    public void setsellername(String sellername) {
        this.sellername = sellername;
    }

    public String getcontact_person() {
        return contact_person;
    }

    public void setcontact_person(String contact_person) {
        this.contact_person = contact_person;
    }

    public String getattempt() {
        return attempt;
    }

    public void setattempt(String attempt) {
        this.attempt = attempt;
    }

    public String getmobile_pull_status() {
        return mobile_pull_status;
    }

    public void setmobile_pull_status(String mobile_pull_status) {
        this.mobile_pull_status = mobile_pull_status;
    }

    public int getposition() {
        return position;
    }

    public void setposition(int position) {
        this.position = position;
    }

    public String getproduct_description() {
        return product_description;
    }

    public void setproduct_description(String product_description) {
        this.product_description = product_description;
    }

    public String getreason_for_return() {
        return reason_for_return;
    }

    public void setreason_for_return(String reason_for_return) {
        this.reason_for_return = reason_for_return;
    }

    public String getreturn_request_id() {
        return return_request_id;
    }

    public void setreturn_request_id(String return_request_id) {
        this.return_request_id = return_request_id;
    }

    public String gettp_code() {
        return tp_code;
    }

    public void settp_code(String tp_code) {
        this.tp_code = tp_code;
    }

    public String getreturn_pincode() {
        return return_pincode;
    }

    public void setreturn_pincode(String return_pincode) {
        this.return_pincode = return_pincode;
    }

    public String getImageRequired() {
        return imageRequired;
    }

    public void setImageRequired(String imageRequired) {
        this.imageRequired = imageRequired;
    }
}
