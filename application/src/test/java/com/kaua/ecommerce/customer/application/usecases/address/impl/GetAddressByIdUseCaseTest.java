package com.kaua.ecommerce.customer.application.usecases.address.impl;

import com.kaua.ecommerce.customer.application.UseCaseTest;
import com.kaua.ecommerce.customer.application.exceptions.UseCaseInputCannotBeNullException;
import com.kaua.ecommerce.customer.application.repositories.AddressRepository;
import com.kaua.ecommerce.customer.domain.Fixture;
import com.kaua.ecommerce.customer.domain.address.AddressId;
import com.kaua.ecommerce.customer.domain.customer.CustomerId;
import com.kaua.ecommerce.lib.domain.exceptions.NotFoundException;
import com.kaua.ecommerce.lib.domain.utils.IdentifierUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.Optional;

class GetAddressByIdUseCaseTest extends UseCaseTest {

    @Mock
    private AddressRepository addressRepository;

    @InjectMocks
    private DefaultGetAddressByIdUseCase getAddressByIdUseCase;

    @Test
    void givenAnInvalidNullInput_whenCallGetAddressById_thenThrowUseCaseInputCannotBeNullException() {
        final var expectedErrorMessage = "Input to DefaultGetAddressByIdUseCase cannot be null";

        final var aException = Assertions.assertThrows(UseCaseInputCannotBeNullException.class,
                () -> this.getAddressByIdUseCase.execute(null));

        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());

        Mockito.verifyNoInteractions(addressRepository);
    }

    @Test
    void givenAValidAddressId_whenCallGetAddressById_thenReturnGetAddressByIdOutput() {
        final var aAddress = Fixture.Addresses.newAddressWithComplement(
                new CustomerId(IdentifierUtils.generateNewUUID()),
                true
        );

        final var aAddressId = aAddress.getId();

        Mockito.when(addressRepository.addressOfId(aAddressId)).thenReturn(Optional.of(aAddress));

        final var aOutput = this.getAddressByIdUseCase.execute(aAddressId);

        Assertions.assertEquals(aAddressId.value().toString(), aOutput.id());
        Assertions.assertEquals(aAddress.getTitle().value(), aOutput.title());
        Assertions.assertEquals(aAddress.getCustomerId().value().toString(), aOutput.customerId());
        Assertions.assertEquals(aAddress.getZipCode(), aOutput.zipCode());
        Assertions.assertEquals(aAddress.getNumber(), aOutput.number());
        Assertions.assertEquals(aAddress.getStreet(), aOutput.street());
        Assertions.assertEquals(aAddress.getCity(), aOutput.city());
        Assertions.assertEquals(aAddress.getDistrict(), aOutput.district());
        Assertions.assertEquals(aAddress.getCountry(), aOutput.country());
        Assertions.assertEquals(aAddress.getState(), aOutput.state());
        Assertions.assertEquals(aAddress.getComplement().orElse(null), aOutput.complement());
        Assertions.assertEquals(aAddress.isDefault(), aOutput.isDefault());
        Assertions.assertEquals(aAddress.getCreatedAt(), aOutput.createdAt());
        Assertions.assertEquals(aAddress.getUpdatedAt(), aOutput.updatedAt());
        Assertions.assertEquals(aAddress.getVersion(), aOutput.version());

        Mockito.verify(addressRepository, Mockito.times(1)).addressOfId(aAddressId);
    }

    @Test
    void givenAnInvalidAddressId_whenCallGetAddressById_thenThrowNotFoundException() {
        final var aAddressId = new AddressId(IdentifierUtils.generateNewUUID());

        final var expectedErrorMessage = NotFoundException.ERROR_MESSAGE
                .formatted("Address", "id", aAddressId.value().toString());

        Mockito.when(addressRepository.addressOfId(aAddressId)).thenReturn(Optional.empty());

        final var aException = Assertions.assertThrows(NotFoundException.class,
                () -> this.getAddressByIdUseCase.execute(aAddressId));

        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());

        Mockito.verify(addressRepository, Mockito.times(1)).addressOfId(aAddressId);
    }
}
