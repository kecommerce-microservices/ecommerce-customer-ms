package com.kaua.ecommerce.customer.application.usecases.customer.outputs;

import com.kaua.ecommerce.customer.domain.customer.Customer;

import java.util.UUID;

public record CreateCustomerOutput(UUID customerId, UUID userId) {

    public CreateCustomerOutput(final Customer customer) {
        this(customer.getId().value(), customer.getUserId().value());
    }
}
