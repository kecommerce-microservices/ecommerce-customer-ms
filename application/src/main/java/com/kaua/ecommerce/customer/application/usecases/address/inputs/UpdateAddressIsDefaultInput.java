package com.kaua.ecommerce.customer.application.usecases.address.inputs;

import java.util.UUID;

public record UpdateAddressIsDefaultInput(
        UUID addressId,
        boolean isDefault
) {
}
