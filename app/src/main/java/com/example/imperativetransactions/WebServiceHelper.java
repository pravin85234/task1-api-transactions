package com.example.imperativetransactions;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WebServiceHelper {

    public static HttpResultDO callWebService(RestMethodType restMethodType, String url, JsonObject json) {
        String screen = "";
        if (json.size() == 3) {
            screen = isnull(json.get("Screen").getAsString());
        }

        HttpResultDO httpResult = new HttpResultDO();
        List<ConnectionSpec> connectionSpecs = Arrays.asList(
                ConnectionSpec.MODERN_TLS,
                ConnectionSpec.CLEARTEXT
        );

        try {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
            OkHttpClient client = new OkHttpClient.Builder().connectionSpecs(connectionSpecs).addInterceptor(loggingInterceptor)
                    .connectTimeout(30, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).build();

            Retrofit retrofit;
            Call<JsonObject> retrofitCall = null;
            Call<JsonArray> retrofitCall1 = null;

            switch (restMethodType) {
                case GET:
                    retrofit = new Retrofit.Builder().baseUrl(url + "/").client(client).addConverterFactory(GsonConverterFactory.create()).build();
                    RetrofitAPI retrofitAPIGet = retrofit.create(RetrofitAPI.class);
                    String tokenId = json.get("TokennId").getAsString();
                    retrofitCall1 = retrofitAPIGet.createGet(tokenId);
                    break;

                case POST:
                    retrofit = new Retrofit.Builder().baseUrl(url + "/").client(client).addConverterFactory(GsonConverterFactory.create()).build();
                    RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
                    retrofitCall = retrofitAPI.createPost(json, url);
                    break;
            }

            try {
                int statusCode;
                if ("Prices".equalsIgnoreCase(screen)) {
                    Response<JsonArray> response1 = retrofitCall1.execute();
                    httpResult.setResult(ServiceCallStatus.Sent);
                    statusCode = response1.code();

                    if (statusCode >= 200 && statusCode <= 206) {
                        httpResult.setResult(ServiceCallStatus.Success);
                        Gson gson = new GsonBuilder().serializeNulls().create();
                        httpResult.setResponseContent(gson.toJson(response1.body()));
                    } else {
                        httpResult.setResult(ServiceCallStatus.Failed);
                        httpResult.setErrorMessage("Error sending data, status code: " + statusCode);
                    }
                } else {
                    Response<JsonObject> response = retrofitCall.execute();
                    httpResult.setResult(ServiceCallStatus.Sent);
                    statusCode = response.code();

                    if (statusCode >= 200 && statusCode <= 206) {
                        httpResult.setResult(ServiceCallStatus.Success);
                        Gson gson = new GsonBuilder().serializeNulls().create();
                        httpResult.setResponseContent(gson.toJson(response.body()));
                    } else {
                        httpResult.setResult(ServiceCallStatus.Failed);
                        httpResult.setErrorMessage("Error sending data, status code: " + statusCode);
                    }
                }
            } catch (Exception e) {
                httpResult.setResult(ServiceCallStatus.Exception);
                httpResult.setErrorMessage("Exception: " + e.getMessage());
                e.printStackTrace();
            }
        } catch (Exception e) {
            httpResult.setResult(ServiceCallStatus.Exception);
            httpResult.setErrorMessage("Unexpected exception: " + e.getMessage());
            e.printStackTrace();
        }

        return httpResult;
    }

    public enum ServiceCallStatus {
        Pending, Sent, Success, Failed, Exception, ConcurrencyError
    }

    public enum RestMethodType {
        GET, POST, PUT, DELETE
    }

    public static String isnull(String value) {
        return (value == null) ? "" : value;
    }
}
