package com.example.healthy.model;

public class Food {
    private String name;
    private Float calories;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Float getCalories() {
        return calories;
    }

    public void setCalories(Float calories) {
        this.calories = calories;
    }

    public Food(String name, Float calories) {
        this.name = name;
        this.calories = calories;
    }
}
