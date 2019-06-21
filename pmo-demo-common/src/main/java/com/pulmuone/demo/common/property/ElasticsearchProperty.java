package com.pulmuone.demo.common.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "pmo.elasticsearch")
public class ElasticsearchProperty {

    @Value("${pmo.elasticsearch.host}")
    private String host;
    @Value("${pmo.elasticsearch.port}")
    private int port;
    @Value("${pmo.elasticsearch.dic.path}")
    private String dictionaryTempPath;

}
