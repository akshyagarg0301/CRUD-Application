package com.example.democrud;

import com.example.democrud.entity.Category;
import com.example.democrud.entity.Content;
import com.example.democrud.entity.Newspaper;
import com.google.common.cache.Cache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfig {

    @Bean
    public CacheStore<Newspaper> NewspaperCache() {
        return new CacheStore<Newspaper>(10, TimeUnit.SECONDS);
    }

    @Bean
    public CacheStore<Content> ContentCache(){ return new CacheStore<Content>(10,TimeUnit.SECONDS);}

    @Bean
    public CacheStore<Category>CategoryCache(){return new CacheStore<Category>(10,TimeUnit.SECONDS);}

}