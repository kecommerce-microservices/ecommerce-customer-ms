package com.kaua.ecommerce.customer.infrastructure.rest.req.address;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CreateCustomerAddressRequest(
        @JsonProperty("title") String title,
        @JsonProperty("zip_code") String zipCode,
        @JsonProperty("number") String number,
        @JsonProperty("country") String country,
        @JsonProperty("complement") String complement,
        @JsonProperty("is_default") boolean isDefault
) {
}
