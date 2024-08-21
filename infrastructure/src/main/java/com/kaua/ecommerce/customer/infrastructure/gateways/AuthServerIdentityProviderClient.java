package com.kaua.ecommerce.customer.infrastructure.gateways;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaua.ecommerce.customer.application.gateways.IdentityProviderGateway;
import com.kaua.ecommerce.customer.domain.customer.idp.User;
import com.kaua.ecommerce.customer.domain.customer.idp.UserId;
import com.kaua.ecommerce.customer.infrastructure.authentication.GetClientCredentials;
import com.kaua.ecommerce.customer.infrastructure.configurations.annotations.Users;
import com.kaua.ecommerce.customer.infrastructure.configurations.properties.WebClientProperties;
import com.kaua.ecommerce.lib.infrastructure.clients.HttpClientUtils;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
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

    public static final String NAMESPACE_NAME = "users";

    private static final Logger log = LoggerFactory.getLogger(AuthServerIdentityProviderClient.class);
    private static final String CREATE_USER_ACTION = "creating user";
    private static final String DELETE_USER_ACTION = "deleting user";

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

    @Bulkhead(name = NAMESPACE_NAME)
    @CircuitBreaker(name = NAMESPACE_NAME)
    @Retry(name = NAMESPACE_NAME)
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

    // O certo seria termos o bulkhead e circuit breaker também, assim não sobrecarregamos o AuthServer e evitamos que ele fique indisponível
    // mas consequentemente teríamos que tratar os erros que podem ocorrer por causa disso e uma forma de excluir o user do banco de dados
    @Retry(name = NAMESPACE_NAME)
    @Override
    public void deleteOfUserId(final UserId userId) {
        final var aUserId = userId.value().toString();

        log.info("Deleting user in AuthServer [userId:{}]", aUserId);

        doDelete(aUserId, () -> webClient
                .delete()
                .uri(this.userUrl + "/delete/" + aUserId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + getClientCredentials.retrieve())
                .retrieve()
                .onStatus(isBadRequest, badRequestHandler(aUserId, DELETE_USER_ACTION))
                .onStatus(is5xx, a5xxHandler(aUserId, DELETE_USER_ACTION))
                .bodyToMono(Void.class)
                .block());

        log.info("User deleted in AuthServer [userId:{}]", aUserId);
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
