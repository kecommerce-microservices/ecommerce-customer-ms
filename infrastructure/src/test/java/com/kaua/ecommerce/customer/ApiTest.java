package com.kaua.ecommerce.customer;

import com.kaua.ecommerce.customer.infrastructure.configurations.authentication.EcommerceAuthentication;
import com.kaua.ecommerce.customer.infrastructure.configurations.authentication.EcommerceUser;
import com.kaua.ecommerce.lib.domain.utils.IdentifierUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.List;
import java.util.UUID;

public interface ApiTest {

    static RequestPostProcessor admin() {
        return admin(IdentifierUtils.generateNewUUID());
    }

    static RequestPostProcessor admin(final UUID userId) {
        Jwt.Builder jwtBuilder = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim(JwtClaimNames.SUB, userId)
                .claim("customer_id", IdentifierUtils.generateNewUUID());

        return SecurityMockMvcRequestPostProcessors.authentication(new EcommerceAuthentication(
                jwtBuilder.build(),
                new EcommerceUser(userId, IdentifierUtils.generateNewUUID()),
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
        ));
    }

    static RequestPostProcessor admin(final UUID userId, final UUID customerId) {
        Jwt.Builder jwtBuilder = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim(JwtClaimNames.SUB, userId)
                .claim("customer_id", customerId);

        return SecurityMockMvcRequestPostProcessors.authentication(new EcommerceAuthentication(
                jwtBuilder.build(),
                new EcommerceUser(userId, customerId),
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
        ));
    }
}
