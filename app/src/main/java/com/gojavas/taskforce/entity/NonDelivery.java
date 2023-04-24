package com.gojavas.taskforce.entity;

/**
 * Created by MadanS on 10/23/2016.
 */
public class NonDelivery {
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

    public String getIsReversePickup() {
        return IsReversePickup;
    }

    public void setIsReversePickup(String isReversePickup) {
        IsReversePickup = isReversePickup;
    }

    public String getIsNDR() {
        return IsNDR;
    }

    public void setIsNDR(String isNDR) {
        IsNDR = isNDR;
    }

    private String NDRReasonID;
    private String NDRCode;
    private String NDRReason;
    private String IsReversePickup;
    private String IsNDR;
}
