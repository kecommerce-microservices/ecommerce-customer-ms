package com.kaua.ecommerce.customer.infrastructure.authentication;

public interface AuthenticationGateway {

    AuthenticationResult login(ClientCredentialsInput input);

    record AuthenticationResult(String accessToken) {}

    record ClientCredentialsInput(String clientId, String clientSecret) {}
}
