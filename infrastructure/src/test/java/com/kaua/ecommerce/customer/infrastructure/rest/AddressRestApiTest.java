package com.kaua.ecommerce.customer.infrastructure.rest;

import com.kaua.ecommerce.customer.ControllerTest;
import com.kaua.ecommerce.customer.application.usecases.address.CreateCustomerAddressUseCase;
import com.kaua.ecommerce.customer.application.usecases.address.UpdateAddressIsDefaultUseCase;
import com.kaua.ecommerce.customer.application.usecases.address.UpdateAddressUseCase;
import com.kaua.ecommerce.customer.application.usecases.address.inputs.CreateCustomerAddressInput;
import com.kaua.ecommerce.customer.application.usecases.address.inputs.UpdateAddressInput;
import com.kaua.ecommerce.customer.application.usecases.address.inputs.UpdateAddressIsDefaultInput;
import com.kaua.ecommerce.customer.application.usecases.address.outputs.CreateCustomerAddressOutput;
import com.kaua.ecommerce.customer.application.usecases.address.outputs.UpdateAddressIsDefaultOutput;
import com.kaua.ecommerce.customer.application.usecases.address.outputs.UpdateAddressOutput;
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

    @MockBean
    private UpdateAddressIsDefaultUseCase updateAddressIsDefaultUseCase;

    @MockBean
    private UpdateAddressUseCase updateAddressUseCase;

    @Captor
    private ArgumentCaptor<CreateCustomerAddressInput> createCustomerAddressInputCaptor;

    @Captor
    private ArgumentCaptor<UpdateAddressIsDefaultInput> updateAddressIsDefaultInputCaptor;

    @Captor
    private ArgumentCaptor<UpdateAddressInput> updateAddressInputCaptor;

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

    @Test
    void givenAValidInput_whenCallUpdateAddressIsDefault_thenReturnAddressIdAndIsDefault() throws Exception {
        final var aAddressId = IdentifierUtils.generateNewId();
        final var aIsDefault = true;

        Mockito.when(updateAddressIsDefaultUseCase.execute(any()))
                .thenReturn(new UpdateAddressIsDefaultOutput(aAddressId, aIsDefault));

        var json = """
                {
                    "is_default": %s
                }
                """.formatted(aIsDefault);

        final var aRequest = MockMvcRequestBuilders.patch("/v1/addresses/%s/default".formatted(aAddressId))
                .with(admin(IdentifierUtils.generateNewUUID(), UUID.randomUUID()))
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(json);

        final var aResponse = this.mvc.perform(aRequest);

        aResponse
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.address_id").value(aAddressId))
                .andExpect(jsonPath("$.is_default").value(aIsDefault));

        Mockito.verify(updateAddressIsDefaultUseCase, Mockito.times(1)).execute(updateAddressIsDefaultInputCaptor.capture());

        final var updateAddressIsDefaultInput = updateAddressIsDefaultInputCaptor.getValue();

        Assertions.assertEquals(aAddressId, updateAddressIsDefaultInput.addressId().toString());
        Assertions.assertEquals(aIsDefault, updateAddressIsDefaultInput.isDefault());
    }

    @Test
    void givenAValidInput_whenCallUpdateAddress_thenReturnAddressId() throws Exception {
        final var aAddressId = IdentifierUtils.generateNewId();
        final var aTitle = "Home";
        final var aZipCode = "12345678";
        final var aNumber = "123";
        final var aCountry = "BR";
        final var aComplement = "Apt 123";
        final var aStreet = "Rua 123";
        final var aDistrict = "Bairro 123";

        Mockito.when(updateAddressUseCase.execute(any()))
                .thenReturn(new UpdateAddressOutput(aAddressId, IdentifierUtils.generateNewId()));

        var json = """
                {
                    "title": "%s",
                    "zip_code": "%s",
                    "number": "%s",
                    "complement": "%s",
                    "street": "%s",
                    "district": "%s",
                    "country": "%s"
                }
                """.formatted(aTitle, aZipCode, aNumber, aComplement, aStreet, aDistrict, aCountry);

        final var aRequest = MockMvcRequestBuilders.patch("/v1/addresses/%s".formatted(aAddressId))
                .with(admin(IdentifierUtils.generateNewUUID(), UUID.randomUUID()))
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(json);

        final var aResponse = this.mvc.perform(aRequest);

        aResponse
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id").value(aAddressId))
                .andExpect(jsonPath("$.customer_id").isNotEmpty());

        Mockito.verify(updateAddressUseCase, Mockito.times(1)).execute(updateAddressInputCaptor.capture());

        final var updateAddressInput = updateAddressInputCaptor.getValue();

        Assertions.assertEquals(aAddressId, updateAddressInput.addressId().toString());
        Assertions.assertEquals(aTitle, updateAddressInput.title());
        Assertions.assertEquals(aZipCode, updateAddressInput.zipCode());
        Assertions.assertEquals(aNumber, updateAddressInput.number());
        Assertions.assertEquals(aCountry, updateAddressInput.country());
        Assertions.assertEquals(aComplement, updateAddressInput.complement());
        Assertions.assertEquals(aStreet, updateAddressInput.street());
        Assertions.assertEquals(aDistrict, updateAddressInput.district());
    }
}