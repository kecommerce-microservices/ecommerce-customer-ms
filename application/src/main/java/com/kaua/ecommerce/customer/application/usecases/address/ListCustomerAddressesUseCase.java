package com.kaua.ecommerce.customer.application.usecases.address;

import com.kaua.ecommerce.customer.application.UseCase;
import com.kaua.ecommerce.customer.application.usecases.address.inputs.ListCustomerAddressesInput;
import com.kaua.ecommerce.customer.application.usecases.address.outputs.ListCustomerAddressesOutput;
import com.kaua.ecommerce.lib.domain.pagination.Pagination;

public abstract class ListCustomerAddressesUseCase extends
        UseCase<ListCustomerAddressesInput, Pagination<ListCustomerAddressesOutput>> {
}
