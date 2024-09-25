package com.kaua.ecommerce.customer.infrastructure.repositories;

import com.kaua.ecommerce.customer.AbstractRepositoryTest;
import com.kaua.ecommerce.customer.domain.Fixture;
import com.kaua.ecommerce.customer.domain.address.Address;
import com.kaua.ecommerce.customer.domain.address.Title;
import com.kaua.ecommerce.customer.domain.customer.CustomerId;
import com.kaua.ecommerce.lib.domain.pagination.SearchQuery;
import com.kaua.ecommerce.lib.domain.utils.IdentifierUtils;
import com.kaua.ecommerce.lib.infrastructure.exceptions.ConflictException;
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

    @Test
    void givenAValidAddressId_whenCallAddressOfId_thenReturnAddress() {
        Assertions.assertEquals(0, countAddresses());

        final var aCustomerId = new CustomerId(IdentifierUtils.generateNewUUID());

        final var aAddress = Fixture.Addresses.newAddressWithComplement(
                aCustomerId,
                true
        );

        this.addressRepository().save(aAddress);

        Assertions.assertEquals(1, countAddresses());

        final var aActualResponse = this.addressRepository().addressOfId(aAddress.getId());

        Assertions.assertTrue(aActualResponse.isPresent());
        Assertions.assertEquals(aAddress.getId().value(), aActualResponse.get().getId().value());
        Assertions.assertEquals(aAddress.getTitle().value(), aActualResponse.get().getTitle().value());
        Assertions.assertEquals(aCustomerId, aActualResponse.get().getCustomerId());
        Assertions.assertEquals(aAddress.getZipCode(), aActualResponse.get().getZipCode());
        Assertions.assertEquals(aAddress.getStreet(), aActualResponse.get().getStreet());
        Assertions.assertEquals(aAddress.getNumber(), aActualResponse.get().getNumber());
        Assertions.assertEquals(aAddress.getCity(), aActualResponse.get().getCity());
        Assertions.assertEquals(aAddress.getDistrict(), aActualResponse.get().getDistrict());
        Assertions.assertEquals(aAddress.getCountry(), aActualResponse.get().getCountry());
        Assertions.assertEquals(aAddress.getState(), aActualResponse.get().getState());
        Assertions.assertEquals(aAddress.getComplement().get(), aActualResponse.get().getComplement().get());
        Assertions.assertEquals(aAddress.isDefault(), aActualResponse.get().isDefault());
        Assertions.assertEquals(aAddress.getCreatedAt(), aActualResponse.get().getCreatedAt());
        Assertions.assertEquals(aAddress.getUpdatedAt(), aActualResponse.get().getUpdatedAt());
    }

    @Test
    void givenAValidCustomerId_whenCallAddressByCustomerIdAndIsDefaultTrue_thenReturnAddress() {
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

        final var aActualResponse = this.addressRepository().addressByCustomerIdAndIsDefaultTrue(aAddress.getCustomerId());

        Assertions.assertTrue(aActualResponse.isPresent());
        Assertions.assertEquals(aAddress.getId().value(), aActualResponse.get().getId().value());
        Assertions.assertEquals(aAddress.getTitle().value(), aActualResponse.get().getTitle().value());
        Assertions.assertEquals(aCustomerId, aActualResponse.get().getCustomerId());
        Assertions.assertEquals(aAddress.getZipCode(), aActualResponse.get().getZipCode());
        Assertions.assertEquals(aAddress.getStreet(), aActualResponse.get().getStreet());
        Assertions.assertEquals(aAddress.getNumber(), aActualResponse.get().getNumber());
        Assertions.assertEquals(aAddress.getCity(), aActualResponse.get().getCity());
        Assertions.assertEquals(aAddress.getDistrict(), aActualResponse.get().getDistrict());
        Assertions.assertEquals(aAddress.getCountry(), aActualResponse.get().getCountry());
        Assertions.assertEquals(aAddress.getState(), aActualResponse.get().getState());
        Assertions.assertEquals(aAddress.getComplement().get(), aActualResponse.get().getComplement().get());
        Assertions.assertEquals(aAddress.isDefault(), aActualResponse.get().isDefault());
        Assertions.assertEquals(aAddress.getCreatedAt(), aActualResponse.get().getCreatedAt());
        Assertions.assertEquals(aAddress.getUpdatedAt(), aActualResponse.get().getUpdatedAt());
    }

    @Test
    void givenAValidAddressToUpdate_whenCallSave_thenAddressIsUpdated() {
        Assertions.assertEquals(0, countAddresses());

        final var aCustomerId = new CustomerId(IdentifierUtils.generateNewUUID());

        final var aAddress = Fixture.Addresses.newAddressWithComplement(
                aCustomerId,
                true
        );

        this.addressRepository().save(aAddress);

        Assertions.assertEquals(1, countAddresses());

        final var aUpdatedAddress = aAddress.updateIsDefault(false);

        final var aActualResponse = this.addressRepository().save(aUpdatedAddress);

        Assertions.assertEquals(1, countAddresses());
        Assertions.assertEquals(2, aActualResponse.getVersion());
        Assertions.assertEquals(aUpdatedAddress.getId().value(), aActualResponse.getId().value());
        Assertions.assertEquals(aCustomerId, aActualResponse.getCustomerId());
        Assertions.assertEquals(aUpdatedAddress.getStreet(), aActualResponse.getStreet());
        Assertions.assertEquals(aUpdatedAddress.getNumber(), aActualResponse.getNumber());
        Assertions.assertEquals(aUpdatedAddress.getComplement().get(), aActualResponse.getComplement().get());
        Assertions.assertEquals(aUpdatedAddress.getCity(), aActualResponse.getCity());
        Assertions.assertEquals(aUpdatedAddress.getDistrict(), aActualResponse.getDistrict());
        Assertions.assertEquals(aUpdatedAddress.getCountry(), aActualResponse.getCountry());
        Assertions.assertEquals(aUpdatedAddress.getState(), aActualResponse.getState());
        Assertions.assertEquals(aUpdatedAddress.getZipCode(), aActualResponse.getZipCode());
        Assertions.assertNotNull(aActualResponse.getCreatedAt());
        Assertions.assertNotNull(aActualResponse.getUpdatedAt());
    }

    @Test
    void givenAValidAddressToUpdate_whenCallSaveButVersionIsNotMatch_thenThrowConflictException() {
        Assertions.assertEquals(0, countAddresses());

        final var aCustomerId = new CustomerId(IdentifierUtils.generateNewUUID());

        final var aAddress = Fixture.Addresses.newAddressWithComplement(
                aCustomerId,
                true
        );
        this.addressRepository().save(aAddress);

        final var expectedErrorMessage = "Address version does not match, address was updated by another user";

        Assertions.assertEquals(1, countAddresses());

        final var aAddressSaved = this.addressRepository().addressOfId(aAddress.getId()).get();

        final var aUpdatedAddress = aAddressSaved.updateIsDefault(false);

        aUpdatedAddress.setVersion(2);

        final var aAddressRepository = addressRepository();
        final var aException = Assertions.assertThrows(ConflictException.class, () -> {
            aAddressRepository.save(aUpdatedAddress);
        });

        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());
    }

    @Test
    void givenAValidValues_whenCallAddressesByCustomerId_thenReturnPaginatedAddresses() {
        Assertions.assertEquals(0, countAddresses());

        final var aCustomerId = new CustomerId(IdentifierUtils.generateNewUUID());

        final var aAddressOne = Fixture.Addresses.newAddressWithComplement(
                aCustomerId,
                true
        );
        final var aAddressTwo = Fixture.Addresses.newAddressWithComplement(
                aCustomerId,
                false
        );

        this.addressRepository().save(aAddressOne);
        this.addressRepository().save(aAddressTwo);
        this.addressRepository().save(Fixture.Addresses.newAddressWithComplement(
                new CustomerId(IdentifierUtils.generateNewUUID()),
                false
        ));

        final var aPage = 0;
        final var aPerPage = 10;
        final var aTerms = "";
        final var aDirection = "asc";
        final var aSort = "created_at";
        final var aTotalPages = 1;
        final var aTotalItems = 2;

        final var aSearchQuery = new SearchQuery(
                aPage,
                aPerPage,
                aTerms,
                aSort,
                aDirection
        );

        Assertions.assertEquals(3, countAddresses());

        final var aActualResponse = this.addressRepository().addressesByCustomerId(aCustomerId, aSearchQuery);

        Assertions.assertEquals(aTotalPages, aActualResponse.metadata().totalPages());
        Assertions.assertEquals(aTotalItems, aActualResponse.metadata().totalItems());
        Assertions.assertEquals(aPage, aActualResponse.metadata().currentPage());
        Assertions.assertEquals(aPerPage, aActualResponse.metadata().perPage());
        Assertions.assertEquals(aAddressOne.getId().value(), aActualResponse.items().get(0).getId().value());
        Assertions.assertEquals(aAddressTwo.getId().value(), aActualResponse.items().get(1).getId().value());
    }

    @Test
    void givenAValidValuesButNoHasData_whenCallAddressesByCustomerId_thenReturnEmptyPaginated() {
        Assertions.assertEquals(0, countAddresses());

        final var aCustomerId = new CustomerId(IdentifierUtils.generateNewUUID());

        final var aPage = 0;
        final var aPerPage = 10;
        final var aTerms = "";
        final var aDirection = "asc";
        final var aSort = "created_at";
        final var aTotalPages = 0;
        final var aTotalItems = 0;

        final var aSearchQuery = new SearchQuery(
                aPage,
                aPerPage,
                aTerms,
                aSort,
                aDirection
        );

        Assertions.assertEquals(0, countAddresses());

        final var aActualResponse = this.addressRepository().addressesByCustomerId(aCustomerId, aSearchQuery);

        Assertions.assertEquals(aTotalPages, aActualResponse.metadata().totalPages());
        Assertions.assertEquals(aTotalItems, aActualResponse.metadata().totalItems());
        Assertions.assertEquals(aPage, aActualResponse.metadata().currentPage());
        Assertions.assertEquals(aPerPage, aActualResponse.metadata().perPage());
        Assertions.assertTrue(aActualResponse.items().isEmpty());
    }

    @Test
    void givenAValidValuesWithTerm_whenCallAddressesByCustomerId_thenReturnPaginatedAddresses() {
        Assertions.assertEquals(0, countAddresses());

        final var aCustomerId = new CustomerId(IdentifierUtils.generateNewUUID());

        final var aAddressOne = Fixture.Addresses.newAddressWithTitle(
                aCustomerId,
                "Work",
                true
        );
        final var aAddressTwo = Fixture.Addresses.newAddressWithComplement(
                aCustomerId,
                false
        );

        this.addressRepository().save(aAddressOne);
        this.addressRepository().save(aAddressTwo);
        this.addressRepository().save(Fixture.Addresses.newAddressWithComplement(
                new CustomerId(IdentifierUtils.generateNewUUID()),
                false
        ));

        final var aPage = 0;
        final var aPerPage = 10;
        final var aTerms = "w";
        final var aDirection = "asc";
        final var aSort = "created_at";
        final var aTotalPages = 1;
        final var aTotalItems = 1;

        final var aSearchQuery = new SearchQuery(
                aPage,
                aPerPage,
                aTerms,
                aSort,
                aDirection
        );

        Assertions.assertEquals(3, countAddresses());

        final var aActualResponse = this.addressRepository().addressesByCustomerId(aCustomerId, aSearchQuery);

        Assertions.assertEquals(aTotalPages, aActualResponse.metadata().totalPages());
        Assertions.assertEquals(aTotalItems, aActualResponse.metadata().totalItems());
        Assertions.assertEquals(aPage, aActualResponse.metadata().currentPage());
        Assertions.assertEquals(aPerPage, aActualResponse.metadata().perPage());
        Assertions.assertEquals(aAddressOne.getId().value(), aActualResponse.items().get(0).getId().value());
    }

    @Test
    void givenAValidAddressId_whenCallDelete_thenAddressIsDeleted() {
        Assertions.assertEquals(0, countAddresses());

        final var aCustomerId = new CustomerId(IdentifierUtils.generateNewUUID());

        final var aAddress = Fixture.Addresses.newAddressWithComplement(
                aCustomerId,
                true
        );

        this.addressRepository().save(aAddress);

        Assertions.assertEquals(1, countAddresses());

        this.addressRepository().delete(aAddress.getId());

        Assertions.assertEquals(0, countAddresses());
    }
}