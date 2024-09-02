package com.kaua.ecommerce.customer.application.usecases.customer.impl;

import com.kaua.ecommerce.customer.application.UseCaseTest;
import com.kaua.ecommerce.customer.application.exceptions.UseCaseInputCannotBeNullException;
import com.kaua.ecommerce.customer.application.gateways.IdentityProviderGateway;
import com.kaua.ecommerce.customer.domain.customer.idp.UserId;
import com.kaua.ecommerce.lib.domain.utils.IdentifierUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

class DeleteIdpUserUseCaseTest extends UseCaseTest {

    @Mock
    private IdentityProviderGateway identityProviderGateway;

    @InjectMocks
    private DefaultDeleteIdpUserUseCase deleteIdpUserUseCase;

    @Test
    void givenAValidUserId_whenCallDeleteIdpUser_thenShouldDeleteUser() {
        final var aUserId = new UserId(IdentifierUtils.generateNewUUID());

        Mockito.doNothing().when(identityProviderGateway).deleteOfUserId(aUserId);

        Assertions.assertDoesNotThrow(() -> this.deleteIdpUserUseCase.execute(aUserId));

        Mockito.verify(identityProviderGateway, Mockito.times(1)).deleteOfUserId(aUserId);
    }

    @Test
    void givenANullUserId_whenCallDeleteIdpUser_thenShouldThrowUseCaseInputCannotBeNullException() {
        final var expectedErrorMessage = "Input to DefaultDeleteIdpUserUseCase cannot be null";

        final var aException = Assertions.assertThrows(UseCaseInputCannotBeNullException.class,
                () -> this.deleteIdpUserUseCase.execute(null));

        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());

        Mockito.verify(identityProviderGateway, Mockito.never()).deleteOfUserId(Mockito.any());
    }
}
