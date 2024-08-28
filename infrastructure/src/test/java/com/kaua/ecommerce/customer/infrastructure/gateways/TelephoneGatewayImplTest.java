package com.kaua.ecommerce.customer.infrastructure.gateways;

import com.kaua.ecommerce.customer.IntegrationTest;
import com.kaua.ecommerce.lib.domain.exceptions.ValidationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
class TelephoneGatewayImplTest {

    @Autowired
    private TelephoneGatewayImpl telephoneGateway;

    @Test
    void givenAValidPhoneNumber_whenCallIsValid_thenShouldReturnTrue() {
        final var aPhoneNumber = "+55 (27) 2445-8092";

        final var aResult = this.telephoneGateway.isValid(aPhoneNumber);

        Assertions.assertTrue(aResult);
    }

    @Test
    void givenAnInvalidPhoneNumber_whenCallIsValid_thenShouldReturnFalse() {
        final var aPhoneNumber = "invalid-phone-number";

        final var aResult = this.telephoneGateway.isValid(aPhoneNumber);

        Assertions.assertFalse(aResult);
    }

    @Test
    void givenAValidPhoneNumber_whenCallFormat_thenShouldReturnFormattedPhoneNumberInE164() {
        final var aPhoneNumber = "+55 (27) 2445-8092";
        final var aExpected = "+552724458092";

        final var aResult = this.telephoneGateway.format(aPhoneNumber);

        Assertions.assertEquals(aExpected, aResult);
    }

    @Test
    void givenAnInvalidPhoneNumber_whenCallFormat_thenShouldThrowValidationException() {
        final var aPhoneNumber = "invalid-phone-number";

        final var expectedErrorMessage = "Invalid telephone or telephone format";

        final var aException = Assertions.assertThrows(ValidationException.class,
                () -> this.telephoneGateway.format(aPhoneNumber));

        Assertions.assertEquals(expectedErrorMessage, aException.getErrors().get(0).message());
    }

    @ParameterizedTest
    @CsvSource({
            "+12025550123,(202) 555-0123,+1,US",
            "+447911123456,07911 123456,+44,GG",
            "+5511987654321,(11) 98765-4321,+55,BR",
            "+4915212345678,01521 2345678,+49,DE",
            "+14165550123,(416) 555-0123,+1,CA",
            "+61412345678,0412 345 678,+61,AU",
            "+33612345678,06 12 34 56 78,+33,FR",
            "+819012345678,090-1234-5678,+81,JP",
            "+919876543210,098765 43210,+91,IN",
            "+79161234567,8 (916) 123-45-67,+7,RU",
            "+8613912345678,139 1234 5678,+86,CN",
            "+5215512345678,15512345678,+52,MX",
            "+393451234567,345 123 4567,+39,IT",
            "+27821234567,082 123 4567,+27,ZA",
            "+541112345678,011 1234-5678,+54,AR",
            "+2347012345678,0701 234 5678,+234,NG",
            "+34612345678,612 34 56 78,+34,ES",
            "+628123456789,0812-3456-789,+62,ID",
            "+46701234567,070-123 45 67,+46,SE",
    })
    void givenAValidPhoneNumber_whenCallFormatToLocal_thenShouldReturnFormattedPhoneNumberInNationalFormat(
            final String aPhoneNumber,
            final String aFormattedPhoneNumber,
            final String aCountryCode,
            final String aRegionCode
    ) {
        final var aResult = this.telephoneGateway.formatToLocal(aPhoneNumber);

        Assertions.assertEquals(aFormattedPhoneNumber, aResult.phoneNumber());
        Assertions.assertEquals(aCountryCode, aResult.countryCode());
        Assertions.assertEquals(aRegionCode, aResult.regionCode());
    }

    @Test
    void givenAnInvalidPhoneNumber_whenCallFormatToLocal_thenShouldThrowValidationException() {
        final var aPhoneNumber = "invalid-phone-number";

        final var expectedErrorMessage = "Invalid telephone or telephone format";

        final var aException = Assertions.assertThrows(ValidationException.class,
                () -> this.telephoneGateway.formatToLocal(aPhoneNumber));

        Assertions.assertEquals(expectedErrorMessage, aException.getErrors().get(0).message());
    }
}
