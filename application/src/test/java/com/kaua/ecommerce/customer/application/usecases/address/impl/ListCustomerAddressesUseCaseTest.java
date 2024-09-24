package com.kaua.ecommerce.customer.application.usecases.address.impl;

import com.kaua.ecommerce.customer.application.UseCaseTest;
import com.kaua.ecommerce.customer.application.exceptions.UseCaseInputCannotBeNullException;
import com.kaua.ecommerce.customer.application.repositories.AddressRepository;
import com.kaua.ecommerce.customer.application.usecases.address.inputs.ListCustomerAddressesInput;
import com.kaua.ecommerce.customer.application.usecases.address.outputs.ListCustomerAddressesOutput;
import com.kaua.ecommerce.customer.domain.Fixture;
import com.kaua.ecommerce.customer.domain.address.Address;
import com.kaua.ecommerce.customer.domain.customer.CustomerId;
import com.kaua.ecommerce.lib.domain.pagination.Pagination;
import com.kaua.ecommerce.lib.domain.pagination.PaginationMetadata;
import com.kaua.ecommerce.lib.domain.pagination.SearchQuery;
import com.kaua.ecommerce.lib.domain.utils.IdentifierUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.List;

class ListCustomerAddressesUseCaseTest extends UseCaseTest {

    @Mock
    private AddressRepository addressRepository;

    @InjectMocks
    private DefaultListCustomerAddressesUseCase listCustomerAddressesUseCase;

    @Test
    void givenAnInvalidNullInput_whenCallListCustomerAddresses_thenThrowUseCaseInputCannotBeNullException() {
        final var expectedErrorMessage = "Input to DefaultListCustomerAddressesUseCase cannot be null";

        final var aException = Assertions.assertThrows(UseCaseInputCannotBeNullException.class,
                () -> this.listCustomerAddressesUseCase.execute(null));

        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());

        Mockito.verifyNoInteractions(this.addressRepository);
    }

    @Test
    void givenAValidInput_whenCallListCustomerAddresses_thenCallAddressRepositoryAddressesByCustomerId() {
        final var aCustomerId = new CustomerId(IdentifierUtils.generateNewUUID());

        final var aAddressOne = Fixture.Addresses.newAddressWithComplement(aCustomerId, true);
        final var aAddressTwo = Fixture.Addresses.newAddressWithComplement(aCustomerId, false);

        final var aAddresses = List.of(aAddressOne, aAddressTwo);

        final var aPage = 0;
        final var aPerPage = 10;
        final var aTotalPages = 1;
        final var aTerms = "";
        final var aSort = "title";
        final var aDirection = "asc";

        final var aSearchQuery = new SearchQuery(aPage, aPerPage, aTerms, aSort, aDirection);

        final var aMetadata = new PaginationMetadata(aPage, aPerPage, aTotalPages, aAddresses.size());
        final var aPagination = new Pagination<>(aMetadata, aAddresses);

        final var aItemsCount = 2;
        final var aResult = aPagination.map(ListCustomerAddressesOutput::new);

        final var aInput = new ListCustomerAddressesInput(aCustomerId, aSearchQuery);

        Mockito.when(addressRepository.addressesByCustomerId(aCustomerId, aSearchQuery)).thenReturn(aPagination);

        final var aOutput = this.listCustomerAddressesUseCase.execute(aInput);

        Assertions.assertEquals(aItemsCount, aOutput.metadata().totalItems());
        Assertions.assertEquals(aResult.items(), aOutput.items());
        Assertions.assertEquals(aResult.metadata(), aOutput.metadata());

        Mockito.verify(addressRepository, Mockito.times(1)).addressesByCustomerId(aCustomerId, aSearchQuery);
    }

    @Test
    void givenAValidInputButHasNoData_whenCallListCustomerAddresses_thenCallAddressRepositoryAddressesByCustomerId() {
        final var aCustomerId = new CustomerId(IdentifierUtils.generateNewUUID());

        final var aPage = 0;
        final var aPerPage = 10;
        final var aTotalPages = 1;
        final var aTerms = "";
        final var aSort = "title";
        final var aDirection = "asc";

        final var aSearchQuery = new SearchQuery(aPage, aPerPage, aTerms, aSort, aDirection);

        final var aMetadata = new PaginationMetadata(aPage, aPerPage, aTotalPages, 0);
        final var aPagination = new Pagination<Address>(aMetadata, List.of());

        final var aItemsCount = 0;

        final var aInput = new ListCustomerAddressesInput(aCustomerId, aSearchQuery);

        Mockito.when(addressRepository.addressesByCustomerId(aCustomerId, aSearchQuery)).thenReturn(aPagination);

        final var aOutput = this.listCustomerAddressesUseCase.execute(aInput);

        Assertions.assertEquals(aItemsCount, aOutput.metadata().totalItems());
        Assertions.assertTrue(aOutput.items().isEmpty());
        Assertions.assertEquals(aMetadata, aOutput.metadata());

        Mockito.verify(addressRepository, Mockito.times(1)).addressesByCustomerId(aCustomerId, aSearchQuery);
    }
}
