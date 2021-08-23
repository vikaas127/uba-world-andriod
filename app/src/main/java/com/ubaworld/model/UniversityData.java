package com.ubaworld.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class UniversityData implements Serializable {

    @SerializedName("data")
    public List<Data> data;

    @SerializedName("message")
    public String message;

    @SerializedName("success")
    public boolean success;

    public static class Data {

        @SerializedName("name")
        public String name;

        @SerializedName("id")
        public int id;

        public Data(String name, int id) {
            this.name = name;
            this.id = id;
        }
    }

}
