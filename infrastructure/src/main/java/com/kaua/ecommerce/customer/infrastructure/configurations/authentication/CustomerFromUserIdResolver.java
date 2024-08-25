package com.kaua.ecommerce.customer.infrastructure.configurations.authentication;

import com.kaua.ecommerce.customer.domain.customer.Customer;
import com.kaua.ecommerce.customer.domain.customer.idp.UserId;

import java.util.Optional;
import java.util.function.Function;

public interface CustomerFromUserIdResolver extends Function<UserId, Optional<Customer>> {
}
