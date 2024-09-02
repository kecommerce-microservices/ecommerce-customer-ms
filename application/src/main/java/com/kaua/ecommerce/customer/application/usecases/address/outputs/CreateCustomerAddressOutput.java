package com.kaua.ecommerce.customer.application.usecases.address.outputs;

import com.kaua.ecommerce.customer.domain.address.Address;

public record CreateCustomerAddressOutput(
        String addressId,
        String customerId
) {

    public CreateCustomerAddressOutput(final Address aAddress) {
        this(aAddress.getId().value().toString(), aAddress.getCustomerId().value().toString());
    }
}
