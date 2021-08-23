package com.ubaworld.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class NotificationData implements Serializable {

    @SerializedName("data")
    public List<Data> data;

    @SerializedName("message")
    public String message;

    @SerializedName("success")
    public boolean success;

    public static class Data {

        @SerializedName("updated_at")
        public String updated_at;

        @SerializedName("created_at")
        public String created_at;

        @SerializedName("is_read")
        public int is_read;

        @SerializedName("type")
        public int type;

        @SerializedName("user_id")
        public int user_id;

        @SerializedName("id")
        public int id;
    }
}
