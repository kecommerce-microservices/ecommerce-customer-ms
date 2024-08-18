package com.kaua.ecommerce.customer.infrastructure.gateways;

import com.kaua.ecommerce.customer.AbstractRestClientTest;
import com.kaua.ecommerce.customer.domain.customer.CustomerId;
import com.kaua.ecommerce.customer.domain.customer.idp.User;
import com.kaua.ecommerce.customer.domain.customer.idp.UserId;
import com.kaua.ecommerce.customer.domain.person.Email;
import com.kaua.ecommerce.customer.domain.person.Name;
import com.kaua.ecommerce.customer.infrastructure.authentication.GetClientCredentials;
import com.kaua.ecommerce.lib.domain.exceptions.ValidationException;
import com.kaua.ecommerce.lib.domain.utils.IdentifierUtils;
import com.kaua.ecommerce.lib.domain.validation.Error;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

class AuthServerIdentityProviderClientTest extends AbstractRestClientTest {

    @SpyBean
    private GetClientCredentials getClientCredentials;

    @Autowired
    private AuthServerIdentityProviderClient client;

    @Test
    void givenAValidUser_whenCallCreate_shouldReturnAnIdpUserId() {
        final var aCustomerId = new CustomerId(IdentifierUtils.generateNewUUID());
        final var aName = new Name("John", "Doe");
        final var aEmail = new Email("testes@tes.com");
        final var aPassword = "123456Ab*";

        final var aAccessToken = "accessToken";

        final var aUser = User.newUser(aCustomerId, aName, aEmail, aPassword);

        final var expectedUserId = new UserId(IdentifierUtils.generateNewUUID());

        stubFor(
                post("/api/v1/users")
                        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON_VALUE))
                        .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE))
                        .willReturn(aResponse()
                                .withStatus(201)
                                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .withBody(writeValueAsString(new AuthServerIdentityProviderClient.CreateUserIdpResult(expectedUserId.value().toString()))))
        );

        Mockito.doReturn(aAccessToken).when(getClientCredentials).retrieve();

        final var aOutput = this.client.create(aUser);

        Assertions.assertEquals(expectedUserId.value(), aOutput.value());
        Assertions.assertEquals("users", this.client.namespace());

        verify(1, postRequestedFor(urlEqualTo("/api/v1/users"))
                .withHeader(HttpHeaders.AUTHORIZATION, equalTo("Bearer " + aAccessToken))
                .withRequestBody(equalToJson("""
                        {
                            "customer_id": "%s",
                            "first_name": "John",
                            "last_name": "Doe",
                            "email": "testes@tes.com",
                            "password": "123456Ab*"
                        }
                        """.formatted(aCustomerId.value().toString()))));
    }

    @Test
    void givenAnInvalidName_whenCallCreate_shouldThrowValidationException() {
        final var aCustomerId = new CustomerId(IdentifierUtils.generateNewUUID());
        final var aName = new Name("John", "Doe");
        final var aEmail = new Email("testes@tess.com");
        final var aPassword = "123456Ab*";

        final var aAccessToken = "accessToken";

        final var aUser = User.newUser(aCustomerId, aName, aEmail, aPassword);

        final var expectedErrors = List.of(new Error("firstName", "should not be empty"));

        Mockito.doReturn(aAccessToken).when(getClientCredentials).retrieve();

        stubFor(
                post("/api/v1/users")
                        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON_VALUE))
                        .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE))
                        .willReturn(aResponse()
                                .withStatus(400)
                                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .withBody(writeValueAsString(ValidationException.with(expectedErrors))))
        );

        final var aException = Assertions.assertThrows(ValidationException.class,
                () -> this.client.create(aUser));

        Assertions.assertEquals(expectedErrors, aException.getErrors());
    }
}
