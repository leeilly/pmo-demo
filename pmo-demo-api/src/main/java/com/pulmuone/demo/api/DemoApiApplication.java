package com.pulmuone.demo.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@Slf4j
@SpringBootApplication(scanBasePackages = { "com.pulmuone.demo" })
public class DemoApiApplication extends SpringBootServletInitializer {

    public static final String APPLICATION_LOCATIONS = "spring.config.location="
            + "classpath:application.properties,"
            + "classpath:aws.properties";

    public static void main(String[] args) {

        new SpringApplicationBuilder(DemoApiApplication.class)
                .properties(APPLICATION_LOCATIONS)
                .run(args);
    }


}