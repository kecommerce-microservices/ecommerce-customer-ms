package com.kaua.ecommerce.customer.infrastructure.configurations.authentication;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;

public class EcommerceAuthentication extends AbstractAuthenticationToken {

    private final Jwt jwt;
    private final EcommerceUser user;

    public EcommerceAuthentication(
            final Jwt jwt,
            final EcommerceUser user,
            final Collection<? extends GrantedAuthority> authorities
    ) {
        super(authorities);
        this.jwt = jwt;
        this.user = user;
    }

    @Override
    public Object getCredentials() {
        return jwt;
    }

    @Override
    public Object getPrincipal() {
        return user;
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }
}
