package com.example.healthy.network.response;

import com.google.gson.annotations.SerializedName;

public class CaloriesResponse {
    @SerializedName("calories")
    public float calories;
    @SerializedName("totalNutrients")
    public totalNutrients totalNutrients;

    public class totalNutrients {
        @SerializedName("FAT")
        public Detail fat;
        @SerializedName("PROCNT")
        public Detail protein;
        @SerializedName("ENERC_KCAL")
        public Detail ENERC_KCAL;
    }

    public class Detail {
        @SerializedName("label")
        public String label;
        @SerializedName("quantity")
        public float quantity;
        @SerializedName("unit")
        public String Unit;
    }
}
