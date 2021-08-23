package com.ubaworld.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AlarmRepeatTypeData implements Serializable {

    @SerializedName("repeatId")
    private String repeatId;

    @SerializedName("repeatType")
    private String repeatType;

    public AlarmRepeatTypeData(String repeatType, String repeatId) {
        this.repeatType = repeatType;
        this.repeatId = repeatId;
    }

    public String getRepeatId() {
        return repeatId;
    }

    public void setRepeatId(String repeatId) {
        this.repeatId = repeatId;
    }

    public String getRepeatType() {
        return repeatType;
    }

    public void setRepeatType(String repeatType) {
        this.repeatType = repeatType;
    }
}
