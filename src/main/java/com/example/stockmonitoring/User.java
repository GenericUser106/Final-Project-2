package com.example.stockmonitoring;

import android.graphics.Bitmap;

import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class User {

    private String email;
    private String username;
    private Bitmap userImage;
    private ArrayList<Stock> stocks;

    public User(String email, String username)
    {
        this.email = email;
        this.username = username;
        stocks = new ArrayList<>();
    }

    public User()
    {
        email="";
        username="";
        stocks = new ArrayList<>();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Bitmap getUserImage() {
        return userImage;
    }

    public void setUserImage(Bitmap userImage) {
        this.userImage = userImage;
    }

    public ArrayList<Stock> getStocks() {
        return stocks;
    }

    public void setStocks(ArrayList<Stock> stocks) {
        this.stocks = stocks;
    }

    public void addStocks(Stock stock)
    {
        stocks.add(stock);
    }
}