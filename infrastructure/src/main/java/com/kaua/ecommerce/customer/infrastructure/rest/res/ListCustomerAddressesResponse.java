package com.kaua.ecommerce.customer.infrastructure.rest.res;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaua.ecommerce.customer.application.usecases.address.outputs.ListCustomerAddressesOutput;

import java.time.Instant;

public record ListCustomerAddressesResponse(
        @JsonProperty("id") String id,
        @JsonProperty("customer_id") String customerId,
        @JsonProperty("title") String title,
        @JsonProperty("zip_code") String zipCode,
        @JsonProperty("number") String number,
        @JsonProperty("street") String street,
        @JsonProperty("state") String state,
        @JsonProperty("city") String city,
        @JsonProperty("district") String district,
        @JsonProperty("created_at") Instant createdAt,
        @JsonProperty("updated_at") Instant updatedAt
) {

    public ListCustomerAddressesResponse(final ListCustomerAddressesOutput aOutput) {
        this(
                aOutput.id(),
                aOutput.customerId(),
                aOutput.title(),
                aOutput.zipCode(),
                aOutput.number(),
                aOutput.street(),
                aOutput.state(),
                aOutput.city(),
                aOutput.district(),
                aOutput.createdAt(),
                aOutput.updatedAt()
        );
    }
}
