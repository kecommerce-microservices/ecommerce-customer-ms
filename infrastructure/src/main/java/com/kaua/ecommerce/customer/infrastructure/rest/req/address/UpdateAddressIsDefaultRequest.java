package com.kaua.ecommerce.customer.infrastructure.rest.req.address;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UpdateAddressIsDefaultRequest(
        @JsonProperty("is_default") boolean isDefault
) {
}
