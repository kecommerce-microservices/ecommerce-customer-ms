package com.kaua.ecommerce.customer.application.gateways;

public interface TelephoneGateway {

    boolean isValid(String phoneNumber);

    String format(String phoneNumber);

    PhoneNumberInformation formatToLocal(String phoneNumber);

    record PhoneNumberInformation(
            String phoneNumber,
            String countryCode,
            String regionCode
    ) {}
}
