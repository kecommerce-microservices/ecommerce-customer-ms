package com.kaua.ecommerce.customer.infrastructure.rest.req;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CreateCustomerRequest(
        @JsonProperty("first_name") String firstName,
        @JsonProperty("last_name") String lastName,
        @JsonProperty("email") String email,
        @JsonProperty("password") String password
) {

    public SignUpRequest toSignUpRequest() {
        return new SignUpRequest(
                firstName(),
                lastName(),
                email(),
                password()
        );
    }
}
