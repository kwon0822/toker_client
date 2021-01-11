package com.example.toker.http;

import com.example.toker.recyclerview.Item_Chat_Title;
import com.example.toker.recyclerview.Item_Msg;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface RetrofitAPI {

    @POST("/auth")
    Call<String> postAuth(@Body String id);

    @FormUrlEncoded
    @POST("/msgOn")
    Call<List<Item_Msg>> postMsgOn(@Field("id") String id);

    @FormUrlEncoded
    @POST("/msgOff")
    Call<String> postMsgOff(@Field("id") String id,
                            @Field("no") String no);

    @FormUrlEncoded
    @POST("/chatOnTitle")
    Call<List<Item_Chat_Title>> postChatOnt(@Field("id") String id);

    @FormUrlEncoded
    @POST("/chatOffTitle")
    Call<String> postChatOffTitle(@Field("id") String id,
                                  @Field("no") String no);
}
