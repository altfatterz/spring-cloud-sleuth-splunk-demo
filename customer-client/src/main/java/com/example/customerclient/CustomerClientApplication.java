package com.example.customerclient;

import brave.propagation.CurrentTraceContext;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    @Bean
    public CurrentTraceContext currentTraceContext() {
        return CustomSlf4jCurrentTraceContext.create();
    }

}

@RestController
@Slf4j
class CustomerRestController {

    private static final Logger audit = LoggerFactory.getLogger("audit-logger");

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

        audit.info(String.format("%s request to %s", request.getMethod(), request.getRequestURL().toString()));

        ResponseEntity<List<Customer>> customers = restTemplate.exchange("http://" + host + ":8082",
                HttpMethod.GET, null, new ParameterizedTypeReference<List<Customer>>() {});

        return customers.getBody();
    }

}
