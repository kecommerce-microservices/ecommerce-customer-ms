package com.kaua.ecommerce.customer.infrastructure.authentication;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaua.ecommerce.customer.infrastructure.configurations.annotations.AuthServer;
import com.kaua.ecommerce.customer.infrastructure.configurations.properties.AuthServerProperties;
import com.kaua.ecommerce.lib.infrastructure.clients.HttpClientUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Base64;
import java.util.Objects;

@Component
public class AuthServerAuthenticationGateway implements AuthenticationGateway, HttpClientUtils {

    private static final Logger log = LoggerFactory.getLogger(AuthServerAuthenticationGateway.class);

    private final WebClient webClient;
    private final String tokenUri;

    public AuthServerAuthenticationGateway(
            @AuthServer final WebClient webClient,
            final AuthServerProperties authServerProperties
    ) {
        this.webClient = Objects.requireNonNull(webClient);
        this.tokenUri = Objects.requireNonNull(authServerProperties.getTokenUri());
    }

    @Override
    public AuthenticationResult login(final ClientCredentialsInput input) {
        log.debug("Creating client credentials [clientId:{}]", input.clientId());

        final var aCredentials = input.clientId() + ":" + input.clientSecret();
        final var aEncodedCredentials = Base64.getEncoder().encodeToString(aCredentials.getBytes());

        final var aOutput = doPost(() -> webClient.post()
                .uri(this.tokenUri)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .header(HttpHeaders.AUTHORIZATION, "Basic " + aEncodedCredentials)
                .body(BodyInserters.fromFormData("grant_type", "client_credentials"))
                .retrieve()
                .onStatus(isBadRequest, badRequestHandler(input.clientId()))
                .onStatus(is5xx, a5xxHandler(input.clientId()))
                .bodyToMono(AuthServerAuthenticationResult.class)
                .block());

        log.info("Client credentials created [clientId:{}]", input.clientId());

        return new AuthenticationResult(aOutput.accessToken);
    }

    @Override
    public String namespace() {
        return "AuthServer";
    }

    @Override
    public Logger logger() {
        return log;
    }

    record AuthServerAuthenticationResult(@JsonProperty("access_token") String accessToken) {
    }
}
