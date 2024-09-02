package com.kaua.ecommerce.customer.infrastructure.gateways;

import com.kaua.ecommerce.customer.AbstractRestClientTest;
import com.kaua.ecommerce.lib.domain.exceptions.InternalErrorException;
import com.kaua.ecommerce.lib.domain.exceptions.ValidationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

class AddressGatewayImplTest extends AbstractRestClientTest {

    @Autowired
    private AddressGatewayImpl client;

    @Test
    void givenAValidZipCode_whenCallGetAddressByZipCode_shouldReturnAnAddress() {
        final var aZipCode = "01311-300";

        final var aResponseBody = writeValueAsString(Map.of(
                "cep", aZipCode,
                "logradouro", "Avenida Paulista",
                "bairro", "Bela Vista",
                "localidade", "São Paulo",
                "uf", "SP"
        ));

        stubFor(
                get("/ws/%s/json".formatted(aZipCode))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .withBody(aResponseBody))
        );

        final var aOutput = this.client.getAddressByZipCode(aZipCode);

        Assertions.assertTrue(aOutput.isPresent());
        Assertions.assertEquals(aZipCode, aOutput.get().zipCode());
        Assertions.assertEquals("Avenida Paulista", aOutput.get().street());
        Assertions.assertEquals("Bela Vista", aOutput.get().district());
        Assertions.assertEquals("São Paulo", aOutput.get().city());

        verify(1, getRequestedFor(urlEqualTo("/ws/%s/json".formatted(aZipCode))));
    }

    @Test
    void givenAnInvalidZipCode_whenCallGetAddressByZipCode_shouldReturnEmpty() {
        final var aZipCode = "01311-300";

        final var aResponseBody = writeValueAsString(Map.of(
                "erro", true
        ));

        stubFor(
                get("/ws/%s/json".formatted(aZipCode))
                        .willReturn(aResponse()
                                .withStatus(404)
                                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .withBody(aResponseBody))
        );

        final var aOutput = this.client.getAddressByZipCode(aZipCode);

        Assertions.assertTrue(aOutput.isEmpty());

        verify(1, getRequestedFor(urlEqualTo("/ws/%s/json".formatted(aZipCode))));
    }

    @Test
    void givenAnInvalidZipCodeBadRequest_whenCallGetAddressByZipCode_shouldThrowValidationException() {
        final var aZipCode = "01311-300";

        final var aResponseBody = writeValueAsString(Map.of(
                "message", "Bad Request"
        ));

        stubFor(
                get("/ws/%s/json".formatted(aZipCode))
                        .willReturn(aResponse()
                                .withStatus(400)
                                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .withBody(aResponseBody))
        );

        final var aException = Assertions.assertThrows(ValidationException.class,
                () -> this.client.getAddressByZipCode(aZipCode));

        Assertions.assertEquals("{\"message\":\"Bad Request\"}", aException.getErrors().get(0).message());

        verify(1, getRequestedFor(urlEqualTo("/ws/%s/json".formatted(aZipCode))));
    }

    @Test
    void givenAValidZipCodeButReturns5xx_whenCallGetAddressByZipCode_shouldThrowInternalErrorException() {
        final var aZipCode = "01311-300";

        final var aResponseBody = writeValueAsString(Map.of(
                "message", "Internal Server Error"
        ));

        stubFor(
                get("/ws/%s/json".formatted(aZipCode))
                        .willReturn(aResponse()
                                .withStatus(500)
                                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .withBody(aResponseBody))
        );

        final var aException = Assertions.assertThrows(InternalErrorException.class,
                () -> this.client.getAddressByZipCode(aZipCode));

        Assertions.assertEquals("Error observed from address [method:GET] [resourceId:01311-300] [status:500] [response:{\"message\":\"Internal Server Error\"}]", aException.getMessage());

        verify(2, getRequestedFor(urlEqualTo("/ws/%s/json".formatted(aZipCode))));
    }
}
