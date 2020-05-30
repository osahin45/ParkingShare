package com.example.android.parkingshare.Utils;

import android.net.Uri;

public class Users {
   private String Ad;
   private String Soyad;
   private String email;

   private String plaka;
   private String username;
   private String photoUrl;

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public Users() {
    }

    public Users(String ad, String soyad, String email, String plaka, String username) {
        Ad = ad;
        Soyad = soyad;
        this.email = email;
        this.plaka = plaka;
        this.username = username;
    }

    public String getAd() {
        return Ad;
    }

    public void setAd(String ad) {
        Ad = ad;
    }

    public String getSoyad() {
        return Soyad;
    }

    public void setSoyad(String soyad) {
        Soyad = soyad;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }



    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getPlaka() {
        return plaka;
    }

    public void setPlaka(String plaka) {
        this.plaka = plaka;
    }

    @Override
    public String toString() {
        return "Users{" +
                "Ad='" + Ad + '\'' +
                ", Soyad='" + Soyad + '\'' +
                ", email='" + email + '\'' +
                ", plaka='" + plaka + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
