package com.example.healthy.model;

public class Event {
    private String name;
    private int img;
    private String playId;

    public String getPlayId() {
        return playId;
    }

    public void setPlayId(String playId) {
        this.playId = playId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }

    public Event(String name, int img, String playId) {
        this.name = name;
        this.img = img;
        this.playId = playId;
    }
}
