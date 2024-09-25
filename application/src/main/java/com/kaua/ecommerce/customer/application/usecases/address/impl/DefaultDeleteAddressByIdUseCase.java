package com.kaua.ecommerce.customer.application.usecases.address.impl;

import com.kaua.ecommerce.customer.application.exceptions.UseCaseInputCannotBeNullException;
import com.kaua.ecommerce.customer.application.repositories.AddressRepository;
import com.kaua.ecommerce.customer.application.usecases.address.DeleteAddressByIdUseCase;
import com.kaua.ecommerce.customer.domain.address.AddressId;

import java.util.Objects;

public class DefaultDeleteAddressByIdUseCase extends DeleteAddressByIdUseCase {

    private final AddressRepository addressRepository;

    public DefaultDeleteAddressByIdUseCase(final AddressRepository addressRepository) {
        this.addressRepository = Objects.requireNonNull(addressRepository);
    }

    @Override
    public void execute(final AddressId input) {
        if (input == null) throw new UseCaseInputCannotBeNullException(DefaultDeleteAddressByIdUseCase.class.getSimpleName());

        this.addressRepository.delete(input);
    }
}
