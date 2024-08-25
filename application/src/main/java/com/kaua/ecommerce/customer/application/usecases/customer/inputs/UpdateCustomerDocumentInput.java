package com.kaua.ecommerce.customer.application.usecases.customer.inputs;

import java.util.UUID;

public record UpdateCustomerDocumentInput(
        UUID customerId,
        String documentNumber,
        String documentType
) {
}
