package com.kaua.ecommerce.customer.application.usecases.customer.impl;

import com.kaua.ecommerce.customer.application.exceptions.UseCaseInputCannotBeNullException;
import com.kaua.ecommerce.customer.application.gateways.TelephoneGateway;
import com.kaua.ecommerce.customer.application.repositories.CustomerRepository;
import com.kaua.ecommerce.customer.application.usecases.customer.UpdateCustomerTelephoneUseCase;
import com.kaua.ecommerce.customer.application.usecases.customer.inputs.UpdateCustomerTelephoneInput;
import com.kaua.ecommerce.customer.application.usecases.customer.outputs.UpdateCustomerTelephoneOutput;
import com.kaua.ecommerce.customer.domain.customer.Customer;
import com.kaua.ecommerce.customer.domain.customer.CustomerId;
import com.kaua.ecommerce.customer.domain.person.Telephone;
import com.kaua.ecommerce.lib.domain.exceptions.NotFoundException;
import com.kaua.ecommerce.lib.domain.exceptions.ValidationException;
import com.kaua.ecommerce.lib.domain.validation.Error;

import java.util.Objects;

public class DefaultUpdateCustomerTelephoneUseCase extends UpdateCustomerTelephoneUseCase {

    private final CustomerRepository customerRepository;
    private final TelephoneGateway telephoneGateway;

    public DefaultUpdateCustomerTelephoneUseCase(
            final CustomerRepository customerRepository,
            final TelephoneGateway telephoneGateway
    ) {
        this.customerRepository = Objects.requireNonNull(customerRepository);
        this.telephoneGateway = Objects.requireNonNull(telephoneGateway);
    }

    @Override
    public UpdateCustomerTelephoneOutput execute(final UpdateCustomerTelephoneInput input) {
        if (input == null) throw new UseCaseInputCannotBeNullException(DefaultUpdateCustomerTelephoneUseCase.class.getSimpleName());

        final var aCustomerId = new CustomerId(input.customerId());

        final var aCustomer = this.customerRepository.customerOfId(aCustomerId)
                .orElseThrow(NotFoundException.with(Customer.class, aCustomerId));

        if (!this.telephoneGateway.isValid(input.telephone())) {
            throw ValidationException.with(new Error("Invalid telephone"));
        }

        final var aTelephoneInternational = this.telephoneGateway.format(input.telephone());

        final var aCustomerUpdated = aCustomer.updateTelephone(new Telephone(aTelephoneInternational));

        return new UpdateCustomerTelephoneOutput(this.customerRepository
                .save(aCustomerUpdated),
                this.telephoneGateway.formatToLocal(aTelephoneInternational).phoneNumber());
    }
}
