package com.kaua.ecommerce.customer.domain;

import com.kaua.ecommerce.customer.domain.address.Address;
import com.kaua.ecommerce.customer.domain.address.Title;
import com.kaua.ecommerce.customer.domain.customer.Customer;
import com.kaua.ecommerce.customer.domain.customer.CustomerId;
import com.kaua.ecommerce.customer.domain.customer.idp.User;
import com.kaua.ecommerce.customer.domain.customer.idp.UserId;
import com.kaua.ecommerce.customer.domain.person.Document;
import com.kaua.ecommerce.customer.domain.person.Email;
import com.kaua.ecommerce.customer.domain.person.Name;
import com.kaua.ecommerce.customer.domain.person.Telephone;
import com.kaua.ecommerce.lib.domain.utils.IdentifierUtils;
import com.kaua.ecommerce.lib.domain.utils.RandomStringUtils;
import net.datafaker.Faker;

public final class Fixture {

    private static final Faker faker = new Faker();

    private Fixture() {
    }

    public static String email() {
        return faker.internet().emailAddress();
    }

    public static final class IdpUsers {

        private IdpUsers() {
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

    public static final class Customers {
        private Customers() {
        }

        public static Customer newCustomer() {
            final var aFirstName = faker.name().firstName();
            final var aFirstNameValid = aFirstName.length() < 3
                    ? aFirstName + RandomStringUtils.generateValue(3 - aFirstName.length())
                    : aFirstName;

            final var aLastName = faker.name().lastName();
            final var aLastNameValid = aLastName.length() < 3
                    ? aLastName + RandomStringUtils.generateValue(3 - aLastName.length())
                    : aLastName;

            return Customer.newCustomer(
                    new CustomerId(IdentifierUtils.generateNewUUID()),
                    new UserId(IdentifierUtils.generateNewUUID()),
                    new Email(email()),
                    new Name(aFirstNameValid, aLastNameValid)
            );
        }

        public static Customer newCustomerWithAllValues() {
            final var aFirstName = faker.name().firstName();
            final var aFirstNameValid = aFirstName.length() < 3
                    ? aFirstName + RandomStringUtils.generateValue(3 - aFirstName.length())
                    : aFirstName;

            final var aLastName = faker.name().lastName();
            final var aLastNameValid = aLastName.length() < 3
                    ? aLastName + RandomStringUtils.generateValue(3 - aLastName.length())
                    : aLastName;

            final var aCustomer = Customer.newCustomer(
                    new CustomerId(IdentifierUtils.generateNewUUID()),
                    new UserId(IdentifierUtils.generateNewUUID()),
                    new Email(email()),
                    new Name(aFirstNameValid, aLastNameValid)
            );

            aCustomer.updateDocument(Document.create("479.993.810-04", "CPF"));
            aCustomer.updateTelephone(new Telephone("+5511987654321"));

            return aCustomer;
        }
    }

    public static final class Addresses {
        private Addresses() {}

        public static Address newAddressWithComplement(final CustomerId aCustomerId, final boolean aIsDefault) {
            final var aAddress = faker.address();

            return Address.newAddress(
                    new Title("Home"),
                    aCustomerId,
                    aAddress.zipCode(),
                    aAddress.buildingNumber(),
                    aAddress.streetName(),
                    aAddress.city(),
                    "District",
                    aAddress.country(),
                    aAddress.state(),
                    "Complement",
                    aIsDefault
            );
        }
    }
}
