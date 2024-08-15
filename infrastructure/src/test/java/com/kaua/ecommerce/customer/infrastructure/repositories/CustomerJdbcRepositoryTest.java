package com.kaua.ecommerce.customer.infrastructure.repositories;

import com.kaua.ecommerce.customer.AbstractRepositoryTest;
import com.kaua.ecommerce.customer.domain.customer.Customer;
import com.kaua.ecommerce.customer.domain.customer.idp.UserId;
import com.kaua.ecommerce.customer.domain.person.Email;
import com.kaua.ecommerce.customer.domain.person.Name;
import com.kaua.ecommerce.lib.domain.utils.IdentifierUtils;
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
        Assertions.assertEquals(0, aActualResponse.getVersion());
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
}
