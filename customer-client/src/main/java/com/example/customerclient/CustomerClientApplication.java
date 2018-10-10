package com.example.customerclient;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@SpringBootApplication
public class CustomerClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(CustomerClientApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }
}

@RestController
@Slf4j
class CustomerRestController {

    private final RestTemplate restTemplate;

    @Autowired
    public CustomerRestController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/")
    public List<Customer> getCustomers() {
        log.info("getting customers from the customer-service...");

        ResponseEntity<List<Customer>> customers = restTemplate.exchange("http://localhost:8082",
                HttpMethod.GET, null, new ParameterizedTypeReference<List<Customer>>() {});

        return customers.getBody();
    }

}
