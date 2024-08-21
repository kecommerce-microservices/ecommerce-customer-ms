package com.kaua.ecommerce.customer.application.usecases.customer.impl;

import com.kaua.ecommerce.customer.application.exceptions.UseCaseInputCannotBeNullException;
import com.kaua.ecommerce.customer.application.gateways.IdentityProviderGateway;
import com.kaua.ecommerce.customer.application.usecases.customer.DeleteIdpUserUseCase;
import com.kaua.ecommerce.customer.domain.customer.idp.UserId;

import java.util.Objects;

public class DefaultDeleteIdpUserUseCase extends DeleteIdpUserUseCase {

    private final IdentityProviderGateway identityProviderGateway;

    public DefaultDeleteIdpUserUseCase(final IdentityProviderGateway identityProviderGateway) {
        this.identityProviderGateway = Objects.requireNonNull(identityProviderGateway);
    }

    @Override
    public void execute(final UserId input) {
        if (input == null) throw new UseCaseInputCannotBeNullException(DefaultDeleteIdpUserUseCase.class.getSimpleName());

        this.identityProviderGateway.deleteOfUserId(input);
    }
}
