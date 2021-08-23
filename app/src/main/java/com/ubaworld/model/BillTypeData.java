package com.ubaworld.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class BillTypeData implements Serializable {

    @SerializedName("billId")
    private String billId;

    @SerializedName("billType")
    private String billType;

    public BillTypeData(String billType, String billId) {
        this.billType = billType;
        this.billId = billId;
    }

    public String getBillId() {
        return billId;
    }

    public void setBillId(String billId) {
        this.billId = billId;
    }

    public String getBillType() {
        return billType;
    }

    public void setBillType(String billType) {
        this.billType = billType;
    }
}
