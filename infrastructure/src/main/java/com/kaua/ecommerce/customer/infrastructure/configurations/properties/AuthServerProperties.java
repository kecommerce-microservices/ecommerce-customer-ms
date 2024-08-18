package com.kaua.ecommerce.customer.infrastructure.configurations.properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "auth-server")
public class AuthServerProperties implements InitializingBean {

    private static final Logger log = LoggerFactory.getLogger(AuthServerProperties.class);

    private String clientId;
    private String clientSecret;
    private String tokenUri;

    @Override
    public void afterPropertiesSet() {
        log.debug("AuthServerProperties initialized: {}", this);
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getTokenUri() {
        return tokenUri;
    }

    public void setTokenUri(String tokenUri) {
        this.tokenUri = tokenUri;
    }

    @Override
    public String toString() {
        return "AuthServerProperties(" +
                "clientId='" + clientId + '\'' +
                ", tokenUri='" + tokenUri + '\'' +
                ')';
    }
}
