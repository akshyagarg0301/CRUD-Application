package com.example.democrud.controllers;

import com.example.democrud.entity.Category;
import com.example.democrud.entity.CategoryList;
import com.example.democrud.Repository.DaoForCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

@RestController
public class CategoryController {
    @Autowired
    DaoForCategory dao;

    @GetMapping("/category/getAll")
    public CategoryList getAll()
    {
        return dao.findAll();
    }

    @GetMapping("/category/getByID/{CategoryID}")
    @Cacheable(key="#CategoryID",value ="Category")
    public Category getByID(@PathVariable int CategoryID)
    {
        return dao.findByID(CategoryID);
    }

    @DeleteMapping("/category/deletebyID/{CategoryID}")
    public String deleteByID(@PathVariable int CategoryID)
    {
        return dao.deletebyID(CategoryID);
    }

    @PostMapping("/category/insert")
    public String insert(@RequestBody Category category)
    {
        return dao.insert(category);
    }

    @PutMapping("/category/update/{id}")
    public String update(@RequestBody Category category,@PathVariable int id)
    {
        return dao.update(category,id);
    }


}
