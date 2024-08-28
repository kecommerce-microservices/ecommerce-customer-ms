package com.kaua.ecommerce.customer.application.usecases.customer.inputs;

import java.util.UUID;

public record UpdateCustomerTelephoneInput(
        UUID customerId,
        String telephone
) {
}
