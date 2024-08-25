package com.kaua.ecommerce.customer.application.usecases.customer.impl;

import com.kaua.ecommerce.customer.application.exceptions.UseCaseInputCannotBeNullException;
import com.kaua.ecommerce.customer.application.repositories.CustomerRepository;
import com.kaua.ecommerce.customer.application.usecases.customer.UpdateCustomerDocumentUseCase;
import com.kaua.ecommerce.customer.application.usecases.customer.inputs.UpdateCustomerDocumentInput;
import com.kaua.ecommerce.customer.application.usecases.customer.outputs.UpdateCustomerDocumentOutput;
import com.kaua.ecommerce.customer.domain.customer.Customer;
import com.kaua.ecommerce.customer.domain.customer.CustomerId;
import com.kaua.ecommerce.customer.domain.person.Document;
import com.kaua.ecommerce.lib.domain.exceptions.DomainException;
import com.kaua.ecommerce.lib.domain.exceptions.NotFoundException;

import java.util.Objects;

public class DefaultUpdateCustomerDocumentUseCase extends UpdateCustomerDocumentUseCase {

    private final CustomerRepository customerRepository;

    public DefaultUpdateCustomerDocumentUseCase(final CustomerRepository customerRepository) {
        this.customerRepository = Objects.requireNonNull(customerRepository);
    }

    @Override
    public UpdateCustomerDocumentOutput execute(final UpdateCustomerDocumentInput input) {
        if (input == null) throw new UseCaseInputCannotBeNullException(DefaultUpdateCustomerDocumentUseCase.class.getSimpleName());

        final var aDocument = Document.create(input.documentNumber(), input.documentType());

        if (this.customerRepository.existsByDocument(aDocument.value())) {
            throw DomainException.with("Document already exists");
        }

        final var aCustomerId = new CustomerId(input.customerId());
        final var aCustomer = this.customerRepository.customerOfId(aCustomerId)
                .orElseThrow(NotFoundException.with(Customer.class, aCustomerId));


        final var aCustomerUpdated = aCustomer.updateDocument(aDocument);

        return new UpdateCustomerDocumentOutput(this.customerRepository.save(aCustomerUpdated));
    }
}
