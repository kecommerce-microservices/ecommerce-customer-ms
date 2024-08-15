package com.kaua.ecommerce.customer.application.usecases.customer.impl;

import com.kaua.ecommerce.customer.application.exceptions.UseCaseInputCannotBeNullException;
import com.kaua.ecommerce.customer.application.repositories.CustomerRepository;
import com.kaua.ecommerce.customer.application.usecases.customer.CreateCustomerUseCase;
import com.kaua.ecommerce.customer.application.usecases.customer.inputs.CreateCustomerInput;
import com.kaua.ecommerce.customer.application.usecases.customer.outputs.CreateCustomerOutput;
import com.kaua.ecommerce.customer.domain.customer.Customer;
import com.kaua.ecommerce.customer.domain.customer.CustomerId;
import com.kaua.ecommerce.customer.domain.customer.idp.UserId;
import com.kaua.ecommerce.customer.domain.person.Email;
import com.kaua.ecommerce.customer.domain.person.Name;
import com.kaua.ecommerce.lib.domain.exceptions.DomainException;

import java.util.Objects;

public class DefaultCreateCustomerUseCase extends CreateCustomerUseCase {

    private final CustomerRepository customerRepository;

    public DefaultCreateCustomerUseCase(final CustomerRepository customerRepository) {
        this.customerRepository = Objects.requireNonNull(customerRepository);
    }

    @Override
    public CreateCustomerOutput execute(final CreateCustomerInput input) {
        if (input == null) throw new UseCaseInputCannotBeNullException(DefaultCreateCustomerUseCase.class.getSimpleName());

        if (this.customerRepository.existsByEmail(input.email())) {
            throw DomainException.with("Email already exists");
        }

        final var aCustomer = this.newCustomerWith(input);
        final var aSavedCustomer = this.customerRepository.save(aCustomer);
        return new CreateCustomerOutput(aSavedCustomer);
    }

    private Customer newCustomerWith(final CreateCustomerInput input) {
        return Customer.newCustomer(
                new CustomerId(input.customerId()),
                new UserId(input.userId()),
                new Email(input.email()),
                new Name(input.firstName(), input.lastName())
        );
    }
}
