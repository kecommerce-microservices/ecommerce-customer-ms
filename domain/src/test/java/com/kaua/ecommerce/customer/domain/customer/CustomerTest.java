package com.kaua.ecommerce.customer.domain.customer;

import com.kaua.ecommerce.customer.domain.UnitTest;
import com.kaua.ecommerce.customer.domain.customer.idp.UserId;
import com.kaua.ecommerce.customer.domain.person.DocumentFactory;
import com.kaua.ecommerce.customer.domain.person.Email;
import com.kaua.ecommerce.customer.domain.person.Name;
import com.kaua.ecommerce.lib.domain.utils.IdentifierUtils;
import com.kaua.ecommerce.lib.domain.utils.InstantUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class CustomerTest extends UnitTest {

    @Test
    void givenAValidValues_whenCallNewCustomer_thenANewCustomerShouldBeCreated() {
        final var aCustomerId = new CustomerId(IdentifierUtils.generateNewUUID());
        final var aUserId = new UserId(IdentifierUtils.generateNewUUID());
        final var aName = new Name("John", "Doe");
        final var aEmail = new Email("testes@tess.com");

        final var aCustomer = Customer.newCustomer(aCustomerId, aUserId, aEmail, aName);

        Assertions.assertNotNull(aCustomer);
        Assertions.assertEquals(aCustomerId, aCustomer.getId());
        Assertions.assertEquals(aUserId, aCustomer.getUserId());
        Assertions.assertEquals(aName, aCustomer.getName());
        Assertions.assertEquals(aEmail, aCustomer.getEmail());
        Assertions.assertTrue(aCustomer.getDocument().isEmpty());
        Assertions.assertNotNull(aCustomer.getCreatedAt());
        Assertions.assertNotNull(aCustomer.getUpdatedAt());
    }

    @Test
    void givenAValidValues_whenCallWith_thenANewCustomerShouldBeCreated() {
        final var aCustomerId = new CustomerId(IdentifierUtils.generateNewUUID());
        final var aVersion = 1L;
        final var aUserId = new UserId(IdentifierUtils.generateNewUUID());
        final var aName = new Name("John", "Doe");
        final var aEmail = new Email("testes@sess.com");
        final var aDocument = DocumentFactory.create("47999381004", "cpf");
        final var aNow = InstantUtils.now();

        final var aCustomer = Customer.with(
                aCustomerId,
                aVersion,
                aUserId,
                aEmail,
                aName,
                aDocument,
                aNow,
                aNow
        );

        Assertions.assertNotNull(aCustomer);
        Assertions.assertEquals(aCustomerId, aCustomer.getId());
        Assertions.assertEquals(aUserId, aCustomer.getUserId());
        Assertions.assertEquals(aName, aCustomer.getName());
        Assertions.assertEquals(aEmail, aCustomer.getEmail());
        Assertions.assertEquals(aDocument, aCustomer.getDocument().get());
        Assertions.assertEquals(aNow, aCustomer.getCreatedAt());
        Assertions.assertEquals(aNow, aCustomer.getUpdatedAt());
    }

    @Test
    void testCallCustomerToString() {
        final var aCustomerId = new CustomerId(IdentifierUtils.generateNewUUID());
        final var aVersion = 1L;
        final var aUserId = new UserId(IdentifierUtils.generateNewUUID());
        final var aName = new Name("John", "Doe");
        final var aEmail = new Email("testes@sess.com");
        final var aDocument = DocumentFactory.create("47999381004", "cpf");
        final var aNow = InstantUtils.now();

        final var aCustomer = Customer.with(
                aCustomerId,
                aVersion,
                aUserId,
                aEmail,
                aName,
                aDocument,
                aNow,
                aNow
        );

        final var aCustomerString = aCustomer.toString();

        Assertions.assertNotNull(aCustomerString);
        Assertions.assertTrue(aCustomerString.contains("customerId=" + aCustomerId.value()));
        Assertions.assertTrue(aCustomerString.contains("userId=" + aUserId.value()));
        Assertions.assertTrue(aCustomerString.contains("name=" + aName.fullName()));
        Assertions.assertTrue(aCustomerString.contains("email=" + aEmail.value()));
        Assertions.assertTrue(aCustomerString.contains("document=" + aDocument.type()));
        Assertions.assertTrue(aCustomerString.contains("createdAt=" + aNow));
        Assertions.assertTrue(aCustomerString.contains("updatedAt=" + aNow));
        Assertions.assertTrue(aCustomerString.contains("version=" + aVersion));
    }
}
