package com.kaua.ecommerce.customer.infrastructure.gateways;

import com.kaua.ecommerce.customer.application.gateways.IdentityProviderGateway;
import com.kaua.ecommerce.customer.domain.customer.idp.User;
import com.kaua.ecommerce.customer.domain.customer.idp.UserId;
import org.springframework.stereotype.Component;

@Component
public class AuthServerIdentityProviderClient implements IdentityProviderGateway {

    @Override
    public UserId create(User user) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
