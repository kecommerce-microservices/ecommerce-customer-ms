package com.kaua.ecommerce.customer.infrastructure.rest.res;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaua.ecommerce.customer.application.usecases.customer.outputs.GetCustomerByIdentifierOutput;

import java.time.Instant;

public record GetCustomerByIdentifierResponse(
        @JsonProperty("customer_id") String customerId,
        @JsonProperty("user_id") String userId,
        @JsonProperty("first_name") String firstName,
        @JsonProperty("last_name") String lastName,
        @JsonProperty("full_name") String fullName,
        @JsonProperty("email") String email,
        @JsonProperty("document") GetCustomerByIdentifierDocumentResponse document,
        @JsonProperty("telephone") GetCustomerByIdentifierTelephoneResponse telephone,
        @JsonProperty("created_at") Instant createdAt,
        @JsonProperty("updated_at") Instant updatedAt,
        @JsonProperty("version") long version
) {

    public GetCustomerByIdentifierResponse(final GetCustomerByIdentifierOutput aOutput) {
        this(
                aOutput.customerId(),
                aOutput.userId(),
                aOutput.firstName(),
                aOutput.lastName(),
                aOutput.fullName(),
                aOutput.email(),
                aOutput.document() != null ? new GetCustomerByIdentifierDocumentResponse(
                        aOutput.document().documentNumber(),
                        aOutput.document().documentType()
                ) : null,
                aOutput.telephone() != null ? new GetCustomerByIdentifierTelephoneResponse(
                        aOutput.telephone().phoneNumber(),
                        aOutput.telephone().countryCode(),
                        aOutput.telephone().regionCode()
                ) : null,
                aOutput.createdAt(),
                aOutput.updatedAt(),
                aOutput.version()
        );
    }

    public record GetCustomerByIdentifierDocumentResponse(
            @JsonProperty("number") String number,
            @JsonProperty("type") String type
    ) {}

    public record GetCustomerByIdentifierTelephoneResponse(
            @JsonProperty("phone_number") String phoneNumber,
            @JsonProperty("country_code") String countryCode,
            @JsonProperty("region_code") String regionCode
    ) {}
}
