package com.kaua.ecommerce.customer.application.repositories;

import com.kaua.ecommerce.customer.domain.address.Address;
import com.kaua.ecommerce.customer.domain.address.AddressId;
import com.kaua.ecommerce.customer.domain.customer.CustomerId;
import com.kaua.ecommerce.lib.domain.pagination.Pagination;
import com.kaua.ecommerce.lib.domain.pagination.SearchQuery;

import java.util.Optional;

public interface AddressRepository {

    Address save(Address address);

    Optional<Address> addressOfId(AddressId addressId);

    Optional<Address> addressByCustomerIdAndIsDefaultTrue(CustomerId customerId);

    int countByCustomerId(CustomerId customerId);

    boolean existsByCustomerIdAndIsDefaultTrue(CustomerId customerId);

    Pagination<Address> addressesByCustomerId(CustomerId customerId, SearchQuery searchQuery);

    void delete(AddressId addressId);
}
