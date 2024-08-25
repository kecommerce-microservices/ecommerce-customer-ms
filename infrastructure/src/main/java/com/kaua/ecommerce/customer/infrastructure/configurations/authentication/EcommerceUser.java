package com.kaua.ecommerce.customer.infrastructure.configurations.authentication;

import com.kaua.ecommerce.customer.domain.customer.idp.UserId;
import com.kaua.ecommerce.lib.infrastructure.exceptions.ForbiddenException;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Optional;
import java.util.UUID;

public record EcommerceUser(
        UUID id,
        UUID customerId
) {

    private static final String CUSTOMER_ID = "customer_id";

    public static EcommerceUser fromJwt(final Jwt jwt, final CustomerFromUserIdResolver customerResolver) {
        final var aIdpUserId = UUID.fromString(jwt.getSubject());
        return new EcommerceUser(aIdpUserId,
                Optional.ofNullable(jwt.getClaimAsString(CUSTOMER_ID))
                        .map(UUID::fromString)
                        .or(() -> customerResolver.apply(new UserId(aIdpUserId)).map(cus -> cus.getUserId().value()))
                        .orElseThrow(() -> ForbiddenException.with("Could not resolve customer from user")));
    }
}
