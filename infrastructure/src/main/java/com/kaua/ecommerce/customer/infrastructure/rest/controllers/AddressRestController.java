package com.kaua.ecommerce.customer.infrastructure.rest.controllers;

import com.kaua.ecommerce.customer.application.usecases.address.*;
import com.kaua.ecommerce.customer.application.usecases.address.inputs.CreateCustomerAddressInput;
import com.kaua.ecommerce.customer.application.usecases.address.inputs.ListCustomerAddressesInput;
import com.kaua.ecommerce.customer.application.usecases.address.inputs.UpdateAddressInput;
import com.kaua.ecommerce.customer.application.usecases.address.inputs.UpdateAddressIsDefaultInput;
import com.kaua.ecommerce.customer.application.usecases.address.outputs.CreateCustomerAddressOutput;
import com.kaua.ecommerce.customer.application.usecases.address.outputs.UpdateAddressIsDefaultOutput;
import com.kaua.ecommerce.customer.application.usecases.address.outputs.UpdateAddressOutput;
import com.kaua.ecommerce.customer.domain.address.AddressId;
import com.kaua.ecommerce.customer.domain.customer.CustomerId;
import com.kaua.ecommerce.customer.infrastructure.configurations.authentication.EcommerceUser;
import com.kaua.ecommerce.customer.infrastructure.rest.AddressRestApi;
import com.kaua.ecommerce.customer.infrastructure.rest.req.address.CreateCustomerAddressRequest;
import com.kaua.ecommerce.customer.infrastructure.rest.req.address.UpdateAddressIsDefaultRequest;
import com.kaua.ecommerce.customer.infrastructure.rest.req.address.UpdateAddressRequest;
import com.kaua.ecommerce.customer.infrastructure.rest.res.GetAddressByIdResponse;
import com.kaua.ecommerce.customer.infrastructure.rest.res.GetDefaultAddressByCustomerIdResponse;
import com.kaua.ecommerce.customer.infrastructure.rest.res.ListCustomerAddressesResponse;
import com.kaua.ecommerce.lib.domain.pagination.Pagination;
import com.kaua.ecommerce.lib.domain.pagination.SearchQuery;
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
    private final UpdateAddressUseCase updateAddressUseCase;
    private final GetAddressByIdUseCase getAddressByIdUseCase;
    private final GetDefaultAddressByCustomerIdUseCase getDefaultAddressByCustomerIdUseCase;
    private final ListCustomerAddressesUseCase listCustomerAddressesUseCase;
    private final DeleteAddressByIdUseCase deleteAddressByIdUseCase;

    public AddressRestController(
            final CreateCustomerAddressUseCase createCustomerAddressUseCase,
            final UpdateAddressIsDefaultUseCase updateAddressIsDefaultUseCase,
            final UpdateAddressUseCase updateAddressUseCase,
            final GetAddressByIdUseCase getAddressByIdUseCase,
            final GetDefaultAddressByCustomerIdUseCase getDefaultAddressByCustomerIdUseCase,
            final ListCustomerAddressesUseCase listCustomerAddressesUseCase,
            final DeleteAddressByIdUseCase deleteAddressByIdUseCase
    ) {
        this.createCustomerAddressUseCase = Objects.requireNonNull(createCustomerAddressUseCase);
        this.updateAddressIsDefaultUseCase = Objects.requireNonNull(updateAddressIsDefaultUseCase);
        this.updateAddressUseCase = Objects.requireNonNull(updateAddressUseCase);
        this.getAddressByIdUseCase = Objects.requireNonNull(getAddressByIdUseCase);
        this.getDefaultAddressByCustomerIdUseCase = Objects.requireNonNull(getDefaultAddressByCustomerIdUseCase);
        this.listCustomerAddressesUseCase = Objects.requireNonNull(listCustomerAddressesUseCase);
        this.deleteAddressByIdUseCase = Objects.requireNonNull(deleteAddressByIdUseCase);
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

        log.info("Address is default updated: {}", aOutput);
        return ResponseEntity.ok(aOutput);
    }

    @Override
    public ResponseEntity<UpdateAddressOutput> updateAddress(
            final String addressId,
            final UpdateAddressRequest request
    ) {
        log.debug("Received a request to update the address: {}", addressId);

        final var aInput = new UpdateAddressInput(
                UUID.fromString(addressId),
                request.title(),
                request.zipCode(),
                request.number(),
                request.complement(),
                request.street(),
                request.district(),
                request.country()
        );

        final var aOutput = this.updateAddressUseCase.execute(aInput);

        log.info("Address updated: {}", aOutput);
        return ResponseEntity.ok(aOutput);
    }

    @Override
    public ResponseEntity<GetAddressByIdResponse> getAddressById(final String addressId) {
        log.debug("Received a request to get the address by id: {}", addressId);

        final var aAddressId = new AddressId(UUID.fromString(addressId));
        final var aOutput = this.getAddressByIdUseCase.execute(aAddressId);

        return ResponseEntity.ok(new GetAddressByIdResponse(aOutput));
    }

    @Override
    public ResponseEntity<GetDefaultAddressByCustomerIdResponse> getDefaultAddressByCustomerId(
            final EcommerceUser user
    ) {
        log.debug("Received a request to get the default address by customer id: {}", user.customerId());

        final var aCustomerId = new CustomerId(user.customerId());
        final var aOutput = this.getDefaultAddressByCustomerIdUseCase.execute(aCustomerId);

        return ResponseEntity.ok(new GetDefaultAddressByCustomerIdResponse(aOutput));
    }

    @Override
    public Pagination<ListCustomerAddressesResponse> listCustomerAddresses(
            final EcommerceUser user,
            final String search,
            final int page,
            final int perPage,
            final String sort,
            final String direction
    ) {
        log.debug("Received a request to list the addresses by customer id: {}", user.customerId());

        final var aCustomerId = new CustomerId(user.customerId());
        final var aSearchQuery = new SearchQuery(page, perPage, search, sort, direction);

        final var aInput = new ListCustomerAddressesInput(aCustomerId, aSearchQuery);

        return this.listCustomerAddressesUseCase.execute(aInput)
                .map(ListCustomerAddressesResponse::new);
    }

    @Override
    public void deleteAddressById(final String addressId) {
        log.debug("Received a request to delete the address by id: {}", addressId);

        final var aAddressId = new AddressId(UUID.fromString(addressId));

        this.deleteAddressByIdUseCase.execute(aAddressId);
    }
}