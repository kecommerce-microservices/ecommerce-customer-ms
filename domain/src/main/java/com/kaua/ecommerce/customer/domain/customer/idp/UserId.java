package com.kaua.ecommerce.customer.domain.customer.idp;

import com.kaua.ecommerce.lib.domain.Identifier;

import java.util.UUID;

public record UserId(UUID value) implements Identifier<UUID> {

//    public UserId {
//        this.assertArgumentNotNull(value, "id", "should not be null");
//    }

    public static UserId empty() {
        return new UserId(null);
    }
}
