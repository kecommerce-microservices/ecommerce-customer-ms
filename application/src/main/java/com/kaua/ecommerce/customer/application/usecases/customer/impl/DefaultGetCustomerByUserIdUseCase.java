package com.kaua.ecommerce.customer.application.usecases.customer.impl;

import com.kaua.ecommerce.customer.application.exceptions.UseCaseInputCannotBeNullException;
import com.kaua.ecommerce.customer.application.gateways.TelephoneGateway;
import com.kaua.ecommerce.customer.application.repositories.CustomerRepository;
import com.kaua.ecommerce.customer.application.usecases.customer.GetCustomerByUserIdUseCase;
import com.kaua.ecommerce.customer.application.usecases.customer.outputs.GetCustomerByIdentifierOutput;
import com.kaua.ecommerce.customer.domain.customer.Customer;
import com.kaua.ecommerce.customer.domain.customer.idp.UserId;
import com.kaua.ecommerce.lib.domain.exceptions.NotFoundException;

import java.util.Objects;

public class DefaultGetCustomerByUserIdUseCase extends GetCustomerByUserIdUseCase {

    private final CustomerRepository customerRepository;
    private final TelephoneGateway telephoneGateway;

    public DefaultGetCustomerByUserIdUseCase(
            final CustomerRepository customerRepository,
            final TelephoneGateway telephoneGateway
    ) {
        this.customerRepository = Objects.requireNonNull(customerRepository);
        this.telephoneGateway = Objects.requireNonNull(telephoneGateway);
    }

    @Override
    public GetCustomerByIdentifierOutput execute(final UserId input) {
        if (input == null) throw new UseCaseInputCannotBeNullException(DefaultGetCustomerByUserIdUseCase.class.getSimpleName());

        final var aCustomer = this.customerRepository.customerOfUserId(input)
                .orElseThrow(NotFoundException.with(Customer.class, input));

        final var aTelephone = aCustomer.getTelephone()
                .map(it -> this.telephoneGateway.formatToLocal(it.value()))
                .orElse(null);

        return new GetCustomerByIdentifierOutput(aCustomer, aTelephone);
    }
}
