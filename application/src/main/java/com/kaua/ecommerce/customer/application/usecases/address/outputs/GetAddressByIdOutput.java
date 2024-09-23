package com.kaua.ecommerce.customer.application.usecases.address.outputs;

import com.kaua.ecommerce.customer.domain.address.Address;

import java.time.Instant;

public record GetAddressByIdOutput(
        String id,
        String title,
        String customerId,
        String zipCode,
        String number,
        String street,
        String city,
        String district,
        String country,
        String state,
        String complement,
        boolean isDefault,
        Instant createdAt,
        Instant updatedAt,
        long version
) {

    public GetAddressByIdOutput(final Address aAddress) {
        this(
                aAddress.getId().value().toString(),
                aAddress.getTitle().value(),
                aAddress.getCustomerId().value().toString(),
                aAddress.getZipCode(),
                aAddress.getNumber(),
                aAddress.getStreet(),
                aAddress.getCity(),
                aAddress.getDistrict(),
                aAddress.getCountry(),
                aAddress.getState(),
                aAddress.getComplement().orElse(null),
                aAddress.isDefault(),
                aAddress.getCreatedAt(),
                aAddress.getUpdatedAt(),
                aAddress.getVersion()
        );
    }
}
