package com.kaua.ecommerce.customer.application.usecases.customer.outputs;

import com.kaua.ecommerce.customer.application.gateways.TelephoneGateway;
import com.kaua.ecommerce.customer.domain.customer.Customer;

import java.time.Instant;

public record GetCustomerByIdentifierOutput(
        String customerId,
        String userId,
        String firstName,
        String lastName,
        String fullName,
        String email,
        GetCustomerByIdentifierDocumentOutput document,
        GetCustomerByIdentifierTelephoneOutput telephone,
        Instant createdAt,
        Instant updatedAt,
        long version
) {

    public GetCustomerByIdentifierOutput(
            final Customer aCustomer,
            final TelephoneGateway.PhoneNumberInformation aTelephone
    ) {
        this(
                aCustomer.getId().value().toString(),
                aCustomer.getUserId().value().toString(),
                aCustomer.getName().firstName(),
                aCustomer.getName().lastName(),
                aCustomer.getName().fullName(),
                aCustomer.getEmail().value(),
                aCustomer.getDocument().map(it -> new GetCustomerByIdentifierDocumentOutput(
                        it.formattedValue(),
                        it.type()
                )).orElse(null),
                aTelephone != null ? new GetCustomerByIdentifierTelephoneOutput(
                        aTelephone.phoneNumber(),
                        aTelephone.countryCode(),
                        aTelephone.regionCode()
                ) : null,
                aCustomer.getCreatedAt(),
                aCustomer.getUpdatedAt(),
                aCustomer.getVersion()
        );
    }


    public record GetCustomerByIdentifierDocumentOutput(
            String documentNumber,
            String documentType
    ) {
    }

    public record GetCustomerByIdentifierTelephoneOutput(
            String phoneNumber,
            String countryCode,
            String regionCode
    ) {}
}
