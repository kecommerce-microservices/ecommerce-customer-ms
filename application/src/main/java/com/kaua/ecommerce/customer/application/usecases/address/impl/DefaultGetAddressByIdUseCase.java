package com.kaua.ecommerce.customer.application.usecases.address.impl;

import com.kaua.ecommerce.customer.application.exceptions.UseCaseInputCannotBeNullException;
import com.kaua.ecommerce.customer.application.repositories.AddressRepository;
import com.kaua.ecommerce.customer.application.usecases.address.GetAddressByIdUseCase;
import com.kaua.ecommerce.customer.application.usecases.address.outputs.GetAddressByIdOutput;
import com.kaua.ecommerce.customer.domain.address.Address;
import com.kaua.ecommerce.customer.domain.address.AddressId;
import com.kaua.ecommerce.lib.domain.exceptions.NotFoundException;

import java.util.Objects;

public class DefaultGetAddressByIdUseCase extends GetAddressByIdUseCase {

    private final AddressRepository addressRepository;

    public DefaultGetAddressByIdUseCase(final AddressRepository addressRepository) {
        this.addressRepository = Objects.requireNonNull(addressRepository);
    }

    @Override
    public GetAddressByIdOutput execute(final AddressId input) {
        if (input == null) throw new UseCaseInputCannotBeNullException(DefaultGetAddressByIdUseCase.class.getSimpleName());

        return this.addressRepository.addressOfId(input)
                .map(GetAddressByIdOutput::new)
                .orElseThrow(NotFoundException.with(Address.class, input));
    }
}
