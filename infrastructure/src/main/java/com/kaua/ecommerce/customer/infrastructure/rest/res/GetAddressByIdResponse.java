package com.kaua.ecommerce.customer.infrastructure.rest.res;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaua.ecommerce.customer.application.usecases.address.outputs.GetAddressByIdOutput;

import java.time.Instant;

public record GetAddressByIdResponse(
        @JsonProperty("id") String id,
        @JsonProperty("title") String title,
        @JsonProperty("customer_id") String customerId,
        @JsonProperty("zip_code") String zipCode,
        @JsonProperty("number") String number,
        @JsonProperty("street") String street,
        @JsonProperty("city") String city,
        @JsonProperty("district") String district,
        @JsonProperty("country") String country,
        @JsonProperty("state") String state,
        @JsonProperty("complement") String complement,
        @JsonProperty("is_default") boolean isDefault,
        @JsonProperty("created_at") Instant createdAt,
        @JsonProperty("updated_at") Instant updatedAt,
        @JsonProperty("version") long version
) {

    public GetAddressByIdResponse(final GetAddressByIdOutput aOutput) {
        this(
                aOutput.id(),
                aOutput.title(),
                aOutput.customerId(),
                aOutput.zipCode(),
                aOutput.number(),
                aOutput.street(),
                aOutput.city(),
                aOutput.district(),
                aOutput.country(),
                aOutput.state(),
                aOutput.complement(),
                aOutput.isDefault(),
                aOutput.createdAt(),
                aOutput.updatedAt(),
                aOutput.version()
        );
    }
}
