package com.example.democrud.controllers;

import com.example.democrud.service.APIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;

@org.springframework.web.bind.annotation.RestController
public class APIController {
    @Autowired
    APIService service;

    String url="http://stage-api-news.dailyhunt.in/api/v2/cruise/info";
    @GetMapping("/dailyhunt/info")
    public String getData() throws InterruptedException {
       // Thread.sleep(30000);
        return service.consumeAPI(url);
    }
}
