package com.android.retrofit;

import com.android.models.FaceCompare;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface ApiService {


    @POST("userInfo")
    Observable<JsonObject> userInfo(@Body HashMap<String, String> jsonBody);

    @Multipart
    @POST("compare")
    Observable<FaceCompare> compareTwoFace(@Part("api_key") RequestBody api_key, @Part("api_secret") RequestBody api_secret, @Part MultipartBody.Part file, @Part MultipartBody.Part file2);


    @GET("details")
    Observable<ResponseBody> checkAppUpdate(@Query("id") String packageName);

    @GET("v2.4/{user_id}/taggable_friends")
    Observable<JsonObject> getFriendsList(@Path("user_id") String userId,
                                          @Query("access_token") String accessToken,
                                          @Query("limit") int limit,
                                          @Query("after") String afterPage);
}
