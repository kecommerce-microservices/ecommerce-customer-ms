package com.kaua.ecommerce.customer.infrastructure.configurations.usecases;

import com.kaua.ecommerce.customer.application.gateways.AddressGateway;
import com.kaua.ecommerce.customer.application.repositories.AddressRepository;
import com.kaua.ecommerce.customer.application.usecases.address.CreateCustomerAddressUseCase;
import com.kaua.ecommerce.customer.application.usecases.address.UpdateAddressIsDefaultUseCase;
import com.kaua.ecommerce.customer.application.usecases.address.UpdateAddressUseCase;
import com.kaua.ecommerce.customer.application.usecases.address.impl.DefaultCreateCustomerAddressUseCase;
import com.kaua.ecommerce.customer.application.usecases.address.impl.DefaultUpdateAddressIsDefaultUseCase;
import com.kaua.ecommerce.customer.application.usecases.address.impl.DefaultUpdateAddressUseCase;
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

    @Bean
    public UpdateAddressIsDefaultUseCase updateAddressIsDefaultUseCase(
            final AddressRepository addressRepository
    ) {
        return new DefaultUpdateAddressIsDefaultUseCase(addressRepository);
    }

    @Bean
    public UpdateAddressUseCase updateAddressUseCase(
            final AddressRepository addressRepository,
            final AddressGateway addressGateway
    ) {
        return new DefaultUpdateAddressUseCase(addressGateway, addressRepository);
    }
}
