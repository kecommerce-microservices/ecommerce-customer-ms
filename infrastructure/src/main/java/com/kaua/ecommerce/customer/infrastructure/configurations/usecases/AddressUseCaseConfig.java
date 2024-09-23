package com.kaua.ecommerce.customer.infrastructure.configurations.usecases;

import com.kaua.ecommerce.customer.application.gateways.AddressGateway;
import com.kaua.ecommerce.customer.application.repositories.AddressRepository;
import com.kaua.ecommerce.customer.application.usecases.address.*;
import com.kaua.ecommerce.customer.application.usecases.address.impl.*;
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

    @Bean
    public GetAddressByIdUseCase getAddressByIdUseCase(
            final AddressRepository addressRepository
    ) {
        return new DefaultGetAddressByIdUseCase(addressRepository);
    }

    @Bean
    public GetDefaultAddressByCustomerIdUseCase getDefaultAddressByCustomerIdUseCase(
            final AddressRepository addressRepository
    ) {
        return new DefaultGetDefaultAddressByCustomerIdUseCase(addressRepository);
    }
}
