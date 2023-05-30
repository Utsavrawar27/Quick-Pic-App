package com.example.quickpic_app.API;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL = "http://quickpic.us-east-1.elasticbeanstalk.com/";
    private static Retrofit retrofit;
    private static ApiService apiService;

    public static synchronized ApiService getInstance() {
        if (apiService == null) {
            if (retrofit == null) {
                retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
            }
            apiService = retrofit.create(ApiService.class);
        }
        return apiService;
    }

    public ApiService getApiService() {
        return apiService;
    }
}