package com.kaua.ecommerce.customer.application.usecases.customer.outputs;

import com.kaua.ecommerce.customer.domain.customer.Customer;

public record UpdateCustomerDocumentOutput(String customerId, String documentType) {

    public UpdateCustomerDocumentOutput(final Customer aCustomer) {
        this(aCustomer.getId().value().toString(), aCustomer.getDocument().get().type());
    }
}
