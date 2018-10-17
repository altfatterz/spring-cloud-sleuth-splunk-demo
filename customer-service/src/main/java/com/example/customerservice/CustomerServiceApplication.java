package com.example.customerservice;

import brave.propagation.CurrentTraceContext;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication
@RestController
@Slf4j
public class CustomerServiceApplication {

    private static final Logger audit = LoggerFactory.getLogger("audit-logger");

    public static void main(String[] args) {
        SpringApplication.run(CustomerServiceApplication.class, args);
    }

    @Bean
    public CurrentTraceContext currentTraceContext() {
        return CustomSlf4jCurrentTraceContext.create();
    }

    @GetMapping("/")
    public List<Customer> getCustomers() {
        log.info("retrieving customers...");

        List<Customer> customers = Arrays.asList(new Customer("John", "Doe"));

        audit.info("Retrieving customers:" + customers);

        return customers;
    }

}




