package com.pulmuone.demo.api.config;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.apache.commons.lang.StringUtils;

import static org.springframework.web.cors.CorsConfiguration.ALL;

@Slf4j
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Value("${spring.profiles.active}")
    private String activeProfile;

    @Bean
    public CompositeConfiguration configuration() {
        CompositeConfiguration config = new CompositeConfiguration();

        try {
            config.addConfiguration(new PropertiesConfiguration("classpath:application"
                    + (!StringUtils.equals(activeProfile, "local") ? "-" + activeProfile : "")
                    + ".properties"));
        } catch (ConfigurationException e) {
            //log.warn("Unexpected exception:", e);
        }

        return config;
    }

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:/message/message");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
    }

    @Bean
    public SessionLocaleResolver localeResolver() {
        return new SessionLocaleResolver();
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins(ALL)
                        .allowedMethods(ALL)
                        .allowedHeaders(ALL)
                        .allowCredentials(true);
            }
        };
    }

}
