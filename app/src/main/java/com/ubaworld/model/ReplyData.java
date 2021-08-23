package com.ubaworld.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ReplyData implements Serializable {

    @SerializedName("data")
    public List<Data> data;

    @SerializedName("message")
    public String message;

    @SerializedName("success")
    public boolean success;

    public static class Data {

        @SerializedName("replies")
        public List<Replies> replies;

        @SerializedName("is_verify")
        public int is_verify;

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

        @SerializedName("user_id")
        public int user_id;

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
