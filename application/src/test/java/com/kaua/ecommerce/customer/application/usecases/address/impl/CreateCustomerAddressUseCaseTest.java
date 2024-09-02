package com.kaua.ecommerce.customer.application.usecases.address.impl;

import com.kaua.ecommerce.customer.application.UseCaseTest;
import com.kaua.ecommerce.customer.application.exceptions.UseCaseInputCannotBeNullException;
import com.kaua.ecommerce.customer.application.gateways.AddressGateway;
import com.kaua.ecommerce.customer.application.repositories.AddressRepository;
import com.kaua.ecommerce.customer.application.usecases.address.inputs.CreateCustomerAddressInput;
import com.kaua.ecommerce.customer.domain.customer.CustomerId;
import com.kaua.ecommerce.lib.domain.exceptions.DomainException;
import com.kaua.ecommerce.lib.domain.exceptions.NotFoundException;
import com.kaua.ecommerce.lib.domain.utils.IdentifierUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.Optional;

import static org.mockito.AdditionalAnswers.returnsFirstArg;

class CreateCustomerAddressUseCaseTest extends UseCaseTest {

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private AddressGateway addressGateway;

    @InjectMocks
    private DefaultCreateCustomerAddressUseCase createCustomerAddressUseCase;

    @Test
    void givenAValidValues_whenCallCreateCustomerAddress_thenShouldCreateCustomerAddress() {
        final var aCustomerId = new CustomerId(IdentifierUtils.generateNewUUID());
        final var aTitle = "Home";
        final var aZipCode = "12345678";
        final var aStreet = "Rua Teste";
        final var aNumber = "123";
        final var aComplement = "Apto 123";
        final var aCity = "São Paulo";
        final var aCountry = "BR";
        final var aDistrict = "Centro";
        final var aState = "SP";
        final var aIsDefault = true;

        final var aInput = new CreateCustomerAddressInput(
                aCustomerId.value(),
                aTitle,
                aZipCode,
                aNumber,
                aComplement,
                aCountry,
                aIsDefault
        );

        Mockito.when(addressRepository.countByCustomerId(aCustomerId))
                .thenReturn(0);
        Mockito.when(addressRepository.existsByCustomerIdAndIsDefaultTrue(aCustomerId))
                .thenReturn(false);
        Mockito.when(addressGateway.getAddressByZipCode(aZipCode))
                .thenReturn(Optional.of(new AddressGateway.GetAddressByZipCodeResponse(
                        aZipCode,
                        aCity,
                        aStreet,
                        aDistrict,
                        aState
                )));
        Mockito.when(addressRepository.save(Mockito.any())).thenAnswer(returnsFirstArg());

        final var aOutput = Assertions.assertDoesNotThrow(() -> this.createCustomerAddressUseCase.execute(aInput));

        Assertions.assertEquals(aCustomerId.value().toString(), aOutput.customerId());
        Assertions.assertNotNull(aOutput.addressId());

        Mockito.verify(addressRepository, Mockito.times(1)).countByCustomerId(aCustomerId);
        Mockito.verify(addressRepository, Mockito.times(1)).existsByCustomerIdAndIsDefaultTrue(aCustomerId);
        Mockito.verify(addressGateway, Mockito.times(1)).getAddressByZipCode(aZipCode);
        Mockito.verify(addressRepository, Mockito.times(1)).save(Mockito.any());
    }

    @Test
    void givenAValidValuesButIsDefaultIsFalse_whenCallCreateCustomerAddress_thenShouldCreateCustomerAddress() {
        final var aCustomerId = new CustomerId(IdentifierUtils.generateNewUUID());
        final var aTitle = "Home";
        final var aZipCode = "12345678";
        final var aStreet = "Rua Teste";
        final var aNumber = "123";
        final var aComplement = "Apto 123";
        final var aCity = "São Paulo";
        final var aCountry = "BR";
        final var aDistrict = "Centro";
        final var aState = "SP";
        final var aIsDefault = false;

        final var aInput = new CreateCustomerAddressInput(
                aCustomerId.value(),
                aTitle,
                aZipCode,
                aNumber,
                aComplement,
                aCountry,
                aIsDefault
        );

        Mockito.when(addressRepository.countByCustomerId(aCustomerId))
                .thenReturn(0);
        Mockito.when(addressGateway.getAddressByZipCode(aZipCode))
                .thenReturn(Optional.of(new AddressGateway.GetAddressByZipCodeResponse(
                        aZipCode,
                        aCity,
                        aStreet,
                        aDistrict,
                        aState
                )));
        Mockito.when(addressRepository.save(Mockito.any())).thenAnswer(returnsFirstArg());

        final var aOutput = Assertions.assertDoesNotThrow(() -> this.createCustomerAddressUseCase.execute(aInput));

        Assertions.assertEquals(aCustomerId.value().toString(), aOutput.customerId());
        Assertions.assertNotNull(aOutput.addressId());

        Mockito.verify(addressRepository, Mockito.times(1)).countByCustomerId(aCustomerId);
        Mockito.verify(addressRepository, Mockito.times(0)).existsByCustomerIdAndIsDefaultTrue(aCustomerId);
        Mockito.verify(addressGateway, Mockito.times(1)).getAddressByZipCode(aZipCode);
        Mockito.verify(addressRepository, Mockito.times(1)).save(Mockito.any());
    }

    @Test
    void givenAnCustomerHaveMoreThanFiveAddresses_whenCallCreateCustomerAddress_thenShouldThrowDomainException() {
        final var aCustomerId = new CustomerId(IdentifierUtils.generateNewUUID());
        final var aTitle = "Home";
        final var aZipCode = "12345678";
        final var aNumber = "123";
        final var aComplement = "Apto 123";
        final var aCountry = "BR";
        final var aIsDefault = true;

        final var aInput = new CreateCustomerAddressInput(
                aCustomerId.value(),
                aTitle,
                aZipCode,
                aNumber,
                aComplement,
                aCountry,
                aIsDefault
        );

        final var expectedErrorMessage = "Customer can't have more than 5 addresses";

        Mockito.when(addressRepository.countByCustomerId(aCustomerId))
                .thenReturn(5);

        final var aException = Assertions.assertThrows(DomainException.class, () -> this.createCustomerAddressUseCase.execute(aInput));

        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());

        Mockito.verify(addressRepository, Mockito.times(1)).countByCustomerId(aCustomerId);
        Mockito.verify(addressRepository, Mockito.times(0)).existsByCustomerIdAndIsDefaultTrue(aCustomerId);
        Mockito.verify(addressGateway, Mockito.never()).getAddressByZipCode(Mockito.any());
        Mockito.verify(addressRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void givenAnCustomerAlreadyHaveADefaultAddress_whenCallCreateCustomerAddress_thenShouldThrowDomainException() {
        final var aCustomerId = new CustomerId(IdentifierUtils.generateNewUUID());
        final var aTitle = "Home";
        final var aZipCode = "12345678";
        final var aNumber = "123";
        final var aComplement = "Apto 123";
        final var aCountry = "BR";
        final var aIsDefault = true;

        final var aInput = new CreateCustomerAddressInput(
                aCustomerId.value(),
                aTitle,
                aZipCode,
                aNumber,
                aComplement,
                aCountry,
                aIsDefault
        );

        final var expectedErrorMessage = "Customer already has a default address";

        Mockito.when(addressRepository.countByCustomerId(aCustomerId))
                .thenReturn(0);
        Mockito.when(addressRepository.existsByCustomerIdAndIsDefaultTrue(aCustomerId))
                .thenReturn(true);

        final var aException = Assertions.assertThrows(DomainException.class, () -> this.createCustomerAddressUseCase.execute(aInput));

        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());

        Mockito.verify(addressRepository, Mockito.times(1)).countByCustomerId(aCustomerId);
        Mockito.verify(addressRepository, Mockito.times(1)).existsByCustomerIdAndIsDefaultTrue(aCustomerId);
        Mockito.verify(addressGateway, Mockito.never()).getAddressByZipCode(Mockito.any());
        Mockito.verify(addressRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void givenAnInvalidZipCode_whenCallCreateCustomerAddress_thenShouldThrowDomainException() {
        final var aCustomerId = new CustomerId(IdentifierUtils.generateNewUUID());
        final var aTitle = "Home";
        final var aZipCode = "12345678";
        final var aNumber = "123";
        final var aComplement = "Apto 123";
        final var aCountry = "BR";
        final var aIsDefault = true;

        final var aInput = new CreateCustomerAddressInput(
                aCustomerId.value(),
                aTitle,
                aZipCode,
                aNumber,
                aComplement,
                aCountry,
                aIsDefault
        );

        final var expectedErrorMessage = "Address with zipCode 12345678 was not found";

        Mockito.when(addressRepository.countByCustomerId(aCustomerId))
                .thenReturn(0);
        Mockito.when(addressRepository.existsByCustomerIdAndIsDefaultTrue(aCustomerId))
                .thenReturn(false);
        Mockito.when(addressGateway.getAddressByZipCode(aZipCode))
                .thenReturn(Optional.empty());

        final var aException = Assertions.assertThrows(NotFoundException.class, () -> this.createCustomerAddressUseCase.execute(aInput));

        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());

        Mockito.verify(addressRepository, Mockito.times(1)).countByCustomerId(aCustomerId);
        Mockito.verify(addressRepository, Mockito.times(1)).existsByCustomerIdAndIsDefaultTrue(aCustomerId);
        Mockito.verify(addressGateway, Mockito.times(1)).getAddressByZipCode(aZipCode);
        Mockito.verify(addressRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void givenAnInvalidNullInput_whenCallCreateCustomerAddress_thenShouldThrowUseCaseInputCannotBeNullException() {
        final var expectedErrorMessage = "Input to DefaultCreateCustomerAddressUseCase cannot be null";

        final var aException = Assertions.assertThrows(UseCaseInputCannotBeNullException.class, () -> this.createCustomerAddressUseCase.execute(null));

        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());

        Mockito.verify(addressRepository, Mockito.never()).countByCustomerId(Mockito.any());
        Mockito.verify(addressRepository, Mockito.never()).existsByCustomerIdAndIsDefaultTrue(Mockito.any());
        Mockito.verify(addressGateway, Mockito.never()).getAddressByZipCode(Mockito.any());
        Mockito.verify(addressRepository, Mockito.never()).save(Mockito.any());
    }
}
