package com.example.democrud.controllers;

import com.example.democrud.entity.Content;
import com.example.democrud.entity.ContentList;
import com.example.democrud.Repository.DaoForContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class ContentController {

    @Autowired
    DaoForContent dao;

    @GetMapping("/content/getAll")
    public ContentList getAll() {
        return dao.findAll();
    }

    @GetMapping("/content/getByID/{ArticleID}")
    public Content getByID(@PathVariable int ArticleID) {
        return dao.findByID(ArticleID);
    }

    @DeleteMapping("/content/deletebyID/{ArticleID}")
    public String deleteByID(@PathVariable int ArticleID) {
        return dao.deletebyID(ArticleID);
    }

    @PostMapping("/content/insert")
    public String insert(@RequestBody Content content) {
        return dao.insert(content);
    }

    @PutMapping("/content/update/{id}")
    public String update(@RequestBody Content content, @PathVariable int id) {
        return dao.update(content, id);
    }
}
