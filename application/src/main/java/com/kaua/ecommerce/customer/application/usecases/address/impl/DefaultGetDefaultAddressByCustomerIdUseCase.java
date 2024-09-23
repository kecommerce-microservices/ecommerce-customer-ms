package com.kaua.ecommerce.customer.application.usecases.address.impl;

import com.kaua.ecommerce.customer.application.exceptions.UseCaseInputCannotBeNullException;
import com.kaua.ecommerce.customer.application.repositories.AddressRepository;
import com.kaua.ecommerce.customer.application.usecases.address.GetDefaultAddressByCustomerIdUseCase;
import com.kaua.ecommerce.customer.application.usecases.address.outputs.GetDefaultAddressByCustomerIdOutput;
import com.kaua.ecommerce.customer.domain.address.Address;
import com.kaua.ecommerce.customer.domain.customer.CustomerId;
import com.kaua.ecommerce.lib.domain.exceptions.NotFoundException;

import java.util.Objects;

public class DefaultGetDefaultAddressByCustomerIdUseCase extends GetDefaultAddressByCustomerIdUseCase {

    private final AddressRepository addressRepository;

    public DefaultGetDefaultAddressByCustomerIdUseCase(final AddressRepository addressRepository) {
        this.addressRepository = Objects.requireNonNull(addressRepository);
    }

    @Override
    public GetDefaultAddressByCustomerIdOutput execute(final CustomerId input) {
        if (input == null) throw new UseCaseInputCannotBeNullException(DefaultGetDefaultAddressByCustomerIdUseCase.class.getSimpleName());

        return this.addressRepository.addressByCustomerIdAndIsDefaultTrue(input)
                .map(GetDefaultAddressByCustomerIdOutput::new)
                .orElseThrow(NotFoundException.with(Address.class, "customerId", input.value().toString()));
    }
}
