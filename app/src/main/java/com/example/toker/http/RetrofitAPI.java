package com.example.toker.http;

import com.example.toker.view.Item.ItemChat;
import com.example.toker.view.Item.ItemHistory;
import com.example.toker.view.Item.ItemMemory;
import com.example.toker.view.Item.ItemMessage;
import com.example.toker.view.Item.ItemAbout;
import com.example.toker.view.Item.ItemPost;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface RetrofitAPI {

    String url = "http://172.30.1.22:3001/";

    @POST("/auth")
    Call<String> postAuth(@Body String id);

    @FormUrlEncoded
    @POST("/msgOn")
    Call<List<ItemMessage>> PostMsgOn(@Field("id") String id);

    @FormUrlEncoded
    @POST("/msgOff")
    Call<String> PostMsgOff(@Field("id") String id,
                            @Field("no") String no);

    @FormUrlEncoded
    @POST("/chatOn")
    Call<List<ItemMemory>> PostChatOn(@Field("id") String id);

    @FormUrlEncoded
    @POST("/chatOff")
    Call<String> PostChatOff(@Field("id") String id,
                             @Field("no") String no);

    @FormUrlEncoded
    @POST("/read")
    Call<List<ItemChat>> PostRead(@Field("no") String no);

    @FormUrlEncoded
    @POST("/level")
    Call<String> PostLevel(@Field("id") String id);

    @FormUrlEncoded
    @POST("/history")
    Call<List<ItemHistory>> PostHistory(@Field("id") String id);

    @GET("/about")
    Call<List<ItemAbout>> GetAbout();

    @FormUrlEncoded
    @POST("/post")
    Call<ItemPost> PostPost(@Field("no") String no);

    @FormUrlEncoded
    @POST("/request")
    Call<String> PostRequest(@Field("id") String id,
                          @Field("description") String description);
}
