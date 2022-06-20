package com.example.healthy.network;

public class ApiUtils {
    private ApiUtils() {
    }

    public static ApiService getApiService() {
        return ApiClient.getClient("https://www.googleapis.com/youtube/v3/").create(ApiService.class);
    }

    public static ApiService getApiServiceEnv() {
        return ApiClient.getClient("https://api.waqi.info/").create(ApiService.class);
    }

    public static ApiService getApiServiceCalories() {
        return ApiClient.getClient("https://api.edamam.com/api/").create(ApiService.class);
    }

}
