package com.kaua.ecommerce.customer.application.repositories;

import com.kaua.ecommerce.customer.domain.address.Address;
import com.kaua.ecommerce.customer.domain.customer.CustomerId;

public interface AddressRepository {

    Address save(Address address);

    int countByCustomerId(CustomerId customerId);

    boolean existsByCustomerIdAndIsDefaultTrue(CustomerId customerId);
}
