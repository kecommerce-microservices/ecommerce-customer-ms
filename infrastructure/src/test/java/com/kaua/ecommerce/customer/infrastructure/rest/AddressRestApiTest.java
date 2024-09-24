package com.kaua.ecommerce.customer.infrastructure.rest;

import com.kaua.ecommerce.customer.ControllerTest;
import com.kaua.ecommerce.customer.application.usecases.address.*;
import com.kaua.ecommerce.customer.application.usecases.address.inputs.CreateCustomerAddressInput;
import com.kaua.ecommerce.customer.application.usecases.address.inputs.UpdateAddressInput;
import com.kaua.ecommerce.customer.application.usecases.address.inputs.UpdateAddressIsDefaultInput;
import com.kaua.ecommerce.customer.application.usecases.address.outputs.*;
import com.kaua.ecommerce.customer.domain.Fixture;
import com.kaua.ecommerce.customer.domain.customer.CustomerId;
import com.kaua.ecommerce.customer.infrastructure.rest.controllers.AddressRestController;
import com.kaua.ecommerce.lib.domain.pagination.Pagination;
import com.kaua.ecommerce.lib.domain.pagination.PaginationMetadata;
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

import java.util.List;
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

    @MockBean
    private GetAddressByIdUseCase getAddressByIdUseCase;

    @MockBean
    private GetDefaultAddressByCustomerIdUseCase getDefaultAddressByCustomerIdUseCase;

    @MockBean
    private ListCustomerAddressesUseCase listCustomerAddressesUseCase;

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

    @Test
    void givenAValidAddressId_whenCallGetAddressById_thenReturnAddressResponse() throws Exception {
        final var aAddress = Fixture.Addresses.newAddressWithComplement(
                new CustomerId(IdentifierUtils.generateNewUUID()),
                true
        );

        final var aAddressId = aAddress.getId().value().toString();

        Mockito.when(getAddressByIdUseCase.execute(any()))
                .thenReturn(new GetAddressByIdOutput(aAddress));

        final var aRequest = MockMvcRequestBuilders.get("/v1/addresses/%s".formatted(aAddressId))
                .with(admin(IdentifierUtils.generateNewUUID(), UUID.randomUUID()))
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        final var aResponse = this.mvc.perform(aRequest);

        aResponse
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id").value(aAddressId))
                .andExpect(jsonPath("$.title").value(aAddress.getTitle().value()))
                .andExpect(jsonPath("$.customer_id").value(aAddress.getCustomerId().value().toString()))
                .andExpect(jsonPath("$.zip_code").value(aAddress.getZipCode()))
                .andExpect(jsonPath("$.number").value(aAddress.getNumber()))
                .andExpect(jsonPath("$.street").value(aAddress.getStreet()))
                .andExpect(jsonPath("$.city").value(aAddress.getCity()))
                .andExpect(jsonPath("$.district").value(aAddress.getDistrict()))
                .andExpect(jsonPath("$.country").value(aAddress.getCountry()))
                .andExpect(jsonPath("$.state").value(aAddress.getState()))
                .andExpect(jsonPath("$.complement").value(aAddress.getComplement().orElse(null)))
                .andExpect(jsonPath("$.is_default").value(aAddress.isDefault()))
                .andExpect(jsonPath("$.created_at").value(aAddress.getCreatedAt().toString()))
                .andExpect(jsonPath("$.updated_at").value(aAddress.getUpdatedAt().toString()))
                .andExpect(jsonPath("$.version").isNotEmpty());
    }

    @Test
    void givenAValidAuthenticatedUser_whenCallGetDefaultAddressByCustomerId_thenReturnAddressResponse() throws Exception {
        final var aCustomerId = new CustomerId(IdentifierUtils.generateNewUUID());

        final var aAddress = Fixture.Addresses.newAddressWithComplement(aCustomerId, true);

        Mockito.when(getDefaultAddressByCustomerIdUseCase.execute(any()))
                .thenReturn(new GetDefaultAddressByCustomerIdOutput(aAddress));

        final var aRequest = MockMvcRequestBuilders.get("/v1/addresses/default")
                .with(admin(IdentifierUtils.generateNewUUID(), aCustomerId.value()))
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        final var aResponse = this.mvc.perform(aRequest);

        aResponse
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id").value(aAddress.getId().value().toString()))
                .andExpect(jsonPath("$.title").value(aAddress.getTitle().value()))
                .andExpect(jsonPath("$.customer_id").value(aAddress.getCustomerId().value().toString()))
                .andExpect(jsonPath("$.zip_code").value(aAddress.getZipCode()))
                .andExpect(jsonPath("$.number").value(aAddress.getNumber()))
                .andExpect(jsonPath("$.street").value(aAddress.getStreet()))
                .andExpect(jsonPath("$.city").value(aAddress.getCity()))
                .andExpect(jsonPath("$.district").value(aAddress.getDistrict()))
                .andExpect(jsonPath("$.country").value(aAddress.getCountry()))
                .andExpect(jsonPath("$.state").value(aAddress.getState()))
                .andExpect(jsonPath("$.complement").value(aAddress.getComplement().orElse(null)))
                .andExpect(jsonPath("$.is_default").value(aAddress.isDefault()))
                .andExpect(jsonPath("$.created_at").value(aAddress.getCreatedAt().toString()))
                .andExpect(jsonPath("$.updated_at").value(aAddress.getUpdatedAt().toString()))
                .andExpect(jsonPath("$.version").isNotEmpty());
    }

    @Test
    void givenAValidValues_whenCallListCustomerAddresses_thenReturnAddressesPaginated() throws Exception {
        final var aCustomerId = new CustomerId(IdentifierUtils.generateNewUUID());

        final var aAddressOne = Fixture.Addresses.newAddressWithComplement(aCustomerId, true);
        final var aAddressTwo = Fixture.Addresses.newAddressWithComplement(aCustomerId, false);

        final var aPage = 0;
        final var aPerPage = 2;
        final var aTerms = "";
        final var aSort = "created_at";
        final var aDirection = "asc";
        final var aItemsCount = 2;
        final var aPagesCount = 1;

        final var aItems = List.of(new ListCustomerAddressesOutput(aAddressOne), new ListCustomerAddressesOutput(aAddressTwo));
        final var aMetadata = new PaginationMetadata(aPage, aPerPage, aPagesCount, aItemsCount);

        Mockito.when(listCustomerAddressesUseCase.execute(any()))
                .thenReturn(new Pagination<>(aMetadata, aItems));

        final var aRequest = MockMvcRequestBuilders.get("/v1/addresses")
                .with(admin(IdentifierUtils.generateNewUUID(), aCustomerId.value()))
                .queryParam("page", String.valueOf(aPage))
                .queryParam("perPage", String.valueOf(aPerPage))
                .queryParam("search", aTerms)
                .queryParam("sort", aSort)
                .queryParam("direction", aDirection)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        final var aResponse = this.mvc.perform(aRequest);

        aResponse
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.metadata.current_page").value(aPage))
                .andExpect(jsonPath("$.metadata.per_page").value(aPerPage))
                .andExpect(jsonPath("$.metadata.total_pages").value(aPagesCount))
                .andExpect(jsonPath("$.metadata.total_items").value(aItemsCount))
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items").isNotEmpty())
                .andExpect(jsonPath("$.items[0].id").value(aAddressOne.getId().value().toString()))
                .andExpect(jsonPath("$.items[0].title").value(aAddressOne.getTitle().value()))
                .andExpect(jsonPath("$.items[0].customer_id").value(aAddressOne.getCustomerId().value().toString()))
                .andExpect(jsonPath("$.items[0].zip_code").value(aAddressOne.getZipCode()))
                .andExpect(jsonPath("$.items[0].number").value(aAddressOne.getNumber()))
                .andExpect(jsonPath("$.items[0].street").value(aAddressOne.getStreet()))
                .andExpect(jsonPath("$.items[0].city").value(aAddressOne.getCity()))
                .andExpect(jsonPath("$.items[0].district").value(aAddressOne.getDistrict()))
                .andExpect(jsonPath("$.items[0].state").value(aAddressOne.getState()))
                .andExpect(jsonPath("$.items[0].created_at").value(aAddressOne.getCreatedAt().toString()))
                .andExpect(jsonPath("$.items[0].updated_at").value(aAddressOne.getUpdatedAt().toString()))
                .andExpect(jsonPath("$.items[1].id").value(aAddressTwo.getId().value().toString()));

        Mockito.verify(listCustomerAddressesUseCase, Mockito.times(1)).execute(any());
    }
}