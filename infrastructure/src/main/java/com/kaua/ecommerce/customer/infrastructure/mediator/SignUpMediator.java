package com.kaua.ecommerce.customer.infrastructure.mediator;

import com.kaua.ecommerce.customer.application.repositories.CustomerRepository;
import com.kaua.ecommerce.customer.application.usecases.customer.CreateCustomerUseCase;
import com.kaua.ecommerce.customer.application.usecases.customer.CreateIdpUserUseCase;
import com.kaua.ecommerce.customer.infrastructure.rest.req.SignUpRequest;
import com.kaua.ecommerce.customer.infrastructure.rest.res.SignUpResponse;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.function.Function;

@Component
public class SignUpMediator {

    private final CustomerRepository customerRepository;
    private final CreateCustomerUseCase createCustomerUseCase;
    private final CreateIdpUserUseCase createIdpUserUseCase;

    public SignUpMediator(
            final CustomerRepository customerRepository,
            final CreateCustomerUseCase createCustomerUseCase,
            final CreateIdpUserUseCase createIdpUserUseCase
    ) {
        this.customerRepository = Objects.requireNonNull(customerRepository);
        this.createCustomerUseCase = Objects.requireNonNull(createCustomerUseCase);
        this.createIdpUserUseCase = Objects.requireNonNull(createIdpUserUseCase);
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
        return req -> new SignUpResponse(this.createCustomerUseCase.execute(req.toCreateCustomerInput()));
    }
}
