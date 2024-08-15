package com.kaua.ecommerce.customer.infrastructure.configurations;

import com.kaua.ecommerce.customer.infrastructure.jdbc.DatabaseClient;
import com.kaua.ecommerce.customer.infrastructure.jdbc.JdbcClientAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.simple.JdbcClient;

@Configuration(proxyBeanMethods = false)
public class JdbcConfig {

    @Bean
    public DatabaseClient databaseClient(final JdbcClient jdbcClient) {
        return new JdbcClientAdapter(jdbcClient);
    }
}
