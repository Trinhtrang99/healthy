package com.example.healthy.network.request;

import com.google.gson.annotations.SerializedName;

public class SigupByRequest {
    @SerializedName("email")
    private String email;
    @SerializedName("name")
    private String name;

    public SigupByRequest(String email, String name) {
        this.email = email;
        this.name = name;
    }
}
