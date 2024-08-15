package com.kaua.ecommerce.customer.domain.customer;

import com.kaua.ecommerce.lib.domain.Identifier;

import java.util.UUID;

public record CustomerId(UUID value) implements Identifier<UUID> {

    public CustomerId {
        this.assertArgumentNotNull(value, "id", "should not be null");
    }
}
