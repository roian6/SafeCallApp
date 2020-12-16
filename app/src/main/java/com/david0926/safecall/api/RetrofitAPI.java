package com.david0926.safecall.api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RetrofitAPI {

    @GET("/safecall/chatbot")
    Call<ResponseBody> getText(
            @Query("text") String text,
            @Query("type") String type
    );

    @GET("/safecall/chatbot/address__list")
    Call<ResponseBody> getAddress();

    @GET("/safecall/chatbot/chat__list")
    Call<ResponseBody> getChat();

    @FormUrlEncoded
    @Headers({"X-NCP-APIGW-API-KEY-ID:d7p1vnspm3", "X-NCP-APIGW-API-KEY:mtDJQ0wj9dHDi80z5w2g02zTMzU36rnYPGZer1sE", "Content-Type:application/x-www-form-urlencoded"})
    @POST("/voice/v1/tts")
    Call<ResponseBody> getSpeech(
            @Field("speaker") String speaker,
            @Field("speed") int speed,
            @Field("text") String text
    );

}
