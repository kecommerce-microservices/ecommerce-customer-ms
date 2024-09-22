package com.kaua.ecommerce.customer.infrastructure.rest.req.address;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UpdateAddressRequest(
        @JsonProperty("title") String title,
        @JsonProperty("zip_code") String zipCode,
        @JsonProperty("number") String number,
        @JsonProperty("complement") String complement,
        @JsonProperty("street") String street,
        @JsonProperty("district") String district,
        @JsonProperty("country") String country
) {
}
