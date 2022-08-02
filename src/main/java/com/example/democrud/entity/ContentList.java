package com.example.democrud.entity;

import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.List;
@RedisHash("ContentList")
public class ContentList implements Serializable {
    List<Content> list;

    public ContentList(List<Content> list) {
        this.list = list;
    }

    public List<Content> getList() {
        return list;
    }

    public void setList(List<Content> list) {
        this.list = list;
    }
}
