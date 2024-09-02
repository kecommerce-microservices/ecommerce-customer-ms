package com.kaua.ecommerce.customer.infrastructure.configurations;

import com.kaua.ecommerce.customer.infrastructure.configurations.annotations.Addresses;
import com.kaua.ecommerce.customer.infrastructure.configurations.annotations.AuthServer;
import com.kaua.ecommerce.customer.infrastructure.configurations.annotations.Users;
import com.kaua.ecommerce.customer.infrastructure.configurations.properties.WebClientProperties;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Configuration(proxyBeanMethods = false)
public class WebClientConfig {

    @Bean
    @ConfigurationProperties(prefix = "web-client.auth-server")
    @AuthServer
    public WebClientProperties authServerWebClientProperties() {
        return new WebClientProperties();
    }

    @Bean
    @ConfigurationProperties(prefix = "web-client.users")
    @Users
    public WebClientProperties usersWebClientProperties() {
        return new WebClientProperties();
    }

    @Bean
    @ConfigurationProperties(prefix = "web-client.address")
    @Addresses
    public WebClientProperties addressWebClientProperties() {
        return new WebClientProperties();
    }

    @Bean
    @AuthServer
    public WebClient authServerWebClient(@AuthServer final WebClientProperties authServerProperties) {
        return buildWebClient(authServerProperties);
    }

    @Bean
    @Users
    public WebClient usersWebClient(@Users final WebClientProperties usersProperties) {
        return buildWebClient(usersProperties);
    }

    @Bean
    @Addresses
    public WebClient addressWebClient(@Addresses final WebClientProperties addressProperties) {
        return buildWebClient(addressProperties);
    }

    private WebClient buildWebClient(final WebClientProperties properties) {
        final var aHttpClient =  HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, properties.getConnectTimeout())
                .responseTimeout(Duration.ofMillis(properties.getReadTimeout()))
                .doOnConnected(conn ->
                        conn.addHandlerLast(new ReadTimeoutHandler(properties.getReadTimeout()))
                                .addHandlerLast(new WriteTimeoutHandler(properties.getReadTimeout())));

        return WebClient
                .builder()
                .clientConnector(new ReactorClientHttpConnector(aHttpClient))
                .baseUrl(properties.getBaseUrl())
                .build();
    }
}
