package com.kaua.ecommerce.customer.domain.address;

import com.kaua.ecommerce.customer.domain.UnitTest;
import com.kaua.ecommerce.customer.domain.customer.CustomerId;
import com.kaua.ecommerce.lib.domain.utils.IdentifierUtils;
import com.kaua.ecommerce.lib.domain.utils.InstantUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class AddressTest extends UnitTest {

    @Test
    void givenAValidValues_whenCallNewAddress_thenANewAddressShouldBeCreated() {
        final var aTitle = new Title("Home");
        final var aCustomerId = new CustomerId(IdentifierUtils.generateNewUUID());
        final var aZipCode = "12345678";
        final var aNumber = "123";
        final var aStreet = "Main Street";
        final var aCity = "New York";
        final var aDistrict = "Manhattan";
        final var aCountry = "USA";
        final var aState = "NY";
        final var aComplement = "Near the park";
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

        Assertions.assertNotNull(aAddress);
        Assertions.assertNotNull(aAddress.getId());
        Assertions.assertEquals(aTitle, aAddress.getTitle());
        Assertions.assertEquals(aCustomerId, aAddress.getCustomerId());
        Assertions.assertEquals(aZipCode, aAddress.getZipCode());
        Assertions.assertEquals(aNumber, aAddress.getNumber());
        Assertions.assertEquals(aStreet, aAddress.getStreet());
        Assertions.assertEquals(aCity, aAddress.getCity());
        Assertions.assertEquals(aDistrict, aAddress.getDistrict());
        Assertions.assertEquals(aCountry, aAddress.getCountry());
        Assertions.assertEquals(aState, aAddress.getState());
        Assertions.assertEquals(aComplement, aAddress.getComplement().get());
        Assertions.assertEquals(aIsDefault, aAddress.isDefault());
        Assertions.assertNotNull(aAddress.getCreatedAt());
        Assertions.assertNotNull(aAddress.getUpdatedAt());
        Assertions.assertEquals(0L, aAddress.getVersion());
    }

    @Test
    void givenAValidValuesWithoutComplement_whenCallNewAddress_thenANewAddressShouldBeCreatedWithoutComplement() {
        final var aTitle = new Title("Home");
        final var aCustomerId = new CustomerId(IdentifierUtils.generateNewUUID());
        final var aZipCode = "12345678";
        final var aNumber = "123";
        final var aStreet = "Main Street";
        final var aCity = "New York";
        final var aDistrict = "Manhattan";
        final var aCountry = "USA";
        final var aState = "NY";
        final String aComplement = null;
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

        Assertions.assertNotNull(aAddress);
        Assertions.assertNotNull(aAddress.getId());
        Assertions.assertEquals(aTitle, aAddress.getTitle());
        Assertions.assertEquals(aCustomerId, aAddress.getCustomerId());
        Assertions.assertEquals(aZipCode, aAddress.getZipCode());
        Assertions.assertEquals(aNumber, aAddress.getNumber());
        Assertions.assertEquals(aStreet, aAddress.getStreet());
        Assertions.assertEquals(aCity, aAddress.getCity());
        Assertions.assertEquals(aDistrict, aAddress.getDistrict());
        Assertions.assertEquals(aCountry, aAddress.getCountry());
        Assertions.assertEquals(aState, aAddress.getState());
        Assertions.assertTrue(aAddress.getComplement().isEmpty());
        Assertions.assertEquals(aIsDefault, aAddress.isDefault());
        Assertions.assertNotNull(aAddress.getCreatedAt());
        Assertions.assertNotNull(aAddress.getUpdatedAt());
        Assertions.assertEquals(0L, aAddress.getVersion());
    }

    @Test
    void givenAValidValues_whenCallWith_thenReturnAnAddressObject() {
        final var aAddressId = new AddressId(IdentifierUtils.generateNewUUID());
        final var aVersion = 0L;
        final var aTitle = new Title("Home");
        final var aCustomerId = new CustomerId(IdentifierUtils.generateNewUUID());
        final var aZipCode = "12345678";
        final var aNumber = "123";
        final var aStreet = "Main Street";
        final var aCity = "New York";
        final var aDistrict = "Manhattan";
        final var aCountry = "USA";
        final var aState = "NY";
        final var aComplement = "Near the park";
        final var aIsDefault = true;
        final var aNow = InstantUtils.now();

        final var aAddress = Address.with(
                aAddressId,
                aVersion,
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
                aIsDefault,
                aNow,
                aNow
        );

        Assertions.assertNotNull(aAddress);
        Assertions.assertEquals(aAddressId, aAddress.getId());
        Assertions.assertEquals(aVersion, aAddress.getVersion());
        Assertions.assertEquals(aTitle, aAddress.getTitle());
        Assertions.assertEquals(aCustomerId, aAddress.getCustomerId());
        Assertions.assertEquals(aZipCode, aAddress.getZipCode());
        Assertions.assertEquals(aNumber, aAddress.getNumber());
        Assertions.assertEquals(aStreet, aAddress.getStreet());
        Assertions.assertEquals(aCity, aAddress.getCity());
        Assertions.assertEquals(aDistrict, aAddress.getDistrict());
        Assertions.assertEquals(aCountry, aAddress.getCountry());
        Assertions.assertEquals(aState, aAddress.getState());
        Assertions.assertEquals(aComplement, aAddress.getComplement().get());
        Assertions.assertEquals(aIsDefault, aAddress.isDefault());
        Assertions.assertEquals(aNow, aAddress.getCreatedAt());
        Assertions.assertEquals(aNow, aAddress.getUpdatedAt());
    }

    @Test
    void testCallToStringAddress() {
        final var aTitle = new Title("Home");
        final var aCustomerId = new CustomerId(IdentifierUtils.generateNewUUID());
        final var aZipCode = "12345678";
        final var aNumber = "123";
        final var aStreet = "Main Street";
        final var aCity = "New York";
        final var aDistrict = "Manhattan";
        final var aCountry = "USA";
        final var aState = "NY";
        final var aComplement = "Near the park";
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

        final var aToString = aAddress.toString();

        Assertions.assertNotNull(aToString);
        Assertions.assertTrue(aToString.contains("id="));
        Assertions.assertTrue(aToString.contains("version="));
        Assertions.assertTrue(aToString.contains("title="));
        Assertions.assertTrue(aToString.contains("customerId="));
        Assertions.assertTrue(aToString.contains("zipCode="));
        Assertions.assertTrue(aToString.contains("number="));
        Assertions.assertTrue(aToString.contains("street="));
        Assertions.assertTrue(aToString.contains("city="));
        Assertions.assertTrue(aToString.contains("district="));
        Assertions.assertTrue(aToString.contains("country="));
        Assertions.assertTrue(aToString.contains("state="));
        Assertions.assertTrue(aToString.contains("complement="));
        Assertions.assertTrue(aToString.contains("isDefault="));
        Assertions.assertTrue(aToString.contains("createdAt="));
        Assertions.assertTrue(aToString.contains("updatedAt="));
        Assertions.assertTrue(aToString.contains("version="));
    }
}
