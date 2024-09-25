package com.kaua.ecommerce.customer.application.usecases.address.inputs;

import com.kaua.ecommerce.customer.domain.customer.CustomerId;
import com.kaua.ecommerce.lib.domain.pagination.SearchQuery;

public record ListCustomerAddressesInput(
        CustomerId customerId,
        SearchQuery searchQuery
) {
}
