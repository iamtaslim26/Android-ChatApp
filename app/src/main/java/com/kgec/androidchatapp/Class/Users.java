package com.kgec.androidchatapp.Class;

public class Users {

    private String ImageUrl,uid,username,status,country,userstatus,search;

    public Users() {
    }

    public Users(String imageUrl, String uid, String username, String status, String country,String userstatus,String search) {
        ImageUrl = imageUrl;
        this.uid = uid;
        this.username = username;
        this.status = status;
        this.country = country;
        this.userstatus=userstatus;
        this.search=search;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getUserstatus() {
        return userstatus;
    }

    public void setUserstatus(String userstatus) {
        this.userstatus = userstatus;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }
}
