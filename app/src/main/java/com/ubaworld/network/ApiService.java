package com.ubaworld.network;

import com.ubaworld.model.LoginData;
import com.ubaworld.model.UniversityData;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;

public interface ApiService {

    @FormUrlEncoded
    @POST("user/login")
    Call<LoginData> user_Login(@Field("email") String email,
                               @Field("password") String password);

    @FormUrlEncoded
    @POST("user/register")
    Call<LoginData> user_Register(@Field("first_name") String first_name,
                                  @Field("last_name") String last_name,
                                  @Field("date_of_birth") String date_of_birth,
                                  @Field("email") String email,
                                  @Field("password") String password);

    @FormUrlEncoded
    @POST("user/forgot-password")
    Call<LoginData> user_ForgotPassword(@Field("email") String email);

//    @FormUrlEncoded
//    @POST("user")
//    Call<LoginData> user_EditProfile(@Field("first_name") String first_name,
//                                     @Field("last_name") String last_name,
//                                     @Field("date_of_birth") String date_of_birth,
//                                     @Field("email") String email,
//                                     @Field("gender") int gender,
//                                     @Field("country") String country,
//                                     @Field("password") String password,
//                                     @Field("user_type") String user_type);

    @Multipart
    @POST("user")
    Call<LoginData> user_EditProfile(@PartMap() Map<String, RequestBody> partMap,
                                     @Part MultipartBody.Part file);

    @Multipart
    @POST("user")
    Call<LoginData> user_EditProfile(@PartMap() Map<String, RequestBody> partMap);

    @POST("user/logout")
    Call<ResponseBody> user_Logout();

    @GET("university")
    Call<UniversityData> list_University();

    @GET("get-comments")
    Call<ResponseBody> list_CommentMy(@Query("comment_type") int comment_type,
                                      @Query("user_id") int user_id,
                                      @Query("page") int page);

    @GET("get-comments")
    Call<ResponseBody> list_Comment(@Query("comment_type") int comment_type,
                                    @Query("key") String key,
                                    @Query("page") int page);

    @FormUrlEncoded
    @POST("add-comment")
    Call<ResponseBody> add_Comment(@Field("comment") String comment,
                                   @Field("type") int type);

    @FormUrlEncoded
    @POST("update-comment")
    Call<ResponseBody> update_Comment(@Field("comment_id") int comment_id,
                                      @Field("comment") String comment);

    @FormUrlEncoded
    @POST("delete-comment")
    Call<ResponseBody> delete_Comment(@Field("comment_id") int comment_id);


    @GET("view-more-replies")
    Call<ResponseBody> list_Reply(@Query("comment_id") int comment_id);

    @FormUrlEncoded
    @POST("add-reply")
    Call<ResponseBody> add_Reply(@Field("comment_id") int comment_id,
                                 @Field("reply") String reply);

    @FormUrlEncoded
    @POST("update-reply")
    Call<ResponseBody> update_Reply(@Field("reply_id") int reply_id,
                                    @Field("reply") String reply);

    @FormUrlEncoded
    @POST("add-remove-comment-like")
    Call<ResponseBody> like_Comment(@Field("comment_id") int comment_id);

    @FormUrlEncoded
    @POST("add-remove-reply-like")
    Call<ResponseBody> like_Reply(@Field("reply_id") int reply_id);

    @FormUrlEncoded
    @POST("delete-reply")
    Call<ResponseBody> delete_Reply(@Field("reply_id") int reply_id);

    @FormUrlEncoded
    @POST("report-user")
    Call<ResponseBody> report_User(@Field("user_id") int user_id);

    @GET("get-comments")
    Call<ResponseBody> list_CommentUniversity(@Query("comment_type") int comment_type,
                                              @Query("key") String key,
                                              @Query("university_id") String university_id);

    @GET("get-comments")
    Call<ResponseBody> list_CommentMyUniversity(@Query("comment_type") int comment_type,
                                                @Query("user_id") int user_id,
                                                @Query("page") int page,
                                                @Query("university_id") String university_id);

    @FormUrlEncoded
    @POST("add-comment")
    Call<ResponseBody> add_CommentUniversity(@Field("comment") String comment,
                                             @Query("university_id") String university_id,
                                             @Field("type") int type);

    @GET("notification")
    Call<ResponseBody> notification(@Query("user_id") String user_id);

    @FormUrlEncoded
    @POST("read-notification")
    Call<ResponseBody> read_Notification(@Field("user_id") int user_id,
                                         @Field("type") int type);

    @FormUrlEncoded
    @POST("user/register-device-token")
    Call<ResponseBody> registerDeviceToken(@Field("device_token") String deviceToken);


}
