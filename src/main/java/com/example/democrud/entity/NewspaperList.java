package com.example.democrud.entity;

import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.List;

@RedisHash("NewspaperList")
public class NewspaperList implements Serializable  {
    List<Newspaper> list;

    public NewspaperList(List<Newspaper> list) {
        this.list = list;
    }

    public List<Newspaper> getList() {
        return list;
    }

    public void setList(List<Newspaper> list) {
        this.list = list;
    }
}
