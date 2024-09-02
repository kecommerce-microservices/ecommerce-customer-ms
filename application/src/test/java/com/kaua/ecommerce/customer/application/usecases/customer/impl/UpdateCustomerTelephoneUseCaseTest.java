package com.kaua.ecommerce.customer.application.usecases.customer.impl;

import com.kaua.ecommerce.customer.application.UseCaseTest;
import com.kaua.ecommerce.customer.application.exceptions.UseCaseInputCannotBeNullException;
import com.kaua.ecommerce.customer.application.gateways.TelephoneGateway;
import com.kaua.ecommerce.customer.application.repositories.CustomerRepository;
import com.kaua.ecommerce.customer.application.usecases.customer.inputs.UpdateCustomerTelephoneInput;
import com.kaua.ecommerce.customer.domain.Fixture;
import com.kaua.ecommerce.customer.domain.customer.CustomerId;
import com.kaua.ecommerce.lib.domain.exceptions.ValidationException;
import com.kaua.ecommerce.lib.domain.utils.IdentifierUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.Objects;
import java.util.Optional;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.argThat;

class UpdateCustomerTelephoneUseCaseTest extends UseCaseTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private TelephoneGateway telephoneGateway;

    @InjectMocks
    private DefaultUpdateCustomerTelephoneUseCase updateCustomerTelephoneUseCase;

    @Test
    void givenAValidValues_whenCallUpdateCustomerTelephone_thenShouldUpdateCustomerTelephone() {
        final var aCustomer = Fixture.Customers.newCustomer();

        final var aCustomerId = aCustomer.getId();
        final var aTelephone = "+5511999999999";

        final var aInput = new UpdateCustomerTelephoneInput(aCustomerId.value(), aTelephone);

        Mockito.when(customerRepository.customerOfId(aCustomerId)).thenReturn(Optional.of(aCustomer));
        Mockito.when(telephoneGateway.isValid(aTelephone)).thenReturn(true);
        Mockito.when(telephoneGateway.format(aTelephone)).thenReturn(aTelephone);
        Mockito.when(customerRepository.save(aCustomer)).thenAnswer(returnsFirstArg());
        Mockito.when(telephoneGateway.formatToLocal(aTelephone)).thenReturn(new TelephoneGateway.PhoneNumberInformation(aTelephone, "55", "BR"));

        final var aOutput = Assertions.assertDoesNotThrow(() -> this.updateCustomerTelephoneUseCase.execute(aInput));

        Assertions.assertEquals(aCustomerId.value().toString(), aOutput.customerId());
        Assertions.assertEquals(aTelephone, aOutput.telephone());

        Mockito.verify(customerRepository, Mockito.times(1)).customerOfId(aCustomerId);
        Mockito.verify(telephoneGateway, Mockito.times(1)).isValid(aTelephone);
        Mockito.verify(telephoneGateway, Mockito.times(1)).format(aTelephone);
        Mockito.verify(customerRepository, Mockito.times(1)).save(argThat(cmd ->
                Objects.equals(aTelephone, cmd.getTelephone().get().value())));
        Mockito.verify(telephoneGateway, Mockito.times(1)).formatToLocal(aTelephone);
    }

    @Test
    void givenAnInvalidTelephone_whenCallUpdateCustomerTelephone_thenShouldThrowValidationException() {
        final var aCustomer = Fixture.Customers.newCustomer();

        final var aCustomerId = aCustomer.getId();
        final var aTelephone = "+5511999999999";

        final var expectedErrorMessage = "Invalid telephone";

        final var aInput = new UpdateCustomerTelephoneInput(aCustomerId.value(), aTelephone);

        Mockito.when(customerRepository.customerOfId(aCustomerId)).thenReturn(Optional.of(aCustomer));
        Mockito.when(telephoneGateway.isValid(aTelephone)).thenReturn(false);

        final var aException = Assertions.assertThrows(ValidationException.class, () -> this.updateCustomerTelephoneUseCase.execute(aInput));

        Assertions.assertEquals(expectedErrorMessage, aException.getErrors().get(0).message());

        Mockito.verify(customerRepository, Mockito.times(1)).customerOfId(aCustomerId);
        Mockito.verify(telephoneGateway, Mockito.times(1)).isValid(aTelephone);
        Mockito.verify(telephoneGateway, Mockito.never()).format(Mockito.any());
        Mockito.verify(customerRepository, Mockito.never()).save(Mockito.any());
        Mockito.verify(telephoneGateway, Mockito.never()).formatToLocal(Mockito.any());
    }

    @Test
    void givenANullInput_whenCallUpdateCustomerTelephone_thenShouldThrowUseCaseInputCannotBeNullException() {
        final var expectedErrorMessage = "Input to DefaultUpdateCustomerTelephoneUseCase cannot be null";

        final var aException = Assertions.assertThrows(UseCaseInputCannotBeNullException.class, () -> this.updateCustomerTelephoneUseCase.execute(null));

        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());

        Mockito.verify(customerRepository, Mockito.never()).customerOfId(Mockito.any());
        Mockito.verify(telephoneGateway, Mockito.never()).isValid(Mockito.any());
        Mockito.verify(telephoneGateway, Mockito.never()).format(Mockito.any());
        Mockito.verify(customerRepository, Mockito.never()).save(Mockito.any());
        Mockito.verify(telephoneGateway, Mockito.never()).formatToLocal(Mockito.any());
    }

    @Test
    void givenAnNonExistentCustomer_whenCallUpdateCustomerTelephone_thenShouldThrowNotFoundException() {
        final var aCustomerId = new CustomerId(IdentifierUtils.generateNewUUID());

        final var expectedErrorMessage = "Customer with id %s was not found"
                .formatted(aCustomerId.value().toString());

        final var aInput = new UpdateCustomerTelephoneInput(aCustomerId.value(), "+5511999999999");

        Mockito.when(customerRepository.customerOfId(aCustomerId)).thenReturn(Optional.empty());

        final var aException = Assertions.assertThrows(Exception.class, () -> this.updateCustomerTelephoneUseCase.execute(aInput));

        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());

        Mockito.verify(customerRepository, Mockito.times(1)).customerOfId(aCustomerId);
        Mockito.verify(telephoneGateway, Mockito.never()).isValid(Mockito.any());
        Mockito.verify(telephoneGateway, Mockito.never()).format(Mockito.any());
        Mockito.verify(customerRepository, Mockito.never()).save(Mockito.any());
        Mockito.verify(telephoneGateway, Mockito.never()).formatToLocal(Mockito.any());
    }
}
