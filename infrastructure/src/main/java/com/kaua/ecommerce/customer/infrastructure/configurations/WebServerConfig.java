package com.kaua.ecommerce.customer.infrastructure.configurations;

import com.kaua.ecommerce.customer.infrastructure.authentication.RefreshClientCredentials;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration(proxyBeanMethods = false)
@ComponentScan("com.kaua.ecommerce.customer")
@EnableScheduling
public class WebServerConfig {

    @Bean
    @Profile("!test-integration")
    ApplicationListener<ContextRefreshedEvent> refreshClientCredentials(final RefreshClientCredentials refreshClientCredentials) {
        return event -> refreshClientCredentials.refresh();
    }
}
