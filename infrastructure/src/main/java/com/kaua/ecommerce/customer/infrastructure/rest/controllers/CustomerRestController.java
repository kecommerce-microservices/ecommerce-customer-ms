package com.kaua.ecommerce.customer.infrastructure.rest.controllers;

import com.kaua.ecommerce.customer.infrastructure.mediator.SignUpMediator;
import com.kaua.ecommerce.customer.infrastructure.rest.CustomerRestApi;
import com.kaua.ecommerce.customer.infrastructure.rest.req.CreateCustomerRequest;
import com.kaua.ecommerce.customer.infrastructure.rest.res.SignUpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
public class CustomerRestController implements CustomerRestApi {

    private static final Logger log = LoggerFactory.getLogger(CustomerRestController.class);

    private final SignUpMediator signUpMediator;

    public CustomerRestController(final SignUpMediator signUpMediator) {
        this.signUpMediator = Objects.requireNonNull(signUpMediator);
    }

    @Override
    public ResponseEntity<SignUpResponse> signUp(final CreateCustomerRequest request) {
        log.debug("Received a request to create a new customer and IdpUser: {}", request.email());

        final var aOutput = this.signUpMediator.signUp(request.toSignUpRequest());

        log.info("Customer and IdpUser created: {}", aOutput);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header("Location", "/v1/customers/%s".formatted(aOutput.customerId()))
                .body(aOutput);
    }
}
