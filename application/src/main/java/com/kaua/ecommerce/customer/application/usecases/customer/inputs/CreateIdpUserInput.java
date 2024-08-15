package com.kaua.ecommerce.customer.application.usecases.customer.inputs;

import java.util.UUID;

public record CreateIdpUserInput(
        UUID customerId,
        String firstName,
        String lastName,
        String email,
        String password
) {
}
