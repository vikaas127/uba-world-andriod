package com.ubaworld.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class GenderData implements Serializable {

    @SerializedName("genderId")
    private String genderId;

    @SerializedName("gender")
    private String gender;

    public GenderData(String gender, String genderId) {
        this.gender = gender;
        this.genderId = genderId;
    }

    public String getGenderId() {
        return genderId;
    }

    public void setGenderId(String genderId) {
        this.genderId = genderId;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
