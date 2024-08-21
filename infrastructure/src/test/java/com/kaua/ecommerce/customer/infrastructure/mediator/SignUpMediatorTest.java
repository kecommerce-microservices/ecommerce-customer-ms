package com.kaua.ecommerce.customer.infrastructure.mediator;

import com.kaua.ecommerce.customer.application.repositories.CustomerRepository;
import com.kaua.ecommerce.customer.application.usecases.customer.CreateCustomerUseCase;
import com.kaua.ecommerce.customer.application.usecases.customer.CreateIdpUserUseCase;
import com.kaua.ecommerce.customer.application.usecases.customer.DeleteIdpUserUseCase;
import com.kaua.ecommerce.customer.application.usecases.customer.inputs.CreateCustomerInput;
import com.kaua.ecommerce.customer.application.usecases.customer.outputs.CreateCustomerOutput;
import com.kaua.ecommerce.customer.application.usecases.customer.outputs.CreateIdpUserOutput;
import com.kaua.ecommerce.customer.domain.UnitTest;
import com.kaua.ecommerce.customer.domain.customer.CustomerId;
import com.kaua.ecommerce.customer.domain.customer.idp.UserId;
import com.kaua.ecommerce.customer.infrastructure.rest.req.SignUpRequest;
import com.kaua.ecommerce.lib.domain.exceptions.InternalErrorException;
import com.kaua.ecommerce.lib.domain.utils.IdentifierUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import static org.mockito.ArgumentMatchers.any;

class SignUpMediatorTest extends UnitTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CreateCustomerUseCase createCustomerUseCase;

    @Mock
    private CreateIdpUserUseCase createIdpUserUseCase;

    @Mock
    private DeleteIdpUserUseCase deleteIdpUserUseCase;

    @InjectMocks
    private SignUpMediator signUpMediator;

    @Captor
    private ArgumentCaptor<CreateCustomerInput> createCustomerInputCaptor;

    @Test
    void givenAValidRequest_whenSignUpSuccessfully_thenShouldReturnUserIdAndCustomerId() {
        Assertions.assertNotNull(customerRepository);

        final var aFirstName = "John";
        final var aLastName = "Doe";
        final var aEmail = "testes@tess.com";
        final var aPassword = "123456Ab*";

        final var expectedUserId = new UserId(IdentifierUtils.generateNewUUID());
        final var expectedCustomerId = new CustomerId(IdentifierUtils.generateNewUUID());

        final var aRequest = new SignUpRequest(aFirstName, aLastName, aEmail, aPassword);

        Mockito.when(customerRepository.nextId()).thenReturn(expectedCustomerId);
        Mockito.when(createIdpUserUseCase.execute(any()))
                .thenAnswer(t -> new CreateIdpUserOutput(expectedUserId));
        Mockito.when(createCustomerUseCase.execute(any()))
                .thenAnswer(t -> new CreateCustomerOutput(expectedCustomerId.value(), expectedUserId.value()));

        final var aResult = this.signUpMediator.signUp(aRequest);

        Assertions.assertEquals(expectedUserId.value().toString(), aResult.idpUserId());
        Assertions.assertEquals(expectedCustomerId.value().toString(), aResult.customerId());

        Mockito.verify(createCustomerUseCase, Mockito.times(1))
                .execute(createCustomerInputCaptor.capture());

        final var aCreateCustomerInput = createCustomerInputCaptor.getValue();

        Assertions.assertEquals(aCreateCustomerInput.customerId().toString(), expectedCustomerId.value().toString());
        Assertions.assertEquals(aCreateCustomerInput.userId().toString(), expectedUserId.value().toString());
        Assertions.assertEquals(aCreateCustomerInput.firstName(), aFirstName);
        Assertions.assertEquals(aCreateCustomerInput.lastName(), aLastName);
        Assertions.assertEquals(aCreateCustomerInput.email(), aEmail);
    }

    @Test
    void givenAValidRequestButCreateCustomerFails_whenCallSignUp_thenShouldDeleteIdpUser() {
        Assertions.assertNotNull(customerRepository);

        final var aFirstName = "John";
        final var aLastName = "Doe";
        final var aEmail = "testes@tess.com";
        final var aPassword = "123456Ab*";

        final var expectedUserId = new UserId(IdentifierUtils.generateNewUUID());
        final var expectedCustomerId = new CustomerId(IdentifierUtils.generateNewUUID());

        final var expectedErrorMessage = "Failed to create customer and compensate idp user creation";

        final var aRequest = new SignUpRequest(aFirstName, aLastName, aEmail, aPassword);

        Mockito.when(customerRepository.nextId()).thenReturn(expectedCustomerId);
        Mockito.when(createIdpUserUseCase.execute(any()))
                .thenAnswer(t -> new CreateIdpUserOutput(expectedUserId));
        Mockito.when(createCustomerUseCase.execute(any()))
                .thenThrow(new RuntimeException("Failed to create customer"));
        Mockito.doNothing().when(deleteIdpUserUseCase).execute(expectedUserId);

        final var aException = Assertions.assertThrows(InternalErrorException.class,
                () -> this.signUpMediator.signUp(aRequest));

        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());

        Mockito.verify(createIdpUserUseCase, Mockito.times(1)).execute(any());
        Mockito.verify(createCustomerUseCase, Mockito.times(1)).execute(any());
        Mockito.verify(deleteIdpUserUseCase, Mockito.times(1)).execute(expectedUserId);
    }

    @Test
    void givenAValidRequestButCreateCustomerFailsAndDeleteIdpUserFails_whenCallSignUp_thenShouldThrowException() {
        Assertions.assertNotNull(customerRepository);

        final var aFirstName = "John";
        final var aLastName = "Doe";
        final var aEmail = "testes@tess.com";
        final var aPassword = "123456Ab*";

        final var expectedUserId = new UserId(IdentifierUtils.generateNewUUID());
        final var expectedCustomerId = new CustomerId(IdentifierUtils.generateNewUUID());

        final var expectedErrorMessage = "Failed to compensate for failed creation of customer deleting idp user";

        final var aRequest = new SignUpRequest(aFirstName, aLastName, aEmail, aPassword);

        Mockito.when(customerRepository.nextId()).thenReturn(expectedCustomerId);
        Mockito.when(createIdpUserUseCase.execute(any()))
                .thenAnswer(t -> new CreateIdpUserOutput(expectedUserId));
        Mockito.when(createCustomerUseCase.execute(any()))
                .thenThrow(new RuntimeException("Failed to create customer"));
        Mockito.doThrow(new RuntimeException("Failed to delete idp user"))
                .when(deleteIdpUserUseCase).execute(expectedUserId);

        final var aException = Assertions.assertThrows(InternalErrorException.class,
                () -> this.signUpMediator.signUp(aRequest));

        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());

        Mockito.verify(createIdpUserUseCase, Mockito.times(1)).execute(any());
        Mockito.verify(createCustomerUseCase, Mockito.times(1)).execute(any());
        Mockito.verify(deleteIdpUserUseCase, Mockito.times(1)).execute(expectedUserId);
    }
}
