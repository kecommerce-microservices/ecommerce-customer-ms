package com.kaua.ecommerce.customer.infrastructure.rest.controllers;

import com.kaua.ecommerce.customer.application.usecases.customer.UpdateCustomerDocumentUseCase;
import com.kaua.ecommerce.customer.application.usecases.customer.UpdateCustomerTelephoneUseCase;
import com.kaua.ecommerce.customer.application.usecases.customer.inputs.UpdateCustomerDocumentInput;
import com.kaua.ecommerce.customer.application.usecases.customer.inputs.UpdateCustomerTelephoneInput;
import com.kaua.ecommerce.customer.application.usecases.customer.outputs.UpdateCustomerDocumentOutput;
import com.kaua.ecommerce.customer.application.usecases.customer.outputs.UpdateCustomerTelephoneOutput;
import com.kaua.ecommerce.customer.infrastructure.configurations.authentication.EcommerceUser;
import com.kaua.ecommerce.customer.infrastructure.mediator.SignUpMediator;
import com.kaua.ecommerce.customer.infrastructure.rest.CustomerRestApi;
import com.kaua.ecommerce.customer.infrastructure.rest.req.CreateCustomerRequest;
import com.kaua.ecommerce.customer.infrastructure.rest.req.UpdateCustomerDocumentRequest;
import com.kaua.ecommerce.customer.infrastructure.rest.req.UpdateCustomerTelephoneRequest;
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
    private final UpdateCustomerDocumentUseCase updateCustomerDocumentUseCase;
    private final UpdateCustomerTelephoneUseCase updateCustomerTelephoneUseCase;

    public CustomerRestController(
            final SignUpMediator signUpMediator,
            final UpdateCustomerDocumentUseCase updateCustomerDocumentUseCase,
            final UpdateCustomerTelephoneUseCase updateCustomerTelephoneUseCase
    ) {
        this.signUpMediator = Objects.requireNonNull(signUpMediator);
        this.updateCustomerDocumentUseCase = Objects.requireNonNull(updateCustomerDocumentUseCase);
        this.updateCustomerTelephoneUseCase = Objects.requireNonNull(updateCustomerTelephoneUseCase);
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

    @Override
    public ResponseEntity<UpdateCustomerDocumentOutput> updateDocument(
            final EcommerceUser principal,
            final UpdateCustomerDocumentRequest request
    ) {
        log.debug("Received a request to update a customer document: {}", request);

        final var aInput = new UpdateCustomerDocumentInput(
                principal.customerId(),
                request.documentNumber(),
                request.documentType()
        );

        final var aOutput = this.updateCustomerDocumentUseCase.execute(aInput);

        log.info("Customer document updated: {}", aOutput);
        return ResponseEntity.ok(aOutput);
    }

    @Override
    public ResponseEntity<UpdateCustomerTelephoneOutput> updateTelephone(
            final EcommerceUser principal,
            final UpdateCustomerTelephoneRequest request
    ) {
        log.debug("Received a request to update a customer telephone: {}", request);

        final var aInput = new UpdateCustomerTelephoneInput(
                principal.customerId(),
                request.phoneNumber()
        );

        final var aOutput = this.updateCustomerTelephoneUseCase.execute(aInput);

        log.info("Customer telephone updated: {}", aOutput);
        return ResponseEntity.ok(aOutput);
    }
}
