package com.kaua.ecommerce.customer.infrastructure.rest.controllers;

import com.kaua.ecommerce.customer.application.usecases.address.CreateCustomerAddressUseCase;
import com.kaua.ecommerce.customer.application.usecases.address.UpdateAddressIsDefaultUseCase;
import com.kaua.ecommerce.customer.application.usecases.address.inputs.CreateCustomerAddressInput;
import com.kaua.ecommerce.customer.application.usecases.address.inputs.UpdateAddressIsDefaultInput;
import com.kaua.ecommerce.customer.application.usecases.address.outputs.CreateCustomerAddressOutput;
import com.kaua.ecommerce.customer.application.usecases.address.outputs.UpdateAddressIsDefaultOutput;
import com.kaua.ecommerce.customer.infrastructure.configurations.authentication.EcommerceUser;
import com.kaua.ecommerce.customer.infrastructure.rest.AddressRestApi;
import com.kaua.ecommerce.customer.infrastructure.rest.req.address.CreateCustomerAddressRequest;
import com.kaua.ecommerce.customer.infrastructure.rest.req.address.UpdateAddressIsDefaultRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;
import java.util.UUID;

@RestController
public class AddressRestController implements AddressRestApi {

    private static final Logger log = LoggerFactory.getLogger(AddressRestController.class);

    private final CreateCustomerAddressUseCase createCustomerAddressUseCase;
    private final UpdateAddressIsDefaultUseCase updateAddressIsDefaultUseCase;

    public AddressRestController(
            final CreateCustomerAddressUseCase createCustomerAddressUseCase,
            final UpdateAddressIsDefaultUseCase updateAddressIsDefaultUseCase
    ) {
        this.createCustomerAddressUseCase = Objects.requireNonNull(createCustomerAddressUseCase);
        this.updateAddressIsDefaultUseCase = Objects.requireNonNull(updateAddressIsDefaultUseCase);
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

    @Override
    public ResponseEntity<UpdateAddressIsDefaultOutput> updateAddressIsDefault(
            final String addressId,
            final UpdateAddressIsDefaultRequest request
    ) {
        log.debug("Received a request to update the default address: {}", addressId);

        final var aInput = new UpdateAddressIsDefaultInput(
                UUID.fromString(addressId),
                request.isDefault()
        );

        final var aOutput = this.updateAddressIsDefaultUseCase.execute(aInput);

        log.info("Address updated: {}", aOutput);
        return ResponseEntity.ok(aOutput);
    }
}