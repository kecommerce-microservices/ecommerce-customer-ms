package com.kaua.ecommerce.customer.infrastructure.configurations.usecases;

import com.kaua.ecommerce.customer.application.gateways.AddressGateway;
import com.kaua.ecommerce.customer.application.repositories.AddressRepository;
import com.kaua.ecommerce.customer.application.usecases.address.CreateCustomerAddressUseCase;
import com.kaua.ecommerce.customer.application.usecases.address.impl.DefaultCreateCustomerAddressUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class AddressUseCaseConfig {

    @Bean
    public CreateCustomerAddressUseCase createCustomerAddressUseCase(
            final AddressRepository addressRepository,
            final AddressGateway addressGateway
    ) {
        return new DefaultCreateCustomerAddressUseCase(addressRepository, addressGateway);
    }
}
