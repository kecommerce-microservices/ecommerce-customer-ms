package com.kaua.ecommerce.customer.infrastructure.authentication;

import com.kaua.ecommerce.customer.domain.UnitTest;
import com.kaua.ecommerce.customer.infrastructure.authentication.AuthenticationGateway.ClientCredentialsInput;
import com.kaua.ecommerce.customer.infrastructure.configurations.properties.AuthServerProperties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

class ClientCredentialsManagerTest extends UnitTest {

    @Mock
    private AuthServerProperties authServerProperties;

    @Mock
    private AuthenticationGateway authenticationGateway;

    @InjectMocks
    private ClientCredentialsManager manager;

    @Test
    void givenAValidAuthenticationResult_whenCallRefresh_shouldCreateCredentials() {
        final var aClientId = "aClientId";
        final var aClientSecret = "aClientSecret";

        final var expectedToken = "accessToken";

        Mockito.doReturn(aClientId).when(authServerProperties).getClientId();
        Mockito.doReturn(aClientSecret).when(authServerProperties).getClientSecret();

        Mockito.doReturn(new AuthenticationGateway.AuthenticationResult(expectedToken))
                .when(authenticationGateway).login(new ClientCredentialsInput(aClientId, aClientSecret));

        this.manager.refresh();
        final var aActualToken = this.manager.retrieve();

        Assertions.assertEquals(expectedToken, aActualToken);
    }
}
