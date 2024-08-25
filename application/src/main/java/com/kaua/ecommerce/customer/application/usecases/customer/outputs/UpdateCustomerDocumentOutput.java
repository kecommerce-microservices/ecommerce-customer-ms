package com.kaua.ecommerce.customer.application.usecases.customer.outputs;

import com.kaua.ecommerce.customer.domain.customer.Customer;
import com.kaua.ecommerce.customer.domain.person.Document;
import com.kaua.ecommerce.lib.domain.exceptions.InternalErrorException;

public record UpdateCustomerDocumentOutput(String customerId, String documentType) {

    public UpdateCustomerDocumentOutput(final Customer aCustomer) {
        this(aCustomer.getId().value().toString(), aCustomer.getDocument().map(Document::type)
                .orElseThrow(() -> InternalErrorException.with("On creating %s, document type is null"
                        .formatted(UpdateCustomerDocumentOutput.class.getSimpleName()))));
    }
}
