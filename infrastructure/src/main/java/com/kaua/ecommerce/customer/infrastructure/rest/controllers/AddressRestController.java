package com.kaua.ecommerce.customer.infrastructure.rest.controllers;

import com.kaua.ecommerce.customer.application.usecases.address.CreateCustomerAddressUseCase;
import com.kaua.ecommerce.customer.application.usecases.address.inputs.CreateCustomerAddressInput;
import com.kaua.ecommerce.customer.application.usecases.address.outputs.CreateCustomerAddressOutput;
import com.kaua.ecommerce.customer.infrastructure.configurations.authentication.EcommerceUser;
import com.kaua.ecommerce.customer.infrastructure.rest.AddressRestApi;
import com.kaua.ecommerce.customer.infrastructure.rest.req.address.CreateCustomerAddressRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
public class AddressRestController implements AddressRestApi {

    private static final Logger log = LoggerFactory.getLogger(AddressRestController.class);

    private final CreateCustomerAddressUseCase createCustomerAddressUseCase;

    public AddressRestController(
            final CreateCustomerAddressUseCase createCustomerAddressUseCase
    ) {
        this.createCustomerAddressUseCase = Objects.requireNonNull(createCustomerAddressUseCase);
    }

    @Override
    public ResponseEntity<CreateCustomerAddressOutput> createAddress(
            final EcommerceUser user,
            final CreateCustomerAddressRequest request
    ) {
        log.debug("Received a request to create a new address: {}", request);

        final var aInput = new CreateCustomerAddressInput(
                user.customerId(),
                request.title(),
                request.zipCode(),
                request.number(),
                request.complement(),
                request.country(),
                request.isDefault()
        );

        final var aOutput = this.createCustomerAddressUseCase.execute(aInput);

        log.info("Address created: {}", aOutput);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header("Location", "/v1/addresses/%s".formatted(aOutput.addressId()))
                .body(aOutput);
    }
}