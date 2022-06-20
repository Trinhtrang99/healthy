package com.example.healthy.model;

public class Protein {
    public int id;
    public String protein;
    public String fat;
    public String kcal;
    public String date;

    public Protein() {
    }

    public Protein(String protein, String fat, String kcal, String date) {
        this.protein = protein;
        this.fat = fat;
        this.kcal = kcal;
        this.date = date;
    }

    public Protein(int id, String protein, String fat, String kcal, String date) {
        this.id = id;
        this.protein = protein;
        this.fat = fat;
        this.kcal = kcal;
        this.date = date;
    }
}
