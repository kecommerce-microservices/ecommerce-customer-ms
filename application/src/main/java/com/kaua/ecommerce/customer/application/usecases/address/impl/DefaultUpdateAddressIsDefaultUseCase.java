package com.kaua.ecommerce.customer.application.usecases.address.impl;

import com.kaua.ecommerce.customer.application.exceptions.UseCaseInputCannotBeNullException;
import com.kaua.ecommerce.customer.application.repositories.AddressRepository;
import com.kaua.ecommerce.customer.application.usecases.address.UpdateAddressIsDefaultUseCase;
import com.kaua.ecommerce.customer.application.usecases.address.inputs.UpdateAddressIsDefaultInput;
import com.kaua.ecommerce.customer.application.usecases.address.outputs.UpdateAddressIsDefaultOutput;
import com.kaua.ecommerce.customer.domain.address.Address;
import com.kaua.ecommerce.customer.domain.address.AddressId;
import com.kaua.ecommerce.lib.domain.exceptions.NotFoundException;

import java.util.Objects;

public class DefaultUpdateAddressIsDefaultUseCase extends UpdateAddressIsDefaultUseCase {

    private final AddressRepository addressRepository;

    public DefaultUpdateAddressIsDefaultUseCase(final AddressRepository addressRepository) {
        this.addressRepository = Objects.requireNonNull(addressRepository);
    }

    @Override
    public UpdateAddressIsDefaultOutput execute(final UpdateAddressIsDefaultInput input) {
        if (input == null)
            throw new UseCaseInputCannotBeNullException(DefaultUpdateAddressIsDefaultUseCase.class.getSimpleName());

        final var aAddressId = new AddressId(input.addressId());

        final var aAddress = this.addressRepository.addressOfId(aAddressId)
                .orElseThrow(NotFoundException.with(Address.class, aAddressId));

        if (aAddress.isDefault() && input.isDefault()) {
            return new UpdateAddressIsDefaultOutput(aAddress);
        }

        if (!aAddress.isDefault() && !input.isDefault()) {
            return new UpdateAddressIsDefaultOutput(aAddress);
        }

        if (input.isDefault()) {
            this.addressRepository.addressByCustomerIdAndIsDefaultTrue(aAddress.getCustomerId())
                    .map(it -> it.updateIsDefault(false))
                    .ifPresent(this.addressRepository::save);

            final var aUpdatedAddress = aAddress.updateIsDefault(true);
            return new UpdateAddressIsDefaultOutput(this.addressRepository.save(aUpdatedAddress));
        }

        return new UpdateAddressIsDefaultOutput(this.addressRepository.save(aAddress.updateIsDefault(false)));
    }
}
