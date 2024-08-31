package com.kaua.ecommerce.customer.application.repositories;

import com.kaua.ecommerce.customer.domain.customer.Customer;
import com.kaua.ecommerce.customer.domain.customer.CustomerId;
import com.kaua.ecommerce.customer.domain.customer.idp.UserId;

import java.util.Optional;

public interface CustomerRepository {

    CustomerId nextId();

    boolean existsByEmail(String email);

    boolean existsByDocument(String documentNumber);

    Customer save(Customer customer);

    Optional<Customer> customerOfId(CustomerId customerId);

    Optional<Customer> customerOfUserId(UserId userId);
}
