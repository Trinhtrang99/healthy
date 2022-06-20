package com.example.healthy.model;

public class Healthy {
    public int id;
    public String step;
    public String water;
    public String sleep;
    public String heart;
    public String bool;
    public String BMI;
    public String Calorie;
    public String distance_step;
    public String date;
    public String energy;


    public Healthy() {
    }

    public Healthy( String step, String water, String sleep, String heart, String bool, String BMI, String calorie, String distance_step, String date,int id,String energy) {
        this.id = id;
        this.step = step;
        this.water = water;
        this.sleep = sleep;
        this.heart = heart;
        this.bool = bool;
        this.BMI = BMI;
        Calorie = calorie;
        this.distance_step = distance_step;
        this.date = date;
        this.energy = energy;
    }

    public Healthy(String step, String water, String sleep, String heart, String bool, String BMI, String calorie, String distance_step, String date,String energy) {
        this.step = step;
        this.water = water;
        this.sleep = sleep;
        this.heart = heart;
        this.bool = bool;
        this.BMI = BMI;
        Calorie = calorie;
        this.distance_step = distance_step;
        this.date = date;
        this.energy = energy;
    }
}
