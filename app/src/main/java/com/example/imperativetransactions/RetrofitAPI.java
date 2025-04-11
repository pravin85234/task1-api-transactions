package com.example.imperativetransactions;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface RetrofitAPI {
    @Headers({"Content-Type: application/json",
            "Accept: application/json"})

    @POST
    Call<JsonObject> createPost(@Body JsonObject body, @Url String url);

    @Headers({"Content-Type: application/json",
            "Accept: application/json"})

    @GET("v1/prices")
    Call<JsonArray> createGet(@Header("Authorization") String token);

}

