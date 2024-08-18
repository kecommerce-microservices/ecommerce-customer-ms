package com.kaua.ecommerce.customer.infrastructure.jobs;

import com.kaua.ecommerce.customer.domain.UnitTest;
import com.kaua.ecommerce.customer.infrastructure.authentication.RefreshClientCredentials;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.verify;

class ClientCredentialsJobTest extends UnitTest {

    @Mock
    private RefreshClientCredentials refreshClientCredentials;

    @InjectMocks
    private ClientCredentialsJob clientCredentialsJob;

    @Test
    void shouldRefreshClientCredentials() {
        clientCredentialsJob.refreshClientCredentials();

        verify(refreshClientCredentials).refresh();
    }
}
