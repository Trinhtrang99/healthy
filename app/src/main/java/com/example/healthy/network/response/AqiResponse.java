package com.example.healthy.network.response;

import com.google.gson.annotations.SerializedName;

public class AqiResponse {
    @SerializedName("data")
    public Data data;

    public class Data {
        @SerializedName("aqi")
        public int aqi;
    }
}
