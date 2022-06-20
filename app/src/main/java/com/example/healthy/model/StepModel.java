package com.example.healthy.model;

public class StepModel {
    private int id;
    private String steps;
    private String kcal;
    private String distance;
    private String date;

    public StepModel() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSteps() {
        return steps;
    }

    public void setSteps(String steps) {
        this.steps = steps;
    }

    public String getKcal() {
        return kcal;
    }

    public void setKcal(String kcal) {
        this.kcal = kcal;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public StepModel(String steps, String kcal, String distance, String date) {
        this.steps = steps;
        this.kcal = kcal;
        this.distance = distance;
        this.date = date;
    }

    public StepModel(int id, String steps, String kcal, String distance, String date) {
        this.id = id;
        this.steps = steps;
        this.kcal = kcal;
        this.distance = distance;
        this.date = date;
    }
}
