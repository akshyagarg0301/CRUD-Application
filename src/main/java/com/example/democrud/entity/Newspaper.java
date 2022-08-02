package com.example.democrud.entity;

import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.Date;
@RedisHash("Newspaper")
public class Newspaper implements Serializable {
    private String name;
    private Date date;
    private String language;
    private int categoryID;
    private int newspprID;
    public  Newspaper()
    {

    }
    public Newspaper(String name, Date date, String language, int categoryID, int newspprID) {
        this.name = name;
        this.date = date;
        this.language = language;
        this.categoryID = categoryID;
        this.newspprID = newspprID;
    }



    public void setName(String name) {
        this.name = name;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setCategoryID(int categoryID) {
        this.categoryID = categoryID;
    }

    public void setNewspprID(int newspprID) {
        this.newspprID = newspprID;
    }

    public String getName() {
        return name;
    }

    public Date getDate() {
        return date;
    }

    public String getLanguage() {
        return language;
    }

    public int getCategoryID() {
        return categoryID;
    }

    public int getNewspprID() {
        return newspprID;
    }


}
