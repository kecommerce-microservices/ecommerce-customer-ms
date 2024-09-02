package com.kaua.ecommerce.customer.application.usecases.customer.impl;

import com.kaua.ecommerce.customer.application.UseCaseTest;
import com.kaua.ecommerce.customer.application.exceptions.UseCaseInputCannotBeNullException;
import com.kaua.ecommerce.customer.application.repositories.CustomerRepository;
import com.kaua.ecommerce.customer.application.usecases.customer.impl.DefaultCreateCustomerUseCase;
import com.kaua.ecommerce.customer.application.usecases.customer.inputs.CreateCustomerInput;
import com.kaua.ecommerce.lib.domain.exceptions.DomainException;
import com.kaua.ecommerce.lib.domain.utils.IdentifierUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.mockito.AdditionalAnswers.returnsFirstArg;

class CreateCustomerUseCaseTest extends UseCaseTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private DefaultCreateCustomerUseCase createCustomerUseCase;

    @Test
    void givenAValidValues_whenCallCreateCustomer_thenCustomerShouldBeCreated() {
        final var aUserId = IdentifierUtils.generateNewUUID();
        final var aCustomerId = IdentifierUtils.generateNewUUID();
        final var aFirstName = "John";
        final var aLastName = "Doe";
        final var aEmail = "testes.tess@tess.com";

        final var aInput = new CreateCustomerInput(aUserId, aCustomerId, aFirstName, aLastName, aEmail);

        Mockito.when(customerRepository.existsByEmail(aEmail)).thenReturn(false);
        Mockito.when(customerRepository.save(Mockito.any())).thenAnswer(returnsFirstArg());

        final var aOutput = this.createCustomerUseCase.execute(aInput);

        Assertions.assertEquals(aCustomerId, aOutput.customerId());
        Assertions.assertEquals(aUserId, aOutput.userId());

        Mockito.verify(customerRepository, Mockito.times(1)).existsByEmail(aEmail);
        Mockito.verify(customerRepository, Mockito.times(1)).save(Mockito.any());
    }

    @Test
    void givenAnExistsEmail_whenCallCreateCustomer_thenShouldThrowDomainException() {
        final var aUserId = IdentifierUtils.generateNewUUID();
        final var aCustomerId = IdentifierUtils.generateNewUUID();
        final var aFirstName = "John";
        final var aLastName = "Doe";
        final var aEmail = "testes.tess@tess.com";

        final var expectedErrorMessage = "Email already exists";

        final var aInput = new CreateCustomerInput(aCustomerId, aUserId, aFirstName, aLastName, aEmail);

        Mockito.when(customerRepository.existsByEmail(aEmail)).thenReturn(true);

        final var aException = Assertions.assertThrows(DomainException.class,
                () -> this.createCustomerUseCase.execute(aInput));

        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());

        Mockito.verify(customerRepository, Mockito.times(1)).existsByEmail(aEmail);
        Mockito.verify(customerRepository, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void givenAnInvalidNullInput_whenCallCreateCustomer_thenShouldThrowUseCaseInputCannotBeNullException() {
        final var expectedErrorMessage = "Input to %s cannot be null".formatted(DefaultCreateCustomerUseCase.class.getSimpleName());

        final var aException = Assertions.assertThrows(UseCaseInputCannotBeNullException.class,
                () -> this.createCustomerUseCase.execute(null));

        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());

        Mockito.verify(customerRepository, Mockito.times(0)).existsByEmail(Mockito.any());
        Mockito.verify(customerRepository, Mockito.times(0)).save(Mockito.any());
    }
}
