package com.example.healthy.model;

public class Meal {
    public int id;
    public String type;
    public String name;
    public String kcal;

    public Meal() {
    }

    public Meal(String type, String name, String kcal) {
        this.type = type;
        this.name = name;
        this.kcal = kcal;
    }

    public Meal(int id, String type, String name, String kcal) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.kcal = kcal;
    }
}
