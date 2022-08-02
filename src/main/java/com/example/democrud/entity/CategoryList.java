package com.example.democrud.entity;

import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.List;

@RedisHash("CategoryList")
public class CategoryList implements Serializable {
    private List<Category> list;

    public CategoryList(List<Category> list) {
        this.list = list;
    }

    public List<Category> getList() {
        return list;
    }

    public void setList(List<Category> list) {
        this.list = list;
    }
}
