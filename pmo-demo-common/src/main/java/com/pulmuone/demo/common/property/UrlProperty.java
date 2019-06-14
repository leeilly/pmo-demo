package com.pulmuone.demo.common.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "pmo.url")
public class UrlProperty {
    @Value("${pmo.url.api}")
    private String api;
    @Value("${pmo.url.admin}")
    private String admin;
}
