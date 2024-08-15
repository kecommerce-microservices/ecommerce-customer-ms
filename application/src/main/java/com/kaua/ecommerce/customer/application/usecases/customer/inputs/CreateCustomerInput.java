package com.kaua.ecommerce.customer.application.usecases.customer.inputs;

import java.util.UUID;

public record CreateCustomerInput(
        UUID userId,
        UUID customerId,
        String firstName,
        String lastName,
        String email
) {
}
