package com.kaua.ecommerce.customer.infrastructure.gateways;

import com.kaua.ecommerce.customer.AbstractRestClientTest;
import com.kaua.ecommerce.customer.domain.Fixture;
import com.kaua.ecommerce.customer.domain.customer.CustomerId;
import com.kaua.ecommerce.customer.domain.customer.idp.User;
import com.kaua.ecommerce.customer.domain.customer.idp.UserId;
import com.kaua.ecommerce.customer.domain.person.Email;
import com.kaua.ecommerce.customer.domain.person.Name;
import com.kaua.ecommerce.customer.infrastructure.authentication.GetClientCredentials;
import com.kaua.ecommerce.lib.domain.exceptions.DomainException;
import com.kaua.ecommerce.lib.domain.exceptions.InternalErrorException;
import com.kaua.ecommerce.lib.domain.exceptions.ValidationException;
import com.kaua.ecommerce.lib.domain.utils.IdentifierUtils;
import com.kaua.ecommerce.lib.domain.validation.Error;
import io.github.resilience4j.bulkhead.BulkheadFullException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Map;

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
        final var aAccessToken = "accessToken";

        final var aUser = Fixture.IdpUsers.withoutUserId();

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

        verify(1, postRequestedFor(urlEqualTo("/api/v1/users"))
                .withHeader(HttpHeaders.AUTHORIZATION, equalTo("Bearer " + aAccessToken))
                .withRequestBody(equalToJson("""
                        {
                            "customer_id": "%s",
                            "first_name": "%s",
                            "last_name": "%s",
                            "email": "%s",
                            "password": "%s"
                        }
                        """.formatted(
                        aUser.getCustomerId().value().toString(),
                        aUser.getName().firstName(),
                        aUser.getName().lastName(),
                        aUser.getEmail().value(),
                        aUser.getPassword()
                ))));
    }

    @Test
    void givenAnExistingUser_whenCallCreate_shouldThrowDomainException() {
        final var aAccessToken = "accessToken";

        final var aUser = Fixture.IdpUsers.withoutUserId();

        final var expectedErrorMessage = "DomainException";
        final var expectedErrors = List.of(new Error("Email already exists"));

        Mockito.doReturn(aAccessToken).when(getClientCredentials).retrieve();

        stubFor(
                post("/api/v1/users")
                        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON_VALUE))
                        .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE))
                        .willReturn(aResponse()
                                .withStatus(422)
                                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .withBody(writeValueAsString(DomainException.with(expectedErrors))))
        );

        final var aException = Assertions.assertThrows(DomainException.class,
                () -> this.client.create(aUser));

        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());
        Assertions.assertEquals(expectedErrors, aException.getErrors());

        verify(1, postRequestedFor(urlEqualTo("/api/v1/users"))
                .withHeader(HttpHeaders.AUTHORIZATION, equalTo("Bearer " + aAccessToken))
                .withRequestBody(equalToJson("""
                        {
                            "customer_id": "%s",
                            "first_name": "%s",
                            "last_name": "%s",
                            "email": "%s",
                            "password": "%s"
                        }
                        """.formatted(
                        aUser.getCustomerId().value().toString(),
                        aUser.getName().firstName(),
                        aUser.getName().lastName(),
                        aUser.getEmail().value(),
                        aUser.getPassword()
                ))));
    }

    @Test
    void givenAValidUser_whenCallCreateButTimeoutExceeded_shouldThrowInternalErrorException() {
        final var aCustomerId = new CustomerId(IdentifierUtils.generateNewUUID());

        final var aAccessToken = "accessToken";

        final var expectedErrorMessage = "Timeout error observed from users [resourceId:%s]"
                .formatted(aCustomerId.value().toString());

        final var expectedUserId = new UserId(IdentifierUtils.generateNewUUID());
        final var aUser = Fixture.IdpUsers.withoutUserId(aCustomerId);

        final var aResponseBody = new AuthServerIdentityProviderClient.CreateUserIdpResult(expectedUserId.value().toString());

        Mockito.doReturn(aAccessToken).when(getClientCredentials).retrieve();

        stubFor(
                post("/api/v1/users")
                        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON_VALUE))
                        .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE))
                        .willReturn(aResponse()
                                .withStatus(201)
                                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .withFixedDelay(1020)
                                .withBody(writeValueAsString(aResponseBody)))
        );

        final var aException = Assertions.assertThrows(InternalErrorException.class,
                () -> this.client.create(aUser));

        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());

        verify(2, postRequestedFor(urlEqualTo("/api/v1/users"))
                .withHeader(HttpHeaders.AUTHORIZATION, equalTo("Bearer " + aAccessToken))
                .withRequestBody(equalToJson("""
                        {
                            "customer_id": "%s",
                            "first_name": "%s",
                            "last_name": "%s",
                            "email": "%s",
                            "password": "%s"
                        }
                        """.formatted(
                        aCustomerId.value().toString(),
                        aUser.getName().firstName(),
                        aUser.getName().lastName(),
                        aUser.getEmail().value(),
                        aUser.getPassword()
                ))));
    }

    @Test
    void givenAValidUser_whenCallCreateButBulkheadIsFull_shouldThrowException() {
        final var aUser = Fixture.IdpUsers.withoutUserId();

        final var expectedErrorMessage = "Bulkhead 'users' is full and does not permit further calls";

        acquireBulkheadPermission(USERS);


        final var aException = Assertions.assertThrows(BulkheadFullException.class,
                () -> this.client.create(aUser));

        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());

        releaseBulkheadPermission(USERS);
    }

    @Test
    void givenAServerError_whenCallCreateButIsMoreThanThreshold_shouldOpenCircuitBreak() {
        final var aAccessToken = "accessToken";

        final var expectedErrorMessage = "CircuitBreaker 'users' is OPEN and does not permit further calls";

        final var aUser = Fixture.IdpUsers.withoutUserId();

        final var aResponseBody = writeValueAsString(Map.of("message", expectedErrorMessage));

        Mockito.doReturn(aAccessToken).when(getClientCredentials).retrieve();

        stubFor(
                post("/api/v1/users")
                        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON_VALUE))
                        .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE))
                        .willReturn(aResponse()
                                .withStatus(500)
                                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .withBody(aResponseBody))
        );

        // primeira vez que entra, da erro, vai abrir o circuito e a partir da segunda vez do retry, vai cair direto no circuito aberto
        Assertions.assertThrows(InternalErrorException.class, () -> this.client.create(aUser));
        final var aException = Assertions.assertThrows(CallNotPermittedException.class, () -> this.client.create(aUser));

        checkCircuitBreakerState(USERS, CircuitBreaker.State.OPEN);
        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());

        verify(3, postRequestedFor(urlEqualTo("/api/v1/users"))
                .withHeader(HttpHeaders.AUTHORIZATION, equalTo("Bearer " + aAccessToken))
                .withRequestBody(equalToJson("""
                        {
                            "customer_id": "%s",
                            "first_name": "%s",
                            "last_name": "%s",
                            "email": "%s",
                            "password": "%s"
                        }
                        """.formatted(
                        aUser.getCustomerId().value().toString(),
                        aUser.getName().firstName(),
                        aUser.getName().lastName(),
                        aUser.getEmail().value(),
                        aUser.getPassword()
                ))));
    }

    @Test
    void givenAValidUser_whenCallCreateOnCBIsOpen_shouldReturnError() {
        transitionToOpenState(USERS);

        final var expectedErrorMessage = "CircuitBreaker 'users' is OPEN and does not permit further calls";

        final var aUser = Fixture.IdpUsers.withoutUserId();

        final var aException = Assertions.assertThrows(CallNotPermittedException.class,
                () -> this.client.create(aUser));

        checkCircuitBreakerState(USERS, CircuitBreaker.State.OPEN);
        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());

        verify(0, postRequestedFor(urlEqualTo("/api/v1/users")));
    }

    @Test
    void givenAValidUser_whenCallCreateButReturn500_shouldThrowInternalErrorException() {
        final var aCustomerId = new CustomerId(IdentifierUtils.generateNewUUID());

        final var aAccessToken = "accessToken";

        final var expectedErrorMessage = "Error observed during creating user from users [method:POST] [resourceId:%s] [status:500] [response:{\"message\":\"Internal Server Error\"}]"
                .formatted(aCustomerId.value().toString());

        final var aUser = Fixture.IdpUsers.withoutUserId(aCustomerId);

        final var aResponseBody = writeValueAsString(Map.of("message", "Internal Server Error"));

        Mockito.doReturn(aAccessToken).when(getClientCredentials).retrieve();

        stubFor(
                post("/api/v1/users")
                        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON_VALUE))
                        .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE))
                        .willReturn(aResponse()
                                .withStatus(500)
                                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .withBody(aResponseBody))
        );

        final var aException = Assertions.assertThrows(InternalErrorException.class,
                () -> this.client.create(aUser));

        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());

        verify(2, postRequestedFor(urlEqualTo("/api/v1/users"))
                .withHeader(HttpHeaders.AUTHORIZATION, equalTo("Bearer " + aAccessToken))
                .withRequestBody(equalToJson("""
                        {
                            "customer_id": "%s",
                            "first_name": "%s",
                            "last_name": "%s",
                            "email": "%s",
                            "password": "%s"
                        }
                        """.formatted(
                        aUser.getCustomerId().value().toString(),
                        aUser.getName().firstName(),
                        aUser.getName().lastName(),
                        aUser.getEmail().value(),
                        aUser.getPassword()
                ))));
    }
}
