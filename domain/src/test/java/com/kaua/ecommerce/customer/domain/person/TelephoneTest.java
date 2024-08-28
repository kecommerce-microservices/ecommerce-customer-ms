package com.kaua.ecommerce.customer.domain.person;

import com.kaua.ecommerce.customer.domain.UnitTest;
import com.kaua.ecommerce.lib.domain.exceptions.ValidationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TelephoneTest extends UnitTest {

    @Test
    void givenAValidPhoneNumber_whenCreateNewTelephone_thenShouldBeCreated() {
        final var aNumber = "5511999999999";

        final var aTelephone = new Telephone(aNumber);

        Assertions.assertEquals(aNumber, aTelephone.value());
    }

    @Test
    void givenAnEmptyPhoneNumber_whenCreateNewTelephone_thenShouldThrowException() {
        final var aNumber = "";

        final var aProperty = "telephone";
        final var aErrorMessage = "should not be empty";

        final var aException = Assertions.assertThrows(ValidationException.class,
                () -> new Telephone(aNumber));

        Assertions.assertEquals(aProperty, aException.getErrors().get(0).property());
        Assertions.assertEquals(aErrorMessage, aException.getErrors().get(0).message());
    }
}
