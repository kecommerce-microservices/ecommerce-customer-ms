package com.kaua.ecommerce.customer.infrastructure.gateways;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaua.ecommerce.customer.application.gateways.IdentityProviderGateway;
import com.kaua.ecommerce.customer.domain.customer.idp.User;
import com.kaua.ecommerce.customer.domain.customer.idp.UserId;
import com.kaua.ecommerce.customer.infrastructure.authentication.GetClientCredentials;
import com.kaua.ecommerce.customer.infrastructure.configurations.annotations.Users;
import com.kaua.ecommerce.customer.infrastructure.configurations.properties.WebClientProperties;
import com.kaua.ecommerce.lib.infrastructure.clients.HttpClientUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.UUID;

@Component
public class AuthServerIdentityProviderClient implements IdentityProviderGateway, HttpClientUtils {

    private static final Logger log = LoggerFactory.getLogger(AuthServerIdentityProviderClient.class);

    public static final String NAMESPACE_NAME = "users";

    private static final String CREATE_USER_ACTION = "creating user";

    private final String userUrl;
    private final WebClient webClient;
    private final GetClientCredentials getClientCredentials;

    public AuthServerIdentityProviderClient(
            @Users final WebClientProperties properties,
            @Users final WebClient webClient,
            final GetClientCredentials getClientCredentials
    ) {
        this.userUrl = Objects.requireNonNull(properties.getBaseUrl());
        this.webClient = Objects.requireNonNull(webClient);
        this.getClientCredentials = Objects.requireNonNull(getClientCredentials);
    }

    @Override
    public UserId create(final User user) {
        final var aCustomerId = user.getCustomerId().value().toString();

        log.info("Creating user in AuthServer [customerId:{}]", aCustomerId);

        final var aMap = new LinkedHashMap<>();
        aMap.put("customer_id", user.getCustomerId().value().toString());
        aMap.put("first_name", user.getName().firstName());
        aMap.put("last_name", user.getName().lastName());
        aMap.put("email", user.getEmail().value());
        aMap.put("password", user.getPassword());

        final var aOutput = doPost(aCustomerId, () -> webClient
                .post()
                .uri(this.userUrl)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + getClientCredentials.retrieve())
                .bodyValue(aMap)
                .retrieve()
                .onStatus(isBadRequest, badRequestHandler(aCustomerId, CREATE_USER_ACTION))
                .onStatus(isUnprocessableEntity, unprocessableEntityHandler(aCustomerId, CREATE_USER_ACTION))
                .onStatus(is5xx, a5xxHandler(aCustomerId, CREATE_USER_ACTION))
                .bodyToMono(CreateUserIdpResult.class)
                .block());

        log.info("User created in AuthServer [customerId:{}] [userId:{}]", aCustomerId, aOutput.userId);

        return new UserId(UUID.fromString(aOutput.userId));
    }

    @Override
    public String namespace() {
        return NAMESPACE_NAME;
    }

    @Override
    public Logger logger() {
        return log;
    }

    record CreateUserIdpResult(@JsonProperty("user_id") String userId) {}
}
