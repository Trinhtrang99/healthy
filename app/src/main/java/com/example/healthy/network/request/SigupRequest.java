package com.example.healthy.network.request;

import java.util.List;

public class SigupRequest {
    private String birthdate;
    private Integer gender;
    private String introduction;
    private Long prefecture_id;
    private Long city_id;
    private String password;
    private List<Long> categories;


    public SigupRequest( String birthdate, Integer gender,
                        String introduction, Long prefecture_id,
                        Long city_id, String password, List<Long> categories) {
        this.birthdate = birthdate;
        this.gender = gender;
        this.introduction = introduction;
        this.prefecture_id = prefecture_id;
        this.city_id = city_id;
        this.password = password;
        this.categories = categories;

    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public Long getPrefecture_id() {
        return prefecture_id;
    }

    public void setPrefecture_id(Long prefecture_id) {
        this.prefecture_id = prefecture_id;
    }

    public Long getCity_id() {
        return city_id;
    }

    public void setCity_id(Long city_id) {
        this.city_id = city_id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Long> getCategories() {
        return categories;
    }

    public void setCategories(List<Long> categories) {
        this.categories = categories;
    }
}
