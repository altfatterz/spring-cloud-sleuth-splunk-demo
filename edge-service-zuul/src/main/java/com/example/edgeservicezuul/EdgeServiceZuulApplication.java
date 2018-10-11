package com.example.edgeservicezuul;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@EnableZuulProxy
@SpringBootApplication
public class EdgeServiceZuulApplication {

    public static void main(String[] args) {
        SpringApplication.run(EdgeServiceZuulApplication.class, args);
    }
}