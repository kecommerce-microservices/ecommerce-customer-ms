package com.kaua.ecommerce.customer.infrastructure.mediator;

import com.kaua.ecommerce.customer.application.repositories.CustomerRepository;
import com.kaua.ecommerce.customer.application.usecases.customer.CreateCustomerUseCase;
import com.kaua.ecommerce.customer.application.usecases.customer.CreateIdpUserUseCase;
import com.kaua.ecommerce.customer.application.usecases.customer.DeleteIdpUserUseCase;
import com.kaua.ecommerce.customer.domain.customer.idp.UserId;
import com.kaua.ecommerce.customer.infrastructure.rest.req.SignUpRequest;
import com.kaua.ecommerce.customer.infrastructure.rest.res.SignUpResponse;
import com.kaua.ecommerce.lib.domain.exceptions.InternalErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;

@Component
public class SignUpMediator {

    private static final Logger log = LoggerFactory.getLogger(SignUpMediator.class);

    private final CustomerRepository customerRepository;
    private final CreateCustomerUseCase createCustomerUseCase;
    private final CreateIdpUserUseCase createIdpUserUseCase;
    private final DeleteIdpUserUseCase deleteIdpUserUseCase;

    public SignUpMediator(
            final CustomerRepository customerRepository,
            final CreateCustomerUseCase createCustomerUseCase,
            final CreateIdpUserUseCase createIdpUserUseCase,
            final DeleteIdpUserUseCase deleteIdpUserUseCase
    ) {
        this.customerRepository = Objects.requireNonNull(customerRepository);
        this.createCustomerUseCase = Objects.requireNonNull(createCustomerUseCase);
        this.createIdpUserUseCase = Objects.requireNonNull(createIdpUserUseCase);
        this.deleteIdpUserUseCase = Objects.requireNonNull(deleteIdpUserUseCase);
    }

    public SignUpResponse signUp(final SignUpRequest req) {
        return nextCustomerId().andThen(createIdpUser()).andThen(createCustomer()).apply(req);
    }

    private Function<SignUpRequest, SignUpRequest> nextCustomerId() {
        return req -> req.with(this.customerRepository.nextId());
    }

    private Function<SignUpRequest, SignUpRequest> createIdpUser() {
        return req -> req.with(this.createIdpUserUseCase.execute(req.toCreateIdpUserInput()));
    }

    private Function<SignUpRequest, SignUpResponse> createCustomer() {
        return req -> {
            try {
                return new SignUpResponse(this.createCustomerUseCase.execute(req.toCreateCustomerInput()));
            } catch (final Exception ex) {
                handleCompensation(req);
                throw InternalErrorException.with("Failed to create customer and compensate idp user creation");
            }
        };
    }

    private void handleCompensation(final SignUpRequest req) {
        log.info("Compensating for failed creation of customer deleting idp user [userId:{}]", req.idpUserId());
        try {
            this.deleteIdpUserUseCase.execute(new UserId(UUID.fromString(req.idpUserId())));
            log.info("Compensated for failed creation of customer deleting idp user [userId:{}]", req.idpUserId());
        } catch (final Exception ex) {
            log.error("Failed to compensate for failed creation of customer deleting idp user [userId:{}]", req.idpUserId());
            throw InternalErrorException.with("Failed to compensate for failed creation of customer deleting idp user");
            // in future send to sentry or other monitoring tool
        }
    }
}
