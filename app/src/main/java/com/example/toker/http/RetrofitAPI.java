package com.example.toker.http;

import com.example.toker.view.Item.ItemChat;
import com.example.toker.view.Item.ItemHistory;
import com.example.toker.view.Item.ItemMemory;
import com.example.toker.view.Item.ItemMessage;
import com.example.toker.view.Item.ItemNotice;
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

//    쪽지보관
    @FormUrlEncoded
    @POST("/messageOn")
    Call<List<ItemMessage>> PostMessageOn(@Field("id") String id);

    @FormUrlEncoded
    @POST("/messageOff")
    Call<String> PostMessageOff(@Field("id") String id,
                                @Field("no") String no);

//    대화보관
    @FormUrlEncoded
    @POST("/chatOn")
    Call<List<ItemMemory>> PostChatOn(@Field("id") String id);

    @FormUrlEncoded
    @POST("/chatOff")
    Call<String> PostChatOff(@Field("id") String id,
                             @Field("no") String no);

    @FormUrlEncoded
    @POST("/chat")
    Call<List<ItemChat>> PostChat(@Field("no") String no);

    @FormUrlEncoded
    @POST("/chatEdit")
    Call<String> PostChatEdit(@Field("id") String id,
                             @Field("no") String no,
                              @Field("description") String description);

//    레벨확인
    @FormUrlEncoded
    @POST("/level")
    Call<String> PostLevel(@Field("id") String id);

    @FormUrlEncoded
    @POST("/history")
    Call<List<ItemHistory>> PostHistory(@Field("id") String id);



// 공지
    @GET("/notice")
    Call<List<ItemNotice>> GetNotice();

    @FormUrlEncoded
    @POST("/post")
    Call<ItemPost> PostPost(@Field("no") String no);


// 요청
    @FormUrlEncoded
    @POST("/request")
    Call<String> PostRequest(@Field("user") String user,
                          @Field("description") String description,
                             @Field("location") String location);
}
