package com.kaua.ecommerce.customer.infrastructure.rest.req;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UpdateCustomerTelephoneRequest(
        @JsonProperty("phone_number") String phoneNumber
) {
}
