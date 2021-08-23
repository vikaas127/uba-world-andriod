package com.ubaworld.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class LoginData implements Serializable {

    @SerializedName("data")
    public Data data;

    @SerializedName("message")
    public String message;

    @SerializedName("success")
    public boolean success;

    public static class Data {

        @SerializedName("token")
        public String token;

        @SerializedName("is_google_login")
        public String is_google_login;

        @SerializedName("updated_at")
        public String updated_at;

        @SerializedName("created_at")
        public String created_at;

        @SerializedName("is_verify")
        public int is_verify;

        @SerializedName("is_blocked")
        public int is_blocked;

        @SerializedName("role")
        public int role;

        @SerializedName("email")
        public String email;

        @SerializedName("user_type")
        public String user_type;

        @SerializedName("country")
        public String country;

        @SerializedName("profile_image")
        public String profile_image;

        @SerializedName("gender")
        public String gender;

        @SerializedName("date_of_birth")
        public String date_of_birth;

        @SerializedName("last_name")
        public String last_name;

        @SerializedName("first_name")
        public String first_name;

        @SerializedName("id")
        public int id;

    }


}
