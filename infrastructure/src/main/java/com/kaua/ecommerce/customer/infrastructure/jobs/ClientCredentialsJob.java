package com.kaua.ecommerce.customer.infrastructure.jobs;

import com.kaua.ecommerce.customer.infrastructure.authentication.RefreshClientCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Component
@Profile("!test-integration")
public class ClientCredentialsJob {

    private static final Logger log = LoggerFactory.getLogger(ClientCredentialsJob.class);

    private final RefreshClientCredentials refreshClientCredentials;

    public ClientCredentialsJob(final RefreshClientCredentials refreshClientCredentials) {
        this.refreshClientCredentials = Objects.requireNonNull(refreshClientCredentials);
    }

    @Scheduled(
            fixedRateString = "${jobs.client-credentials.refresh-rate-minutes}",
            initialDelayString = "${jobs.client-credentials.refresh-initial-delay-minutes}",
            timeUnit = TimeUnit.MINUTES
    )
    public void refreshClientCredentials() {
        log.info("Refreshing client credentials");
        this.refreshClientCredentials.refresh();
        log.info("Client credentials refreshed");
    }
}
