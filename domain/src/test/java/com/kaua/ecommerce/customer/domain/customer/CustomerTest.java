package com.kaua.ecommerce.customer.domain.customer;

import com.kaua.ecommerce.customer.domain.UnitTest;
import com.kaua.ecommerce.customer.domain.customer.idp.UserId;
import com.kaua.ecommerce.customer.domain.person.*;
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
        final var aTelephone = new Telephone("5511999999999");
        final var aNow = InstantUtils.now();

        final var aCustomer = Customer.with(
                aCustomerId,
                aVersion,
                aUserId,
                aEmail,
                aName,
                aDocument,
                aTelephone,
                aNow,
                aNow
        );

        Assertions.assertNotNull(aCustomer);
        Assertions.assertEquals(aCustomerId, aCustomer.getId());
        Assertions.assertEquals(aUserId, aCustomer.getUserId());
        Assertions.assertEquals(aName, aCustomer.getName());
        Assertions.assertEquals(aEmail, aCustomer.getEmail());
        Assertions.assertEquals(aDocument, aCustomer.getDocument().get());
        Assertions.assertEquals(aTelephone, aCustomer.getTelephone().get());
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
        final var aTelephone = new Telephone("5511999999999");
        final var aNow = InstantUtils.now();

        final var aCustomer = Customer.with(
                aCustomerId,
                aVersion,
                aUserId,
                aEmail,
                aName,
                aDocument,
                aTelephone,
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
        Assertions.assertTrue(aCustomerString.contains("telephone=" + aTelephone.value()));
        Assertions.assertTrue(aCustomerString.contains("createdAt=" + aNow));
        Assertions.assertTrue(aCustomerString.contains("updatedAt=" + aNow));
        Assertions.assertTrue(aCustomerString.contains("version=" + aVersion));
    }

    @Test
    void givenAValidDocument_whenCallUpdateDocument_thenDocumentShouldBeUpdated() {
        final var aCustomerId = new CustomerId(IdentifierUtils.generateNewUUID());
        final var aUserId = new UserId(IdentifierUtils.generateNewUUID());
        final var aName = new Name("John", "Doe");
        final var aEmail = new Email("testes@sess.com");
        final var aDocument = Document.create("47999381004", "cpf");

        final var aCustomer = Customer.newCustomer(aCustomerId, aUserId, aEmail, aName);
        final var aCustomerUpdatedAt = aCustomer.getUpdatedAt();

        final var aCustomerUpdated = aCustomer.updateDocument(aDocument);

        Assertions.assertNotNull(aCustomerUpdated);
        Assertions.assertEquals(aCustomerId, aCustomerUpdated.getId());
        Assertions.assertEquals(aUserId, aCustomerUpdated.getUserId());
        Assertions.assertEquals(aName, aCustomerUpdated.getName());
        Assertions.assertEquals(aEmail, aCustomerUpdated.getEmail());
        Assertions.assertEquals(aDocument, aCustomerUpdated.getDocument().get());
        Assertions.assertEquals(aCustomerUpdatedAt, aCustomer.getCreatedAt());
        Assertions.assertTrue(aCustomerUpdatedAt.isBefore(aCustomerUpdated.getUpdatedAt()));
    }

    @Test
    void givenAValidTelephone_whenCallUpdateTelephone_thenTelephoneShouldBeUpdated() {
        final var aCustomerId = new CustomerId(IdentifierUtils.generateNewUUID());
        final var aUserId = new UserId(IdentifierUtils.generateNewUUID());
        final var aName = new Name("John", "Doe");
        final var aEmail = new Email("testes@sess.com");
        final var aTelephone = new Telephone("5511999999999");

        final var aCustomer = Customer.newCustomer(aCustomerId, aUserId, aEmail, aName);
        final var aCustomerUpdatedAt = aCustomer.getUpdatedAt();

        final var aCustomerUpdated = aCustomer.updateTelephone(aTelephone);

        Assertions.assertNotNull(aCustomerUpdated);
        Assertions.assertEquals(aCustomerId, aCustomerUpdated.getId());
        Assertions.assertEquals(aUserId, aCustomerUpdated.getUserId());
        Assertions.assertEquals(aName, aCustomerUpdated.getName());
        Assertions.assertEquals(aEmail, aCustomerUpdated.getEmail());
        Assertions.assertTrue(aCustomerUpdated.getDocument().isEmpty());
        Assertions.assertEquals(aTelephone, aCustomerUpdated.getTelephone().get());
        Assertions.assertEquals(aCustomerUpdatedAt, aCustomer.getCreatedAt());
        Assertions.assertTrue(aCustomerUpdatedAt.isBefore(aCustomerUpdated.getUpdatedAt()));
    }
}
