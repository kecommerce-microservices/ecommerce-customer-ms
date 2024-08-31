package com.kaua.ecommerce.customer.infrastructure.rest;

import com.kaua.ecommerce.customer.ControllerTest;
import com.kaua.ecommerce.customer.application.gateways.TelephoneGateway;
import com.kaua.ecommerce.customer.application.usecases.customer.GetCustomerByUserIdUseCase;
import com.kaua.ecommerce.customer.application.usecases.customer.UpdateCustomerDocumentUseCase;
import com.kaua.ecommerce.customer.application.usecases.customer.UpdateCustomerTelephoneUseCase;
import com.kaua.ecommerce.customer.application.usecases.customer.inputs.UpdateCustomerDocumentInput;
import com.kaua.ecommerce.customer.application.usecases.customer.inputs.UpdateCustomerTelephoneInput;
import com.kaua.ecommerce.customer.application.usecases.customer.outputs.GetCustomerByIdentifierOutput;
import com.kaua.ecommerce.customer.application.usecases.customer.outputs.UpdateCustomerDocumentOutput;
import com.kaua.ecommerce.customer.application.usecases.customer.outputs.UpdateCustomerTelephoneOutput;
import com.kaua.ecommerce.customer.domain.Fixture;
import com.kaua.ecommerce.customer.infrastructure.mediator.SignUpMediator;
import com.kaua.ecommerce.customer.infrastructure.rest.controllers.CustomerRestController;
import com.kaua.ecommerce.customer.infrastructure.rest.req.SignUpRequest;
import com.kaua.ecommerce.customer.infrastructure.rest.res.SignUpResponse;
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
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ControllerTest(controllers = CustomerRestController.class)
class CustomerRestApiTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private SignUpMediator signUpMediator;

    @MockBean
    private UpdateCustomerDocumentUseCase updateCustomerDocumentUseCase;

    @MockBean
    private UpdateCustomerTelephoneUseCase updateCustomerTelephoneUseCase;

    @MockBean
    private GetCustomerByUserIdUseCase getCustomerByUserIdUseCase;

    @Captor
    private ArgumentCaptor<SignUpRequest> signUpRequestCaptor;

    @Captor
    private ArgumentCaptor<UpdateCustomerDocumentInput> updateCustomerDocumentInputCaptor;

    @Captor
    private ArgumentCaptor<UpdateCustomerTelephoneInput> updateCustomerTelephoneInputCaptor;

    @Test
    void givenAValidInput_whenSignUp_thenReturnCustomerIdAndUserId() throws Exception {
        final var aFirstName = "John";
        final var aLastName = "Doe";
        final var aEmail = "testes@tess.com";
        final var aPassword = "1234567Ab*";

        final var expectedCustomerId = "8773509f-966d-4cab-ba7d-30d933ef8112";
        final var expectedUserId = "e8dddaa9-13d1-45c4-9e52-7512d8ae54f6";

        Mockito.when(signUpMediator.signUp(any()))
                .thenReturn(new SignUpResponse(expectedCustomerId, expectedUserId));

        var json = """
                {
                    "first_name": "%s",
                    "last_name": "%s",
                    "email": "%s",
                    "password": "%s"
                }
                """.formatted(aFirstName, aLastName, aEmail, aPassword);

        final var aRequest = MockMvcRequestBuilders.post("/v1/customers/signup")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(json);

        final var aResponse = this.mvc.perform(aRequest);

        aResponse
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/v1/customers/%s".formatted(expectedCustomerId)))
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.customer_id").value(expectedCustomerId))
                .andExpect(jsonPath("$.idp_user_id").value(expectedUserId));

        Mockito.verify(signUpMediator, Mockito.times(1)).signUp(signUpRequestCaptor.capture());

        final var signUpRequest = signUpRequestCaptor.getValue();

        Assertions.assertEquals(aFirstName, signUpRequest.firstName());
        Assertions.assertEquals(aLastName, signUpRequest.lastName());
        Assertions.assertEquals(aEmail, signUpRequest.email());
        Assertions.assertEquals(aPassword, signUpRequest.password());
    }

    @Test
    void givenAnEmptyFirstName_whenSignUp_thenReturnBadRequest() throws Exception {
        final var aFirstName = "";
        final var aLastName = "Doe";
        final var aEmail = "testes.tes@tes.com";
        final var aPassword = "12334567Ab*";

        var json = """
                {
                    "first_name": "%s",
                    "last_name": "%s",
                    "email": "%s",
                    "password": "%s"
                }
                """.formatted(aFirstName, aLastName, aEmail, aPassword);

        final var aRequest = MockMvcRequestBuilders.post("/v1/customers/signup")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(json);

        final var aResponse = this.mvc.perform(aRequest);

        aResponse
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("ValidationException"))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].property").value("firstName"))
                .andExpect(jsonPath("$.errors[0].message").value("should not be empty"));
    }

    @Test
    void givenAValidInput_whenUpdateDocument_thenReturnDocumentUpdated() throws Exception {
        final var aDocumentNumber = "123456789";
        final var aDocumentType = "CPF";

        final var expectedCustomerId = IdentifierUtils.generateNewId();

        Mockito.when(updateCustomerDocumentUseCase.execute(any()))
                .thenReturn(new UpdateCustomerDocumentOutput(expectedCustomerId, aDocumentType));

        var json = """
                {
                    "document_number": "%s",
                    "document_type": "%s"
                }
                """.formatted(aDocumentNumber, aDocumentType);

        final var aRequest = MockMvcRequestBuilders.patch("/v1/customers/document")
                .with(admin(IdentifierUtils.generateNewUUID(), UUID.fromString(expectedCustomerId)))
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(json);

        final var aResponse = this.mvc.perform(aRequest);

        aResponse
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.customer_id").value(expectedCustomerId))
                .andExpect(jsonPath("$.document_type").value(aDocumentType));

        Mockito.verify(updateCustomerDocumentUseCase, Mockito.times(1)).execute(updateCustomerDocumentInputCaptor.capture());

        final var updateCustomerDocumentInput = updateCustomerDocumentInputCaptor.getValue();

        Assertions.assertEquals(expectedCustomerId, updateCustomerDocumentInput.customerId().toString());
        Assertions.assertEquals(aDocumentNumber, updateCustomerDocumentInput.documentNumber());
        Assertions.assertEquals(aDocumentType, updateCustomerDocumentInput.documentType());
    }

    @Test
    void givenAValidInput_whenUpdateTelephone_thenReturnTelephoneUpdated() throws Exception {
        final var aPhoneNumber = "+5511999999999";

        final var expectedCustomerId = IdentifierUtils.generateNewId();

        Mockito.when(updateCustomerTelephoneUseCase.execute(any()))
                .thenReturn(new UpdateCustomerTelephoneOutput(expectedCustomerId, aPhoneNumber));

        var json = """
                {
                    "phone_number": "%s"
                }
                """.formatted(aPhoneNumber);

        final var aRequest = MockMvcRequestBuilders.patch("/v1/customers/telephone")
                .with(admin(IdentifierUtils.generateNewUUID(), UUID.fromString(expectedCustomerId)))
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(json);

        final var aResponse = this.mvc.perform(aRequest);

        aResponse
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.customer_id").value(expectedCustomerId))
                .andExpect(jsonPath("$.telephone").value(aPhoneNumber));

        Mockito.verify(updateCustomerTelephoneUseCase, Mockito.times(1)).execute(updateCustomerTelephoneInputCaptor.capture());

        final var updateCustomerTelephoneInput = updateCustomerTelephoneInputCaptor.getValue();

        Assertions.assertEquals(expectedCustomerId, updateCustomerTelephoneInput.customerId().toString());
        Assertions.assertEquals(aPhoneNumber, updateCustomerTelephoneInput.telephone());
    }

    @Test
    void givenAValidUserId_whenGetCustomerByUserId_thenReturnCustomer() throws Exception {
        final var aCustomer = Fixture.Customers.newCustomerWithAllValues();
        final var aPhoneInfo = new TelephoneGateway.PhoneNumberInformation(
                "(11) 98765-4321",
                "BR",
                "+55"
        );

        final var aUserId = aCustomer.getUserId().value();

        Mockito.when(getCustomerByUserIdUseCase.execute(aCustomer.getUserId()))
                .thenReturn(new GetCustomerByIdentifierOutput(
                        aCustomer,
                        aPhoneInfo
                ));

        final var aRequest = MockMvcRequestBuilders.get("/v1/customers/users/%s".formatted(aUserId))
                .with(admin(aCustomer.getUserId().value(), aCustomer.getId().value()))
                .accept(MediaType.APPLICATION_JSON_VALUE);

        final var aResponse = this.mvc.perform(aRequest);

        aResponse
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.customer_id").value(aCustomer.getId().value().toString()))
                .andExpect(jsonPath("$.user_id").value(aCustomer.getUserId().value().toString()))
                .andExpect(jsonPath("$.first_name").value(aCustomer.getName().firstName()))
                .andExpect(jsonPath("$.last_name").value(aCustomer.getName().lastName()))
                .andExpect(jsonPath("$.full_name").value(aCustomer.getName().fullName()))
                .andExpect(jsonPath("$.email").value(aCustomer.getEmail().value()))
                .andExpect(jsonPath("$.document.number").value(aCustomer.getDocument().get().formattedValue()))
                .andExpect(jsonPath("$.document.type").value(aCustomer.getDocument().get().type()))
                .andExpect(jsonPath("$.telephone.phone_number").value(aPhoneInfo.phoneNumber()))
                .andExpect(jsonPath("$.telephone.country_code").value(aPhoneInfo.countryCode()))
                .andExpect(jsonPath("$.telephone.region_code").value(aPhoneInfo.regionCode()))
                .andExpect(jsonPath("$.created_at").value(aCustomer.getCreatedAt().toString()))
                .andExpect(jsonPath("$.updated_at").value(aCustomer.getUpdatedAt().toString()));

        Mockito.verify(getCustomerByUserIdUseCase, Mockito.times(1)).execute(aCustomer.getUserId());
    }

    @Test
    void givenAValidAuthenticatedUser_whenGetMe_thenReturnCustomer() throws Exception {
        final var aCustomer = Fixture.Customers.newCustomerWithAllValues();
        final var aPhoneInfo = new TelephoneGateway.PhoneNumberInformation(
                "(11) 98765-4321",
                "BR",
                "+55"
        );

        Mockito.when(getCustomerByUserIdUseCase.execute(aCustomer.getUserId()))
                .thenReturn(new GetCustomerByIdentifierOutput(
                        aCustomer,
                        aPhoneInfo
                ));

        final var aRequest = MockMvcRequestBuilders.get("/v1/customers/me")
                .with(admin(aCustomer.getUserId().value(), aCustomer.getId().value()))
                .accept(MediaType.APPLICATION_JSON_VALUE);

        final var aResponse = this.mvc.perform(aRequest);

        aResponse
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.customer_id").value(aCustomer.getId().value().toString()))
                .andExpect(jsonPath("$.user_id").value(aCustomer.getUserId().value().toString()))
                .andExpect(jsonPath("$.first_name").value(aCustomer.getName().firstName()))
                .andExpect(jsonPath("$.last_name").value(aCustomer.getName().lastName()))
                .andExpect(jsonPath("$.full_name").value(aCustomer.getName().fullName()))
                .andExpect(jsonPath("$.email").value(aCustomer.getEmail().value()))
                .andExpect(jsonPath("$.document.number").value(aCustomer.getDocument().get().formattedValue()))
                .andExpect(jsonPath("$.document.type").value(aCustomer.getDocument().get().type()))
                .andExpect(jsonPath("$.telephone.phone_number").value(aPhoneInfo.phoneNumber()))
                .andExpect(jsonPath("$.telephone.country_code").value(aPhoneInfo.countryCode()))
                .andExpect(jsonPath("$.telephone.region_code").value(aPhoneInfo.regionCode()))
                .andExpect(jsonPath("$.created_at").value(aCustomer.getCreatedAt().toString()))
                .andExpect(jsonPath("$.updated_at").value(aCustomer.getUpdatedAt().toString()));

        Mockito.verify(getCustomerByUserIdUseCase, Mockito.times(1)).execute(aCustomer.getUserId());
    }

    @Test
    void givenAValidAuthenticatedUserButWithoutOptionalValues_whenCallGetMe_thenReturnCustomerWithoutOptionalValues() throws Exception {
        final var aCustomer = Fixture.Customers.newCustomer();

        Mockito.when(getCustomerByUserIdUseCase.execute(aCustomer.getUserId()))
                .thenReturn(new GetCustomerByIdentifierOutput(
                        aCustomer,
                        null
                ));

        final var aRequest = MockMvcRequestBuilders.get("/v1/customers/me")
                .with(admin(aCustomer.getUserId().value(), aCustomer.getId().value()))
                .accept(MediaType.APPLICATION_JSON_VALUE);

        final var aResponse = this.mvc.perform(aRequest);

        aResponse
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.customer_id").value(aCustomer.getId().value().toString()))
                .andExpect(jsonPath("$.user_id").value(aCustomer.getUserId().value().toString()))
                .andExpect(jsonPath("$.first_name").value(aCustomer.getName().firstName()))
                .andExpect(jsonPath("$.last_name").value(aCustomer.getName().lastName()))
                .andExpect(jsonPath("$.full_name").value(aCustomer.getName().fullName()))
                .andExpect(jsonPath("$.email").value(aCustomer.getEmail().value()))
                .andExpect(jsonPath("$.document").isEmpty())
                .andExpect(jsonPath("$.telephone").isEmpty())
                .andExpect(jsonPath("$.created_at").value(aCustomer.getCreatedAt().toString()))
                .andExpect(jsonPath("$.updated_at").value(aCustomer.getUpdatedAt().toString()));

        Mockito.verify(getCustomerByUserIdUseCase, Mockito.times(1)).execute(aCustomer.getUserId());
    }
}
