package com.pulmuone.demo.api.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@Slf4j
public class ElasticsearchConfig {


    @Value("${pmo.elasticsearch.host}")
    private String host;

    @Value("${pmo.elasticsearch.port}")
    private int port;

    private static final int TIMEOUT_MILLISEC = 20000;


    @Bean(destroyMethod = "close")
    public RestHighLevelClient restHighLevelClient() {
       //return  new RestHighLevelClient(
       //         RestClient.builder(new HttpHost(host)));

       return new RestHighLevelClient(RestClient.builder(new HttpHost(host, port, "http")).setMaxRetryTimeoutMillis(TIMEOUT_MILLISEC));

    }


}
