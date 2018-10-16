package com.example.customerclient;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
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

    @Value("${customer.service.host:localhost}")
    private String host;

    @Autowired
    public CustomerRestController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/")
    public List<Customer> getCustomers(HttpServletRequest request) {

        Enumeration<String> headerNames = request.getHeaderNames();
        while(headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            System.out.println(headerName + ":" + request.getHeader(headerName));
        }

        log.info("getting customers from the customer-service...");

        ResponseEntity<List<Customer>> customers = restTemplate.exchange("http://" + host + ":8082",
                HttpMethod.GET, null, new ParameterizedTypeReference<List<Customer>>() {});

        return customers.getBody();
    }

}
