package com.kaua.ecommerce.customer.application.usecases.address.impl;

import com.kaua.ecommerce.customer.application.UseCaseTest;
import com.kaua.ecommerce.customer.application.exceptions.UseCaseInputCannotBeNullException;
import com.kaua.ecommerce.customer.application.repositories.AddressRepository;
import com.kaua.ecommerce.customer.domain.Fixture;
import com.kaua.ecommerce.customer.domain.customer.CustomerId;
import com.kaua.ecommerce.lib.domain.exceptions.NotFoundException;
import com.kaua.ecommerce.lib.domain.utils.IdentifierUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.Optional;

class GetDefaultAddressByCustomerIdUseCaseTest extends UseCaseTest {

    @Mock
    private AddressRepository addressRepository;

    @InjectMocks
    private DefaultGetDefaultAddressByCustomerIdUseCase getDefaultAddressByCustomerIdUseCase;

    @Test
    void givenAnInvalidNullInput_whenCallGetDefaultAddressByCustomerId_thenThrowUseCaseInputCannotBeNullException() {
        final var expectedErrorMessage = "Input to DefaultGetDefaultAddressByCustomerIdUseCase cannot be null";

        final var aException = Assertions.assertThrows(UseCaseInputCannotBeNullException.class,
                () -> this.getDefaultAddressByCustomerIdUseCase.execute(null));

        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());

        Mockito.verifyNoInteractions(addressRepository);
    }

    @Test
    void givenAValidInput_whenCallGetDefaultAddressByCustomerId_thenReturnGetDefaultAddressByCustomerIdOutput() {
        final var aCustomerId = new CustomerId(IdentifierUtils.generateNewUUID());

        final var aAddress = Fixture.Addresses.newAddressWithComplement(aCustomerId, true);

        Mockito.when(addressRepository.addressByCustomerIdAndIsDefaultTrue(aCustomerId))
                .thenReturn(Optional.of(aAddress));

        final var aOutput = this.getDefaultAddressByCustomerIdUseCase.execute(aCustomerId);

        Assertions.assertEquals(aAddress.getId().value().toString(), aOutput.id());
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

        Mockito.verify(addressRepository, Mockito.times(1)).addressByCustomerIdAndIsDefaultTrue(aCustomerId);
    }

    @Test
    void givenAnInvalidCustomerId_whenCallGetDefaultAddressByCustomerId_thenThrowNotFoundException() {
        final var aCustomerId = new CustomerId(IdentifierUtils.generateNewUUID());

        final var expectedErrorMessage = "Address with customerId %s was not found".formatted(aCustomerId.value().toString());

        Mockito.when(addressRepository.addressByCustomerIdAndIsDefaultTrue(aCustomerId))
                .thenReturn(Optional.empty());


        final var aException = Assertions.assertThrows(NotFoundException.class,
                () -> this.getDefaultAddressByCustomerIdUseCase.execute(aCustomerId));

        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());

        Mockito.verify(addressRepository, Mockito.times(1)).addressByCustomerIdAndIsDefaultTrue(aCustomerId);
    }
}
