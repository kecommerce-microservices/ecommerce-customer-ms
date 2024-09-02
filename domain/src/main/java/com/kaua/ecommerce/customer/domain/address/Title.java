package com.kaua.ecommerce.customer.domain.address;

import com.kaua.ecommerce.lib.domain.ValueObject;

public record Title(String value) implements ValueObject {

    private static final String TITLE_PARAM = "title";

    public Title {
        this.assertArgumentNotEmpty(value, TITLE_PARAM, "should not be empty");
        this.assertArgumentMinLength(value, 2, TITLE_PARAM, "should have at least 2 characters");
        this.assertArgumentMaxLength(value, 50, TITLE_PARAM, "should have at most 50 characters");
    }
}
