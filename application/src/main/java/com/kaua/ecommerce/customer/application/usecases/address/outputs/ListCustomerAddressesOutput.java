package com.kaua.ecommerce.customer.application.usecases.address.outputs;

import com.kaua.ecommerce.customer.domain.address.Address;

import java.time.Instant;

public record ListCustomerAddressesOutput(
        String id,
        String customerId,
        String title,
        String zipCode,
        String number,
        String street,
        String state,
        String city,
        String district,
        Instant createdAt,
        Instant updatedAt
) {

    public ListCustomerAddressesOutput(final Address aAddress) {
        this(
                aAddress.getId().value().toString(),
                aAddress.getCustomerId().value().toString(),
                aAddress.getTitle().value(),
                aAddress.getZipCode(),
                aAddress.getNumber(),
                aAddress.getStreet(),
                aAddress.getState(),
                aAddress.getCity(),
                aAddress.getDistrict(),
                aAddress.getCreatedAt(),
                aAddress.getUpdatedAt()
        );
    }
}
