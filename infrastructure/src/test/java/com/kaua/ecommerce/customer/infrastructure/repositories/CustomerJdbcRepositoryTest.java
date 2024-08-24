package com.kaua.ecommerce.customer.infrastructure.repositories;

import com.kaua.ecommerce.customer.AbstractRepositoryTest;
import com.kaua.ecommerce.customer.domain.Fixture;
import com.kaua.ecommerce.customer.domain.customer.Customer;
import com.kaua.ecommerce.customer.domain.customer.idp.UserId;
import com.kaua.ecommerce.customer.domain.person.Document;
import com.kaua.ecommerce.customer.domain.person.Email;
import com.kaua.ecommerce.customer.domain.person.Name;
import com.kaua.ecommerce.lib.domain.utils.IdentifierUtils;
import com.kaua.ecommerce.lib.infrastructure.exceptions.ConflictException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class CustomerJdbcRepositoryTest extends AbstractRepositoryTest {

    @Test
    void testAssertDependencies() {
        Assertions.assertNotNull(customerRepository());
    }

    @Test
    void givenAValidNewCustomer_whenCallSave_thenCustomerIsPersisted() {
        Assertions.assertEquals(0, countCustomers());

        final var aCustomerId = customerRepository().nextId();
        final var aUserId = new UserId(IdentifierUtils.generateNewUUID());
        final var aName = new Name("Test", "Test");
        final var aEmail = new Email("testes.tess@teste.com");

        final var aCustomer = Customer.newCustomer(aCustomerId, aUserId, aEmail, aName);

        final var aActualResponse = this.customerRepository().save(aCustomer);

        Assertions.assertEquals(1, countCustomers());
        Assertions.assertEquals(1, aActualResponse.getVersion());
        Assertions.assertEquals(aCustomerId, aActualResponse.getId());
        Assertions.assertEquals(aUserId, aActualResponse.getUserId());
        Assertions.assertEquals(aName, aActualResponse.getName());
        Assertions.assertEquals(aEmail, aActualResponse.getEmail());
        Assertions.assertTrue(aActualResponse.getDocument().isEmpty());
        Assertions.assertNotNull(aActualResponse.getCreatedAt());
        Assertions.assertNotNull(aActualResponse.getUpdatedAt());
    }

    @Test
    void givenAnNonExistsEmail_whenCallExistsByEmail_thenReturnFalse() {
        Assertions.assertEquals(0, countCustomers());
        final var aEmail = "testes.tess@teste.com";

        final var aActualResponse = this.customerRepository().existsByEmail(aEmail);

        Assertions.assertFalse(aActualResponse);
    }

    @Test
    void givenAnExistsEmail_whenCallExistsByEmail_thenReturnTrue() {
        Assertions.assertEquals(0, countCustomers());

        final var aCustomerId = customerRepository().nextId();
        final var aUserId = new UserId(IdentifierUtils.generateNewUUID());
        final var aName = new Name("Test", "Test");
        final var aEmail = new Email("testes.tess@teste.com");

        final var aCustomer = Customer.newCustomer(aCustomerId, aUserId, aEmail, aName);
        this.customerRepository().save(aCustomer);

        Assertions.assertEquals(1, countCustomers());

        final var aActualResponse = this.customerRepository().existsByEmail(aEmail.value());

        Assertions.assertTrue(aActualResponse);
    }

    @Test
    void givenAnNonExistsDocument_whenCallExistsByDocument_thenReturnFalse() {
        Assertions.assertEquals(0, countCustomers());
        final var aDocument = "12345678901";

        final var aActualResponse = this.customerRepository().existsByDocument(aDocument);

        Assertions.assertFalse(aActualResponse);
    }

    @Test
    void givenAnExistsDocument_whenCallExistsByDocument_thenReturnTrue() {
        Assertions.assertEquals(0, countCustomers());

        final var aCustomer = Fixture.Customers.newCustomer();
        aCustomer.updateDocument(Document.create("175.105.270-23", "CPF"));

        this.customerRepository().save(aCustomer);

        Assertions.assertEquals(1, countCustomers());

        final var aActualResponse = this.customerRepository().existsByDocument("17510527023");

        Assertions.assertTrue(aActualResponse);
    }

    @Test
    void givenAValidValuesToUpdate_whenCallSaveWithVersionIsMoreThanZero_thenCustomerIsUpdated() {
        Assertions.assertEquals(0, countCustomers());

        final var aCustomer = Fixture.Customers.newCustomer();
        this.customerRepository().save(aCustomer);

        Assertions.assertEquals(1, countCustomers());

        final var aCustomerSaved = this.customerRepository().customerOfId(aCustomer.getId()).get();

        final var aCustomerUpdated = aCustomerSaved.updateDocument(Document.create("175.105.270-23", "CPF"));

        final var aActualResponse = this.customerRepository().save(aCustomerUpdated);

        Assertions.assertEquals(1, countCustomers());
        Assertions.assertEquals(2, aActualResponse.getVersion());
        Assertions.assertEquals(aCustomer.getId(), aActualResponse.getId());
        Assertions.assertEquals(aCustomer.getUserId(), aActualResponse.getUserId());
        Assertions.assertEquals(aCustomer.getName(), aActualResponse.getName());
        Assertions.assertEquals(aCustomer.getEmail(), aActualResponse.getEmail());
        Assertions.assertEquals(aCustomerUpdated.getDocument().get(), aActualResponse.getDocument().get());
        Assertions.assertNotNull(aActualResponse.getCreatedAt());
        Assertions.assertNotNull(aActualResponse.getUpdatedAt());
    }

    @Test
    void givenAValidValuesToUpdate_whenCallSaveButVersionIsNotMatch_thenThrowIllegalArgumentException() {
        Assertions.assertEquals(0, countCustomers());

        final var aCustomer = Fixture.Customers.newCustomer();
        this.customerRepository().save(aCustomer);

        final var expectedErrorMessage = "Customer version does not match, customer was updated by another user";

        Assertions.assertEquals(1, countCustomers());

        final var aCustomerSaved = this.customerRepository().customerOfId(aCustomer.getId()).get();

        final var aDocument = Document.create("175.105.270-23", "CPF");
        final var aCustomerUpdated = aCustomerSaved.updateDocument(aDocument);

        aCustomerUpdated.setVersion(2);

        final var aException = Assertions.assertThrows(ConflictException.class,
                () -> this.customerRepository().save(aCustomerUpdated));

        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());
    }

    @Test
    void givenAValidCustomerId_whenCallCustomerOfId_thenReturnCustomer() {
        Assertions.assertEquals(0, countCustomers());

        final var aCustomer = Fixture.Customers.newCustomer();
        this.customerRepository().save(aCustomer);

        Assertions.assertEquals(1, countCustomers());

        final var aActualResponse = this.customerRepository().customerOfId(aCustomer.getId()).get();

        Assertions.assertEquals(aCustomer.getId(), aActualResponse.getId());
        Assertions.assertEquals(aCustomer.getUserId(), aActualResponse.getUserId());
        Assertions.assertEquals(aCustomer.getName(), aActualResponse.getName());
        Assertions.assertEquals(aCustomer.getEmail(), aActualResponse.getEmail());
        Assertions.assertTrue(aActualResponse.getDocument().isEmpty());
        Assertions.assertEquals(aCustomer.getCreatedAt(), aActualResponse.getCreatedAt());
        Assertions.assertEquals(aCustomer.getUpdatedAt(), aActualResponse.getUpdatedAt());
    }

    @Test
    void givenAValidCustomerIdWithAllValues_whenCallCustomerOfId_thenReturnCustomer() {
        Assertions.assertEquals(0, countCustomers());

        final var aCustomer = Fixture.Customers.newCustomer();
        aCustomer.updateDocument(Document.create("175.105.270-23", "CPF"));
        this.customerRepository().save(aCustomer);

        Assertions.assertEquals(1, countCustomers());

        final var aActualResponse = this.customerRepository().customerOfId(aCustomer.getId()).get();

        Assertions.assertEquals(aCustomer.getId(), aActualResponse.getId());
        Assertions.assertEquals(aCustomer.getUserId(), aActualResponse.getUserId());
        Assertions.assertEquals(aCustomer.getName(), aActualResponse.getName());
        Assertions.assertEquals(aCustomer.getEmail(), aActualResponse.getEmail());
        Assertions.assertEquals(aCustomer.getDocument().get(), aActualResponse.getDocument().get());
        Assertions.assertEquals(aCustomer.getCreatedAt(), aActualResponse.getCreatedAt());
        Assertions.assertEquals(aCustomer.getUpdatedAt(), aActualResponse.getUpdatedAt());
    }
}
