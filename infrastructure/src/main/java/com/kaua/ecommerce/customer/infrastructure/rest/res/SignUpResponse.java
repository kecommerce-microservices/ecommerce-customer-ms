package com.kaua.ecommerce.customer.infrastructure.rest.res;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaua.ecommerce.customer.application.usecases.customer.outputs.CreateCustomerOutput;

public record SignUpResponse(
        @JsonProperty("customer_id") String customerId,
        @JsonProperty("idp_user_id") String idpUserId
) {

    public SignUpResponse(final CreateCustomerOutput aOutput) {
        this(aOutput.customerId().toString(), aOutput.userId().toString());
    }
}
