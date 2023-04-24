package com.gojavas.taskforce.entity;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by MadanS on 4/8/2017.
 */
public class ReversePickup implements Serializable {

    public ArrayList<String> getSKU_ID() {
        return SKU_ID;
    }

    public void setSKU_ID(ArrayList<String> SKU_ID) {
        this.SKU_ID = SKU_ID;
    }

    private ArrayList<String> SKU_ID;

    public String getProcessID() {
        return ProcessID;
    }

    public void setProcessID(String processID) {
        ProcessID = processID;
    }

    public String getTicketNo() {
        return TicketNo;
    }

    public void setTicketNo(String ticketNo) {
        TicketNo = ticketNo;
    }

    public String getPickupPersonName() {
        return PickupPersonName;
    }

    public void setPickupPersonName(String pickupPersonName) {
        PickupPersonName = pickupPersonName;
    }

    public String getPickupAddress() {
        return PickupAddress;
    }

    public void setPickupAddress(String pickupAddress) {
        PickupAddress = pickupAddress;
    }

    public String getPickupPincode() {
        return PickupPincode;
    }

    public void setPickupPincode(String pickupPincode) {
        PickupPincode = pickupPincode;
    }

    public String getPickupCity() {
        return PickupCity;
    }

    public void setPickupCity(String pickupCity) {
        PickupCity = pickupCity;
    }

    public String getPickupMobile() {
        return PickupMobile;
    }

    public void setPickupMobile(String pickupMobile) {
        PickupMobile = pickupMobile;
    }

    public String getPickupPhone() {
        return PickupPhone;
    }

    public void setPickupPhone(String pickupPhone) {
        PickupPhone = pickupPhone;
    }

    public String getPickupEmail() {
        return PickupEmail;
    }

    public void setPickupEmail(String pickupEmail) {
        PickupEmail = pickupEmail;
    }

    public String getProduct() {
        return Product;
    }

    public void setProduct(String product) {
        Product = product;
    }

    public int getPcs() {
        return Pcs;
    }

    public void setPcs(int pcs) {
        Pcs = pcs;
    }

    public double getWeight() {
        return Weight;
    }

    public void setWeight(double weight) {
        Weight = weight;
    }

    public double getLength() {
        return Length;
    }

    public void setLength(double length) {
        Length = length;
    }

    public double getWidth() {
        return Width;
    }

    public void setWidth(double width) {
        Width = width;
    }

    public double getHeight() {
        return Height;
    }

    public void setHeight(double height) {
        Height = height;
    }

    public String getContents() {
        return Contents;
    }

    public void setContents(String contents) {
        Contents = contents;
    }

    public double getGoodsValue() {
        return GoodsValue;
    }

    public void setGoodsValue(double goodsValue) {
        GoodsValue = goodsValue;
    }

    public String getPickupTime() {
        return PickupTime;
    }

    public void setPickupTime(String pickupTime) {
        PickupTime = pickupTime;
    }

    public String getReturn_Reason() {
        return Return_Reason;
    }

    public void setReturn_Reason(String return_Reason) {
        Return_Reason = return_Reason;
    }

    public String getRemarks() {
        return Remarks;
    }

    public void setRemarks(String remarks) {
        Remarks = remarks;
    }

    public String getSyncDateTime() {
        return SyncDateTime;
    }

    public void setSyncDateTime(String syncDateTime) {
        SyncDateTime = syncDateTime;
    }

    private String Return_Reason;
    private String Remarks;

    public String getManifestNo() {
        return ManifestNo;
    }

    public void setManifestNo(String manifestNo) {
        ManifestNo = manifestNo;
    }

    private String ManifestNo;
    private String SyncDateTime;
    private String ProcessID;
    private String TicketNo;
    private String PickupPersonName;
    private String PickupAddress;
    private String PickupPincode;
    private String PickupCity;
    private String PickupMobile;
    private String PickupPhone;
    private String PickupEmail;
    private String Product;
    private int Pcs;
    private double Weight;
    private double Length;
    private double Width;
    private double Height;
    private String Contents;
    private double GoodsValue;
    private String PickupTime;
    
}
