package com.ubaworld.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class OtherBuyingData implements Serializable {

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    public OtherBuyingData(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

}
