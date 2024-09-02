package com.kaua.ecommerce.customer.infrastructure.repositories;

import com.kaua.ecommerce.customer.AbstractRepositoryTest;
import com.kaua.ecommerce.customer.domain.Fixture;
import com.kaua.ecommerce.customer.domain.address.Address;
import com.kaua.ecommerce.customer.domain.address.Title;
import com.kaua.ecommerce.customer.domain.customer.CustomerId;
import com.kaua.ecommerce.lib.domain.utils.IdentifierUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class AddressJdbcRepositoryTest extends AbstractRepositoryTest {

    @Test
    void testAssertDependencies() {
        Assertions.assertNotNull(addressRepository());
    }

    @Test
    void givenAValidNewAddress_whenCallSave_thenAddressIsPersisted() {
        Assertions.assertEquals(0, countAddresses());

        final var aTitle = new Title("home");
        final var aCustomerId = new CustomerId(IdentifierUtils.generateNewUUID());
        final var aStreet = "Test";
        final var aNumber = "123";
        final var aComplement = "Test ap";
        final var aCity = "city test";
        final var aDistrict = "district";
        final var aCountry = "country test";
        final var aState = "state test";
        final var aZipCode = "12345678";
        final var aIsDefault = true;

        final var aAddress = Address.newAddress(
                aTitle,
                aCustomerId,
                aZipCode,
                aNumber,
                aStreet,
                aCity,
                aDistrict,
                aCountry,
                aState,
                aComplement,
                aIsDefault
        );

        final var aActualResponse = this.addressRepository().save(aAddress);

        Assertions.assertEquals(1, countAddresses());
        Assertions.assertEquals(1, aActualResponse.getVersion());
        Assertions.assertEquals(aAddress.getId().value(), aActualResponse.getId().value());
        Assertions.assertEquals(aCustomerId, aActualResponse.getCustomerId());
        Assertions.assertEquals(aStreet, aActualResponse.getStreet());
        Assertions.assertEquals(aNumber, aActualResponse.getNumber());
        Assertions.assertEquals(aComplement, aActualResponse.getComplement().get());
        Assertions.assertEquals(aCity, aActualResponse.getCity());
        Assertions.assertEquals(aDistrict, aActualResponse.getDistrict());
        Assertions.assertEquals(aCountry, aActualResponse.getCountry());
        Assertions.assertEquals(aState, aActualResponse.getState());
        Assertions.assertEquals(aZipCode, aActualResponse.getZipCode());
        Assertions.assertNotNull(aActualResponse.getCreatedAt());
        Assertions.assertNotNull(aActualResponse.getUpdatedAt());
    }

    @Test
    void givenAValidCustomerId_whenCallCountByCustomerId_thenReturnCount() {
        Assertions.assertEquals(0, countAddresses());

        final var aCustomerId = new CustomerId(IdentifierUtils.generateNewUUID());

        final var aAddress = Fixture.Addresses.newAddressWithComplement(
                aCustomerId,
                true
        );

        this.addressRepository().save(aAddress);
        this.addressRepository().save(Fixture.Addresses.newAddressWithComplement(
                aCustomerId,
                false
        ));

        Assertions.assertEquals(2, countAddresses());

        final var aActualResponse = this.addressRepository().countByCustomerId(aAddress.getCustomerId());

        Assertions.assertEquals(2, aActualResponse);
    }

    @Test
    void givenAValidCustomerId_whenCallCountByCustomerId_thenReturnCountIsZero() {
        Assertions.assertEquals(0, countAddresses());

        final var aCustomerId = new CustomerId(IdentifierUtils.generateNewUUID());

        Assertions.assertEquals(0, countAddresses());

        final var aActualResponse = this.addressRepository().countByCustomerId(aCustomerId);

        Assertions.assertEquals(0, aActualResponse);
    }

    @Test
    void givenAValidCustomerId_whenCallExistsByCustomerIdAndIsDefaultTrue_thenReturnTrue() {
        Assertions.assertEquals(0, countAddresses());

        final var aCustomerId = new CustomerId(IdentifierUtils.generateNewUUID());

        final var aAddress = Fixture.Addresses.newAddressWithComplement(
                aCustomerId,
                true
        );

        this.addressRepository().save(aAddress);
        this.addressRepository().save(Fixture.Addresses.newAddressWithComplement(
                aCustomerId,
                false
        ));

        Assertions.assertEquals(2, countAddresses());

        final var aActualResponse = this.addressRepository().existsByCustomerIdAndIsDefaultTrue(aAddress.getCustomerId());

        Assertions.assertTrue(aActualResponse);
    }

    @Test
    void givenAValidCustomerId_whenCallExistsByCustomerIdAndIsDefaultTrue_thenReturnFalse() {
        Assertions.assertEquals(0, countAddresses());

        final var aCustomerId = new CustomerId(IdentifierUtils.generateNewUUID());

        Assertions.assertEquals(0, countAddresses());

        final var aActualResponse = this.addressRepository().existsByCustomerIdAndIsDefaultTrue(aCustomerId);

        Assertions.assertFalse(aActualResponse);
    }
}