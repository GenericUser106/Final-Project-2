package com.example.stockmonitoring;

public class Stock {
    String stockName;
    String ownedBy;
    float price;


    public Stock() {
    }


    public Stock(String stockName, String ownedBy) {
        this.stockName = stockName;
        this.ownedBy = ownedBy;
    }

    public Stock(String stockName, String ownedBy, float price) {
        this.stockName = stockName;
        this.ownedBy = ownedBy;
        this.price = price;
    }

    public String getStockName() {
        return stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    public String getOwnedBy() {
        return ownedBy;
    }

    public void setOwnedBy(String ownedBy) {
        this.ownedBy = ownedBy;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }
}
