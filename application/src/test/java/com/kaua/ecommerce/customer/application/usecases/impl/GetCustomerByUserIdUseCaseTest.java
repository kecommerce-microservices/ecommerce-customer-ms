package com.kaua.ecommerce.customer.application.usecases.impl;

import com.kaua.ecommerce.customer.application.UseCaseTest;
import com.kaua.ecommerce.customer.application.exceptions.UseCaseInputCannotBeNullException;
import com.kaua.ecommerce.customer.application.gateways.TelephoneGateway;
import com.kaua.ecommerce.customer.application.repositories.CustomerRepository;
import com.kaua.ecommerce.customer.application.usecases.customer.impl.DefaultGetCustomerByUserIdUseCase;
import com.kaua.ecommerce.customer.domain.Fixture;
import com.kaua.ecommerce.customer.domain.customer.idp.UserId;
import com.kaua.ecommerce.lib.domain.exceptions.NotFoundException;
import com.kaua.ecommerce.lib.domain.utils.IdentifierUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.Optional;

class GetCustomerByUserIdUseCaseTest extends UseCaseTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private TelephoneGateway telephoneGateway;

    @InjectMocks
    private DefaultGetCustomerByUserIdUseCase getCustomerByUserIdUseCase;

    @Test
    void givenAValidUserId_whenCallGetCustomerByUserId_thenShouldReturnCustomer() {
        final var aCustomer = Fixture.Customers.newCustomerWithAllValues();

        final var aUserId = aCustomer.getUserId();

        final var aPhoneNumberFormatted = "(11) 98765-4321";
        final var aRegionCode = "BR";
        final var aCountryCode = "+55";

        Mockito.when(customerRepository.customerOfUserId(aUserId))
                .thenReturn(Optional.of(aCustomer));
        Mockito.when(telephoneGateway.formatToLocal(aCustomer.getTelephone().get().value()))
                .thenReturn(new TelephoneGateway.PhoneNumberInformation(
                        aPhoneNumberFormatted,
                        aCountryCode,
                        aRegionCode
                ));

        final var aOutput = Assertions.assertDoesNotThrow(() -> this.getCustomerByUserIdUseCase.execute(aUserId));

        Assertions.assertEquals(aCustomer.getId().value().toString(), aOutput.customerId());
        Assertions.assertEquals(aCustomer.getUserId().value().toString(), aOutput.userId());
        Assertions.assertEquals(aCustomer.getName().firstName(), aOutput.firstName());
        Assertions.assertEquals(aCustomer.getName().lastName(), aOutput.lastName());
        Assertions.assertEquals(aCustomer.getName().fullName(), aOutput.fullName());
        Assertions.assertEquals(aCustomer.getEmail().value(), aOutput.email());
        Assertions.assertEquals(aCustomer.getDocument().get().formattedValue(), aOutput.document().documentNumber());
        Assertions.assertEquals(aCustomer.getDocument().get().type(), aOutput.document().documentType());
        Assertions.assertEquals(aPhoneNumberFormatted, aOutput.telephone().phoneNumber());
        Assertions.assertEquals(aCountryCode, aOutput.telephone().countryCode());
        Assertions.assertEquals(aRegionCode, aOutput.telephone().regionCode());
        Assertions.assertEquals(aCustomer.getCreatedAt(), aOutput.createdAt());
        Assertions.assertEquals(aCustomer.getUpdatedAt(), aOutput.updatedAt());
        Assertions.assertEquals(aCustomer.getVersion(), aOutput.version());

        Mockito.verify(customerRepository, Mockito.times(1)).customerOfUserId(aUserId);
        Mockito.verify(telephoneGateway, Mockito.times(1)).formatToLocal(aCustomer.getTelephone().get().value());
    }

    @Test
    void givenAValidUserIdWithoutOptionalParams_whenCallGetCustomerByUserId_thenShouldReturnCustomer() {
        final var aCustomer = Fixture.Customers.newCustomer();

        final var aUserId = aCustomer.getUserId();

        Mockito.when(customerRepository.customerOfUserId(aUserId))
                .thenReturn(Optional.of(aCustomer));

        final var aOutput = Assertions.assertDoesNotThrow(() -> this.getCustomerByUserIdUseCase.execute(aUserId));

        Assertions.assertEquals(aCustomer.getId().value().toString(), aOutput.customerId());
        Assertions.assertEquals(aCustomer.getUserId().value().toString(), aOutput.userId());
        Assertions.assertEquals(aCustomer.getName().firstName(), aOutput.firstName());
        Assertions.assertEquals(aCustomer.getName().lastName(), aOutput.lastName());
        Assertions.assertEquals(aCustomer.getName().fullName(), aOutput.fullName());
        Assertions.assertEquals(aCustomer.getEmail().value(), aOutput.email());
        Assertions.assertNull(aOutput.document());
        Assertions.assertNull(aOutput.telephone());
        Assertions.assertEquals(aCustomer.getCreatedAt(), aOutput.createdAt());
        Assertions.assertEquals(aCustomer.getUpdatedAt(), aOutput.updatedAt());
        Assertions.assertEquals(aCustomer.getVersion(), aOutput.version());

        Mockito.verify(customerRepository, Mockito.times(1)).customerOfUserId(aUserId);
        Mockito.verify(telephoneGateway, Mockito.never()).formatToLocal(Mockito.any());
    }

    @Test
    void givenANullUserId_whenCallGetCustomerByUserId_thenShouldThrowUseCaseInputCannotBeNullException() {
        final var expectedErrorMessage = "Input to DefaultGetCustomerByUserIdUseCase cannot be null";

        final var aException = Assertions.assertThrows(UseCaseInputCannotBeNullException.class,
                () -> this.getCustomerByUserIdUseCase.execute(null));

        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());
    }

    @Test
    void givenAnInvalidUserId_whenCallGetCustomerByUserId_thenShouldThrowNotFoundException() {
        final var aUserId = new UserId(IdentifierUtils.generateNewUUID());

        final var expectedErrorMessage = NotFoundException.ERROR_MESSAGE
                .formatted("Customer", "id", aUserId.value().toString());

        Mockito.when(customerRepository.customerOfUserId(aUserId))
                .thenReturn(Optional.empty());

        final var aException = Assertions.assertThrows(Exception.class,
                () -> this.getCustomerByUserIdUseCase.execute(aUserId));

        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());

        Mockito.verify(customerRepository, Mockito.times(1)).customerOfUserId(aUserId);
        Mockito.verify(telephoneGateway, Mockito.never()).formatToLocal(Mockito.anyString());
    }
}
