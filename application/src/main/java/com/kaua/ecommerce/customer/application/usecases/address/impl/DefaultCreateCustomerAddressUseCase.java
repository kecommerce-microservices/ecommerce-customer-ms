package com.kaua.ecommerce.customer.application.usecases.address.impl;

import com.kaua.ecommerce.customer.application.exceptions.UseCaseInputCannotBeNullException;
import com.kaua.ecommerce.customer.application.gateways.AddressGateway;
import com.kaua.ecommerce.customer.application.repositories.AddressRepository;
import com.kaua.ecommerce.customer.application.usecases.address.CreateCustomerAddressUseCase;
import com.kaua.ecommerce.customer.application.usecases.address.inputs.CreateCustomerAddressInput;
import com.kaua.ecommerce.customer.application.usecases.address.outputs.CreateCustomerAddressOutput;
import com.kaua.ecommerce.customer.domain.address.Address;
import com.kaua.ecommerce.customer.domain.address.Title;
import com.kaua.ecommerce.customer.domain.customer.CustomerId;
import com.kaua.ecommerce.lib.domain.exceptions.DomainException;
import com.kaua.ecommerce.lib.domain.exceptions.NotFoundException;

import java.util.Objects;

public class DefaultCreateCustomerAddressUseCase extends CreateCustomerAddressUseCase {

    private final AddressRepository addressRepository;
    private final AddressGateway addressGateway;

    public DefaultCreateCustomerAddressUseCase(
            final AddressRepository addressRepository,
            final AddressGateway addressGateway
    ) {
        this.addressRepository = Objects.requireNonNull(addressRepository);
        this.addressGateway = Objects.requireNonNull(addressGateway);
    }

    @Override
    public CreateCustomerAddressOutput execute(final CreateCustomerAddressInput input) {
        if (input == null) throw new UseCaseInputCannotBeNullException(DefaultCreateCustomerAddressUseCase.class.getSimpleName());

        final var aCustomerId = new CustomerId(input.customerId());

        if (this.addressRepository.countByCustomerId(aCustomerId) >= 5) {
            throw DomainException.with("Customer can't have more than 5 addresses");
        }

        // check if exists other default address
        // outra opção seria pegar o default address, desativar e deixar esse como default

        if (input.isDefault() && this.addressRepository.existsByCustomerIdAndIsDefaultTrue(aCustomerId)) {
            throw DomainException.with("Customer already has a default address");
        }

        final var aAddressResponse = this.addressGateway.getAddressByZipCode(input.zipCode())
                .orElseThrow(NotFoundException.with(Address.class, "zipCode", input.zipCode()));

        final var aTitle = new Title(input.title());

        final var aAddress = Address.newAddress(
                aTitle,
                aCustomerId,
                aAddressResponse.zipCode(),
                input.number(),
                aAddressResponse.street(),
                aAddressResponse.city(),
                aAddressResponse.district(),
                input.country(),
                aAddressResponse.state(),
                input.complement(),
                input.isDefault()
        );

        return new CreateCustomerAddressOutput(this.addressRepository.save(aAddress));
    }
}
