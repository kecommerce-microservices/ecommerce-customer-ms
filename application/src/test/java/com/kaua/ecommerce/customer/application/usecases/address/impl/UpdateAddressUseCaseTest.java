package com.kaua.ecommerce.customer.application.usecases.address.impl;

import com.kaua.ecommerce.customer.application.UseCaseTest;
import com.kaua.ecommerce.customer.application.exceptions.UseCaseInputCannotBeNullException;
import com.kaua.ecommerce.customer.application.gateways.AddressGateway;
import com.kaua.ecommerce.customer.application.repositories.AddressRepository;
import com.kaua.ecommerce.customer.application.usecases.address.inputs.UpdateAddressInput;
import com.kaua.ecommerce.customer.domain.Fixture;
import com.kaua.ecommerce.customer.domain.customer.CustomerId;
import com.kaua.ecommerce.lib.domain.exceptions.NotFoundException;
import com.kaua.ecommerce.lib.domain.utils.IdentifierUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.argThat;

class UpdateAddressUseCaseTest extends UseCaseTest {

    @Mock
    private AddressGateway addressGateway;

    @Mock
    private AddressRepository addressRepository;

    @InjectMocks
    private DefaultUpdateAddressUseCase updateAddressUseCase;

    @Test
    void givenAnInvalidNullInput_whenCallUpdateAddress_thenThrowUseCaseCannotBeNullException() {
        final var expectedErrorMessage = "Input to DefaultUpdateAddressUseCase cannot be null";

        final var aException = Assertions.assertThrows(UseCaseInputCannotBeNullException.class,
                () -> this.updateAddressUseCase.execute(null));

        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());
    }

    @Test
    void givenAnNonExistsAddressId_whenCallUpdateAddress_thenThrowNotFoundException() {
        final var aAddressId = UUID.randomUUID();

        final var aInput = new UpdateAddressInput(aAddressId, null, null, null, null, null, null, null);

        final var expectedErrorMessage = NotFoundException.ERROR_MESSAGE
                .formatted("Address", "id", aAddressId);

        Mockito.when(addressRepository.addressOfId(Mockito.any())).thenReturn(Optional.empty());

        final var aException = Assertions.assertThrows(NotFoundException.class,
                () -> this.updateAddressUseCase.execute(aInput));

        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());

        Mockito.verify(addressRepository, Mockito.times(1)).addressOfId(Mockito.any());
        Mockito.verifyNoMoreInteractions(addressRepository);
    }

    @Test
    void givenAnValidInput_whenCallUpdateAddress_thenReturnUpdatedAddress() {
        final var aAddress = Fixture.Addresses.newAddressWithComplement(
                new CustomerId(IdentifierUtils.generateNewUUID()),
                false
        );

        final var aAddressId = aAddress.getId().value();
        final var aTitle = "New Title";
        final String aZipCode = null;
        final String aNumber = "1010";
        final String aComplement = "New Complement";
        final String aStreet = "New Street";
        final String aDistrict = "New District";
        final String aCountry = "New Country";

        final var aInput = new UpdateAddressInput(
                aAddressId,
                aTitle,
                aZipCode,
                aNumber,
                aComplement,
                aStreet,
                aDistrict,
                aCountry
        );

        Mockito.when(addressRepository.addressOfId(Mockito.any())).thenReturn(Optional.of(aAddress));
        Mockito.when(addressRepository.save(Mockito.any())).thenAnswer(returnsFirstArg());

        final var aOutput = this.updateAddressUseCase.execute(aInput);

        Assertions.assertEquals(aAddressId.toString(), aOutput.id());
        Assertions.assertEquals(aAddress.getCustomerId().value().toString(), aOutput.customerId());

        Mockito.verify(addressRepository, Mockito.times(1)).addressOfId(Mockito.any());
        Mockito.verify(addressRepository, Mockito.times(1)).save(argThat(cmd ->
                Objects.equals(aTitle, cmd.getTitle().value())
                        && Objects.equals(aNumber, cmd.getNumber())
                        && Objects.equals(aComplement, cmd.getComplement().orElse(null))
                        && Objects.equals(aStreet, cmd.getStreet())
                        && Objects.equals(aDistrict, cmd.getDistrict())
        ));
    }

    @Test
    void givenAnValidInput_whenCallUpdateAddressWithZipCode_thenReturnUpdatedAddress() {
        final var aAddress = Fixture.Addresses.newAddressWithComplement(
                new CustomerId(IdentifierUtils.generateNewUUID()),
                false
        );

        final var aAddressId = aAddress.getId().value();
        final var aTitle = "New Title";
        final String aZipCode = "12345678";
        final String aCity = "New City";
        final String aNumber = "1010";
        final String aComplement = "New Complement";
        final String aStreet = "New Street";
        final String aDistrict = "New District";
        final String aCountry = "New Country";
        final String aState = "New State";

        final var aInput = new UpdateAddressInput(
                aAddressId,
                aTitle,
                aZipCode,
                aNumber,
                aComplement,
                aStreet,
                aDistrict,
                aCountry
        );

        Mockito.when(addressRepository.addressOfId(Mockito.any())).thenReturn(Optional.of(aAddress));
        Mockito.when(addressGateway.getAddressByZipCode(Mockito.any())).thenReturn(Optional.of(new AddressGateway.GetAddressByZipCodeResponse(
                aZipCode,
                aCity,
                aStreet,
                aDistrict,
                aState
        )));
        Mockito.when(addressRepository.save(Mockito.any())).thenAnswer(returnsFirstArg());

        final var aOutput = this.updateAddressUseCase.execute(aInput);

        Assertions.assertEquals(aAddressId.toString(), aOutput.id());
        Assertions.assertEquals(aAddress.getCustomerId().value().toString(), aOutput.customerId());

        Mockito.verify(addressRepository, Mockito.times(1)).addressOfId(Mockito.any());
        Mockito.verify(addressGateway, Mockito.times(1)).getAddressByZipCode(Mockito.any());
        Mockito.verify(addressRepository, Mockito.times(1)).save(argThat(cmd ->
                Objects.equals(aTitle, cmd.getTitle().value())
                        && Objects.equals(aNumber, cmd.getNumber())
                        && Objects.equals(aComplement, cmd.getComplement().orElse(null))
                        && Objects.equals(aStreet, cmd.getStreet())
                        && Objects.equals(aDistrict, cmd.getDistrict())
                        && Objects.equals(aCountry, cmd.getCountry())
        ));
    }
}
