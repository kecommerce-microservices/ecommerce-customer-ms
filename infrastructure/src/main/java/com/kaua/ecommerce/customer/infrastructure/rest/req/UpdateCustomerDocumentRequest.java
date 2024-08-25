package com.kaua.ecommerce.customer.infrastructure.rest.req;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UpdateCustomerDocumentRequest(
        @JsonProperty("document_number") String documentNumber,
        @JsonProperty("document_type") String documentType
) {
}
