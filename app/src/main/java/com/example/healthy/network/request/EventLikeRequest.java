package com.example.healthy.network.request;

public class EventLikeRequest {
    private Integer like;

    public EventLikeRequest(Integer like) {
        this.like = like;
    }

    public Integer getLike() {
        return like;
    }

    public void setLike(Integer like) {
        this.like = like;
    }
}
