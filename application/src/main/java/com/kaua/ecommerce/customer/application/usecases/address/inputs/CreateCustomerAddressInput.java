package com.kaua.ecommerce.customer.application.usecases.address.inputs;

import java.util.UUID;

public record CreateCustomerAddressInput(
        UUID customerId,
        String title,
        String zipCode,
        String number,
        String complement,
        String country,
        boolean isDefault
) {
}
