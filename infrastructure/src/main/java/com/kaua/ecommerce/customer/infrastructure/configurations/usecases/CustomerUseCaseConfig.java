package com.kaua.ecommerce.customer.infrastructure.configurations.usecases;

import com.kaua.ecommerce.customer.application.gateways.IdentityProviderGateway;
import com.kaua.ecommerce.customer.application.gateways.TelephoneGateway;
import com.kaua.ecommerce.customer.application.repositories.CustomerRepository;
import com.kaua.ecommerce.customer.application.usecases.customer.*;
import com.kaua.ecommerce.customer.application.usecases.customer.impl.*;
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

    @Bean
    public UpdateCustomerDocumentUseCase updateCustomerDocumentUseCase(final CustomerRepository customerRepository) {
        return new DefaultUpdateCustomerDocumentUseCase(customerRepository);
    }

    @Bean
    public UpdateCustomerTelephoneUseCase updateCustomerTelephoneUseCase(final CustomerRepository customerRepository, final TelephoneGateway telephoneGateway) {
        return new DefaultUpdateCustomerTelephoneUseCase(customerRepository, telephoneGateway);
    }

    @Bean
    public GetCustomerByUserIdUseCase getCustomerByUserIdUseCase(final CustomerRepository customerRepository, final TelephoneGateway telephoneGateway) {
        return new DefaultGetCustomerByUserIdUseCase(customerRepository, telephoneGateway);
    }
}
