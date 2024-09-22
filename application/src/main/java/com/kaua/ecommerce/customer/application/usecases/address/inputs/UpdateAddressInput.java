package com.kaua.ecommerce.customer.application.usecases.address.inputs;

import java.util.UUID;

public record UpdateAddressInput(
        UUID addressId,
        String title,
        String zipCode,
        String number,
        String complement,
        String street,
        String district,
        String country
) {
}
