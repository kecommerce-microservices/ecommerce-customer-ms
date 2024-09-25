package com.kaua.ecommerce.customer.application.usecases.address.impl;

import com.kaua.ecommerce.customer.application.exceptions.UseCaseInputCannotBeNullException;
import com.kaua.ecommerce.customer.application.repositories.AddressRepository;
import com.kaua.ecommerce.customer.application.usecases.address.ListCustomerAddressesUseCase;
import com.kaua.ecommerce.customer.application.usecases.address.inputs.ListCustomerAddressesInput;
import com.kaua.ecommerce.customer.application.usecases.address.outputs.ListCustomerAddressesOutput;
import com.kaua.ecommerce.lib.domain.pagination.Pagination;

import java.util.Objects;

public class DefaultListCustomerAddressesUseCase extends ListCustomerAddressesUseCase {

    private final AddressRepository addressRepository;

    public DefaultListCustomerAddressesUseCase(final AddressRepository addressRepository) {
        this.addressRepository = Objects.requireNonNull(addressRepository);
    }

    @Override
    public Pagination<ListCustomerAddressesOutput> execute(final ListCustomerAddressesInput input) {
        if (input == null) throw new UseCaseInputCannotBeNullException(DefaultListCustomerAddressesUseCase.class.getSimpleName());

        return this.addressRepository.addressesByCustomerId(input.customerId(), input.searchQuery())
                .map(ListCustomerAddressesOutput::new);
    }
}
