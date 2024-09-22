package com.kaua.ecommerce.customer.application.usecases.address.impl;

import com.kaua.ecommerce.customer.application.exceptions.UseCaseInputCannotBeNullException;
import com.kaua.ecommerce.customer.application.gateways.AddressGateway;
import com.kaua.ecommerce.customer.application.repositories.AddressRepository;
import com.kaua.ecommerce.customer.application.usecases.address.UpdateAddressUseCase;
import com.kaua.ecommerce.customer.application.usecases.address.inputs.UpdateAddressInput;
import com.kaua.ecommerce.customer.application.usecases.address.outputs.UpdateAddressOutput;
import com.kaua.ecommerce.customer.domain.address.Address;
import com.kaua.ecommerce.customer.domain.address.AddressId;
import com.kaua.ecommerce.customer.domain.address.Title;
import com.kaua.ecommerce.lib.domain.exceptions.NotFoundException;

import java.util.Objects;
import java.util.Optional;

public class DefaultUpdateAddressUseCase extends UpdateAddressUseCase {

    private final AddressGateway addressGateway;
    private final AddressRepository addressRepository;

    public DefaultUpdateAddressUseCase(
            final AddressGateway addressGateway,
            final AddressRepository addressRepository
    ) {
        this.addressGateway = Objects.requireNonNull(addressGateway);
        this.addressRepository = Objects.requireNonNull(addressRepository);
    }

    @Override
    public UpdateAddressOutput execute(final UpdateAddressInput input) {
        if (input == null) throw new UseCaseInputCannotBeNullException(DefaultUpdateAddressUseCase.class.getSimpleName());

        final var aAddressId = new AddressId(input.addressId());

        final var aAddress = this.addressRepository.addressOfId(aAddressId)
                .orElseThrow(NotFoundException.with(Address.class, aAddressId));

        final var aTitle = Optional.ofNullable(input.title())
                .map(Title::new)
                .orElse(aAddress.getTitle());

        final var aNumber = Optional.ofNullable(input.number())
                .orElse(aAddress.getNumber());
        final var aComplement = Optional.ofNullable(input.complement())
                .orElse(aAddress.getComplement().orElse(null));
        final var aStreet = Optional.ofNullable(input.street())
                .orElse(aAddress.getStreet());
        final var aDistrict = Optional.ofNullable(input.district())
                .orElse(aAddress.getDistrict());

        if (input.zipCode() == null) {
            final var aUpdatedAddress = aAddress.update(
                    aTitle,
                    aAddress.getZipCode(),
                    aNumber,
                    aStreet,
                    aAddress.getCity(),
                    aDistrict,
                    aAddress.getCountry(),
                    aAddress.getState(),
                    aComplement
            );

            return new UpdateAddressOutput(this.addressRepository.save(aUpdatedAddress));
        }

        final var aAddressResponse = this.addressGateway.getAddressByZipCode(input.zipCode())
                .orElseThrow(NotFoundException.with(Address.class, "zipCode", input.zipCode()));

        final var aUpdatedAddress = aAddress.update(
                aTitle,
                aAddressResponse.zipCode(),
                aNumber,
                aAddressResponse.street(),
                aAddressResponse.city(),
                aAddressResponse.district(),
                input.country(),
                aAddressResponse.state(),
                aComplement
        );

        return new UpdateAddressOutput(this.addressRepository.save(aUpdatedAddress));
    }
}
