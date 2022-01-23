package com.example.instaprofilepicdownloader.Models;

public class ModelClass {

    String profile_pic_url, profile_pic_url_hd;

    public ModelClass(String profile_pic_url, String profile_pic_url_hd) {
        this.profile_pic_url = profile_pic_url;
        this.profile_pic_url_hd = profile_pic_url_hd;
    }

    public String getProfile_pic_url() {
        return profile_pic_url;
    }

    public void setProfile_pic_url(String profile_pic_url) {
        this.profile_pic_url = profile_pic_url;
    }

    public String getProfile_pic_url_hd() {
        return profile_pic_url_hd;
    }

    public void setProfile_pic_url_hd(String profile_pic_url_hd) {
        this.profile_pic_url_hd = profile_pic_url_hd;
    }
}
