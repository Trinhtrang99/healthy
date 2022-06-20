package com.example.healthy.model;

public class Information {
    private int color;
    private int drawable;
    private String index;
    private  String name;

    public int getDrawable() {
        return drawable;
    }

    public void setDrawable(int drawable) {
        this.drawable = drawable;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public Information(int color, int drawable, String index, String name) {
        this.color = color;
        this.drawable = drawable;
        this.index = index;
        this.name = name;
    }
}
