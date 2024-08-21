package com.kaua.ecommerce.customer.application.gateways;

import com.kaua.ecommerce.customer.domain.customer.idp.User;
import com.kaua.ecommerce.customer.domain.customer.idp.UserId;

public interface IdentityProviderGateway {

    UserId create(User user);

    void deleteOfUserId(UserId userId);
}
