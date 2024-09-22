package com.kaua.ecommerce.customer.application.usecases.address.outputs;

import com.kaua.ecommerce.customer.domain.address.Address;

public record UpdateAddressOutput(
        String id,
        String customerId
) {

    public UpdateAddressOutput(final Address aAddress) {
        this(aAddress.getId().value().toString(), aAddress.getCustomerId().value().toString());
    }
}
