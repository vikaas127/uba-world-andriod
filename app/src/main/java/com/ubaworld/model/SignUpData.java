package com.ubaworld.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SignUpData implements Serializable {

    @SerializedName("data")
    public Data data;

    @SerializedName("message")
    public String message;

    @SerializedName("success")
    public boolean success;

    public static class Data {
        @SerializedName("token")
        public String token;

        @SerializedName("mobile_number")
        public String mobile_number;

        @SerializedName("profile_image")
        public String profile_image;

        @SerializedName("is_google_login")
        public String is_google_login;

        @SerializedName("gender")
        public int gender;

        @SerializedName("id")
        public int id;

        @SerializedName("created_at")
        public String created_at;

        @SerializedName("updated_at")
        public String updated_at;

        @SerializedName("email")
        public String email;

        @SerializedName("date_of_birth")
        public String date_of_birth;

        @SerializedName("last_name")
        public String last_name;

        @SerializedName("first_name")
        public String first_name;
    }
}
