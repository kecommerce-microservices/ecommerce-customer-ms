package com.kaua.ecommerce.customer.infrastructure.configurations.authentication;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.UUID;

@Component
public class JwtConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final AuthoritiesConverter authoritiesConverter;

    public JwtConverter() {
        this.authoritiesConverter = new AuthoritiesConverter();
    }

    @Override
    public AbstractAuthenticationToken convert(@NonNull final Jwt jwt) {
        return new EcommerceAuthentication(
                jwt,
                extractPrincipal(jwt),
                extractAuthorities(jwt)
        );
    }

    private EcommerceUser extractPrincipal(final Jwt jwt) {
        return new EcommerceUser(
                UUID.fromString(jwt.getClaimAsString(JwtClaimNames.SUB)),
                UUID.fromString(jwt.getClaimAsString("customer_id"))
        );
    }

    private Collection<? extends GrantedAuthority> extractAuthorities(final Jwt jwt) {
        return this.authoritiesConverter.convert(jwt);
    }
}
