package com.kaua.ecommerce.customer.infrastructure.authentication;

import com.kaua.ecommerce.customer.infrastructure.authentication.AuthenticationGateway.ClientCredentialsInput;
import com.kaua.ecommerce.customer.infrastructure.configurations.properties.AuthServerProperties;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class ClientCredentialsManager implements GetClientCredentials, RefreshClientCredentials {

    private final AtomicReference<ClientCredentials> credentials = new AtomicReference<>();

    private final AuthenticationGateway authenticationGateway;
    private final AuthServerProperties authServerProperties;

    public ClientCredentialsManager(
            final AuthenticationGateway authenticationGateway,
            final AuthServerProperties authServerProperties
    ) {
        this.authenticationGateway = Objects.requireNonNull(authenticationGateway);
        this.authServerProperties = Objects.requireNonNull(authServerProperties);
    }

    @Override
    public String retrieve() {
        return this.credentials.get().accessToken();
    }

    @Override
    public void refresh() {
        final var aResult = this.authenticationGateway
                .login(new ClientCredentialsInput(clientId(), clientSecret()));

        this.credentials.set(new ClientCredentials(aResult.accessToken()));
    }

    private String clientId() {
        return this.authServerProperties.getClientId();
    }

    private String clientSecret() {
        return this.authServerProperties.getClientSecret();
    }

    record ClientCredentials(String accessToken) {}
}
