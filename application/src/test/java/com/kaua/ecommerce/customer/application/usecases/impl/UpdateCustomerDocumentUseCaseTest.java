package com.kaua.ecommerce.customer.application.usecases.impl;

import com.kaua.ecommerce.customer.application.UseCaseTest;
import com.kaua.ecommerce.customer.application.exceptions.UseCaseInputCannotBeNullException;
import com.kaua.ecommerce.customer.application.repositories.CustomerRepository;
import com.kaua.ecommerce.customer.application.usecases.customer.impl.DefaultUpdateCustomerDocumentUseCase;
import com.kaua.ecommerce.customer.application.usecases.customer.inputs.UpdateCustomerDocumentInput;
import com.kaua.ecommerce.customer.application.usecases.customer.outputs.UpdateCustomerDocumentOutput;
import com.kaua.ecommerce.customer.domain.Fixture;
import com.kaua.ecommerce.lib.domain.exceptions.InternalErrorException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.Objects;
import java.util.Optional;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.argThat;

class UpdateCustomerDocumentUseCaseTest extends UseCaseTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private DefaultUpdateCustomerDocumentUseCase updateCustomerDocumentUseCase;

    @Test
    void givenAValidValues_whenCallUpdateCustomerDocument_thenShouldUpdateDocument() {
        final var aCustomer = Fixture.Customers.newCustomer();

        final var aCustomerId = aCustomer.getId();

        final var aDocumentNumber = "443.032.340-28";
        final var aDocumentType = "CPF";

        final var aInput = new UpdateCustomerDocumentInput(aCustomerId.value(), aDocumentNumber, aDocumentType);

        Mockito.when(customerRepository.existsByDocument(Mockito.anyString()))
                .thenReturn(false);
        Mockito.when(customerRepository.customerOfId(aCustomerId))
                .thenReturn(Optional.of(aCustomer));
        Mockito.when(customerRepository.save(aCustomer))
                .thenAnswer(returnsFirstArg());

        final var aOutput = Assertions.assertDoesNotThrow(() ->
                this.updateCustomerDocumentUseCase.execute(aInput));

        Assertions.assertEquals(aCustomerId.value().toString(), aOutput.customerId());
        Assertions.assertEquals(aDocumentType, aOutput.documentType());

        Mockito.verify(customerRepository, Mockito.times(1)).existsByDocument(Mockito.anyString());
        Mockito.verify(customerRepository, Mockito.times(1)).customerOfId(aCustomerId);
        Mockito.verify(customerRepository, Mockito.times(1)).save(argThat(cmd ->
                Objects.equals(cmd.getDocument().get().formattedValue(), aDocumentNumber)
                        && Objects.equals(cmd.getDocument().get().type(), aDocumentType))
        );
    }

    @Test
    void givenAnExistingDocument_whenCallUpdateCustomerDocument_thenShouldThrowDomainException() {
        final var aCustomer = Fixture.Customers.newCustomer();

        final var aCustomerId = aCustomer.getId();

        final var aDocumentNumber = "443.032.340-28";
        final var aDocumentType = "CPF";

        final var expectedErrorMessage = "Document already exists";

        final var aInput = new UpdateCustomerDocumentInput(aCustomerId.value(), aDocumentNumber, aDocumentType);

        Mockito.when(customerRepository.existsByDocument(Mockito.anyString()))
                .thenReturn(true);

        final var aException = Assertions.assertThrows(Exception.class,
                () -> this.updateCustomerDocumentUseCase.execute(aInput));

        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());

        Mockito.verify(customerRepository, Mockito.times(1)).existsByDocument(Mockito.anyString());
        Mockito.verify(customerRepository, Mockito.times(0)).customerOfId(aCustomerId);
        Mockito.verify(customerRepository, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void givenAnInvalidId_whenCallUpdateCustomerDocument_thenShouldThrowNotFoundException() {
        final var aCustomerId = Fixture.Customers.newCustomer().getId();

        final var aDocumentNumber = "443.032.340-28";
        final var aDocumentType = "CPF";

        final var expectedErrorMessage = "Customer with id %s was not found"
                .formatted(aCustomerId.value().toString());

        final var aInput = new UpdateCustomerDocumentInput(aCustomerId.value(), aDocumentNumber, aDocumentType);

        Mockito.when(customerRepository.existsByDocument(Mockito.anyString()))
                .thenReturn(false);
        Mockito.when(customerRepository.customerOfId(aCustomerId))
                .thenReturn(Optional.empty());

        final var aException = Assertions.assertThrows(Exception.class,
                () -> this.updateCustomerDocumentUseCase.execute(aInput));

        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());

        Mockito.verify(customerRepository, Mockito.times(1)).existsByDocument(Mockito.anyString());
        Mockito.verify(customerRepository, Mockito.times(1)).customerOfId(aCustomerId);
        Mockito.verify(customerRepository, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void givenAnInvalidNullInput_whenCallUpdateCustomerDocument_thenShouldThrowUseCaseInputCannotBeNullException() {
        final var expectedErrorMessage = "Input to DefaultUpdateCustomerDocumentUseCase cannot be null";

        final var aCustomerId = Fixture.Customers.newCustomer().getId();

        final var aException = Assertions.assertThrows(UseCaseInputCannotBeNullException.class,
                () -> this.updateCustomerDocumentUseCase.execute(null));

        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());

        Mockito.verify(customerRepository, Mockito.times(0)).customerOfId(aCustomerId);
        Mockito.verify(customerRepository, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void givenAnInvalidNullDocument_whenCallNewUpdateCustomerDocumentOutput_thenShouldThrowInternalErrorException() {
        final var aCustomer = Fixture.Customers.newCustomer();

        final var expectedErrorMessage = "On creating UpdateCustomerDocumentOutput, document type is null";

        final var aException = Assertions.assertThrows(InternalErrorException.class,
                () -> new UpdateCustomerDocumentOutput(aCustomer));

        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());
    }
}
