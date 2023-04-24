package com.gojavas.taskforce.entity;

/**
 * Created by MadanS on 4/8/2017.
 */
public class NDRReason {

    @Override
    public String toString() {
        return getNDRReason();
    }

    public String getNDRReasonID() {
        return NDRReasonID;
    }

    public void setNDRReasonID(String NDRReasonID) {
        this.NDRReasonID = NDRReasonID;
    }

    public String getNDRCode() {
        return NDRCode;
    }

    public void setNDRCode(String NDRCode) {
        this.NDRCode = NDRCode;
    }

    public String getNDRReason() {
        return NDRReason;
    }

    public void setNDRReason(String NDRReason) {
        this.NDRReason = NDRReason;
    }

    public boolean isReversePickup() {
        return IsReversePickup;
    }

    public void setIsReversePickup(boolean isReversePickup) {
        IsReversePickup = isReversePickup;
    }

    private String NDRReasonID;
    private String NDRCode;
    private String NDRReason;
    private boolean IsReversePickup;



}
