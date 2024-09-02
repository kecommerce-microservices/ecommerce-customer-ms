package com.kaua.ecommerce.customer.infrastructure.rest;

import com.kaua.ecommerce.customer.ControllerTest;
import com.kaua.ecommerce.customer.application.usecases.address.CreateCustomerAddressUseCase;
import com.kaua.ecommerce.customer.application.usecases.address.inputs.CreateCustomerAddressInput;
import com.kaua.ecommerce.customer.application.usecases.address.outputs.CreateCustomerAddressOutput;
import com.kaua.ecommerce.customer.infrastructure.rest.controllers.AddressRestController;
import com.kaua.ecommerce.lib.domain.utils.IdentifierUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.UUID;

import static com.kaua.ecommerce.customer.ApiTest.admin;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ControllerTest(controllers = AddressRestController.class)
class AddressRestApiTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private CreateCustomerAddressUseCase createCustomerAddressUseCase;

    @Captor
    private ArgumentCaptor<CreateCustomerAddressInput> createCustomerAddressInputCaptor;

    @Test
    void givenAValidInput_whenCallCreateCustomerAddress_thenReturnCustomerAndAddressId() throws Exception {
        final var aCustomerId = IdentifierUtils.generateNewId();
        final var aTitle = "Home";
        final var aZipCode = "12345678";
        final var aNumber = "123";
        final var aCountry = "BR";
        final var aComplement = "Apt 123";
        final var aIsDefault = true;

        final var expectedAddressId = IdentifierUtils.generateNewId();

        Mockito.when(createCustomerAddressUseCase.execute(any()))
                .thenReturn(new CreateCustomerAddressOutput(expectedAddressId, aCustomerId));

        var json = """
                {
                    "title": "%s",
                    "zip_code": "%s",
                    "number": "%s",
                    "country": "%s",
                    "complement": "%s",
                    "is_default": %s
                }
                """.formatted(aTitle, aZipCode, aNumber, aCountry, aComplement, aIsDefault);

        final var aRequest = MockMvcRequestBuilders.post("/v1/addresses")
                .with(admin(IdentifierUtils.generateNewUUID(), UUID.fromString(aCustomerId)))
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(json);

        final var aResponse = this.mvc.perform(aRequest);

        aResponse
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/v1/addresses/%s".formatted(expectedAddressId)))
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.address_id").value(expectedAddressId))
                .andExpect(jsonPath("$.customer_id").value(aCustomerId));

        Mockito.verify(createCustomerAddressUseCase, Mockito.times(1)).execute(createCustomerAddressInputCaptor.capture());

        final var createCustomerAddressInput = createCustomerAddressInputCaptor.getValue();

        Assertions.assertEquals(aCustomerId, createCustomerAddressInput.customerId().toString());
        Assertions.assertEquals(aTitle, createCustomerAddressInput.title());
        Assertions.assertEquals(aZipCode, createCustomerAddressInput.zipCode());
        Assertions.assertEquals(aNumber, createCustomerAddressInput.number());
        Assertions.assertEquals(aCountry, createCustomerAddressInput.country());
        Assertions.assertEquals(aComplement, createCustomerAddressInput.complement());
        Assertions.assertEquals(aIsDefault, createCustomerAddressInput.isDefault());
    }
}