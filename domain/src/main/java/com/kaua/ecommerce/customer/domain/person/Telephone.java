package com.kaua.ecommerce.customer.domain.person;

import com.kaua.ecommerce.lib.domain.ValueObject;

public record Telephone(String value) implements ValueObject {

    public Telephone {
        this.assertArgumentNotEmpty(value, "telephone", "should not be empty");
    }
}
