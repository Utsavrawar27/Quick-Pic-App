package com.example.quickpic_app.API;


import com.example.quickpic_app.models.BarcodeDetectionRequest;
import com.example.quickpic_app.models.BarcodeDetectionResponse;
import com.example.quickpic_app.models.LoginResult;
import com.example.quickpic_app.models.Product;
import com.example.quickpic_app.models.ProductResponse;
import com.example.quickpic_app.models.ProductsResponse;
import com.example.quickpic_app.models.SummaryRequest;
import com.example.quickpic_app.models.SummaryResponse;

import java.util.HashMap;
import java.util.Map;

import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.Call;
import retrofit2.http.Query;

public interface ApiService {

    @POST("/v1/users/signin")
    Call<LoginResult> signInUser(@Body HashMap<String, String> map);

    @POST("/v1/users/signup")
    Call<Void> signUpUser(@Body HashMap<String, String> map);

    @POST("v1/products")
    Call<ProductResponse> addProduct(@Header("x-access-token") String token, @Body Product product);

    @GET("v1/products")
    Call<ProductsResponse> getProducts(@Header("x-access-token") String token);

    @Headers({"Content-Type: application/json"})
    @POST("/v1/products/detect")
    Call<BarcodeDetectionResponse> detectBarcode(@Header("x-access-token") String token, @Body BarcodeDetectionRequest barcodeDetectionRequest);

    @DELETE("/v1/products")
    Call<Void> deleteProduct(@Header("x-access-token") String token, @Query("id") String productId);

    @POST("/v1/products/summarize")
    Call<SummaryResponse> getSummary(@Header("x-access-token") String token, @Body Map<String, String> productIdMap);
}