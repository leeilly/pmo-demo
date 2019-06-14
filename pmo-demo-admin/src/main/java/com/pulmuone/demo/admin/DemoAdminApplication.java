package com.pulmuone.demo.admin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@Slf4j
@SpringBootApplication(scanBasePackages = { "com.pulmuone.demo" })
public class DemoAdminApplication extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication.run(DemoAdminApplication.class, args);
    }

}