package com.kaua.ecommerce.customer.infrastructure.rest;

import com.kaua.ecommerce.customer.ControllerTest;
import com.kaua.ecommerce.customer.infrastructure.mediator.SignUpMediator;
import com.kaua.ecommerce.customer.infrastructure.rest.controllers.CustomerRestController;
import com.kaua.ecommerce.customer.infrastructure.rest.req.CreateCustomerRequest;
import com.kaua.ecommerce.customer.infrastructure.rest.res.SignUpResponse;
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

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ControllerTest(controllers = CustomerRestController.class)
class CustomerRestApiTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private SignUpMediator signUpMediator;

    @Captor
    private ArgumentCaptor<CreateCustomerRequest> createCustomerRequestCaptor;

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

        final var aRequest = MockMvcRequestBuilders.post("/v1/customers")
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

        final var aRequest = MockMvcRequestBuilders.post("/v1/customers")
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
}
