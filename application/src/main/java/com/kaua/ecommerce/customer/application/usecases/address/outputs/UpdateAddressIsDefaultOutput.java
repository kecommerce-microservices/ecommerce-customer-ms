package com.kaua.ecommerce.customer.application.usecases.address.outputs;

import com.kaua.ecommerce.customer.domain.address.Address;

public record UpdateAddressIsDefaultOutput(
        String addressId,
        boolean isDefault
) {

    public UpdateAddressIsDefaultOutput(final Address aAddress) {
        this(aAddress.getId().value().toString(), aAddress.isDefault());
    }
}
