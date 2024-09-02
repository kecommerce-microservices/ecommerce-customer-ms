package com.kaua.ecommerce.customer.domain.address;

import com.kaua.ecommerce.lib.domain.Identifier;

import java.util.UUID;

public record AddressId(UUID value) implements Identifier<UUID> {

    public AddressId {
        this.assertArgumentNotNull(value, "id", "should not be null");
    }
}
