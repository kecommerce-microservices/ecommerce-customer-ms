package com.kaua.ecommerce.customer.infrastructure.gateways;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.kaua.ecommerce.customer.application.gateways.TelephoneGateway;
import com.kaua.ecommerce.lib.domain.exceptions.ValidationException;
import com.kaua.ecommerce.lib.domain.validation.Error;
import org.springframework.stereotype.Component;

@Component
public class TelephoneGatewayImpl implements TelephoneGateway {

    private final PhoneNumberUtil phoneNumberUtil;

    public TelephoneGatewayImpl() {
        this.phoneNumberUtil = PhoneNumberUtil.getInstance();
    }

    @Override
    public boolean isValid(final String aPhoneNumber) {
        try {
            final var aParsedPhoneNumber = this.phoneNumberUtil.parse(aPhoneNumber, null);

            return this.phoneNumberUtil.isValidNumber(aParsedPhoneNumber);
        } catch (final NumberParseException e) {
            return false;
        }
    }

    @Override
    public String format(final String aPhoneNumber) {
        try {
            final var aParsedPhoneNumber = this.phoneNumberUtil.parse(aPhoneNumber, null);

            return this.phoneNumberUtil.format(aParsedPhoneNumber, PhoneNumberUtil.PhoneNumberFormat.E164);
        } catch (final NumberParseException e) {
            throw ValidationException.with(new Error("Invalid telephone or telephone format"));
        }
    }

    @Override
    public PhoneNumberInformation formatToLocal(final String aPhoneNumber) {
        try {
            final var aParsedPhoneNumber = this.phoneNumberUtil.parse(aPhoneNumber, null);

            final var aNationalPhoneNumber =  this.phoneNumberUtil.format(aParsedPhoneNumber,
                    PhoneNumberUtil.PhoneNumberFormat.NATIONAL);

            final var aRegionCode = this.phoneNumberUtil.getRegionCodeForNumber(aParsedPhoneNumber);

            return new PhoneNumberInformation(
                    aNationalPhoneNumber,
                    "+" + aParsedPhoneNumber.getCountryCode(),
                    aRegionCode
            );
        } catch (final NumberParseException e) {
            throw ValidationException.with(new Error("Invalid telephone or telephone format"));
        }
    }
}
