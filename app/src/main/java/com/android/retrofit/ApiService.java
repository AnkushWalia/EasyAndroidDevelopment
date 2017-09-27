package com.android.retrofit;

import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface ApiService {


    @POST("userInfo")
    Observable<JsonObject> userInfo(@Body HashMap<String, String> jsonBody);

    @POST("send")
    Observable<JsonObject> sendFcm(@HeaderMap Map<String, String> headers, @Body HashMap<Object, Object> json);

    @GET("v2.4/{user_id}/taggable_friends")
    Observable<JsonObject> getFriendsList(@Path("user_id") String userId,
                                          @Query("access_token") String accessToken,
                                          @Query("limit") int limit,
                                          @Query("after") String afterPage);
}
