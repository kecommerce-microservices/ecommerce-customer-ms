package com.kaua.ecommerce.customer.infrastructure.gateways;

import com.kaua.ecommerce.customer.application.gateways.AddressGateway;
import com.kaua.ecommerce.customer.infrastructure.configurations.annotations.Addresses;
import com.kaua.ecommerce.customer.infrastructure.configurations.properties.WebClientProperties;
import com.kaua.ecommerce.lib.infrastructure.clients.HttpClientUtils;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Objects;
import java.util.Optional;

@Component
public class AddressGatewayImpl implements AddressGateway, HttpClientUtils {

    public static final String NAMESPACE_NAME = "address";

    private final WebClient webClient;
    private final String addressUrl;

    public AddressGatewayImpl(
            final @Addresses WebClient webClient,
            final @Addresses WebClientProperties properties
    ) {
        this.webClient = Objects.requireNonNull(webClient);
        this.addressUrl = Objects.requireNonNull(properties.getBaseUrl());
    }

    @Bulkhead(name = NAMESPACE_NAME)
    @CircuitBreaker(name = NAMESPACE_NAME)
    @Retry(name = NAMESPACE_NAME)
    @Override
    public Optional<GetAddressByZipCodeResponse> getAddressByZipCode(final String aZipCode) {
        logger().debug("Getting address by zip code [zipCode:{}] [url:{}}", aZipCode, addressUrl);

        final var aOutput = doGet(aZipCode, () -> webClient.get()
                .uri(addressUrl + "/%s/json".formatted(aZipCode))
                .header("Content-Type", "application/json")
                .retrieve()
                .onStatus(isNotFound, notFoundHandler(aZipCode))
                .onStatus(isBadRequest, badRequestHandler(aZipCode))
                .onStatus(is5xx, a5xxHandler(aZipCode))
                .bodyToMono(AddressResponse.class)
                .block());

        final var aResponse = aOutput.map(r -> new GetAddressByZipCodeResponse(
                r.cep(),
                r.localidade(),
                r.logradouro(),
                r.bairro(),
                r.uf()
        ));

        logger().info("Address found [zipCode:{}]", aZipCode);

        return aResponse;
    }

    @Override
    public String namespace() {
        return NAMESPACE_NAME;
    }

    @Override
    public Logger logger() {
        return LoggerFactory.getLogger(AddressGatewayImpl.class);
    }

    public record AddressResponse(
            String cep,
            String logradouro,
            String bairro,
            String localidade,
            String uf
    ) {}
}
