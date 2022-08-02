package com.example.democrud.service;

import com.example.democrud.Repository.DaoForContent;
import com.example.democrud.entity.Content;
import com.example.democrud.entity.ContentList;
import com.example.democrud.entity.Newspaper;
import com.example.democrud.entity.NewspaperList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Service
public class ContentService {

    @Autowired
    DaoForContent daoForContent;


    public ContentList getAll()
    {
        return daoForContent.findAll();
    }


    public Content getByID(@PathVariable int articleID)
    {
        return daoForContent.findByID(articleID);
    }


    public String delete(@PathVariable int articleID)
    {
        return daoForContent.deletebyID(articleID);
    }


    public String insert(@RequestBody Content content)
    {
        return daoForContent.insert(content);
    }


    public String update(@RequestBody Content content, @PathVariable int articleID)
    {
        return  daoForContent.update(content,articleID);
    }



}
