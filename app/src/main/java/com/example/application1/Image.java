package com.example.application1;

public class Image {
    String full_name, image_url, timestamo, user_id;

    public Image() {

    }

    public Image(String full_name, String image_url, String timestamo, String user_id) {
        this.full_name = full_name;
        this.image_url = image_url;
        this.timestamo = timestamo;
        this.user_id = user_id;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getTimestamo() {
        return timestamo;
    }

    public void setTimestamo(String timestamo) {
        this.timestamo = timestamo;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
