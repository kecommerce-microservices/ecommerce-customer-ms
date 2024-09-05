package com.kaua.ecommerce.customer.application.usecases.address.impl;

import com.kaua.ecommerce.customer.application.UseCaseTest;
import com.kaua.ecommerce.customer.application.exceptions.UseCaseInputCannotBeNullException;
import com.kaua.ecommerce.customer.application.repositories.AddressRepository;
import com.kaua.ecommerce.customer.application.usecases.address.inputs.UpdateAddressIsDefaultInput;
import com.kaua.ecommerce.customer.domain.Fixture;
import com.kaua.ecommerce.customer.domain.address.Address;
import com.kaua.ecommerce.customer.domain.customer.CustomerId;
import com.kaua.ecommerce.lib.domain.exceptions.NotFoundException;
import com.kaua.ecommerce.lib.domain.utils.IdentifierUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.Optional;

import static org.mockito.AdditionalAnswers.returnsFirstArg;

class UpdateAddressIsDefaultUseCaseTest extends UseCaseTest {

    @Mock
    private AddressRepository addressRepository;

    @InjectMocks
    private DefaultUpdateAddressIsDefaultUseCase updateAddressIsDefaultUseCase;

    @Test
    void givenAnInvalidNullInput_whenCallExecute_thenThrowUseCaseInputCannotBeNullException() {
        final var expectedErrorMessage = "Input to DefaultUpdateAddressIsDefaultUseCase cannot be null";

        final var aException = Assertions.assertThrows(UseCaseInputCannotBeNullException.class,
                () -> this.updateAddressIsDefaultUseCase.execute(null));

        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());

        Mockito.verify(addressRepository, Mockito.times(0)).addressOfId(Mockito.any());
        Mockito.verify(addressRepository, Mockito.times(0)).addressByCustomerIdAndIsDefaultTrue(Mockito.any());
        Mockito.verify(addressRepository, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void givenAnInvalidAddressId_whenCallExecute_thenThrowNotFoundException() {
        final var aAddressId = IdentifierUtils.generateNewUUID();
        final var expectedErrorMessage = NotFoundException.ERROR_MESSAGE.formatted(Address.class.getSimpleName(), "id", aAddressId);

        final var aInput = new UpdateAddressIsDefaultInput(aAddressId, true);

        Mockito.when(addressRepository.addressOfId(Mockito.any())).thenReturn(Optional.empty());

        final var aException = Assertions.assertThrows(NotFoundException.class,
                () -> this.updateAddressIsDefaultUseCase.execute(aInput));

        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());

        Mockito.verify(addressRepository, Mockito.times(1)).addressOfId(Mockito.any());
        Mockito.verify(addressRepository, Mockito.times(0)).addressByCustomerIdAndIsDefaultTrue(Mockito.any());
        Mockito.verify(addressRepository, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void givenAValidAddressIdAndIsDefaultTrueOnAddressIsDefaultTrue_whenCallExecute_thenReturnUpdateAddressIsDefaultOutput() {
        final var aCustomerId = new CustomerId(IdentifierUtils.generateNewUUID());
        final var aAddress = Fixture.Addresses.newAddressWithComplement(
                aCustomerId,
                true
        );

        final var aIsDefault = true;

        final var aInput = new UpdateAddressIsDefaultInput(aCustomerId.value(), aIsDefault);

        Mockito.when(addressRepository.addressOfId(Mockito.any())).thenReturn(Optional.of(aAddress));

        final var aOutput = Assertions.assertDoesNotThrow(() -> this.updateAddressIsDefaultUseCase.execute(aInput));

        Assertions.assertNotNull(aOutput);
        Assertions.assertEquals(aAddress.getId().value().toString(), aOutput.addressId());
        Assertions.assertEquals(aIsDefault, aOutput.isDefault());

        Mockito.verify(addressRepository, Mockito.times(1)).addressOfId(Mockito.any());
        Mockito.verify(addressRepository, Mockito.times(0)).addressByCustomerIdAndIsDefaultTrue(aCustomerId);
        Mockito.verify(addressRepository, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void givenAValidAddressIdAndIsDefaultFalseOnAddressIsDefaultFalse_whenCallExecute_thenReturnUpdateAddressIsDefaultOutput() {
        final var aCustomerId = new CustomerId(IdentifierUtils.generateNewUUID());
        final var aAddress = Fixture.Addresses.newAddressWithComplement(
                aCustomerId,
                false
        );

        final var aIsDefault = false;

        final var aInput = new UpdateAddressIsDefaultInput(aCustomerId.value(), aIsDefault);

        Mockito.when(addressRepository.addressOfId(Mockito.any())).thenReturn(Optional.of(aAddress));

        final var aOutput = Assertions.assertDoesNotThrow(() -> this.updateAddressIsDefaultUseCase.execute(aInput));

        Assertions.assertNotNull(aOutput);
        Assertions.assertEquals(aAddress.getId().value().toString(), aOutput.addressId());
        Assertions.assertEquals(aIsDefault, aOutput.isDefault());

        Mockito.verify(addressRepository, Mockito.times(1)).addressOfId(Mockito.any());
        Mockito.verify(addressRepository, Mockito.times(0)).addressByCustomerIdAndIsDefaultTrue(aCustomerId);
        Mockito.verify(addressRepository, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void givenAValidAddressIdAndIsDefaultTrueOnAddressIsDefault_whenCallExecute_thenReturnUpdateAddressIsDefaultOutput() {
        final var aCustomerId = new CustomerId(IdentifierUtils.generateNewUUID());
        final var aAddress = Fixture.Addresses.newAddressWithComplement(
                aCustomerId,
                false
        );

        final var aIsDefault = true;

        final var aInput = new UpdateAddressIsDefaultInput(aCustomerId.value(), aIsDefault);

        Mockito.when(addressRepository.addressOfId(Mockito.any())).thenReturn(Optional.of(aAddress));
        Mockito.when(addressRepository.addressByCustomerIdAndIsDefaultTrue(aCustomerId))
                .thenReturn(Optional.of(Fixture.Addresses.newAddressWithComplement(aCustomerId, true)));
        Mockito.when(addressRepository.save(Mockito.any())).thenAnswer(returnsFirstArg());

        final var aOutput = Assertions.assertDoesNotThrow(() -> this.updateAddressIsDefaultUseCase.execute(aInput));

        Assertions.assertNotNull(aOutput);
        Assertions.assertEquals(aAddress.getId().value().toString(), aOutput.addressId());
        Assertions.assertEquals(aIsDefault, aOutput.isDefault());

        Mockito.verify(addressRepository, Mockito.times(1)).addressOfId(Mockito.any());
        Mockito.verify(addressRepository, Mockito.times(1)).addressByCustomerIdAndIsDefaultTrue(aCustomerId);
        Mockito.verify(addressRepository, Mockito.times(2)).save(Mockito.any());
    }

    @Test
    void givenAValidAddressIdAndIsDefaultFalseOnAddressIsDefault_whenCallExecute_thenReturnUpdateAddressIsDefaultOutput() {
        final var aCustomerId = new CustomerId(IdentifierUtils.generateNewUUID());
        final var aAddress = Fixture.Addresses.newAddressWithComplement(
                aCustomerId,
                true
        );

        final var aIsDefault = false;

        final var aInput = new UpdateAddressIsDefaultInput(aCustomerId.value(), aIsDefault);

        Mockito.when(addressRepository.addressOfId(Mockito.any())).thenReturn(Optional.of(aAddress));
        Mockito.when(addressRepository.save(Mockito.any())).thenAnswer(returnsFirstArg());

        final var aOutput = Assertions.assertDoesNotThrow(() -> this.updateAddressIsDefaultUseCase.execute(aInput));

        Assertions.assertNotNull(aOutput);
        Assertions.assertEquals(aAddress.getId().value().toString(), aOutput.addressId());
        Assertions.assertEquals(aIsDefault, aOutput.isDefault());

        Mockito.verify(addressRepository, Mockito.times(1)).addressOfId(Mockito.any());
        Mockito.verify(addressRepository, Mockito.times(1)).save(Mockito.any());
    }
}