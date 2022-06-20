package com.example.healthy.sqlite;

public class Infomation {

    public String name;
    public String birthday;
    public String gender;
    public String height;
    public String weight;
    public String occu;
    public int id;

    public Infomation() {
    }

    public Infomation(String name, String birthday, String gender, String height, String weight, String occu) {
        this.name = name;
        this.birthday = birthday;
        this.gender = gender;
        this.height = height;
        this.weight = weight;
        this.occu = occu;
    }

    public Infomation(String name, String birthday, String gender, String height, String weight, String occu, int id) {
        this.name = name;
        this.birthday = birthday;
        this.gender = gender;
        this.height = height;
        this.weight = weight;
        this.occu = occu;
        this.id = id;
    }
}
