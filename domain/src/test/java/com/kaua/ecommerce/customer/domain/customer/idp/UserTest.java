package com.kaua.ecommerce.customer.domain.customer.idp;

import com.kaua.ecommerce.customer.domain.UnitTest;
import com.kaua.ecommerce.customer.domain.customer.CustomerId;
import com.kaua.ecommerce.customer.domain.person.Email;
import com.kaua.ecommerce.customer.domain.person.Name;
import com.kaua.ecommerce.lib.domain.utils.IdentifierUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class UserTest extends UnitTest {

    @Test
    void givenAValidValues_whenCallNewUser_thenANewUserShouldBeCreated() {
        final var aCustomerId = new CustomerId(IdentifierUtils.generateNewUUID());
        final var aName = new Name("John", "Doe");
        final var aEmail = new Email("testes.ts@tess.com");
        final var aPassword = "123456Ab*";

        final var aUser = User.newUser(aCustomerId, aName, aEmail, aPassword);

        Assertions.assertNotNull(aUser);
        Assertions.assertNotNull(aUser.getUserId());
        Assertions.assertEquals(aCustomerId, aUser.getCustomerId());
        Assertions.assertEquals(aName, aUser.getName());
        Assertions.assertEquals(aEmail, aUser.getEmail());
        Assertions.assertEquals(aPassword, aUser.getPassword());
    }

    @Test
    void givenAValidValues_whenCallWith_thenANewUserShouldBeCreated() {
        final var aUserId = new UserId(IdentifierUtils.generateNewUUID());
        final var aCustomerId = new CustomerId(IdentifierUtils.generateNewUUID());
        final var aName = new Name("John", "Doe");
        final var aEmail = new Email("testes.ts@tess.com");

        final var aUser = User.with(aUserId, aCustomerId, aName, aEmail);

        Assertions.assertNotNull(aUser);
        Assertions.assertEquals(aUserId, aUser.getUserId());
        Assertions.assertEquals(aCustomerId, aUser.getCustomerId());
        Assertions.assertEquals(aName, aUser.getName());
        Assertions.assertEquals(aEmail, aUser.getEmail());
    }
}
