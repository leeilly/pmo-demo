package com.pulmuone.demo.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.PropertySource;

@Slf4j
@SpringBootApplication(scanBasePackages = { "com.pulmuone.demo" })
@PropertySource(value = "classpath:aws.properties")
public class DemoApiApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(DemoApiApplication.class, args);
    }


}