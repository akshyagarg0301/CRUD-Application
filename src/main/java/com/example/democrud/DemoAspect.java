package com.example.democrud;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Aspect
@Configuration
public class DemoAspect {

    private long start;
    private long end;
    @Before("execution(* com.example.democrud.controllers.*.*(..))")
    public void sayHi()
    {
        start=System.currentTimeMillis();
    }

    @After("execution(* com.example.democrud.controllers.*.*(..))")
    public void sayNo()
    {
        end=System.currentTimeMillis();
        long ans=end-start;
        System.out.println("Method response time"+ans);
    }
}
