package com.kaua.ecommerce.customer.infrastructure.rest.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaua.ecommerce.customer.application.usecases.customer.inputs.CreateCustomerInput;
import com.kaua.ecommerce.customer.application.usecases.customer.inputs.CreateIdpUserInput;
import com.kaua.ecommerce.customer.application.usecases.customer.outputs.CreateIdpUserOutput;
import com.kaua.ecommerce.customer.domain.customer.CustomerId;
import com.kaua.ecommerce.lib.domain.validation.AssertionConcern;

import java.util.UUID;

public record SignUpRequest(
        @JsonProperty("first_name") String firstName,
        @JsonProperty("last_name") String lastName,
        @JsonProperty("email") String email,
        @JsonProperty("password") String password,
        @JsonProperty("customer_id") String customerId,
        @JsonProperty("idp_user_id") String idpUserId
) implements AssertionConcern {

    private static final String FIRST_NAME = "firstName";
    private static final String LAST_NAME = "lastName";
    private static final String EMAIL_FIELD = "email";
    private static final String PASSWORD_FIELD = "password";

    private static final String SHOULD_NOT_BE_EMPTY = "should not be empty";

    public SignUpRequest {
        this.assertArgumentNotEmpty(firstName, FIRST_NAME, SHOULD_NOT_BE_EMPTY);
        this.assertArgumentMinLength(firstName, 3, FIRST_NAME, "should not be less than 3 characters");
        this.assertArgumentMaxLength(firstName, 100, FIRST_NAME, "should not be greater than 100 characters");

        this.assertArgumentNotEmpty(lastName, LAST_NAME, SHOULD_NOT_BE_EMPTY);
        this.assertArgumentMinLength(lastName, 3, LAST_NAME, "should not be less than 3 characters");
        this.assertArgumentMaxLength(lastName, 100, LAST_NAME, "should not be greater than 100 characters");

        this.assertArgumentNotEmpty(email, EMAIL_FIELD, SHOULD_NOT_BE_EMPTY);
        this.assertArgumentNotEmpty(password, PASSWORD_FIELD, SHOULD_NOT_BE_EMPTY);
    }

    public SignUpRequest(
            String firstName,
            String lastName,
            String email,
            String password
    ) {
        this(firstName, lastName, email, password, null, null);
    }

    public SignUpRequest with(final CreateIdpUserOutput output) {
        return new SignUpRequest(
                firstName(),
                lastName(),
                email(),
                password(),
                customerId(),
                output.idpUserId().value().toString()
        );
    }

    public SignUpRequest with(final CustomerId customerId) {
        return new SignUpRequest(
                firstName(),
                lastName(),
                email(),
                password(),
                customerId.value().toString(),
                idpUserId()
        );
    }

    public CreateIdpUserInput toCreateIdpUserInput() {
        return new CreateIdpUserInput(
                UUID.fromString(customerId()),
                firstName(),
                lastName(),
                email(),
                password()
        );
    }

    public CreateCustomerInput toCreateCustomerInput() {
        return new CreateCustomerInput(
                UUID.fromString(idpUserId()),
                UUID.fromString(customerId()),
                firstName(),
                lastName(),
                email()
        );
    }
}
