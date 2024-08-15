package com.kaua.ecommerce.customer.application.usecases.customer.impl;

import com.kaua.ecommerce.customer.application.exceptions.UseCaseInputCannotBeNullException;
import com.kaua.ecommerce.customer.application.gateways.IdentityProviderGateway;
import com.kaua.ecommerce.customer.application.usecases.customer.CreateIdpUserUseCase;
import com.kaua.ecommerce.customer.application.usecases.customer.inputs.CreateIdpUserInput;
import com.kaua.ecommerce.customer.application.usecases.customer.outputs.CreateIdpUserOutput;
import com.kaua.ecommerce.customer.domain.customer.CustomerId;
import com.kaua.ecommerce.customer.domain.customer.idp.User;
import com.kaua.ecommerce.customer.domain.person.Email;
import com.kaua.ecommerce.customer.domain.person.Name;

import java.util.Objects;

public class DefaultCreateIdpUserUseCase extends CreateIdpUserUseCase {

    private final IdentityProviderGateway identityProviderGateway;

    public DefaultCreateIdpUserUseCase(final IdentityProviderGateway identityProviderGateway) {
        this.identityProviderGateway = Objects.requireNonNull(identityProviderGateway);
    }

    @Override
    public CreateIdpUserOutput execute(final CreateIdpUserInput input) {
        if (input == null) throw new UseCaseInputCannotBeNullException(DefaultCreateIdpUserUseCase.class.getSimpleName());

        final var aUser = this.newUserWith(input);
        final var aCreatedUser = this.identityProviderGateway.create(aUser);
        return new CreateIdpUserOutput(aCreatedUser);
    }

    private User newUserWith(final CreateIdpUserInput input) {
        return User.newUser(
                new CustomerId(input.customerId()),
                new Name(input.firstName(), input.lastName()),
                new Email(input.email()),
                input.password()
        );
    }
}
