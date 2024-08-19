package com.kaua.ecommerce.customer.domain;

import com.kaua.ecommerce.customer.domain.customer.CustomerId;
import com.kaua.ecommerce.customer.domain.customer.idp.User;
import com.kaua.ecommerce.customer.domain.person.Email;
import com.kaua.ecommerce.customer.domain.person.Name;
import com.kaua.ecommerce.lib.domain.utils.IdentifierUtils;
import com.kaua.ecommerce.lib.domain.utils.RandomStringUtils;
import net.datafaker.Faker;

public final class Fixture {

    private static final Faker faker = new Faker();

    private Fixture() {
    }

    public static final class IdpUsers {

        private IdpUsers() {
        }

        public static String email() {
            return faker.internet().emailAddress();
        }

        public static User withoutUserId(final CustomerId aCustomerId) {
            final var aFirstName = faker.name().firstName();
            final var aFirstNameValid = aFirstName.length() < 3
                    ? aFirstName + RandomStringUtils.generateValue(3 - aFirstName.length())
                    : aFirstName;

            final var aLastName = faker.name().lastName();
            final var aLastNameValid = aLastName.length() < 3
                    ? aLastName + RandomStringUtils.generateValue(3 - aLastName.length())
                    : aLastName;

            return User.newUser(
                    aCustomerId,
                    new Name(aFirstNameValid, aLastNameValid),
                    new Email(email()),
                    "123456Ab*"
            );
        }

        public static User withoutUserId() {
            final var aFirstName = faker.name().firstName();
            final var aFirstNameValid = aFirstName.length() < 3
                    ? aFirstName + RandomStringUtils.generateValue(3 - aFirstName.length())
                    : aFirstName;

            final var aLastName = faker.name().lastName();
            final var aLastNameValid = aLastName.length() < 3
                    ? aLastName + RandomStringUtils.generateValue(3 - aLastName.length())
                    : aLastName;

            return User.newUser(
                    new CustomerId(IdentifierUtils.generateNewUUID()),
                    new Name(aFirstNameValid, aLastNameValid),
                    new Email(email()),
                    "123456Ab*"
            );
        }
    }
}
