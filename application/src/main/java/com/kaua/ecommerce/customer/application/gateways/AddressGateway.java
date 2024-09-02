package com.kaua.ecommerce.customer.application.gateways;

import java.util.Optional;

public interface AddressGateway {

    Optional<GetAddressByZipCodeResponse> getAddressByZipCode(String zipCode);

    record GetAddressByZipCodeResponse(
            String zipCode,
            String city,
            String street,
            String district,
            String state
    ) {}
}
