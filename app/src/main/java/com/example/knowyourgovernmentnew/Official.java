package com.example.knowyourgovernmentnew;

import java.io.Serializable;

public class Official implements Serializable {
    private String name;
    private String address;
    private String party;
    private String phoneno;
    private String websiteurl;
    private String email;
    private String photoAddress;
    private String postname;
    private String fb;
    private String twtr;
    private String youtube;
    private String locationtext;
    //NEED SOCIAL MEDIA

    public Official(){

    }
    public Official(String name, String postname){
        this.name = name;
        this.postname = postname;
    }
    public Official(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getParty() {
        return party;
    }

    public void setParty(String party) {
        this.party = party;
    }

    public String getPhoneno() {
        return phoneno;
    }

    public void setPhoneno(String phoneno) {
        this.phoneno = phoneno;
    }

    public String getWebsiteurl() {
        return websiteurl;
    }

    public void setWebsiteurl(String websiteurl) {
        this.websiteurl = websiteurl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhotoAddress() {
        return photoAddress;
    }

    public void setPhotoAddress(String photoAddress) {
        this.photoAddress = photoAddress;
    }

    public String getPostname() {
        return postname;
    }

    public void setPostname(String postname) {
        this.postname = postname;
    }

    public String getfb() {
        return fb;
    }

    public void setfb(String fb) {
        this.fb = fb;
    }

    public String getTwtr() {
        return twtr;
    }

    public void setTwtr(String twtr) {
        this.twtr = twtr;
    }

    public String getYoutube() {
        return youtube;
    }

    public void setYoutube(String youtube) {
        this.youtube = youtube;
    }

    public void setlocationtext(String locationtext) {
        this.locationtext = locationtext;
    }
    public String getlocationtext(){return locationtext;}
}
