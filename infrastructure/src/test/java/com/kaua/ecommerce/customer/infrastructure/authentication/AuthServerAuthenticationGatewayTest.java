package com.kaua.ecommerce.customer.infrastructure.authentication;

import com.kaua.ecommerce.customer.AbstractRestClientTest;
import com.kaua.ecommerce.customer.infrastructure.authentication.AuthServerAuthenticationGateway.AuthServerAuthenticationResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

class AuthServerAuthenticationGatewayTest extends AbstractRestClientTest {

    @Autowired
    private AuthServerAuthenticationGateway gateway;

    @Test
    void givenAValidParams_whenCallLogin_thenShouldReturnAccessToken() {
        final var aClientId = "aClientId";
        final var aClientSecret = "aClientSecret";

        final var expectedAccessToken = "aAccessToken";

        stubFor(
                post(urlPathEqualTo("/api/oauth2/token"))
                        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON_VALUE))
                        .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=UTF-8"))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .withBody(writeValueAsString(new AuthServerAuthenticationResult(expectedAccessToken))))
        );

        final var aOutput = this.gateway.login(new AuthenticationGateway.ClientCredentialsInput(aClientId, aClientSecret));

        Assertions.assertEquals(expectedAccessToken, aOutput.accessToken());
        Assertions.assertEquals("AuthServer", this.gateway.namespace());
    }
}
