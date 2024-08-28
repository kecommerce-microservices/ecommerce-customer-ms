package com.kaua.ecommerce.customer.application.usecases.customer.outputs;

import com.kaua.ecommerce.customer.domain.customer.Customer;

public record UpdateCustomerTelephoneOutput(String customerId, String telephone) {

    public UpdateCustomerTelephoneOutput(final Customer aCustomer, final String aTelephoneFormatted) {
        this(aCustomer.getId().value().toString(), aTelephoneFormatted);
    }
}
