package com.kaua.ecommerce.customer.application.exceptions;

import com.kaua.ecommerce.lib.domain.exceptions.NoStacktraceException;

public class UseCaseInputCannotBeNullException extends NoStacktraceException {

    public UseCaseInputCannotBeNullException(String useCaseName) {
        super("Input to %s cannot be null".formatted(useCaseName));
    }
}
