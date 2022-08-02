package com.example.democrud.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class APIService {
    @Autowired
    private RestTemplate restTemplate;

    public String consumeAPI(String url)
    {
        return restTemplate.getForObject(url,String.class);
    }
}
