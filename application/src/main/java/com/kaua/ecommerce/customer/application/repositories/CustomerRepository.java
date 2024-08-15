package com.kaua.ecommerce.customer.application.repositories;

import com.kaua.ecommerce.customer.domain.customer.Customer;
import com.kaua.ecommerce.customer.domain.customer.CustomerId;

public interface CustomerRepository {

    CustomerId nextId();

    boolean existsByEmail(String email);

    Customer save(Customer customer);
}
