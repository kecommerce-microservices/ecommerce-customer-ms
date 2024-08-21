package com.kaua.ecommerce.customer.infrastructure.configurations.usecases;

import com.kaua.ecommerce.customer.application.gateways.IdentityProviderGateway;
import com.kaua.ecommerce.customer.application.repositories.CustomerRepository;
import com.kaua.ecommerce.customer.application.usecases.customer.CreateCustomerUseCase;
import com.kaua.ecommerce.customer.application.usecases.customer.CreateIdpUserUseCase;
import com.kaua.ecommerce.customer.application.usecases.customer.DeleteIdpUserUseCase;
import com.kaua.ecommerce.customer.application.usecases.customer.impl.DefaultCreateCustomerUseCase;
import com.kaua.ecommerce.customer.application.usecases.customer.impl.DefaultCreateIdpUserUseCase;
import com.kaua.ecommerce.customer.application.usecases.customer.impl.DefaultDeleteIdpUserUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class CustomerUseCaseConfig {

    @Bean
    public CreateCustomerUseCase createCustomerUseCase(final CustomerRepository customerRepository) {
        return new DefaultCreateCustomerUseCase(customerRepository);
    }

    @Bean
    public CreateIdpUserUseCase createIdpUserUseCase(final IdentityProviderGateway identityProviderGateway) {
        return new DefaultCreateIdpUserUseCase(identityProviderGateway);
    }

    @Bean
    public DeleteIdpUserUseCase deleteIdpUserUseCase(final IdentityProviderGateway identityProviderGateway) {
        return new DefaultDeleteIdpUserUseCase(identityProviderGateway);
    }
}
