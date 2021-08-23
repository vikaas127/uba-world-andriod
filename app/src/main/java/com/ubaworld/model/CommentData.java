package com.ubaworld.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class CommentData implements Serializable {

    @SerializedName("data")
    public Data data;

    @SerializedName("message")
    public String message;

    @SerializedName("success")
    public boolean success;

    public static class Data {

        @SerializedName("total")
        public int total;

        @SerializedName("to")
        public int to;

        @SerializedName("per_page")
        public int per_page;

        @SerializedName("path")
        public String path;

        @SerializedName("last_page_url")
        public String last_page_url;

        @SerializedName("last_page")
        public int last_page;

        @SerializedName("from")
        public int from;

        @SerializedName("first_page_url")
        public String first_page_url;

        @SerializedName("data")
        public List<DataItem> dataItems;

        @SerializedName("current_page")
        public int current_page;
    }

    public static class DataItem {

        @SerializedName("get_university")
        public Get_university get_university;

        @SerializedName("replies")
        public List<Replies> replies;

        @SerializedName("is_verify")
        public int is_verify;

        @SerializedName("profile_image")
        public String profile_image;

        @SerializedName("is_liked")
        public int is_liked;

        @SerializedName("updated_at")
        public String updated_at;

        @SerializedName("created_at")
        public String created_at;

        @SerializedName("likes")
        public int likes;

        @SerializedName("type")
        public int type;

        @SerializedName("comment")
        public String comment;

        @SerializedName("university_id")
        public int university_id;

        @SerializedName("user_id")
        public int user_id;

        @SerializedName("id")
        public int id;
    }

    public static class Get_university {

        @SerializedName("name")
        public String name;

        @SerializedName("id")
        public int id;
    }

    public static class Replies {

        @SerializedName("is_verify")
        public int is_verify;

        @SerializedName("profile_image")
        public String profile_image;

        @SerializedName("is_liked")
        public int is_liked;

        @SerializedName("updated_at")
        public String updated_at;

        @SerializedName("created_at")
        public String created_at;

        @SerializedName("likes")
        public int likes;

        @SerializedName("reply")
        public String reply;

        @SerializedName("comment_id")
        public int comment_id;

        @SerializedName("user_id")
        public int user_id;

        @SerializedName("id")
        public int id;
    }
}
