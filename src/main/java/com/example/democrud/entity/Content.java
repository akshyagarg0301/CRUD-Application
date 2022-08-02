package com.example.democrud.entity;


import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@RedisHash("Content")
public class Content implements Serializable {
    private int articleID;
    private int categoryID;
    private int newspprID;
    private String article;
    public Content()
    {

    }
    public Content(int articleID, int categoryID, int newspprID, String article) {
        this.articleID = articleID;
        this.categoryID = categoryID;
        this.newspprID = newspprID;
        this.article = article;
    }

    public int getArticleID() {
        return articleID;
    }

    public void setArticleID(int articleID) {
        this.articleID = articleID;
    }

    public int getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(int categoryID) {
        this.categoryID = categoryID;
    }

    public int getNewspprID() {
        return newspprID;
    }

    public void setNewspprID(int newspprID) {
        this.newspprID = newspprID;
    }

    public String getArticle() {
        return article;
    }

    public void setArticle(String article) {
        this.article = article;
    }



}
