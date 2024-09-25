package com.kaua.ecommerce.customer.application.usecases.address.impl;

import com.kaua.ecommerce.customer.application.UseCaseTest;
import com.kaua.ecommerce.customer.application.exceptions.UseCaseInputCannotBeNullException;
import com.kaua.ecommerce.customer.application.repositories.AddressRepository;
import com.kaua.ecommerce.customer.domain.address.AddressId;
import com.kaua.ecommerce.lib.domain.utils.IdentifierUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

class DeleteAddressByIdUseCaseTest extends UseCaseTest {

    @Mock
    private AddressRepository addressRepository;

    @InjectMocks
    private DefaultDeleteAddressByIdUseCase deleteAddressByIdUseCase;

    @Test
    void givenAnInvalidNullInput_whenCallDeleteAddressById_thenThrowsUseCaseInputCannotBeNullException() {
        final var expectedErrorMessage = "Input to DefaultDeleteAddressByIdUseCase cannot be null";

        final var aException = Assertions.assertThrows(UseCaseInputCannotBeNullException.class,
                () -> this.deleteAddressByIdUseCase.execute(null));

        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());

        Mockito.verifyNoInteractions(addressRepository);
    }

    @Test
    void givenAValidAddressId_whenCallDeleteAddressById_thenDeleteAddressById() {
        final var aAddressId = new AddressId(IdentifierUtils.generateNewUUID());

        Mockito.doNothing().when(addressRepository).delete(aAddressId);

        Assertions.assertDoesNotThrow(() -> this.deleteAddressByIdUseCase.execute(aAddressId));

        Mockito.verify(addressRepository, Mockito.times(1)).delete(aAddressId);
    }
}
