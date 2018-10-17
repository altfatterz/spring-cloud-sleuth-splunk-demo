package com.example.edgeservicezuul;

import brave.propagation.CurrentTraceContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableZuulProxy
public class EdgeServiceZuulApplication {

    public static void main(String[] args) {
        SpringApplication.run(EdgeServiceZuulApplication.class, args);
    }

    @Bean
    public CurrentTraceContext currentTraceContext() {
        return CustomSlf4jCurrentTraceContext.create();
    }

}
