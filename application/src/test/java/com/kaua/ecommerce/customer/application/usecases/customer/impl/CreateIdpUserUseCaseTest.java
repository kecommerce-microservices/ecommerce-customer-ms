package com.kaua.ecommerce.customer.application.usecases.customer.impl;

import com.kaua.ecommerce.customer.application.UseCaseTest;
import com.kaua.ecommerce.customer.application.exceptions.UseCaseInputCannotBeNullException;
import com.kaua.ecommerce.customer.application.gateways.IdentityProviderGateway;
import com.kaua.ecommerce.customer.application.usecases.customer.inputs.CreateIdpUserInput;
import com.kaua.ecommerce.customer.domain.customer.idp.UserId;
import com.kaua.ecommerce.lib.domain.utils.IdentifierUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

class CreateIdpUserUseCaseTest extends UseCaseTest {

    @Mock
    private IdentityProviderGateway identityProviderGateway;

    @InjectMocks
    private DefaultCreateIdpUserUseCase createIdpUserUseCase;

    @Test
    void givenAValidValues_whenCallCreateIdpUser_thenIdpUserShouldBeCreated() {
        final var aCustomerId = IdentifierUtils.generateNewUUID();
        final var aFirstName = "John";
        final var aLastName = "Doe";
        final var aEmail = "testes.tess@tess.com";
        final var aPassword = "1234567Ab*";

        final var expectedUserId = new UserId(IdentifierUtils.generateNewUUID());

        final var aInput = new CreateIdpUserInput(aCustomerId, aFirstName, aLastName, aEmail, aPassword);

        Mockito.when(identityProviderGateway.create(Mockito.any())).thenReturn(expectedUserId);

        final var aOutput = this.createIdpUserUseCase.execute(aInput);

        Assertions.assertNotNull(aOutput);
        Assertions.assertEquals(expectedUserId, aOutput.idpUserId());

        Mockito.verify(identityProviderGateway, Mockito.times(1)).create(Mockito.any());
    }

    @Test
    void givenAnInvalidNullInput_whenCallCreateIdpUser_thenShouldThrowUseCaseInputCannotBeNullException() {
        final var expectedErrorMessage = "Input to %s cannot be null".formatted(DefaultCreateIdpUserUseCase.class.getSimpleName());

        final var aException = Assertions.assertThrows(UseCaseInputCannotBeNullException.class,
                () -> this.createIdpUserUseCase.execute(null));

        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());

        Mockito.verify(identityProviderGateway, Mockito.times(0)).create(Mockito.any());
    }
}
