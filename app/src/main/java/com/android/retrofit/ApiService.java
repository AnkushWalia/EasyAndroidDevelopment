package com.android.retrofit;

import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;


public interface ApiService {


    @POST("userInfo")
    Observable<JsonObject> userInfo(@Body HashMap<String, String> jsonBody);

    @POST("send")
    Observable<JsonObject> sendFcm(@HeaderMap Map<String, String> headers, @Body HashMap<Object, Object> json);



}
