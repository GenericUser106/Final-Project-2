package com.example.stockmonitoring;

public class Article {
    public String articleName;
    public String webAddress;

    public Article() {
    }

    public Article(String articleName, String webAddress) {
        this.articleName = articleName;
        this.webAddress = webAddress;
    }

    public void setArticleName(String articleName) {
        this.articleName = articleName;
    }

    public String getArticleName() {
        return articleName;
    }

    public void setWebAddress(String webAddress) {
        this.webAddress = webAddress;
    }

    public String getWebAddress() {
        return webAddress;
    }
}

